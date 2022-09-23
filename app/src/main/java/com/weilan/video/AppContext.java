package com.weilan.video;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.meihu.beautylibrary.MHSDK;
import com.mob.MobSDK;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEnv;
import com.tencent.bugly.crashreport.CrashReport;
//import com.tencent.live.TXLiveBase;
import com.tencent.rtmp.TXLiveBase;
import com.yunbao.beauty.ui.views.BeautyDataModel;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Language.LanguageUtil;
import com.yunbao.common.bean.MeiyanConfig;
import com.yunbao.common.utils.DevicesUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.LogUtil;
import com.yunbao.common.utils.MiitHelper;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.common.appsflyer.AppsFlyer;

import java.util.Locale;

import cn.jiguang.jmlinksdk.api.JMLinkAPI;
import cn.jiguang.jmlinksdk.api.JMLinkResponse;
import cn.jiguang.jmlinksdk.api.JMLinkResponseObj;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends CommonAppContext {

    public static AppContext sInstance;
    private boolean mBeautyInited;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;


        //腾讯云鉴权url
        String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/afae5862e02ad26df964d6f5b1737b4c/TXUgcSDK.licence";
        //腾讯云鉴权key
        String ugcKey = "178890087209a5c0c061af5dd1313305";
        TXLiveBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey);
        L.setDeBug(BuildConfig.DEBUG);

        //初始化腾讯bugly
        CrashReport.initCrashReport(this,"fe9f3807bc",false);
        CrashReport.setAppVersion(this, CommonAppConfig.getInstance().getVersion());

        //初始化ShareSdk
        MobSDK.init(this);
        //初始化极光推送
        ImPushUtil.getInstance().init(this);
        //初始化极光IM
        ImMessageUtil.getInstance().init();

//        JMLinkAPI.getInstance().setDebugMode(true);
//        JMLinkAPI.getInstance().init(sInstance);
//        JMLinkAPI.getInstance().register(new JMLinkResponse() {
//            @Override
//            public void response(JMLinkResponseObj obj) {
//                Log.e("JMLinkAPI", "replay = " + obj.paramMap + " " + obj.uri + " " + obj.source + " " + obj.type);
//
//                // 获取无码邀请参数
//                String inviteid = JMLinkAPI.getInstance().getParam("u_id");
//                if(inviteid != null){
//                    ToastUtil.show(inviteid);
//                }else{
//                    ToastUtil.show("JMLinkAPI null");
//                }
//            }
//        });


        //初始化 ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }
        PLShortVideoEnv.init(this);
        Context context = getApplicationContext();
        AppsFlyer.InitAppsFlyer(context);
        new MiitHelper(context, ids -> {
            if (!TextUtils.isEmpty(ids)){
                CommonAppConfig.getInstance().setIsSupportOaid(true);
                CommonAppConfig.getInstance().setOaid(ids);
            }
        });

//      LanguageUtil.applyLanguage(this);

        //腾讯im
        // 1. 从 IM 控制台获取应用 SDKAppID，详情请参考 SDKAppID。
        // 2. 初始化 config 对象
//        V2TIMSDKConfig config = new V2TIMSDKConfig();
//        // 3. 指定 log 输出级别，详情请参考 SDKConfig。
//        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
//        // 4. 初始化 SDK 并设置 V2TIMSDKListener 的监听对象。
//        // initSDK 后 SDK 会自动连接网络，网络连接状态可以在 V2TIMSDKListener 回调里面监听。
//        V2TIMManager.getInstance().initSDK(context, sdkAppID, sdkConfig, new V2TIMSDKListener() {
//                    // 5. 监听 V2TIMSDKListener 回调
//                    @Override
//                    public void onConnecting() {
//                        // 正在连接到腾讯云服务器
//                    }
//                    @Override
//                    public void onConnectSuccess() {
//                        // 已经成功连接到腾讯云服务器
//                    }
//                    @Override
//                    public void onConnectFailed(int code, String error) {
//                        // 连接腾讯云服务器失败
//                    }
//        });
    }

    /**
     * 初始化美狐
     */

    public void initBeautySdk(String beautyKey) {
        CommonAppConfig.getInstance().setBeautyKey(beautyKey);
        if (!TextUtils.isEmpty(beautyKey)) {
            if (!mBeautyInited) {
                mBeautyInited = true;
                MHSDK.getInstance().init(this, beautyKey);
                CommonAppConfig.getInstance().setTiBeautyEnable(true);

                //根据后台配置设置美颜参数
                MeiyanConfig meiyanConfig = CommonAppConfig.getInstance().getConfig().parseMeiyanConfig();
                int[] dataArray = meiyanConfig.getDataArray();
                BeautyDataModel.getInstance().setBeautyDataMap(dataArray);

                L.e("美狐初始化------->" + beautyKey);
            }
        } else {
            CommonAppConfig.getInstance().setTiBeautyEnable(false);
        }
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        Locale newLocale = LanguageUtil.getLocale(getApplicationContext());
//        Context context = LanguageUtil.wrap(this,newLocale);
//        super.attachBaseContext(context);
//    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        Locale newLocale = LanguageUtil.getLocale(getApplicationContext());
//        ContextWrapper wrapper = LanguageUtil.wrap(this,newLocale);
//        super.onConfigurationChanged(wrapper.getApplicationContext().getResources().getConfiguration());
//    }
}
