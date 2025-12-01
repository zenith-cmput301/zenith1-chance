package com.example.zenithchance.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.OutputStream;

public class PeopleListFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private String eventId;
    private TextView textViewUsers;
    private String csvToSave = "";

    public static PeopleListFragment newInstance(String eventId) {
        PeopleListFragment fragment = new PeopleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_people, container, false);

        textViewUsers = view.findViewById(R.id.tv_users_list);

        Button btnWaiting = view.findViewById(R.id.btnWaiting);
        Button btnAccepted = view.findViewById(R.id.btnAccepted);
        Button btnDeclined = view.findViewById(R.id.btnDeclined);
        Button btnChosen = view.findViewById(R.id.btnChosen);
        Button btnExport = view.findViewById(R.id.btnExport);

        btnWaiting.setOnClickListener(v -> fetchEntrantsByStatus("onWaiting", "waiting"));
        btnAccepted.setOnClickListener(v -> fetchEntrantsByStatus("onAccepted", "accepted"));
        btnDeclined.setOnClickListener(v -> fetchEntrantsByStatus("onDeclined", "declined"));
        btnChosen.setOnClickListener(v -> fetchEntrantsByStatus("onInvite", "invited"));
        btnExport.setOnClickListener(v -> exportAcceptedAsCSV());
        return view;
    }
    private void fetchEntrantsByStatus(String field, String label) {
        if (eventId == null) {
            Toast.makeText(getContext(), "Event ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("type", "entrant")
                .whereArrayContains(field, eventId)
                .get()
                .addOnSuccessListener(snap -> {
                    StringBuilder usersText = new StringBuilder();

                    for (QueryDocumentSnapshot doc : snap) {
                        String name = doc.getString("name");
                        if (name == null || name.trim().isEmpty()) name = doc.getId();
                        usersText.append("• ").append(name).append("\n");
                    }
                    if (usersText.length() == 0) {
                        textViewUsers.setText("No users were " + label + ".");
                    } else {
                        textViewUsers.setText(usersText.toString());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void exportAcceptedAsCSV() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("type", "entrant")
                .whereArrayContains("onAccepted", eventId)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap.isEmpty()) {
                        textViewUsers.setText("No accepted users found.");
                        return;
                    }
                    StringBuilder csv = new StringBuilder();
                    csv.append("Name\n");

                    for (QueryDocumentSnapshot doc : snap) {
                        String name = doc.getString("name");
                        if (name == null || name.trim().isEmpty()) name = doc.getId();
                        csv.append(name).append("\n");
                    }
                    textViewUsers.setText(csv.toString());
                    csvToSave = csv.toString();
                    openSaveDialog();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void openSaveDialog() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "accepted_users.csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, 999);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                saveCSVToUri(data.getData());
            }
        }
    }
    private void saveCSVToUri(Uri uri) {
        try {
            OutputStream out = requireContext().getContentResolver().openOutputStream(uri);
            out.write(csvToSave.getBytes());
            out.close();
            Toast.makeText(getContext(), "Saved to Downloads!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Saving failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
