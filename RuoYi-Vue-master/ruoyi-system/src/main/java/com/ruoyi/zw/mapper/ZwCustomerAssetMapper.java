package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwAsset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 客户资产Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface ZwCustomerAssetMapper {

    /**
     * 查询客户资产列表（带筛选分页）
     *
     * @param assetName 资产名称（模糊）
     * @param assetType 资产类型
     * @param status 状态
     * @param qrCode 二维码编号
     * @param customerId 客户ID
     * @return 资产集合
     */
    List<ZwAsset> selectZwCustomerAssetList(
        @Param("assetName") String assetName,
        @Param("assetType") String assetType,
        @Param("status") String status,
        @Param("qrCode") String qrCode,
        @Param("customerId") Long customerId
    );

    /**
     * 查询客户资产总数（用于分页）
     *
     * @param assetName 资产名称（模糊）
     * @param assetType 资产类型
     * @param status 状态
     * @param qrCode 二维码编号
     * @param customerId 客户ID
     * @return 资产总数
     */
    int selectZwCustomerAssetCount(
        @Param("assetName") String assetName,
        @Param("assetType") String assetType,
        @Param("status") String status,
        @Param("qrCode") String qrCode,
        @Param("customerId") Long customerId
    );

    /**
     * 查询单个客户资产详情
     *
     * @param assetId 资产ID
     * @param customerId 客户ID
     * @return 资产信息
     */
    ZwAsset selectZwCustomerAssetById(@Param("assetId") Long assetId, @Param("customerId") Long customerId);
}
