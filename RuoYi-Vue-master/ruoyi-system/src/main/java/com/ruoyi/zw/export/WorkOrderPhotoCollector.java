package com.ruoyi.zw.export;

import com.ruoyi.zw.domain.ZwWorkOrder;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 工单签字图片采集器：负责下载URL图片并替换文档中的 {@code ${customerSignUrl}} /
 * {@code ${engineerSignUrl}} 占位符为真实图片。
 */
public class WorkOrderPhotoCollector {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderPhotoCollector.class);

    /** 图片宽度 EMU（约 2.5cm） */
    private static final int PICTURE_WIDTH_EMU = 914400 / 2;

    /** 图片高度 EMU（约 1.5cm） */
    private static final int PICTURE_HEIGHT_EMU = 914400 / 3;

    /** 下载图片超时 */
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 10000;

    public void inject(XWPFDocument document, ZwWorkOrder workOrder) {
        String customerSignUrl = WorkOrderFieldMapper.nullToStr(workOrder.getCustomerSignUrl());
        if (!customerSignUrl.isEmpty()) {
            replaceImagePlaceholder(document, "customerSignUrl", customerSignUrl);
        }

        String engineerSignUrl = WorkOrderFieldMapper.nullToStr(workOrder.getEngineerSignUrl());
        if (!engineerSignUrl.isEmpty()) {
            replaceImagePlaceholder(document, "engineerSignUrl", engineerSignUrl);
        }
    }

    private void replaceImagePlaceholder(XWPFDocument document, String placeholder, String imageUrl) {
        byte[] imageBytes = downloadImage(imageUrl);
        if (imageBytes == null) {
            log.warn("无法下载签字图片，跳过插入: url={}", imageUrl);
            return;
        }
        replaceInParagraphs(document.getParagraphs(), placeholder, imageBytes);
        for (XWPFTable table : document.getTables()) {
            replaceInTable(table, placeholder, imageBytes);
        }
    }

    private void replaceInParagraphs(java.util.List<XWPFParagraph> paragraphs, String placeholder, byte[] imageBytes) {
        for (XWPFParagraph para : paragraphs) {
            String text = para.getText();
            if (text != null && text.contains("${" + placeholder + "}")) {
                para.getRuns().forEach(run -> run.setText("", 0));
                insertPicture(para.createRun(), imageBytes);
            }
        }
    }

    private void replaceInTable(XWPFTable table, String placeholder, byte[] imageBytes) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph para : cell.getParagraphs()) {
                    String text = para.getText();
                    if (text != null && text.contains("${" + placeholder + "}")) {
                        para.getRuns().forEach(run -> run.setText("", 0));
                        insertPicture(para.createRun(), imageBytes);
                    }
                }
            }
        }
    }

    private void insertPicture(XWPFRun run, byte[] imageBytes) {
        try {
            run.addPicture(
                    new ByteArrayInputStream(imageBytes),
                    XWPFDocument.PICTURE_TYPE_PNG,
                    "signature.png",
                    PICTURE_WIDTH_EMU,
                    PICTURE_HEIGHT_EMU
            );
        } catch (Exception e) {
            log.warn("插入签字图片失败: {}", e.getMessage());
        }
    }

    private byte[] downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return IOUtils.toByteArray(conn.getInputStream());
            }
        } catch (Exception e) {
            log.warn("下载签字图片失败: url={}, error={}", imageUrl, e.getMessage());
        }
        return null;
    }
}