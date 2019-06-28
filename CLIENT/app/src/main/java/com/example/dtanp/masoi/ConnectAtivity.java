package com.example.dtanp.masoi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dtanp.masoi.singleton.SocketSingleton;

public class ConnectAtivity extends Activity {

    private   EditText edtConnect;
    private Button btnConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_ativity);
        edtConnect = findViewById(R.id.edtConnect);
        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketSingleton.HOST = edtConnect.getText().toString().trim();
                Intent intent = new Intent(ConnectAtivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
