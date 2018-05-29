package com.example.jipark.memorycache.main;


import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.jipark.memorycache.R;
import com.example.jipark.memorycache.Utils;
import com.example.jipark.memorycache.map.MapActivity;
import com.example.jipark.memorycache.models.Memory;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // Variables
    Button mLaunchMapActivity;
    CheckBox mVerboseCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOk()) {
            init();
        }
    }
          
    /**
     * Initialize buttons and setOnClickListeners
     */
    private void init() {
        mVerboseCheckBox = findViewById(R.id.verbose_mode_checkbox);

        mLaunchMapActivity = findViewById(R.id.btn_launch_map_activity);
        mLaunchMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.getInstance().setVerboseMode(mVerboseCheckBox.isChecked());
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Check if Google Play Services version is compatible with user's device
     * @return
     */
    private boolean isServicesOk() {
        Log.d(TAG, "isServicesOk: checking Google Services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            // Everything is good.  User can make map requests.
            Log.d(TAG, "isServicesOk: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // An error has occurred, but can be resolved.
            Log.d(TAG, "isServicesOk: An error has occurred, but it can be resolved.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
            return false;
        }
        else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
