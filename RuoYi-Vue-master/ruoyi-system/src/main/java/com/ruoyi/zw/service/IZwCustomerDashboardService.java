package com.ruoyi.zw.service;

import java.util.Map;

/**
 * 客户驾驶舱Service接口
 *
 * @author ruoyi
 */
public interface IZwCustomerDashboardService {

    /**
     * 获取客户驾驶舱数据
     *
     * @param customerId 客户ID
     * @return 驾驶舱数据（资产概览、服务概览、故障排行、即将到期合同）
     */
    Map<String, Object> selectCustomerDashboard(Long customerId);
}
