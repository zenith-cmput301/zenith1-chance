package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PeopleListFragment extends Fragment {

    private static final String ARG_EVENT_NAME = "event_name";
    private String eventName;

    private TextView textViewUsers;

    public PeopleListFragment() {
        // Required empty public constructor
    }

    public static PeopleListFragment newInstance(String eventName) {
        PeopleListFragment fragment = new PeopleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_NAME, eventName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventName = getArguments().getString(ARG_EVENT_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_people, container, false);

        fetchWaitingList();

        return view;
    }

    private void fetchWaitingList() {
        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(getContext(), "Event name not provided", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("waiting_list")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                    StringBuilder usersText = new StringBuilder();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String userName = doc.getString("userName");
                        if (userName != null) {
                            usersText.append("• ").append(userName).append("\n");
                        }
                    }
                    if (usersText.length() == 0) {
                        textViewUsers.setText("No users on the waiting list.");
                    } else {
                        textViewUsers.setText(usersText.toString());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error fetching waiting list: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
