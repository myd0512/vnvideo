package com.yunbao.common.http;

import android.app.Application;

//import com.lzy.okgo.OkGo;
//import com.lzy.okgo.cache.CacheMode;
//import com.lzy.okgo.cookie.CookieJarImpl;
//import com.lzy.okgo.cookie.store.MemoryCookieStore;
//import com.lzy.okgo.https.HttpsUtils;
//import com.lzy.okgo.request.GetRequest;
//import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.tencent.rtmp.TXLiveBase;
//import com.tencent.live.TXLiveBase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class OkHttpBuilder {
    private int mTimeout = 10000;
    private String mHost;
    private Interceptor mLoggingInterceptor;
    private Interceptor mRSAInterceptor;

    public OkHttpBuilder() {
    }

    public OkHttpBuilder setHost(String host) {
        this.mHost = host;
        return this;
    }

    public OkHttpBuilder setTimeout(int timeout) {
        this.mTimeout = timeout;
        return this;
    }

    public OkHttpBuilder setLoggingInterceptor(Interceptor loggingInterceptor) {
        this.mLoggingInterceptor = loggingInterceptor;
        return this;
    }

    public OkHttpBuilder setRSAInterceptor(Interceptor rsaInterceptor) {
        this.mRSAInterceptor = rsaInterceptor;
        return this;
    }

    public OkHttpClient build(Application application) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout((long)this.mTimeout, TimeUnit.MILLISECONDS);
        builder.readTimeout((long)this.mTimeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout((long)this.mTimeout, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        builder.hostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        if (this.mLoggingInterceptor != null) {
            builder.addInterceptor(this.mLoggingInterceptor);
        }
        if (this.mRSAInterceptor != null) {
            builder.addInterceptor(this.mRSAInterceptor);
        }

        builder.addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Connection", "keep-alive").addHeader("referer", OkHttpBuilder.this.mHost).build();
                return chain.proceed(request);
            }
        });
        OkHttpClient okHttpClient = builder.build();
        OkGo.getInstance().init(application).setOkHttpClient(okHttpClient).setCacheMode(CacheMode.NO_CACHE).setRetryCount(1);
        return okHttpClient;
    }

    public <T> GetRequest<T> req1(String url, String tag, Class<T> clazz) {
        //return TXLiveBase.getInstance().isInited() ? (GetRequest)OkGo.get(url).tag(tag) : OkGo.get("xxxx");
        return (GetRequest)OkGo.get(url).tag(tag);
    }

    public <T> PostRequest<T> req2(String url, String tag, Class<T> clazz) {
        //return TXLiveBase.getInstance().isInited() ? (PostRequest)OkGo.post(url).tag(tag) : OkGo.post("xxxx");
        return (PostRequest)OkGo.post(url).tag(tag);
    }

    public void cancel(OkHttpClient okHttpClient, String tag) {
        OkGo.cancelTag(okHttpClient, tag);
    }
}