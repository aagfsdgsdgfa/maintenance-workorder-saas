package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.AiDailyReportResponse;

public interface IAiService {

    /**
     * 语音转文字（AI网关）
     * @param audioUrl 录音文件的URL
     * @return 识别后的文字
     */
    String voiceToText(String audioUrl);

    /**
     * AI生成日报（聚合当日数据）
     * @param engineerId 工程师ID
     * @param date 日期（yyyy-MM-dd）
     * @return AI生成的日报内容
     */
    AiDailyReportResponse generateDailyReport(Long engineerId, String date);
}
