package com.ruoyi.zw.domain;

import java.util.Date;

/**
 * 报修申请表 zw_repair_apply
 *
 * @author ruoyi
 */
public class ZwRepairApply {
    private static final long serialVersionUID = 1L;

    /** 报修ID */
    private Long applyId;

    /** 客户ID */
    private Long customerId;

    /** 资产ID */
    private Long assetId;

    /** 报修标题 */
    private String applyTitle;

    /** 故障描述 */
    private String faultDesc;

    /** 故障图片JSON数组 */
    private String faultPhotos;

    /** 故障视频JSON数组 */
    private String faultVideos;

    /** 联系人 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 期望上门时间 */
    private Date expectedTime;

    /** 状态：0待受理 1已受理 2处理中 3已完成 4已评价 5已取消 */
    private String status;

    /** 关联工单ID */
    private Long workOrderId;

    /** 删除标志：0存在 2删除 */
    private String delFlag;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

    // ---------- 关联字段（用于列表展示，非表字段） ----------
    /** 客户名称 */
    private String customerName;

    /** 资产名称 */
    private String assetName;

    /** 工单编号 */
    private String orderNo;

    public Long getApplyId() { return applyId; }
    public void setApplyId(Long applyId) { this.applyId = applyId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getApplyTitle() { return applyTitle; }
    public void setApplyTitle(String applyTitle) { this.applyTitle = applyTitle; }

    public String getFaultDesc() { return faultDesc; }
    public void setFaultDesc(String faultDesc) { this.faultDesc = faultDesc; }

    public String getFaultPhotos() { return faultPhotos; }
    public void setFaultPhotos(String faultPhotos) { this.faultPhotos = faultPhotos; }

    public String getFaultVideos() { return faultVideos; }
    public void setFaultVideos(String faultVideos) { this.faultVideos = faultVideos; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Date getExpectedTime() { return expectedTime; }
    public void setExpectedTime(Date expectedTime) { this.expectedTime = expectedTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

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

    @Override
    public String toString() {
        return "ZwRepairApply{applyId=" + applyId + ", applyTitle='" + applyTitle + "', status='" + status + "'}";
    }
}
