package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwSatisfaction;
import java.util.List;

public interface IZwSatisfactionService {
    List<ZwSatisfaction> selectZwSatisfactionList(ZwSatisfaction satisfaction);
    ZwSatisfaction selectZwSatisfactionById(Long evalId);
    int insertZwSatisfaction(ZwSatisfaction satisfaction);
    int updateZwSatisfaction(ZwSatisfaction satisfaction);
    int deleteZwSatisfactionByIds(Long[] evalIds);
    ZwSatisfaction selectSatisfactionByWorkOrderId(Long workOrderId);
}
