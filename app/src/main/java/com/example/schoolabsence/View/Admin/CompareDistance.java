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
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.schoolabsence.Utility.Algorithm;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.databinding.ActivityCompareDistanceBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CompareDistance extends AppCompatActivity {

    private ActivityCompareDistanceBinding binding;

    private LocationRequest locationRequest;
    private List<Address> addressList;

    private double latitudeSchool, longitudeSchool;
    private double latitudeCurrent, longitudeCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompareDistanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        listenerComponent();

    }

    private void listenerComponent(){
        binding.backBtn.setOnClickListener(view -> finish());

        binding.setLocationBtnSchool.setOnClickListener(view -> {
            binding.progressCircular.setVisibility(View.VISIBLE);
            binding.setLocationBtnSchool.setEnabled(false);

            String latSchoolText = binding.schoolLatEdt.getText().toString();
            String longSchoolText = binding.schoolLongEdt.getText().toString();
            String address = binding.schoolLocationEdt.getText().toString();

            if (latSchoolText.isEmpty() && longSchoolText.isEmpty() && !address.isEmpty()){
                LatLng schoolLatLang = GlobalFunction.getLocationFromAddress(CompareDistance.this, address);
                if (schoolLatLang != null){
                    latitudeSchool = schoolLatLang.latitude;
                    longitudeSchool = schoolLatLang.longitude;
                    binding.schoolLatEdt.setText(String.valueOf(latitudeSchool));
                    binding.schoolLongEdt.setText(String.valueOf(longitudeSchool));
                }else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(CompareDistance.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (GlobalFunction.isGPSEnabled(CompareDistance.this)) {
                            LocationServices.getFusedLocationProviderClient(CompareDistance.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(CompareDistance.this).removeLocationUpdates(this);
                                    if (locationResult.getLocations().size() > 0){
                                        int index = locationResult.getLocations().size() - 1;


                                        latitudeSchool = locationResult.getLocations().get(index).getLatitude();
                                        longitudeSchool = locationResult.getLocations().get(index).getLongitude();


                                        Geocoder geocoder = new Geocoder(CompareDistance.this, Locale.getDefault());
                                        try {
                                            addressList = geocoder.getFromLocation(latitudeSchool, longitudeSchool,1);
                                            binding.schoolLocationEdt.setText(addressList.get(0).getAddressLine(0));
                                            binding.schoolLatEdt.setText(String.valueOf(latitudeSchool));
                                            binding.schoolLongEdt.setText(String.valueOf(longitudeSchool));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        Toast.makeText(CompareDistance.this, "Find location failed", Toast.LENGTH_SHORT).show();
                                    }
                                    binding.progressCircular.setVisibility(View.GONE);
                                    binding.setLocationBtnSchool.setEnabled(true);
                                }
                            }, Looper.getMainLooper());

                        } else {
                            GlobalFunction.turnOnGPS(locationRequest, CompareDistance.this);
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

        });

        binding.setLocationBtnYour.setOnClickListener(view -> {
            binding.progressCircular.setVisibility(View.VISIBLE);
            binding.setLocationBtnYour.setEnabled(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(CompareDistance.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (GlobalFunction.isGPSEnabled(CompareDistance.this)) {
                        LocationServices.getFusedLocationProviderClient(CompareDistance.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                LocationServices.getFusedLocationProviderClient(CompareDistance.this).removeLocationUpdates(this);
                                if (locationResult.getLocations().size() > 0){
                                    int index = locationResult.getLocations().size() - 1;

                                    latitudeCurrent = locationResult.getLocations().get(index).getLatitude();
                                    longitudeCurrent = locationResult.getLocations().get(index).getLongitude();

                                    Geocoder geocoder = new Geocoder(CompareDistance.this, Locale.getDefault());
                                    try {
                                        addressList = geocoder.getFromLocation(latitudeCurrent, longitudeCurrent,1);
                                        binding.yourLocationEdt.setText(addressList.get(0).getAddressLine(0));
                                        binding.yourLatEdt.setText(String.valueOf(latitudeCurrent));
                                        binding.yourLongEdt.setText(String.valueOf(longitudeCurrent));

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    Toast.makeText(CompareDistance.this, "Find location failed", Toast.LENGTH_SHORT).show();
                                }
                                binding.progressCircular.setVisibility(View.GONE);
                                binding.setLocationBtnYour.setEnabled(true);
                            }
                        }, Looper.getMainLooper());

                    } else {
                        GlobalFunction.turnOnGPS(locationRequest, CompareDistance.this);
                        binding.progressCircular.setVisibility(View.GONE);
                        binding.setLocationBtnYour.setEnabled(true);
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1005);
                    binding.progressCircular.setVisibility(View.GONE);
                    binding.setLocationBtnYour.setEnabled(true);
                }
            }

        });

        binding.previewMapBtnSchool.setOnClickListener(view -> {
            if (latitudeSchool != 0 && longitudeSchool != 0){
                GlobalFunction.previewStreetMaps(latitudeSchool, longitudeSchool, CompareDistance.this);
            }else {
                Toast.makeText(CompareDistance.this, "Nothing school location set", Toast.LENGTH_SHORT).show();
            }
        });

        binding.previewMapBtnYour.setOnClickListener(view -> {
            if (latitudeCurrent != 0 && longitudeCurrent != 0){
                GlobalFunction.previewStreetMaps(latitudeCurrent, longitudeCurrent, CompareDistance.this);
            }else {
                Toast.makeText(CompareDistance.this, "Nothing your location set", Toast.LENGTH_SHORT).show();
            }
        });

        binding.compareBtn.setOnClickListener(view -> {
            if (latitudeCurrent != 0 && longitudeCurrent != 0 && latitudeSchool != 0 && longitudeSchool != 0){
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.compareBtn.setEnabled(false);
                compareAlgorithm();
            }else {
                Toast.makeText(CompareDistance.this, "Location not set!!", Toast.LENGTH_SHORT).show();
                binding.progressCircular.setVisibility(View.GONE);
                binding.compareBtn.setEnabled(true);
            }
        });

        binding.clearDataBtn.setOnClickListener(view -> {
            binding.schoolLatEdt.setText("");
            binding.schoolLongEdt.setText("");
            binding.schoolLocationEdt.setText("");
            binding.yourLatEdt.setText("");
            binding.yourLongEdt.setText("");
            binding.yourLocationEdt.setText("");
            latitudeSchool = 0;
            longitudeSchool = 0;
            latitudeCurrent = 0;
            longitudeCurrent = 0;
            binding.resultDistanceHaversine.setText("-");
            binding.resultDistanceEuclidean.setText("-");
        });

    }

    private void compareAlgorithm(){
        float euclideanResult = (float) Algorithm.euclidean(latitudeCurrent, longitudeCurrent, latitudeSchool, longitudeSchool);
        float haversineResult = (float) Algorithm.haversine(latitudeCurrent, longitudeCurrent, latitudeSchool, longitudeSchool);

        // km value euclidean
        float oneDegreesEarth = (float) 111.322;
        euclideanResult = euclideanResult * oneDegreesEarth;

        // km value
        Log.d("Distance Km Result", euclideanResult+" / "+haversineResult);


        // convert to meters
        float metersEuclidean = euclideanResult * 1000;
        float metersHaversine = haversineResult * 1000;

        binding.resultDistanceEuclidean.setText(String.valueOf(metersEuclidean));
        binding.resultDistanceHaversine.setText(String.valueOf(metersHaversine));

        binding.progressCircular.setVisibility(View.GONE);
        binding.compareBtn.setEnabled(true);

    }

}