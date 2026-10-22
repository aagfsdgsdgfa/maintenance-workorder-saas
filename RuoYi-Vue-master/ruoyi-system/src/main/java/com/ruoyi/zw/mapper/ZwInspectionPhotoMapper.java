package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwInspectionPhoto;
import java.util.List;

public interface ZwInspectionPhotoMapper {
    List<ZwInspectionPhoto> selectZwInspectionPhotoList(ZwInspectionPhoto photo);
    ZwInspectionPhoto selectZwInspectionPhotoById(Long photoId);
    int insertZwInspectionPhoto(ZwInspectionPhoto photo);
    int deleteZwInspectionPhotoByIds(Long[] photoIds);
}
