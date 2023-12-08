package com.github.xiaolyuh.utils;

import com.alibaba.fastjson.JSON;
import com.github.xiaolyuh.HttpException;
import com.google.common.collect.Maps;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.xiaolyuh.utils.ConfigUtil.PREFERENCES;

/**
 * OkHttpClient工具
 *
 * @author yuhao.wang3
 */
public abstract class OkHttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpClientUtil.class);

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    /**
     * 发起 application/json 的 post 请求
     *
     * @param url           地址
     * @param param         参数
     */
    public static <T> void postApplicationJson(String url, Object param, Class<T> clazz) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , JSON.toJSONString(param));
        post(url, requestBody, null, clazz);
    }

    public static <T> T postWithToken(String url, RequestBody requestBody, Map<String, String> headers, Class<T> clazz) {
        String kubesphereToken = PREFERENCES.get("kubesphereToken", "");
        if (headers == null) {
            headers = Maps.newHashMap();
            headers.put("Cookie", "token=" + kubesphereToken);
        } else {
            headers.put("Cookie", "token=" + kubesphereToken);
        }
        return post(url, requestBody, headers, clazz);
    }

    /**
     * 发起post请求，不做任何签名
     *
     * @param url           发送请求的URL
     * @param requestBody   请求体
     */
    public static <T> T post(String url, RequestBody requestBody, Map<String, String> headers, Class<T> clazz) {
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        if (headers != null) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Request request = builder.build();

        Response response = null;
        String result;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new HttpException(response.code(), response.message());
            }
            result = response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(url, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return JSON.parseObject(result, clazz);
    }

    public static <T> T getWithToken(String url, Map<String, String> headers, Class<T> clazz) {
        String kubesphereToken = PREFERENCES.get("kubesphereToken", "");
        if (headers == null) {
            headers = Maps.newHashMap();
            headers.put("Cookie", "token=" + kubesphereToken);
        } else {
            headers.put("Cookie", "token=" + kubesphereToken);
        }
        return get(url, headers, clazz);
    }

    public static <T> T get(String url, Map<String, String> headers, Class<T> clazz) {
        Request.Builder builder = new Request.Builder().url(url).get();
        if (headers != null) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Request request = builder.build();
        Response response = null;
        String result;
        try {
            Call call = okHttpClient.newCall(request);
            response = call.execute();
            if (!response.isSuccessful()) {
                throw new HttpException(response.code(), response.message());
            }
            result = response.body().string();
        }  catch (Exception e) {
            throw new RuntimeException(url, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return JSON.parseObject(result, clazz);
    }

}
