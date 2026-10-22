package com.ruoyi.zw.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwDailyReport;
import com.ruoyi.zw.mapper.ZwDailyReportMapper;
import com.ruoyi.zw.mapper.ZwFaultRecordMapper;
import com.ruoyi.zw.mapper.ZwInspectionTaskMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.service.IZwDailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ZwDailyReportServiceImpl implements IZwDailyReportService {

    @Autowired
    private ZwDailyReportMapper dailyReportMapper;

    @Autowired
    private ZwWorkOrderMapper workOrderMapper;

    @Autowired
    private ZwFaultRecordMapper faultRecordMapper;

    @Autowired
    private ZwInspectionTaskMapper inspectionTaskMapper;

    @Override
    public List<ZwDailyReport> selectZwDailyReportList(ZwDailyReport dailyReport) {
        return dailyReportMapper.selectZwDailyReportList(dailyReport);
    }

    @Override
    public ZwDailyReport selectZwDailyReportById(Long reportId) {
        return dailyReportMapper.selectZwDailyReportById(reportId);
    }

    @Override
    public int insertZwDailyReport(ZwDailyReport dailyReport) {
        dailyReport.setDelFlag("0");
        // engineer_id 为 not null，始终设置当前用户ID兜底
        if (dailyReport.getEngineerId() == null) {
            dailyReport.setEngineerId(SecurityUtils.getUserId());
        }
        if (dailyReport.getReportDate() == null || dailyReport.getReportDate().trim().isEmpty()) {
            dailyReport.setReportDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
        if (dailyReport.getStatus() == null || dailyReport.getStatus().trim().isEmpty()) {
            dailyReport.setStatus("0");
        }
        if (dailyReport.getCreateBy() == null || dailyReport.getCreateBy().trim().isEmpty()) {
            dailyReport.setCreateBy(SecurityUtils.getUsername());
        }
        return dailyReportMapper.insertZwDailyReport(dailyReport);
    }

    @Override
    public int updateZwDailyReport(ZwDailyReport dailyReport) {
        return dailyReportMapper.updateZwDailyReport(dailyReport);
    }

    @Override
    public int deleteZwDailyReportByIds(Long[] reportIds) {
        return dailyReportMapper.deleteZwDailyReportByIds(reportIds);
    }

    @Override
    public ZwDailyReport autoGenerate() {
        int workOrderCount = workOrderMapper.countToday();
        int faultCount = faultRecordMapper.countToday();
        int inspectionCount = inspectionTaskMapper.countToday();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String reportDate = sdf.format(new Date());
        String content = String.format(
            "当日汇总：工单%d个，故障%d个，巡检任务%d个。",
            workOrderCount, faultCount, inspectionCount
        );
        ZwDailyReport report = new ZwDailyReport();
        report.setEngineerId(SecurityUtils.getUserId());
        report.setReportDate(reportDate);
        report.setReportTitle("日报-" + reportDate);
        report.setReportContent(content);
        report.setWorkOrderCount(workOrderCount);
        report.setInspectionCount(inspectionCount);
        report.setStatus("0");
        report.setCreateBy(SecurityUtils.getUsername());
        dailyReportMapper.insertZwDailyReport(report);
        return report;
    }
}
