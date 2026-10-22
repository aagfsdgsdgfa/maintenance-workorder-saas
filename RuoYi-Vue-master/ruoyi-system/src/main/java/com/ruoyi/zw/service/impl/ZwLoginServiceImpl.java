package com.ruoyi.zw.service.impl;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.zw.domain.vo.EngineerLoginVo;
import com.ruoyi.zw.service.IZwLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ZwLoginServiceImpl implements IZwLoginService {

    private static final String ENGINEER_ROLE = "zw_engineer";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ZwTokenService tokenService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysConfigService configService;

    @Override
    public EngineerLoginVo login(String username, String password, String code, String uuid) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new ServiceException("用户名或密码不能为空");
        }

        // 验证码校验
        validateCaptcha(username, code, uuid);

        // Spring Security 认证
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            // 设置到 SecurityContextHolder，SysPasswordService.validate() 会从中读取
            SecurityContextHolder.getContext().setAuthentication(authToken);
            authentication = authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new UserPasswordNotMatchException();
        } catch (Exception e) {
            throw new ServiceException("认证失败，用户名或密码错误");
        }
        // 注意：不调用 clearContext()，让框架在请求结束后自动清理

        if (authentication == null) {
            throw new ServiceException("认证服务异常");
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 校验是否为工程师角色（检查用户的角色 key）
        boolean isEngineer = loginUser.getUser().getRoles().stream()
                .anyMatch(role -> ENGINEER_ROLE.equals(role.getRoleKey()));
        if (!isEngineer) {
            throw new ServiceException("该账号不是工程师账号，无权登录小程序");
        }

        // 记录登录信息
        recordLoginInfo(loginUser.getUserId());

        // 生成 token
        String token = tokenService.createToken(loginUser);

        // 填充返回 VO
        return buildLoginVo(loginUser, token);
    }

    @Override
    public EngineerLoginVo getLoginInfo() {
        LoginUser loginUser = tokenService.getLoginUser(
                com.ruoyi.common.utils.ServletUtils.getRequest());
        if (loginUser == null) {
            throw new ServiceException("未登录或登录已过期，请重新登录");
        }
        String token = tokenService.createToken(loginUser);
        return buildLoginVo(loginUser, token);
    }

    @Override
    public void logout() {
        LoginUser loginUser = tokenService.getLoginUser(
                com.ruoyi.common.utils.ServletUtils.getRequest());
        if (loginUser != null && StringUtils.isNotEmpty(loginUser.getToken())) {
            tokenService.delLoginUser(loginUser.getToken());
        }
    }

    private EngineerLoginVo buildLoginVo(LoginUser loginUser, String token) {
        EngineerLoginVo vo = new EngineerLoginVo();
        vo.setToken(token);
        vo.setUserId(loginUser.getUserId());
        vo.setUsername(loginUser.getUsername());
        vo.setNickName(loginUser.getUser().getNickName());
        vo.setPhonenumber(loginUser.getUser().getPhonenumber());
        vo.setAvatar(loginUser.getUser().getAvatar());
        vo.setEmail(loginUser.getUser().getEmail());
        vo.setSex(loginUser.getUser().getSex());
        vo.setDeptId(loginUser.getUser().getDeptId());
        vo.setPermissions(loginUser.getPermissions());
        return vo;
    }

    private void recordLoginInfo(Long userId) {
        userService.updateLoginInfo(userId, IpUtils.getIpAddr(), new java.util.Date());
    }

    private void validateCaptcha(String username, String code, String uuid) {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            if (StringUtils.isEmpty(uuid)) {
                throw new CaptchaExpireException();
            }
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
            String cachedCode = redisCache.getCacheObject(verifyKey);
            if (cachedCode == null) {
                throw new CaptchaExpireException();
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(cachedCode)) {
                throw new CaptchaException();
            }
        }
    }
}
