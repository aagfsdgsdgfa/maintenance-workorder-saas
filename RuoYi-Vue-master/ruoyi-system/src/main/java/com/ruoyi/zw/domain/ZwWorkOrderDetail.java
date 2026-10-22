package com.ruoyi.zw.domain;

import java.util.Date;
import java.util.List;

public class ZwWorkOrderDetail extends ZwWorkOrder {

    private List<ZwInspectionTask> inspectionTasks;
    private List<ZwCheckIn> checkInRecords;
    private List<ZwFaultRecord> faultRecords;
    private ZwSatisfaction satisfaction;
    private Boolean isOverdue;

    public List<ZwInspectionTask> getInspectionTasks() { return inspectionTasks; }
    public void setInspectionTasks(List<ZwInspectionTask> inspectionTasks) { this.inspectionTasks = inspectionTasks; }

    public List<ZwCheckIn> getCheckInRecords() { return checkInRecords; }
    public void setCheckInRecords(List<ZwCheckIn> checkInRecords) { this.checkInRecords = checkInRecords; }

    public List<ZwFaultRecord> getFaultRecords() { return faultRecords; }
    public void setFaultRecords(List<ZwFaultRecord> faultRecords) { this.faultRecords = faultRecords; }

    public ZwSatisfaction getSatisfaction() { return satisfaction; }
    public void setSatisfaction(ZwSatisfaction satisfaction) { this.satisfaction = satisfaction; }

    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }
}
