package com.ruoyi.zw.service.impl;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.zw.domain.ZwEngineer;
import com.ruoyi.zw.domain.vo.ZwDispatchHistoryVo;
import com.ruoyi.zw.domain.vo.ZwEngineerVo;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.mapper.ZwEngineerMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.service.IZwNotificationService;
import com.ruoyi.zw.service.IZwEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 工程师管理服务实现
 */
@Service
public class ZwEngineerServiceImpl implements IZwEngineerService {

    private static final String ENGINEER_ROLE_KEY = "zw_engineer";

    @Autowired
    private ZwEngineerMapper engineerMapper;

    @Autowired
    private ZwWorkOrderMapper workOrderMapper;

    @Autowired
    private IZwNotificationService notificationService;

    @Autowired
    private SysRoleMapper roleMapper;

@Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public List<ZwEngineerVo> selectEngineerList(ZwEngineer engineer) {
        return engineerMapper.selectEngineerList(engineer);
    }

    @Override
    public ZwEngineerVo selectEngineerById(Long engineerId) {
        return engineerMapper.selectEngineerById(engineerId);
    }

    @Override
    @Transactional
    public int insertEngineer(ZwEngineer engineer) {
        if (engineer.getUserId() == null) {
            throw new ServiceException("关联用户ID不能为空");
        }
        if (engineerMapper.selectEngineerByUserId(engineer.getUserId()) != null) {
            throw new ServiceException("该用户已是工程师，无需重复添加");
        }
        // 如果前端传了 realName（工程师姓名），同步更新 sys_user 的 nick_name
        if (StringUtils.isNotEmpty(engineer.getRealName())) {
            SysUser userUpdate = new SysUser();
            userUpdate.setUserId(engineer.getUserId());
            userUpdate.setNickName(engineer.getRealName());
            userMapper.updateUser(userUpdate);
        }
        int rows = engineerMapper.insertEngineerExt(engineer);
        // 同时给用户分配 zw_engineer 角色
        assignEngineerRole(engineer.getUserId());
        return rows;
    }

    @Override
    @Transactional
    public int updateEngineer(ZwEngineer engineer) {
        if (engineer.getUserId() == null) {
            throw new ServiceException("关联用户ID不能为空");
        }
        // 如果传了 realName，同步更新 sys_user 的 nick_name
        if (StringUtils.isNotEmpty(engineer.getRealName())) {
            SysUser userUpdate = new SysUser();
            userUpdate.setUserId(engineer.getUserId());
            userUpdate.setNickName(engineer.getRealName());
            userMapper.updateUser(userUpdate);
        }
        return engineerMapper.updateEngineerExt(engineer);
    }

    @Override
    @Transactional
    public int deleteEngineerById(Long engineerId) {
        int rows = engineerMapper.deleteEngineerExtById(engineerId);
        removeEngineerRole(engineerId);
        return rows;
    }

    @Override
    @Transactional
    public int deleteEngineerByIds(Long[] engineerIds) {
        int rows = engineerMapper.deleteEngineerExtByIds(engineerIds);
        for (Long engineerId : engineerIds) {
            removeEngineerRole(engineerId);
        }
        return rows;
    }

    @Override
    public List<ZwEngineerVo> selectDispatchableEngineers(ZwEngineer engineer) {
        ZwEngineer query = new ZwEngineer();
        query.setStatus("0");
        if (engineer != null) {
            query.setRealName(engineer.getRealName());
        }
        return engineerMapper.selectEngineerList(query);
    }

    @Override
    @Transactional
    public int dispatchWorkOrder(Long workOrderId, Long engineerId) {
        ZwWorkOrder workOrder = workOrderMapper.selectZwWorkOrderById(workOrderId);
        if (workOrder == null) {
            throw new ServiceException("工单不存在");
        }
        if (!"0".equals(workOrder.getStatus())) {
            throw new ServiceException("只能向待接单状态的工单派发工程师");
        }
        ZwEngineerVo engineer = engineerMapper.selectEngineerById(engineerId);
        if (engineer == null) {
            throw new ServiceException("工程师不存在或无权登录小程序");
        }
        ZwWorkOrder update = new ZwWorkOrder();
        update.setWorkOrderId(workOrderId);
        update.setAssignedTo(engineerId);
        update.setAssignedBy(SecurityUtils.getUserId());
        int rows = workOrderMapper.updateZwWorkOrder(update);
        if (rows > 0) {
            try {
                notificationService.sendNotification(
                    engineer.getUserId(), workOrder.getCustomerId(),
                    "新工单派发",
                    "您有一个新的维修工单：" + workOrder.getTitle() + "，请及时处理",
                    "1",
                    workOrderId
                );
            } catch (Exception e) {
                // 通知失败不影响主流程
            }
        }
        return rows;
    }

    @Override
    public List<ZwDispatchHistoryVo> selectDispatchHistoryByEngineerId(Long engineerId) {
        return engineerMapper.selectDispatchHistoryByEngineerId(engineerId);
    }

    private void assignEngineerRole(Long userId) {
        var role = roleMapper.checkRoleKeyUnique(ENGINEER_ROLE_KEY);
        if (role != null && role.getRoleId() != null) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(role.getRoleId());
            userRoleMapper.deleteUserRoleInfo(ur);
            userRoleMapper.batchUserRole(List.of(ur));
        }
    }

    private void removeEngineerRole(Long userId) {
        var role = roleMapper.checkRoleKeyUnique(ENGINEER_ROLE_KEY);
        if (role != null && role.getRoleId() != null) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(role.getRoleId());
            userRoleMapper.deleteUserRoleInfo(ur);
        }
    }
}
