package org.example.demo.minio.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.example.demo.minio.config.MinioClientConfig;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.minio.DeleteBucketEncryptionArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hekai
 * @date 2024/3/7
 */
@Slf4j
public class MinioUtil {

    /**
     * 文件上传
     * @param file
     * @param file_name
     * @param bucket_name
     */
    public static boolean minioUpload(MultipartFile file, String file_name, String bucket_name) {
        boolean is_upload_success = false;
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        if (file_name == null || file_name.isEmpty()) {
            file_name = file.getOriginalFilename();
            file_name = file_name.replaceAll(" ", "_");
        }
        InputStream input_stream = null;
        try {
            input_stream = file.getInputStream();
        } catch (IOException e) {
            log.error("minio upload fail, ", e);
        }
        PutObjectArgs object = PutObjectArgs.builder() //
            .bucket(bucket_name) //
            .object(file_name) //
            .stream(input_stream, file.getSize(), -1) //
            .contentType(file.getContentType()) //
            .build();
        try {
            // 若文件已存在，会覆盖原文件
            ObjectWriteResponse response = minio_client.putObject(object);
            System.out.println(response.object());
            is_upload_success = true;
        } catch (Exception e) {
            log.error("minio upload fail, ", e);
        }
        return is_upload_success;
    }

    /**
     * 判断桶是否存在
     * @param bucket_name
     * @return
     */
    public boolean bucketExists(String bucket_name) {
        return MinioClientConfig.isBucketExists(bucket_name);
    }

    public InputStream getFileInputStream(String file_name, String bucket_name) {
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        try {
            return minio_client.getObject(GetObjectArgs.builder().bucket(bucket_name).object(file_name).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean createBucket(String bucket_name) {
        boolean is_bucket_create_success = false;
        if (bucket_name == null || bucket_name.isEmpty()) {
            return is_bucket_create_success;
        }
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        boolean is_bucket_existed = MinioClientConfig.isBucketExists(bucket_name);
        if (is_bucket_existed) {
            return true;
        } else {
            try {
                minio_client.makeBucket(MakeBucketArgs.builder().bucket(bucket_name).build());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static boolean minioDownload(String bucket_name, String original_name, String file_save_path) {
        boolean is_download_success = false;
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        try (
            InputStream input_stream =
                minio_client.getObject(GetObjectArgs.builder().bucket(bucket_name).object(original_name).build());
            FileOutputStream output_stream = new FileOutputStream(file_save_path)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input_stream.read(buffer)) > 0) {
                output_stream.write(buffer, 0, len);
            }
            is_download_success = true;
        } catch (Exception e) {
            log.error("down error: ", e);
        }
        return is_download_success;
    }

    public void deleteBucket(String bucket_name) {
        if (StringUtils.isEmpty(bucket_name)) {
            return;
        }

        MinioClient minio_client = MinioClientConfig.getMinioClient();
        boolean is_bucket_existed = MinioClientConfig.isBucketExists(bucket_name);
        if (is_bucket_existed) {
            try {
                minio_client.removeBucket(RemoveBucketArgs.builder().bucket(bucket_name).build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void deleteFile(String bucket_name, String file_name) {
        if (StringUtils.isEmpty(bucket_name)) {
            return;
        }

        MinioClient minio_client = MinioClientConfig.getMinioClient();
        boolean is_bucket_existed = MinioClientConfig.isBucketExists(bucket_name);
        if (is_bucket_existed) {
            try {
                minio_client.removeObject(RemoveObjectArgs.builder().bucket(bucket_name).object(file_name).build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteBucketAllFiles(String bucket_name) {
        if (StringUtils.isEmpty(bucket_name)) {
            return;
        }

        MinioClient minio_client = MinioClientConfig.getMinioClient();
        boolean is_bucket_existed = MinioClientConfig.isBucketExists(bucket_name);
        if (is_bucket_existed) {
            try {
                minio_client.deleteBucketEncryption(DeleteBucketEncryptionArgs.builder().bucket(bucket_name).build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getPreviewFileUrl(String bucket_name, String file_name) {
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        try {
            return minio_client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder().bucket(bucket_name).object(file_name).method(Method.GET).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
