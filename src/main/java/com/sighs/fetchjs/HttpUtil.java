package com.sighs.fetchjs;

import net.neoforged.fml.loading.FMLPaths;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static it.unimi.dsi.fastutil.io.TextIO.BUFFER_SIZE;

public class HttpUtil {
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static boolean fetch(String url, Consumer<String> callback) {
        return fetch(url, "GET", null, null, null, 20000, callback);
    }

    public static boolean fetch(
            String url,
            String method,
            Map<String, String> headers,
            String jsonBody,
            Map<String, String> formData,
            int timeoutMillis,
            Consumer<String> callback) {

        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            HttpURLConnection connection = null;
            try {
                URL requestUrl = new URL(url);
                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(timeoutMillis);
                connection.setReadTimeout(timeoutMillis);

                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        connection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }

                if (requiresBody(method)) {
                    connection.setDoOutput(true);
                    String contentType;
                    String bodyContent = "";

                    if (jsonBody != null) {
                        contentType = "application/json; charset=utf-8";
                        bodyContent = jsonBody;
                    } else if (formData != null) {
                        // 介是表单
                        contentType = "application/x-www-form-urlencoded; charset=UTF-8";
                        bodyContent = buildFormData(formData);
                    } else {
                        contentType = "text/plain; charset=UTF-8";
                    }

                    connection.setRequestProperty("Content-Type", contentType);

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(bodyContent.getBytes(StandardCharsets.UTF_8));
                        os.flush();
                    }
                }

                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    // UTF-8，去掉会绝赞乱码
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        callback.accept(response.toString());
                        return true;
                    }
                } else {
                    callback.accept(null);
                    Fetchjs.LOGGER.warn("Unexpected HTTP response: " + responseCode);
                }
            } catch (Exception e) {
                callback.accept(null);
                Fetchjs.LOGGER.warn("HTTP request failed: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return false;
        }, executor);

        return true;
    }

    private static boolean requiresBody(String method) {
        return "POST".equalsIgnoreCase(method) ||
                "PUT".equalsIgnoreCase(method) ||
                "PATCH".equalsIgnoreCase(method);
    }

    private static String buildFormData(Map<String, String> formData) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            // 对键和值进行 URL 编码
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    public static boolean download(String url, String path, Consumer<Double> progressCallback) {
        return download(url, path, null, 20000, progressCallback);
    }

    public static boolean download(
            String url,
            String path,
            Map<String, String> headers,
            int timeoutMillis,
            Consumer<Double> progressCallback) {
        if (url == null || url.isEmpty() || path == null || path.isEmpty()) {
            return false;
        }

        CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                URL downloadUrl = new URL(url);
                connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeoutMillis);
                connection.setReadTimeout(timeoutMillis);

                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        connection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }

                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {

                    Path filePath = FMLPaths.GAMEDIR.get().resolve(path);
                    Files.createDirectories(filePath.getParent());

                    // 获取内容长度用于进度显示
                    long contentLength = connection.getContentLengthLong();
                    long bytesDownloaded = 0;

                    try {
                        inputStream = new BufferedInputStream(connection.getInputStream());
                        outputStream = new BufferedOutputStream(new FileOutputStream(path));

                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            bytesDownloaded += bytesRead;

                            if (progressCallback != null) {
                                progressCallback.accept(1d * bytesDownloaded / contentLength);
                            }
                        }
                        outputStream.flush();

                        Fetchjs.LOGGER.info("File downloaded successfully: " + path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Fetchjs.LOGGER.warn("Download failed. HTTP response: " + responseCode);
                }
            } catch (Exception e) {
                Fetchjs.LOGGER.warn("Download failed: " + e.getMessage());

                // 删除可能已部分下载的文件
                try {
                    Files.deleteIfExists(Paths.get(path));
                } catch (IOException ex) {
                    Fetchjs.LOGGER.warn("Failed to delete partial download: " + ex.getMessage());
                }
            } finally {
                // 关闭
                try {
                    if (inputStream != null) inputStream.close();
                } catch (IOException ignored) {
                }
                try {
                    if (outputStream != null) outputStream.close();
                } catch (IOException ignored) {
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }, executor);

        return true;
    }
}