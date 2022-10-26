package com.gm.modules.basicconfig.controller;

import java.util.Arrays;
import java.util.Map;

import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.HeroLevelEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.basicconfig.service.HeroLevelService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 英雄等级表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-03 19:44:44
 */
@RestController
@RequestMapping("basicconfig/heroleve")
public class HeroLevelController {
    @Autowired
    private HeroLevelService heroLevelService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:heroleve:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = heroLevelService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{heroLeveId}")
    @RequiresPermissions("basicconfig:heroleve:info")
    public R info(@PathVariable("heroLeveId") Long heroLeveId){
        HeroLevelEntity heroLeve = heroLevelService.getById(heroLeveId);

        return R.ok().put("heroLeve", heroLeve);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:heroleve:save")
    public R save(@RequestBody HeroLevelEntity heroLeve){
        heroLevelService.save(heroLeve);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:heroleve:update")
    public R update(@RequestBody HeroLevelEntity heroLeve){
        ValidatorUtils.validateEntity(heroLeve);
        heroLevelService.updateById(heroLeve);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:heroleve:delete")
    public R delete(@RequestBody Long[] heroLeveIds){
        heroLevelService.removeByIds(Arrays.asList(heroLeveIds));

        return R.ok();
    }

}
