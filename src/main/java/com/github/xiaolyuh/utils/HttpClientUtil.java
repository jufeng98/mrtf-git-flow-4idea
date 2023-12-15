package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.net.HttpException;
import com.github.xiaolyuh.ui.KbsMsgDialog;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
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
    public static <T> void postApplicationJson(String url, Object param, Class<T> resType) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        postForObject(url, gson.toJson(param), headers, resType);
    }

    public static <T> T postJsonForObjectWithToken(String url, String reqBody, Map<String, String> headers, Class<T> resType) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return postForObjectWithToken(url, reqBody, headers, resType);
    }

    public static <T> T postForObjectWithToken(String url, String reqBody, Map<String, String> headers, Class<T> resType) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return postForObject(url, reqBody, headers, resType);
    }

    public static <T> T postForObject(String url, String reqBody, Map<String, String> headers, Class<T> resType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(reqBody, StandardCharsets.UTF_8))
                .uri(URI.create(url));
        return reqForObject(builder, headers, resType);
    }

    public static <T> T getForObjectWithToken(String url, Map<String, String> headers, Class<T> resType) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return getForObject(url, headers, resType);
    }

    public static <T> T getForObject(String url, Map<String, String> headers, Class<T> resType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url));
        return reqForObject(builder, headers, resType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T reqForObject(HttpRequest.Builder builder, Map<String, String> headers, Class<T> resType) {
        if (headers != null) {
            headers.forEach(builder::setHeader);
        }
        HttpRequest request = builder.build();
        HttpResponse<T> response;
        try {
            if (resType == byte[].class) {
                response = (HttpResponse<T>) client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            } else {
                response = (HttpResponse<T>) client.send(request, HttpResponse.BodyHandlers.ofString());
            }
            if (response.statusCode() == 401) {
                loginAndSaveToken();
                return getForObjectWithToken(request.uri().toString(), headers, resType);
            }
            T body = response.body();
            if (resType == String.class || resType == byte[].class) {
                return body;
            }
            return gson.fromJson((String) body, resType);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(request.uri().toString(), e);
        }
    }

    public static <T> T getForObjectWithTokenUseUrl(String url, Map<String, String> headers, Class<T> resType) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return getForObjectUseUrl(url, headers, resType);
    }

    public static <T> void getForObjectWithTokenUseUrl(String url, Map<String, String> headers, Class<T> resType,
                                                       Consumer<T> consumer, KbsMsgDialog kbsMsgDialog) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        getForObjectUseUrl(url, headers, resType, consumer, kbsMsgDialog);
    }

    public static <T> T getForObjectUseUrl(String url, Map<String, String> headers, Class<T> resType) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                loginAndSaveToken();
                return getForObjectWithTokenUseUrl(url, headers, resType);
            }
            inputStream = connection.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            if (resType == byte[].class) {
                return (T) bytes;
            }
            String body = new String(bytes);
            if (resType == String.class) {
                //noinspection unchecked
                return (T) body;
            }
            return gson.fromJson(body, resType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static <T> void getForObjectUseUrl(String url, Map<String, String> headers, Class<T> resType,
                                              Consumer<T> consumer, KbsMsgDialog kbsMsgDialog) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                loginAndSaveToken();
                getForObjectWithTokenUseUrl(url, headers, resType);
                return;
            }
            while (kbsMsgDialog.getInsRefreshOpen()) {
                inputStream = connection.getInputStream();
                byte[] bytes = inputStream.readNBytes(4096);
                if (kbsMsgDialog.getInsRefreshOpen()) {
                    if (resType == byte[].class) {
                        consumer.accept((T) bytes);
                    } else if (resType == String.class) {
                        String body = new String(bytes);
                        //noinspection unchecked
                        consumer.accept((T) body);
                    } else {
                        String body = new String(bytes);
                        consumer.accept(gson.fromJson(body, resType));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
