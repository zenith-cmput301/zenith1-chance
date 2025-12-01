package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zenithchance.R;
import com.example.zenithchance.adapters.AdminImageGridAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseImagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminImageGridAdapter adapter;
    private List<String> imageUrls = new ArrayList<>();
    private TextView emptyMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_browse_images, container, false);

        recyclerView = view.findViewById(R.id.images_recycler);
        emptyMessage = view.findViewById(R.id.noImagesMessage);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        adapter = new AdminImageGridAdapter(imageUrls, this::openImagePreview);
        recyclerView.setAdapter(adapter);

        loadEventImages();
        return view;
    }

    private void loadEventImages() {
        FirebaseFirestore.getInstance().collection("events")
                .get()
                .addOnSuccessListener(snapshot -> {
                    imageUrls.clear();

                    for (var doc : snapshot.getDocuments()) {
                        String url = doc.getString("imageUrl");

                        if (url != null && !url.isEmpty()) {
                            imageUrls.add(url);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // Update visibility AFTER loading images
                    if (imageUrls.isEmpty()) {
                        emptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void openImagePreview(String imageUrl) {
        AdminImagePreviewFragment fragment = new AdminImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString("image_url", imageUrl);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
