package com.yunbao.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import com.yunbao.common.CommonAppConfig;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author:
 * @createTime: 2019/9/27
 * @description:
 * @changed by:
 */
public class DevicesUtil {

    /**
     *
     * @param context
     * @return
     */
    private static final String TAG = "DevicesUtil";

    public static String getIdfa(Context context) {
        String idfa;
        if (CommonAppConfig.getInstance().isSupportOaid()) {
            idfa = CommonAppConfig.getInstance().getOaid();
        } else {
            idfa = getUniqueId(context);
        }
        return idfa;
    }


    /**
     *
     * @param context
     * @return
     */
    public static String getUniqueId(Context context) {
        String uniqueId = "";
        try {
            uniqueId = getIMEI(context);
            Log.d(TAG, "IMEI:"+ uniqueId);
            if (TextUtils.isEmpty(uniqueId) || uniqueId.equals("")) {
              if(CommonAppConfig.getInstance().isSupportOaid()) {
                  uniqueId = CommonAppConfig.getInstance().getOaid();
                  Log.d(TAG, "Oaid:"+ uniqueId);
              }else{
                   uniqueId = getAndroidId(context);
                  Log.d(TAG, "AndroidId:"+ uniqueId);
              }
            }
        } catch (Exception e) {
                e.printStackTrace();
                return "";
        }
        return uniqueId;
    }

    public static void printALl(Context context) {
        try {
            Log.i("apkinfo", "imei:"+getIMEI(context)+"|oaid:"+CommonAppConfig.getInstance().getOaid()+"|androidid:"+getAndroidId(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @return
     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static String getIMEI(Context context) {
//        try {
//            int state = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
//            TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if (state == PackageManager.PERMISSION_GRANTED) {
//                String imei = tel.getDeviceId();
//                if (TextUtils.isEmpty(imei) || imei.equals("")) {
//                    imei = tel.getImei();
//                }
//                return imei;
//            }
//        }catch(Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//        return "";
//    }

    public static String getIMEI(Context context){
        String imei = "";
        try {
            int state = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (state == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imei = tm.getDeviceId();
                } else {
                    Method method = tm.getClass().getMethod("getImei");
                    imei = (String) method.invoke(tm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        @SuppressLint("HardwareIds")
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidID;
    }


    /**
     *
     * @return
     */
    public static String getUUID() {
        String serial;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10;

        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial";
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

//    private static boolean selfPermissionGranted(Context context, String permission){
//        // For Android < Android M, self permissions are always granted.
//        boolean result = true;
//        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (targetSdkVersion >= Build.VERSION_CODES.M) {
//                // targetSdkVersion >= Android M, we can
//                // use Context#checkSelfPermission
//                result = ActivityCompat.checkSelfPermission(context, permission)
//                        == PackageManager.PERMISSION_GRANTED;
//            } else {
//                // targetSdkVersion < Android M, we have to use PermissionChecker
//                result = PermissionChecker.checkSelfPermission(context, permission)
//                        == PermissionChecker.PERMISSION_GRANTED;
//            }
//        }
//        Log.d(TAG, "selfPermissionGranted: Permission="+permission+",result="+result);
//        return result;
//    }



}
