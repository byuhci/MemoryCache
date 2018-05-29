package com.example.jipark.memorycache;

import com.example.jipark.memorycache.models.GeoMemory;
import com.example.jipark.memorycache.models.Memory;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jipark on 2/26/18.
 */

public class Utils {
    private static final Utils ourInstance = new Utils();

    public static Utils getInstance() {
        return ourInstance;
    }

    public static final int GEOFENCE_RADIUS = 100;
    private LatLng currentLatLng;
    private ArrayList<Geofence> geofences;
    private HashMap<String, GeoMemory> geoMemories;
    private boolean verboseMode = false;

    private Set<String> notificationMemorySet;
    private ArrayList<String> createdMemories;

    private ArrayList<Memory> notificationMemories;

    private Utils() {
        currentLatLng = new LatLng(0, 0);
        geofences = new ArrayList<>();

        // Keeps track of Geofence and Memory pairs with their UUIDs.
        geoMemories = new HashMap<>();

        // List of IDs to trigger the NotificationActivity for, ensures that we have no duplicates.
        notificationMemorySet = new HashSet<>();

        // This list keeps track of the created Memories so that they don't trigger the NotificationActivity.
        createdMemories = new ArrayList<>();

        // This is the list that contains all of the memories to be listed inside of the NotificationActivity.
        notificationMemories = new ArrayList<>();
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public void setCurrentLatLng(LatLng currentLatLng) {
        this.currentLatLng = currentLatLng;
    }

    public ArrayList<Geofence> getGeofences() {
        return geofences;
    }

    public void setGeofences(ArrayList<Geofence> geofences) {
        this.geofences = geofences;
    }

    public HashMap<String, GeoMemory> getGeoMemories() {
        return geoMemories;
    }

    public boolean isVerboseModeOn() {
        return verboseMode;
    }

    public void setVerboseMode(boolean verboseMode) {
        this.verboseMode = verboseMode;
    }

    public Set<String> getNotificationMemorySet() {
        return notificationMemorySet;
    }

    public void resetNotificationMemorySet() {
        notificationMemorySet.clear();
    }

    public ArrayList<String> getCreatedMemories() {
        return createdMemories;
    }

    public void resetNotificationMemories() {
        notificationMemories.clear();
    }

    public ArrayList<Memory> getNotificationMemories() {
        return notificationMemories;
    }
}
