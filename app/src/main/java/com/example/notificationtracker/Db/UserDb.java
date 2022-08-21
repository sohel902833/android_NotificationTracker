package com.example.notificationtracker.Db;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.notificationtracker.LoginActivity;
import com.example.notificationtracker.Model.User;
import com.google.gson.Gson;


public class UserDb {
    private Activity activity;
    public UserDb(Activity activity) {
        this.activity = activity;
    }
    public void setUserData(User user) {
        SharedPreferences sharedPreferences=activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.commit();
    }

    public User getUserData(){
        User user=null;
        SharedPreferences sharedPreferences=activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user","");
        user = gson.fromJson(json, User.class);
        return  user;
    }
    public void removeUserData(){
        SharedPreferences userShared = activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        userShared.edit().clear().apply();
    }

    public void logoutUser(Activity activity){
        removeUserData();
        Intent intent=new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

}
