package com.example.jipark.memorycache;

import com.example.jipark.memorycache.models.GeoMemory;
import com.example.jipark.memorycache.models.Memory;
import com.google.android.gms.location.Geofence;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by jipark on 1/29/18.
 */

public class DAO {

    public interface DataReceivedListener {
        void onStart();
        void onSuccess(ArrayList<Memory> memories);
        void onGetGeoMemoriesSuccess(ArrayList<GeoMemory> geoMemories);
        void onFail(String errorLog);
    }

    // Singleton instance
    private static final DAO ourInstance = new DAO();

    // Database
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    /**
     * Returns database access object singleton reference.
     *
     * @return Singleton instance
     */
    public static DAO getInstance() {
        return ourInstance;
    }

    /**
     * Constructor
     */
    private DAO() {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Method for retrieving specific database key node reference.
     *
     * @param memoryID Unique identifier for each memory
     * @return Database reference pointer
     */
    private DatabaseReference getDBReference(String memoryID) {
        return database.getReference(memoryID);
    }

    /**
     * Method for getting Firebase cloud storage reference.
     *
     * @return Storage reference pointer
     */
    public StorageReference getStorageReference() {
        return storage.getReference();
    }

    /**
     * Method for writing object(s) to the database.
     *
     * @param id Unique identifier
     * @param object   Object to write to database
     */
    public void writeObjectsToDatabase(String id, Object object) {
        DAO.getInstance().getDBReference(id).setValue(object);
    }

    /**
     * Method for reading object(s) from the database.
     *
     * @param memoryID Unique identifier for each memory
     * @return Object retrieved from database key
     */
    public ArrayList<Object> readObjectsFromDatabase(String memoryID) {
        final ArrayList<Object> dataObjects = new ArrayList<>();
        DAO.getInstance().getDBReference(memoryID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    dataObjects.add(child.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
        return dataObjects;
    }

    /**
     * Grab the entire list of existing GeoMemories from firebase.
     * @param listener
     */
    public void getGeoMemories(final DataReceivedListener listener) {
        final ArrayList<GeoMemory> geoMemories = new ArrayList<>();
        listener.onStart();

        DAO.getInstance().getDBReference("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    System.out.println(data.getKey());
                    Memory memory = data.child("memory").getValue(Memory.class);
                    if (memory == null) throw new AssertionError();
                    memory.setId(data.getKey());
                    Geofence geofence = new Geofence.Builder()
                            .setRequestId(data.getKey())
                            .setCircularRegion(memory.getLatitude(), memory.getLongitude(), Utils.GEOFENCE_RADIUS)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_DWELL |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .setLoiteringDelay(10000)
                            .build();

                    GeoMemory geoMemory = new GeoMemory(memory, geofence);
                    geoMemories.add(geoMemory);
                }
                listener.onGetGeoMemoriesSuccess(geoMemories);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFail("Retrieving GeoMemories failed.");
            }
        });
    }
}