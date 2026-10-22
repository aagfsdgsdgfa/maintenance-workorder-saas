package com.ruoyi.web.controller.zw;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zw.domain.ZwReportExport;
import com.ruoyi.zw.service.IZwReportExportService;
import com.ruoyi.zw.service.IZwReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 报表导出Controller
 */
@RestController
@RequestMapping("/zw/report")
public class ZwReportExportController extends BaseController {

    @Autowired
    private IZwReportExportService reportExportService;

    @Autowired
    private IZwReportService reportService;

    /**
     * 客户门户导出报告（生成真实PDF文件）
     * POST /zw/report/export
     */
    @Log(title = "报告导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public AjaxResult exportReport(@RequestBody Map<String, Object> params) {
        String reportType = (String) params.get("reportType");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        String fileFormat = (String) params.getOrDefault("fileFormat", "PDF");
        Long customerId = SecurityUtils.getCustomerId();

        if (StringUtils.isBlank(reportType)) return error("请选择报告类型");
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) return error("请选择日期范围");
        if (customerId == null) return error("请先登录");

        String fileName;
        String filePath;
        try {
            String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            fileName = "智维云服_" + getReportTypeName(reportType) + "_" + timestamp + ".pdf";
            String uploadDir = RuoYiConfig.getUploadPath() + "/report";
            new File(uploadDir).mkdirs();
            filePath = uploadDir + "/" + fileName;
            generateReportFile(reportType, startDate, endDate, filePath, customerId);
        } catch (Exception e) {
            return error("生成报告失败：" + e.getMessage());
        }

        ZwReportExport record = new ZwReportExport();
        record.setCustomerId(customerId != null ? customerId : 0L);
        record.setReportType(reportType);
        record.setStartDate(startDate);
        record.setEndDate(endDate);
        record.setFileName(fileName);
        record.setFilePath("/report/" + fileName);
        record.setFileFormat(fileFormat.toUpperCase());
        record.setExportStatus("1");
        reportExportService.insertZwReportExport(record);

        String downloadUrl = "/zw/report/download/" + record.getExportId();
        return success(Map.of(
            "exportId", record.getExportId(),
            "fileName", fileName,
            "downloadUrl", downloadUrl,
            "status", "1"
        ));
    }

    /**
     * 客户门户下载报告（真实文件流）
     * GET /zw/report/download/{exportId}
     */
    @GetMapping("/download/{exportId}")
    public void download(@PathVariable Long exportId, HttpServletResponse response) {
        ZwReportExport record = reportExportService.selectZwReportExportById(exportId);
        if (record == null) { writeError(response, "导出记录不存在"); return; }
        // 归属校验：客户只能下载自己的记录
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId != null && !customerId.equals(record.getCustomerId())) {
            writeError(response, "无权下载该报告"); return;
        }
        // 路径遍历防护：禁止逃逸出上传目录
        File uploadDir;
        try {
            uploadDir = new File(RuoYiConfig.getUploadPath()).getCanonicalFile();
        } catch (IOException e) {
            writeError(response, "上传目录解析失败"); return;
        }
        File safeFile;
        try {
            safeFile = new File(uploadDir, record.getFilePath()).getCanonicalFile();
            if (!safeFile.toPath().startsWith(uploadDir.toPath())) {
                writeError(response, "无效文件路径"); return;
            }
        } catch (IOException e) {
            writeError(response, "文件路径解析失败"); return;
        }
        if (!safeFile.exists()) { writeError(response, "文件不存在，可能已被清理"); return; }
        try (InputStream in = new FileInputStream(safeFile); OutputStream out = response.getOutputStream()) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String encodedName = URLEncoder.encode(record.getFileName(), StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedName + "\"; filename*=utf-8''" + encodedName);
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            out.flush();
        } catch (IOException e) {
            writeError(response, "下载失败：" + e.getMessage());
        }
    }

    /**
     * 查询导出记录列表（管理后台用）
     */
    @PreAuthorize("@ss.hasPermi('zw:reportExport:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwReportExport reportExport) {
        startPage();
        var list = reportExportService.selectZwReportExportList(reportExport);
        return getDataTable(list);
    }

    /**
     * 删除导出记录
     */
    @PreAuthorize("@ss.hasPermi('zw:reportExport:remove')")
    @Log(title = "报表导出", businessType = BusinessType.DELETE)
    @DeleteMapping("/{exportIds}")
    public AjaxResult remove(@PathVariable Long[] exportIds) {
        return toAjax(reportExportService.deleteZwReportExportByIds(exportIds));
    }

    // ====== 私有方法 ======

    private void generateReportFile(String reportType, String startDate, String endDate,
                                    String filePath, Long customerId) throws Exception {
        switch (reportType) {
            case "1" -> reportService.generateInspectionReportPdf(startDate, endDate, filePath, customerId);
            case "2" -> generateMaintenanceReport(filePath, startDate, endDate);
            case "3" -> generateAssetReport(filePath, startDate, endDate);
            default -> throw new IllegalArgumentException("不支持的报告类型：" + reportType);
        }
    }

    private void generateMaintenanceReport(String filePath, String startDate, String endDate) throws Exception {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4);
        com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
        document.open();
        com.itextpdf.text.pdf.BaseFont bfChinese = com.itextpdf.text.pdf.BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", com.itextpdf.text.pdf.BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bfChinese, 22, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(30, 80, 140));
        com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL, new com.itextpdf.text.BaseColor(60, 60, 60));
        com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(80, 80, 80));

        com.itextpdf.text.Paragraph mainTitle = new com.itextpdf.text.Paragraph("维保报告", titleFont);
        mainTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        mainTitle.setSpacingAfter(10);
        document.add(mainTitle);

        com.itextpdf.text.Paragraph sub = new com.itextpdf.text.Paragraph("报告周期：" + startDate + " 至 " + endDate, normalFont);
        sub.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        sub.setSpacingAfter(20);
        document.add(sub);

        // TODO: 查询实际维保数据
        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(3);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        String[] headers = {"工单编号", "设备名称", "维保时间"};
        for (String h : headers) {
            com.itextpdf.text.pdf.PdfPCell hCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(h, labelFont));
            hCell.setBackgroundColor(new com.itextpdf.text.BaseColor(30, 80, 140));
            hCell.setPadding(8f);
            hCell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
            hCell.setBorderColor(new com.itextpdf.text.BaseColor(30, 80, 140));
            table.addCell(hCell);
        }
        String[] sampleRow = {"WO-001", "电梯设备", startDate + " ~ " + endDate};
        for (String cell : sampleRow) {
            com.itextpdf.text.pdf.PdfPCell dCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(cell, normalFont));
            dCell.setPadding(8f);
            table.addCell(dCell);
        }
        document.add(table);
        document.close();
    }

    private void generateAssetReport(String filePath, String startDate, String endDate) throws Exception {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4);
        com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
        document.open();
        com.itextpdf.text.pdf.BaseFont bfChinese = com.itextpdf.text.pdf.BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", com.itextpdf.text.pdf.BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bfChinese, 22, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(30, 80, 140));
        com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL, new com.itextpdf.text.BaseColor(60, 60, 60));
        com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(80, 80, 80));

        com.itextpdf.text.Paragraph mainTitle = new com.itextpdf.text.Paragraph("资产报告", titleFont);
        mainTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        mainTitle.setSpacingAfter(10);
        document.add(mainTitle);

        com.itextpdf.text.Paragraph sub = new com.itextpdf.text.Paragraph("报告周期：" + startDate + " 至 " + endDate, normalFont);
        sub.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        sub.setSpacingAfter(20);
        document.add(sub);

        // TODO: 查询实际资产数据
        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        String[] headers = {"资产编号", "资产名称", "位置", "状态"};
        for (String h : headers) {
            com.itextpdf.text.pdf.PdfPCell hCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(h, labelFont));
            hCell.setPadding(8f);
            hCell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
            hCell.setBorderColor(new com.itextpdf.text.BaseColor(30, 80, 140));
            table.addCell(hCell);
        }
        String[] sampleRow = {"AST-001", "电梯设备A区", "A区一楼", "正常"};
        for (String cell : sampleRow) {
            com.itextpdf.text.pdf.PdfPCell dCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(cell, normalFont));
            dCell.setPadding(8f);
            table.addCell(dCell);
        }
        document.add(table);
        document.close();
    }

    private String getReportTypeName(String reportType) {
        return switch (reportType) {
            case "1" -> "巡检报告";
            case "2" -> "维保报告";
            case "3" -> "资产报告";
            default -> "综合报告";
        };
    }

    private void writeError(HttpServletResponse response, String msg) {
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"" + msg + "\"}");
        } catch (IOException e) {
            // 写入失败时记录到日志，不再静默吞掉
            e.printStackTrace();
        }
    }
}
