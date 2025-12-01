package com.example.zenithchance.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


public class AdminImagePreviewFragment extends Fragment {

    private String imageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_image_preview, container, false);

        ImageView imageView = view.findViewById(R.id.preview_image);
        MaterialButton deleteBtn = view.findViewById(R.id.delete_image_button);

        if (getArguments() != null) {
            imageUrl = getArguments().getString("image_url");

            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_my_events)
                    .into(imageView);
        }

        deleteBtn.setOnClickListener(v -> askDelete());

        return view;
    }

    private void askDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Image?")
                .setMessage("This image will be permanently removed.")
                .setPositiveButton("Delete", (d, w) -> deleteImage())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage() {
        // Delete from Firebase Storage
        FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    removeImageUrlFromEvents();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Storage delete failed: " + e, Toast.LENGTH_LONG).show());
    }

    private void removeImageUrlFromEvents() {
        FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("imageUrl", imageUrl)
                .get()
                .addOnSuccessListener(snapshot -> {
                    snapshot.getDocuments().forEach(doc ->
                            doc.getReference().update("imageUrl", null));

                    Toast.makeText(requireContext(), "Image deleted", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
    }
}
