package com.ruoyi.zw.service.impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwInspectionResult;
import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.service.IZwAssetService;
import com.ruoyi.zw.service.IZwInspectionResultService;
import com.ruoyi.zw.service.IZwInspectionTaskService;
import com.ruoyi.zw.service.IZwReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

/**
 * 巡检报告Service实现
 */
@Service
public class ZwReportServiceImpl implements IZwReportService {

    private static final Logger log = LoggerFactory.getLogger(ZwReportServiceImpl.class);
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private IZwInspectionTaskService taskService;

    @Autowired
    private IZwInspectionResultService resultService;

    @Autowired
    private IZwAssetService assetService;

    @Override
    public String generateInspectionPdf(Long taskId) {
        File pdfFile = null;
        try {
            // 1. 查询数据
            ZwInspectionTask task = taskService.selectZwInspectionTaskById(taskId);
            if (task == null) {
                log.warn("巡检任务不存在: taskId={}", taskId);
                return null;
            }
            ZwAsset asset = null;
            if (task.getAssetId() != null) {
                asset = assetService.selectZwAssetById(task.getAssetId());
            }
            ZwInspectionResult result = resultService.selectResultByTaskId(taskId);

            // 2. 创建PDF
            String fileName = "inspection_report_" + taskId + "_" + System.currentTimeMillis() + ".pdf";
            String uploadPath = com.ruoyi.common.config.RuoYiConfig.getUploadPath();
            File dir = new File(uploadPath + "/report");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            pdfFile = new File(dir, fileName);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // 字体
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(bfChinese, 20, Font.BOLD, new BaseColor(30, 80, 140));
            Font headerFont = new Font(bfChinese, 13, Font.BOLD, new BaseColor(50, 50, 50));
            Font normalFont = new Font(bfChinese, 11, Font.NORMAL, new BaseColor(60, 60, 60));
            Font labelFont = new Font(bfChinese, 11, Font.BOLD, new BaseColor(80, 80, 80));

            // ===== 标题 =====
            Paragraph title = new Paragraph("巡检报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // ===== 基本信息表格 =====
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1f, 2.5f});
            infoTable.setSpacingAfter(15);

            addLabelCell(infoTable, "巡检任务ID", labelFont);
            addValueCell(infoTable, String.valueOf(taskId), normalFont);
            addLabelCell(infoTable, "任务状态", labelFont);
            addValueCell(infoTable, formatStatus(task.getStatus()), normalFont);
            addLabelCell(infoTable, "计划时间", labelFont);
            addValueCell(infoTable, task.getPlanTime() != null ? SDF.format(task.getPlanTime()) : "-", normalFont);
            addLabelCell(infoTable, "实际开始", labelFont);
            addValueCell(infoTable, task.getActualStartTime() != null ? SDF.format(task.getActualStartTime()) : "-", normalFont);
            addLabelCell(infoTable, "实际结束", labelFont);
            addValueCell(infoTable, task.getActualEndTime() != null ? SDF.format(task.getActualEndTime()) : "-", normalFont);

            document.add(infoTable);

            // ===== 设备信息 =====
            if (asset != null) {
                Paragraph sectionTitle = new Paragraph("设备信息", headerFont);
                sectionTitle.setSpacingBefore(10);
                sectionTitle.setSpacingAfter(8);
                document.add(sectionTitle);

                PdfPTable assetTable = new PdfPTable(2);
                assetTable.setWidthPercentage(100);
                assetTable.setWidths(new float[]{1f, 2.5f});
                assetTable.setSpacingAfter(15);

                addLabelCell(assetTable, "设备名称", labelFont);
                addValueCell(assetTable, nullToDash(asset.getAssetName()), normalFont);
                addLabelCell(assetTable, "SN码", labelFont);
                addValueCell(assetTable, nullToDash(asset.getAssetCode()), normalFont);
                addLabelCell(assetTable, "位置", labelFont);
                addValueCell(assetTable, nullToDash(asset.getLocation()), normalFont);
                addLabelCell(assetTable, "品牌/型号", labelFont);
                addValueCell(assetTable, nullToDash(asset.getBrand()) + " " + nullToDash(asset.getModel()), normalFont);
                addLabelCell(assetTable, "设备类型", labelFont);
                addValueCell(assetTable, nullToDash(asset.getAssetType()), normalFont);

                document.add(assetTable);
            }

            // ===== 检查结果 =====
            if (result != null) {
                Paragraph sectionTitle2 = new Paragraph("巡检结果", headerFont);
                sectionTitle2.setSpacingBefore(10);
                sectionTitle2.setSpacingAfter(8);
                document.add(sectionTitle2);

                PdfPTable resultTable = new PdfPTable(2);
                resultTable.setWidthPercentage(100);
                resultTable.setWidths(new float[]{1f, 2.5f});
                resultTable.setSpacingAfter(15);

                addLabelCell(resultTable, "检查项", labelFont);
                addValueCell(resultTable, nullToDash(result.getCheckItems()), normalFont);
                addLabelCell(resultTable, "检查备注", labelFont);
                addValueCell(resultTable, nullToDash(result.getRemark()), normalFont);
                addLabelCell(resultTable, "照片", labelFont);
                addValueCell(resultTable, nullToDash(result.getPhotoUrls()), normalFont);
                addLabelCell(resultTable, "语音转文字", labelFont);
                addValueCell(resultTable, nullToDash(result.getVoiceText()), normalFont);

                document.add(resultTable);
            }

            // ===== 页脚 =====
            Paragraph footer = new Paragraph(
                "报告生成时间: " + SDF.format(new java.util.Date()),
                new Font(bfChinese, 9, Font.ITALIC, new BaseColor(150, 150, 150))
            );
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();

            // 3. 更新任务表的 report_url
            String reportUrl = "/report/" + fileName;
            taskService.updateReportUrl(taskId, reportUrl);

            return reportUrl;

        } catch (Exception e) {
            log.error("生成巡检报告PDF失败: taskId={}", taskId, e);
            // 清理不完整的文件
            if (pdfFile != null && pdfFile.exists()) {
                pdfFile.delete();
            }
            return null;
        }
    }

    @Override
    public void generateInspectionReportPdf(String startDate, String endDate, String filePath, Long customerId) throws Exception {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4);
        com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
        document.open();

        com.itextpdf.text.pdf.BaseFont bfChinese = com.itextpdf.text.pdf.BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", com.itextpdf.text.pdf.BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bfChinese, 22, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(30, 80, 140));
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(bfChinese, 13, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(50, 50, 50));
        com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL, new com.itextpdf.text.BaseColor(60, 60, 60));
        com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(80, 80, 80));

        // 标题
        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("设备巡检汇总报告", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        // 报告摘要
        com.itextpdf.text.Paragraph sub = new com.itextpdf.text.Paragraph("报告周期：" + startDate + " 至 " + endDate + "    生成时间：" + SDF.format(new java.util.Date()), normalFont);
        sub.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        sub.setSpacingAfter(20);
        document.add(sub);

        // 查询该时间段内的巡检任务
        ZwInspectionTask query = new ZwInspectionTask();
        if (customerId != null) query.setCustomerId(customerId);
        var tasks = taskService.selectZwInspectionTaskList(query);

        // 过滤日期范围
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date start = sdf.parse(startDate);
        java.util.Date end = sdf.parse(endDate);
        java.util.List<ZwInspectionTask> filtered = tasks.stream()
            .filter(t -> {
                if (t.getPlanTime() == null) return false;
                java.util.Date plan = t.getPlanTime();
                return !plan.before(start) && !plan.after(end);
            })
            .toList();

        // 统计
long total = filtered.size();
        long completed = filtered.stream().filter(t -> "3".equals(t.getStatus())).count();
        long abnormal = 0; // TODO: 实际接入巡检结果后，从 ZwInspectionResult.isNormal 判断
        long normal = completed; // 已完成视为正常

        com.itextpdf.text.pdf.PdfPTable statTable = new com.itextpdf.text.pdf.PdfPTable(4);
        statTable.setWidthPercentage(100f);
        statTable.setSpacingAfter(20f);
        String[] stats = { "总任务数", "已完成", "正常", "异常" };
        String[] statVals = { String.valueOf(total), String.valueOf(completed), String.valueOf(normal), String.valueOf(abnormal) };
        for (int i = 0; i < 4; i++) {
            com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(stats[i] + "：" + statVals[i], labelFont));
            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            cell.setPadding(10f);
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 238, 250));
            statTable.addCell(cell);
        }
        document.add(statTable);

        // 明细表
        if (!filtered.isEmpty()) {
            com.itextpdf.text.Paragraph secTitle = new com.itextpdf.text.Paragraph("巡检明细", headerFont);
            secTitle.setSpacingBefore(10);
            secTitle.setSpacingAfter(8);
            document.add(secTitle);

            com.itextpdf.text.pdf.PdfPTable detailTable = new com.itextpdf.text.pdf.PdfPTable(5);
            detailTable.setWidthPercentage(100f);
            detailTable.setWidths(new float[]{1f, 2f, 1.5f, 1f, 1.5f});

            String[] headers = { "任务ID", "计划时间", "状态", "设备ID", "实际结束" };
            for (String h : headers) {
                com.itextpdf.text.pdf.PdfPCell hCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(h, labelFont));
                hCell.setPadding(8f);
                hCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                hCell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
                hCell.setBorderColor(new com.itextpdf.text.BaseColor(30, 80, 140));
                detailTable.addCell(hCell);
            }

            for (ZwInspectionTask task : filtered) {
                detailTable.addCell(makeCell(task.getTaskId() != null ? task.getTaskId().toString() : "-", normalFont));
                detailTable.addCell(makeCell(task.getPlanTime() != null ? SDF.format(task.getPlanTime()) : "-", normalFont));
                detailTable.addCell(makeCell(formatStatus(task.getStatus()), normalFont));
                detailTable.addCell(makeCell(task.getAssetId() != null ? task.getAssetId().toString() : "-", normalFont));
                detailTable.addCell(makeCell(task.getActualEndTime() != null ? SDF.format(task.getActualEndTime()) : "-", normalFont));
            }
            document.add(detailTable);
        } else {
            com.itextpdf.text.Paragraph empty = new com.itextpdf.text.Paragraph("该时间段内无巡检记录", normalFont);
            empty.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            empty.setSpacingBefore(40);
            document.add(empty);
        }

        document.close();
    }

    // ===== 私有方法 =====

    private void addLabelCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBackgroundColor(new BaseColor(240, 244, 250));
        cell.setPadding(8);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addValueCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text != null ? text : "-", font));
        cell.setPadding(8);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private com.itextpdf.text.pdf.PdfPCell makeCell(String text, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(text != null ? text : "-", font));
        c.setPadding(6f);
        c.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
        return c;
    }

    private String nullToDash(String s) {
        return (s != null && !s.isBlank()) ? s : "-";
    }

    private String formatStatus(String status) {
        if (status == null) return "-";
        switch (status) {
            case "0": return "待执行";
            case "1": return "已到达";
            case "2": return "进行中";
            case "3": return "已完成";
            default: return status;
        }
    }
}
