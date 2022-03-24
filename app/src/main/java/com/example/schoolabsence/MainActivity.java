package com.example.schoolabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.View.Admin.AdministratorMenu;
import com.example.schoolabsence.View.Admin.CompareDistance;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalFunction.checkWindowSetFlag(this);

        GlobalFunction.updateUI(this, AdministratorMenu.class);
    }
}