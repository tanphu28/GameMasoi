package com.example.dtanp.masoi.control;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class StaticFirebase {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final FirebaseAuth auth = FirebaseAuth.getInstance();

}
