package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.AiDailyReportResponse;
import com.ruoyi.zw.service.IAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AI 接口（小程序工程师端）
 * 包括语音转文字和AI日报生成
 */
@RestController
@RequestMapping("/ai")
public class AiController extends BaseController {

    @Autowired
    private IAiService aiService;

    /**
     * 语音转文字（智维AI网关 OpenClaw）
     * POST /ai/voice/toText
     * Body: { "audioUrl": "https://..." } 或 { "audioBase64": "..." }
     */
    @PostMapping("/voice/toText")
    public AjaxResult voiceToText(@RequestBody java.util.Map<String, String> body) {
        String audioUrl = body.get("audioUrl");
        if (audioUrl == null || audioUrl.isEmpty()) {
            return AjaxResult.error("audioUrl 不能为空");
        }
        String text = aiService.voiceToText(audioUrl);
        if (text == null || text.isEmpty()) {
            return AjaxResult.error("语音识别失败，请稍后重试");
        }
        return AjaxResult.success(text);
    }
}
