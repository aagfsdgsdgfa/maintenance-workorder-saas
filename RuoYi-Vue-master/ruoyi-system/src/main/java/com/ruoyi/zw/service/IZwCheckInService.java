package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwCheckIn;
import java.util.List;

public interface IZwCheckInService {
    List<ZwCheckIn> selectZwCheckInList(ZwCheckIn checkIn);
    ZwCheckIn selectZwCheckInById(Long checkId);
    int insertZwCheckIn(ZwCheckIn checkIn);
    int updateZwCheckIn(ZwCheckIn checkIn);
    int deleteZwCheckInByIds(Long[] checkIds);
    List<ZwCheckIn> selectCheckInByWorkOrderId(Long workOrderId);
}
