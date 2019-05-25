package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserStore;
import com.example.dtanp.masoi.utils.MD5Util;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class LoginActivity extends Activity implements View.OnClickListener {


    public static final int RC_SIGN_IN = 123;
    public static final String TAG = "abc";
    private GoogleSignInClient mGoogleSignInClient;
    private EditText edtuser, edtpassworld;
    private Button btnlogin, btnsignup;
    ImageButton btngg;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Emitter.Listener emitterUserLogin;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        database = StaticFirebase.database;
        auth = StaticFirebase.auth;
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        try {
            StaticUser.socket = IO.socket("http://192.168.1.9:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        StaticUser.socket.connect();
        StaticUser.gson=new Gson();

        AddConTrols();
        AddEvents();
    }


    private void AddEvents() {
        btnlogin.setOnClickListener(this);
        btngg.setOnClickListener(this);
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startmhsignup();
                hide();
            }
        });
        emitterUserLogin=new Emitter.Listener(){

            @Override
            public void call(final Object... args) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(args[0]==null)
                        {
                            Toast.makeText(LoginActivity.this,"Username or PassWord incorrect",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            JSONObject jsonObject = (JSONObject) args[0];
                            StaticUser.user = StaticUser.gson.fromJson(jsonObject.toString(),User.class);
                            startmh();
                            finish();
                        }

                    }
                });


            }
        };
        StaticUser.socket.on("userlogin",emitterUserLogin);
        edtuser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();
            }
        });
        edtpassworld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();
            }
        });
    }

    private void AddConTrols() {
        btnsignup = findViewById(R.id.btnsignup);
        edtuser = findViewById(R.id.edtuser);
        edtpassworld = findViewById(R.id.edtpass);
        btnlogin = findViewById(R.id.btnlogin);
        btnsignup = findViewById(R.id.btnsignup);
        btngg = findViewById(R.id.btngg);
    }

    public void startmhsignup()
    {
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnlogin) {

//            final DatabaseReference reference = database.getReference();
//            if (edtuser.getText().toString() != "" && edtpassworld.getText().toString() != "") {
//                String user = edtuser.getText().toString();
//                String pass = edtpassworld.getText().toString();
//                auth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            //System.out.println(task.getResult().getUser().getUid().toString());
////                            reference.child("User").child(task.getResult().getUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
////                                @Override
////                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                    User us = dataSnapshot.getValue(User.class);
////                                    StaticUser.user = us;
////                                    Toast.makeText(LoginActivity.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
////                                    startmh();
////                                    finish();
////
////
////                                }
////
////                                @Override
////                                public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                }
////                            });
//                            //Toast.makeText(LoginActivity.this,"Đăng nhập !",Toast.LENGTH_SHORT).show();
//                            StaticUser.socket.emit("finduserlogin",task.getResult().getUser().getUid());
//                        } else {
//                            Toast.makeText(LoginActivity.this,"Đăng nhập thất bại",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }

            String username = edtuser.getText().toString().trim();
            String pass = edtpassworld.getText().toString().trim();
            pass = MD5Util.getMD5(pass);
            UserStore userStore = new UserStore(username,pass);
            String json = StaticUser.gson.toJson(userStore);
            StaticUser.socket.emit("login",json);
        } else if (id == R.id.btngg) {
            signIn();
        }
    }

    public void startmh() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        System.out.println("toi 3");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
        System.out.println("toi 4");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("toi 5");

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            System.out.println("tao toi ne");
            GoogleSignInAccount account = task.getResult(ApiException.class);
            System.out.println("toi 1");
            firebaseAuthWithGoogle(account);
            System.out.println("toi 2");
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
            // ...
            System.out.println("toi tao");
        }

    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        System.out.println("toi ne");
        System.out.println(acct.getId());
        System.out.println(acct.getDisplayName());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in UserActivity's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            System.out.println(task.getResult().getUser().getEmail());
                            Toast.makeText(LoginActivity.this, "thanh cong", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the UserActivity.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(LoginActivity.this, "that bai", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
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
