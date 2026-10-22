package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwAssetLogs;
import java.util.List;

public interface IZwAssetLogsService {
    /**
     * 写入资产日志（工单/巡检完成时调用）
     */
    void insertAssetLog(Long assetId, String operationType, Long workOrderId,
                        Long operatorId, String operatorName, String description);

    /**
     * 查询资产日志列表
     */
    List<ZwAssetLogs> selectLogsByAssetId(Long assetId);
}
