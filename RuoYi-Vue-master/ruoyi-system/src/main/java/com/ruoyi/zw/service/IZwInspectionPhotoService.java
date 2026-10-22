package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwInspectionPhoto;
import java.util.List;

public interface IZwInspectionPhotoService {
    List<ZwInspectionPhoto> selectZwInspectionPhotoList(ZwInspectionPhoto photo);
    ZwInspectionPhoto selectZwInspectionPhotoById(Long photoId);
    int insertZwInspectionPhoto(ZwInspectionPhoto photo);
    int deleteZwInspectionPhotoByIds(Long[] photoIds);
}
