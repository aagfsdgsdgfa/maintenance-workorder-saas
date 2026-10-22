package com.ruoyi.web.controller.zw;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.zw.service.IZwWorkOrderExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 工单Word导出Controller
 */
@RestController
@RequestMapping("/zw/workOrder")
public class ZwWorkOrderExportController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ZwWorkOrderExportController.class);

    @Autowired
    private IZwWorkOrderExportService workOrderExportService;

    /**
     * 导出工单Word文档
     * GET /zw/workOrder/exportWord/{id}
     */
    @PreAuthorize("@ss.hasPermi('zw:workOrder:export')")
    @GetMapping("/exportWord/{id}")
    public void exportWord(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            String fileRelativePath = workOrderExportService.exportWorkOrderWord(id);

            if (fileRelativePath == null) {
                // 模板文件不存在
                writeJson(response, 500, "Word模板文件未找到，请联系管理员上传模板到 resources/static/template/维保工单模板.docx");
                return;
            }

            String uploadPath = RuoYiConfig.getUploadPath();
            File file = new File(uploadPath + fileRelativePath);
            if (!file.exists()) {
                writeJson(response, 404, "文件生成失败");
                return;
            }

            String fileName = file.getName();
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20") + "\"; filename*=utf-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20"));

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
                os.flush();
            }

        } catch (Exception e) {
            log.error("导出工单Word失败: id={}", id, e);
            writeJson(response, 500, "导出失败：" + e.getMessage());
        }
    }

    private void writeJson(HttpServletResponse response, int code, String msg) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"code\":" + code + ",\"msg\":\"" + msg + "\"}");
        } catch (IOException ignored) {
        }
    }
}
