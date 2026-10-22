package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwInspectionPlan;
import java.util.List;

public interface IZwInspectionPlanService {
    List<ZwInspectionPlan> selectZwInspectionPlanList(ZwInspectionPlan plan);
    ZwInspectionPlan selectZwInspectionPlanById(Long planId);
    int insertZwInspectionPlan(ZwInspectionPlan plan);
    int updateZwInspectionPlan(ZwInspectionPlan plan);
    int deleteZwInspectionPlanByIds(Long[] planIds);
}
