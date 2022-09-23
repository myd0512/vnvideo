package com.yunbao.common.http;

import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;

import okhttp3.OkHttpClient;

public class HttpNoRSAClient{
    private static HttpNoRSAClient sInstance;

    private String mLanguage;//语言
    private String mUrl;
    private OkHttpClient mOkHttpClient;
    private OkHttpBuilder mBuilder;

    private HttpNoRSAClient() {
        mUrl = CommonAppConfig.HOST + "/appapi/?service=";
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BASIC);
//        HttpRSAInterceptor rsaInterceptor = new HttpRSAInterceptor();
//        rsaInterceptor.setPublicKey(CommonAppConfig.RSA_PUBLIC_KEY);
//        rsaInterceptor.setPrivateKey(CommonAppConfig.RSA_PRIVATE_KEY);
        mBuilder = new OkHttpBuilder();
        mOkHttpClient = mBuilder
                .setHost(CommonAppConfig.HOST)
                .setTimeout(10000)
                .setLoggingInterceptor(loggingInterceptor)
//                .setRSAInterceptor(rsaInterceptor)
                .build(CommonAppContext.sInstance);
    }


    public static HttpNoRSAClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpNoRSAClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpNoRSAClient();
                }
            }
        }
        return sInstance;
    }



    public GetRequest<JsonBean> get(String serviceName, String tag) {
        return mBuilder.req1(mUrl + serviceName, tag, JsonBean.class)
                .params(CommonHttpConsts.LANGUAGE, mLanguage);

    }

    public PostRequest<JsonBean> post(String serviceName, String tag) {
        return mBuilder.req2(mUrl + serviceName, tag, JsonBean.class)
                .params(CommonHttpConsts.LANGUAGE, mLanguage);
    }

    public PostRequest<JsonBean> postFile(String host, String serviceName, String tag){
        String url = host + "/appapi/?service=" + serviceName;
        return mBuilder.req2(url, tag, JsonBean.class)
                .params(CommonHttpConsts.LANGUAGE, mLanguage);
    }

    public void cancel(String tag) {
        mBuilder.cancel(mOkHttpClient,tag);
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }
}
