package org.example.demo.minio.controller;

import javax.servlet.http.HttpServletResponse;

import org.example.demo.minio.config.MinioClientConfig;
import org.example.demo.minio.utils.MinioUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;

/**
 * @author hekai
 * @date 2024/3/8
 */
@RestController
@RequestMapping("/minio")
public class MinioController {

    @PostMapping("/upload")
    public void uploadFile(@RequestBody MultipartFile file) {
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        boolean is_upload_success = MinioUtil.minioUpload(file, "", "test");
    }

    @GetMapping("/preview")
    public String getFilePreviewUrl(@RequestParam String file_name) {
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        String url = MinioUtil.getPreviewFileUrl("test", file_name);
        return url;
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam String file_name, HttpServletResponse response) {
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        MinioUtil.minioDownload("test", file_name, "/home/hekai/" + file_name);
    }

    @DeleteMapping("/delete")
    public String deleteFile(String file_name) {
        MinioClient minio_client = MinioClientConfig.getMinioClient();
        MinioUtil.deleteFile("test", file_name);
        return "";
    }

}
