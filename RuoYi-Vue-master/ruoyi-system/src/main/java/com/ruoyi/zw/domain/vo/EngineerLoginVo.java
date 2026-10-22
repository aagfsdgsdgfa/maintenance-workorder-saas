package com.ruoyi.zw.domain.vo;

import java.util.Set;

/**
 * 工程师登录响应 VO
 */
public class EngineerLoginVo {

    /** 登录令牌 */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickName;

    /** 手机号 */
    private String phonenumber;

    /** 头像 */
    private String avatar;

    /** 邮箱 */
    private String email;

    /** 性别 */
    private String sex;

    /** 部门ID */
    private Long deptId;

    /** 角色集合 */
    private Set<String> roles;

    /** 权限集合 */
    private Set<String> permissions;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getPhonenumber() { return phonenumber; }
    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
}
