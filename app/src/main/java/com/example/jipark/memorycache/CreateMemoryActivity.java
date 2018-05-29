package com.example.jipark.memorycache;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jipark.memorycache.models.GeoMemory;
import com.example.jipark.memorycache.models.Memory;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


public class CreateMemoryActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private final String AB = "0123456789ABCDEFGHJKLMNOPQRSTUVWXYZ"; //doesn't contain I or l for avoiding ambiguity
    private final int UNIQUE_MEMORY_ID_LENGTH = 5;
    private final int PICK_IMAGE_REQUEST = 71;
    private static final int CAMERA_REQUEST = 100;

    private String filePath;
    private File destination;
    private ImageView mImageHolder;
    private ProgressBar mImageProgressBar;
    private TextView mImageHolderText;
    private EditText mDescriptionInput;
    private FloatingActionButton mAddImageButton;
    private Button mCreateMemoryButton;
    private Button mCancelButton;
    private byte[] bytebuff;

//    private LatLng mCurrentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memory);

        // Initialize image holder
        mImageHolder = findViewById(R.id.image_holder);
        mImageProgressBar = findViewById(R.id.image_progress);
        mImageHolderText = findViewById(R.id.image_holder_text);

        // Initialize EditText
        mDescriptionInput = findViewById(R.id.description_input);

        // Initialize buttons
        mAddImageButton = findViewById(R.id.add_image_button);
        mCreateMemoryButton = findViewById(R.id.create_memory_button);
        mCancelButton = findViewById(R.id.cancel_button);

        String name =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");

        // Setting onClick listeners
        setAddImageButtonOnClick();
        setCreateMemoryButtonOnClick();
        setCancelButtonOnClick();

        // Picasso Library use example
        // Picasso.with(this).load("https://path/to/image").into(mImageHolder);

        // Current location... CATCH: This grabs the location of the device on the map before this activity gets launched.  If the user moves with this activity on, the location doesn't update.
//        mCurrentLatLng = new LatLng(getIntent().getDoubleExtra("CURRENT_LATITUDE", 0), getIntent().getDoubleExtra("CURRENT_LONGITUDE", 0));
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
    private void setAddImageButtonOnClick() {
        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void setCreateMemoryButtonOnClick() {
        mCreateMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    private void setCancelButtonOnClick() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Browse image gallery to pick image to upload.
     */
    private void chooseImage() {

//        Intent pictureIntent = new Intent(
//                MediaStore.ACTION_IMAGE_CAPTURE
//        );
//        if(pictureIntent.resolveActivity(getPackageManager()) != null) {
//          //  pictureIntent.setType("image/*");
//            startActivityForResult(pictureIntent,
//                    REQUEST_CAPTURE_IMAGE);
//
//        }
//

        Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.setType("image/*");
        if(cameraIntent.resolveActivity(getPackageManager()) != null) {
           // startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_IMAGE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
            mImageProgressBar.setVisibility(View.VISIBLE);
            mImageHolderText.setVisibility(View.INVISIBLE);
        }
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        mImageProgressBar.setVisibility(View.VISIBLE);
//        mImageHolderText.setVisibility(View.INVISIBLE);
    }

    /**
     * After choosing an image, displaying it to the image holder view
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageProgressBar.setVisibility(View.INVISIBLE);
        mImageHolderText.setVisibility(View.VISIBLE);
//        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
//                && data != null && data.getData() != null )
//        {
//            filePath = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                mImageHolder.setImageBitmap(bitmap);
//                mAddImageButton.setVisibility(View.INVISIBLE);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                && data != null) {


//            try {
                //FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                //filePath = destination.getAbsolutePath();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImageHolder.setImageBitmap(bitmap);
                mAddImageButton.setVisibility(View.INVISIBLE);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bytebuff = baos.toByteArray();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            if (data != null && data.getExtras() != null && data.getData() != null) {
 //               filePath = data.getData();
//              try {


//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//          }
        }
    }

    /**
     * Upload image to Firebase, on success, write to database
     */
    private void uploadImage() {
        if(bytebuff != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = DAO.getInstance().getStorageReference().child("images/"+ UUID.randomUUID().toString());

            ref.putBytes(bytebuff)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateMemoryActivity.this, "Created", Toast.LENGTH_SHORT).show();

                            // Write to Firebase database
                            Uri imageLink = taskSnapshot.getMetadata().getDownloadUrl();
                            writeMemoryToDatabase(imageLink.toString());

                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateMemoryActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        else {
            //TODO: No image selected... do something here
        }
    }

    private void writeMemoryToDatabase(String imageLink) {
        LatLng currentLatLng = Utils.getInstance().getCurrentLatLng();

        // Generate random ID
        String UUID = generateRandomID(UNIQUE_MEMORY_ID_LENGTH);

        // Create Memory Object
        String descriptionText = mDescriptionInput.getText().toString();
        Memory memory = new Memory(currentLatLng.latitude, currentLatLng.longitude, descriptionText, imageLink, null);

        // Create Geofence Object
        Geofence geofence = new Geofence.Builder()
                .setRequestId(UUID)
                .setCircularRegion(currentLatLng.latitude, currentLatLng.longitude, Utils.GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(10000)
                .build();

        // Add Geofence to the list of geofences which the LocationAlertIntentService is using
        Utils.getInstance().getGeofences().add(geofence);

        // Combine Memory and Geofence objects
        GeoMemory geoMemory = new GeoMemory(memory, geofence);

        // Insert to HashMap
        Utils.getInstance().getGeoMemories().put(UUID, geoMemory);

        // Insert to created Memory list
        Utils.getInstance().getCreatedMemories().add(UUID);

        // Send to Firebase Database
        DAO.getInstance().writeObjectsToDatabase(UUID, geoMemory);

//        Random r = new Random();
//        // DUMMY DATA
//        for (int i = 0; i < 15; i++) {
//            String tUUID = generateRandomID(UNIQUE_MEMORY_ID_LENGTH);
//            double randomValue = 1 + (10 - 1) * r.nextDouble();
//            String tDescriptionText = "test test test";
//            Memory tMemory = new Memory(currentLatLng.latitude - randomValue, currentLatLng.longitude + randomValue, tDescriptionText, imageLink, null);
//
//            Geofence tGeofence = new Geofence.Builder()
//                    .setRequestId(tUUID)
//                    .setCircularRegion(currentLatLng.latitude - randomValue, currentLatLng.longitude + randomValue, Utils.GEOFENCE_RADIUS)
//                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                    .setLoiteringDelay(10000)
//                    .build();
//
//            GeoMemory tGeoMemory = new GeoMemory(tMemory, tGeofence);
//
//            DAO.getInstance().writeObjectsToDatabase(tUUID, tGeoMemory);
//
//        }
    }

    private String generateRandomID(int len) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
