package com.ruoyi.zw.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备检查项结果表 zw_inspection_result
 */
public class ZwInspectionResult {
    private Long resultId;
    private Long taskId;
    private Long pointId;
    private Long assetId;
    private Long engineerId;
    private String checkItems;
    private String photoUrls;
    private String voiceText;
    private String remark;
    private String isNormal;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String delFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getPointId() { return pointId; }
    public void setPointId(Long pointId) { this.pointId = pointId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Long getEngineerId() { return engineerId; }
    public void setEngineerId(Long engineerId) { this.engineerId = engineerId; }

    public String getCheckItems() { return checkItems; }
    public void setCheckItems(String checkItems) { this.checkItems = checkItems; }

    public String getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(String photoUrls) { this.photoUrls = photoUrls; }

    public String getVoiceText() { return voiceText; }
    public void setVoiceText(String voiceText) { this.voiceText = voiceText; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getIsNormal() { return isNormal; }
    public void setIsNormal(String isNormal) { this.isNormal = isNormal; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

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
}
