package com.example.tracenotification.Model;

import android.graphics.Bitmap;

public class NotificationModel {
    int i=-2;
    String body="";
    Bitmap imaBitmap;
    String packageName="";
    String ticker="";
    String title="";
    String applicationName="";
    String date="";
    public NotificationModel(){}
    public NotificationModel(String body, Bitmap imaBitmap, String packageName, String ticker, String title, String applicationName, String date) {
        this.body = body;
        this.imaBitmap = imaBitmap;
        this.packageName = packageName;
        this.ticker = ticker;
        this.title = title;
        this.applicationName = applicationName;
        this.date=date;
    }

    public NotificationModel(int i, String body, Bitmap imaBitmap, String packageName, String ticker, String title, String applicationName, String date) {
        this.i = i;
        this.body = body;
        this.imaBitmap = imaBitmap;
        this.packageName = packageName;
        this.ticker = ticker;
        this.title = title;
        this.applicationName = applicationName;
        this.date = date;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public Bitmap getImaBitmap() {
        return imaBitmap;
    }

    public void setImaBitmap(Bitmap imaBitmap) {
        this.imaBitmap = imaBitmap;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String name) {
        this.body = name;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}