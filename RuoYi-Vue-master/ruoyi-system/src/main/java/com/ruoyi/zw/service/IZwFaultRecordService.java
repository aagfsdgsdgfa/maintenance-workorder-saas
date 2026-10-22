package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwFaultRecord;
import java.util.List;

public interface IZwFaultRecordService {
    List<ZwFaultRecord> selectZwFaultRecordList(ZwFaultRecord faultRecord);
    ZwFaultRecord selectZwFaultRecordById(Long faultId);
    int insertZwFaultRecord(ZwFaultRecord faultRecord);
    int updateZwFaultRecord(ZwFaultRecord faultRecord);
    int deleteZwFaultRecordByIds(Long[] faultIds);
    Long faultToWorkOrder(Long faultId);
    List<ZwFaultRecord> selectFaultRecordsByWorkOrderId(Long workOrderId);

    /**
     * 标记故障已解决
     */
    int resolveFault(Long faultId, String resolution);

    /**
     * 新增故障并同时创建工单（派发给指定工程师）
     */
    Long createFaultWithWorkOrder(ZwFaultRecord faultRecord, Long assignedTo);
}
