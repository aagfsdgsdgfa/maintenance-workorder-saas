package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.MaintenanceContract;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 维保合同Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface ZwContractMapper {

    /**
     * 查询维保合同列表
     *
     * @param contract 维保合同信息
     * @return 维保合同集合
     */
    List<MaintenanceContract> selectZwContractList(MaintenanceContract contract);

    /**
     * 查询维保合同详情
     *
     * @param contractId 合同ID
     * @return 维保合同信息
     */
    MaintenanceContract selectZwContractById(Long contractId);

    /**
     * 根据客户ID查询合同列表
     *
     * @param customerId 客户ID
     * @return 维保合同集合
     */
    List<MaintenanceContract> selectZwContractByCustomerId(Long customerId);

    /**
     * 新增维保合同
     *
     * @param contract 维保合同信息
     * @return 影响行数
     */
    int insertZwContract(MaintenanceContract contract);

    /**
     * 修改维保合同
     *
     * @param contract 维保合同信息
     * @return 影响行数
     */
    int updateZwContract(MaintenanceContract contract);

    /**
     * 删除维保合同（逻辑删除）
     *
     * @param contractIds 合同ID数组
     * @return 影响行数
     */
    int deleteZwContractByIds(Long[] contractIds);
}
