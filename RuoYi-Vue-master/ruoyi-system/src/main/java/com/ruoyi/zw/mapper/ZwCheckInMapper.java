package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwCheckIn;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ZwCheckInMapper {
    List<ZwCheckIn> selectZwCheckInList(ZwCheckIn checkIn);
    ZwCheckIn selectZwCheckInById(Long checkId);
    int insertZwCheckIn(ZwCheckIn checkIn);
    int updateZwCheckIn(ZwCheckIn checkIn);
    int deleteZwCheckInByIds(Long[] checkIds);
    List<ZwCheckIn> selectCheckInByWorkOrderId(Long workOrderId);
    List<ZwCheckIn> selectByEngineerAndDate(Long engineerId, String date);
}
