package com.example.notificationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.notificationtracker.Db.UserDb;
import com.example.notificationtracker.Lib.AppBar;
import com.example.notificationtracker.Lib.CustomDialog;
import com.example.notificationtracker.Lib.CustomDialogClickListner;
import com.example.notificationtracker.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {
    private AppBar appBar;
    private Toolbar toolbar;


    private BottomNavigationView navigationView;
    private UserDb userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        init();
        appBar.init(toolbar, "Pending Users");
        appBar.hideBackButton();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new FragmentPendingUser()).commit();
        navigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.menu_PendingId: {
                        selectedFragment = new FragmentPendingUser();
                        appBar.setAppBarText("Pending Users");
                        break;
                    }
                    case R.id.menu_ApprovedId: {
                        selectedFragment = new FragmentApprovedUser();
                        appBar.setAppBarText("Approved Users");
                        break;
                    }
                    case R.id.menu_logoutId: {
                        logoutAdmin();
                        break;
                    }
                }
                if(selectedFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                            selectedFragment).commit();
                    return true;
                }else{
                    return true;
                }

            }
        });
    }

    private void init() {
        appBar = new AppBar(this);
        toolbar = findViewById(R.id.appbarId);
        userDb=new UserDb(this);
        //navigation
        navigationView = findViewById(R.id.main_nav);
    }

    private void  logoutAdmin(){
        CustomDialog customDialog=new CustomDialog(this);
        customDialog.show("Logout?");
        customDialog.onActionClick(new CustomDialogClickListner() {
            @Override
            public void onPositiveButtonClicked(View view, AlertDialog dialog) {

                userDb.logoutUser(AdminMainActivity.this);
            }

            @Override
            public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                dialog.dismiss();
            }
        });
    }
}