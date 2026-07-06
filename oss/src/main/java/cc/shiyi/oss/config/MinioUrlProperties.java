package cc.shiyi.oss.config;

import cc.shiyi.oss.utils.MinioUrlUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 在启动时将 MinIO 配置注入到 MinioUrlUtil 的静态工具方法中。
 * 该组件会被 Jackson 序列化器和代理控制器使用。
 */
@Component
public class MinioUrlProperties {

    @Value("${minio.host}")
    private String host;

    @Value("${minio.port}")
    private int port;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.isHttps}")
    private boolean isHttps;

    @PostConstruct
    public void init() {
        String scheme = isHttps ? "https://" : "http://";
        String baseUrl = scheme + host + ":" + port + "/" + bucket + "/";
        String proxyPath = "/api/v1/oss/file";

        MinioUrlUtil.configure(baseUrl, proxyPath, host, port, bucket, isHttps);
    }
}
