package com.ruoyi.zw.config;

import org.springframework.context.annotation.Configuration;

/**
 * AI HTTP client configuration placeholder.
 * RestTemplate/RestClient will be used directly via HttpURLConnection if spring-web is not available.
 */
@Configuration
public class RestTemplateConfig {
    // No beans required - using HttpURLConnection directly in AiServiceImpl
}
