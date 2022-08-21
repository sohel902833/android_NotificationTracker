package com.example.notificationtracker.Lib;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApiRef {
    public static final DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users");
}
