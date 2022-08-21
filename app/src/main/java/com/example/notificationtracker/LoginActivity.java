package com.example.notificationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notificationtracker.Db.UserDb;
import com.example.notificationtracker.Lib.ApiRef;
import com.example.notificationtracker.Lib.AppBar;
import com.example.notificationtracker.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBar appBar;
    private ProgressDialog progressDialog;
    private TextView titleTv;
    private EditText emailEt,passwordEt;
    private Button actionButton,changeActionButton;
    private UserDb userDb;
    private String ACTION="login";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();


        changeActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ACTION.equals("login")){
                    ACTION="register";
                }else{
                    ACTION="login";
                }
                changeActionState();
            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailEt.getText().toString();
                String password=passwordEt.getText().toString().trim();
                if(email.isEmpty()){
                    emailEt.setError("Enter Your Email Address.");
                    emailEt.requestFocus();
                }else if(password.isEmpty()){
                    passwordEt.setError("Enter Your Password");
                    passwordEt.requestFocus();
                }else if(!password.isEmpty() && password.length()<6 && ACTION.equals("register")){
                    passwordEt.setError("Password To Short (Minimum 6 Character)");
                    passwordEt.requestFocus();
                }else{
                    if(checkAdmin(email,password)){
                        loginAdmin(email,password);
                    }else{
                        if(ACTION.equals("login")){
                            loginUser(email,password);
                        }else{
                            registerUser(email,password);
                        }
                    }


                }
            }
        });




    }



    private  void init(){
        userDb=new UserDb(this);
        mAuth=FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Login");
        appBar.hideBackButton();

        progressDialog=new ProgressDialog(this);
        titleTv=findViewById(R.id.login_titleTvId);
        emailEt=findViewById(R.id.login_emailEt);
        passwordEt=findViewById(R.id.login_passwordEt);
        actionButton=findViewById(R.id.login_takeActionButton);
        changeActionButton=findViewById(R.id.login_changeActionButton);



    }

    private void changeActionState() {
        if(ACTION.equals("login")){
            titleTv.setText("Login");
            appBar.setAppBarText("Login User");
            actionButton.setText("Login");
            changeActionButton.setText("Register");
        }else{
            titleTv.setText("Register");
            appBar.setAppBarText("Register User");
            actionButton.setText("Register");
            changeActionButton.setText("Login");
        }
    }

    private void loginUser(String email,String password){
        progressDialog.setMessage("We Are Checking Your Accounts");
        progressDialog.setTitle("Please Wait.");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,password.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String userId=mAuth.getCurrentUser().getUid();
                    ApiRef.userRef.child(userId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        User user = snapshot.getValue(User.class);
                                        userDb.setUserData(user);
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        sendUserToMainActivity();
                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void registerUser(String email,String password){
        progressDialog.setMessage("We Are Creating Your Accounts");
        progressDialog.setTitle("Please Wait.");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String userId=mAuth.getCurrentUser().getUid();
                    User user=new User(userId,email,password,User.PENDING_STATUS,false,User.DONT_SEND_NOTIFICATION,User.SEND_NOTIFICATION);
                    ApiRef.userRef.child(userId)
                            .setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        userDb.setUserData(user);
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        sendUserToMainActivity();

                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loginAdmin(String email, String password) {
        User user=new User("admin",email,password,"admin",true,User.DONT_SEND_NOTIFICATION,"");
        userDb.setUserData(user);
        progressDialog.dismiss();
        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
        sendUserToAdminMainActivity();
    }


    private void sendUserToMainActivity() {
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
     private void sendUserToAdminMainActivity() {
            Intent intent=new Intent(LoginActivity.this, AdminMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

    private boolean checkAdmin(String email,String password){
        if(email.equals("smsandnotificationforwarder@gmail.com") && password.equals("notificationsender1234")){
            return true;
        }else{
            return false;
        }
    }
}