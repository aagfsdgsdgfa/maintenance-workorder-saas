package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwEngineer;
import com.ruoyi.zw.domain.vo.ZwDispatchHistoryVo;
import com.ruoyi.zw.domain.vo.ZwEngineerVo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 工程师管理 Mapper 接口
 */
public interface ZwEngineerMapper {

    /**
     * 查询工程师列表（关联 sys_user + zw_engineer_ext）
     */
    List<ZwEngineerVo> selectEngineerList(ZwEngineer engineer);

    /**
     * 查询工程师详情
     */
    ZwEngineerVo selectEngineerById(@Param("engineerId") Long engineerId);

    /**
     * 根据 userId 查询工程师
     */
    ZwEngineerVo selectEngineerByUserId(@Param("userId") Long userId);

    /**
     * 新增工程师扩展信息
     */
    int insertEngineerExt(ZwEngineer engineer);

    /**
     * 修改工程师扩展信息
     */
    int updateEngineerExt(ZwEngineer engineer);

    /**
     * 删除工程师扩展信息
     */
    int deleteEngineerExtById(@Param("engineerId") Long engineerId);

    /**
     * 批量删除工程师扩展信息
     */
    int deleteEngineerExtByIds(@Param("engineerIds") Long[] engineerIds);

    /**
     * 查询某工程师的派发历史（工单）
     */
    List<ZwDispatchHistoryVo> selectDispatchHistoryByEngineerId(@Param("engineerId") Long engineerId);
}
