package com.example.schoolabsence.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.schoolabsence.Model.ModelUser;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.View.Admin.AbsenceRecap;
import com.example.schoolabsence.View.Admin.AdministratorMenu;
import com.example.schoolabsence.View.Authentication.Auth;
import com.example.schoolabsence.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;

    private ActivityResultLauncher<String> imagePick;
    private boolean onImageChange = false;
    private String email, username, nik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();
        pickImageSetup();
        setDataProfile();

    }

    private void listenerComponent() {
        binding.backBtn.setOnClickListener(view -> finish());
        binding.pickPhotoBtn.setOnClickListener(view -> imagePick.launch("image/*"));
        binding.headBtn.setOnClickListener(v -> GlobalFunction.updateUI(Profile.this, AbsenceRecap.class));
        binding.administratorBtn.setOnClickListener(view -> GlobalFunction.updateUI(Profile.this, AdministratorMenu.class));
        binding.signOutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            GlobalFunction.updateUI(Profile.this, Auth.class);
            finish();
        });

        binding.saveBtn.setOnClickListener(view -> {
            email = Objects.requireNonNull(binding.emailEditTi.getText()).toString();
            username = Objects.requireNonNull(binding.usernameEditTi.getText()).toString();
            nik = Objects.requireNonNull(binding.nikEditTi.getText()).toString();

            if (email.isEmpty()){
                Toast.makeText(Profile.this, "Email is null", Toast.LENGTH_SHORT).show();
            }else if (username.isEmpty()){
                Toast.makeText(Profile.this, "Username is null", Toast.LENGTH_SHORT).show();
            }else if (nik.isEmpty()){
                Toast.makeText(Profile.this, "Number phone is null", Toast.LENGTH_SHORT).show();
            }else if (!GlobalFunction.validateEmail(email)){
                Toast.makeText(Profile.this, "Email format wrong", Toast.LENGTH_SHORT).show();
            }else {

                if (onImageChange){
                    saveChangeWithImage();
                }else {
                    saveChangeProfile(GlobalVariable.urlPhoto);
                }
            }

        });
    }

    private void pickImageSetup() {
        imagePick = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    binding.acountPhoto.setImageURI(result);
                    onImageChange = true;
                }
        );
    }

    private void setDataProfile(){
        if (GlobalVariable.roleCurrent.equals("head")){
            binding.headBtn.setVisibility(View.VISIBLE);
        }

        if (GlobalVariable.roleCurrent.equals("admin")){
            binding.administratorBtn.setVisibility(View.VISIBLE);
        }

        if (GlobalVariable.username != null){
            binding.usernameEditTi.setText(GlobalVariable.username);
            binding.usernameTv.setText(GlobalVariable.username);
        }

        if (!GlobalVariable.urlPhoto.equals("-")){
            Picasso.get().load(GlobalVariable.urlPhoto).into(binding.acountPhoto);
        }

        if (GlobalVariable.email != null){
            binding.emailEditTi.setText(GlobalVariable.email);
        }

        if (GlobalVariable.nik != null){
            binding.nikEditTi.setText(GlobalVariable.nik);
        }

    }

    private void saveChangeWithImage() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = dateFormat.format(now);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("photo_profile/"+fileName);

        Bitmap bitmap = ((BitmapDrawable) binding.acountPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baOs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baOs);
        byte[] data = baOs.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);

        if (!GlobalVariable.urlPhoto.equals("-")){
            FirebaseStorage referenceStorage = FirebaseStorage.getInstance();
            String name_photo = referenceStorage.getReferenceFromUrl(GlobalVariable.urlPhoto).getName();
            StorageReference deleteRef = referenceStorage.getReference("photo_profile/"+name_photo);
            deleteRef.delete();

        }

        uploadTask.addOnFailureListener(e -> Toast.makeText(Profile.this, "Upload : "+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveChangeProfile(String.valueOf(uri)))
                        .addOnFailureListener(e -> Toast.makeText(Profile.this, "Url Download :"+e.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    private void saveChangeProfile(String urlPhoto){
        ModelUser modelUser = new ModelUser(
                username,
                nik,
                GlobalVariable.roleCurrent,
                email,
                urlPhoto
        );

        GlobalVariable.reference.child("users").child(GlobalVariable.uidCurrent).setValue(modelUser)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(Profile.this, "Your data is change", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        GlobalVariable.urlPhoto = urlPhoto;
        onImageChange = false;
    }
}