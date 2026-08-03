package com.shrooms.scaffold.inspection;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InspectionClientConfigTest {

    @Test
    public void inspectionApiKeyInterceptor_shouldAddApiKeyHeader() {
        InspectionClientConfig config = new InspectionClientConfig();

        ReflectionTestUtils.setField(config, "apiKey", "test-key");

        RequestInterceptor interceptor = config.inspectionApiKeyInterceptor();

        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertTrue(template.headers().containsKey("X-Api-Key"));
        assertTrue(template.headers().get("X-Api-Key").contains("test-key"));
    }
}
