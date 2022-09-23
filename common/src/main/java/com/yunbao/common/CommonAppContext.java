package com.yunbao.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import android.util.Log;

//import androidx.multidex.MultiDex;
//import androidx.multidex.MultiDexApplication;

import com.umeng.commonsdk.UMConfigure;
import com.yunbao.common.utils.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Created by cxf on 2017/8/3.
 */

public class CommonAppContext extends MultiDexApplication {

    public static final String JMMSG = "JMMSG";
    public static CommonAppContext sInstance;
    private int mCount;
    private boolean mFront;//是否前台

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //初始化友盟统计
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        registerActivityLifecycleCallbacks();
//        initComment();
    }

    //初始化apk附带信息读取
    private void initComment(){
        String pth = getPackageCodePath();
        String comment = readApk(pth);
        if(comment == null) comment = "";
        Log.i("apkinfo", comment);
        CommonAppConfig.getInstance().setComment(comment);
    }

    private String readApk(String path) {
        byte[] bytes = null;
        try {
            File file = new File(path);
            RandomAccessFile accessFile = new RandomAccessFile(file, "r");
            long index = accessFile.length();

            // 文件最后两个字节代表了comment的长度
//            bytes = new byte[2];
//            index = index - bytes.length;
//            accessFile.seek(index);
//            accessFile.readFully(bytes);
//
//            int contentLength = bytes2Short(bytes, 0);
//
//            // 获取comment信息
//            bytes = new byte[contentLength];
//            index = index - bytes.length;
//            accessFile.seek(index);
//            accessFile.readFully(bytes);
            //文件最后6个字节代表邀请码
            bytes = new byte[6];
            index = index - bytes.length;
            accessFile.seek((index));
            accessFile.readFully(bytes);

            return new String(bytes, "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static short bytes2Short(byte[] bytes, int offset) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(bytes[offset]);
        buffer.put(bytes[offset + 1]);
        return buffer.getShort(0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    private void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mCount++;
                if (!mFront) {
                    mFront = true;
                    L.e("AppContext------->处于前台");
                    CommonAppConfig.getInstance().setFrontGround(true);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mCount--;
                if (mCount == 0) {
                    mFront = false;
                    L.e("AppContext------->处于后台");
                    CommonAppConfig.getInstance().setFrontGround(false);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

}
