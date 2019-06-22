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
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.dtanp.masoi.appinterface.LoginView;
import com.example.dtanp.masoi.appinterface.API;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.presenter.LoginPresenter;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, LoginView {
    private LinearLayout Prof_section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView Name,Email;
    private ImageView Prof_pic;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private LoginPresenter loginPresenter;

    public static final int REC_CODE = 9001;

    public static final int RC_SIGN_IN = 123;
    public static final String TAG = "abc";
    private EditText edtuser, edtpassworld;
    private Button btnlogin, btnsignup;
    private ImageButton btngg;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Emitter.Listener emitterUserLogin;
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
    boolean isOK = false;
    AlertDialog dialog;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        loginPresenter = new LoginPresenter(MainActivity.this,MainActivity.this);
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        SignIn=findViewById(R.id.btn_login);
        SignIn.setOnClickListener(this);

        //fb login
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

                        AccessToken tok;
                        tok = AccessToken.getCurrentAccessToken();
                        System.out.println("aaaa");
                        System.out.println(tok.toString());
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {
                                        if (response.getError() != null) {
                                            Toast.makeText(MainActivity.this,"Login Fail!",Toast.LENGTH_SHORT).show();
                                        } else {
                                            userId = me.optString("id");
                                            String name =me.optString("name");
                                            loginPresenter.emitLoginFB(userId,name);
                                            Toast.makeText(MainActivity.this,"Login Success!",Toast.LENGTH_SHORT).show();
                                            Enviroment.METHOD_LOGIN = 2;
                                        }
                                    }
                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        System.out.println("eeee");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("ffff");
                    }
                });
        //google
        GoogleSignInOptions signInOptions =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleSignInClient= GoogleSignIn.getClient(this, signInOptions);
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(
                Auth.GOOGLE_SIGN_IN_API,signInOptions
        ).build();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        try {
            Enviroment.socket = IO.socket("http://192.168.1.6:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Enviroment.socket.connect();
        Enviroment.gson=new Gson();
        createDialogUpdate();
        try {
            PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        loginPresenter.listenVersionName(version);
        AddConTrols();
        AddEvents();
        AddDialog();
        loginPresenter.listenLogin();
        loginPresenter.listenRegister();
        loginPresenter.emitCheckVersionName();

    }
    private static TextView textCheck;
    public  void  AddDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                    loginPresenter.emitCheckUser(edtNickname.getText().toString().trim());
                }
                else
                {
                    Toast.makeText(MainActivity.this,"nick name is not empty !",Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOK && !edtNickname.getText().toString().trim().equals(""))
                {
                    loginPresenter.registNicknameLoginFb(userId,edtNickname.getText().toString().trim());
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Unsuccessful!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginPresenter.listenCheckUser();
        loginPresenter.listenRegistNickname();

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REC_CODE);
        googleApiClient.connect();
    }
    private void updateUI(boolean isLogin){
        if(isLogin){
            Prof_section.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.GONE);
        }else
        {
            Prof_section.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REC_CODE){
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Toast.makeText(MainActivity.this,result.getStatus().toString()+"",Toast.LENGTH_SHORT).show();
            handleSignInResult(result);

        }


    }
    private void handleSignInResult(GoogleSignInResult result) {
        Toast.makeText(MainActivity.this,result.isSuccess()+"",Toast.LENGTH_SHORT).show();
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            String name =account.getDisplayName();
            String email=account.getEmail();
            userId =email;
            loginPresenter.emitLoginFB(email,name);
            Toast.makeText(MainActivity.this,"Login Success!",Toast.LENGTH_SHORT).show();
        }else{
            updateUI(false);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_login:
                signIn();
                Enviroment.METHOD_LOGIN = 3;
                break;

        }
        int id = v.getId();
        if (id == R.id.btnlogin) {
            String username = edtuser.getText().toString().trim();
            String pass = edtpassworld.getText().toString().trim();
            loginPresenter.login(username,pass);
        }
    }


    public void  createDialogUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                    Toast.makeText(MainActivity.this,"Download Successfully",Toast.LENGTH_SHORT).show();
                    if(writtenToDisk){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/app.apk")), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(MainActivity.this,"Fail!",Toast.LENGTH_SHORT).show();
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
        loginPresenter.listenUserLogin();
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

    @Override
    public void showDialogRegister() {
        dialog.show();
    }

    @Override
    public void loginSuccess() {
        startmh();
        finish();
    }

    @Override
    public void checkUser(boolean flag) {
        if(flag==true)
        {
            textCheck.setText("Nickname is use");
            isOK = true;
        }
        else {
            textCheck.setText("Nickname is Exists");
        }
    }

    @Override
    public void registNicknameSuccess() {
        dialog.cancel();
        startmh();
        finish();
    }

    @Override
    public void showDialogUpdate() {
        dialogUpdate.show();
    }

    @Override
    public void userLoginSuccess(boolean flag) {
        if(flag==false)
        {
            Toast.makeText(MainActivity.this,"Username or PassWord incorrect",Toast.LENGTH_SHORT).show();
        }
        else {
            startmh();
            finish();
        }
    }
}