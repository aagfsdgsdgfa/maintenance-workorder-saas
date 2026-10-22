package com.ruoyi.zw.domain;

import java.util.Date;

/**
 * 巡检点表 zw_inspection_point
 *
 * @author ruoyi
 */
public class ZwInspectionPoint {
    private static final long serialVersionUID = 1L;

    /** 巡检点ID */
    private Long pointId;

    /** 所属资产ID */
    private Long assetId;

    /** 巡检点名称 */
    private String pointName;

    /** 巡检点编码/二维码 */
    private String pointCode;

    /** 巡检点位置描述 */
    private String pointLocation;

    /** 纬度 */
    private java.math.BigDecimal latitude;

    /** 经度 */
    private java.math.BigDecimal longitude;

    /** 扫码有效半径(米)，默认50 */
    private Integer scanRadius;

    /** 关联表单配置ID */
    private Long formConfigId;

    /** 巡检检查项JSON */
    private String checkItems;

    /** 状态：0正常 1停用 */
    private String status;

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

    // ---------- 关联字段（用于列表展示） ----------
    /** 资产名称 */
    private String assetName;

    /** 资产编码 */
    private String assetCode;

    public Long getPointId() { return pointId; }
    public void setPointId(Long pointId) { this.pointId = pointId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getPointName() { return pointName; }
    public void setPointName(String pointName) { this.pointName = pointName; }

    public String getPointCode() { return pointCode; }
    public void setPointCode(String pointCode) { this.pointCode = pointCode; }

    public String getPointLocation() { return pointLocation; }
    public void setPointLocation(String pointLocation) { this.pointLocation = pointLocation; }

    public java.math.BigDecimal getLatitude() { return latitude; }
    public void setLatitude(java.math.BigDecimal latitude) { this.latitude = latitude; }

    public java.math.BigDecimal getLongitude() { return longitude; }
    public void setLongitude(java.math.BigDecimal longitude) { this.longitude = longitude; }

    public Integer getScanRadius() { return scanRadius; }
    public void setScanRadius(Integer scanRadius) { this.scanRadius = scanRadius; }

    public Long getFormConfigId() { return formConfigId; }
    public void setFormConfigId(Long formConfigId) { this.formConfigId = formConfigId; }

    public String getCheckItems() { return checkItems; }
    public void setCheckItems(String checkItems) { this.checkItems = checkItems; }

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

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getAssetCode() { return assetCode; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }

    @Override
    public String toString() {
        return "ZwInspectionPoint{pointId=" + pointId + ", pointName='" + pointName + "', assetId=" + assetId + "}";
    }
}
