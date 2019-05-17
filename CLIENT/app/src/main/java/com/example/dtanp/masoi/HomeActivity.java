package com.example.dtanp.masoi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class HomeActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AddConTrols();
        AddEvents();
    }

    private void AddEvents() {
        findViewById(R.id.btnchonban).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startmhroom();
                finish();
            }
        });
    }

    private void AddConTrols() {
        setContentView(R.layout.activity_home);
    }


    public void startmhroom()
    {
        Intent intent = new Intent(this,RoomActivity.class);
        startActivity(intent);
    }
}
