package com.github.xiaolyuh.utils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * HttpClient工具
 *
 * @author yuhao.wang3
 */
public class HttpClientUtil {
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
    public static <T> void postApplicationJson(String url, Object param, Class<T> resType, Project project) throws Exception {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        postForObject(url, gson.toJson(param), headers, resType, project);
    }

    public static <T> T postJsonForObjectWithToken(String url, String reqBody, Map<String, String> headers,
                                                   Class<T> resType, Project project) throws Exception {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return postForObjectWithToken(url, reqBody, headers, resType, project);
    }

    public static <T> T postForObjectWithToken(String url, String reqBody, Map<String, String> headers,
                                               Class<T> resType, Project project) throws Exception {
        String kubesphereToken = ConfigUtil.getKubesphereToken(project);
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return postForObject(url, reqBody, headers, resType, project);
    }

    public static <T> T postForObject(String url, String reqBody, Map<String, String> headers,
                                      Class<T> resType, Project project) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(reqBody, StandardCharsets.UTF_8))
                .uri(URI.create(url));
        return reqForObject(builder, headers, resType, project);
    }

    public static <T> T getForObjectWithToken(String url, Map<String, String> headers,
                                              Class<T> resType, Project project) throws Exception {
        String kubesphereToken = ConfigUtil.getKubesphereToken(project);
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return getForObject(url, headers, resType, project);
    }

    public static <T> T getForObject(String url, Map<String, String> headers, Class<T> resType,
                                     Project project) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url));
        return reqForObject(builder, headers, resType, project);
    }

    @SuppressWarnings("unchecked")
    public static <T> T reqForObject(HttpRequest.Builder builder, Map<String, String> headers,
                                     Class<T> resType, Project project) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();

        if (headers != null) {
            headers.forEach(builder::setHeader);
        }

        HttpRequest request = builder.build();
        HttpResponse<T> response;
        if (resType == byte[].class) {
            response = (HttpResponse<T>) client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } else {
            response = (HttpResponse<T>) client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        int statusCode = response.statusCode();
        if (statusCode == 404) {
            throw new RuntimeException("链接 404:" + request.uri());
        }

        if (statusCode == 401 || statusCode == 403) {
            if (KubesphereUtils.isLoginUrl(request.uri().toString(), project)) {
                throw new RuntimeException("用户名或密码无效," + response.body());
            }

            KubesphereUtils.loginAndSaveToken(project);
            return getForObjectWithToken(request.uri().toString(), headers, resType, project);
        }

        List<String> cookies = response.headers().allValues("set-cookie");
        JsonObject resJson = handleGroupLogin(request.uri().toString(), project, statusCode, cookies, response.body() + "");
        if (resJson != null) {
            return (T) resJson;
        }

        T body = response.body();

        if (resType == String.class || resType == byte[].class) {
            return body;
        }

        return gson.fromJson((String) body, resType);
    }

    private static @Nullable JsonObject handleGroupLogin(String url, Project project, int status, List<String> cookies, String body) {
        boolean isGroupLoginUrl = KubesphereUtils.isLoginUrl(url, project) && ConfigUtil.isGroupKubesphere(project);
        if (isGroupLoginUrl && status == 200) {
            throw new RuntimeException("用户名或密码无效," + body);
        } else if (isGroupLoginUrl && status == 302) {
            String accessToken = KubesphereUtils.getTokenFromResponseCookie(cookies);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("access_token", accessToken);
            return jsonObject;
        }

        return null;
    }

    public static <T> T getForObjectWithTokenUseUrl(String url, Map<String, String> headers,
                                                    Class<T> resType, Project project) throws Exception {
        String kubesphereToken = ConfigUtil.getKubesphereToken(project);
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        return getForObjectUseUrl(url, headers, resType, project);
    }

    public static <T> void getForObjectWithTokenUseUrl(String url, Map<String, String> headers, Class<T> resType,
                                                       Consumer<T> consumer, Project project) throws Exception {
        String kubesphereToken = ConfigUtil.getKubesphereToken(project);
        if (headers == null) {
            headers = Maps.newHashMap();
        }
        headers.put("Cookie", "token=" + kubesphereToken);
        getForObjectUseUrl(url, headers, resType, consumer, project);
    }

    public static <T> T getForObjectUseUrl(String url, Map<String, String> headers,
                                           Class<T> resType, Project project) throws Exception {
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

            if (responseCode == 404) {
                throw new RuntimeException("链接 404:" + url);
            }

            if (responseCode == 401 || responseCode == 403) {
                if (KubesphereUtils.isLoginUrl(url, project)) {
                    inputStream = connection.getInputStream();
                    byte[] bytes = inputStream.readAllBytes();
                    String body = new String(bytes);
                    throw new RuntimeException("用户名或密码无效," + body);
                }

                KubesphereUtils.loginAndSaveToken(project);
                return getForObjectWithTokenUseUrl(url, headers, resType, project);
            }

            List<String> cookies = connection.getHeaderFields().get("set-cookie");
            JsonObject resJson = handleGroupLogin(url, project, responseCode, cookies, "请检查Kubesphere配置");
            if (resJson != null) {
                //noinspection unchecked
                return (T) resJson;
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
                                              Consumer<T> consumer, Project project) throws Exception {
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

            if (responseCode == 404) {
                throw new RuntimeException("链接 404:" + url);
            }

            if (responseCode == 401 || responseCode == 403) {
                if (KubesphereUtils.isLoginUrl(url, project)) {
                    inputStream = connection.getInputStream();
                    byte[] bytes = inputStream.readAllBytes();
                    String body = new String(bytes);
                    throw new RuntimeException("用户名或密码无效," + body);
                }

                KubesphereUtils.loginAndSaveToken(project);
                getForObjectWithTokenUseUrl(url, headers, resType, project);
                return;
            }

            List<String> cookies = connection.getHeaderFields().get("set-cookie");
            handleGroupLogin(url, project, responseCode, cookies, "请检查Kubesphere配置");

            while (!Thread.currentThread().isInterrupted()) {
                inputStream = connection.getInputStream();
                byte[] bytes = inputStream.readNBytes(4096);
                if (!Thread.currentThread().isInterrupted()) {
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
