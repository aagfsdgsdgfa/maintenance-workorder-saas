package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwRepairApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ZwRepairApplyMapper {

    List<ZwRepairApply> selectZwRepairApplyList(ZwRepairApply repairApply);

    List<ZwRepairApply> selectZwRepairApplyListByCustomerId(@Param("customerId") Long customerId);

    ZwRepairApply selectZwRepairApplyById(Long applyId);

    int insertZwRepairApply(ZwRepairApply repairApply);

    int updateZwRepairApply(ZwRepairApply repairApply);

    int deleteZwRepairApplyByIds(Long[] applyIds);

    /**
     * 批量更新状态
     */
    int updateStatusById(@Param("applyId") Long applyId, @Param("status") String status);

    /**
     * 通过工单ID更新报修申请状态
     */
    int updateStatusByWorkOrderId(@Param("workOrderId") Long workOrderId, @Param("status") String status);

    List<ZwRepairApply> selectZwRepairApplyListForExport(ZwRepairApply repairApply);
}
