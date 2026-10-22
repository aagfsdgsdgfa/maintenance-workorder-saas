package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.MaintenanceContract;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

/**
 * 维保合同Service接口
 *
 * @author ruoyi
 */
public interface IZwContractService {

    /**
     * 查询维保合同列表
     *
     * @param contract 维保合同信息
     * @return 维保合同集合
     */
    List<MaintenanceContract> selectContractList(MaintenanceContract contract);

    /**
     * 查询维保合同详情
     *
     * @param id 合同ID
     * @return 维保合同信息
     */
    MaintenanceContract selectContractById(Long id);

    /**
     * 根据客户ID查询合同列表
     *
     * @param customerId 客户ID
     * @return 维保合同集合
     */
    List<MaintenanceContract> selectContractByCustomerId(Long customerId);

    /**
     * 新增维保合同
     *
     * @param contract 维保合同信息
     * @return 影响行数
     */
    int insertContract(MaintenanceContract contract);

    /**
     * 修改维保合同
     *
     * @param contract 维保合同信息
     * @return 影响行数
     */
    int updateContract(MaintenanceContract contract);

    /**
     * 删除维保合同（逻辑删除）
     *
     * @param ids 合同ID数组
     * @return 影响行数
     */
    int deleteContractByIds(Long[] ids);

    /**
     * 查询合同详情（含已使用次数、关联资产）
     *
     * @param id 合同ID
     * @return 合同详情Map（包含合同信息、已用次数、关联资产列表）
     */
    Map<String, Object> selectContractDetail(Long id);

    /**
     * 工单完成时核销合同服务次数
     *
     * @param workOrderId 工单ID
     * @param orderType 工单类型：1巡检 2维修
     * @param assetId 资产ID
     */
    void deductContractUsage(Long workOrderId, String orderType, Long assetId);

    /**
     * OCR图片扫描识别合同内容
     *
     * @param file 图片文件
     * @return 识别出的合同字段Map
     */
    Map<String, Object> ocrContractImage(MultipartFile file);

    /**
     * Excel导入解析合同内容
     *
     * @param file Excel文件
     * @return 解析出的合同字段Map
     */
    Map<String, Object> parseContractExcel(MultipartFile file);
}
