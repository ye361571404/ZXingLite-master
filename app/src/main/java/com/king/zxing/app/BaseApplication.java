package com.king.zxing.app;

import android.app.Application;
import android.content.Context;

import com.king.zxing.app.helper.DBHelper;

/**
 * on 2018/11/28
 */
public class BaseApplication extends Application {


    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = getApplicationContext();
        DBHelper.getInstance().openDB();
    }

    public static Context getContext(){
        return mContext;
    }









}
