package com.example.schoolabsence.View.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.databinding.ActivityAdministratorMenuBinding;

public class AdministratorMenu extends AppCompatActivity {

    private ActivityAdministratorMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministratorMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GlobalFunction.checkWindowSetFlag(this);

        listenerComponent();

    }

    private void listenerComponent() {
        binding.cardCompareAlgorithm.setOnClickListener(view -> GlobalFunction.updateUI(AdministratorMenu.this, CompareDistance.class));
    }
}