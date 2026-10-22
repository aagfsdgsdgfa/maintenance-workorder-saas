package com.ruoyi.zw.domain;

import java.util.Date;

public class ZwDailyReport {
    private Long reportId;
    private String reportDate;
    private Long engineerId;
    private String reportTitle;
    private String reportContent;
    private Integer workOrderCount;
    private Integer inspectionCount;
    private String voiceSummary;
    private String status;
    private String delFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String engineerName;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }

    public Long getEngineerId() { return engineerId; }
    public void setEngineerId(Long engineerId) { this.engineerId = engineerId; }

    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }

    public String getReportContent() { return reportContent; }
    public void setReportContent(String reportContent) { this.reportContent = reportContent; }

    public Integer getWorkOrderCount() { return workOrderCount; }
    public void setWorkOrderCount(Integer workOrderCount) { this.workOrderCount = workOrderCount; }

    public Integer getInspectionCount() { return inspectionCount; }
    public void setInspectionCount(Integer inspectionCount) { this.inspectionCount = inspectionCount; }

    public String getVoiceSummary() { return voiceSummary; }
    public void setVoiceSummary(String voiceSummary) { this.voiceSummary = voiceSummary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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

    public String getEngineerName() { return engineerName; }
    public void setEngineerName(String engineerName) { this.engineerName = engineerName; }
}
