package com.example.zenithchance.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;
import com.example.zenithchance.interfaces.EntrantProviderInterface;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Event;
import com.google.android.material.button.MaterialButton;

/**
 * This fragment displays details of the selected event.
 *
 * @author Percy
 * @version 1.0
 * @see AllEventsFragment
 * @see EntrantEventListFragment
 * @see OrganizerEventListFragment
 */
public class EntrantEventDetailsFragment extends Fragment {
    private Entrant currentEntrant;

    public EntrantEventDetailsFragment() { }

    /**
     * Splits setting entrant and constructor for Activity to set entrant before showing fragment
     * @param entrant Current entrant
     */
    public void setCurrentEntrant(Entrant entrant) {
        this.currentEntrant = entrant;
    }

    /**
     * Defines behavior when fragment is attached to activity
     * @param context Activity that hosts this fragment
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (currentEntrant == null && context instanceof EntrantProviderInterface) {
            currentEntrant = ((EntrantProviderInterface) context).getCurrentEntrant();
        }
    }

    /**
     * This method defines what happens when this fragment is created
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_event_details, container, false);

        TextView name = view.findViewById(R.id.event_name);
        TextView location = view.findViewById(R.id.location);
        TextView organizer = view.findViewById(R.id.organizer_name);
        TextView time = view.findViewById(R.id.time);
        TextView desc = view.findViewById(R.id.description);
        ImageView image = view.findViewById(R.id.header_image);
        MaterialButton actionBtn = view.findViewById(R.id.event_action_button);
        ViewGroup inviteActions = view.findViewById(R.id.invite_actions);
        MaterialButton acceptBtn  = view.findViewById(R.id.btn_accept);
        MaterialButton declineBtn = view.findViewById(R.id.btn_decline);

        String eventName = null;
        String eventDocId = null;
        String imageUrl = null;
        String eventLocation = null;
        String eventOrganizer = null;
        String eventTime = null;
        String eventDesc = null;

        Bundle args = getArguments();
        if (args != null) {
            eventName = args.getString("event_name");
            eventLocation = args.getString("event_location");
            eventOrganizer = args.getString("event_organizer");
            eventTime = args.getString("event_time");
            eventDesc = args.getString("event_description");
            imageUrl = args.getString("event_image_url");
            eventDocId = args.getString("event_doc_id");

            name.setText(eventName);
            location.setText(eventLocation);
            organizer.setText(eventOrganizer);
            time.setText(eventTime);
            desc.setText(eventDesc);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.celebration_placeholder)
                    .into(image);
        }

        // wiring action buttons, first create holder event
        Event eventForLocal = new Event();
        eventForLocal.setName(eventName);
        eventForLocal.setLocation(eventLocation);
        eventForLocal.setDescription(eventDesc);

        bindActionForState(eventDocId, eventForLocal, inviteActions, actionBtn, acceptBtn, declineBtn);

        return view;
    }

    /**
     * Binds action button behaviour to specific state (enroll, invited, etc.)
     *
     * @param eventDocId        Firestore document id of event
     * @param eventForLocal     Local copy of event fetched from Firestore
     * @param inviteActions     Special group of buttons in case entrant is invited
     * @param actionBtn         Button to enroll/drop out of waiting list
     * @param acceptBtn         Button to accept invitation
     * @param declineBtn        Button to decline invitation
     */
    private void bindActionForState(String eventDocId, Event eventForLocal,
                                    ViewGroup inviteActions,
                                    MaterialButton actionBtn,
                                    MaterialButton acceptBtn,
                                    MaterialButton declineBtn) {
        actionBtn.setOnClickListener(null); // clear previous listener

        // Case 1: Enroll
        if (!currentEntrant.isInAnyList(eventDocId)) {
            actionBtn.setText("Enroll");
            actionBtn.setEnabled(true);
            enrollWaiting(eventDocId, actionBtn, eventForLocal);
        }

        // Case 2: Enrolled but wants to drop
        else if (currentEntrant.isInWaitingList(eventDocId)) {
            actionBtn.setText("Drop Waiting List");
            actionBtn.setEnabled(true);
            dropWaitingList(eventDocId, actionBtn, eventForLocal);
        }

        // Case 3: Invited, waiting to accept or decline
        else if (currentEntrant.isInInvitedList(eventDocId)) {
            // switch default buttons to accept/decline buttons
            actionBtn.setVisibility(View.GONE);
            inviteActions.setVisibility(View.VISIBLE);
            respondInvitation(eventDocId, inviteActions, actionBtn, acceptBtn, declineBtn, eventForLocal);
        }

        // Case 4: Accepted/Declined
        else if (currentEntrant.isInAcceptedList(eventDocId)) {
            actionBtn.setText("Accepted");
            actionBtn.setTextColor(Color.WHITE);
            actionBtn.setEnabled(false);
        }
        else if (currentEntrant.isInDeclinedList(eventDocId)) {
            actionBtn.setText("Declined");
            actionBtn.setTextColor(Color.WHITE);
            actionBtn.setEnabled(false);
        }

        else {
            actionBtn.setText("Unexpected: Which status is the entrant on?");
            actionBtn.setTextColor(Color.WHITE);
            actionBtn.setEnabled(false);
        }


    }

    /**
     * Allows entrant to enroll an event's waiting list
     *
     * @param eventDocId        Firestore id of event
     * @param actionBtn         Button to wire
     * @param eventForLocal     Event to enroll
     */
    public void enrollWaiting(String eventDocId, MaterialButton actionBtn,
                              Event eventForLocal) {

        actionBtn.setOnClickListener( v -> {
            actionBtn.setEnabled(false);   // prevent double taps during transition
            actionBtn.setText("Enrolling...");
            actionBtn.setTextColor(Color.WHITE);

            currentEntrant.enrollInWaiting(
                    eventForLocal,
                    eventDocId,
                    // success
                    () -> {
                        actionBtn.setText("Drop Waiting List");
                        actionBtn.setTextColor(Color.WHITE);
                        actionBtn.setEnabled(true);

                        // bind to dropping list behaviour on next click
                        dropWaitingList(eventDocId, actionBtn, eventForLocal);

                        Toast.makeText(requireContext(), "Added to waiting list", Toast.LENGTH_SHORT).show();
                    },
                    // fail to enroll due to firebase shenanigans
                    e -> {
                        actionBtn.setText("Enroll");
                        actionBtn.setEnabled(true);
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );
        });
    }

    /**
     * Allows entrant to drop from event's waiting list
     *
     * @param eventDocId        Firestore id of event
     * @param actionBtn         Button to wire
     * @param eventForLocal     Event to drop from
     */
    public void dropWaitingList(String eventDocId, MaterialButton actionBtn,
                              Event eventForLocal) {

        actionBtn.setOnClickListener(v-> {
            actionBtn.setEnabled(false); // prevent double taps while transitioning
            actionBtn.setText("Dropping...");
            actionBtn.setTextColor(Color.WHITE);

            currentEntrant.dropWaiting(
                    eventForLocal,
                    eventDocId,
                    // success
                    () -> {
                        // Flip UI back to "Enroll" and rebind to enroll flow
                        actionBtn.setText("Enroll");
                        actionBtn.setEnabled(true);
                        Toast.makeText(requireContext(), "Removed from waiting list", Toast.LENGTH_SHORT).show();

                        // rebind to enroll behavior so a subsequent tap enrolls again
                        enrollWaiting(eventDocId, actionBtn, eventForLocal);
                    },
                    // fail to enroll due to firebase shenanigans
                    e -> {
                        actionBtn.setText("Drop Waiting List");
                        actionBtn.setEnabled(true);
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );
        });
    }

    /**
     * Allows entrant to respond to invitation (accept/decline)
     *
     * @param eventDocId        Firestore document id of event
     * @param inviteActions     Special group of buttons in case entrant is invited
     * @param actionBtn         Button to enroll/drop out of waiting list
     * @param acceptBtn         Button to accept invitation
     * @param declineBtn        Button to decline invitation
     * @param eventForLocal     Local copy of event fetched from Firestore
     */
    public void respondInvitation(String eventDocId, ViewGroup inviteActions,
                                  MaterialButton actionBtn, MaterialButton acceptBtn, MaterialButton declineBtn,
                                  Event eventForLocal) {
        // accept button wiring
        acceptBtn.setOnClickListener(v-> {
            acceptBtn.setEnabled(false);
            declineBtn.setEnabled(false);
            acceptBtn.setText("Accepting...");
            acceptBtn.setTextColor(Color.WHITE);

            currentEntrant.acceptInvite(
                    eventForLocal, eventDocId,
                    () -> { // success
                        Toast.makeText(requireContext(), "Invite accepted", Toast.LENGTH_SHORT).show();
                        inviteActions.setVisibility(View.GONE);
                        actionBtn.setText("Accepted");
                        actionBtn.setTextColor(Color.WHITE);
                        actionBtn.setEnabled(false);
                        actionBtn.setVisibility(View.VISIBLE);
                    }, // fail to make changes to database
                    e -> {
                        acceptBtn.setText("Accept");
                        actionBtn.setTextColor(Color.WHITE);
                        acceptBtn.setEnabled(true);
                        declineBtn.setEnabled(true);
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );
        });

        // decline button wiring
        declineBtn.setOnClickListener(v -> {
            acceptBtn.setEnabled(false);
            declineBtn.setEnabled(false);
            declineBtn.setText("Declining...");
            declineBtn.setTextColor(Color.WHITE);

            currentEntrant.declineInvite(
                    eventForLocal, eventDocId,
                    () -> {
                        Toast.makeText(requireContext(), "Invite declined", Toast.LENGTH_SHORT).show();
                        inviteActions.setVisibility(View.GONE);
                        actionBtn.setText("Declined");
                        actionBtn.setTextColor(Color.WHITE);
                        actionBtn.setEnabled(false);
                        actionBtn.setVisibility(View.VISIBLE);
                    },
                    e -> {
                        declineBtn.setText("Decline");
                        actionBtn.setTextColor(Color.WHITE);
                        acceptBtn.setEnabled(true);
                        declineBtn.setEnabled(true);
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );
        });
    }
}
