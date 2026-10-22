package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwCustomerUser;
import java.util.List;

public interface ZwCustomerUserMapper {
    List<ZwCustomerUser> selectZwCustomerUserList(ZwCustomerUser customerUser);
    ZwCustomerUser selectZwCustomerUserById(Long custUserId);
    int insertZwCustomerUser(ZwCustomerUser customerUser);
    int updateZwCustomerUser(ZwCustomerUser customerUser);
    int deleteZwCustomerUserByIds(Long[] custUserIds);
}
