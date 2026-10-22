package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwInspectionTask;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ZwInspectionTaskMapper {
    List<ZwInspectionTask> selectZwInspectionTaskList(ZwInspectionTask task);
    ZwInspectionTask selectZwInspectionTaskById(Long taskId);
    int insertZwInspectionTask(ZwInspectionTask task);
    int updateZwInspectionTask(ZwInspectionTask task);
    int deleteZwInspectionTaskByIds(Long[] taskIds);
    List<ZwInspectionTask> selectTasksByWorkOrderId(Long workOrderId);
    int countToday();
    int updateReportUrl(Long taskId, String reportUrl);
    int existsTodayTaskByPlanAndAsset(Long planId, Long assetId, java.util.Date planDate);
    int batchAssign(List<Long> taskIds, Long engineerId, String updateBy);
}