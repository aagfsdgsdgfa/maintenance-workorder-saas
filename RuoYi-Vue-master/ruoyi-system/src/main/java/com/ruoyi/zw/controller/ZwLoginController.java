package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zw.domain.vo.EngineerLoginVo;
import com.ruoyi.zw.service.IZwLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工程师登录 Controller
 * 无需权限注解，小程序直接调用
 */
@RestController
@RequestMapping("/zw")
public class ZwLoginController extends BaseController {

    @Autowired
    private IZwLoginService loginService;

    /**
     * 工程师登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginRequest request) {
        EngineerLoginVo vo = loginService.login(
                request.getUsername(),
                request.getPassword(),
                request.getCode(),
                request.getUuid()
        );
        // 将 token 放在顶层，方便小程序前端获取
        AjaxResult result = success(vo);
        if (StringUtils.isNotEmpty(vo.getToken())) {
            result.put("token", vo.getToken());
        }
        return result;
    }

    /**
     * 获取当前登录工程师信息（刷新 token）
     */
    @GetMapping("/login/info")
    public AjaxResult getLoginInfo() {
        EngineerLoginVo vo = loginService.getLoginInfo();
        AjaxResult result = success(vo);
        if (StringUtils.isNotEmpty(vo.getToken())) {
            result.put("token", vo.getToken());
        }
        return result;
    }

    /**
     * 工程师登出
     */
    @PostMapping("/logout")
    public AjaxResult logout() {
        loginService.logout();
        return success();
    }

    /**
     * 登录请求体
     */
    public static class LoginRequest {
        private String username;
        private String password;
        private String code;
        private String uuid;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
    }
}
