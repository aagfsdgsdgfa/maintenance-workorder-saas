package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwRepairApply;
import java.util.List;

public interface IZwRepairApplyService {

    List<ZwRepairApply> selectZwRepairApplyList(ZwRepairApply repairApply);

    List<ZwRepairApply> selectZwRepairApplyListByCustomerId(Long customerId);

    ZwRepairApply selectZwRepairApplyById(Long applyId);

    int insertZwRepairApply(ZwRepairApply repairApply);

    int updateZwRepairApply(ZwRepairApply repairApply);

    int deleteZwRepairApplyByIds(Long[] applyIds);

    /**
     * 受理报修（状态从0变为1）
     */
    int acceptRepairApply(Long applyId);

    /**
     * 转工单（状态从1变为2，同时创建工单并关联）
     */
    int transferToWorkOrder(Long applyId, Long assignedTo, String priority);

    /**
     * 一键受理并派发（状态0->2，自动受理后直接转工单）
     */
    int acceptAndTransfer(Long applyId, Long assignedTo);

    /**
     * 批量一键受理派发（支持多选）
     */
    int acceptAndTransferBatch(List<Long> applyIds, Long assignedTo);

    /**
     * 查询报修申请列表（用于导出，不受分页限制）
     */
    List<ZwRepairApply> selectZwRepairApplyListForExport(ZwRepairApply repairApply);
}
