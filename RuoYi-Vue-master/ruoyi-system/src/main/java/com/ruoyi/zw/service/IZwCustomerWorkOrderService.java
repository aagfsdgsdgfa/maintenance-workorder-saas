package com.ruoyi.zw.service;

import java.util.Map;

/**
 * 客户工单Service接口
 *
 * @author ruoyi
 */
public interface IZwCustomerWorkOrderService {

    /**
     * 查询客户工单列表（分页）
     *
     * @param customerId 客户ID
     * @param status 工单状态
     * @param orderType 工单类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果（total/pageNum/pageSize/rows）
     */
    Map<String, Object> selectWorkOrderList(Long customerId, String status, String orderType,
                                            String startDate, String endDate,
                                            int pageNum, int pageSize);

    /**
     * 查询客户工单详情（含进度跟踪）
     *
     * @param workOrderId 工单ID
     * @return 工单详情
     */
    Map<String, Object> selectWorkOrderDetail(Long workOrderId);
}
