package com.example.zenithchance.fragments;

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
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Event;

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
        com.google.android.material.button.MaterialButton actionBtn = view.findViewById(R.id.event_action_button);

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
                    .placeholder(R.drawable.ic_my_events)
                    .into(image);
        }

        // wiring action buttons, first create holder event
        Event eventForLocal = new Event();
        eventForLocal.setName(eventName);
        eventForLocal.setLocation(eventLocation);
        eventForLocal.setDescription(eventDesc);

        bindActionForState(eventDocId, actionBtn, eventForLocal, eventName);

        return view;
    }

    private void bindActionForState(String eventDocId,
                                    com.google.android.material.button.MaterialButton actionBtn,
                                    Event eventForLocal, String eventName) {
        actionBtn.setOnClickListener(null); // clear previous listener

        // Case 1: Enroll
        if (!currentEntrant.isInAnyList(eventDocId)) {
            actionBtn.setText("Enroll");
            actionBtn.setEnabled(true);
            enrollWaiting(eventDocId, actionBtn, eventForLocal);
        }

        // Case 2: Enrolled but wants to drop
        else if (currentEntrant.isInWaitingListById(eventDocId)) {
            actionBtn.setText("Drop Waiting List");
            actionBtn.setEnabled(true);
            dropWaitingList(eventDocId, actionBtn, eventForLocal);
        }


    }

    /**
     * Allows entrant to enroll an event's waiting list
     *
     * @param eventDocId        Firestore id of event
     * @param actionBtn         Button to wire
     * @param eventForLocal     Event to enroll
     */
    public void enrollWaiting(String eventDocId,
                              com.google.android.material.button.MaterialButton actionBtn,
                              Event eventForLocal) {

        actionBtn.setOnClickListener( v -> {
            actionBtn.setEnabled(false);   // prevent double taps during transition
            actionBtn.setText("Enrolling…");

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
    public void dropWaitingList(String eventDocId,
                              com.google.android.material.button.MaterialButton actionBtn,
                              Event eventForLocal) {

        actionBtn.setOnClickListener(v-> {
            actionBtn.setEnabled(false); // prevent double taps while transitioning
            actionBtn.setText("Dropping…");

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
}
