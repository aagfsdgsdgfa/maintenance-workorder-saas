package com.ruoyi.zw.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 工单表 zw_work_order
 *
 * @author ruoyi
 */
public class ZwWorkOrder {
    private static final long serialVersionUID = 1L;

    /** 工单ID */
    @Excel(name = "工单ID")
    private Long workOrderId;

    /** 工单编号 */
    @Excel(name = "工单编号")
    private String orderNo;

    /** 客户ID */
    @Excel(name = "客户ID")
    private Long customerId;

    /** 关联资产ID */
    @Excel(name = "资产ID")
    private Long assetId;

    /** 工单类型 */
    @Excel(name = "工单类型", readConverterExp = "1=巡检,2=维修,3=送货,4=混合")
    private String orderType;

    /** 工单标题 */
    @Excel(name = "工单标题")
    private String title;

    /** 工单描述 */
    @Excel(name = "工单描述")
    private String description;

    /** 优先级 */
    @Excel(name = "优先级", readConverterExp = "1=低,2=中,3=高,4=紧急")
    private String priority;

    /** 分配给工程师 */
    @Excel(name = "工程师ID")
    private Long assignedTo;

    /** 派单人 */
    @Excel(name = "派单人")
    private Long assignedBy;

    /** 创建时间查询起点 yyyy-MM-dd（非持久化，仅用于 SQL 端日期过滤） */
    private String createTimeStart;

    /** 创建时间查询终点 yyyy-MM-dd（非持久化，仅用于 SQL 端日期过滤） */
    private String createTimeEnd;

    /** 状态（多状态查询时使用，如 "0,1,2" 展开为 IN 查询） */
    private String status;

    /** 状态列表，用于多状态 IN 查询 */
    private List<String> statusList;

    /** SLA截止时间 */
    @Excel(name = "SLA截止", width = 120, dateFormat = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date slaDeadline;

    /** 是否已发超时预警：Y/N */
    private String slaWarningSent;

    /** 客户联系人 */
    private String contactName;

    /** 联系人电话 */
    private String contactPhone;

    /** 上门地址 */
    private String contactAddress;

    /** 完成时间 */
    private Date completeTime;

    /** 甲方签名图片URL */
    private String signImage;

    /** 删除标志 */
    private String delFlag;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

    /** 客户名称（JOIN查询填充，不映射数据库列） */
    private String customerName;

    /** 资产名称（JOIN查询填充，不映射数据库列） */
    private String assetName;

    /** 设备序列号（JOIN查询填充，不映射数据库列） */
    private String serialNumber;

    // ====== Word导出扩展字段 ======
    /** 受理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date acceptTime;

    /** 服务时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date serviceTime;

    /** 维保类型（定期检查/故障维修/其他） */
    private String workOrderType;

    /** 故障描述（甲方填写） */
    private String faultDescription;

    /** 故障原因（乙方填写） */
    private String faultCause;

    /** 上门费 */
    private java.math.BigDecimal visitFee;

    /** 工时费 */
    private java.math.BigDecimal serviceFee;

    /** 合计金额 */
    private java.math.BigDecimal totalAmount;

    /** 是否收费 */
    private String chargeStatus;

    /** 测试情况与运行状态 */
    private String testStatus;

    /** 客户签字图片URL */
    private String customerSignUrl;

    /** 工程师签字图片URL */
    private String engineerSignUrl;

    /** 备注说明 */
    private String remark;

    // ====== 资产扩展字段（JOIN zw_asset） ======
    /** 品牌 */
    private String brand;

    /** 型号 */
    private String model;

    /** 购置日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date purchaseDate;

    /** 设备类型（乙方售出/第三方） */
    private String assetType;

    // ====== 维保期限（JOIN zw_contract） ======
    /** 维保开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractStartDate;

    /** 维保结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractEndDate;

    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    public String getCreateTimeStart() { return createTimeStart; }
    public void setCreateTimeStart(String createTimeStart) { this.createTimeStart = createTimeStart; }

    public String getCreateTimeEnd() { return createTimeEnd; }
    public void setCreateTimeEnd(String createTimeEnd) { this.createTimeEnd = createTimeEnd; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(Date slaDeadline) { this.slaDeadline = slaDeadline; }

    public String getSlaWarningSent() { return slaWarningSent; }
    public void setSlaWarningSent(String slaWarningSent) { this.slaWarningSent = slaWarningSent; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactAddress() { return contactAddress; }
    public void setContactAddress(String contactAddress) { this.contactAddress = contactAddress; }

    public Date getCompleteTime() { return completeTime; }
    public void setCompleteTime(Date completeTime) { this.completeTime = completeTime; }

    public String getSignImage() { return signImage; }
    public void setSignImage(String signImage) { this.signImage = signImage; }

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

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public Date getAcceptTime() { return acceptTime; }
    public void setAcceptTime(Date acceptTime) { this.acceptTime = acceptTime; }
    public Date getServiceTime() { return serviceTime; }
    public void setServiceTime(Date serviceTime) { this.serviceTime = serviceTime; }
    public String getWorkOrderType() { return workOrderType; }
    public void setWorkOrderType(String workOrderType) { this.workOrderType = workOrderType; }
    public String getFaultDescription() { return faultDescription; }
    public void setFaultDescription(String faultDescription) { this.faultDescription = faultDescription; }
    public String getFaultCause() { return faultCause; }
    public void setFaultCause(String faultCause) { this.faultCause = faultCause; }
    public java.math.BigDecimal getVisitFee() { return visitFee; }
    public void setVisitFee(java.math.BigDecimal visitFee) { this.visitFee = visitFee; }
    public java.math.BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(java.math.BigDecimal serviceFee) { this.serviceFee = serviceFee; }
    public java.math.BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(java.math.BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getChargeStatus() { return chargeStatus; }
    public void setChargeStatus(String chargeStatus) { this.chargeStatus = chargeStatus; }
    public String getTestStatus() { return testStatus; }
    public void setTestStatus(String testStatus) { this.testStatus = testStatus; }
    public String getCustomerSignUrl() { return customerSignUrl; }
    public void setCustomerSignUrl(String customerSignUrl) { this.customerSignUrl = customerSignUrl; }
    public String getEngineerSignUrl() { return engineerSignUrl; }
    public void setEngineerSignUrl(String engineerSignUrl) { this.engineerSignUrl = engineerSignUrl; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }
    public Date getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(Date contractStartDate) { this.contractStartDate = contractStartDate; }
    public Date getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(Date contractEndDate) { this.contractEndDate = contractEndDate; }

    public List<String> getStatusList() { return statusList; }
    public void setStatusList(List<String> statusList) { this.statusList = statusList; }

    @Override
    public String toString() {
        return "ZwWorkOrder{workOrderId=" + workOrderId + ", orderNo='" + orderNo + "', title='" + title + "'}";
    }
}