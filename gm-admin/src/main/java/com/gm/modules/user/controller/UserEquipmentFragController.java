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

import com.gm.modules.user.entity.UserEquipmentFragEntity;
import com.gm.modules.user.service.UserEquipmentFragService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 玩家装备碎片表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-15 19:37:43
 */
@RestController
@RequestMapping("user/userequipmentfrag")
public class UserEquipmentFragController {
    @Autowired
    private UserEquipmentFragService userEquipmentFragService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userequipmentfrag:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userEquipmentFragService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gmUserEquipmentFragId}")
    @RequiresPermissions("user:userequipmentfrag:info")
    public R info(@PathVariable("gmUserEquipmentFragId") Long gmUserEquipmentFragId){
        UserEquipmentFragEntity userEquipmentFrag = userEquipmentFragService.getById(gmUserEquipmentFragId);

        return R.ok().put("userEquipmentFrag", userEquipmentFrag);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userequipmentfrag:save")
    public R save(@RequestBody UserEquipmentFragEntity userEquipmentFrag){
        userEquipmentFragService.save(userEquipmentFrag);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userequipmentfrag:update")
    public R update(@RequestBody UserEquipmentFragEntity userEquipmentFrag){
        ValidatorUtils.validateEntity(userEquipmentFrag);
        userEquipmentFragService.updateById(userEquipmentFrag);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userequipmentfrag:delete")
    public R delete(@RequestBody Long[] gmUserEquipmentFragIds){
        userEquipmentFragService.removeByIds(Arrays.asList(gmUserEquipmentFragIds));

        return R.ok();
    }

}
