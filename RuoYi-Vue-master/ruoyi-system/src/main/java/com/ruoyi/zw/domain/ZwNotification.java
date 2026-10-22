package com.ruoyi.zw.domain;

import java.util.Date;

public class ZwNotification {
    private Long notifId;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private Long relatedId;
    private String isRead;
    private Date readTime;
    private String delFlag;
    private String createBy;
    private Date createTime;
    private Long customerId;

    public Long getNotifId() { return notifId; }
    public void setNotifId(Long notifId) { this.notifId = notifId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }

    public String getIsRead() { return isRead; }
    public void setIsRead(String isRead) { this.isRead = isRead; }

    public Date getReadTime() { return readTime; }
    public void setReadTime(Date readTime) { this.readTime = readTime; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
