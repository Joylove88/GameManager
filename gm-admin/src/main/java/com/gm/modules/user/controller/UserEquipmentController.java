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

import com.gm.modules.user.entity.UserEquipmentEntity;
import com.gm.modules.user.service.UserEquipmentService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 玩家装备表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-15 19:37:43
 */
@RestController
@RequestMapping("user/userequipment")
public class UserEquipmentController {
    @Autowired
    private UserEquipmentService userEquipmentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userequipment:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userEquipmentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{userEquipmentId}")
    @RequiresPermissions("user:userequipment:info")
    public R info(@PathVariable("userEquipmentId") Long userEquipmentId){
        UserEquipmentEntity userEquipment = userEquipmentService.getById(userEquipmentId);

        return R.ok().put("userEquipment", userEquipment);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userequipment:save")
    public R save(@RequestBody UserEquipmentEntity userEquipment){
        userEquipmentService.save(userEquipment);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userequipment:update")
    public R update(@RequestBody UserEquipmentEntity userEquipment){
        ValidatorUtils.validateEntity(userEquipment);
        userEquipmentService.updateById(userEquipment);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userequipment:delete")
    public R delete(@RequestBody Long[] userEquipmentIds){
        userEquipmentService.removeByIds(Arrays.asList(userEquipmentIds));

        return R.ok();
    }

}
