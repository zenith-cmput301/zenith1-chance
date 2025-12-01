package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.zenithchance.R;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.navigation.EntrantNavigationHelper;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This fragment displays the interactive My Events page for Entrants
 *
 * @author Percy
 * @version 1.0
 * @see EntrantNavigationHelper
 * @see EntrantEventListFragment
 */
public class EntrantEventsFragment extends Fragment {

    private EntrantEventListFragment eventListFrag;
    private Entrant currentEntrant;

    /**
     * Method to inflate fragment
     *
     * @param inflater              The LayoutInflater object that can be used to inflate
     *                              any views in the fragment,
     * @param container             If non-null, this is the parent view that the fragment's
     *                              UI should be attached to.
     *                              The fragment should not add the view itself,
     *                              but this can be used to generate the
     *                              LayoutParams of the view.
     * @param savedInstanceState    If non-null, this fragment is being re-constructed
     *                              from a previous saved state as given here.
     *
     * @return                      Inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // get current entrant
        currentEntrant = (Entrant) getArguments().getSerializable("entrant");

        View view = inflater.inflate(R.layout.fragment_entrant_my_events, container, false);

        // Inflate child fragment
        FragmentManager fm = getChildFragmentManager();
        eventListFrag = (EntrantEventListFragment) fm.findFragmentByTag("entrant_event_list");

        if (eventListFrag == null) {
            eventListFrag = new EntrantEventListFragment();
            fm.beginTransaction()
                    .replace(R.id.eventsFragmentContainer, eventListFrag, "entrant_event_list")
                    .commit();
            fm.executePendingTransactions();
        }

        // get buttons
        Button upcomingButton = view.findViewById(R.id.upcoming_events);
        Button pastButton = view.findViewById(R.id.past_events);
        upcomingButton.setEnabled(false);

        // wire upcoming button
        upcomingButton.setOnClickListener(v -> {
            eventListFrag.setFilter(true);
            upcomingButton.setEnabled(false);
            pastButton.setEnabled(true);
        });

        // wire past button
        pastButton.setOnClickListener(v -> {
            eventListFrag.setFilter(false);
            pastButton.setEnabled(false);
            upcomingButton.setEnabled(true);
        });

        // fetch events
        getEvents();

        return view;
    }

    /**
     * This method fetch all events this entrant has participated from Firestore.
     */
    private void getEvents() {
        String uid = currentEntrant.getUserId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(userSnap -> {
                    // get participating events' id
                    Set<String> idSet = new HashSet<>();
                    addArrayToSet(userSnap, "onWaiting",  idSet);
                    addArrayToSet(userSnap, "onInvite",   idSet);
                    addArrayToSet(userSnap, "onAccepted", idSet);
                    addArrayToSet(userSnap, "onDeclined", idSet);

                    // no event
                    if (idSet.isEmpty()) {
                        eventListFrag.setEvents(new ArrayList<>());
                        return;
                    }

                    // get events from document "events"
                    List<String> allIds = new ArrayList<>(idSet);
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();

                    // fetch 10 at a time due to whereIn's limit 10
                    for (int i = 0; i < allIds.size(); i += 10) {
                        int end = Math.min(i + 10, allIds.size());
                        List<String> chunk = allIds.subList(i, end);
                        Task<QuerySnapshot> t = db.collection("events")
                                .whereIn(FieldPath.documentId(), chunk)
                                .get();
                        tasks.add(t);
                    }

                    // transform to actual event instances
                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(results -> {
                                List<Event> merged = new ArrayList<>();
                                Set<String> seen = new HashSet<>();

                                for (Object obj : results) {
                                    QuerySnapshot qs = (QuerySnapshot) obj;
                                    for (DocumentSnapshot d : qs) {
                                        if (seen.add(d.getId())) {
                                            Event e = d.toObject(Event.class);
                                            if (e != null) {
                                                e.setDocId(d.getId());
                                                merged.add(e);
                                            }
                                        }
                                    }
                                }
                                eventListFrag.setEvents(merged);
                            });
                });
    }

    /**
     * This method converts array of string from Firebase into list of string in Java
     *
     * @param snap      Firebase document
     * @param field     Name of array field in that document
     * @param out       We put the array items from Firestore in here for Java to use
     */
    private void addArrayToSet(DocumentSnapshot snap, String field, Set<String> out) {
        List<String> arr = (List<String>) snap.get(field);
        if (arr != null) out.addAll(arr);
    }
}
