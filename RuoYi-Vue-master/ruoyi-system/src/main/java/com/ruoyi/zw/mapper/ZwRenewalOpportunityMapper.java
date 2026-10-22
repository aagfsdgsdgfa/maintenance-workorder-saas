package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.RenewalOpportunity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 续保商机Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface ZwRenewalOpportunityMapper {

    /**
     * 查询续保商机列表
     *
     * @param opportunity 续保商机信息
     * @return 续保商机集合
     */
    List<RenewalOpportunity> selectZwRenewalOpportunityList(RenewalOpportunity opportunity);

    /**
     * 查询续保商机详情
     *
     * @param opportunityId 商机ID
     * @return 续保商机信息
     */
    RenewalOpportunity selectZwRenewalOpportunityById(Long opportunityId);

    /**
     * 根据客户ID查询商机列表
     *
     * @param customerId 客户ID
     * @return 续保商机集合
     */
    List<RenewalOpportunity> selectZwRenewalOpportunityByCustomerId(Long customerId);

    /**
     * 根据资产ID查询商机列表
     *
     * @param assetId 资产ID
     * @return 续保商机集合
     */
    List<RenewalOpportunity> selectZwRenewalOpportunityByAssetId(Long assetId);

    /**
     * 根据合同ID查询商机
     *
     * @param contractId 合同ID
     * @return 续保商机信息
     */
    RenewalOpportunity selectZwRenewalOpportunityByContractId(Long contractId);

    /**
     * 新增续保商机
     *
     * @param opportunity 续保商机信息
     * @return 影响行数
     */
    int insertZwRenewalOpportunity(RenewalOpportunity opportunity);

    /**
     * 修改续保商机
     *
     * @param opportunity 续保商机信息
     * @return 影响行数
     */
    int updateZwRenewalOpportunity(RenewalOpportunity opportunity);

    /**
     * 删除续保商机（逻辑删除）
     *
     * @param opportunityIds 商机ID数组
     * @return 影响行数
     */
    int deleteZwRenewalOpportunityByIds(Long[] opportunityIds);
}
