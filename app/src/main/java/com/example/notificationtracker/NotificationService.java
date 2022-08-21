package com.example.notificationtracker;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.ByteArrayOutputStream;

public class NotificationService extends NotificationListenerService {

    Context context;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        String title = "";
        title=extras.getString("android.title");
        String text = "";
        try{text=extras.getCharSequence("android.text").toString();}catch (Exception e){}
        int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap id = sbn.getNotification().largeIcon;

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        msgrcv.putExtra("applicationName", getApplicationName(pack));
        if(id != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            msgrcv.putExtra("icon",byteArray);
        }


            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);



    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }

    private String getApplicationName(String pack){
         PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( pack, 0);
        } catch ( PackageManager.NameNotFoundException e) {
            ai = null;
        }
         String applicationName = (String) (ai != null ?
                pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }
}