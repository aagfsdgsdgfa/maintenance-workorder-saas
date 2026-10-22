package com.ruoyi.zw.domain;

import java.util.Date;

/**
 * 合同资产关联表 contract_asset_relations
 *
 * @author ruoyi
 */
public class ContractAssetRelation {
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long relationId;

    /** 合同ID */
    private Long contractId;

    /** 资产ID */
    private Long assetId;

    /** 创建时间 */
    private Date createTime;

    public Long getRelationId() { return relationId; }
    public void setRelationId(Long relationId) { this.relationId = relationId; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "ContractAssetRelation{relationId=" + relationId + ", contractId=" + contractId + ", assetId=" + assetId + "}";
    }
}
