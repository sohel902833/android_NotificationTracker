package com.example.notificationtracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import javax.mail.Message;
import  javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationtracker.Adapters.NotificationListAdapter;
import com.example.notificationtracker.Db.MyDatabaseHelper;
import com.example.notificationtracker.Db.UserDb;
import com.example.notificationtracker.Lib.ApiRef;
import com.example.notificationtracker.Lib.AppBar;
import com.example.notificationtracker.Lib.DateHelper;
import com.example.notificationtracker.Lib.JavaMailApi;
import com.example.notificationtracker.Model.NotificationModel;
import com.example.notificationtracker.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<NotificationModel> modelList;
    private NotificationListAdapter notificationListAdapter;
    private RecyclerView recyclerView;
    MyDatabaseHelper myDatabaseHelper;
    private UserDb userDb;

    //
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();


        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
    }

    private void init(){
        userDb=new UserDb(this);
        mAuth=FirebaseAuth.getInstance();
        modelList = new ArrayList<>();
        myDatabaseHelper=new MyDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase=myDatabaseHelper.getWritableDatabase();


        AppBar appBar=new AppBar(this);
        appBar.init(findViewById(R.id.appbarId),"Notification Sender");
        appBar.hideBackButton();
        setSupportActionBar(findViewById(R.id.appbarId));
        this.setTitle("");

        recyclerView=findViewById(R.id.notificationListRecyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                 startActivity(new Intent(MainActivity.this,SettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            String packageName = intent.getStringExtra("package");
            String ticker = intent.getStringExtra("ticker");
            String applicationName = intent.getStringExtra("applicationName");
            //int id = intent.getIntExtra("icon",0);

            Context remotePackageContext = null;
            try {
                byte[] byteArray =intent.getByteArrayExtra("icon");
                Bitmap bmp = null;
                if(byteArray !=null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }

                String currentDate= DateHelper.getTodayDate()+" At "+DateHelper.getCurrentTime();

                NotificationModel model = new NotificationModel(text,bmp,packageName,ticker,title,applicationName,currentDate);

                User user=userDb.getUserData();
                if(user.getStatus().equals(User.ACTIVE_STATUS)){
                    myDatabaseHelper.addNewNotification(model);
                    loadLocalDbData();
                }
                if(user.getSendNotification().equals(User.SEND_NOTIFICATION) && user.getStatus().equals(User.ACTIVE_STATUS)){
                    if(model.getPackageName().equals("com.google.android.apps.messaging")){
                        if(user.getSendMessageAppNotification().equals(User.SEND_NOTIFICATION)){
                            sentEmailV2(model);
                        }
                    }else{
                        sentEmailV2(model);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        User user=userDb.getUserData();
        if(user!=null){
            checkPermission();
            if(user.isAdmin()){
                Intent intent=new Intent(MainActivity.this, AdminMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }else if(mAuth.getCurrentUser()==null){
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        loadLocalDbData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getUserData();
            }
        });

    }


    private void loadLocalDbData(){
        modelList=myDatabaseHelper.getAllNotification();
        Collections.reverse(modelList);

        notificationListAdapter=new NotificationListAdapter(this,modelList);
        recyclerView.setAdapter(notificationListAdapter);
    }
    private void getUserData() {

        User user=userDb.getUserData();
        ApiRef.userRef.child(user.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                           User user = snapshot.getValue(User.class);
                            userDb.setUserData(user);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                     }
                });


    }
    private void checkPermission(){
        Dexter.withContext(MainActivity.this)
                .withPermissions(
                        Manifest.permission.RECEIVE_SMS
                        ,Manifest.permission.READ_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if(multiplePermissionsReport.areAllPermissionsGranted()){
                        }else{
                            checkPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }
   public void sentEmailV2(NotificationModel notificationModel){
            String mEmail = userDb.getUserData().getNotificationEmail();
            String mSubject = notificationModel.getApplicationName();
            String mMessage ="Application Name: "+notificationModel.getApplicationName()+"\n"+
                    "Title: "+notificationModel.getTitle()+"\n\n"+
                    "Body: "+notificationModel.getBody()+"\n\n"+
                    "Time: "+notificationModel.getDate()+"\n\n";;


            JavaMailApi javaMailAPI = new JavaMailApi(this, mEmail, mSubject, mMessage);

            javaMailAPI.execute();
    }
}
