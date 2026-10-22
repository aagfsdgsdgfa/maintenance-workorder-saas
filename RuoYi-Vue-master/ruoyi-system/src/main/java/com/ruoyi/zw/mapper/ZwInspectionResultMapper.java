package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwInspectionResult;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ZwInspectionResultMapper {
    List<ZwInspectionResult> selectZwInspectionResultList(ZwInspectionResult result);
    ZwInspectionResult selectZwInspectionResultById(Long resultId);
    int insertZwInspectionResult(ZwInspectionResult result);
    int updateZwInspectionResult(ZwInspectionResult result);
    int deleteZwInspectionResultByIds(Long[] resultIds);
    ZwInspectionResult selectResultByTaskId(Long taskId);

    /**
     * 查询工程师指定日期的巡检结果
     */
    List<ZwInspectionResult> selectByEngineerAndDate(Long engineerId, String date);
}