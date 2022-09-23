package com.yunbao.common.appsflyer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerLibCore;
import com.appsflyer.AppsFlyerProperties;
import com.appsflyer.AppsFlyerTrackingRequestListener;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DevicesUtil;
import com.yunbao.common.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AppsFlyer {
    private static Context _context;
    public static boolean _conversionFlag = false;
    public static Map<String, Object> _conversionData;
    public static String _conversionErrorMessage = "";
    public static boolean _openflag = false;
    public static Map<String, String> _openAttr;
    public static String _openErrorMessage = "";

    public static void InitAppsFlyer(Context context)
    {
        String afKey = CommonAppConfig.getInstance().getAFKey();
        AppsFlyer.SetContext(context);
        AppsFlyerConversionListener conversionListener = AppsFlyer.StartConversionListener();
        AppsFlyerLib.getInstance().init(afKey, conversionListener, context);
        AppsFlyer.SetUID();
        AppsFlyerLib.getInstance().startTracking(context, afKey);
    }

    public static void SetContext(Context context){
        _context = context;
    }

    public static boolean GetConversionFlag(){
        return _conversionFlag;
    }

    public static String GetConversionData(){
        return map2json(_conversionData);
    }

    public static String GetConversionError(){
        return _conversionErrorMessage;
    }

    public static boolean GetOpenFlag(){
        return _openflag;
    }

    public static String GetOpenAttr(){
        return map2jsonstr(_openAttr);
    }

    public static String GetOpenError(){
        return _openErrorMessage;
    }

    public static String GetAppsFlyerUID(){
        return AppsFlyerLib.getInstance().getAppsFlyerUID(_context);
    }

    public static void SetCustomerUserID(String id){
        String oldid = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID);
        if(!TextUtils.isEmpty(oldid) && !oldid.equals("")){
            return;
        }
        AppsFlyerLib.getInstance().setCustomerUserId(id);
    }

    public static void SetUID(){
        String id = GetAppsFlyerUID();
        Log.i("appflyerSetUid",id);
        if(!TextUtils.isEmpty(id) && !id.equals("")){
            return;
        }
        String uniqueId = "";
        try {
            uniqueId = DevicesUtil.getIMEI(_context);
            if (TextUtils.isEmpty(uniqueId) || uniqueId.equals("")) {
                if (CommonAppConfig.getInstance().isSupportOaid()) {
                    Log.i("appflyerSetUid","setOaid");
                    uniqueId = CommonAppConfig.getInstance().getOaid();
                    AppsFlyerLib.getInstance().setOaidData(uniqueId);
                } else {
                    Log.i("appflyerSetUid","setAndroidId");
                    uniqueId = DevicesUtil.getAndroidId(_context);
                    AppsFlyerLib.getInstance().setAndroidIdData(uniqueId);
                }
            }else {
                Log.i("appflyerSetUid","setimei");
                AppsFlyerLib.getInstance().setImeiData(uniqueId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SetAdditionalData(String json){
        HashMap<String, Object> CustomDataMap = new HashMap<>();
        jsonObject2HashMap(json, CustomDataMap);
        AppsFlyerLib.getInstance().setAdditionalData(CustomDataMap);
    }

    public static void SetMinTimeBetweenSession(int second){
        AppsFlyerLib.getInstance().setMinTimeBetweenSessions(second);
    }

    public static void ReportTrackSession(){
        AppsFlyerLib.getInstance().reportTrackSession(_context);
    }

    private static void RecordEventInner(Map<String, Object> eventValue, String event){
        AppsFlyerLib.getInstance().trackEvent(_context, event, eventValue, StartTrackingListener());
    }

    public static void RecordEvent(String json, String event){
        HashMap<String, Object> eventValue = new HashMap<>();
        jsonObject2HashMap(json, eventValue);
        RecordEventInner(eventValue, event);
    }

    public static AppsFlyerTrackingRequestListener StartTrackingListener() {
        return new AppsFlyerTrackingRequestListener() {
            @Override
            public void onTrackingRequestSuccess() {
                Log.i("apkinfo", "onTrackingRequestSuccess");

            }
            @Override
            public void onTrackingRequestFailure(String error) {
                Log.i("apkinfo", "onTrackingRequestFailure: " + error);
            }
        };
    }

    public static AppsFlyerConversionListener StartConversionListener(){
        return new AppsFlyerConversionListener() {

            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                _conversionFlag = true;
                _conversionData = conversionData;
                Log.i("apkinfo", "onConversionDataSuccess");
                for (String attrName : conversionData.keySet()) {
                    Log.i("apkinfo", "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
                if(conversionData.get("is_first_launch").toString().equals("true")){
                    Log.i("apkinfo", "AppsFlyerConversionListener enter first launch");
                    String inviter = SpUtil.getInstance().getStringValue(SpUtil.UPLOAD_INVITER);
                    Log.i("apkinfo", "localinviter--"+inviter);
                    if(TextUtils.isEmpty(inviter)|| inviter.equals("") || inviter.equals("uploaded")){
                        String uid = CommonAppConfig.getInstance().getUid();
                        String code = conversionData.get("inviter").toString();
                        Log.i("apkinfo", "uid--"+uid);
                        if(TextUtils.isEmpty(uid)||uid.equals("-1")){
                            Log.i("apkinfo", "AppsFlyerConversionListener 当前未登陆 保存邀请信息"+code);
                            SpUtil.getInstance().setStringValue(SpUtil.UPLOAD_INVITER, code);
                        }else {
                            Log.i("apkinfo", "AppsFlyerConversionListener 当前已登陆 直接发送邀请信息"+code);
                            CommonHttpUtil.setDistribut(code, new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    Log.i("apkinfo", msg);
                                }
                            });
                            SpUtil.getInstance().setStringValue(SpUtil.UPLOAD_INVITER, "uploaded");
                        }
                    }
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.i("apkinfo", "error getting conversion data: " + errorMessage);
                _conversionFlag = false;
                _conversionErrorMessage = errorMessage;
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {
                _openflag = true;
                _openAttr = conversionData;
//                Log.i("apkinfo", "onAppOpenAttribution");
//                for (String attrName : conversionData.keySet()) {
//                    Log.i("apkinfo", "attribute: " + attrName + " = " + conversionData.get(attrName));
//                }

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                _openflag = false;
                _openErrorMessage = errorMessage;
                Log.i("apkinfo", "error onAttributionFailure : " + errorMessage);
            }
        };
    }

    private static String map2json(Map<String, Object> data){
        if (data == null || data.isEmpty()){
            Log.i("map2json:","no value");
            return "";
        }
        JSONObject json = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry: data.entrySet()) {
                String key = entry.getKey();
                if (null == key) {
                    key = "";
                }
                Object value = entry.getValue();
                Log.i("map2json:",key+"="+value);
                json.put(key, value);
            }
        } catch(Exception e) {
            Log.e("json encode", e.toString());
        }
        return json.toString();
    }

    private static String map2jsonstr(Map<String, String> data){
        if (data == null || data.isEmpty()){
            return "";
        }
        JSONObject json = new JSONObject();
        try {
            for (Map.Entry<String, String> entry: data.entrySet()) {
                String key = entry.getKey();
                if (null == key) {
                    key = "";
                }
                Object value = entry.getValue();
                json.put(key, value);
            }
        } catch(Exception e) {
            Log.e("json encode", e.toString());
        }
        return json.toString();
    }

    private static void jsonObject2HashMap(String json, Map<String, Object> rst) {
        try {
            JSONObject jo = new JSONObject(json);
            for (Iterator<String> keys = jo.keys(); keys.hasNext();) {
                    String key = keys.next();
                    rst.put(key, jo.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
