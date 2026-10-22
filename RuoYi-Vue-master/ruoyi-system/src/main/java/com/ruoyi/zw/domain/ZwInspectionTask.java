package com.ruoyi.zw.domain;

import java.util.Date;

/**
 * 巡检任务表 zw_inspection_task
 */
public class ZwInspectionTask {
    private Long taskId;
    private Long workOrderId;
    private String workOrderNo;
    private String workOrderTitle;
    private Long assetId;
    private String assetName;
    private String taskType;
    private Date planTime;
    private Date actualStartTime;
    private Date actualEndTime;
    private String checkInLocation;
    private String checkInPhoto;
    private String checkInAddress;
    private String status;
    private String reportUrl;
    private String delFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private Long customerId;
    private String customerName;
    /** 工程师用户ID（通过 work_order.assigned_to 关联，非持久化字段） */
    private Long assignedTo;

    /** 计划时间查询起点 yyyy-MM-dd（非持久化，仅用于 SQL 端日期过滤） */
    private String planStartDate;

    /** 计划时间查询终点 yyyy-MM-dd（非持久化，仅用于 SQL 端日期过滤） */
    private String planEndDate;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

    public String getWorkOrderNo() { return workOrderNo; }
    public void setWorkOrderNo(String workOrderNo) { this.workOrderNo = workOrderNo; }

    public String getWorkOrderTitle() { return workOrderTitle; }
    public void setWorkOrderTitle(String workOrderTitle) { this.workOrderTitle = workOrderTitle; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public Date getPlanTime() { return planTime; }
    public void setPlanTime(Date planTime) { this.planTime = planTime; }

    public Date getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(Date actualStartTime) { this.actualStartTime = actualStartTime; }

    public Date getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(Date actualEndTime) { this.actualEndTime = actualEndTime; }

    public String getCheckInLocation() { return checkInLocation; }
    public void setCheckInLocation(String checkInLocation) { this.checkInLocation = checkInLocation; }

    public String getCheckInPhoto() { return checkInPhoto; }
    public void setCheckInPhoto(String checkInPhoto) { this.checkInPhoto = checkInPhoto; }

    public String getCheckInAddress() { return checkInAddress; }
    public void setCheckInAddress(String checkInAddress) { this.checkInAddress = checkInAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }

    public String getPlanStartDate() { return planStartDate; }
    public void setPlanStartDate(String planStartDate) { this.planStartDate = planStartDate; }

    public String getPlanEndDate() { return planEndDate; }
    public void setPlanEndDate(String planEndDate) { this.planEndDate = planEndDate; }
}
