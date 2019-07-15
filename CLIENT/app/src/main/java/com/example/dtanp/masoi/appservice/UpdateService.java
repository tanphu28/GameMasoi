package com.example.dtanp.masoi.appservice;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.example.dtanp.masoi.MainActivity;
import com.example.dtanp.masoi.R;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class UpdateService extends Service {

    private Socket socket;
    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            socket = IO.socket("http://192.168.1.9:3000");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on("updateversion", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String version;
                try {
                    PackageInfo pInfo =getApplication().getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = pInfo.versionName;
                    String ver = (String) args[0];
                    if(!version.equals(ver)){
                        showNotification();
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void showNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);
        builder.setContentIntent(contentIntent);
        builder.setContentText("you have new version game!");
        builder.setContentTitle("Update game MaSoi");
        builder.setSmallIcon(R.drawable.back);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);

        Notification notification = builder.build();
        nm.notify((int)System.currentTimeMillis(),notification);
    }
}
