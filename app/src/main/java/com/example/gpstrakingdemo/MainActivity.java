package com.example.gpstrakingdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;

    //references to the UI elements.
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;

    Switch sw_locationsupdates, sw_gps;

    // variable to remember if we are tracking location or not.
    boolean updateOn = false;

    //location request is a config file for all setting related to FusedLocationProviderClient
    LocationRequest locationRequest;

    //Google's API for location services. The majority of the app function using this class
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);

        //set all properties of locationRequest
        locationRequest = new LocationRequest();

        locationRequest.setInterval(30000);

        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    //most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                }
                else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("using Towers + Wifi");
                }
            }
        });

        updateGPS();


    } //end onCreate method

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requires permission in order to work", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS() {
        //get permissions from the user to track GPS
        //get the current location from the fused client
        //update te UI - i.e. set all properties in their associated text view items.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // user provide permission?!?
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permissions. Put the values of location. XXX into the UI components.
                    updateUIValues(location);
                }
            });
        }
        else {
            //ask for permissions!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }
    private void updateUIValues(Location location) {
        //update all of the text view objects with a new location.
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAltitude()));

        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Not available");
        }
        if (location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("Not available");
        }
    }
}