package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.zw.domain.vo.ZwEngineerVo;
import com.ruoyi.zw.service.IZwEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工程师管理 Controller（用户下拉相关）
 */
@RestController
@RequestMapping("/zw/engineer")
public class ZwEngineerUserController extends BaseController {

    @Autowired
    private IZwEngineerService engineerService;

    /**
     * 获取所有工程师用户下拉列表（用于新增工程师时选择关联用户）
     */
    @GetMapping("/users")
    @PreAuthorize("@ss.hasPermi('zw:engineer:list')")
    public AjaxResult listEngineerUsers() {
        List<ZwEngineerVo> list = engineerService.selectEngineerList(null);
        return success(list);
    }
}
