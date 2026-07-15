package com.shrooms.scaffold.inspection;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class InspectionClientConfig {

    private static final String API_KEY_HEADER = "X-Api-Key";

    @Value("${inspection.service.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor inspectionApiKeyInterceptor() {
        return requestTemplate ->
                requestTemplate.header(API_KEY_HEADER, apiKey);
    }
}