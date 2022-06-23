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

import com.gm.modules.user.entity.GmMiningInfoEntity;
import com.gm.modules.user.service.GmMiningInfoService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 用户挖矿属性表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-12 19:36:10
 */
@RestController
@RequestMapping("user/gmmininginfo")
public class GmMiningInfoController {
    @Autowired
    private GmMiningInfoService gmMiningInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:gmmininginfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmMiningInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("user:gmmininginfo:info")
    public R info(@PathVariable("id") Long id){
        GmMiningInfoEntity gmMiningInfo = gmMiningInfoService.getById(id);

        return R.ok().put("gmMiningInfo", gmMiningInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:gmmininginfo:save")
    public R save(@RequestBody GmMiningInfoEntity gmMiningInfo){
        gmMiningInfoService.save(gmMiningInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:gmmininginfo:update")
    public R update(@RequestBody GmMiningInfoEntity gmMiningInfo){
        ValidatorUtils.validateEntity(gmMiningInfo);
        gmMiningInfoService.updateById(gmMiningInfo);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:gmmininginfo:delete")
    public R delete(@RequestBody Long[] ids){
        gmMiningInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
