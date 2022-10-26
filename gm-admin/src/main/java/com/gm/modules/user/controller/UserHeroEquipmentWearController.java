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

import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import com.gm.modules.user.service.UserHeroEquipmentWearService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 玩家英雄装备穿戴表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 14:47:27
 */
@RestController
@RequestMapping("user/userheroequipmentwear")
public class UserHeroEquipmentWearController {
    @Autowired
    private UserHeroEquipmentWearService userHeroEquipmentWearService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userheroequipmentwear:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userHeroEquipmentWearService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{userHeroEquipmentWearId}")
    @RequiresPermissions("user:userheroequipmentwear:info")
    public R info(@PathVariable("userHeroEquipmentWearId") Long userHeroEquipmentWearId){
        UserHeroEquipmentWearEntity userHeroEquipmentWear = userHeroEquipmentWearService.getById(userHeroEquipmentWearId);

        return R.ok().put("userHeroEquipmentWear", userHeroEquipmentWear);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userheroequipmentwear:save")
    public R save(@RequestBody UserHeroEquipmentWearEntity userHeroEquipmentWear){
        userHeroEquipmentWearService.save(userHeroEquipmentWear);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userheroequipmentwear:update")
    public R update(@RequestBody UserHeroEquipmentWearEntity userHeroEquipmentWear){
        ValidatorUtils.validateEntity(userHeroEquipmentWear);
        userHeroEquipmentWearService.updateById(userHeroEquipmentWear);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userheroequipmentwear:delete")
    public R delete(@RequestBody Long[] userHeroEquipmentWearIds){
        userHeroEquipmentWearService.removeByIds(Arrays.asList(userHeroEquipmentWearIds));

        return R.ok();
    }

}
