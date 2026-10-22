package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwInspectionPhoto;
import com.ruoyi.zw.mapper.ZwInspectionPhotoMapper;
import com.ruoyi.zw.service.IZwInspectionPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ZwInspectionPhotoServiceImpl implements IZwInspectionPhotoService {

    @Autowired
    private ZwInspectionPhotoMapper photoMapper;

    @Override
    public List<ZwInspectionPhoto> selectZwInspectionPhotoList(ZwInspectionPhoto photo) {
        return photoMapper.selectZwInspectionPhotoList(photo);
    }

    @Override
    public ZwInspectionPhoto selectZwInspectionPhotoById(Long photoId) {
        return photoMapper.selectZwInspectionPhotoById(photoId);
    }

    @Override
    public int insertZwInspectionPhoto(ZwInspectionPhoto photo) {
        photo.setDelFlag("0");
        if (photo.getShootTime() == null) {
            photo.setShootTime(new Date());
        }
        return photoMapper.insertZwInspectionPhoto(photo);
    }

    @Override
    public int deleteZwInspectionPhotoByIds(Long[] photoIds) {
        return photoMapper.deleteZwInspectionPhotoByIds(photoIds);
    }
}
