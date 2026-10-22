package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.service.IZwAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 资产Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class ZwAssetServiceImpl implements IZwAssetService {

    @Autowired
    private ZwAssetMapper assetMapper;

    /**
     * 查询资产列表
     *
     * @param asset 资产信息
     * @return 资产集合
     */
    @Override
    public List<ZwAsset> selectZwAssetList(ZwAsset asset) {
        return assetMapper.selectZwAssetList(asset);
    }

    /**
     * 查询资产详情
     *
     * @param assetId 资产ID
     * @return 资产信息
     */
    @Override
    public ZwAsset selectZwAssetById(Long assetId) {
        return assetMapper.selectZwAssetById(assetId);
    }

    /**
     * 根据二维码查询资产
     *
     * @param qrCode 二维码编号
     * @return 资产信息
     */
    @Override
    public ZwAsset selectZwAssetByQrCode(String qrCode) {
        return assetMapper.selectZwAssetByQrCode(qrCode);
    }

    /**
     * 新增资产
     *
     * @param asset 资产信息
     * @return 影响行数
     */
    @Override
    public int insertZwAsset(ZwAsset asset) {
        asset.setDelFlag("0");
        return assetMapper.insertZwAsset(asset);
    }

    /**
     * 修改资产
     *
     * @param asset 资产信息
     * @return 影响行数
     */
    @Override
    public int updateZwAsset(ZwAsset asset) {
        return assetMapper.updateZwAsset(asset);
    }

    /**
     * 删除资产（逻辑删除）
     *
     * @param assetIds 资产ID数组
     * @return 影响行数
     */
    @Override
    public int deleteZwAssetByIds(Long[] assetIds) {
        return assetMapper.deleteZwAssetByIds(assetIds);
    }

    /**
     * 更新资产状态
     *
     * @param assetId 资产ID
     * @param status 状态
     * @return 影响行数
     */
    @Override
    public int updateAssetStatus(Long assetId, String status) {
        return assetMapper.updateAssetStatus(assetId, status);
    }

    @Override
    public String generateQrCode(Long assetId) {
        ZwAsset asset = assetMapper.selectZwAssetById(assetId);
        if (asset == null) {
            return null;
        }
        try {
            String content = (asset.getQrCode() != null && !asset.getQrCode().isBlank())
                ? asset.getQrCode()
                : "ASSET:" + assetId;

            com.google.zxing.common.BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                content,
                com.google.zxing.BarcodeFormat.QR_CODE,
                300, 300
            );

            String fileName = "qr_" + assetId + "_" + System.currentTimeMillis() + ".png";
            String uploadPath = com.ruoyi.common.config.RuoYiConfig.getUploadPath();
            java.io.File dir = new java.io.File(uploadPath + "/qrcode");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            java.io.File file = new java.io.File(dir, fileName);
            com.google.zxing.client.j2se.MatrixToImageWriter.writeToPath(bitMatrix, "PNG", file.toPath());

            String qrUrl = "/qrcode/" + fileName;
            ZwAsset update = new ZwAsset();
            update.setAssetId(assetId);
            update.setQrCode(qrUrl);
            assetMapper.updateZwAsset(update);

            return qrUrl;
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(getClass()).error("生成二维码失败", e);
            return null;
        }
    }
}
