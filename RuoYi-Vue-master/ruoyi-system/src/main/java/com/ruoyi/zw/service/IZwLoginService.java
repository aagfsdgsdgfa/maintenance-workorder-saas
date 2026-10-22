package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.vo.EngineerLoginVo;

/**
 * 工程师登录服务接口
 */
public interface IZwLoginService {

    /**
     * 工程师登录
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 验证码 uuid 密码
     * @return 登录结果（token + 用户信息）
     */
    EngineerLoginVo login(String username, String password, String code, String uuid);

    /**
     * 获取当前登录工程师信息
     */
    EngineerLoginVo getLoginInfo();

    /**
     * 工程师登出
     */
    void logout();
}
