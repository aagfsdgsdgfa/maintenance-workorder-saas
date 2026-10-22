package com.ruoyi.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 配置类
 * API Key 通过环境变量 AI_API_KEY 注入，不写死在配置文件中
 *
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfig {

    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);

    /** API Key */
    private String apiKey;

    /** API 基础地址 */
    private String baseUrl;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        if (apiKey != null && !apiKey.isEmpty()) {
            log.info("AI 配置已加载，Base URL: {}", baseUrl);
        } else {
            log.warn("AI 配置未找到 API Key，请设置环境变量 AI_API_KEY");
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
