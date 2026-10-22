package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwEngineer;
import com.ruoyi.zw.domain.vo.ZwDispatchHistoryVo;
import com.ruoyi.zw.domain.vo.ZwEngineerVo;
import java.util.List;

/**
 * 工程师管理服务接口
 */
public interface IZwEngineerService {

    /**
     * 查询工程师列表
     */
    List<ZwEngineerVo> selectEngineerList(ZwEngineer engineer);

    /**
     * 查询工程师详情
     */
    ZwEngineerVo selectEngineerById(Long engineerId);

    /**
     * 新增工程师
     */
    int insertEngineer(ZwEngineer engineer);

    /**
     * 修改工程师
     */
    int updateEngineer(ZwEngineer engineer);

    /**
     * 删除工程师
     */
    int deleteEngineerById(Long engineerId);

    /**
     * 批量删除工程师
     */
    int deleteEngineerByIds(Long[] engineerIds);

    /**
     * 获取所有可派发工程师（在线、正常状态，支持姓名搜索和分页）
     */
    List<ZwEngineerVo> selectDispatchableEngineers(ZwEngineer engineer);

    /**
     * 工单派发（分配工程师）
     */
    int dispatchWorkOrder(Long workOrderId, Long engineerId);

    /**
     * 查询工程师派发历史（工单列表）
     */
    List<ZwDispatchHistoryVo> selectDispatchHistoryByEngineerId(Long engineerId);
}
