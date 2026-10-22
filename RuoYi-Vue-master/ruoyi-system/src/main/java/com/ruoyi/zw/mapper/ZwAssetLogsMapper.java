package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwAssetLogs;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 资产时间轴日志Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface ZwAssetLogsMapper {

    /**
     * 查询资产日志列表
     *
     * @param logs 资产日志信息
     * @return 资产日志集合
     */
    List<ZwAssetLogs> selectZwAssetLogsList(ZwAssetLogs logs);

    /**
     * 查询资产日志详情
     *
     * @param logId 日志ID
     * @return 资产日志信息
     */
    ZwAssetLogs selectZwAssetLogsById(Long logId);

    /**
     * 根据资产ID查询日志列表（按时间倒序）
     *
     * @param assetId 资产ID
     * @return 资产日志集合
     */
    List<ZwAssetLogs> selectZwAssetLogsByAssetId(Long assetId);

    /**
     * 新增资产日志
     *
     * @param logs 资产日志信息
     * @return 影响行数
     */
    int insertZwAssetLogs(ZwAssetLogs logs);

    /**
     * 修改资产日志
     *
     * @param logs 资产日志信息
     * @return 影响行数
     */
    int updateZwAssetLogs(ZwAssetLogs logs);

    /**
     * 删除资产日志
     *
     * @param logIds 日志ID数组
     * @return 影响行数
     */
    int deleteZwAssetLogsByIds(Long[] logIds);
}
