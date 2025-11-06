package com.example.zenithchance.fragments;

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

        // Wiring action buttons, first create holder event
        Event eventForLocal = new Event();
        eventForLocal.setName(eventName);
        eventForLocal.setLocation(eventLocation);
        eventForLocal.setDescription(eventDesc);

        boolean inSomeList = currentEntrant.isInAnyListByName(eventName);

        // Case 1: To enroll
        if (!inSomeList) {
            String finalEventName = eventName;
            String finalEventDocId = eventDocId;

            actionBtn.setOnClickListener( v -> {
                actionBtn.setEnabled(false);   // prevent double taps
                actionBtn.setText("Enrolling…");

                currentEntrant.enrollInWaiting(
                        eventForLocal,
                        finalEventDocId,
                        // success
                        () -> {
                            actionBtn.setText("Enrolled");
                            actionBtn.setEnabled(false);
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

        return view;
    }
}
