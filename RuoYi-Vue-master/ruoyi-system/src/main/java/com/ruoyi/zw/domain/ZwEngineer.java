package com.ruoyi.zw.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工程师管理对象 zw_engineer
 */
public class ZwEngineer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 工程师ID（复用 sys_user.user_id） */
    private Long engineerId;

    /** 关联用户ID */
    private Long userId;

    /** 工号 */
    @Excel(name = "工号")
    private String employeeNo;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 性别 */
    @Excel(name = "性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phonenumber;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 所属部门ID */
    private Long deptId;

    /** 所属部门名称 */
    private String deptName;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 资质等级 */
    @Excel(name = "资质等级")
    private String skillLevel;

    /** 擅长领域 */
    @Excel(name = "擅长领域")
    private String specialties;

    /** 入职日期 */
    @Excel(name = "入职日期", width = 120, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date joinDate;

    /** 总接单数 */
    private Integer totalOrders;

    /** 总完工数 */
    private Integer completedOrders;

    /** 评分均分 */
    private Double ratingAvg;

    /** 今日待处理数 */
    private Integer pendingToday;

    /** 头像URL */
    private String avatar;

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
}
