package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwAssetLogs;
import com.ruoyi.zw.mapper.ZwAssetLogsMapper;
import com.ruoyi.zw.service.IZwAssetLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ZwAssetLogsServiceImpl implements IZwAssetLogsService {

    @Autowired
    private ZwAssetLogsMapper assetLogsMapper;

    @Override
    public void insertAssetLog(Long assetId, String operationType, Long workOrderId,
                               Long operatorId, String operatorName, String description) {
        if (assetId == null) return;
        ZwAssetLogs log = new ZwAssetLogs();
        log.setAssetId(assetId);
        log.setOperationType(operationType);
        log.setWorkOrderId(workOrderId);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setDescription(description);
        log.setOperateTime(new Date());
        log.setDelFlag("0");
        assetLogsMapper.insertZwAssetLogs(log);
    }

    @Override
    public List<ZwAssetLogs> selectLogsByAssetId(Long assetId) {
        return assetLogsMapper.selectZwAssetLogsByAssetId(assetId);
    }
}
