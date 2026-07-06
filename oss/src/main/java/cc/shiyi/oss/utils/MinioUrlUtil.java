package cc.shiyi.oss.utils;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 用于处理 MinIO URL 的静态工具类。
 * 配置值由 {@link cc.shiyi.oss.config.MinioUrlProperties} 在启动时通过 {@link #configure} 注入。
 */
public class MinioUrlUtil {

    private static String baseUrl;
    private static String proxyPath;
    private static String host;
    private static int port;
    private static String bucket;
    private static boolean isHttps;

    private MinioUrlUtil() {
        // 工具类
    }

    /**
     * 由 MinioUrlProperties 在启动时调用，以注入配置。
     */
    public static void configure(String baseUrl, String proxyPath,
                                  String host, int port, String bucket, boolean isHttps) {
        MinioUrlUtil.baseUrl = baseUrl;
        MinioUrlUtil.proxyPath = proxyPath;
        MinioUrlUtil.host = host;
        MinioUrlUtil.port = port;
        MinioUrlUtil.bucket = bucket;
        MinioUrlUtil.isHttps = isHttps;
    }

    /**
     * 将直接的 MinIO URL 转换为后端代理 URL。
     * 若 URL 以配置的 MinIO 基础前缀开头，则返回代理 URL；否则原样返回。
     */
    public static String toProxyUrl(String directUrl) {
        if (directUrl == null || directUrl.isEmpty()) {
            return directUrl;
        }
        if (baseUrl == null) {
            // 尚未初始化配置 — 原样返回
            return directUrl;
        }
        if (!directUrl.startsWith(baseUrl)) {
            return directUrl;
        }
        String encoded = URLEncoder.encode(directUrl, StandardCharsets.UTF_8);
        return proxyPath + "?url=" + encoded;
    }

    /**
     * 将完整的 MinIO URL（或已包装的代理 URL）解析为 [bucket, objectName]。
     * 验证 URL 的 host 和 port 是否与已配置的 MinIO 端点匹配。
     * 若传入的已是代理 URL（/api/v1/oss/file?url=...），则自动提取内层直连 URL 后再解析。
     *
     * @param url 完整的 MinIO URL（例如 http://host:port/bucket/objectName）或代理 URL
     * @return [bucket, objectName]
     * @throws IllegalArgumentException 若 URL 无效或 host/port 不匹配
     */
    public static String[] resolveBucketAndObject(String url) {
        if (baseUrl == null) {
            throw new IllegalStateException("MinioUrlUtil 尚未由 MinioUrlProperties 初始化");
        }

        // 若传入的已是代理 URL，提取内层直连 URL 后再解析（仅解包一层）
        if (url != null && url.startsWith(proxyPath + "?url=")) {
            String innerUrl;
            try {
                innerUrl = java.net.URLDecoder.decode(
                        url.substring((proxyPath + "?url=").length()), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new IllegalArgumentException("无法解码代理 URL 中的内层参数: " + url, e);
            }
            // 递归解析内层 URL（内层必为直连 URL，不会再次匹配 proxyPath）
            return resolveBucketAndObject(innerUrl);
        }

        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的 URL: " + url, e);
        }

        // 验证 scheme
        String expectedScheme = isHttps ? "https" : "http";
        if (!expectedScheme.equals(uri.getScheme())) {
            throw new IllegalArgumentException(
                    "URL scheme 不匹配: 期望 " + expectedScheme + "，实际 " + uri.getScheme());
        }

        // 验证 host
        if (!host.equals(uri.getHost())) {
            throw new IllegalArgumentException(
                    "URL host 不匹配: 期望 " + host + "，实际 " + uri.getHost());
        }

        // 验证 port（默认端口按 scheme 处理）
        int expectedPort = port;
        int actualPort = uri.getPort();
        if (actualPort == -1) {
            actualPort = isHttps ? 443 : 80;
        }
        if (expectedPort != actualPort) {
            throw new IllegalArgumentException(
                    "URL port 不匹配: 期望 " + expectedPort + "，实际 " + actualPort);
        }

        // 解析路径: /bucket/object/name
        String path = uri.getPath();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            throw new IllegalArgumentException("URL 路径中缺少 bucket/objectName: " + url);
        }

        // 去除开头的 /
        path = path.startsWith("/") ? path.substring(1) : path;

        int slashIndex = path.indexOf('/');
        if (slashIndex == -1) {
            // 路径形如 "bucket" — 没有对象名
            throw new IllegalArgumentException("URL 路径中缺少对象名: " + url);
        }

        String bucketFromUrl = path.substring(0, slashIndex);
        String objectName = path.substring(slashIndex + 1);

        if (objectName.isEmpty()) {
            throw new IllegalArgumentException("URL 路径中缺少对象名: " + url);
        }

        return new String[]{bucketFromUrl, objectName};
    }
}
