package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwDailyReport;
import java.util.List;

public interface ZwDailyReportMapper {
    List<ZwDailyReport> selectZwDailyReportList(ZwDailyReport dailyReport);
    ZwDailyReport selectZwDailyReportById(Long reportId);
    int insertZwDailyReport(ZwDailyReport dailyReport);
    int updateZwDailyReport(ZwDailyReport dailyReport);
    int deleteZwDailyReportByIds(Long[] reportIds);
}
