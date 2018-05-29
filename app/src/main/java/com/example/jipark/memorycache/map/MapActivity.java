package com.example.jipark.memorycache.map;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.jipark.memorycache.CreateMemoryActivity;
import com.example.jipark.memorycache.DAO;
import com.example.jipark.memorycache.R;
import com.example.jipark.memorycache.Utils;
import com.example.jipark.memorycache.models.GeoMemory;
import com.example.jipark.memorycache.models.Memory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, DAO.DataReceivedListener {

    // Constants
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int GEOFENCE_RADIUS = 100;
    private static final int GEOFENCE_EXPIRATION = 6000;

    // Variables
//    private ArrayList<Geofence> mGeofenceList;
    private GeofencingClient mGeofencingClient;
    private PendingIntent geofencePendingIntent;
    private Boolean locationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
//    private LatLng mCurrentLatLng;

    FloatingActionButton mLaunchCreateMemoryButton;
    FloatingActionButton mStartHikeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        init();
//        mGeofenceList = new ArrayList<>();
        getLocationPermission();
        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }


    /**
     * Initializer for views and onClickListeners
     */
    private void init() {
        mLaunchCreateMemoryButton = findViewById(R.id.launch_create_memory_button);
        mLaunchCreateMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Grab current latitude and longitude
                getDeviceLocation(); //This stores the device's location into mCurrentLatLng
                Intent intent = new Intent(MapActivity.this, CreateMemoryActivity.class);
                startActivity(intent);
            }
        });

        mStartHikeButton = findViewById(R.id.start_hike_button);
//        mStartHikeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getDeviceLocation();
////                DAO.getInstance().getMemories(DAO.MEMORY_COUNT, MapActivity.this);
//
//                // The memories will be returned inside the onSuccess function
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addIntentToGeofences();
    }

    /**
     * This should be called any time a the geofence list changes.
     */
    private void addIntentToGeofences() {
        if (Utils.getInstance().getGeofences().size() > 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Geofences added
                            // ...
                            System.out.println("WE ADDED A GEOFENCE");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to add geofences
                            // ...
                            System.out.println("WE FAILED TO ADD A GEOFENCE");
                        }
                    });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        if (locationPermissionGranted) {
            getDeviceLocation();
            DAO.getInstance().getGeoMemories(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        addIntentToGeofences();
    }

//    private void buildGeofence(String key, double latitude, double longitude) {
//        Utils.getInstance().getGeofences().add(new Geofence.Builder()
//                .setRequestId(key)
//                .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS)
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_DWELL |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setLoiteringDelay(10000)
//                .build());
//    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(Utils.getInstance().getGeofences());
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, LocationAlertIntentService.class);
        geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    /**
     * Get current location of the user's device
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location)task.getResult();
                            Log.d(TAG, currentLocation + " ");
                            System.out.println(currentLocation);
                            if(currentLocation != null) {
                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                Utils.getInstance().setCurrentLatLng(latLng);
                                moveCamera(Utils.getInstance().getCurrentLatLng(), DEFAULT_ZOOM);
                            }
                        }
                        else {
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch(SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /**
     * Moves map camera to specified LatLng location
     * @param latLng
     * @param zoom
     */
    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to : lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Initialize Google Maps
     */
    private void initMap() {
        Log.d(TAG, "initMap: Initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: Called");
        locationPermissionGranted = false;
        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: Permission failed");
                            return;
                        }
                    }
                    locationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionResult: Permission granted");
                    // Initialize our map
                    initMap();
                }
            }
        }
    }

    /**
     * Check if user has location permissions.
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting location permissions");
        String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION };
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                initMap();
            }
            else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /*****************************************
     * DataReceivedListener Override Methods *
     *****************************************/

    /**
     * When getting memory starts
     */
    @Override
    public void onStart() {
        super.onStart();
        // Start progress bar
    }

    @Override
    public void onGetGeoMemoriesSuccess(ArrayList<GeoMemory> geoMemories) {
        ArrayList<Geofence> geofences = new ArrayList<>();
        for (GeoMemory geoMemory : geoMemories) {
            geofences.add(geoMemory.getGeofence());
            Utils.getInstance().getGeoMemories().put(geoMemory.getGeofence().getRequestId(), geoMemory);

            if (Utils.getInstance().isVerboseModeOn()) {
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(geoMemory.getMemory().getLatitude(), geoMemory.getMemory().getLongitude()))
                        .radius(GEOFENCE_RADIUS)
                        .strokeColor(Color.RED)
                        .fillColor(Color.BLUE));
            }
        }
        Utils.getInstance().setGeofences(geofences);
        addIntentToGeofences();
    }

    /**
     * Getting memories from database is finished and successful
     * @param memories
     */
    @Override
    public void onSuccess(ArrayList<Memory> memories) {
        // Do something with array
        // End progress bar
//        for (Memory memory : memories) {
//            System.out.println(memory.getLatitude() + " " + memory.getLongitude());
//        }
    }

    /**
     * Getting memories has failed
     * @param errorLog
     */
    @Override
    public void onFail(String errorLog) {
        System.out.println(errorLog);
    }
}
