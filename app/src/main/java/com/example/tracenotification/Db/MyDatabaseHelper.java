package com.example.tracenotification.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tracenotification.Model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper  extends SQLiteOpenHelper {

    private  static  final  String DATABASE_NAME="Notification.db";
    private  static  final  String TABLE_NAME="notification_lists";
    private  static  final  String ID="_id";
    private  static  final  String BODY="body";
    private  static  final  String PACKAGE_NAME="packageName";
    private  static  final  String TICKER="ticker";
    private  static  final  String TITLE="title";
    private  static  final  String APPLICATION_NAME="applicationName";
    private  static  final  String DATE="date";
    private  static  final  int VERSION_NUMBER=4;
    private  static  final  String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+"("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+BODY+" VARCHAR(4550),"+PACKAGE_NAME+" VARCHAR(150), "+TICKER+" VARCHAR(500), "+APPLICATION_NAME+" VARCHAR(150), "+DATE+" VARCHAR(50),"+TITLE+" VARCHAR(500));";
    private  static  final  String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;
    private  static  final  String GETVALUE="SELECT * FROM "+TABLE_NAME;
    private Context context;



    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try{
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }catch (Exception e){
            Toast.makeText(context, "Error : "+e, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            sqLiteDatabase.execSQL(DROP_TABLE);
            onCreate(sqLiteDatabase);
        }catch (Exception e){
            Toast.makeText(context, "Something Wrong"+e, Toast.LENGTH_SHORT).show();
        }

    }


    public long addNewNotification(NotificationModel model){

        SQLiteDatabase sqLiteDatabase= this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(BODY,model.getBody());
        contentValues.put(PACKAGE_NAME,model.getPackageName());
        contentValues.put(TICKER,model.getTicker());
        contentValues.put(TITLE,model.getTitle());
        contentValues.put(APPLICATION_NAME,model.getApplicationName());
        contentValues.put(DATE,model.getDate());

        long rowid=  sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return rowid;



    }

    public boolean deleteNotification(String id)
    {
        SQLiteDatabase sqLiteDatabase= this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, ID + "=" + id, null) > 0;
    }
    public List<NotificationModel> getAllNotification(){
        SQLiteDatabase sqLiteDatabase= this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(GETVALUE,null);
        List<NotificationModel> notificationList=new ArrayList<>();
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                int id=cursor.getInt(0);
                String body=cursor.getString(1);
                String packageName=cursor.getString(2);
                String ticker=cursor.getString(3);
                String applicationName=cursor.getString(4);
                String date=cursor.getString(5);
                String title=cursor.getString(6);
                if(title!=null && !title.equals("Chat heads active")){
                    notificationList.add(new NotificationModel(id,body,null,packageName,ticker,title,applicationName,date));
                }else if(title==null && body!=null){
                    notificationList.add(new NotificationModel(id,body,null,packageName,ticker,title,applicationName,date));
                }
            }
        }
        return notificationList;

    }



}
