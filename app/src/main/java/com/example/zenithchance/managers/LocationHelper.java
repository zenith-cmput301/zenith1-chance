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

/**
 * Helper class for getting device location using Google Play Services.
 * Provides methods to get current location and calculate distances between points.
 *
 * @author Sabrina
 * @version 1.0
 */
public class LocationHelper {

    private final FusedLocationProviderClient fusedLocationClient;

    /**
     * Creates a new LocationHelper instance.
     *
     * @param context Application context
     */
    public LocationHelper(Context context) {
        // Use app context to avoid leaking an Activity
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(
                context.getApplicationContext()
        );
    }

    /**
     * Requests a single location update.
     *
     * @param callback Callback to receive the location result
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
     * Converts an Android Location to a Firebase GeoPoint.
     *
     * @param location The Android Location object
     * @return GeoPoint with latitude and longitude, or null if location is null
     */
    @Nullable
    public static GeoPoint locationToGeoPoint(@Nullable Location location) {
        if (location == null) return null;
        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    /**
     * Calculates the distance between two GeoPoints in meters.
     *
     * @param point1 First GeoPoint
     * @param point2 Second GeoPoint
     * @return Distance in meters, or Float.MAX_VALUE if either point is null
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


    /**
     * Callback interface for receiving location results.
     */
    public interface LocationCallback {
        /**
         * Called when location is retrieved or fails.
         *
         * @param location The location result, or null if location unavailable
         */
        void onLocationResult(@Nullable Location location);
    }
}
