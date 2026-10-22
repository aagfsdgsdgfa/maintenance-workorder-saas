package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwInspectionResult;
import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.mapper.ZwInspectionResultMapper;
import com.ruoyi.zw.service.IZwAssetLogsService;
import com.ruoyi.zw.service.IZwInspectionResultService;
import com.ruoyi.zw.service.IZwInspectionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ZwInspectionResultServiceImpl implements IZwInspectionResultService {

    @Autowired
    private ZwInspectionResultMapper resultMapper;

    @Autowired
    private IZwInspectionTaskService taskService;

    @Autowired
    private IZwAssetLogsService assetLogsService;

    @Override
    public List<ZwInspectionResult> selectZwInspectionResultList(ZwInspectionResult result) {
        return resultMapper.selectZwInspectionResultList(result);
    }

    @Override
    public ZwInspectionResult selectZwInspectionResultById(Long resultId) {
        return resultMapper.selectZwInspectionResultById(resultId);
    }

    @Override
    public ZwInspectionResult selectResultByTaskId(Long taskId) {
        return resultMapper.selectResultByTaskId(taskId);
    }

    @Override
    @Transactional
    public int insertZwInspectionResult(ZwInspectionResult result) {
        result.setDelFlag("0");
        int rows = resultMapper.insertZwInspectionResult(result);
        // 写入资产时间轴
        if (rows > 0 && result.getTaskId() != null) {
            ZwInspectionTask task = taskService.selectZwInspectionTaskById(result.getTaskId());
            if (task != null && task.getAssetId() != null) {
                assetLogsService.insertAssetLog(
                    task.getAssetId(),
                    "inspect",
                    null,
                    null,
                    null,
                    "巡检完成，检查项 " + ("1".equals(result.getIsNormal()) ? "正常" : "异常")
                );
            }
        }
        return rows;
    }

    @Override
    public int updateZwInspectionResult(ZwInspectionResult result) {
        return resultMapper.updateZwInspectionResult(result);
    }

    @Override
    public int deleteZwInspectionResultByIds(Long[] resultIds) {
        return resultMapper.deleteZwInspectionResultByIds(resultIds);
    }
}
