package org.example.demo.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hekai
 * @date 2024/3/7
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "s3")
public class StorageProperty {

    private String url;

    private String accessKey;

    private String secretKey;
}
