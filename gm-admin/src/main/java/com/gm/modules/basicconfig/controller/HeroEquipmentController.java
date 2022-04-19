package com.gm.modules.basicconfig.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.basicconfig.entity.HeroEquipmentEntity;
import com.gm.modules.basicconfig.service.HeroEquipmentService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 英雄装备栏表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-17 18:07:31
 */
@RestController
@RequestMapping("basicconfig/heroequipment")
public class HeroEquipmentController extends AbstractController{
    @Autowired
    private HeroEquipmentService heroEquipmentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:heroequipment:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = heroEquipmentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gmHeroEquipmentId}")
    @RequiresPermissions("basicconfig:heroequipment:info")
    public R info(@PathVariable("gmHeroEquipmentId") Long gmHeroEquipmentId){
        HeroEquipmentEntity heroEquipment = heroEquipmentService.getById(gmHeroEquipmentId);

        return R.ok().put("heroEquipment", heroEquipment);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:heroequipment:save")
    public R save(@RequestBody HeroEquipmentEntity heroEquipment){
        ValidatorUtils.validateEntity(heroEquipment);
        Date now = new Date();
        heroEquipment.setCreateUser(getUserId());
        heroEquipment.setCreateTime(now);
        heroEquipment.setCreateTimeTs(now.getTime());
        heroEquipmentService.save(heroEquipment);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:heroequipment:update")
    public R update(@RequestBody HeroEquipmentEntity heroEquipment){
        ValidatorUtils.validateEntity(heroEquipment);
        Date now = new Date();
        heroEquipment.setUpdateUser(getUserId());
        heroEquipment.setUpdateTime(now);
        heroEquipment.setUpdateTimeTs(now.getTime());
        heroEquipmentService.updateById(heroEquipment);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:heroequipment:delete")
    public R delete(@RequestBody Long[] gmHeroEquipmentIds){
        heroEquipmentService.removeByIds(Arrays.asList(gmHeroEquipmentIds));

        return R.ok();
    }

}
