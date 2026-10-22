package com.ruoyi.zw.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.Date;

public class ZwSatisfaction {
    @JsonAlias({"evaluationId", "evalId"})
    private Long evalId;
    private Long workOrderId;
    private Long customerId;
    private Integer responseScore;
    private Integer techScore;
    private Integer overallScore;
    private String comment;
    private Date evalTime;
    private String delFlag;
    private String createBy;
    private Date createTime;
    private String orderNo;
    private String customerName;

    public Long getEvalId() { return evalId; }
    public void setEvalId(Long evalId) { this.evalId = evalId; }

    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Integer getResponseScore() { return responseScore; }
    public void setResponseScore(Integer responseScore) { this.responseScore = responseScore; }

    public Integer getTechScore() { return techScore; }
    public void setTechScore(Integer techScore) { this.techScore = techScore; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Date getEvalTime() { return evalTime; }
    public void setEvalTime(Date evalTime) { this.evalTime = evalTime; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
