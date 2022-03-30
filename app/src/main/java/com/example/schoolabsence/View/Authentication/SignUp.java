package com.example.schoolabsence.View.Authentication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.schoolabsence.Model.ModelUser;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();

    }

    private void listenerComponent() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.signUpBtn.setOnClickListener(view -> {
            String username = binding.usernameSignUp.getText().toString();
            String nik = binding.nikSignUp.getText().toString();
            String email = binding.emailSignUp.getText().toString();
            String pass = binding.passSignUp.getText().toString();
            String re_pass = binding.rePassSignUp.getText().toString();

            if (username.isEmpty()){
                Toast.makeText(this, "Username is empty", Toast.LENGTH_SHORT).show();
            }else if (nik.isEmpty()){
                Toast.makeText(this, "Identification number is empty", Toast.LENGTH_SHORT).show();
            }else if (email.isEmpty()){
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            }else if (pass.isEmpty()){
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            }else if (re_pass.isEmpty()){
                Toast.makeText(this, "Confirmation password is empty", Toast.LENGTH_SHORT).show();
            }else if (!GlobalFunction.validateEmail(email)){
                Toast.makeText(this, "Email is not valid format", Toast.LENGTH_SHORT).show();
            }else {
                if (!pass.equals(re_pass)){
                    Toast.makeText(this, "Password not same with Confirmation Password", Toast.LENGTH_SHORT).show();
                }else {
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    binding.signUpBtn.setEnabled(false);
                    signUp(email, pass);
                }
            }
        });
    }

    private void signUp(String email, String pass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
            binding.progressCircular.setVisibility(View.GONE);
            binding.signUpBtn.setEnabled(true);
            FirebaseUser user = authResult.getUser();
            saveDataUser(user);
        }).addOnFailureListener(e -> {
            binding.progressCircular.setVisibility(View.GONE);
            binding.signUpBtn.setEnabled(true);
            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void saveDataUser(FirebaseUser user){
        String username = binding.usernameSignUp.getText().toString();
        String nik = binding.nikSignUp.getText().toString();
        String email = binding.emailSignUp.getText().toString();
        String pass = binding.passSignUp.getText().toString();

        ModelUser modelUser = new ModelUser(
                username,
                nik,
                "teacher",
                email,
                "-"
        );

        GlobalVariable.reference.child("users").child(user.getUid()).setValue(modelUser).addOnSuccessListener(unused -> {
            Toast.makeText(SignUp.this, "Sign Up Success, Please Login", Toast.LENGTH_SHORT).show();
            binding.progressCircular.setVisibility(View.GONE);
            binding.signUpBtn.setEnabled(true);
            GlobalFunction.updateUI(SignUp.this, SignIn.class);
            finish();
        }).addOnFailureListener(e ->{
            binding.progressCircular.setVisibility(View.GONE);
            binding.signUpBtn.setEnabled(true);
            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}