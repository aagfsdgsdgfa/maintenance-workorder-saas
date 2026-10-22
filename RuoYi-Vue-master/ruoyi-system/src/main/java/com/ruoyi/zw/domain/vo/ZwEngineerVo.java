package com.ruoyi.zw.domain.vo;

import java.util.Date;
import java.util.Set;

/**
 * 工程师详情 VO（包含关联信息）
 */
public class ZwEngineerVo {

    private Long engineerId;
    private Long userId;
    private String employeeNo;
    private String realName;
    private String sex;
    private String phonenumber;
    private String email;
    private Long deptId;
    private String deptName;
    private String status;
    private String skillLevel;
    private String specialties;
    private Date joinDate;
    private Integer totalOrders;
    private Integer completedOrders;
    private Double ratingAvg;
    private Integer pendingToday;
    private String avatar;
    private Set<String> roles;
    private Set<String> permissions;
    private Date createTime;
    private Date lastLoginTime;

    public Long getEngineerId() { return engineerId; }
    public void setEngineerId(Long engineerId) { this.engineerId = engineerId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getPhonenumber() { return phonenumber; }
    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }

    public String getSpecialties() { return specialties; }
    public void setSpecialties(String specialties) { this.specialties = specialties; }

    public Date getJoinDate() { return joinDate; }
    public void setJoinDate(Date joinDate) { this.joinDate = joinDate; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public Integer getCompletedOrders() { return completedOrders; }
    public void setCompletedOrders(Integer completedOrders) { this.completedOrders = completedOrders; }

    public Double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(Double ratingAvg) { this.ratingAvg = ratingAvg; }

    public Integer getPendingToday() { return pendingToday; }
    public void setPendingToday(Integer pendingToday) { this.pendingToday = pendingToday; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }
}
