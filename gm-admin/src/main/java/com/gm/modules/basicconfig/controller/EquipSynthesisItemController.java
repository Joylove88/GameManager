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

import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;
import com.gm.modules.basicconfig.service.EquipSynthesisItemService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 装备合成公式表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-14 14:07:17
 */
@RestController
@RequestMapping("basicconfig/equipsynthesisitem")
public class EquipSynthesisItemController {
    @Autowired
    private EquipSynthesisItemService equipSynthesisItemService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:equipsynthesisitem:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = equipSynthesisItemService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gmEquipSynthesisItemId}")
    @RequiresPermissions("basicconfig:equipsynthesisitem:info")
    public R info(@PathVariable("gmEquipSynthesisItemId") Long gmEquipSynthesisItemId){
        EquipSynthesisItemEntity equipSynthesisItem = equipSynthesisItemService.getById(gmEquipSynthesisItemId);

        return R.ok().put("equipSynthesisItem", equipSynthesisItem);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:equipsynthesisitem:save")
    public R save(@RequestBody EquipSynthesisItemEntity equipSynthesisItem){
        equipSynthesisItemService.save(equipSynthesisItem);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:equipsynthesisitem:update")
    public R update(@RequestBody EquipSynthesisItemEntity equipSynthesisItem){
        ValidatorUtils.validateEntity(equipSynthesisItem);
        equipSynthesisItemService.updateById(equipSynthesisItem);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:equipsynthesisitem:delete")
    public R delete(@RequestBody Long[] gmEquipSynthesisItemIds){
        equipSynthesisItemService.removeByIds(Arrays.asList(gmEquipSynthesisItemIds));

        return R.ok();
    }

}
