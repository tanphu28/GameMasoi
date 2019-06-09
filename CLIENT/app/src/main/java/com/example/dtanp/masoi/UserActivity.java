package com.example.dtanp.masoi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.User;
import com.facebook.login.LoginManager;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserActivity extends Activity {

    TextView  txtlevel,txtid  ,txtloss,txtwin,txtNickName,txtFullname,txtPhone,txtBirthday,txtEmail;
    ImageButton btnLogout,btnFeedback;
    Button btnEdit;
    AlertDialog dialog,dialogUserInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        AddConTrols();
        AddEvents();
        LoadData();
        AddDialogFeedBack();
        AddDialogUserInfo();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void AddEvents() {
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StaticUser.METHOD_LOGIN == 2) {
                    LoginManager.getInstance().logOut();
                }
                else if (StaticUser.METHOD_LOGIN ==3){
                    StaticFirebase.auth.signOut();
                }

                Intent intent = new Intent(UserActivity.this,MainActivity.class);
                startActivity(intent);
                finishAffinity();

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUserInfo.show();
            }
        });
    }

    private void AddConTrols() {
        txtid= findViewById(R.id.txtid);
        txtNickName = findViewById(R.id.txtname);
        txtFullname = findViewById(R.id.txtfullname);
        txtPhone = findViewById(R.id.txtphone);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtEmail = findViewById(R.id.txtEmail);
        txtlevel=findViewById(R.id.txtlevel);
        txtloss=findViewById(R.id.txtloss);
        txtwin=findViewById(R.id.txtwwin);
        btnEdit =findViewById(R.id.btnEdit);
        btnLogout = findViewById(R.id.btnLogout);
        btnFeedback = findViewById(R.id.btnFeedback);
    }

    private void LoadData(){
        txtid.setText(StaticUser.user.getUserId());
        txtNickName.setText(StaticUser.user.getName());
        txtFullname.setText(StaticUser.user.getFullname());
        txtEmail.setText(StaticUser.user.getEmail());
        txtBirthday.setText(StaticUser.user.getBirthday());
        txtPhone.setText(StaticUser.user.getPhone_number());
        txtlevel.setText(StaticUser.user.getLevel()+"");
        txtwin.setText(StaticUser.user.getWin()+"");
        txtloss.setText(StaticUser.user.getLose()+"");
    }

    private void AddDialogFeedBack(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_feedback,null);
        builder.setView(view);
        final EditText edtEmail = view.findViewById(R.id.edtEmail);
        final EditText edtcontent  = view.findViewById(R.id.edtContent);
        Button btnSendFeedBack = view.findViewById(R.id.btnSendFeedBack);
        btnSendFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("email",edtEmail.getText().toString());
                jsonObject.addProperty("message",edtcontent.getText().toString());
                String json = StaticUser.gson.toJson(jsonObject);
                StaticUser.socket.emit("feedback",json);
                Toast.makeText(UserActivity.this,"Thank You!",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        dialog = builder.create();
    }

    private  void  AddDialogUserInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_userinfo,null);
        builder.setView(view);
        final EditText edtFullName = view.findViewById(R.id.edtFullName);
        final EditText edtEmail = view.findViewById(R.id.edtEmail);
        final EditText edtPhoneNumber = view.findViewById(R.id.edtPhone);
        final EditText edtAddress = view.findViewById(R.id.edtaddress);
        final EditText edtBirthday = view.findViewById(R.id.edtBirthday);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(calendar.YEAR);
        final int month = calendar.get(calendar.MONTH);
        final int day = calendar.get(calendar.DAY_OF_MONTH);
        Button datePicker = view.findViewById(R.id.dateBirthday);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(UserActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            final Calendar myCalendar = Calendar.getInstance();
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String myFormat = "dd-MM-yyyy"; //In which you need put here
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                edtBirthday.setText(sdf.format(myCalendar.getTime()));

                            }
                        }, year, month, day);
                dpd.show();
            }
        });
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StaticUser.user.setFullname(edtFullName.getText().toString());
                StaticUser.user.setEmail(edtEmail.getText().toString());
                StaticUser.user.setPhone_number(edtPhoneNumber.getText().toString());
                StaticUser.user.setAddress(edtAddress.getText().toString());
                StaticUser.user.setBirthday(edtBirthday.getText().toString());
                String json = StaticUser.gson.toJson(StaticUser.user);
                StaticUser.socket.emit("updateuserinfo",json);
                LoadData();
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogUserInfo = builder.create();

    }
}
