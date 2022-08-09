package com.gm.modules.user.controller;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.user.entity.GmUserWithdrawEntity;
import com.gm.modules.user.service.GmUserWithdrawService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 提现表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-08-09 19:13:43
 */
@RestController
@RequestMapping("user/gmuserwithdraw")
public class GmUserWithdrawController {
    @Autowired
    private GmUserWithdrawService gmUserWithdrawService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:gmuserwithdraw:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = gmUserWithdrawService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{withdrawId}")
    @RequiresPermissions("user:gmuserwithdraw:info")
    public R info(@PathVariable("withdrawId") Long withdrawId) {
        GmUserWithdrawEntity gmUserWithdraw = gmUserWithdrawService.getById(withdrawId);

        return R.ok().put("gmUserWithdraw", gmUserWithdraw);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:gmuserwithdraw:save")
    public R save(@RequestBody GmUserWithdrawEntity gmUserWithdraw) {
        gmUserWithdrawService.save(gmUserWithdraw);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:gmuserwithdraw:update")
    public R update(@RequestBody GmUserWithdrawEntity gmUserWithdraw) {
        ValidatorUtils.validateEntity(gmUserWithdraw);
        gmUserWithdrawService.updateById(gmUserWithdraw);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:gmuserwithdraw:delete")
    public R delete(@RequestBody Long[] withdrawIds) {
        gmUserWithdrawService.removeByIds(Arrays.asList(withdrawIds));

        return R.ok();
    }

}
