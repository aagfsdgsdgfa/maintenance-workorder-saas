package com.ruoyi.zw.domain;

import java.util.Date;

/**
 * 续保商机表 renewal_opportunities
 *
 * @author ruoyi
 */
public class RenewalOpportunity {
    private static final long serialVersionUID = 1L;

    /** 商机ID */
    private Long opportunityId;

    /** 客户ID */
    private Long customerId;

    /** 资产ID */
    private Long assetId;

    /** 合同ID */
    private Long contractId;

    /** 商机类型：warranty_expire/contract_expire */
    private String opportunityType;

    /** 到期日期 */
    private Date expireDate;

    /** 销售负责人用户ID */
    private Long salesUserId;

    /** 状态：open/following/closed */
    private String status;

    /** 跟进记录(JSON) */
    private String followRecords;

    /** 报价单URL */
    private String quotationUrl;

    /** 成交金额 */
    private java.math.BigDecimal dealAmount;

    /** 商机标题 */
    private String title;

    /** 商机描述 */
    private String description;

    /** 删除标志（逻辑删除） */
    private String delFlag;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    public Long getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public String getOpportunityType() { return opportunityType; }
    public void setOpportunityType(String opportunityType) { this.opportunityType = opportunityType; }

    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }

    public Long getSalesUserId() { return salesUserId; }
    public void setSalesUserId(Long salesUserId) { this.salesUserId = salesUserId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFollowRecords() { return followRecords; }
    public void setFollowRecords(String followRecords) { this.followRecords = followRecords; }

    public String getQuotationUrl() { return quotationUrl; }
    public void setQuotationUrl(String quotationUrl) { this.quotationUrl = quotationUrl; }

    public java.math.BigDecimal getDealAmount() { return dealAmount; }
    public void setDealAmount(java.math.BigDecimal dealAmount) { this.dealAmount = dealAmount; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    @Override
    public String toString() {
        return "RenewalOpportunity{opportunityId=" + opportunityId + ", customerId=" + customerId + ", status='" + status + "'}";
    }
}
