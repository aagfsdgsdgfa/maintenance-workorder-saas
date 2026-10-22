package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwAsset;
import java.util.List;
import java.util.Map;

/**
 * 客户资产Service接口
 *
 * @author ruoyi
 */
public interface IZwCustomerAssetService {

    /**
     * 查询客户资产列表（分页）
     *
     * @param customerId 客户ID
     * @param assetName 资产名称（模糊）
     * @param assetType 资产类型
     * @param status 状态
     * @param warrantyStatus 质保状态
     * @param qrCode 二维码
     * @param assetCode SN码
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果（total/pageNum/pageSize/rows）
     */
    Map<String, Object> selectAssetList(Long customerId, String assetName, String assetType,
                                        String status, String warrantyStatus, String qrCode,
                                        String assetCode, int pageNum, int pageSize);

    /**
     * 查询资产详情
     *
     * @param assetId 资产ID
     * @return 资产信息
     */
    ZwAsset selectAssetDetail(Long assetId);

    /**
     * 查询资产操作日志时间轴
     *
     * @param assetId 资产ID
     * @return 操作日志列表
     */
    List<Map<String, Object>> selectAssetLogs(Long assetId);
}
