package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.domain.ZwWorkOrderDetail;
import java.util.List;
import java.util.Map;

public interface IZwWorkOrderService {

    List<ZwWorkOrder> selectZwWorkOrderList(ZwWorkOrder workOrder);

    ZwWorkOrder selectZwWorkOrderById(Long workOrderId);

    ZwWorkOrderDetail selectZwWorkOrderDetailById(Long workOrderId);

    int insertZwWorkOrder(ZwWorkOrder workOrder);

    int updateZwWorkOrder(ZwWorkOrder workOrder);

    int deleteZwWorkOrderByIds(Long[] workOrderIds);

    int changeStatus(Long workOrderId, String status);

    int assignEngineer(Long workOrderId, Long engineerId, Long assignedBy);

    /**
     * 获取工程师的工单任务（status: 0待接单, 1处理中）
     */
    List<ZwWorkOrder> selectMyTasks(Long engineerId, String status);

    /**
     * 获取工程师的工单统计（各状态数量）
     */
    Map<String, Long> selectMyTaskStats(Long engineerId);

    /**
     * 接单
     */
    int accept(Long workOrderId, Long engineerId);

    /**
     * 拒单
     */
    int reject(Long workOrderId, Long engineerId, String reason);

    /**
     * 开始处理（到达现场）
     */
    int start(Long workOrderId);

    /**
     * 完工确认（带签名、配件、照片）
     */
    int complete(Long workOrderId, String resultDesc, String signImage, String photos, String parts, String nextVisitTime);
}
