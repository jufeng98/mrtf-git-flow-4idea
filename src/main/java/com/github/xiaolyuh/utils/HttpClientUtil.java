package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.HttpException;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * HttpClient工具
 *
 * @author yuhao.wang3
 */
public class HttpClientUtil {
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build();
    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    /**
     * 发起 application/json 的 post 请求
     *
     * @param url   地址
     * @param param 参数
     */
    public static <T> void postApplicationJson(String url, Object param, Class<T> clazz) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        postForObject(url, gson.toJson(param), headers, clazz);
    }

    public static <T> T postJsonForObjectWithToken(String url, String reqBody, Map<String, String> headers, Class<T> clazz) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return postForObjectWithToken(url, reqBody, headers, clazz);
    }

    public static <T> T postForObjectWithToken(String url, String reqBody, Map<String, String> headers, Class<T> clazz) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return postForObject(url, reqBody, headers, clazz);
    }

    public static <T> T postForObject(String url, String reqBody, Map<String, String> headers, Class<T> clazz) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(reqBody, StandardCharsets.UTF_8))
                .uri(URI.create(url));
        if (headers != null) {
            headers.forEach(builder::setHeader);
        }
        HttpRequest request = builder.build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            return gson.fromJson(body, clazz);
        } catch (Exception e) {
            throw new RuntimeException(url, e);
        }
    }

    public static <T> T getForObjectWithToken(String url, Map<String, String> headers, Class<T> clazz) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return getForObject(url, headers, clazz);
    }

    public static <T> T getForObject(String url, Map<String, String> headers, Class<T> clazz) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url));
        if (headers != null) {
            headers.forEach(builder::setHeader);
        }
        HttpRequest request = builder.build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 401) {
                throw new HttpException(401, "token失效");
            }
            String body = response.body();
            return gson.fromJson(body, clazz);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(url, e);
        }
    }
}
