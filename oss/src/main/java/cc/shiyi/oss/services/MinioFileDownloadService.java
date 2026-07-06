package cc.shiyi.oss.services;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 从 MinIO 下载/流式传输对象文件的服务。
 */
@Service
public class MinioFileDownloadService {

    @Autowired
    private MinioClient minioClient;

    /**
     * 从 MinIO 获取对象，返回包含对象数据流和头信息的响应。
     *
     * @param bucket     存储桶名称
     * @param objectName 对象路径/名称
     * @return GetObjectResponse（InputStream + headers）
     * @throws Exception 若对象不存在或发生其他 MinIO 错误
     */
    public GetObjectResponse getObject(String bucket, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build());
    }
}
