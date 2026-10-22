package com.ruoyi.zw.domain;

/**
 * AI 语音转文字响应
 */
public class AiVoiceToTextResponse {
    private String text;
    private String language;
    private Double confidence;

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
