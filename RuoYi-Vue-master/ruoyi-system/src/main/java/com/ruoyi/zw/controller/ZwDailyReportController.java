package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.AiDailyReportResponse;
import com.ruoyi.zw.domain.ZwDailyReport;
import com.ruoyi.zw.service.IAiService;
import com.ruoyi.zw.service.IZwDailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/zw/dailyReport")
public class ZwDailyReportController extends BaseController {

    @Autowired
    private IZwDailyReportService dailyReportService;

    @Autowired
    private IAiService aiService;

    @PreAuthorize("@ss.hasPermi('zw:dailyReport:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwDailyReport dailyReport) {
        startPage();
        List<ZwDailyReport> list = dailyReportService.selectZwDailyReportList(dailyReport);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:dailyReport:query')")
    @GetMapping("/{reportId}")
    public AjaxResult getInfo(@PathVariable Long reportId) {
        return success(dailyReportService.selectZwDailyReportById(reportId));
    }

    @PreAuthorize("@ss.hasPermi('zw:dailyReport:add')")
    @Log(title = "AI日报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwDailyReport dailyReport) {
        return toAjax(dailyReportService.insertZwDailyReport(dailyReport));
    }

    @PreAuthorize("@ss.hasPermi('zw:dailyReport:edit')")
    @Log(title = "AI日报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwDailyReport dailyReport) {
        return toAjax(dailyReportService.updateZwDailyReport(dailyReport));
    }

    @PreAuthorize("@ss.hasPermi('zw:dailyReport:remove')")
    @Log(title = "AI日报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reportIds}")
    public AjaxResult remove(@PathVariable Long[] reportIds) {
        return toAjax(dailyReportService.deleteZwDailyReportByIds(reportIds));
    }

    @PreAuthorize("@ss.hasPermi('zw:dailyReport:add')")
    @Log(title = "AI日报自动汇总", businessType = BusinessType.INSERT)
    @PostMapping("/autoGenerate")
    public AjaxResult autoGenerate() {
        return success(dailyReportService.autoGenerate());
    }

    /**
     * AI生成日报（小程序工程师端）
     * 聚合当日打卡、工单、巡检、故障数据，调用AI生成结构化日报，并自动持久化到数据库
     */
    @PreAuthorize("@ss.hasPermi('zw:dailyReport:add')")
    @Log(title = "AI日报生成", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestParam(required = false) String date) {
        Long engineerId = SecurityUtils.getUserId();
        if (date == null || date.isEmpty()) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        // 1. 调用 AI 生成日报内容
        AiDailyReportResponse aiResponse = aiService.generateDailyReport(engineerId, date);

        // 2. 将 AI 内容持久化到数据库
        ZwDailyReport report = new ZwDailyReport();
        report.setEngineerId(engineerId);
        report.setReportDate(date);
        report.setReportTitle(aiResponse.getTitle());
        // 将 sections 转成纯文本存储
        String content = aiResponse.getSections() != null
            ? aiResponse.getSections().stream()
                .map(s -> s.getTitle() + s.getContent())
                .collect(Collectors.joining("\n"))
            : aiResponse.getSummary();
        report.setReportContent(content);
        report.setStatus("1"); // 已生成
        report.setCreateBy(SecurityUtils.getUsername());
        dailyReportService.insertZwDailyReport(report);

        // 3. 返回 AI 生成的完整结构化内容给前端
        return success(aiResponse);
    }
}
