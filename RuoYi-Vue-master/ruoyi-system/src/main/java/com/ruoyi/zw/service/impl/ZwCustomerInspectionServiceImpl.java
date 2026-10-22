package com.ruoyi.zw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwInspectionResult;
import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.enums.InspectionStatusEnum;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwInspectionResultMapper;
import com.ruoyi.zw.mapper.ZwInspectionTaskMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.zw.service.IZwCustomerInspectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户巡检Service实现
 *
 * @author ruoyi
 */
@Service
public class ZwCustomerInspectionServiceImpl implements IZwCustomerInspectionService {

    private static final Logger log = LoggerFactory.getLogger(ZwCustomerInspectionServiceImpl.class);

    @Autowired
    private ZwInspectionTaskMapper inspectionTaskMapper;

    @Autowired
    private ZwInspectionResultMapper inspectionResultMapper;

    @Autowired
    private ZwAssetMapper assetMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Map<String, Object> selectInspectionList(Long customerId, Long assetId,
                                                    String startDate, String endDate,
                                                    int pageNum, int pageSize) {
        // 构建查询条件（customerId/assetId/日期都下推到 SQL 端，避免内存分页与 N+1）
        ZwInspectionTask query = new ZwInspectionTask();
        query.setDelFlag("0");
        query.setCustomerId(customerId);
        if (assetId != null) {
            query.setAssetId(assetId);
        }
        if (startDate != null && !startDate.isBlank()) {
            query.setPlanStartDate(startDate);
        }
        if (endDate != null && !endDate.isBlank()) {
            query.setPlanEndDate(endDate);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<ZwInspectionTask> pageList = inspectionTaskMapper.selectZwInspectionTaskList(query);
        PageInfo<ZwInspectionTask> pageInfo = new PageInfo<>(pageList);

        Map<Long, SysUser> userCache = new HashMap<>();
        List<Map<String, Object>> rows = pageList.stream().map(task -> {
            SysUser engineer = task.getAssignedTo() != null
                    ? userCache.computeIfAbsent(task.getAssignedTo(), sysUserMapper::selectUserById)
                    : null;
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", task.getTaskId());
            map.put("workOrderId", task.getWorkOrderId());
            map.put("orderNo", task.getWorkOrderNo());
            map.put("assetId", task.getAssetId());
            map.put("assetName", task.getAssetName());
            map.put("engineerName", engineer != null ? engineer.getNickName() : null);
            map.put("planTime", task.getPlanTime());
            map.put("actualStartTime", task.getActualStartTime());
            map.put("actualEndTime", task.getActualEndTime());
            map.put("status", task.getStatus());
            map.put("statusName", convertTaskStatusName(task.getStatus()));
            map.put("checkInAddress", task.getCheckInAddress());
            map.put("reportUrl", task.getReportUrl());
            map.put("signatureUrl", null);
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("rows", rows);
        return result;
    }

    @Override
    public Map<String, Object> selectInspectionReport(Long taskId) {
        ZwInspectionTask task = inspectionTaskMapper.selectZwInspectionTaskById(taskId);
        if (task == null) {
            return null;
        }

        Map<String, Object> report = new HashMap<>();
        report.put("taskId", task.getTaskId());
        report.put("workOrderId", task.getWorkOrderId());
        report.put("orderNo", task.getWorkOrderNo());

        // 资产信息
        ZwAsset asset = task.getAssetId() != null ? assetMapper.selectZwAssetById(task.getAssetId()) : null;
        if (asset != null) {
            Map<String, Object> assetMap = new HashMap<>();
            assetMap.put("assetId", asset.getAssetId());
            assetMap.put("assetName", asset.getAssetName());
            assetMap.put("assetCode", asset.getAssetCode());
            assetMap.put("brand", asset.getBrand());
            assetMap.put("model", asset.getModel());
            assetMap.put("location", asset.getLocation());
            report.put("asset", assetMap);
        } else {
            report.put("asset", null);
        }

        SysUser engineer = task.getAssignedTo() != null ? sysUserMapper.selectUserById(task.getAssignedTo()) : null;
        report.put("engineerName", engineer != null ? engineer.getNickName() : null);
        report.put("engineerPhone", engineer != null ? engineer.getPhonenumber() : null);
        report.put("planTime", task.getPlanTime());
        report.put("actualStartTime", task.getActualStartTime());
        report.put("actualEndTime", task.getActualEndTime());
        report.put("checkInAddress", task.getCheckInAddress());

        // 检查项结果
        ZwInspectionResult result = inspectionResultMapper.selectResultByTaskId(taskId);
        List<Map<String, Object>> results = new ArrayList<>();
        int normalCount = 0;
        int abnormalCount = 0;
        if (result != null) {
            report.put("voiceText", result.getVoiceText());
            report.put("remark", result.getRemark());
            report.put("reportUrl", task.getReportUrl());
            report.put("signatureUrl", task.getCheckInPhoto()); // 打卡照片复用为签名
            report.put("photos", splitPhotoUrls(result.getPhotoUrls()));

            // 解析 checkItems JSON字段（格式：[{"item":"机房温度","value":"25℃","normal":true},...]）
            if (result.getCheckItems() != null && !result.getCheckItems().isBlank()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    List<com.fasterxml.jackson.databind.node.ObjectNode> items =
                            mapper.readValue(result.getCheckItems(),
                                    mapper.getTypeFactory().constructCollectionType(List.class, com.fasterxml.jackson.databind.node.ObjectNode.class));
                    for (com.fasterxml.jackson.databind.node.ObjectNode item : items) {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("resultId", result.getResultId());
                        itemMap.put("checkItem", item.has("item") ? item.get("item").asText() : "");
                        itemMap.put("checkValue", item.has("value") ? item.get("value").asText() : "");
                        boolean isNormal = item.has("normal") && item.get("normal").asBoolean();
                        itemMap.put("isNormal", isNormal);
                        if (isNormal) normalCount++; else abnormalCount++;
                        itemMap.put("photoUrls", new ArrayList<>());
                        results.add(itemMap);
                    }
                } catch (Exception e) {
                    log.warn("解析巡检检查项JSON失败, taskId={}: {}", taskId, e.getMessage());
                }
            }
        } else {
            report.put("voiceText", null);
            report.put("remark", null);
            report.put("reportUrl", null);
            report.put("signatureUrl", null);
            report.put("photos", new ArrayList<>());
        }

        report.put("results", results);
        report.put("normalCount", normalCount);
        report.put("abnormalCount", abnormalCount);

        return report;
    }

    private List<String> splitPhotoUrls(String photoUrls) {
        List<String> list = new ArrayList<>();
        if (photoUrls != null && !photoUrls.isBlank()) {
            for (String url : photoUrls.split(",")) {
                if (!url.isBlank()) list.add(url.trim());
            }
        }
        return list;
    }

    private String convertTaskStatusName(String status) {
        InspectionStatusEnum e = InspectionStatusEnum.of(status);
        return e != null ? e.getLabel() : status;
    }
}
