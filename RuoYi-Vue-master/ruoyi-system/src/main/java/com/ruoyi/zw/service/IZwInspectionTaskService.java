package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwInspectionTask;
import java.util.List;

public interface IZwInspectionTaskService {
    List<ZwInspectionTask> selectZwInspectionTaskList(ZwInspectionTask task);
    ZwInspectionTask selectZwInspectionTaskById(Long taskId);
    List<ZwInspectionTask> selectTasksByWorkOrderId(Long workOrderId);
    int insertZwInspectionTask(ZwInspectionTask task);
    int updateZwInspectionTask(ZwInspectionTask task);
    int deleteZwInspectionTaskByIds(Long[] taskIds);
    int executeTask(Long taskId, String checkInLocation, String checkInPhoto, String checkInAddress);
    int countToday();

    /**
     * 更新巡检报告URL
     *
     * @param taskId   任务ID
     * @param reportUrl 报告URL
     * @return 影响行数
     */
    int updateReportUrl(Long taskId, String reportUrl);

    /**
     * 批量分配工程师
     *
     * @param taskIds     任务ID列表
     * @param engineerId  工程师用户ID
     * @param updateBy    更新人
     * @return 影响行数
     */
    int batchAssign(List<Long> taskIds, Long engineerId, String updateBy);
}