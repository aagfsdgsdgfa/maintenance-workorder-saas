package com.ruoyi.zw.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 资产表 zw_asset
 *
 * @author ruoyi
 */
public class ZwAsset {
    private static final long serialVersionUID = 1L;

    /** 资产ID */
    @Excel(name = "资产ID")
    private Long assetId;

    /** 资产编码/SN */
    @Excel(name = "资产编码")
    private String assetCode;

    /** 资产名称 */
    @Excel(name = "资产名称")
    private String assetName;

    /** 资产类型 */
    @Excel(name = "资产类型")
    private String assetType;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 归属客户ID */
    @Excel(name = "客户ID")
    private Long customerId;

    /** 归属客户名称（非持久化，用于导出显示） */
    private String customerName;

    /** 是否第三方代保：0自售 1代保 */
    @Excel(name = "代保", readConverterExp = "0=自售,1=代保")
    private String isThirdParty;

    /** 购买日期 */
    @Excel(name = "购买日期", width = 120, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date purchaseDate;

    /** 保修截止日期 */
    @Excel(name = "保修截止", width = 120, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date warrantyEndDate;

    /** 安装位置 */
    @Excel(name = "安装位置")
    private String location;

    /** 二维码/NFC标签编号 */
    @Excel(name = "二维码编号")
    private String qrCode;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=故障,2=维修中,3=已报废")
    private String status;

/** 健康度评分 */
    @Excel(name = "健康度")
    private Integer healthScore;

    /** 动态巡检指标配置(JSON) */
    private String inspectionConfig;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

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

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getAssetCode() { return assetCode; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getIsThirdParty() { return isThirdParty; }
    public void setIsThirdParty(String isThirdParty) { this.isThirdParty = isThirdParty; }

    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }

    public Date getWarrantyEndDate() { return warrantyEndDate; }
    public void setWarrantyEndDate(Date warrantyEndDate) { this.warrantyEndDate = warrantyEndDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }

    public String getInspectionConfig() { return inspectionConfig; }
    public void setInspectionConfig(String inspectionConfig) { this.inspectionConfig = inspectionConfig; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

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
        return "ZwAsset{assetId=" + assetId + ", assetCode='" + assetCode + "', assetName='" + assetName + "'}";
    }
}
