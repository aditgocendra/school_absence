package com.example.schoolabsence;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import com.example.schoolabsence.Utility.GlobalFunction;
import com.example.schoolabsence.Utility.GlobalVariable;
import com.example.schoolabsence.View.Authentication.Auth;
import com.example.schoolabsence.View.HomeApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalFunction.checkWindowSetFlag(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            GlobalVariable.uidCurrent = user.getUid();
            Handler handler = new Handler();
            handler.postDelayed(() -> GlobalFunction.updateUI(MainActivity.this, HomeApp.class), 300);
        }else {
            GlobalFunction.updateUI(this, Auth.class);
        }
    }
}