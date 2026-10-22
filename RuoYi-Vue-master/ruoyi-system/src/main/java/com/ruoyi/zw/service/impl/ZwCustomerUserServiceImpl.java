package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwCustomerUser;
import com.ruoyi.zw.mapper.ZwCustomerUserMapper;
import com.ruoyi.zw.service.IZwCustomerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZwCustomerUserServiceImpl implements IZwCustomerUserService {

    @Autowired
    private ZwCustomerUserMapper customerUserMapper;

    @Override
    public List<ZwCustomerUser> selectZwCustomerUserList(ZwCustomerUser customerUser) {
        return customerUserMapper.selectZwCustomerUserList(customerUser);
    }

    @Override
    public ZwCustomerUser selectZwCustomerUserById(Long custUserId) {
        return customerUserMapper.selectZwCustomerUserById(custUserId);
    }

    @Override
    public int insertZwCustomerUser(ZwCustomerUser customerUser) {
        customerUser.setDelFlag("0");
        return customerUserMapper.insertZwCustomerUser(customerUser);
    }

    @Override
    public int updateZwCustomerUser(ZwCustomerUser customerUser) {
        return customerUserMapper.updateZwCustomerUser(customerUser);
    }

    @Override
    public int deleteZwCustomerUserByIds(Long[] custUserIds) {
        return customerUserMapper.deleteZwCustomerUserByIds(custUserIds);
    }
}
