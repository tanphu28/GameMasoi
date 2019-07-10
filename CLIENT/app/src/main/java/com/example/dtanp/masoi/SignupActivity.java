package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.dtanp.masoi.appinterface.SignupView;
import com.example.dtanp.masoi.model.UserStore;
import com.example.dtanp.masoi.presenter.SignupPresenter;
import com.example.dtanp.masoi.utils.CommonFunction;

import io.fabric.sdk.android.Fabric;

public class SignupActivity extends Activity implements SignupView {

    private static final boolean AUTO_HIDE = true;


    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;


    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    EditText edtuser,edtpass,edtpass2;
    Button btnsignup;
    boolean isOpened = false,isOK = false;
    AlertDialog dialog;
    private SignupPresenter signupPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_signup);
        signupPresenter = new SignupPresenter(SignupActivity.this,SignupActivity.this);
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);



        AddConTrols();
        AddEvents();
        edtpass2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();
            }
        });

        edtuser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();
            }
        });
        edtpass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();
            }
        });
        signupPresenter.listenRegister();
        AddDialog();

    }
    private TextView textCheck;
    public  void  AddDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_nickname,null);
        builder.setView(view);
        Button btnCheck = view.findViewById(R.id.btnCheck);
        Button btnOK = view . findViewById(R.id.btnApply);
        final EditText edtNickname = view.findViewById(R.id.nickName);
        textCheck = view.findViewById(R.id.textCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtNickname.getText().toString().trim().equals(""))
                {
                    signupPresenter.emitCheckUser(edtNickname.getText().toString().trim());
                }
                else
                {
                    Toast.makeText(SignupActivity.this,"nick name is not empty !",Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOK && !edtNickname.getText().toString().trim().equals(""))
                {
                    signupPresenter.emitRegistNickname(edtuser.getText().toString().trim(),edtNickname.getText().toString().trim());
                }
                else
                {
                    Toast.makeText(SignupActivity.this,"Unsuccessful!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        signupPresenter.listenCheckUser();
        signupPresenter.listenRegistnickname();
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
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
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String userId = edtuser.getText().toString();
                 String pass= edtpass.getText().toString();
                 String passAgain = edtpass2.getText().toString();
                if(userId.trim().equals(""))
                {
                    Toast.makeText(SignupActivity.this,"Username is not empty !",Toast.LENGTH_SHORT).show();
                }
                else if(pass.trim().equals(""))
                {
                    Toast.makeText(SignupActivity.this,"PassWord is not empty !",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!pass.trim().equals(passAgain.trim())){
                        Toast.makeText(SignupActivity.this,"Two different passwords",Toast.LENGTH_SHORT).show();
                    }else{
                        pass = CommonFunction.getMD5(pass);
                        UserStore userStore = new UserStore(userId,pass);
                       signupPresenter.emitRegister(userStore);
                    }
                }
                hide();
            }
        });
    }
    public void addDialogNoInternet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.conectServer);
        builder.setCancelable(false);
        builder.create().show();
    }
    private void AddConTrols() {
        edtuser= findViewById(R.id.edtuser);
        edtpass2 = findViewById(R.id.edtpass2);
        edtpass = findViewById(R.id.edtpass);
        btnsignup = findViewById(R.id.btnsignup);
    }

    public void startmh()
    {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void checkUser(boolean flag) {
        if(flag == true)
        {
            textCheck.setText("Nickname is use");
            isOK = true;
        }
        else {
            textCheck.setText("Nickname is Exists");
        }
    }

    @Override
    public void loginSuccess() {
        dialog.cancel();
        startmh();
        finish();
    }

    @Override
    public void register(boolean flag) {
        if (flag == true){
            dialog.show();
        }else {
            Toast.makeText(SignupActivity.this,"User is exists",Toast.LENGTH_SHORT).show();
        }
    }
}
