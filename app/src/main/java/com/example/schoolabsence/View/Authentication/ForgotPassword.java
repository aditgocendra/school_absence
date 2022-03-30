package com.example.schoolabsence.View.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.databinding.ActivityForgotPasswordBinding;

public class ForgotPassword extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);


    }
}