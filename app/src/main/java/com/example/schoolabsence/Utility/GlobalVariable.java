package com.example.schoolabsence.Utility;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// global variable
public class GlobalVariable {

    public static DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    // user account
    public static String uidCurrent = null;
    public static String roleCurrent = null;
    public static String username = null;
    public static String email = null;
    public static String nik = null;
    public static String urlPhoto = null;


}
