package com.ruoyi.zw.domain;

import java.util.Date;

/**
 * 维保合同表 maintenance_contracts
 *
 * @author ruoyi
 */
public class MaintenanceContract {
    private static final long serialVersionUID = 1L;

    /** 合同ID */
    private Long contractId;

    /** 合同编号 */
    private String contractNo;

    /** 归属客户ID */
    private Long customerId;

    /** 合同名称 */
    private String contractName;

    /** 合同开始日期 */
    private Date startDate;

    /** 合同结束日期 */
    private Date endDate;

    /** SLA响应时长(小时) */
    private Integer slaResponseHours;

    /** SLA到场时长(小时) */
    private Integer slaArrivalHours;

    /** 含巡检次数 */
    private Integer includedInspections;

    /** 含维修次数 */
    private Integer includedRepairs;

    /** 免费配件额度 */
    private java.math.BigDecimal freePartsQuota;

    /** 额外上门费 */
    private java.math.BigDecimal extraVisitFee;

    /** 折扣率 */
    private java.math.BigDecimal discountRate;

    /** 合同附件URL */
    private String attachmentUrl;

    /** 合同图片URL */
    private String imageUrl;

    /** 状态：0生效 1到期 2终止 */
    private String status;

    /** 已用巡检次数 */
    private Integer usedInspections;

    /** 已用维修次数 */
    private Integer usedRepairs;

    /** 已用配件金额 */
    private java.math.BigDecimal usedPartsAmount;

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

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getContractName() { return contractName; }
    public void setContractName(String contractName) { this.contractName = contractName; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Integer getSlaResponseHours() { return slaResponseHours; }
    public void setSlaResponseHours(Integer slaResponseHours) { this.slaResponseHours = slaResponseHours; }

    public Integer getSlaArrivalHours() { return slaArrivalHours; }
    public void setSlaArrivalHours(Integer slaArrivalHours) { this.slaArrivalHours = slaArrivalHours; }

    public Integer getIncludedInspections() { return includedInspections; }
    public void setIncludedInspections(Integer includedInspections) { this.includedInspections = includedInspections; }

    public Integer getIncludedRepairs() { return includedRepairs; }
    public void setIncludedRepairs(Integer includedRepairs) { this.includedRepairs = includedRepairs; }

    public java.math.BigDecimal getFreePartsQuota() { return freePartsQuota; }
    public void setFreePartsQuota(java.math.BigDecimal freePartsQuota) { this.freePartsQuota = freePartsQuota; }

    public java.math.BigDecimal getExtraVisitFee() { return extraVisitFee; }
    public void setExtraVisitFee(java.math.BigDecimal extraVisitFee) { this.extraVisitFee = extraVisitFee; }

    public java.math.BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(java.math.BigDecimal discountRate) { this.discountRate = discountRate; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getUsedInspections() { return usedInspections; }
    public void setUsedInspections(Integer usedInspections) { this.usedInspections = usedInspections; }

    public Integer getUsedRepairs() { return usedRepairs; }
    public void setUsedRepairs(Integer usedRepairs) { this.usedRepairs = usedRepairs; }

    public java.math.BigDecimal getUsedPartsAmount() { return usedPartsAmount; }
    public void setUsedPartsAmount(java.math.BigDecimal usedPartsAmount) { this.usedPartsAmount = usedPartsAmount; }

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

    @Override
    public String toString() {
        return "MaintenanceContract{contractId=" + contractId + ", contractNo='" + contractNo + "', contractName='" + contractName + "'}";
    }
}
