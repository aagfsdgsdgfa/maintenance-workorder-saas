package com.ruoyi.zw.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwCheckIn;
import com.ruoyi.zw.mapper.ZwCheckInMapper;
import com.ruoyi.zw.service.IZwCheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ZwCheckInServiceImpl implements IZwCheckInService {

    @Autowired
    private ZwCheckInMapper checkInMapper;

    @Override
    public List<ZwCheckIn> selectZwCheckInList(ZwCheckIn checkIn) {
        return checkInMapper.selectZwCheckInList(checkIn);
    }

    @Override
    public ZwCheckIn selectZwCheckInById(Long checkId) {
        return checkInMapper.selectZwCheckInById(checkId);
    }

    @Override
    public int insertZwCheckIn(ZwCheckIn checkIn) {
        checkIn.setDelFlag("0");
        checkIn.setCheckTime(new Date());
        // 默认从当前登录用户填充 engineerId 和 createBy
        if (checkIn.getEngineerId() == null) {
            checkIn.setEngineerId(SecurityUtils.getUserId());
        }
        if (checkIn.getCreateBy() == null || checkIn.getCreateBy().trim().isEmpty()) {
            checkIn.setCreateBy(SecurityUtils.getUsername());
        }
        return checkInMapper.insertZwCheckIn(checkIn);
    }

    @Override
    public int updateZwCheckIn(ZwCheckIn checkIn) {
        checkIn.setUpdateTime(new Date());
        return checkInMapper.updateZwCheckIn(checkIn);
    }

    @Override
    public int deleteZwCheckInByIds(Long[] checkIds) {
        return checkInMapper.deleteZwCheckInByIds(checkIds);
    }

    @Override
    public List<ZwCheckIn> selectCheckInByWorkOrderId(Long workOrderId) {
        return checkInMapper.selectCheckInByWorkOrderId(workOrderId);
    }
}
