package com.ruoyi.zw.domain.vo;

public class WorkOrderCompleteRequest {
    private String repairResult;
    private String parts;
    private String photos;
    private String signature;
    private String nextVisitTime;

    public String getRepairResult() { return repairResult; }
    public void setRepairResult(String repairResult) { this.repairResult = repairResult; }

    public String getParts() { return parts; }
    public void setParts(String parts) { this.parts = parts; }

    public String getPhotos() { return photos; }
    public void setPhotos(String photos) { this.photos = photos; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getNextVisitTime() { return nextVisitTime; }
    public void setNextVisitTime(String nextVisitTime) { this.nextVisitTime = nextVisitTime; }
}
