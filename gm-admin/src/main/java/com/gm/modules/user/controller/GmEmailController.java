package com.gm.modules.user.controller;

import java.util.Arrays;
import java.util.Map;

import com.gm.common.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.user.entity.GmEmailEntity;
import com.gm.modules.user.service.GmEmailService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 玩家邮箱
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-10-20 18:37:41
 */
@RestController
@RequestMapping("user/gmemail")
public class GmEmailController {
    @Autowired
    private GmEmailService gmEmailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:gmemail:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmEmailService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("user:gmemail:info")
    public R info(@PathVariable("id") Long id){
        GmEmailEntity gmEmail = gmEmailService.getById(id);

        return R.ok().put("gmEmail", gmEmail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:gmemail:save")
    public R save(@RequestBody GmEmailEntity gmEmail){
        gmEmailService.save(gmEmail);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:gmemail:update")
    public R update(@RequestBody GmEmailEntity gmEmail){
        ValidatorUtils.validateEntity(gmEmail);
        gmEmailService.updateById(gmEmail);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:gmemail:delete")
    public R delete(@RequestBody Long[] ids){
        gmEmailService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
