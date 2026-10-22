package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwCustomerUser;
import java.util.List;

public interface IZwCustomerUserService {
    List<ZwCustomerUser> selectZwCustomerUserList(ZwCustomerUser customerUser);
    ZwCustomerUser selectZwCustomerUserById(Long custUserId);
    int insertZwCustomerUser(ZwCustomerUser customerUser);
    int updateZwCustomerUser(ZwCustomerUser customerUser);
    int deleteZwCustomerUserByIds(Long[] custUserIds);
}
