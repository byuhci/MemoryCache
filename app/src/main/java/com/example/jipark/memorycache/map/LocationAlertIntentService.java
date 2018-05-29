package com.example.jipark.memorycache.map;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;


import com.example.jipark.memorycache.models.Memory;
import com.example.jipark.memorycache.notification.INotify;
import com.example.jipark.memorycache.notification.Notify;

import com.example.jipark.memorycache.Utils;

import com.example.jipark.memorycache.notification.NotificationActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jipark on 2/21/18.
 */

public class LocationAlertIntentService extends IntentService {
    private static final String IDENTIFIER = "LocationAlertIS";

    public LocationAlertIntentService() {
        super(IDENTIFIER);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(IDENTIFIER, "" + getErrorString(geofencingEvent.getErrorCode()));
            return;
        }

        Log.i(IDENTIFIER, geofencingEvent.toString());

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String transitionDetails = getGeofenceTransitionInfo(
                    triggeringGeofences);
            String transitionType = getTransitionString(geofenceTransition);
            notifyLocationAlert(transitionType, transitionDetails);
        }
    }

    private String getGeofenceTransitionInfo(List<Geofence> triggeringGeofences) {
        ArrayList<String> locationNames = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            locationNames.add(getLocationName(geofence.getRequestId()));
        }
        return TextUtils.join(", ", locationNames);
    }

    private String getLocationName(String key) {
        String[] strs = key.split("-");

        String locationName = null;
        if (strs != null && strs.length == 2) {
            double lat = Double.parseDouble(strs[0]);
            double lng = Double.parseDouble(strs[1]);

            locationName = getLocationNameGeocoder(lat, lng);
        }
        if (locationName != null) {
            return locationName;
        } else {
            return key;
        }
    }

    private String getLocationNameGeocoder(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception ioException) {
            Log.e("", "Error in getting location name for the location");
        }

        if (addresses == null || addresses.size() == 0) {
            Log.d("", "no location name");
            return null;
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressInfo = new ArrayList<>();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressInfo.add(address.getAddressLine(i));
            }

            return TextUtils.join(System.getProperty("line.separator"), addressInfo);
        }
    }

    private String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Error: Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Error: Too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Error: Too many PENDING_INTENTS";
            default:
                return "Error: Geofence error";
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "ENTER";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "EXIT";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "DWELL";
            default:
                return "TRANSITION";
        }
    }

    private void notifyLocationAlert(String locTransitionType, String locationDetails) {
        Log.d(IDENTIFIER, "Inside notifyLocationAlert");
        Log.d(IDENTIFIER, locTransitionType);
        Log.d(IDENTIFIER, locationDetails);

        // This is to parse locationDetails, in case we enter or exit an area where two or more Geofences overlap each other.
        // If overlapping, they will come in this format: M1473, O1NPY, and this will parse the IDs and store them inside of this array.
        String[] values = locationDetails.replaceAll("^[,\\s]+", "").split("[,\\s]+");

        if (locTransitionType.equals("ENTER")) {
            Utils.getInstance().resetNotificationMemorySet();
            Utils.getInstance().resetNotificationMemories();
            for (String id : values) {
                if (Utils.getInstance().getCreatedMemories().size() > 0) {
                    for (String createdId : Utils.getInstance().getCreatedMemories()) {
                        if (!id.equals(createdId)) {
                            Utils.getInstance().getNotificationMemorySet().add(id);
                        }
                    }
                }
                else {
                    Utils.getInstance().getNotificationMemorySet().add(id);
                }
            }

            if (Utils.getInstance().getNotificationMemorySet().size() > 0) {

                // Convert notificationMemorySet to notificationMemories to use inside of the NotificationActivity
                for (String id : Utils.getInstance().getNotificationMemorySet()) {
                    Memory memory = Utils.getInstance().getGeoMemories().get(id).getMemory();
                    memory.setId(id);
                    Utils.getInstance().getNotificationMemories().add(memory);
                }

                // Send a notification to the phone.
                INotify notify = new Notify();
                notify.Notify("", "", new NotificationActivity(), getApplicationContext());
            }
        }
        else if (locTransitionType.equals("EXIT")) {
            ArrayList<Memory> removeFromCreatedMemories = new ArrayList<>();
            for (String id : values) {
                for (Memory memory : Utils.getInstance().getNotificationMemories()) {
                    if (id.equals(memory.getId())) {
                        removeFromCreatedMemories.add(memory);
                    }
                }
            }
            if (removeFromCreatedMemories.size() > 0) {
                Utils.getInstance().getNotificationMemories().removeAll(removeFromCreatedMemories);
            }
        }
        Log.d(IDENTIFIER, Utils.getInstance().getNotificationMemories().size() + " ");
    }

    /**
     * Debugging method for logging the values of an array
     * @param values
     */
    private void logArray(String[] values) {
        for (int i = 0; i < values.length; i++) {
            Log.d(IDENTIFIER, values[i]);
        }
    }
}