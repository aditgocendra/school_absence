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
import com.example.schoolabsence.Model.ModelLocationSchool;
import com.example.schoolabsence.Utility.Algorithm;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.databinding.ActivityCompareDistanceBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
        setDataSchoolLocation();

    }

    private void listenerComponent(){
        binding.backBtn.setOnClickListener(view -> finish());

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

    private void setDataSchoolLocation(){
        GlobalVariable.reference.child("location_school").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelLocationSchool modelLocationSchool = task.getResult().getValue(ModelLocationSchool.class);
                if (modelLocationSchool != null){
                    binding.schoolLocationEdt.setText(modelLocationSchool.getAddress());
                    binding.schoolLatEdt.setText(String.valueOf(modelLocationSchool.getMedian_lat()));
                    binding.schoolLongEdt.setText(String.valueOf(modelLocationSchool.getMedian_long()));
                    latitudeSchool = modelLocationSchool.getMedian_lat();
                    longitudeSchool = modelLocationSchool.getMedian_long();
                }
            }else{
                Toast.makeText(CompareDistance.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}