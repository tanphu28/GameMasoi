package com.example.dtanp.masoi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends Activity {

    EditText edtuser,edtemail,edtpass,edtbirthday;
    Button btnsignup;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        auth = StaticFirebase.auth;
        database = StaticFirebase.database;
        reference = database.getReference();


        AddConTrols();
        AddEvents();
    }

    private void AddEvents() {
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = edtemail.getText().toString();
                String pass= edtpass.getText().toString();
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            User us= new User(task.getResult().getUser().getUid().toString(),edtuser.getText().toString(),email,"");
                            StaticUser.user=us;
                            //reference.child("User").child(StaticUser.user.getId()).setValue(StaticUser.user);
                            String jsonUser =  StaticUser.gson.toJson(us);
                            StaticUser.socket.emit("register_user",jsonUser);
                            startmh();
                            finish();
                        }
                        else
                        {
                            System.out.println("Khong thanh cong");
                        }
                    }
                });
            }
        });
    }

    private void AddConTrols() {
        edtuser= findViewById(R.id.edtuser);
        edtbirthday=findViewById(R.id.edtbirthday);
        edtemail = findViewById(R.id.edtemail);
        edtpass = findViewById(R.id.edtpass);
        btnsignup = findViewById(R.id.btnsignup);
    }

    public void startmh()
    {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
