package com.ruoyi.zw.task;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.RenewalOpportunity;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwRenewalOpportunityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 续保商机自动生成定时任务
 * 每天凌晨1点执行，检查资产质保到期并创建商机记录
 *
 * @author ruoyi
 */
@Component("zwRenewalOpportunityTask")
public class ZwRenewalOpportunityTask {

    private static final Logger log = LoggerFactory.getLogger(ZwRenewalOpportunityTask.class);

    /** 质保到期提前预警天数 */
    private static final int WARNING_DAYS = 90;

    /**
     * 检查即将到期的资产质保，生成商机
     */
    public void generateRenewalOpportunities() {
        log.info("续保商机生成任务开始执行");
        int created = 0;
        int skipped = 0;
        int errors = 0;
        try {
            ZwAssetMapper assetMapper = SpringUtils.getBean(ZwAssetMapper.class);
            ZwRenewalOpportunityMapper opportunityMapper = SpringUtils.getBean(ZwRenewalOpportunityMapper.class);

            Date now = new Date();
            Date threshold = new Date(now.getTime() + (long) WARNING_DAYS * 24 * 60 * 60 * 1000L);

            List<ZwAsset> expiringAssets = assetMapper.selectAssetsWithExpiringWarranty(now, threshold);
            for (ZwAsset asset : expiringAssets) {
                try {
                    boolean createdOne = createOpportunityIfNotExists(opportunityMapper, asset,
                            "warranty_expire", "资产质保到期提醒");
                    if (createdOne) {
                        created++;
                    } else {
                        skipped++;
                    }
                } catch (Exception e) {
                    errors++;
                    log.warn("处理资产 {} 商机关联时异常: {}", asset.getAssetId(), e.getMessage());
                    // 单条失败不影响其他资产，继续处理
                }
            }
        } catch (Exception e) {
            log.error("续保商机生成任务执行异常: {}", e.getMessage(), e);
        } finally {
            log.info("续保商机生成任务执行完成，创建 {} 条，跳过 {} 条，异常 {} 条",
                    created, skipped, errors);
        }
    }

    private boolean createOpportunityIfNotExists(ZwRenewalOpportunityMapper mapper,
                                                   ZwAsset asset, String opportunityType, String defaultTitle) {
        // 检查是否已有相同类型且未关闭的商机
        RenewalOpportunity query = new RenewalOpportunity();
        query.setAssetId(asset.getAssetId());
        query.setOpportunityType(opportunityType);
        query.setStatus("open");
        List<RenewalOpportunity> existing = mapper.selectZwRenewalOpportunityList(query);
        if (existing != null && !existing.isEmpty()) {
            return false; // 已有未关闭商机，跳过
        }

        RenewalOpportunity op = new RenewalOpportunity();
        op.setAssetId(asset.getAssetId());
        op.setCustomerId(asset.getCustomerId());
        op.setOpportunityType(opportunityType);
        op.setTitle(defaultTitle + " - " + asset.getAssetName());
        op.setDescription("资产【" + asset.getAssetName() + "】（" + asset.getAssetCode() + "）即将到期，请及时跟进续保。");
        op.setStatus("open");
        op.setDelFlag("0");
        Date now = new Date();
        op.setCreateTime(now);
        op.setUpdateTime(now);
        mapper.insertZwRenewalOpportunity(op);
        log.debug("商机已创建: assetId={}, type={}", asset.getAssetId(), opportunityType);
        return true;
    }
}
