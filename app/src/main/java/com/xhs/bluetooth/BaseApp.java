package com.xhs.bluetooth;

import android.app.Application;

/**
 * Created by 布鲁斯.李 on 2018/7/18.
 * Email:zp18595658325@163.com
 */

public class BaseApp extends Application {

    private static BaseApp mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
    }

    public synchronized static BaseApp getmContext(){
        return mContext;
    }
}
