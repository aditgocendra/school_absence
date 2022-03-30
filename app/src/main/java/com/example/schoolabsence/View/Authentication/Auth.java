package com.example.schoolabsence.View.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.databinding.ActivityAuthBinding;

public class Auth extends AppCompatActivity {

    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();
    }

    private void listenerComponent() {
        binding.signUpBtn.setOnClickListener(view -> GlobalFunction.updateUI(Auth.this, SignUp.class));
        binding.signInBtn.setOnClickListener(view -> GlobalFunction.updateUI(Auth.this, SignIn.class));
    }
}