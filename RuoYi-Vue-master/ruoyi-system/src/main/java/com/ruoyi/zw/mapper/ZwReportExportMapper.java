package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwReportExport;
import java.util.List;

public interface ZwReportExportMapper {
    List<ZwReportExport> selectZwReportExportList(ZwReportExport reportExport);
    ZwReportExport selectZwReportExportById(Long exportId);
    int insertZwReportExport(ZwReportExport reportExport);
    int updateZwReportExport(ZwReportExport reportExport);
    int deleteZwReportExportByIds(Long[] exportIds);
}
