package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.dtanp.masoi.appinterface.API;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserStore;
import com.example.dtanp.masoi.utils.MD5Util;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends FragmentActivity implements View.OnClickListener ,GoogleApiClient.OnConnectionFailedListener  {


    public static final int RC_SIGN_IN = 123;
    public static final String TAG = "abc";
    private GoogleSignInClient mGoogleSignInClient;
    private EditText edtuser, edtpassworld;
    private Button btnlogin, btnsignup;
    ImageButton btngg;
    private Emitter.Listener emitterUserLogin;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private SignInButton SignIn;
    private LinearLayout Prof_section;
    private GoogleApiClient googleApiClient;
    public static final int REC_CODE = 9001;
    private static final boolean AUTO_HIDE = true;
    private AlertDialog dialogUpdate;


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
    String version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        //configure fb
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // configure login fb
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(LoginActivity.this,"eee",Toast.LENGTH_SHORT).show();
                        AccessToken tok;
                        tok = AccessToken.getCurrentAccessToken();
                        System.out.println("aaaaa");
                        System.out.println(tok.getUserId());
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            Toast.makeText(LoginActivity.this,"aaa",Toast.LENGTH_SHORT).show();
                                            System.out.println("bbbb");
                                        } else {
                                            Toast.makeText(LoginActivity.this,"bbb",Toast.LENGTH_SHORT).show();
                                            String id = me.optString("id");
                                            String name =me.optString("name");
                                            System.out.println("cccc");
                                            Enviroment.METHOD_LOGIN = 2;
                                            //finish();
                                        }
                                    }
                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this,"ddd",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this,"ccc",Toast.LENGTH_SHORT).show();
                        System.out.println("dddd");
                    }
                });
        // Configure Google Sign In


        SignIn=(SignInButton) findViewById(R.id.btn_login);
        SignIn.setOnClickListener(this);

        GoogleSignInOptions signInOptions =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();


        try {
            Enviroment.socket = IO.socket("http://172.16.6.183:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Enviroment.socket.connect();
        Enviroment.gson=new Gson();
        createDialogUpdate();
        LangNgheVersionName();
        AddConTrols();
        AddEvents();

        try {
            PackageInfo pInfo = LoginActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            Enviroment.socket.emit("CheckVersionName",1);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public  void LangNgheVersionName(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String ver = (String) args[0];
                        if(!version.equals(ver)){
                            dialogUpdate.show();
                        }
                    }
                });

            }
        };
        Enviroment.socket.on("CheckVersionName",listener);
    }

    public void  createDialogUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Update Version Android");
        builder.setMessage("You must update version app!");
        builder.setNegativeButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadfile();
            }
        });
        dialogUpdate = builder.create();
    }

    public  void downloadfile(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.9:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API downloadService = retrofit.create(API.class);

        Call<ResponseBody> call = downloadService.downloadApk("http://192.168.1.9:3000/apk");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //Log.d(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                    Toast.makeText(LoginActivity.this,"Download Successfully",Toast.LENGTH_SHORT).show();
                    if(writtenToDisk){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/app.apk")), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    //Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    //Log.d(TAG, "server contact failed");
                    Toast.makeText(LoginActivity.this,"Fail!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Log.e(TAG, "error");
            }
        });
    }

    public boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "app.apk");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("CHIM", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                Log.d("CHIMERR",e.getMessage().toString());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.d("CHIMERR",e.getMessage().toString());
            return false;
        }
    }


    private void AddEvents() {
        btnlogin.setOnClickListener(this);

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
                            Enviroment.user = Enviroment.gson.fromJson(jsonObject.toString(),User.class);
                            startmh();
                            finish();
                        }

                    }
                });


            }
        };
        Enviroment.socket.on("userlogin",emitterUserLogin);
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
////                                    Enviroment.user = us;
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
//                            Enviroment.socket.emit("finduserlogin",task.getResult().getUser().getUid());
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
            String json = Enviroment.gson.toJson(userStore);
            Enviroment.socket.emit("login",json);
            Enviroment.METHOD_LOGIN = 1;
        }
    }

    public void startmh() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
