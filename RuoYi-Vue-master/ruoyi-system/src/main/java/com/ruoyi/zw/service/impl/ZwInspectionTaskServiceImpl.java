package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.mapper.ZwInspectionTaskMapper;
import com.ruoyi.zw.service.IZwInspectionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class ZwInspectionTaskServiceImpl implements IZwInspectionTaskService {

    @Autowired
    private ZwInspectionTaskMapper taskMapper;

    @Autowired
    private com.ruoyi.zw.service.IZwNotificationService notificationService;

    @Override
    public List<ZwInspectionTask> selectZwInspectionTaskList(ZwInspectionTask task) {
        return taskMapper.selectZwInspectionTaskList(task);
    }

    @Override
    public ZwInspectionTask selectZwInspectionTaskById(Long taskId) {
        return taskMapper.selectZwInspectionTaskById(taskId);
    }

    @Override
    public List<ZwInspectionTask> selectTasksByWorkOrderId(Long workOrderId) {
        return taskMapper.selectTasksByWorkOrderId(workOrderId);
    }

    @Override
    @Transactional
    public int insertZwInspectionTask(ZwInspectionTask task) {
        task.setDelFlag("0");
        task.setStatus("0");
        return taskMapper.insertZwInspectionTask(task);
    }

    @Override
    public int updateZwInspectionTask(ZwInspectionTask task) {
        return taskMapper.updateZwInspectionTask(task);
    }

    @Override
    public int deleteZwInspectionTaskByIds(Long[] taskIds) {
        return taskMapper.deleteZwInspectionTaskByIds(taskIds);
    }

    @Override
    @Transactional
    public int executeTask(Long taskId, String checkInLocation, String checkInPhoto, String checkInAddress) {
        // 先查询任务获取 customerId
        ZwInspectionTask existing = taskMapper.selectZwInspectionTaskById(taskId);
        ZwInspectionTask task = new ZwInspectionTask();
        task.setTaskId(taskId);
        task.setActualStartTime(new Date());
        task.setCheckInLocation(checkInLocation);
        task.setCheckInPhoto(checkInPhoto);
        task.setCheckInAddress(checkInAddress);
        task.setStatus("1"); // 已到达
        int rows = taskMapper.updateZwInspectionTask(task);
        if (rows > 0 && existing != null && existing.getCustomerId() != null) {
            try {
                notificationService.sendNotification(
                        existing.getCustomerId(), existing.getCustomerId(),
                        "巡检已开始",
                        "您的巡检任务已由工程师到达现场，开始巡检中，请知悉",
                        "inspection",
                        taskId
                );
            } catch (Exception e) {
                // 通知失败不影响主流程
            }
        }
        return rows;
    }

    @Override
    public int countToday() {
        return taskMapper.countToday();
    }

    @Override
    public int updateReportUrl(Long taskId, String reportUrl) {
        return taskMapper.updateReportUrl(taskId, reportUrl);
    }

    @Override
    @Transactional
    public int batchAssign(List<Long> taskIds, Long engineerId, String updateBy) {
        if (taskIds == null || taskIds.isEmpty()) {
            return 0;
        }
        int rows = taskMapper.batchAssign(taskIds, engineerId, updateBy);
        if (rows > 0) {
            try {
                notificationService.sendNotification(
                        engineerId, null,
                        "新的巡检任务",
                        "您被分配了 " + taskIds.size() + " 个巡检任务，请及时处理",
                        "inspection",
                        taskIds.get(0)
                );
            } catch (Exception e) {
                // 通知失败不影响主流程
            }
        }
        return rows;
    }
}