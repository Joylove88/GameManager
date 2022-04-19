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

import com.gm.modules.basicconfig.entity.EquipmentFragEntity;
import com.gm.modules.basicconfig.service.EquipmentFragService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 装备碎片表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 19:09:21
 */
@RestController
@RequestMapping("basicconfig/equipmentfrag")
public class EquipmentFragController {
    @Autowired
    private EquipmentFragService equipmentFragService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:equipmentfrag:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = equipmentFragService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{equipmentFragId}")
    @RequiresPermissions("basicconfig:equipmentfrag:info")
    public R info(@PathVariable("equipmentFragId") Long equipmentFragId){
        EquipmentFragEntity equipmentFrag = equipmentFragService.getById(equipmentFragId);

        return R.ok().put("equipmentFrag", equipmentFrag);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:equipmentfrag:save")
    public R save(@RequestBody EquipmentFragEntity equipmentFrag){
        equipmentFragService.save(equipmentFrag);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:equipmentfrag:update")
    public R update(@RequestBody EquipmentFragEntity equipmentFrag){
        ValidatorUtils.validateEntity(equipmentFrag);
        equipmentFragService.updateById(equipmentFrag);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:equipmentfrag:delete")
    public R delete(@RequestBody Long[] equipmentFragIds){
        equipmentFragService.removeByIds(Arrays.asList(equipmentFragIds));

        return R.ok();
    }

}
