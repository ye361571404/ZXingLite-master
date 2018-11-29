package com.king.zxing.app.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabaseLockedException;

import com.king.zxing.app.BaseApplication;
import com.king.zxing.app.bean.DaoMaster;
import com.king.zxing.app.bean.DaoSession;
import com.king.zxing.app.bean.IMEIBeanDao;
import com.king.zxing.app.db.GreenDaoContext;

/**
 * on 2018/11/28
 */
public class DBHelper {

    private static volatile DBHelper instance;
    private Context mContext;
    private String dbName = "systek.db";
    private DaoMaster.DevOpenHelper mDevOpenHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public DBHelper(Context context) {
        this.mContext = context;
    }


    public static DBHelper getInstance(){
        if (instance == null) {
            synchronized(DBHelper.class){
                if (instance == null) {
                    DBHelper.instance = new DBHelper(BaseApplication.getContext());
                }
            }
        }
        return instance;
    }



    public void openDB(){
        mDevOpenHelper = new DaoMaster.DevOpenHelper(new GreenDaoContext(BaseApplication.getContext()), dbName, null);
        try {
            mDaoMaster = new DaoMaster(mDevOpenHelper.getWritableDb());
        }catch(SQLiteDatabaseLockedException e){
            e.printStackTrace();
        }
        mDaoSession = mDaoMaster.newSession();
    }

    public boolean isOpenDb(){
        return mDevOpenHelper.getReadableDatabase().isOpen();
    }

    public void closeDB(){
        mDevOpenHelper.close();
    }



    public IMEIBeanDao getIMEIBeanDao(){
        return mDaoSession.getIMEIBeanDao();
    }

}
