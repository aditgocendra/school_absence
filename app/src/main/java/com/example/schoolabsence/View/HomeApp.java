package com.example.schoolabsence.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.schoolabsence.Model.ModelAbsenceUsers;

import com.example.schoolabsence.Model.ModelLocationSchool;
import com.example.schoolabsence.Model.ModelUser;
import com.example.schoolabsence.R;
import com.example.schoolabsence.Utility.Algorithm;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.databinding.ActivityHomeAppBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeApp extends AppCompatActivity {

    private ActivityHomeAppBinding binding;
    private LocationRequest locationRequest;

    private double latitudeSchool, longitudeSchool;
    private float maxDistance;

    // absence for today
    private ModelAbsenceUsers userAbsence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();
        setDataUser();
        setLocationSchool();
        setDistanceAbsence();
        checkUserAbsence();
        setDateTime();
    }



    private void listenerComponent() {
        binding.accountImage.setOnClickListener(view -> {
            if (GlobalVariable.roleCurrent != null){
                GlobalFunction.updateUI(this, Profile.class);
            }
        });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        binding.absenceBtn.setOnClickListener(view -> {
            if (GlobalVariable.roleCurrent == null){
                return;
            }
            binding.absenceBtn.setEnabled(false);

            if (userAbsence != null){
                if (!userAbsence.getTimeIn().equals("-") && !userAbsence.getTimeOut().equals("-")){
                    binding.absenceBtn.setEnabled(true);
                    Toast.makeText(HomeApp.this, "You have done presence today", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(HomeApp.this, "Please wait until the presence process is complete", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();
                }
            }else{
                Toast.makeText(HomeApp.this, "Please wait until the presence process is complete", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
        });

    }

    private void getCurrentLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(HomeApp.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (GlobalFunction.isGPSEnabled(HomeApp.this)) {
                    LocationServices.getFusedLocationProviderClient(HomeApp.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(HomeApp.this).removeLocationUpdates(this);
                            if (locationResult.getLocations().size() > 0){
                                int index = locationResult.getLocations().size() - 1;
                                double latitudeCurrent = locationResult.getLocations().get(index).getLatitude();
                                double longitudeCurrent = locationResult.getLocations().get(index).getLongitude();

                                absenceTeacher(latitudeCurrent, longitudeCurrent);

                            }else {
                                Toast.makeText(HomeApp.this, "Find location failed", Toast.LENGTH_SHORT).show();
                            }
                            binding.absenceBtn.setEnabled(true);
                        }
                    }, Looper.getMainLooper());

                } else {
                    GlobalFunction.turnOnGPS(locationRequest, HomeApp.this);
                    binding.absenceBtn.setEnabled(true);
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1005);
                binding.absenceBtn.setEnabled(true);
            }
        }
    }

    private void absenceTeacher(double latitudeCurrent, double longitudeCurrent) {
        float distanceResult;

        if (maxDistance == 0){
            Toast.makeText(this, "Error, rangefinder, please come back later", Toast.LENGTH_SHORT).show();
            return;
        }
        distanceResult = (float) Algorithm.haversine(latitudeCurrent, longitudeCurrent, latitudeSchool, longitudeSchool);

        // convert to meters
        float distanceMeters = distanceResult * 1000;

        if (distanceMeters <= maxDistance){
            saveDataAbsence(latitudeCurrent, longitudeCurrent, distanceMeters);
        }
    }

    private void saveDataAbsence(double latitudeCurrent, double longitudeCurrent, float distanceMeters) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        String day = monthName.substring(0, 3)+" "+cal.get(Calendar.DATE)+", "+cal.get(Calendar.YEAR);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time = sdf.format(new Date());

        ModelAbsenceUsers modelAbsenceUsers;

        String hour = time.substring(0, 2);

        if (userAbsence == null){
            binding.paramLate.setVisibility(View.VISIBLE);
            if (Integer.parseInt(hour) >= 12){
                modelAbsenceUsers = new ModelAbsenceUsers(
                        0,
                        0,
                        latitudeCurrent,
                        longitudeCurrent,
                        "-",
                        distanceMeters+" Meters",
                        day,
                        "-",
                        time,
                        GlobalVariable.uidCurrent
                );

                binding.layoutOut.setVisibility(View.VISIBLE);
                binding.distanceOut.setText(modelAbsenceUsers.getDistanceOut());
                binding.hourTimeOut.setText(modelAbsenceUsers.getTimeOut() + " WIB");

                if (Integer.parseInt(hour) < 15){
                    binding.absenceOut.setColorFilter(getResources().getColor(R.color.green));
                }

                if (Integer.parseInt(hour) >= 15){
                    binding.absenceOut.setColorFilter(getResources().getColor(R.color.red_calm));
                }

            }else {
                modelAbsenceUsers = new ModelAbsenceUsers(
                        latitudeCurrent,
                        longitudeCurrent,
                        0,
                        0,
                        distanceMeters+" Meters",
                        "-",
                        day,
                        time,
                        "-",
                        GlobalVariable.uidCurrent
                );

                binding.layoutIn.setVisibility(View.VISIBLE);
                binding.distanceIn.setText(modelAbsenceUsers.getDistanceIn());
                binding.hourTimeIn.setText(modelAbsenceUsers.getTimeIn() + "WIB");

                if (Integer.parseInt(hour) < 11){
                    binding.absenceIn.setColorFilter(getResources().getColor(R.color.green));
                }

                if (Integer.parseInt(hour) >= 11){
                    binding.absenceIn.setColorFilter(getResources().getColor(R.color.red_calm));
                }
            }
        }else{
            binding.paramLate.setVisibility(View.VISIBLE);
            if (Integer.parseInt(hour) >= 12){

                userAbsence.setTimeOut(time);
                userAbsence.setDistanceOut(distanceMeters+" Meters");
                userAbsence.setLatitudeOut(latitudeCurrent);
                userAbsence.setLongitudeOut(longitudeCurrent);

                binding.layoutOut.setVisibility(View.VISIBLE);
                binding.distanceOut.setText(userAbsence.getDistanceOut());
                binding.hourTimeOut.setText(userAbsence.getTimeOut() + " WIB");

                if (Integer.parseInt(hour) < 15){
                    binding.absenceOut.setColorFilter(getResources().getColor(R.color.green));
                }

                if (Integer.parseInt(hour) >= 15){
                    binding.absenceOut.setColorFilter(getResources().getColor(R.color.red_calm));
                }

            }else {
                userAbsence.setTimeIn(time);
                userAbsence.setDistanceIn(distanceMeters+" Meters");
                userAbsence.setLatitudeIn(latitudeCurrent);
                userAbsence.setLatitudeIn(latitudeCurrent);

                binding.layoutIn.setVisibility(View.VISIBLE);
                binding.distanceIn.setText(userAbsence.getDistanceIn());
                binding.hourTimeIn.setText(userAbsence.getTimeIn() + " WIB");

                if (Integer.parseInt(hour) < 11){
                    binding.absenceIn.setColorFilter(getResources().getColor(R.color.green));
                }

                if (Integer.parseInt(hour) >= 11){
                    binding.absenceIn.setColorFilter(getResources().getColor(R.color.red_calm));
                }
            }

            modelAbsenceUsers = userAbsence;
        }

        GlobalVariable.reference.child("absence_users").child(day).child(GlobalVariable.uidCurrent).setValue(modelAbsenceUsers).addOnSuccessListener(unused -> {
            Toast.makeText(HomeApp.this, "Presence Success", Toast.LENGTH_SHORT).show();
            binding.imageAbsence.setImageResource(R.drawable.ic_clear_absence);
            binding.textDirection.setText("Thank you for taking Presence today");

        }).addOnFailureListener(e -> Toast.makeText(HomeApp.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        binding.absenceBtn.setEnabled(true);
    }

    private void checkUserAbsence() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        String day = monthName.substring(0, 3)+" "+cal.get(Calendar.DATE)+", "+cal.get(Calendar.YEAR);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time = sdf.format(new Date());
        int hour = Integer.parseInt(time.substring(0, 2));

        GlobalVariable.reference.child("absence_users").child(day).child(GlobalVariable.uidCurrent).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                userAbsence = task.getResult().getValue(ModelAbsenceUsers.class);
                if (userAbsence != null){

                    if (userAbsence.getTimeIn().equals("-") && hour < 12){
                        binding.textDirection.setText("Welcome, please presence in for today :)");
                        binding.imageAbsence.setImageResource(R.drawable.ic_absence);
                    }else if(userAbsence.getTimeOut().equals("-") && hour >= 12){
                        binding.textDirection.setText("Welcome, please presence out for today :)");
                        binding.imageAbsence.setImageResource(R.drawable.ic_absence);
                    }else{
                        binding.imageAbsence.setImageResource(R.drawable.ic_clear_absence);
                        binding.textDirection.setText("Thank you for taking presence today");
                        binding.absenceBtn.setEnabled(false);
                    }

                    binding.paramLate.setVisibility(View.VISIBLE);

                    // Absence in
                    if (!userAbsence.getTimeIn().equals("-")){
                        // Set too late or not presence
                        binding.layoutIn.setVisibility(View.VISIBLE);
                        String hourAbsenceTimeIn = userAbsence.getTimeIn().substring(0, 2);
                        binding.distanceIn.setText(userAbsence.getDistanceIn());
                        binding.hourTimeIn.setText(userAbsence.getTimeIn() + " WIB");
                        if (Integer.parseInt(hourAbsenceTimeIn) < 11){
                            binding.absenceIn.setColorFilter(getResources().getColor(R.color.green));
                        }
                        if (Integer.parseInt(hourAbsenceTimeIn) >= 11){
                            binding.absenceIn.setColorFilter(getResources().getColor(R.color.red_calm));
                        }
                    }

                    // Absence out
                    if (!userAbsence.getTimeOut().equals("-")){
                        binding.layoutOut.setVisibility(View.VISIBLE);
                        String hourAbsenceTimeOut = userAbsence.getTimeOut().substring(0, 2);
                        binding.distanceOut.setText(userAbsence.getDistanceOut());
                        binding.hourTimeOut.setText(userAbsence.getTimeOut() + " WIB");
                        if (Integer.parseInt(hourAbsenceTimeOut) < 15){
                            binding.absenceOut.setColorFilter(getResources().getColor(R.color.green));
                        }
                        if (Integer.parseInt(hourAbsenceTimeOut) >= 15){
                            binding.absenceOut.setColorFilter(getResources().getColor(R.color.red_calm));
                        }
                    }
                }else {
                    binding.textDirection.setText("Welcome, please presence for today :)");
                    binding.imageAbsence.setImageResource(R.drawable.ic_absence);
                }
                binding.absenceBtn.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(HomeApp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataUser() {
        GlobalVariable.reference.child("users").child(GlobalVariable.uidCurrent).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                if (modelUser != null){
                    GlobalVariable.roleCurrent = modelUser.getRole();
                    GlobalVariable.username = modelUser.getUsername();
                    GlobalVariable.urlPhoto = modelUser.getUrl_photo();
                    GlobalVariable.email = modelUser.getEmail();
                    GlobalVariable.nik = modelUser.getNik();

                    if (!modelUser.getUrl_photo().equals("-")){
                        Picasso.get().load(modelUser.getUrl_photo()).into(binding.accountImage);
                    }
                }
            }else {
                Toast.makeText(HomeApp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLocationSchool() {
        GlobalVariable.reference.child("location_school").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelLocationSchool modelLocationSchool = task.getResult().getValue(ModelLocationSchool.class);
                if (modelLocationSchool != null){
                    latitudeSchool = modelLocationSchool.getMedian_lat();
                    longitudeSchool = modelLocationSchool.getMedian_long();
                }else {
                    Toast.makeText(HomeApp.this, "School location not set", Toast.LENGTH_SHORT).show();
                    binding.absenceBtn.setEnabled(false);
                }
            }else {
                Toast.makeText(HomeApp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDistanceAbsence() {
        GlobalVariable.reference.child("max_distance").child("value").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Float max_distance = task.getResult().getValue(Float.class);
                if (max_distance != null){
                    maxDistance = max_distance;
                }else {
                    Toast.makeText(HomeApp.this, "Distance Attendance not found", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(HomeApp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDateTime() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String month_name = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        binding.dateText.setText(String.valueOf(cal.get(Calendar.DATE)));
        String monthYear = month_name+" "+cal.get(Calendar.YEAR);
        binding.monthAndYear.setText(monthYear);

        Log.d("date", String.valueOf(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)));
    }
}