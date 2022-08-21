package com.example.notificationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationtracker.Db.UserDb;
import com.example.notificationtracker.Lib.ApiRef;
import com.example.notificationtracker.Lib.AppBar;
import com.example.notificationtracker.Lib.CustomDialog;
import com.example.notificationtracker.Lib.CustomDialogClickListner;
import com.example.notificationtracker.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {


    private UserDb userDb;
    private FirebaseAuth mAuth;
    private TextView emailTv,statusTv,sendNotificationStatusTv,sendMessageAppNotificationStatusTv;
    private EditText emailEt;
    private Button turnOnNotificationListnerBtn,logoutButton,updateEmailButton;
    private  Button turnOnNotificationBtn,turnOffNotificationBtn;
    private  Button turnOnMessageAppNotificationBtn,turnOffMessageAppNotificationBtn;
    private ProgressDialog progressDialog;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();


        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailEt.getText().toString();
                if(email.isEmpty()){
                    emailEt.setError("Enter Email Address.");
                    emailEt.requestFocus();
                }else{
                    progressDialog.setMessage("Updating Email.");
                    progressDialog.show();
                    HashMap<String,Object> updateMap=new HashMap<>();
                    updateMap.put("notificationEmail",email);
                    User user=userDb.getUserData();

                    ApiRef.userRef.child(user.getUserId())
                            .updateChildren(updateMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        user.setNotificationEmail(email);
                                        userDb.setUserData(user);
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Email Updated.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show("Logout?");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                        mAuth.signOut();
                        userDb.logoutUser(SettingActivity.this);
                    }

                    @Override
                    public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });

            }
        });

        turnOnNotificationListnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if(!isNotificationServiceRunning()){
                        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                }
            }
        });

        turnOffNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tooggleNotificationSendStatus(User.DONT_SEND_NOTIFICATION);
            }
        });
        turnOnNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tooggleNotificationSendStatus(User.SEND_NOTIFICATION);
            }
        });
        turnOffMessageAppNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSendMessageAppNotificationStatus(User.DONT_SEND_NOTIFICATION);
            }
        });
        turnOnMessageAppNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSendMessageAppNotificationStatus(User.SEND_NOTIFICATION);
            }
        });

    }

    private void tooggleNotificationSendStatus(String status) {
        progressDialog.setMessage("Turning "+ status);
        progressDialog.show();
        User user=userDb.getUserData();

        HashMap<String,Object> updateMap=new HashMap<>();
        updateMap.put("sendNotification",status);

        ApiRef.userRef.child(user.getUserId())
                .updateChildren(updateMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(SettingActivity.this, "Notification Switched To "+status, Toast.LENGTH_SHORT).show();
                            user.setSendNotification(status);
                            userDb.setUserData(user);
                            sendNotificationStatusTv.setText(""+status);

                            if(status.equals(User.SEND_NOTIFICATION)){
                                turnOnNotificationBtn.setVisibility(View.GONE);
                                turnOffNotificationBtn.setVisibility(View.VISIBLE);
                            }else{
                                turnOnNotificationBtn.setVisibility(View.VISIBLE);
                                turnOffNotificationBtn.setVisibility(View.GONE);
                            }

                        }else{
                            Toast.makeText(SettingActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void toggleSendMessageAppNotificationStatus(String status) {
        progressDialog.setMessage("Turning "+ status);
        progressDialog.show();
        User user=userDb.getUserData();

        HashMap<String,Object> updateMap=new HashMap<>();
        updateMap.put("sendMessageAppNotification",status);

        ApiRef.userRef.child(user.getUserId())
                .updateChildren(updateMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(SettingActivity.this, "Message Notification Switched To "+status, Toast.LENGTH_SHORT).show();
                            user.setSendMessageAppNotification(status);
                            userDb.setUserData(user);
                            sendMessageAppNotificationStatusTv.setText(""+status);

                            if(status.equals(User.SEND_NOTIFICATION)){
                                turnOnMessageAppNotificationBtn.setVisibility(View.GONE);
                                turnOffMessageAppNotificationBtn.setVisibility(View.VISIBLE);
                            }else{
                                turnOnMessageAppNotificationBtn.setVisibility(View.VISIBLE);
                                turnOffMessageAppNotificationBtn.setVisibility(View.GONE);
                            }

                        }else{
                            Toast.makeText(SettingActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void init(){
        Toolbar toolbar=findViewById(R.id.appbarId);
        AppBar appBar=new AppBar(this);
        appBar.init(toolbar,"Settings");

        customDialog=new CustomDialog(this);
        progressDialog=new ProgressDialog(this);
        userDb=new UserDb(this);
        mAuth=FirebaseAuth.getInstance();
        emailTv=findViewById(R.id.setting_emailTv);
        statusTv=findViewById(R.id.setting_statusTv);
        emailEt=findViewById(R.id.setting_emailEt);
        turnOnNotificationListnerBtn=findViewById(R.id.setting_turnOnNotificationListenerBtn);
        logoutButton=findViewById(R.id.setting_logoutBtn);
        updateEmailButton=findViewById(R.id.setting_updateNotificationEmailBtn);
        sendNotificationStatusTv=findViewById(R.id.setting_sendNotificationStatusTv);
        turnOnNotificationBtn=findViewById(R.id.setting_turnOnNotificationButton);
        turnOffNotificationBtn=findViewById(R.id.setting_turnOffNotificationButton);

        sendMessageAppNotificationStatusTv=findViewById(R.id.setting_sendMessageAppNotificationStatusTv);
        turnOffMessageAppNotificationBtn=findViewById(R.id.setting_turnOffMessageAppNotificationButton);
        turnOnMessageAppNotificationBtn=findViewById(R.id.setting_turnOnMessageAppNotificationButton);
        if(isNotificationServiceRunning()){
            turnOnNotificationListnerBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        User user=userDb.getUserData();
        ApiRef.userRef.child(user.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            progressDialog.dismiss();
                            User user = snapshot.getValue(User.class);
                            userDb.setUserData(user);

                            emailEt.setText(""+user.getNotificationEmail());
                            emailTv.setText(""+user.getEmail());
                            statusTv.setText("Account Status: "+user.getStatus());
                            sendNotificationStatusTv.setText(""+user.getSendNotification());
                            sendMessageAppNotificationStatusTv.setText(""+user.getSendMessageAppNotification());

                            if(user.getSendNotification().equals(User.SEND_NOTIFICATION)){
                                turnOffNotificationBtn.setVisibility(View.VISIBLE);
                            }else{
                                turnOnNotificationBtn.setVisibility(View.VISIBLE);
                            }
                           if(user.getSendMessageAppNotification().equals(User.SEND_NOTIFICATION)){
                                turnOffMessageAppNotificationBtn.setVisibility(View.VISIBLE);
                            }else{
                                turnOnMessageAppNotificationBtn.setVisibility(View.VISIBLE);
                            }
                        }else{
                            userDb.logoutUser(SettingActivity.this);
                            progressDialog.dismiss();
                            Toast.makeText(SettingActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
    private boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }
}