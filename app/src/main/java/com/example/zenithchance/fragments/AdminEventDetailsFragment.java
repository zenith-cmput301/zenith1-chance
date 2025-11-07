package com.example.zenithchance.fragments;

import android.app.AlertDialog;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminEventDetailsFragment extends Fragment {

    public AdminEventDetailsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_event_details, container, false);

        TextView name = view.findViewById(R.id.event_name);
        TextView location = view.findViewById(R.id.location);
        TextView organizer = view.findViewById(R.id.organizer_name);
        TextView time = view.findViewById(R.id.time);
        TextView desc = view.findViewById(R.id.description);
        ImageView image = view.findViewById(R.id.header_image);
        MaterialButton deleteBtn = view.findViewById(R.id.delete_button);

        Bundle args = getArguments();
        if (args != null) {
            String docId = args.getString("event_doc_id");
            name.setText(args.getString("event_name"));
            location.setText(args.getString("event_location"));
            organizer.setText(args.getString("event_organizer"));
            time.setText(args.getString("event_time"));
            desc.setText(args.getString("event_description"));

            String imageUrl = args.getString("event_image_url");
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_my_events)
                    .error(R.drawable.ic_my_events)
                    .into(image);


            MaterialToolbar toolbar = view.findViewById(R.id.admin_toolbar);
            toolbar.setNavigationOnClickListener(v1 -> requireActivity().getSupportFragmentManager().popBackStack());

            deleteBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Event?")
                        .setMessage("This action cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            FirebaseFirestore.getInstance()
                                    .collection("events")
                                    .document(docId)
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(requireContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

        }

        return view;
    }
}
