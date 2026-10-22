package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwFaultRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ZwFaultRecordMapper {
    List<ZwFaultRecord> selectZwFaultRecordList(ZwFaultRecord faultRecord);
    ZwFaultRecord selectZwFaultRecordById(Long faultId);
    int insertZwFaultRecord(ZwFaultRecord faultRecord);
    int updateZwFaultRecord(ZwFaultRecord faultRecord);
    int deleteZwFaultRecordByIds(Long[] faultIds);
    List<ZwFaultRecord> selectFaultRecordsByWorkOrderId(Long workOrderId);
    int countToday();

    /**
     * 查询工程师指定日期的故障记录（日报聚合用）
     */
    List<ZwFaultRecord> selectByEngineerAndDate(@Param("createBy") String createBy, @Param("date") String date);
}
