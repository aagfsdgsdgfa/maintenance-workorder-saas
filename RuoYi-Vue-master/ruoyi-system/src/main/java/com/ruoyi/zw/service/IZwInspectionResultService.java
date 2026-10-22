package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwInspectionResult;
import java.util.List;

public interface IZwInspectionResultService {
    List<ZwInspectionResult> selectZwInspectionResultList(ZwInspectionResult result);
    ZwInspectionResult selectZwInspectionResultById(Long resultId);
    ZwInspectionResult selectResultByTaskId(Long taskId);
    int insertZwInspectionResult(ZwInspectionResult result);
    int updateZwInspectionResult(ZwInspectionResult result);
    int deleteZwInspectionResultByIds(Long[] resultIds);
}