package com.example.tracenotification.Lib;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
    public static  String getTodayDate(){
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }
    public static  String getCurrentTime(){
        Date d=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a");
        String currentDateTimeString = sdf.format(d);
        return currentDateTimeString;
    }
}
