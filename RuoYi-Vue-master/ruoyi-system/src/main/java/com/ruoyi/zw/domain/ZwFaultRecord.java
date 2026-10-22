package com.ruoyi.zw.domain;

import java.util.Date;

public class ZwFaultRecord {
    private Long faultId;
    private Long customerId;
    private Long assetId;
    private Long workOrderId;
    private String faultDesc;
    private String faultType;
    private String faultLevel;
    private String faultStatus;
    private Date faultTime;
    private Date resolveTime;
    private String resolution;
    private String delFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String customerName;
    private String assetName;
    private String orderNo;

    public Long getFaultId() { return faultId; }
    public void setFaultId(Long faultId) { this.faultId = faultId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

    public String getFaultDesc() { return faultDesc; }
    public void setFaultDesc(String faultDesc) { this.faultDesc = faultDesc; }

    public String getFaultType() { return faultType; }
    public void setFaultType(String faultType) { this.faultType = faultType; }

    public String getFaultLevel() { return faultLevel; }
    public void setFaultLevel(String faultLevel) { this.faultLevel = faultLevel; }

    public String getFaultStatus() { return faultStatus; }
    public void setFaultStatus(String faultStatus) { this.faultStatus = faultStatus; }

    public Date getFaultTime() { return faultTime; }
    public void setFaultTime(Date faultTime) { this.faultTime = faultTime; }

    public Date getResolveTime() { return resolveTime; }
    public void setResolveTime(Date resolveTime) { this.resolveTime = resolveTime; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

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

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}
