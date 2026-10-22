package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwInspectionPlan;
import java.util.List;

public interface ZwInspectionPlanMapper {
    List<ZwInspectionPlan> selectZwInspectionPlanList(ZwInspectionPlan plan);
    ZwInspectionPlan selectZwInspectionPlanById(Long planId);
    int insertZwInspectionPlan(ZwInspectionPlan plan);
    int updateZwInspectionPlan(ZwInspectionPlan plan);
    int deleteZwInspectionPlanByIds(Long[] planIds);
    List<ZwInspectionPlan> selectActivePlans();
}
