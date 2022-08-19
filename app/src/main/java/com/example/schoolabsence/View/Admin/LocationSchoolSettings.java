package com.example.schoolabsence.View.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import com.example.schoolabsence.Model.ModelLocationSchool;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.databinding.ActivityLocationSchoolSettingsBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LocationSchoolSettings extends AppCompatActivity {

    private ActivityLocationSchoolSettingsBinding binding;
    private LocationRequest locationRequest;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    // lat and long
    private double latitudeSchool, latitudeNorth, latitudeSouth, latitudeWest, latitudeEast;
    private double longitudeSchool, longitudeNorth, longitudeSouth, longitudeWest, longitudeEast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationSchoolSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        listenerComponent();
        setDataLocationSchool();
    }

    private void listenerComponent() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.setLocationBtnSchool.setOnClickListener(view -> getLocation("address"));
        binding.northCoordinateSet.setOnClickListener(view -> getLocation("north"));
        binding.southCoordinateBtn.setOnClickListener(view -> getLocation("south"));
        binding.westCoordinateBtn.setOnClickListener(view -> getLocation("west"));
        binding.eastCoordinateBtn.setOnClickListener(view -> getLocation("east"));

        binding.previewMapBtnSchool.setOnClickListener(view -> {
            if (latitudeSchool != 0 && longitudeSchool != 0){
                GlobalFunction.previewStreetMaps(latitudeSchool, longitudeSchool, LocationSchoolSettings.this);
            }else {
                Toast.makeText(LocationSchoolSettings.this, "Location address not set", Toast.LENGTH_SHORT).show();
            }
        });

        binding.saveLocationBtn.setOnClickListener(view -> {
            String address = binding.schoolLocationEdt.getText().toString();

            if (address.isEmpty()){
                Toast.makeText(LocationSchoolSettings.this, "Address not set", Toast.LENGTH_SHORT).show();
            }else if (latitudeNorth == 0 && longitudeNorth == 0){
                Toast.makeText(LocationSchoolSettings.this, "North coordinate not set", Toast.LENGTH_SHORT).show();
            }else if (latitudeSouth == 0 && longitudeSouth == 0){
                Toast.makeText(LocationSchoolSettings.this, "South coordinate not set", Toast.LENGTH_SHORT).show();
            }else if (latitudeWest == 0 && longitudeWest == 0){
                Toast.makeText(LocationSchoolSettings.this, "West coordinate not set", Toast.LENGTH_SHORT).show();
            }else if (latitudeEast == 0 && longitudeEast == 0){
                Toast.makeText(LocationSchoolSettings.this, "East coordinate not set", Toast.LENGTH_SHORT).show();
            }else {
                LatLng median = new LatLng(
                        (latitudeNorth + latitudeSouth) / 2,
                        (longitudeWest + longitudeEast) / 2
                );

                updateDataLocationSchool(
                        address, latitudeNorth, longitudeNorth,
                        latitudeSouth, longitudeSouth,
                        latitudeWest, longitudeWest,
                        latitudeEast, longitudeEast,
                        median.latitude, median.longitude
                );

            }
        });

        binding.clearDataBtn.setOnClickListener(view -> {
            // set 0 variable
            latitudeNorth = 0;
            longitudeNorth = 0;
            latitudeSouth = 0;
            longitudeSouth = 0;
            latitudeWest = 0;
            longitudeWest = 0;
            latitudeEast = 0;
            longitudeEast = 0;

            // clear data in component
            binding.schoolLocationEdt.setText("");
            binding.latitudeNorthEdt.setText("");
            binding.longitudeNorthEdt.setText("");
            binding.latitudeSouthEdt.setText("");
            binding.longitudeSouthEdt.setText("");
            binding.latitudeWestEdt.setText("");
            binding.longitudeWestEdt.setText("");
            binding.latitudeEastEdt.setText("");
            binding.longitudeEastEdt.setText("");

        });

    }

    private void getLocation(String state){
        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.setLocationBtnSchool.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(LocationSchoolSettings.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (GlobalFunction.isGPSEnabled(LocationSchoolSettings.this)) {
                    LocationServices.getFusedLocationProviderClient(LocationSchoolSettings.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(LocationSchoolSettings.this).removeLocationUpdates(this);
                            if (locationResult.getLocations().size() > 0){
                                int index = locationResult.getLocations().size() - 1;

                                if (state.equals("address")){
                                    latitudeSchool = locationResult.getLocations().get(index).getLatitude();
                                    longitudeSchool = locationResult.getLocations().get(index).getLongitude();
                                }

                                if (state.equals("north")){
                                    latitudeNorth = locationResult.getLocations().get(index).getLatitude();
                                    longitudeNorth = locationResult.getLocations().get(index).getLongitude();
                                    binding.latitudeNorthEdt.setText(String.valueOf(latitudeNorth));
                                    binding.longitudeNorthEdt.setText(String.valueOf(longitudeNorth));
                                }

                                if (state.equals("south")){
                                    latitudeSouth = locationResult.getLocations().get(index).getLatitude();
                                    longitudeSouth = locationResult.getLocations().get(index).getLongitude();
                                    binding.latitudeSouthEdt.setText(String.valueOf(latitudeSouth));
                                    binding.longitudeSouthEdt.setText(String.valueOf(longitudeSouth));
                                }

                                if (state.equals("west")){
                                    latitudeWest = locationResult.getLocations().get(index).getLatitude();
                                    longitudeWest = locationResult.getLocations().get(index).getLongitude();
                                    binding.latitudeWestEdt.setText(String.valueOf(latitudeWest));
                                    binding.longitudeWestEdt.setText(String.valueOf(longitudeWest));
                                }

                                if (state.equals("east")){
                                    latitudeEast = locationResult.getLocations().get(index).getLatitude();
                                    longitudeEast = locationResult.getLocations().get(index).getLongitude();
                                    binding.latitudeEastEdt.setText(String.valueOf(latitudeEast));
                                    binding.longitudeEastEdt.setText(String.valueOf(longitudeEast));
                                }

                                Geocoder geocoder = new Geocoder(LocationSchoolSettings.this, Locale.getDefault());
                                try {
                                    if (state.equals("address")){
                                        List<Address> addressList = geocoder.getFromLocation(latitudeSchool, longitudeSchool,1);
                                        binding.schoolLocationEdt.setText(addressList.get(0).getAddressLine(0));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(LocationSchoolSettings.this, "Find location failed", Toast.LENGTH_SHORT).show();
                            }
                            binding.progressCircular.setVisibility(View.GONE);
                            binding.setLocationBtnSchool.setEnabled(true);
                        }
                    }, Looper.getMainLooper());

                } else {
                    GlobalFunction.turnOnGPS(locationRequest, LocationSchoolSettings.this);
                    binding.progressCircular.setVisibility(View.GONE);
                    binding.setLocationBtnSchool.setEnabled(true);
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1005);
                binding.progressCircular.setVisibility(View.GONE);
                binding.setLocationBtnSchool.setEnabled(true);
            }
        }
    }

    private void updateDataLocationSchool(String address, double lat_north, double long_north, double lat_south, double long_south, double lat_west, double long_west, double lat_east, double long_east, double median_lat, double median_long){
        ModelLocationSchool modelLocationSchool = new ModelLocationSchool(
                address, lat_north, long_north,
                lat_south, long_south,
                lat_west, long_west,
                lat_east, long_east,
                median_lat, median_long
        );

        reference.child("location_school").setValue(modelLocationSchool)
                .addOnSuccessListener(unused -> Toast.makeText(LocationSchoolSettings.this, "Location school is update", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(LocationSchoolSettings.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setDataLocationSchool(){
        reference.child("location_school").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelLocationSchool modelLocationSchool = task.getResult().getValue(ModelLocationSchool.class);
                if (modelLocationSchool != null){
                    // set component
                    binding.schoolLocationEdt.setText(modelLocationSchool.getAddress());
                    binding.latitudeNorthEdt.setText(String.valueOf(modelLocationSchool.getLat_north()));
                    binding.longitudeNorthEdt.setText(String.valueOf(modelLocationSchool.getLong_north()));
                    binding.latitudeSouthEdt.setText(String.valueOf(modelLocationSchool.getLat_south()));
                    binding.longitudeSouthEdt.setText(String.valueOf(modelLocationSchool.getLong_south()));
                    binding.latitudeWestEdt.setText(String.valueOf(modelLocationSchool.getLat_west()));
                    binding.longitudeWestEdt.setText(String.valueOf(modelLocationSchool.getLong_west()));
                    binding.latitudeEastEdt.setText(String.valueOf(modelLocationSchool.getLat_east()));
                    binding.longitudeEastEdt.setText(String.valueOf(modelLocationSchool.getLong_east()));

                    // set variable
                    latitudeSchool = modelLocationSchool.getMedian_lat();
                    longitudeSchool = modelLocationSchool.getMedian_long();

                    latitudeNorth = modelLocationSchool.getLat_north();
                    longitudeNorth = modelLocationSchool.getLong_north();

                    latitudeSouth = modelLocationSchool.getLat_south();
                    longitudeSouth = modelLocationSchool.getLong_south();

                    latitudeWest = modelLocationSchool.getLat_west();
                    longitudeWest = modelLocationSchool.getLong_west();

                    latitudeEast = modelLocationSchool.getLat_east();
                    longitudeEast = modelLocationSchool.getLong_east();

                }else {
                    Toast.makeText(LocationSchoolSettings.this, "Location data is null", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(LocationSchoolSettings.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}