package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.zw.domain.ZwCustomerUser;
import com.ruoyi.zw.service.IZwCustomerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zw/customerUser")
public class ZwCustomerUserController extends BaseController {

    @Autowired
    private IZwCustomerUserService customerUserService;

    @PreAuthorize("@ss.hasPermi('zw:customerUser:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwCustomerUser customerUser) {
        startPage();
        List<ZwCustomerUser> list = customerUserService.selectZwCustomerUserList(customerUser);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:customerUser:query')")
    @GetMapping("/{custUserId}")
    public AjaxResult getInfo(@PathVariable Long custUserId) {
        return success(customerUserService.selectZwCustomerUserById(custUserId));
    }

    @PreAuthorize("@ss.hasPermi('zw:customerUser:add')")
    @Log(title = "客户联系人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwCustomerUser customerUser) {
        return toAjax(customerUserService.insertZwCustomerUser(customerUser));
    }

    @PreAuthorize("@ss.hasPermi('zw:customerUser:edit')")
    @Log(title = "客户联系人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwCustomerUser customerUser) {
        return toAjax(customerUserService.updateZwCustomerUser(customerUser));
    }

    @PreAuthorize("@ss.hasPermi('zw:customerUser:remove')")
    @Log(title = "客户联系人", businessType = BusinessType.DELETE)
    @DeleteMapping("/{custUserIds}")
    public AjaxResult remove(@PathVariable Long[] custUserIds) {
        return toAjax(customerUserService.deleteZwCustomerUserByIds(custUserIds));
    }
}
