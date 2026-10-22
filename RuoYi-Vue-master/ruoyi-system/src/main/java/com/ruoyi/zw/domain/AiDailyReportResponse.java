package com.ruoyi.zw.domain;

import java.util.List;

/**
 * AI 日报生成响应
 */
public class AiDailyReportResponse {
    private String title;
    private String summary;
    private List<ReportSection> sections;

    public static class ReportSection {
        private String title;
        private String content;

        public ReportSection() {}

        public ReportSection(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<ReportSection> getSections() { return sections; }
    public void setSections(List<ReportSection> sections) { this.sections = sections; }
}
