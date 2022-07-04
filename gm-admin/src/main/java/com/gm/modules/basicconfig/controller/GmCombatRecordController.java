package com.gm.modules.basicconfig.controller;

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

import com.gm.modules.basicconfig.entity.GmCombatRecordEntity;
import com.gm.modules.basicconfig.service.GmCombatRecordService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 战斗记录表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-24 15:46:33
 */
@RestController
@RequestMapping("basicconfig/gmcombatrecord")
public class GmCombatRecordController {
    @Autowired
    private GmCombatRecordService gmCombatRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:gmcombatrecord:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmCombatRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("basicconfig:gmcombatrecord:info")
    public R info(@PathVariable("id") Long id){
        GmCombatRecordEntity gmCombatRecord = gmCombatRecordService.getById(id);

        return R.ok().put("gmCombatRecord", gmCombatRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:gmcombatrecord:save")
    public R save(@RequestBody GmCombatRecordEntity gmCombatRecord){
        gmCombatRecordService.save(gmCombatRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:gmcombatrecord:update")
    public R update(@RequestBody GmCombatRecordEntity gmCombatRecord){
        ValidatorUtils.validateEntity(gmCombatRecord);
        gmCombatRecordService.updateById(gmCombatRecord);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:gmcombatrecord:delete")
    public R delete(@RequestBody Long[] ids){
        gmCombatRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
