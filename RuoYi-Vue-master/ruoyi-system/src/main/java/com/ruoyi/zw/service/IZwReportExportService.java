package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwReportExport;
import java.util.List;

public interface IZwReportExportService {
    List<ZwReportExport> selectZwReportExportList(ZwReportExport reportExport);
    ZwReportExport selectZwReportExportById(Long exportId);
    int insertZwReportExport(ZwReportExport reportExport);
    int updateZwReportExport(ZwReportExport reportExport);
    int deleteZwReportExportByIds(Long[] exportIds);
}
