package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwInspectionPlan;
import com.ruoyi.zw.mapper.ZwInspectionPlanMapper;
import com.ruoyi.zw.service.IZwInspectionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZwInspectionPlanServiceImpl implements IZwInspectionPlanService {

    @Autowired
    private ZwInspectionPlanMapper planMapper;

    @Override
    public List<ZwInspectionPlan> selectZwInspectionPlanList(ZwInspectionPlan plan) {
        return planMapper.selectZwInspectionPlanList(plan);
    }

    @Override
    public ZwInspectionPlan selectZwInspectionPlanById(Long planId) {
        return planMapper.selectZwInspectionPlanById(planId);
    }

    @Override
    public int insertZwInspectionPlan(ZwInspectionPlan plan) {
        plan.setDelFlag("0");
        return planMapper.insertZwInspectionPlan(plan);
    }

    @Override
    public int updateZwInspectionPlan(ZwInspectionPlan plan) {
        return planMapper.updateZwInspectionPlan(plan);
    }

    @Override
    public int deleteZwInspectionPlanByIds(Long[] planIds) {
        return planMapper.deleteZwInspectionPlanByIds(planIds);
    }
}
