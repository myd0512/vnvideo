package com.yunbao.common.Language;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class LanguageUtil {
    public static void applyLanguage(Context context) {

        Locale locale = getLocale(context);

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static int getLanguage(Context context) {
        SharedPreferences sp = context.getSharedPreferences("Language",Context.MODE_PRIVATE);
        String language = sp.getString("i18N_l",null);
        if(language == null){
            return 1;
        }else if(language.equals("vi")){
            return 0;
        }else{
            return 2;
        }
    }

    public static Locale getLocale(Context context){
        Locale locale;

        SharedPreferences sp = context.getSharedPreferences("Language",Context.MODE_PRIVATE);
        String language = sp.getString("i18N_l",null);
        if(language == null){
            locale = Locale.getDefault();
        }else{
            String country = sp.getString("i18N_c",null);
            locale = new Locale(language,country);
        }

        return locale;
    }

    public static void changeLanguage(Context context,LanguageEnum constants ){

        SharedPreferences sp = context.getSharedPreferences("Language",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        switch (constants){
            case VIETNAM:
                editor.putString("i18N_l","vi");
                editor.putString("i18N_c","rVN");
                break;
            default:
                editor.putString("i18N_l",null);
        }

        editor.commit();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static ContextWrapper wrap(Context context,Locale newLocale){
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            configuration.setLocale(newLocale);
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            context = context.createConfigurationContext(configuration);
        }else{
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);
        }
        res.updateConfiguration(configuration,res.getDisplayMetrics());
        return new ContextWrapper(context);
    }

}
