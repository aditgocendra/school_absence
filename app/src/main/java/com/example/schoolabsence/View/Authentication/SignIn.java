package com.example.schoolabsence.View.Authentication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.View.HomeApp;
import com.example.schoolabsence.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();
    }

    private void listenerComponent() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.signInBtn.setOnClickListener(view -> {
            String email = binding.emailSignIn.getText().toString();
            String pass = binding.passSignIn.getText().toString();

            if (email.isEmpty()){
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            }else if (pass.isEmpty()){
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            }else if (!GlobalFunction.validateEmail(email)){
                Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
            }else {
                signIn(email, pass);
            }
        });
    }

    private void signIn(String email, String pass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    assert user != null;
                    GlobalVariable.uidCurrent = user.getUid();
                    GlobalFunction.updateUI(SignIn.this, HomeApp.class);
                        })
                .addOnFailureListener(e -> Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}