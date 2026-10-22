package com.ruoyi.zw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 客户工单Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface ZwCustomerWorkOrderMapper {

    /**
     * 查询客户工单列表（带筛选分页）
     *
     * @param status 工单状态
     * @param orderType 工单类型
     * @param assetId 资产ID
     * @param customerId 客户ID
     * @param orderNo 工单编号（模糊）
     * @return 工单集合
     */
    List<Map<String, Object>> selectZwCustomerWorkOrderList(
        @Param("status") String status,
        @Param("orderType") String orderType,
        @Param("assetId") Long assetId,
        @Param("customerId") Long customerId,
        @Param("orderNo") String orderNo
    );

    /**
     * 查询客户工单总数（用于分页）
     *
     * @param status 工单状态
     * @param orderType 工单类型
     * @param assetId 资产ID
     * @param customerId 客户ID
     * @param orderNo 工单编号（模糊）
     * @return 工单总数
     */
    int selectZwCustomerWorkOrderCount(
        @Param("status") String status,
        @Param("orderType") String orderType,
        @Param("assetId") Long assetId,
        @Param("customerId") Long customerId,
        @Param("orderNo") String orderNo
    );
}
