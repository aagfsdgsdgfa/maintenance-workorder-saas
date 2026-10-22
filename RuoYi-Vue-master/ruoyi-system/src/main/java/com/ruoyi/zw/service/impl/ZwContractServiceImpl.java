package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ContractAssetRelation;
import com.ruoyi.zw.domain.MaintenanceContract;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwContractAssetRelationMapper;
import com.ruoyi.zw.mapper.ZwContractMapper;
import com.ruoyi.zw.service.IZwContractService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 维保合同Service实现
 *
 * @author ruoyi
 */
@Service
public class ZwContractServiceImpl implements IZwContractService {

    @Autowired
    private ZwContractMapper contractMapper;

    @Autowired
    private ZwContractAssetRelationMapper contractAssetRelationMapper;

    @Autowired
    private ZwAssetMapper assetMapper;

    @Override
    public List<MaintenanceContract> selectContractList(MaintenanceContract contract) {
        return contractMapper.selectZwContractList(contract);
    }

    @Override
    public MaintenanceContract selectContractById(Long id) {
        return contractMapper.selectZwContractById(id);
    }

    @Override
    public List<MaintenanceContract> selectContractByCustomerId(Long customerId) {
        return contractMapper.selectZwContractByCustomerId(customerId);
    }

    @Override
    public int insertContract(MaintenanceContract contract) {
        if (contract.getCustomerId() == null) {
            throw new com.ruoyi.common.exception.ServiceException("归属客户不能为空");
        }
        contract.setDelFlag("0");
        // 默认生效状态
        if (contract.getStatus() == null) {
            contract.setStatus("0");
        }
        return contractMapper.insertZwContract(contract);
    }

    @Override
    public int updateContract(MaintenanceContract contract) {
        return contractMapper.updateZwContract(contract);
    }

    @Override
    public int deleteContractByIds(Long[] ids) {
        return contractMapper.deleteZwContractByIds(ids);
    }

    @Override
    public Map<String, Object> selectContractDetail(Long id) {
        MaintenanceContract contract = contractMapper.selectZwContractById(id);
        if (contract == null) {
            return null;
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("contractId", contract.getContractId());
        detail.put("contractNo", contract.getContractNo());
        detail.put("contractName", contract.getContractName());
        detail.put("customerId", contract.getCustomerId());
        detail.put("startDate", contract.getStartDate());
        detail.put("endDate", contract.getEndDate());
        detail.put("status", contract.getStatus());
        detail.put("slaResponseHours", contract.getSlaResponseHours());
        detail.put("slaArrivalHours", contract.getSlaArrivalHours());
        detail.put("includedInspections", contract.getIncludedInspections());
        detail.put("includedRepairs", contract.getIncludedRepairs());
        detail.put("usedInspections", contract.getUsedInspections());
        detail.put("usedRepairs", contract.getUsedRepairs());
        detail.put("remainingInspections",
                (contract.getIncludedInspections() != null ? contract.getIncludedInspections() : 0)
                - (contract.getUsedInspections() != null ? contract.getUsedInspections() : 0));
        detail.put("remainingRepairs",
                (contract.getIncludedRepairs() != null ? contract.getIncludedRepairs() : 0)
                - (contract.getUsedRepairs() != null ? contract.getUsedRepairs() : 0));
        detail.put("freePartsQuota", contract.getFreePartsQuota());
        detail.put("usedPartsAmount", contract.getUsedPartsAmount());
        detail.put("remainingPartsAmount",
                contract.getFreePartsQuota() != null && contract.getUsedPartsAmount() != null
                ? contract.getFreePartsQuota().subtract(contract.getUsedPartsAmount())
                : contract.getFreePartsQuota());
        detail.put("attachmentUrl", contract.getAttachmentUrl());
        detail.put("createTime", contract.getCreateTime());

        // 查询关联资产列表（批量查询，避免N+1）
        List<Long> assetIds = contractAssetRelationMapper.selectAssetIdsByContractId(id);
        List<Map<String, Object>> assets = new ArrayList<>();
        if (assetIds != null && !assetIds.isEmpty()) {
            List<ZwAsset> assetList = assetMapper.selectZwAssetByIds(assetIds);
            for (ZwAsset asset : assetList) {
                Map<String, Object> assetMap = new HashMap<>();
                assetMap.put("assetId", asset.getAssetId());
                assetMap.put("assetName", asset.getAssetName());
                assetMap.put("location", asset.getLocation());
                assets.add(assetMap);
            }
        }
        detail.put("assetCount", assetIds != null ? assetIds.size() : 0);
        detail.put("assets", assets);

        return detail;
    }

    @Override
    public void deductContractUsage(Long workOrderId, String orderType, Long assetId) {
        if (assetId == null || orderType == null) return;
        // 1. 查合同关联表，找该资产关联的有效合同
        List<Long> contractIds = contractAssetRelationMapper.selectContractIdsByAssetId(assetId);
        if (contractIds == null || contractIds.isEmpty()) return;

        // 2. 找有效合同（当前日期在 start_date~end_date 之间且 del_flag='0'）
        for (Long contractId : contractIds) {
            MaintenanceContract contract = contractMapper.selectZwContractById(contractId);
            if (contract == null) continue;
            if (!"0".equals(contract.getDelFlag())) continue;
            // 检查日期范围
            Date now = new Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            Date today = cal.getTime();
            if (contract.getStartDate() != null && contract.getStartDate().after(today)) continue;
            if (contract.getEndDate() != null && contract.getEndDate().before(today)) continue;

            // 3. 扣减次数
            MaintenanceContract update = new MaintenanceContract();
            update.setContractId(contractId);
            if ("1".equals(orderType)) {
                // 巡检扣 used_inspections
                int used = contract.getUsedInspections() != null ? contract.getUsedInspections() : 0;
                int included = contract.getIncludedInspections() != null ? contract.getIncludedInspections() : 0;
                if (used < included) {
                    update.setUsedInspections(used + 1);
                }
            } else if ("2".equals(orderType)) {
                // 维修扣 used_repairs
                int used = contract.getUsedRepairs() != null ? contract.getUsedRepairs() : 0;
                int included = contract.getIncludedRepairs() != null ? contract.getIncludedRepairs() : 0;
                if (used < included) {
                    update.setUsedRepairs(used + 1);
                }
            }
            contractMapper.updateZwContract(update);
            break; // 只扣一个合同
        }
    }

    @Override
    public Map<String, Object> ocrContractImage(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            Tesseract tesseract = new Tesseract();
            // 默认使用系统安装的 Tesseract，如未安装则提示
            String tessDataPath = System.getenv("TESSDATA_PREFIX");
            if (tessDataPath != null && !tessDataPath.isEmpty()) {
                tesseract.setDatapath(tessDataPath);
            }
            tesseract.setLanguage("chi_sim+eng");

            // Tesseract.doOCR 需 File 或 BufferedImage，先写到临时文件（保留原始扩展名以支持PNG/BMP等格式）
            String originalFilename = file.getOriginalFilename();
            String suffix = ".jpg";
            if (originalFilename != null) {
                int dot = originalFilename.lastIndexOf('.');
                if (dot > 0) suffix = originalFilename.substring(dot);
            }
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("ocr_", suffix);
            file.transferTo(tempFile.toFile());
            String text = tesseract.doOCR(tempFile.toFile());
            java.nio.file.Files.deleteIfExists(tempFile);

            // 正则提取合同字段
            // 合同名称：匹配"合同名称："或"合同名称："
            result.put("contractName", extractByRegex(text, "(?:合同[\\s]*名[称:：]+)([^\\n\\r]{2,30})"));
            // 合同编号：匹配"合同编号："后面跟的数字字母
            result.put("contractNo", extractByRegex(text, "(?:合同[\\s]*编号[：:]+)([A-Za-z0-9\\-]{4,30})"));
            // 客户名称：匹配"甲方："或"客户："后面的内容
            result.put("customerName", extractByRegex(text, "(?:甲方|客户|委托方)[：:]+\\s*([^\n\\r]{2,30})"));
            // 开始日期
            String startDate = extractByRegex(text, "(?:开始日期|生效日期|签订日期)[：:]+\\s*(\\d{4}[年\\-/]\\d{1,2}[月\\-/]\\d{1,2}[日]?)");
            if (!startDate.isEmpty()) result.put("startDate", normalizeDate(startDate));
            // 结束日期
            String endDate = extractByRegex(text, "(?:结束日期|终止日期|到期日期)[：:]+\\s*(\\d{4}[年\\-/]\\d{1,2}[月\\-/]\\d{1,2}[日]?)");
            if (!endDate.isEmpty()) result.put("endDate", normalizeDate(endDate));
            // SLA响应时效
            String slaResp = extractByRegex(text, "(?:SLA响应|响应时效|响应时间)[：:]+\\s*(\\d+)");
            if (!slaResp.isEmpty()) result.put("slaResponseHours", Integer.parseInt(slaResp));
            // SLA到场时效
            String slaArr = extractByRegex(text, "(?:到场时效|到达时间|响应到场)[：:]+\\s*(\\d+)");
            if (!slaArr.isEmpty()) result.put("slaArrivalHours", Integer.parseInt(slaArr));
            // 含巡检次数
            String insp = extractByRegex(text, "(?:巡检次数|含巡检)[：:]+\\s*(\\d+)");
            if (!insp.isEmpty()) result.put("includedInspections", Integer.parseInt(insp));
            // 含维修次数
            String rep = extractByRegex(text, "(?:维修次数|含维修)[：:]+\\s*(\\d+)");
            if (!rep.isEmpty()) result.put("includedRepairs", Integer.parseInt(rep));
            // 免费配件额度
            String quota = extractByRegex(text, "(?:配件额度|免费配件)[：:]+\\s*([\\d\\.]+)");
            if (!quota.isEmpty()) result.put("freePartsQuota", new BigDecimal(quota));
            // 原始OCR文本（便于人工核对）
            result.put("_rawText", text);
            // 图片base64（便于前端预览）
            byte[] imgBytes = file.getBytes();
            String ct = file.getContentType();
            result.put("_imageBase64", "data:" + (ct != null ? ct : "image/jpeg") + ";base64," + Base64.getEncoder().encodeToString(imgBytes));

        } catch (TesseractException e) {
            result.put("_error", "OCR识别失败：" + e.getMessage());
        } catch (IOException e) {
            result.put("_error", "文件读取失败：" + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> parseContractExcel(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            // 取第一行作为表头，建立列名→列索引映射
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colIndex = new HashMap<>();
            if (headerRow != null) {
                for (Cell cell : headerRow) {
                    String val = formatter.formatCellValue(cell).trim();
                    if (val != null && !val.isEmpty()) {
                        colIndex.put(val, cell.getColumnIndex());
                    }
                }
            }

            // 遍历剩余行，提取合同数据（取第一行非空数据）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                java.util.function.Function<String, String> getVal = (col) -> {
                    Integer idx = colIndex.get(col);
                    if (idx == null) return "";
                    Cell cell = row.getCell(idx);
                    if (cell == null) return "";
                    String v = formatter.formatCellValue(cell).trim();
                    return v != null ? v : "";
                };

                // 合同名称
                if (result.get("contractName") == null || "".equals(result.get("contractName"))) {
                    String v = getVal.apply("合同名称");
                    if (v.isEmpty()) v = getVal.apply("名称");
                    if (!v.isEmpty()) result.put("contractName", v);
                }
                // 合同编号
                if (result.get("contractNo") == null || "".equals(result.get("contractNo"))) {
                    String v = getVal.apply("合同编号");
                    if (v.isEmpty()) v = getVal.apply("编号");
                    if (!v.isEmpty()) result.put("contractNo", v);
                }
                // 客户名称（存到customerName，前端再做名称→ID映射）
                if (result.get("customerName") == null || "".equals(result.get("customerName"))) {
                    String v = getVal.apply("客户");
                    if (v.isEmpty()) v = getVal.apply("甲方");
                    if (!v.isEmpty()) result.put("customerName", v);
                }
                // 开始日期
                if (result.get("startDate") == null) {
                    String v = getVal.apply("开始日期");
                    if (v.isEmpty()) v = getVal.apply("生效日期");
                    if (!v.isEmpty()) result.put("startDate", normalizeDate(v));
                }
                // 结束日期
                if (result.get("endDate") == null) {
                    String v = getVal.apply("结束日期");
                    if (v.isEmpty()) v = getVal.apply("到期日期");
                    if (!v.isEmpty()) result.put("endDate", normalizeDate(v));
                }
                // SLA响应
                if (result.get("slaResponseHours") == null) {
                    String v = getVal.apply("SLA响应(h)");
                    if (v.isEmpty()) v = getVal.apply("响应时效");
                    if (!v.isEmpty()) {
                        try { result.put("slaResponseHours", Integer.parseInt(v.replaceAll("\\D", ""))); } catch (NumberFormatException ignored) {}
                    }
                }
                // SLA到场
                if (result.get("slaArrivalHours") == null) {
                    String v = getVal.apply("SLA到场(h)");
                    if (v.isEmpty()) v = getVal.apply("到场时效");
                    if (!v.isEmpty()) {
                        try { result.put("slaArrivalHours", Integer.parseInt(v.replaceAll("\\D", ""))); } catch (NumberFormatException ignored) {}
                    }
                }
                // 含巡检
                if (result.get("includedInspections") == null) {
                    String v = getVal.apply("含巡检次数");
                    if (v.isEmpty()) v = getVal.apply("巡检次数");
                    if (!v.isEmpty()) {
                        try { result.put("includedInspections", Integer.parseInt(v.replaceAll("\\D", ""))); } catch (NumberFormatException ignored) {}
                    }
                }
                // 含维修
                if (result.get("includedRepairs") == null) {
                    String v = getVal.apply("含维修次数");
                    if (v.isEmpty()) v = getVal.apply("维修次数");
                    if (!v.isEmpty()) {
                        try { result.put("includedRepairs", Integer.parseInt(v.replaceAll("\\D", ""))); } catch (NumberFormatException ignored) {}
                    }
                }
                // 免费配件额度
                if (result.get("freePartsQuota") == null) {
                    String v = getVal.apply("免费配件额度");
                    if (v.isEmpty()) v = getVal.apply("配件额度");
                    if (!v.isEmpty()) {
                        try { result.put("freePartsQuota", new BigDecimal(v.replaceAll("[^\\d\\.]", ""))); } catch (NumberFormatException ignored) {}
                    }
                }
                // 状态
                if (result.get("status") == null) {
                    String v = getVal.apply("状态");
                    if ("生效".equals(v) || "有效".equals(v)) result.put("status", "0");
                    else if ("到期".equals(v) || "已到期".equals(v)) result.put("status", "1");
                    else if ("终止".equals(v) || "已终止".equals(v)) result.put("status", "2");
                }

                Map<String, Object> rowData = new LinkedHashMap<>();
                rowData.put("contractName", getVal.apply("合同名称"));
                if (rowData.get("contractName") == null || "".equals(rowData.get("contractName"))) rowData.put("contractName", getVal.apply("名称"));
                rowData.put("contractNo", getVal.apply("合同编号"));
                if (rowData.get("contractNo") == null || "".equals(rowData.get("contractNo"))) rowData.put("contractNo", getVal.apply("编号"));
                rowData.put("customerName", getVal.apply("客户"));
                if (rowData.get("customerName") == null || "".equals(rowData.get("customerName"))) rowData.put("customerName", getVal.apply("甲方"));
                String sd = getVal.apply("开始日期");
                if (sd.isEmpty()) sd = getVal.apply("生效日期");
                if (!sd.isEmpty()) rowData.put("startDate", normalizeDate(sd));
                String ed = getVal.apply("结束日期");
                if (ed.isEmpty()) ed = getVal.apply("到期日期");
                if (!ed.isEmpty()) rowData.put("endDate", normalizeDate(ed));
                String slaResp = getVal.apply("SLA响应(h)");
                if (slaResp.isEmpty()) slaResp = getVal.apply("响应时效");
                if (!slaResp.isEmpty()) { try { rowData.put("slaResponseHours", Integer.parseInt(slaResp.replaceAll("\\D", ""))); } catch (NumberFormatException ignored) {} }
                String slaArr = getVal.apply("SLA到场(h)");
                if (slaArr.isEmpty()) slaArr = getVal.apply("到场时效");
                if (!slaArr.isEmpty()) { try { rowData.put("slaArrivalHours", Integer.parseInt(slaArr.replaceAll("\\D", ""))); } catch (NumberFormatException ignored) {} }
                String insp = getVal.apply("含巡检次数");
                if (insp.isEmpty()) insp = getVal.apply("巡检次数");
                if (!insp.isEmpty()) { try { rowData.put("includedInspections", Integer.parseInt(insp.replaceAll("\\D", ""))); } catch (NumberFormatException ignored) {} }
                String rep = getVal.apply("含维修次数");
                if (rep.isEmpty()) rep = getVal.apply("维修次数");
                if (!rep.isEmpty()) { try { rowData.put("includedRepairs", Integer.parseInt(rep.replaceAll("\\D", ""))); } catch (NumberFormatException ignored) {} }
                String quota = getVal.apply("免费配件额度");
                if (quota.isEmpty()) quota = getVal.apply("配件额度");
                if (!quota.isEmpty()) { try { rowData.put("freePartsQuota", new BigDecimal(quota.replaceAll("[^\\d\\.]", ""))); } catch (NumberFormatException ignored) {} }
                String statusV = getVal.apply("状态");
                if ("生效".equals(statusV) || "有效".equals(statusV)) rowData.put("status", "0");
                else if ("到期".equals(statusV) || "已到期".equals(statusV)) rowData.put("status", "1");
                else if ("终止".equals(statusV) || "已终止".equals(statusV)) rowData.put("status", "2");
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> allRows = (List<Map<String, Object>>) result.computeIfAbsent("_rows", k -> new ArrayList<>());
                allRows.add(rowData);
            }

            // 如果完全没有数据，给个提示
            if (result.isEmpty() && result.get("_rows") == null) {
                result.put("_error", "未识别到任何合同数据，请检查Excel格式");
            } else if (result.get("_rows") != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> allRows = (List<Map<String, Object>>) result.get("_rows");
                // 取第一条作为主表单数据
                if (!allRows.isEmpty()) {
                    Map<String, Object> first = allRows.get(0);
                    for (Map.Entry<String, Object> e : first.entrySet()) {
                        if (result.get(e.getKey()) == null || "".equals(result.get(e.getKey()))) {
                            result.put(e.getKey(), e.getValue());
                        }
                    }
                    result.put("_preview", allRows);
                }
            }

        } catch (IOException e) {
            result.put("_error", "Excel解析失败：" + e.getMessage());
        }
        return result;
    }

    // 正则提取辅助方法
    private String extractByRegex(String text, String regex) {
        try {
            Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(text);
            if (m.find()) {
                return m.group(1).trim();
            }
        } catch (Exception ignored) {}
        return "";
    }

    // 日期标准化：各种格式统一转 yyyy-MM-dd
    private String normalizeDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        dateStr = dateStr.trim();
        // 已经是 yyyy-MM-dd
        if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) return dateStr;
        // 替换年月日分隔符
        dateStr = dateStr.replaceAll("[年\\-/]", "-").replaceAll("日?", "");
        // 去掉多余的空格
        dateStr = dateStr.replaceAll("\\s+", " ").trim();
        // 补全格式
        String[] parts = dateStr.split("-");
        if (parts.length >= 3) {
            return String.format("%04d-%02d-%02d",
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]));
        }
        return "";
    }
}
