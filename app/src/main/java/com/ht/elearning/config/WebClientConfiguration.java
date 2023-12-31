package com.ht.elearning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Value("${file-server.master.host}")
    private String fileServerMasterHost;
    @Value("${file-server.master.port}")
    private Integer fileServerMasterPort;
    @Value("${file-server.volume.host}")
    private String fileServerVolumeHost;
    @Value("${file-server.volume.port}")
    private Integer fileServerVolumePort;

    @Bean
    public WebClient fileMasterApiClient() {
        return WebClient.create("http://" + fileServerMasterHost + ":" + fileServerMasterPort);
    }

    @Bean
    public WebClient fileVolumeApiClient() {
        return WebClient.create("http://" + fileServerVolumeHost + ":" + fileServerVolumePort);
    }
}
