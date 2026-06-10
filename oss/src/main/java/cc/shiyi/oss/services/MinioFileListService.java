package cc.shiyi.oss.services;

import cc.shiyi.oss.services.func.MinioFileListFunc;
import cc.shiyi.oss.items.FileItem;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shiyi
 *
 */
@Slf4j
@Service
public class MinioFileListService {

    @Value("${minio.bucket}")
    String bucket;

	@Value("${minio.host}")
	String host;

	@Value("${minio.isHttps}")
	boolean isHttps;

	@Value("${minio.port}")
	String port;

    @Autowired
    MinioClient minioClient;
    
	public List<FileItem> listFile() throws Exception {
		MinioFileListFunc func = MinioFileListFunc.builder()
				.host(host)
				.bucket(bucket)
				.isHttps(isHttps)
				.minioClient(minioClient)
				.port(port)
				.build();
		return func.listFile();
	}

	public List<FileItem> listFile(String folder) throws Exception {
		MinioFileListFunc func = MinioFileListFunc.builder()
				.host(host)
				.bucket(bucket)
				.isHttps(isHttps)
				.minioClient(minioClient)
				.port(port)
				.build();
		return func.listFile(folder);
	}

}
