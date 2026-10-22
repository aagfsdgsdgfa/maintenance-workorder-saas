package com.ruoyi.zw.domain;

import java.util.Date;

/**
 * 资产时间轴日志表 zw_asset_logs
 *
 * @author ruoyi
 */
public class ZwAssetLogs {
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long logId;

    /** 资产ID */
    private Long assetId;

    /** 关联工单ID */
    private Long workOrderId;

    /** 操作类型：create/move/inspect/repair/warranty_renew/contract_renew/scrap */
    private String operationType;

    /** 操作描述 */
    private String description;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人名称 */
    private String operatorName;

    /** 操作时间 */
    private Date operateTime;

    /** 附加数据(JSON) */
    private String extraData;

    /** 删除标志 */
    private String delFlag;

    /** 创建时间 */
    private Date createTime;

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public Date getOperateTime() { return operateTime; }
    public void setOperateTime(Date operateTime) { this.operateTime = operateTime; }

    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "ZwAssetLogs{logId=" + logId + ", assetId=" + assetId + ", operationType='" + operationType + "'}";
    }
}
