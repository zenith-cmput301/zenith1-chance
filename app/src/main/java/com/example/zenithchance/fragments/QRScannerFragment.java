package com.example.zenithchance.fragments;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.example.zenithchance.managers.QRManager;
import com.example.zenithchance.models.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;

public class QRScannerFragment extends Fragment {

    private DecoratedBarcodeView barcodeView;

    private FirebaseFirestore db;

    public QRScannerFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);
        barcodeView = view.findViewById(R.id.qr_scanner);

        scan();
        requestCameraPermission();

        return view;
    }

    /**
     * https://www.geeksforgeeks.org/android/how-to-read-qr-code-using-zxing-library-in-android/
     */

    private void scan() {

        barcodeView.getBarcodeView().setDecoderFactory(
                new DefaultDecoderFactory(Collections.singletonList(BarcodeFormat.QR_CODE))
        );

        barcodeView.decodeContinuous(result -> {
            String text = result.getText();

            if (text != null) {

                navigateToEvent(text);
            }
        });
    }

    private void requestCameraPermission() {
        Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        barcodeView.resume();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void navigateToEvent(String link) {

        // Removes prefix
        String docId = link.replace("zenith1/", "");

        db = FirebaseFirestore.getInstance();

        // Grabs Event
        db.collection("events")
                .document(docId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        populateDetailFragment(event);
                    }
                })
                .addOnFailureListener(e -> {
                });

    }

    public void populateDetailFragment (Event event) {
        EntrantEventDetailsFragment fragment = new EntrantEventDetailsFragment();

        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

        Bundle bundle = new Bundle();
        bundle.putString("event_name", event.getName());
        bundle.putString("event_location", event.getLocation());
        bundle.putString("event_organizer", event.getOrganizer());
        bundle.putString("event_time", fmt.format(event.getDate()));
        bundle.putString("event_description", event.getDescription());
        bundle.putString("event_image_url", event.getImageUrl());
        bundle.putString("event_doc_id", event.getDocId());
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
