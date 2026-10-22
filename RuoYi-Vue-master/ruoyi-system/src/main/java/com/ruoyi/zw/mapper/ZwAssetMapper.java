package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwAsset;
import org.apache.ibatis.annotations.Mapper;
import java.util.Date;
import java.util.List;

/**
 * 资产Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface ZwAssetMapper {

    /**
     * 查询资产列表
     *
     * @param asset 资产信息
     * @return 资产集合
     */
    List<ZwAsset> selectZwAssetList(ZwAsset asset);

    /**
     * 查询资产详情
     *
     * @param assetId 资产ID
     * @return 资产信息
     */
    ZwAsset selectZwAssetById(Long assetId);

    /**
     * 根据二维码查询资产
     *
     * @param qrCode 二维码编号
     * @return 资产信息
     */
    ZwAsset selectZwAssetByQrCode(String qrCode);

    /**
     * 新增资产
     *
     * @param asset 资产信息
     * @return 影响行数
     */
    int insertZwAsset(ZwAsset asset);

    /**
     * 修改资产
     *
     * @param asset 资产信息
     * @return 影响行数
     */
    int updateZwAsset(ZwAsset asset);

    /**
     * 删除资产（逻辑删除）
     *
     * @param assetIds 资产ID数组
     * @return 影响行数
     */
    int deleteZwAssetByIds(Long[] assetIds);

    /**
     * 更新资产状态
     *
     * @param assetId 资产ID
     * @param status 状态
     * @return 影响行数
     */
    int updateAssetStatus(Long assetId, String status);

    /**
     * 查询质保即将到期的资产（warranty_end_date 在 from~to 之间）
     *
     * @param from 开始日期
     * @param to 结束日期
     * @return 资产列表
     */
    List<ZwAsset> selectAssetsWithExpiringWarranty(Date from, Date to);

    /**
     * 批量查询资产
     *
     * @param assetIds 资产ID列表
     * @return 资产列表
     */
    List<ZwAsset> selectZwAssetByIds(List<Long> assetIds);
}
