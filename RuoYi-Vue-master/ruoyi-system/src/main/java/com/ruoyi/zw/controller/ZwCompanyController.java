package com.ruoyi.zw.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 客户公司管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/company")
public class ZwCompanyController extends BaseController
{
    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取公司列表（分页，仅返回有公司编码的记录）
     */
    @PreAuthorize("@ss.hasPermi('system:company:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDept dept)
    {
        startPage();
        // 只查询有公司编码的记录（即客户公司，非内部部门）
        dept.setCompanyCode("__not_null__");
        List<SysDept> list = deptService.selectCompanyList(dept);
        return getDataTable(list);
    }

    /**
     * 获取公司详情
     */
    @PreAuthorize("@ss.hasPermi('system:company:query')")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId)
    {
        return success(deptService.selectDeptById(deptId));
    }

    /**
     * 新增公司
     */
    @PreAuthorize("@ss.hasPermi('system:company:add')")
    @Log(title = "公司管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkCompanyNameUnique(dept))
        {
            return error("新增公司'" + dept.getDeptName() + "'失败，公司名称已存在");
        }
        dept.setCreateBy(getUsername());
        return toAjax(deptService.insertCompany(dept));
    }

    /**
     * 修改公司
     */
    @PreAuthorize("@ss.hasPermi('system:company:edit')")
    @Log(title = "公司管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkCompanyNameUnique(dept))
        {
            return error("修改公司'" + dept.getDeptName() + "'失败，公司名称已存在");
        }
        dept.setUpdateBy(getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除公司
     */
    @PreAuthorize("@ss.hasPermi('system:company:remove')")
    @Log(title = "公司管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId)
    {
        if (deptService.checkDeptExistUser(deptId))
        {
            return warn("该公司存在关联用户，不允许删除");
        }
        return toAjax(deptService.deleteDeptById(deptId));
    }
}
