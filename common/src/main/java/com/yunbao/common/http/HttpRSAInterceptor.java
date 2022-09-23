package com.yunbao.common.http;

import android.util.Log;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class HttpRSAInterceptor implements Interceptor {
    // 客户端私钥
    private static String privateKey;
    // 服务端公钥
    private static String publicKey;

    public void setPrivateKey(String key) {
        privateKey = key;
    }

    public void setPublicKey(String key) {
        publicKey = key;
    }

    public static String encryptParams(HashMap<String, String> params) {
        String data = JSON.toJSONString(params);
//        Log.i("http", "request before encryption: " + data);
        // 加密
        try {
            Key key = RSA.privateKey(privateKey);
            return RSA.encrypt(data, key);
        } catch (Exception e) {
            Log.e("http", "request before encryption: " + data, e);
        }
        return "";
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        String method = chain.request().method();
        if (method.equals("GET")) {
            return processGET(chain);
        }
        return processPOST(chain);
    }

    private Response processGET(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
//        Log.i("mmmm", "GET query " + request.url().query());
        return chain.proceed(request);
    }

    private Response processPOST(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response;

        try {
            request = encryptRequest(request);
            response = chain.proceed(request);
            response = decryptResponse(response);
        } catch (Exception e) {
            Log.e("http", "HTTP POST fail", e);
            throw new IOException("RSA decrypt fail", e);
        }

        return response;
    }

    private Request encryptRequest(Request request) throws Exception {
        // 这种类型的就是外面已经处理了加密
        String type = request.body().contentType().type();
        if (type.contains("multipart")) {
//            Log.i("http", "request content-type: " + type);
            return request;
        }
        // 转成JSON格式
        HashMap<String, String> params = requestParams(request);
        String data = JSON.toJSONString(params);
//        Log.i("http", "request before encryption: " + data);
        // 加密
//        Key key = RSA.publicKey(publicKey);
        Key key = RSA.privateKey(privateKey);
        data = RSA.encrypt(data, key);
        // 构建新请求
//        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
//        RequestBody newBody = RequestBody.create(mediaType, data);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        data = "datastring="+ URLEncoder.encode(data, "UTF-8");
        RequestBody newBody = RequestBody.create(mediaType, data);
//        Log.i("http", "request after encryption: " + data);
        return request.newBuilder()
                .header("Content-Type", newBody.contentType().toString())
                .header("Content-Length", String.valueOf(newBody.contentLength()))
                .method(request.method(), newBody)
                .build();
    }

    private Response decryptResponse(Response response) throws Exception {
        String data = response.body().string();
//        Log.i("http", "response before decryption: " + data);
        // 解密
//        Key key = RSA.privateKey(privateKey);
        Key key = RSA.publicKey(publicKey);
        data = RSA.decrypt(data, key);
        // 构建新响应
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        ResponseBody newBody = ResponseBody.create(mediaType, data);
//        Log.i("http", "response after decryption: " + data);
        return response.newBuilder()
                .header("Content-Type", newBody.contentType().toString())
                .header("Content-Length", String.valueOf(newBody.contentLength()))
                .body(newBody)
                .build();
    }

    private HashMap<String, String> requestParams(Request request) {
        RequestBody body = request.body();
        String query = requestBodyToString(body);
//        Log.i("http", "request raw: " + query);
        HashMap<String, String> params = queryToMap(query);
        query = request.url().query();
        HashMap<String, String> params2 = queryToMap(query);
        params.putAll(params2);
        return params;
    }

    private String requestBodyToString(RequestBody body) {
        Buffer buffer = new Buffer();
        try {
            body.writeTo(buffer);
        } catch (Exception e) {
            return "";
        }
        String s = buffer.readUtf8();
        buffer.close();
        return s;
    }

    private HashMap<String, String> queryToMap(String query) {
        String[] pairs = query.split("&");
        HashMap<String, String> params = new HashMap<String, String>();
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key, val;
            try {
                key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                val = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
            } catch (Exception e) {
                continue;
            }
            params.put(key, val);
        }
        return params;
    }
}
