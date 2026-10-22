package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwAsset;
import java.util.List;

/**
 * 资产Service接口
 *
 * @author ruoyi
 */
public interface IZwAssetService {

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
     * 为资产生成二维码并返回URL
     *
     * @param assetId 资产ID
     * @return 二维码访问URL，失败返回null
     */
    String generateQrCode(Long assetId);
}
