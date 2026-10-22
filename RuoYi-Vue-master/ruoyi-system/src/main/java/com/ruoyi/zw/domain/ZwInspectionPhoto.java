package com.ruoyi.zw.domain;

import java.math.BigDecimal;
import java.util.Date;

public class ZwInspectionPhoto {
    private Long photoId;
    private Long taskId;
    private Long workOrderId;
    private String photoType;
    private String photoUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Date shootTime;
    private String watermarkInfo;
    private String delFlag;
    private String createBy;
    private Date createTime;
    private String orderNo;

    public Long getPhotoId() { return photoId; }
    public void setPhotoId(Long photoId) { this.photoId = photoId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

    public String getPhotoType() { return photoType; }
    public void setPhotoType(String photoType) { this.photoType = photoType; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public Date getShootTime() { return shootTime; }
    public void setShootTime(Date shootTime) { this.shootTime = shootTime; }

    public String getWatermarkInfo() { return watermarkInfo; }
    public void setWatermarkInfo(String watermarkInfo) { this.watermarkInfo = watermarkInfo; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}
