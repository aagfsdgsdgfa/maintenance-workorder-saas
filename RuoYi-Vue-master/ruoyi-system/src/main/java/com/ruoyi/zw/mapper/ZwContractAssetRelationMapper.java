package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ContractAssetRelation;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 合同资产关联Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface ZwContractAssetRelationMapper {

    /**
     * 查询合同资产关联列表
     *
     * @param relation 关联信息
     * @return 关联集合
     */
    List<ContractAssetRelation> selectZwContractAssetRelationList(ContractAssetRelation relation);

    /**
     * 根据合同ID查询关联的资产ID列表
     *
     * @param contractId 合同ID
     * @return 资产ID列表
     */
    List<Long> selectAssetIdsByContractId(Long contractId);

    /**
     * 根据资产ID查询关联的合同ID列表
     *
     * @param assetId 资产ID
     * @return 合同ID列表
     */
    List<Long> selectContractIdsByAssetId(Long assetId);

    /**
     * 新增合同资产关联
     *
     * @param relation 关联信息
     * @return 影响行数
     */
    int insertZwContractAssetRelation(ContractAssetRelation relation);

    /**
     * 删除合同资产关联
     *
     * @param relationIds 关联ID数组
     * @return 影响行数
     */
    int deleteZwContractAssetRelationByIds(Long[] relationIds);

    /**
     * 根据合同ID删除所有关联
     *
     * @param contractId 合同ID
     * @return 影响行数
     */
    int deleteByContractId(Long contractId);

    /**
     * 根据资产ID删除所有关联
     *
     * @param assetId 资产ID
     * @return 影响行数
     */
    int deleteByAssetId(Long assetId);
}
