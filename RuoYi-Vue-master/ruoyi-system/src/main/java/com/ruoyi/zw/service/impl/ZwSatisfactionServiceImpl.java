package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwSatisfaction;
import com.ruoyi.zw.mapper.ZwSatisfactionMapper;
import com.ruoyi.zw.service.IZwSatisfactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ZwSatisfactionServiceImpl implements IZwSatisfactionService {

    @Autowired
    private ZwSatisfactionMapper satisfactionMapper;

    @Override
    public List<ZwSatisfaction> selectZwSatisfactionList(ZwSatisfaction satisfaction) {
        return satisfactionMapper.selectZwSatisfactionList(satisfaction);
    }

    @Override
    public ZwSatisfaction selectZwSatisfactionById(Long evalId) {
        return satisfactionMapper.selectZwSatisfactionById(evalId);
    }

    @Override
    public int insertZwSatisfaction(ZwSatisfaction satisfaction) {
        satisfaction.setDelFlag("0");
        // 强制置空 evalId，防止前端传入导致唯一索引冲突；由数据库 auto_increment 生成
        satisfaction.setEvalId(null);
        if (satisfaction.getEvalTime() == null) {
            satisfaction.setEvalTime(new Date());
        }
        return satisfactionMapper.insertZwSatisfaction(satisfaction);
    }

    @Override
    public int updateZwSatisfaction(ZwSatisfaction satisfaction) {
        return satisfactionMapper.updateZwSatisfaction(satisfaction);
    }

    @Override
    public int deleteZwSatisfactionByIds(Long[] evalIds) {
        return satisfactionMapper.deleteZwSatisfactionByIds(evalIds);
    }

    @Override
    public ZwSatisfaction selectSatisfactionByWorkOrderId(Long workOrderId) {
        return satisfactionMapper.selectSatisfactionByWorkOrderId(workOrderId);
    }
}
