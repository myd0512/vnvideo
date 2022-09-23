# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/macpro/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the groupLast number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the groupLast number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile



-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化  不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepclasseswithmembernames class * {
        native <methods>;
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#不提示V4包下错误警告
-dontwarn android.support.v4.**
#保持下面的V4兼容包的类不被混淆
-keep class android.support.v4.**{*;}

-keep public class * extends android.view.View{
        *** get*();
        void set*(***);
        public <init>(android.content.Context);
        public <init>(android.content.Context,
                                    android.util.AttributeSet);
        public <init>(android.content.Context,
                                    android.util.AttributeSet,int);
}

#不混淆Parcelable和它的实现子类，还有Creator成员变量
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }

    #不混淆Serializable和它的实现子类、其成员变量
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }
-keepclassmembers class * {
        public <init>(org.json.JSONObject);
    }
-keep class com.yunbao.common.**{*;}
-keep class com.yunbao.live.socket.**{*;}


-dontwarn cn.magicwindow.**
-keep class cn.magicwindow.** {*;}

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-keep class com.bun.miitmdid.** { *; }
#eventBus
-keepattributes *Annotation*

-keepclassmembers class ** {

    @org.greenrobot.eventbus.Subscribe <methods>;

}

-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor

-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}


-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
-keepattributes Annotation
-keepattributes InnerClasses

-dontwarn com.tencent.bugly.**
-keep class com.tencent.bugly.** { *; }

#okgo
    -dontwarn com.lzy.okgo.**
    -keep class com.lzy.okgo.**{*;}

    #okrx
    -dontwarn com.lzy.okrx.**
    -keep class com.lzy.okrx.**{*;}

    #okserver
    -dontwarn com.lzy.okserver.**
    -keep class com.lzy.okserver.**{*;}

    #okhttp
    -dontwarn okhttp3.**
    -keep class okhttp3.**{*;}

    #okio
    -dontwarn okio.**
    -keep class okio.**{*;}

# RX
-dontwarn rx.**
-keepclassmembers class rx.** { *; }
# retrolambda
-dontwarn java.lang.invoke.*

#Universal Image Loader
-keep class com.nostra13.universalimageloader.** { *; }
-keepattributes Signature

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
   @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
   @butterknife.* <methods>;
}

# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}
-keep class com.google.protobuf.** {*;}
#这句非常重要，主要是滤掉 com.demo.demo.bean包下的所有.class文件不进行混淆编译,com.demo.demo是你的包名
-keep class com.demo.demo.bean.** {*;}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep class com.uc.** {*;}
-keep class com.zui.** {*;}
-keep class com.miui.** {*;}
-keep class com.heytap.** {*;}
-keep class a.** {*;}
-keep class com.vivo.** {*;}

-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.alibaba.android.arouter.routes.** {*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}