package com.example.jipark.memorycache.models;

import com.google.android.gms.location.Geofence;

/**
 * Created by jipark on 3/1/18.
 */

public class GeoMemory {
    private Memory memory;
    private Geofence geofence;

    public GeoMemory(Memory memory, Geofence geofence) {
        this.memory = memory;
        this.geofence = geofence;
    }

    public Memory getMemory() {
        return memory;
    }

    public Geofence getGeofence() {
        return geofence;
    }
}
