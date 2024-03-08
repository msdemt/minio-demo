package org.example.demo.minio.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hekai
 * @date 2024/3/7
 */
@Slf4j
@Configuration
public class MinioClientConfig {

    @Autowired
    private StorageProperty storage_property;

    private static MinioClient minio_client;

    public static MinioClient getMinioClient() {
        return minio_client;
    }

    public static boolean isBucketExists(String bucket_name) {
        try {
            return minio_client.bucketExists(BucketExistsArgs.builder().bucket(bucket_name).build());
        } catch (Exception e) {
            log.error("查询 bucket 异常：", e);
            return false;
        }
    }

    public static List<Bucket> getAllBuckets() {
        try {
            return minio_client.listBuckets();
        } catch (Exception e) {
            log.error("查询 bucket 异常：", e);
            return null;
        }
    }

    @PostConstruct
    public void init() {
        try {
            minio_client = MinioClient.builder() //
                .endpoint(storage_property.getUrl()) //
                .credentials(storage_property.getAccessKey(), storage_property.getSecretKey()) //
                .build();
        } catch (Exception e) {
            log.error("初始化 minio 异常：", e);
        }

    }
}
