package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserStore;
import com.example.dtanp.masoi.utils.MD5Util;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.Socket;

import io.fabric.sdk.android.Fabric;

public class SignupActivity extends Activity {

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
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    boolean isOpened = false,isOK = false;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_signup);
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        auth = StaticFirebase.auth;
        database = StaticFirebase.database;
        reference = database.getReference();


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
        LangNgheRegister();
        AddDialog();

    }

    public  void  AddDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_nickname,null);
        builder.setView(view);
        Button btnCheck = view.findViewById(R.id.btnCheck);
        Button btnOK = view . findViewById(R.id.btnApply);
        final EditText edtNickname = view.findViewById(R.id.nickName);
        final TextView textCheck = view.findViewById(R.id.textCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtNickname.getText().toString().trim().equals(""))
                {
                    StaticUser.socket.emit("CheckUser",edtNickname.getText().toString().trim());
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
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("userId", edtuser.getText().toString().trim());
                    jsonObject.addProperty("name", edtNickname.getText().toString().trim());
                    String json = StaticUser.gson.toJson(jsonObject);
                    StaticUser.socket.emit("Registnickname",json);
                }
                else
                {
                    Toast.makeText(SignupActivity.this,"Unsuccessful!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                SignupActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if((boolean)args[0]==true)
                        {
                            textCheck.setText("Nickname is use");
                            isOK = true;
                        }
                        else {
                            textCheck.setText("Nickname is Exists");
                        }
                    }
                });
            }
        };
        StaticUser.socket.on("CheckUser",listener);

        Emitter.Listener listenerLogin = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
               SignupActivity.this.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       JSONObject jsonObject = (JSONObject) args[0];
                       StaticUser.user = StaticUser.gson.fromJson(jsonObject.toString(),User.class);
                       dialog.cancel();
                       startmh();
                       finish();
                   }
               });
            }
        };

        StaticUser.socket.on("Registnickname",listenerLogin);

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

    public  void LangNgheRegister(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                SignupActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if((boolean)args[0]==true)
                        {
                            dialog.show();
                        }
                        else
                        {
                            Toast.makeText(SignupActivity.this,"User is exists",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };

        StaticUser.socket.on("register_user",listener);
    }


    private void AddEvents() {
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String userId = edtuser.getText().toString();
                 String pass= edtpass.getText().toString();
                 String passAgain = edtpass2.getText().toString();
//                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful())
//                        {
//                            UserStore us= new UserStore(task.getResult().getUser().getUid().toString(),pass);
//
//                            //reference.child("User").child(StaticUser.user.getUserId()).setValue(StaticUser.user);
//                            String jsonUser =  StaticUser.gson.toJson(us);
//                            StaticUser.socket.emit("register_user",jsonUser);
//                            startmh();
//                            finish();
//                        }
//                        else
//                        {
//                            System.out.println("Khong thanh cong");
//                        }
//                    }
//                });
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
                        pass = MD5Util.getMD5(pass);
                        UserStore userStore = new UserStore(userId,pass);
                        String json =  StaticUser.gson.toJson(userStore);
                        StaticUser.socket.emit("register_user",json);
                    }
                }
                hide();
            }
        });
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
}
