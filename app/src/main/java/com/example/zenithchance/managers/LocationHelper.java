package com.example.zenithchance.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;

public class LocationHelper {

    private final FusedLocationProviderClient fusedLocationClient;

    public LocationHelper(Context context) {
        // Use app context to avoid leaking an Activity
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(
                context.getApplicationContext()
        );
    }

    /**
     * Get a single current location update.
     * Assumes location permission has already been granted.
     */
    @SuppressLint("MissingPermission") // permission handled elsewhere
    public void getCurrentLocation(LocationCallback callback) {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10_000L)
                .setFastestInterval(5_000L)
                .setNumUpdates(1);   // one result

        fusedLocationClient.requestLocationUpdates(
                request,
                new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = null;
                        if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                            location = locationResult.getLastLocation();
                        }
                        callback.onLocationResult(location);

                        // Stop updates after first result
                        fusedLocationClient.removeLocationUpdates(this);
                    }
                },
                Looper.getMainLooper()
        );
    }

    /**
     * Convert Android Location to Firebase GeoPoint.
     */
    @Nullable
    public static GeoPoint locationToGeoPoint(@Nullable Location location) {
        if (location == null) return null;
        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    /**
     * Calculate distance in meters between two GeoPoints.
     */
    public static float distanceMeters(GeoPoint point1, GeoPoint point2) {
        if (point1 == null || point2 == null) return Float.MAX_VALUE;

        float[] results = new float[1];
        Location.distanceBetween(
                point1.getLatitude(), point1.getLongitude(),
                point2.getLatitude(), point2.getLongitude(),
                results
        );
        return results[0];
    }


    public interface LocationCallback {
        void onLocationResult(@Nullable Location location);
    }
}
