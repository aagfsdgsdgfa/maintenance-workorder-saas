package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwAssetLogs;
import com.ruoyi.zw.enums.AssetStatusEnum;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwAssetLogsMapper;
import com.ruoyi.zw.service.IZwCustomerAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户资产Service实现
 *
 * @author ruoyi
 */
@Service
public class ZwCustomerAssetServiceImpl implements IZwCustomerAssetService {

    @Autowired
    private ZwAssetMapper assetMapper;

    @Autowired
    private ZwAssetLogsMapper assetLogsMapper;

    @Override
    public Map<String, Object> selectAssetList(Long customerId, String assetName, String assetType,
                                                String status, String warrantyStatus, String qrCode,
                                                String assetCode, int pageNum, int pageSize) {
        ZwAsset query = new ZwAsset();
        query.setCustomerId(customerId);
        query.setDelFlag("0");
        if (assetName != null && !assetName.isBlank()) {
            query.setAssetName(assetName);
        }
        if (assetType != null && !assetType.isBlank()) {
            query.setAssetType(assetType);
        }
        if (status != null && !status.isBlank()) {
            query.setStatus(status);
        }
        if (qrCode != null && !qrCode.isBlank()) {
            query.setQrCode(qrCode);
        }
        if (assetCode != null && !assetCode.isBlank()) {
            query.setAssetCode(assetCode);
        }

        List<ZwAsset> allAssets = assetMapper.selectZwAssetList(query);

        // 内存端过滤质保状态（MyBatis XML中不易表达，移到Service层）
        List<ZwAsset> filtered = allAssets;
        if (warrantyStatus != null && !warrantyStatus.isBlank()) {
            java.util.Date now = new java.util.Date();
            filtered = allAssets.stream().filter(a -> {
                if (a.getWarrantyEndDate() == null) {
                    return "out_of_warranty".equals(warrantyStatus) || "expiring".equals(warrantyStatus);
                }
                long diffDays = (a.getWarrantyEndDate().getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                return switch (warrantyStatus) {
                    case "in_warranty" -> diffDays > 0;
                    case "out_of_warranty" -> diffDays <= 0;
                    case "expiring" -> diffDays > 0 && diffDays <= 30;
                    default -> true;
                };
            }).collect(Collectors.toList());
        }

        // 分页
        int total = filtered.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<ZwAsset> pageList = fromIndex < total ? filtered.subList(fromIndex, toIndex) : new ArrayList<>();

        List<Map<String, Object>> rows = pageList.stream().map(this::convertAssetToMap).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("rows", rows);
        return result;
    }

    @Override
    public ZwAsset selectAssetDetail(Long assetId) {
        return assetMapper.selectZwAssetById(assetId);
    }

    @Override
    public List<Map<String, Object>> selectAssetLogs(Long assetId) {
        List<ZwAssetLogs> logs = assetLogsMapper.selectZwAssetLogsByAssetId(assetId);
        return logs.stream().map(log -> {
            Map<String, Object> m = new HashMap<>();
            m.put("logId", log.getLogId());
            m.put("operationType", log.getOperationType());
            m.put("operationTypeName", convertOperationTypeName(log.getOperationType()));
            m.put("operatorName", log.getOperatorName());
            m.put("operationTime", log.getOperateTime());
            m.put("remark", log.getDescription());
            m.put("workOrderId", log.getWorkOrderId());
            m.put("extraData", log.getExtraData());
            return m;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> convertAssetToMap(ZwAsset asset) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetId", asset.getAssetId());
        map.put("assetCode", asset.getAssetCode());
        map.put("assetName", asset.getAssetName());
        map.put("assetType", asset.getAssetType());
        map.put("assetTypeName", convertAssetTypeName(asset.getAssetType()));
        map.put("brand", asset.getBrand());
        map.put("model", asset.getModel());
        map.put("location", asset.getLocation());
        map.put("status", asset.getStatus());
        map.put("statusName", convertStatusName(asset.getStatus()));
        map.put("qrCode", asset.getQrCode());
        map.put("purchaseDate", asset.getPurchaseDate());
        map.put("warrantyEndDate", asset.getWarrantyEndDate());
        map.put("warrantyStatus", computeWarrantyStatus(asset.getWarrantyEndDate()));
        map.put("warrantyStatusName", computeWarrantyStatusName(asset.getWarrantyEndDate()));
        map.put("healthScore", asset.getHealthScore());
        map.put("isThirdParty", asset.getIsThirdParty());
        map.put("isThirdPartyName", "1".equals(asset.getIsThirdParty()) ? "代保" : "自售");
        map.put("createTime", asset.getCreateTime());
        return map;
    }

    private String convertAssetTypeName(String assetType) {
        return switch (assetType) {
            case "device" -> "设备";
            case "software" -> "软件";
            case "consumable" -> "耗材";
            case "other" -> "其他";
            default -> assetType;
        };
    }

    private String convertStatusName(String status) {
        AssetStatusEnum e = AssetStatusEnum.of(status);
        return e != null ? e.getLabel() : status;
    }

    private String computeWarrantyStatus(java.util.Date warrantyEndDate) {
        if (warrantyEndDate == null) return "out_of_warranty";
        long diffDays = (warrantyEndDate.getTime() - new java.util.Date().getTime()) / (1000 * 60 * 60 * 24);
        if (diffDays <= 0) return "out_of_warranty";
        if (diffDays <= 30) return "expiring";
        return "in_warranty";
    }

    private String computeWarrantyStatusName(java.util.Date warrantyEndDate) {
        return switch (computeWarrantyStatus(warrantyEndDate)) {
            case "in_warranty" -> "质保内";
            case "out_of_warranty" -> "已过保";
            case "expiring" -> "即将到期";
            default -> "未知";
        };
    }

    private String convertOperationTypeName(String operationType) {
        return switch (operationType) {
            case "create" -> "创建";
            case "move" -> "移位";
            case "inspect" -> "巡检";
            case "repair" -> "维修";
            case "warranty_renew" -> "质保续期";
            case "contract_renew" -> "合同续期";
            case "scrap" -> "报废";
            default -> operationType;
        };
    }
}
