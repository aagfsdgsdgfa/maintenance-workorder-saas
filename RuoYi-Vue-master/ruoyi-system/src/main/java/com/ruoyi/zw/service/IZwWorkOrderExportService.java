package com.ruoyi.zw.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 工单Word导出服务接口
 */
public interface IZwWorkOrderExportService {

    /**
     * 导出工单Word文档（文件方式，保存到磁盘）
     *
     * @param workOrderId 工单ID
     * @return 文件相对路径，如 /report/工单_xxx.docx；模板不存在时返回 null
     */
    String exportWorkOrderWord(Long workOrderId) throws Exception;

    /**
     * 导出工单Word文档（流方式，直接写入HTTP响应）
     * 不落盘，更高效，推荐使用此接口
     *
     * @param workOrderId 工单ID
     * @param response   HTTP响应对象，用于直接写入输出流
     */
    void exportWorkOrderWordAsStream(Long workOrderId, HttpServletResponse response) throws Exception;
}
