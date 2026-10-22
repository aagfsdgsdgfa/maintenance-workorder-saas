package com.ruoyi.zw.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.config.AiConfig;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zw.domain.AiDailyReportResponse;
import com.ruoyi.zw.domain.ZwCheckIn;
import com.ruoyi.zw.domain.ZwDailyReport;
import com.ruoyi.zw.domain.ZwFaultRecord;
import com.ruoyi.zw.domain.ZwInspectionResult;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.enums.WorkOrderStatusEnum;
import com.ruoyi.zw.mapper.ZwCheckInMapper;
import com.ruoyi.zw.mapper.ZwDailyReportMapper;
import com.ruoyi.zw.mapper.ZwFaultRecordMapper;
import com.ruoyi.zw.mapper.ZwInspectionResultMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.service.IAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AiServiceImpl implements IAiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired(required = false)
    private AiConfig aiConfig;

    @Autowired
    private ZwCheckInMapper checkInMapper;

    @Autowired
    private ZwWorkOrderMapper workOrderMapper;

    @Autowired
    private ZwInspectionResultMapper inspectionResultMapper;

    @Autowired
    private ZwFaultRecordMapper faultRecordMapper;

    @Autowired
    private ZwDailyReportMapper dailyReportMapper;

    @Override
    public String voiceToText(String audioUrl) {
        if (aiConfig == null || StringUtils.isEmpty(aiConfig.getApiKey())) {
            log.warn("AI API Key 未配置，无法进行语音转文字");
            return "[AI未配置，请设置环境变量 AI_API_KEY]";
        }

        HttpURLConnection conn = null;
        try {
            String endpoint = aiConfig.getBaseUrl() + "/v1/audio/transcriptions";
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + aiConfig.getApiKey());
            conn.setDoOutput(true);

            String requestBody = String.format(
                "{\"model\":\"whisper-1\",\"file_url\":\"%s\"}", audioUrl);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }
                String resp = response.toString();
                if (StringUtils.isNotEmpty(resp)) {
                    JsonNode node = OBJECT_MAPPER.readTree(resp);
                    String text = node.has("text") ? node.get("text").asText() : resp;
                    log.info("语音转文字成功: {}", text);
                    return text;
                }
            } else {
                log.error("语音转文字请求失败，HTTP状态码: {}", responseCode);
            }
        } catch (Exception e) {
            log.error("语音转文字失败", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return "";
    }

    @Override
    public AiDailyReportResponse generateDailyReport(Long engineerId, String date) {
        AiDailyReportResponse response = new AiDailyReportResponse();

        try {
            String username = SecurityUtils.getUsername();

            List<ZwCheckIn> checkIns = checkInMapper.selectByEngineerAndDate(engineerId, date);
            List<ZwWorkOrder> workOrders = workOrderMapper.selectByEngineerAndDate(engineerId, date);
            List<ZwInspectionResult> inspections = inspectionResultMapper.selectByEngineerAndDate(engineerId, date);
            List<ZwFaultRecord> faults = faultRecordMapper.selectByEngineerAndDate(username, date);

            int checkInCount = checkIns.size();
            int workOrderCount = workOrders.size();
            int inspectionCount = inspections.size();
            int faultCount = faults.size();

            // 构建日报数据摘要
            StringBuilder dataSummary = new StringBuilder();
            dataSummary.append(String.format("日期：%s，工程师：%s\n", date, username));
            dataSummary.append(String.format("打卡次数：%d\n", checkInCount));
            dataSummary.append(String.format("工单处理：%d个\n", workOrderCount));
            if (!workOrders.isEmpty()) {
                dataSummary.append("工单详情：\n");
                for (ZwWorkOrder wo : workOrders) {
                    dataSummary.append(String.format("  - [%s] %s (状态：%s)\n",
                        wo.getOrderNo(), wo.getTitle(), translateStatus(wo.getStatus())));
                }
            }
            dataSummary.append(String.format("巡检完成：%d个点\n", inspectionCount));
            dataSummary.append(String.format("故障处理：%d条\n", faultCount));
            if (!faults.isEmpty()) {
                dataSummary.append("故障详情：\n");
                for (ZwFaultRecord f : faults) {
                    dataSummary.append(String.format("  - %s：%s\n",
                        f.getFaultType(), f.getFaultDesc()));
                }
            }

            // 构建 prompt 尝试调用 AI
            String aiContent = callAiReport(date, username, dataSummary.toString());

            // AI 调用成功时使用 AI 内容，否则降级到模板
            List<AiDailyReportResponse.ReportSection> sections = new ArrayList<>();
            if (aiContent != null && !aiContent.isEmpty()) {
                String[] lines = aiContent.split("\n");
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    int colonIdx = line.indexOf("：");
                    if (colonIdx > 0 && colonIdx < line.length() - 1) {
                        sections.add(new AiDailyReportResponse.ReportSection(
                            line.substring(0, colonIdx + 1),
                            line.substring(colonIdx + 1).trim()
                        ));
                    } else {
                        sections.add(new AiDailyReportResponse.ReportSection("工作内容", line.trim()));
                    }
                }
            }

            // AI 未返回有效内容时降级到模板
            if (sections.isEmpty()) {
                sections.add(new AiDailyReportResponse.ReportSection("一、签到记录",
                    checkIns.isEmpty() ? "今日无签到记录" : String.format("今日共签到%d次。", checkInCount)));
                sections.add(new AiDailyReportResponse.ReportSection("二、工单处理",
                    workOrders.isEmpty() ? "今日无工单处理" : String.format("今日共完成工单%d个。", workOrderCount)));
                sections.add(new AiDailyReportResponse.ReportSection("三、巡检情况",
                    inspections.isEmpty() ? "今日无巡检记录" : String.format("今日共完成巡检点%d个。", inspectionCount)));
                sections.add(new AiDailyReportResponse.ReportSection("四、故障处理",
                    faults.isEmpty() ? "今日无故障上报" : String.format("今日共处理故障%d条。", faultCount)));
            }

            response.setTitle(username + " " + date + " 维保日报");
            response.setSummary(String.format("今日共签到%d次，完成工单%d个，巡检设备%d台，处理故障%d条。",
                checkInCount, workOrderCount, inspectionCount, faultCount));
            response.setSections(sections);

            log.info("日报生成成功：{} - {}个工单，{}个巡检", date, workOrderCount, inspectionCount);

        } catch (Exception e) {
            log.error("日报生成失败", e);
            response.setTitle("日报-" + date);
            response.setSummary("日报生成失败，请稍后重试。");
        }
        return response;
    }

    private String translateStatus(String status) {
        WorkOrderStatusEnum e = WorkOrderStatusEnum.of(status);
        return e != null ? e.getLabel() : "未知";
    }

    private String callAiReport(String date, String username, String dataSummary) {
        if (aiConfig == null || StringUtils.isEmpty(aiConfig.getApiKey())) {
            log.warn("AI API Key 未配置，日报使用模板生成");
            return null;
        }

        HttpURLConnection conn = null;
        try {
            String prompt = String.format(
                "你是一位专业的维保工程师，帮我生成一份简洁的日报。要求：\n" +
                "1. 分4个部分：签到记录、工单处理、巡检情况、故障处理\n" +
                "2. 每部分一句话总结\n" +
                "3. 如有异常情况请特别说明\n" +
                "4. 语言专业简洁\n" +
                "以下是今日数据：\n%s",
                dataSummary
            );

            String requestBody = OBJECT_MAPPER.writeValueAsString(
                java.util.Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", new Object[]{
                        java.util.Map.of("role", "system", "content", "你是专业的维保日报助手，请用中文回复，格式简洁，每部分一行，以'标题：内容'的格式输出。"),
                        java.util.Map.of("role", "user", "content", prompt)
                    },
                    "max_tokens", 800,
                    "temperature", 0.3
                )
            );

            String endpoint = aiConfig.getBaseUrl() + "/v1/chat/completions";
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + aiConfig.getApiKey());
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder resp = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        resp.append(line);
                    }
                }
                JsonNode node = OBJECT_MAPPER.readTree(resp.toString());
                String content = node.path("choices")
                    .path(0).path("message").path("content").asText();
                log.info("AI日报生成成功");
                return content;
            } else {
                log.warn("AI日报生成请求失败，HTTP状态码: {}，降级到模板", responseCode);
            }
        } catch (Exception e) {
            log.warn("AI日报生成异常，降级到模板: {}", e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
