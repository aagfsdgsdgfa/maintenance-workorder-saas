package com.ruoyi.zw.service;

/**
 * 巡检报告Service接口
 */
public interface IZwReportService {

    /**
     * 生成巡检报告PDF（单任务）
     *
     * @param taskId 巡检任务ID
     * @return PDF文件URL，失败返回null
     */
    String generateInspectionPdf(Long taskId);

    /**
     * 按日期范围生成巡检汇总报告PDF（客户门户导出用）
     *
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd
     * @param filePath  保存路径（含文件名）
     * @param customerId 客户ID（可为空）
     */
    void generateInspectionReportPdf(String startDate, String endDate, String filePath, Long customerId) throws Exception;
}
