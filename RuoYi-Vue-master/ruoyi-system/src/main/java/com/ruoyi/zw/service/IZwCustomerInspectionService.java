package com.ruoyi.zw.service;

import java.util.Map;

/**
 * 客户巡检Service接口
 *
 * @author ruoyi
 */
public interface IZwCustomerInspectionService {

    /**
     * 查询客户巡检记录列表（分页）
     *
     * @param customerId 客户ID
     * @param assetId 资产ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果（total/pageNum/pageSize/rows）
     */
    Map<String, Object> selectInspectionList(Long customerId, Long assetId,
                                            String startDate, String endDate,
                                            int pageNum, int pageSize);

    /**
     * 查询巡检报告详情
     *
     * @param taskId 巡检任务ID
     * @return 巡检报告详情
     */
    Map<String, Object> selectInspectionReport(Long taskId);
}
