package com.ruoyi.zw.service.impl;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.export.WorkOrderFieldMapper;
import com.ruoyi.zw.export.WorkOrderPhotoCollector;
import com.ruoyi.zw.export.WorkOrderTemplateRenderer;
import com.ruoyi.zw.service.IZwWorkOrderService;
import com.ruoyi.zw.service.IZwWorkOrderExportService;
import jakarta.annotation.PostConstruct;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * 工单Word导出Service实现。
 *
 * <p>编排三类职责：
 * <ul>
 *   <li>{@link WorkOrderFieldMapper} — 工单字段 → 模板字段映射</li>
 *   <li>{@link WorkOrderTemplateRenderer} — 占位符文本替换</li>
 *   <li>{@link WorkOrderPhotoCollector} — 签字图片插入</li>
 * </ul>
 */
@Service
public class ZwWorkOrderExportServiceImpl implements IZwWorkOrderExportService {

    private static final Logger log = LoggerFactory.getLogger(ZwWorkOrderExportServiceImpl.class);

    /** Word模板classpath路径 */
    private static final String TEMPLATE_CLASSPATH = "static/template/维保工单模板.docx";

    /** Word模板文件系统路径（备用） */
    private static final String TEMPLATE_FILESYSTEM = "resources/static/template/维保工单模板.docx";

    /** Word输出子目录 */
    private static final String OUTPUT_SUBDIR = "/report";

    @Autowired
    private IZwWorkOrderService workOrderService;

    @Autowired
    private ISysUserService sysUserService;

    private WorkOrderFieldMapper fieldMapper;
    private final WorkOrderTemplateRenderer templateRenderer = new WorkOrderTemplateRenderer();
    private final WorkOrderPhotoCollector photoCollector = new WorkOrderPhotoCollector();

    @PostConstruct
    void initHelpers() {
        this.fieldMapper = new WorkOrderFieldMapper(sysUserService);
    }

    @Override
    public String exportWorkOrderWord(Long workOrderId) throws Exception {
        InputStream templateStream = loadTemplate();
        if (templateStream == null) {
            log.warn("Word模板文件不存在: {}", TEMPLATE_CLASSPATH);
            return null;
        }

        try (XWPFDocument document = new XWPFDocument(templateStream)) {
            ZwWorkOrder workOrder = workOrderService.selectZwWorkOrderById(workOrderId);
            if (workOrder == null) {
                log.warn("工单不存在: workOrderId={}", workOrderId);
                return null;
            }

            Map<String, String> fields = fieldMapper.build(workOrder);
            templateRenderer.render(document, fields);
            photoCollector.inject(document, workOrder);

            String fileName = "工单_" + nullToStr(workOrder.getOrderNo()) + "_" + System.currentTimeMillis() + ".docx";
            String uploadPath = RuoYiConfig.getUploadPath();
            File outputDir = new File(uploadPath + OUTPUT_SUBDIR);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File outputFile = new File(outputDir, fileName);

            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                document.write(out);
            }

            log.info("工单Word导出成功: workOrderId={}, file={}", workOrderId, outputFile.getAbsolutePath());
            return OUTPUT_SUBDIR + "/" + fileName;
        } finally {
            try { templateStream.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public void exportWorkOrderWordAsStream(Long workOrderId, HttpServletResponse response) throws Exception {
        InputStream templateStream = loadTemplate();
        if (templateStream == null) {
            log.warn("Word模板文件不存在: {}", TEMPLATE_CLASSPATH);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (XWPFDocument document = new XWPFDocument(templateStream)) {
            ZwWorkOrder workOrder = workOrderService.selectZwWorkOrderById(workOrderId);
            if (workOrder == null) {
                log.warn("工单不存在: workOrderId={}", workOrderId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Map<String, String> fields = fieldMapper.build(workOrder);
            templateRenderer.render(document, fields);
            photoCollector.inject(document, workOrder);

            // Content-Type 和 Header 由Controller层设置（文件名由Controller编码）
            document.write(response.getOutputStream());
            response.getOutputStream().flush();

            log.info("工单Word流式导出成功: workOrderId={}", workOrderId);
        } finally {
            try { templateStream.close(); } catch (Exception ignored) {}
        }
    }

    private InputStream loadTemplate() {
        // 方式1：从classpath加载
        try {
            ClassPathResource resource = new ClassPathResource(TEMPLATE_CLASSPATH);
            if (resource.exists()) {
                return resource.getInputStream();
            }
        } catch (Exception e) {
            log.debug("从classpath加载模板失败: {}", e.getMessage());
        }

        // 方式2：从文件系统加载（相对于项目根目录）
        try {
            File file = new File(TEMPLATE_FILESYSTEM);
            if (file.exists()) {
                return new FileInputStream(file);
            }
        } catch (Exception e) {
            log.debug("从文件系统加载模板失败: {}", e.getMessage());
        }

        return null;
    }

    private static String nullToStr(String s) {
        return (s != null && !s.isBlank()) ? s : "";
    }
}