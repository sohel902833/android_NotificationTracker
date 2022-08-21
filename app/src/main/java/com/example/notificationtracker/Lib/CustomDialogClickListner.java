package com.example.notificationtracker.Lib;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

public interface CustomDialogClickListner {
        void onPositiveButtonClicked(View view, AlertDialog dialog);
        void onNegativeButtonClicked(View view, AlertDialog dialog);
}
