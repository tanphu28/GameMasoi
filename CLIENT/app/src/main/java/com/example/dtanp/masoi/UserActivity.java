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

import com.example.dtanp.masoi.appinterface.UserView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.presenter.UserPresenter;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserActivity extends Activity implements UserView {

    TextView  txtlevel,txtid  ,txtloss,txtwin,txtNickName,txtFullname,txtPhone,txtBirthday,txtEmail;
    ImageButton btnLogout,btnFeedback;
    Button btnEdit;
    AlertDialog dialog,dialogUserInfo;
    private UserPresenter userPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        userPresenter = new UserPresenter(UserActivity.this,UserActivity.this);
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
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Enviroment.METHOD_LOGIN == 2) {
                    LoginManager.getInstance().logOut();
                }
                else if (Enviroment.METHOD_LOGIN ==3){
                    auth.signOut();
                }

                Intent intent = new Intent(UserActivity.this,MainActivity.class);
                startActivity(intent);
                finishAffinity();
                userPresenter.emitLogout();

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
        txtid.setText(Enviroment.user.getUserId());
        txtNickName.setText(Enviroment.user.getName());
        txtFullname.setText(Enviroment.user.getFullname());
        txtEmail.setText(Enviroment.user.getEmail());
        txtBirthday.setText(Enviroment.user.getBirthday());
        txtPhone.setText(Enviroment.user.getPhone_number());
        txtlevel.setText(Enviroment.user.getLevel()+"");
        txtwin.setText(Enviroment.user.getWin()+"");
        txtloss.setText(Enviroment.user.getLose()+"");
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
                userPresenter.emitFeedBack(edtEmail.getText().toString(),edtcontent.getText().toString());
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
        edtFullName.setText(Enviroment.user.getFullname());
        edtEmail.setText(Enviroment.user.getEmail());
        edtPhoneNumber.setText(Enviroment.user.getPhone_number());
        edtBirthday.setText(Enviroment.user.getBirthday());
        edtAddress.setText(Enviroment.user.getAddress());
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
                Enviroment.user.setFullname(edtFullName.getText().toString());
                Enviroment.user.setEmail(edtEmail.getText().toString());
                Enviroment.user.setPhone_number(edtPhoneNumber.getText().toString());
                Enviroment.user.setAddress(edtAddress.getText().toString());
                Enviroment.user.setBirthday(edtBirthday.getText().toString());
                userPresenter.emitUpdateUserInfo(Enviroment.user);
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
