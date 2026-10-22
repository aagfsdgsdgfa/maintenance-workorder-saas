package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwWorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ZwWorkOrderMapper {

    List<ZwWorkOrder> selectZwWorkOrderList(ZwWorkOrder workOrder);

    ZwWorkOrder selectZwWorkOrderById(Long workOrderId);

    int insertZwWorkOrder(ZwWorkOrder workOrder);

    int updateZwWorkOrder(ZwWorkOrder workOrder);

    int deleteZwWorkOrderByIds(Long[] workOrderIds);

    String selectMaxOrderNo(String datePrefix);

    int countToday();

    /**
     * 查询待处理且未发SLA预警的工单（status 0/1/2 且 sla_warning_sent = 'N'）
     */
    List<ZwWorkOrder> selectSlaPendingOrders();

    /**
     * 查询工程师的工单任务
     */
    List<ZwWorkOrder> selectMyTasks(@Param("engineerId") Long engineerId, @Param("status") String status);

    /**
     * 查询工程师指定日期完成的工单（日报聚合用）
     */
    List<ZwWorkOrder> selectByEngineerAndDate(@Param("engineerId") Long engineerId, @Param("date") String date);
}