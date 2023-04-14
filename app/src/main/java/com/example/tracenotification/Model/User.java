package com.example.tracenotification.Model;

public class User {
    String userId;
    String email;
    String password;
    String status;
    boolean isAdmin;
    String sendNotification="Off";
    String sendMessageAppNotification="Off";
    String notificationEmail="";
    String secondNotificationEmail="";
    public  static  final  String ACTIVE_STATUS="active";
    public  static  final  String PENDING_STATUS="pending";
    public  static  final  String SEND_NOTIFICATION="On";
    public  static  final  String DONT_SEND_NOTIFICATION="Off";

    public  User(){}

    public User(String userId,String email, String password, String status,boolean isAdmin,String sendNotification,String sendMessageAppNotification) {
        this.userId=userId;
        this.email = email;
        this.password = password;
        this.status = status;
        this.isAdmin=isAdmin;
        this.sendNotification=sendNotification;
        this.sendMessageAppNotification=sendMessageAppNotification;
    }

    public String getSecondNotificationEmail() {
        return secondNotificationEmail;
    }

    public void setSecondNotificationEmail(String secondNotificationEmail) {
        this.secondNotificationEmail = secondNotificationEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(String sendNotification) {
        this.sendNotification = sendNotification;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendMessageAppNotification() {
        return sendMessageAppNotification;
    }

    public void setSendMessageAppNotification(String sendMessageAppNotification) {
        this.sendMessageAppNotification = sendMessageAppNotification;
    }
}
