package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.net.HttpException;
import com.github.xiaolyuh.ui.KbsMsgDialog;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.xiaolyuh.utils.KubesphereUtils.loginAndSaveToken;

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
            int statusCode = response.statusCode();
            if (statusCode == 401) {
                loginAndSaveToken();
                return postForObjectWithToken(url, reqBody, headers, clazz);
            }
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
                loginAndSaveToken();
                return getForObjectWithToken(url, headers, clazz);
            }
            String body = response.body();
            if (clazz == String.class) {
                //noinspection unchecked
                return (T) body;
            }
            return gson.fromJson(body, clazz);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(url, e);
        }
    }

    public static <T> T getForObjectWithTokenUseUrl(String url, Map<String, String> headers, Class<T> clazz) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return getForObjectUseUrl(url, headers, clazz);
    }

    public static <T> void getForObjectWithTokenUseUrl(String url, Map<String, String> headers, Class<T> clazz,
                                                       Consumer<String> consumer, KbsMsgDialog kbsMsgDialog) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        getForObjectUseUrl(url, headers, clazz, consumer, kbsMsgDialog);
    }

    public static <T> T getForObjectUseUrl(String url, Map<String, String> headers, Class<T> clazz) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                loginAndSaveToken();
                return getForObjectWithTokenUseUrl(url, headers, clazz);
            }
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            connection.disconnect();
            String body = new String(bytes);
            if (clazz == String.class) {
                //noinspection unchecked
                return (T) body;
            }
            return gson.fromJson(body, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void getForObjectUseUrl(String url, Map<String, String> headers, Class<T> clazz,
                                              Consumer<String> consumer, KbsMsgDialog kbsMsgDialog) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                loginAndSaveToken();
                getForObjectWithTokenUseUrl(url, headers, clazz);
                return;
            }
            InputStream inputStream = null;
            while (kbsMsgDialog.getInsRefreshOpen()) {
                inputStream = connection.getInputStream();
                byte[] bytes = inputStream.readNBytes(4096);
                if (kbsMsgDialog.getInsRefreshOpen()) {
                    String body = new String(bytes);
                    consumer.accept(body);
                }
            }
            if (inputStream != null) {
                inputStream.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
