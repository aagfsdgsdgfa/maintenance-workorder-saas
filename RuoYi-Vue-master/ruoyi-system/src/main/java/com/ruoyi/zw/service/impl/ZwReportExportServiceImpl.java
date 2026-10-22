package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwReportExport;
import com.ruoyi.zw.mapper.ZwReportExportMapper;
import com.ruoyi.zw.service.IZwReportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZwReportExportServiceImpl implements IZwReportExportService {

    @Autowired
    private ZwReportExportMapper reportExportMapper;

    @Override
    public List<ZwReportExport> selectZwReportExportList(ZwReportExport reportExport) {
        return reportExportMapper.selectZwReportExportList(reportExport);
    }

    @Override
    public ZwReportExport selectZwReportExportById(Long exportId) {
        return reportExportMapper.selectZwReportExportById(exportId);
    }

    @Override
    public int insertZwReportExport(ZwReportExport reportExport) {
        reportExport.setDelFlag("0");
        return reportExportMapper.insertZwReportExport(reportExport);
    }

    @Override
    public int updateZwReportExport(ZwReportExport reportExport) {
        return reportExportMapper.updateZwReportExport(reportExport);
    }

    @Override
    public int deleteZwReportExportByIds(Long[] exportIds) {
        return reportExportMapper.deleteZwReportExportByIds(exportIds);
    }
}
