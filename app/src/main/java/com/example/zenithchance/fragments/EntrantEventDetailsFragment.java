package com.example.zenithchance.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;
import com.example.zenithchance.interfaces.EntrantProviderInterface;
import com.example.zenithchance.managers.LocationHelper;
import com.example.zenithchance.managers.QRManager;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Event;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private TextView waitingCountView;
    private int waitingCount = -1;

    private static final float MAX_DISTANCE_METERS = 50000f; // distance from actual event location (allows for some flexibility)

    private Event eventForLocal;

    // For handling permission request
    private ActivityResultLauncher<String> locationPermissionRequest;
    private MaterialButton pendingActionButton;
    private String pendingEventDocId;
    private Event pendingEvent;
    private Boolean pendingGeoRequired;


    private LocationHelper locationHelper;
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

        locationHelper = new LocationHelper(context.getApplicationContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register permission request launcher
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d("Permission", "Location permission granted");
                        // Permission granted, proceed with enrollment
                        if (pendingActionButton != null
                                && pendingEventDocId != null
                                && pendingEvent != null
                                && pendingGeoRequired != null) {

                            proceedWithLocationCheck(
                                    pendingEventDocId,
                                    pendingActionButton,
                                    pendingEvent,
                                    pendingGeoRequired
                            );
                        }
                    } else {
                        Log.d("Permission", "Location permission denied");
                        // Permission denied
                        if (pendingActionButton != null) {
                            pendingActionButton.setText("Enroll");
                            pendingActionButton.setEnabled(true);
                        }

                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Location Permission Required")
                                .setMessage("This event requires location access to record where you joined from. Please grant location permission to join.")
                                .setPositiveButton("OK", null)
                                .show();
                    }

                    // Clear pending
                    pendingActionButton = null;
                    pendingEventDocId = null;
                    pendingEvent = null;
                    pendingGeoRequired = null;
                }
        );
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
        waitingCountView = view.findViewById(R.id.waiting_list_count);
        ImageButton infoBtn = view.findViewById(R.id.info_button);
        ImageView qr = view.findViewById(R.id.qr);

        if (currentEntrant == null) {
            Toast.makeText(requireContext(),
                    "Error: no current entrant found.",
                    Toast.LENGTH_LONG).show();
            actionBtn.setEnabled(false);
            actionBtn.setText("Unavailable");
            waitingCountView.setText("Waiting list: --");
            return view;
        }

        Bundle args = getArguments();
        String eventDocId = null;
        if (args != null) {
            eventDocId = args.getString("event_doc_id");
        }

        if (eventDocId == null) {
            Toast.makeText(requireContext(),
                    "No event specified.",
                    Toast.LENGTH_LONG).show();
            actionBtn.setEnabled(false);
            actionBtn.setText("Event not available");
            waitingCountView.setText("Waiting list: --");
            return view;
        }

        // Load full Event from Firestore
        String finalEventDocId = eventDocId;
        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventDocId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    eventForLocal = snapshot.toObject(Event.class);

                    if (eventForLocal == null) {
                        Toast.makeText(requireContext(),
                                "Event not found.",
                                Toast.LENGTH_LONG).show();
                        actionBtn.setEnabled(false);
                        actionBtn.setText("Event not available");
                        waitingCountView.setText("Waiting list: --");
                        return;
                    }

                    // Populate UI with fresh data
                    name.setText(eventForLocal.getName());
                    location.setText(eventForLocal.getLocation());
                    organizer.setText(eventForLocal.getOrganizer());
                    time.setText(formatEventDateTime(eventForLocal.getDate()));
                    desc.setText(eventForLocal.getDescription());

                    // QR
                    QRManager manager = new QRManager();
                    manager.updateImageView(qr, eventForLocal);

                    // Image
                    String imageUrl = eventForLocal.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.celebration_placeholder)
                                .into(image);
                    } else {
                        image.setImageResource(R.drawable.celebration_placeholder);
                    }

                    // Wire buttons with fresh event data
                    loadWaitingListCount(finalEventDocId, eventForLocal);
                    refreshEntrantListsAndBind(finalEventDocId, eventForLocal,
                            inviteActions, actionBtn, acceptBtn, declineBtn);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(),
                            "Failed to load event: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    actionBtn.setEnabled(false);
                    actionBtn.setText("Event not available");
                    waitingCountView.setText("Waiting list: --");
                });

        // Info button
        infoBtn.setOnClickListener(v -> showDrawInfoDialog());

        return view;
    }

    // Add this helper method
    private String formatEventDateTime(Date date) {
        if (date == null) {
            return "Date not set";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());
        return formatter.format(date);
    }

    /**
     * This function fetches newest event details data to accurately shows buttons
     * (in case queue status changes)
     *
     * @param eventDocId        Firestore document id of event
     * @param eventForLocal     Local copy of event fetched from Firestore
     * @param inviteActions     Special group of buttons in case entrant is invited
     * @param actionBtn         Button to enroll/drop out of waiting list
     * @param acceptBtn         Button to accept invitation
     * @param declineBtn        Button to decline invitation
     */
    private void refreshEntrantListsAndBind(String eventDocId, Event eventForLocal, ViewGroup inviteActions, MaterialButton actionBtn, MaterialButton acceptBtn, MaterialButton declineBtn) {

        // show loading state while fetching for fancy purposes
        actionBtn.setEnabled(false);
        actionBtn.setText("Loading...");
        actionBtn.setTextColor(Color.WHITE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentEntrant.getUserId())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot != null && snapshot.exists()) {
                        List<String> waiting  = (List<String>) snapshot.get("onWaiting");
                        List<String> invited  = (List<String>) snapshot.get("onInvite");
                        List<String> accepted = (List<String>) snapshot.get("onAccepted");
                        List<String> declined = (List<String>) snapshot.get("onDeclined");

                        // update currentEntrant with latest data
                        currentEntrant.setOnWaiting(waiting != null ? new ArrayList<>(waiting) : new ArrayList<>());
                        currentEntrant.setOnInvite(invited != null ? new ArrayList<>(invited) : new ArrayList<>());
                        currentEntrant.setOnAccepted(accepted != null ? new ArrayList<>(accepted) : new ArrayList<>());
                        currentEntrant.setOnDeclined(declined != null ? new ArrayList<>(declined) : new ArrayList<>());
                    }

                    bindActionForState(eventDocId, eventForLocal, inviteActions, actionBtn, acceptBtn, declineBtn);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to refresh status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    bindActionForState(eventDocId, eventForLocal, inviteActions, actionBtn, acceptBtn, declineBtn);
                });
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

        // Check if event has passed
        Date now = new Date();
        if (eventForLocal != null && eventForLocal.isPast(now)) {
            actionBtn.setVisibility(View.VISIBLE);
            inviteActions.setVisibility(View.GONE);   // hide accept/decline buttons
            actionBtn.setText("Event passed");
            actionBtn.setEnabled(false);
            actionBtn.setTextColor(Color.WHITE);      // or a softer gray if you prefer
            return;
        }

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
            actionBtn.setText("Cancel Spot");
            actionBtn.setTextColor(Color.WHITE);
            actionBtn.setEnabled(true);
            cancelAccepted(eventDocId, actionBtn, eventForLocal);
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
     * Checks if entrant can enroll in event's waiting list.
     * Requests location permission if needed.
     */
    public void enrollWaiting(String eventDocId, MaterialButton actionBtn, Event eventForLocal) {
        actionBtn.setOnClickListener(v -> {

            boolean geoRequired = Boolean.TRUE.equals(eventForLocal.getGeolocationRequired());

            // Always require permission, regardless of geoRequired
            if (!hasLocationPermission()) {
                // Save pending action so you can retry after permission result
                pendingActionButton = actionBtn;
                pendingEventDocId = eventDocId;
                pendingEvent = eventForLocal;
                pendingGeoRequired = geoRequired;

                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }

            // Permission already granted; always go through location check
            proceedWithLocationCheck(eventDocId, actionBtn, eventForLocal, geoRequired);
        });
    }

                        // send enrollemnt notification
                        UserManager.getInstance().sendNotification(eventDocId, "enrolled", currentEntrant.getUserId());

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
     * Performs the actual enrollment
     */
    private void performEnrollment(String eventDocId, MaterialButton actionBtn,
                                   Event eventForLocal, GeoPoint location) {
        currentEntrant.enrollInWaiting(
                eventForLocal,
                eventDocId,
                location,
                // Success
                () -> {
                    actionBtn.setText("Drop Waiting List");
                    actionBtn.setEnabled(true);
                    dropWaitingList(eventDocId, actionBtn, eventForLocal);

                    waitingCount++;
                    updateWaitingCountLabel();

                    Toast.makeText(requireContext(), "✓ Added to waiting list", Toast.LENGTH_SHORT).show();
                },
                // Failure
                e -> {
                    actionBtn.setText("Enroll");
                    actionBtn.setEnabled(true);
                    Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
        );
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

                        // decrement #entrants
                        waitingCount--;
                        updateWaitingCountLabel();

                        // send notifications
                        UserManager.getInstance().sendNotification(eventDocId, "dropped", currentEntrant.getUserId());
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

    /**
     * Allow entrant to decline accepted spot
     *
     * @param eventDocId        Firestore document id of event
     * @param actionBtn         Button to drop out of accepted list
     * @param eventForLocal     Local copy of event fetched from Firestore
     */
    public void cancelAccepted(String eventDocId, MaterialButton actionBtn, Event eventForLocal) {

        actionBtn.setOnClickListener(v -> {
            actionBtn.setEnabled(false);
            actionBtn.setText("Cancelling...");
            actionBtn.setTextColor(Color.WHITE);

            currentEntrant.cancelAccepted(
                    eventForLocal,
                    eventDocId,
                    () -> {
                        Toast.makeText(requireContext(), "You cancelled your spot", Toast.LENGTH_SHORT).show();
                        actionBtn.setText("Declined");
                        actionBtn.setTextColor(Color.WHITE);
                        actionBtn.setEnabled(false);
                        UserManager.getInstance().sendNotification(eventDocId, "cancelled", currentEntrant.getUserId());
                    },
                    e -> {
                        actionBtn.setText("Cancel Spot");
                        actionBtn.setEnabled(true);
                        Toast.makeText(requireContext(),
                                "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );
        });
    }

    /**
     * Fetch and display number of entrants on waiting list
     *
     * @param eventDocId    Firestore document id of event
     * @param eventForLocal Local copy of event fetched from Firestore
     */
    private void loadWaitingListCount(String eventDocId, Event eventForLocal) {

        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventDocId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> waiting = (List<String>) snapshot.get("waitingList");
                    waitingCount = (waiting != null) ? waiting.size() : 0;
                    if (eventForLocal != null) { // get newest event details to local
                        eventForLocal.setWaitingList(waiting != null ? new ArrayList<>(waiting) : new ArrayList<>());
                    }
                    updateWaitingCountLabel();
                })
                .addOnFailureListener(e -> {
                    waitingCountView.setText("Waiting list: --");
                });
    }

    /**
     * Update the number of entrants on waiting list displayed on screen
     */
    private void updateWaitingCountLabel() {
        String label = "Waiting list: " + waitingCount + (waitingCount == 1 ? " entrant" : " entrants");
        waitingCountView.setText(label);
    }

    /**
     * To show lottery info popup
     */
    private void showDrawInfoDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("How the draw works")
                .setMessage("Entrants join the waiting list.\n\n" +
                        "When registration closes, a random draw selects up to the max number allowed " +
                        "of entrants.\n\n" +
                        "Selected entrants receive an invitation and must accept by the " +
                        "deadline.\n\n" +
                        "If someone declines or cancels, another entrant may be drawn.")
                .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
