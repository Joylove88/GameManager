package com.gm.modules.user.controller;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.user.entity.GmUserVipLevelEntity;
import com.gm.modules.user.service.GmUserVipLevelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 用户消费等级
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-23 15:25:58
 */
@RestController
@RequestMapping("user/gmuserviplevel")
public class GmUserVipLevelController {
    @Autowired
    private GmUserVipLevelService gmUserVipLevelService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:gmuserviplevel:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = gmUserVipLevelService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{vipLevelId}")
    @RequiresPermissions("user:gmuserviplevel:info")
    public R info(@PathVariable("vipLevelId") Long vipLevelId) {
        GmUserVipLevelEntity gmUserVipLevel = gmUserVipLevelService.getById(vipLevelId);

        return R.ok().put("gmUserVipLevel", gmUserVipLevel);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:gmuserviplevel:save")
    public R save(@RequestBody GmUserVipLevelEntity gmUserVipLevel) {
        gmUserVipLevelService.save(gmUserVipLevel);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:gmuserviplevel:update")
    public R update(@RequestBody GmUserVipLevelEntity gmUserVipLevel) {
        ValidatorUtils.validateEntity(gmUserVipLevel);
        gmUserVipLevelService.updateById(gmUserVipLevel);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:gmuserviplevel:delete")
    public R delete(@RequestBody Long[] vipLevelIds) {
        gmUserVipLevelService.removeByIds(Arrays.asList(vipLevelIds));

        return R.ok();
    }

}
