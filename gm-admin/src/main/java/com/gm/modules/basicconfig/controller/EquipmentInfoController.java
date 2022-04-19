package com.gm.modules.basicconfig.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

import com.gm.modules.basicconfig.entity.EquipmentInfoEntity;
import com.gm.modules.basicconfig.service.EquipmentInfoService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 装备基础表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 17:20:39
 */
@RestController
@RequestMapping("basicconfig/equipmentinfo")
public class EquipmentInfoController extends AbstractController {
    @Autowired
    private EquipmentInfoService equipmentInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:equipmentinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = equipmentInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{equipId}")
    @RequiresPermissions("basicconfig:equipmentinfo:info")
    public R info(@PathVariable("equipId") Long equipId){
        EquipmentInfoEntity equipmentInfo = equipmentInfoService.getById(equipId);

        return R.ok().put("equipmentInfo", equipmentInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:equipmentinfo:save")
    public R save(@RequestBody EquipmentInfoEntity equipmentInfo){
        ValidatorUtils.validateEntity(equipmentInfo);
        Date now = new Date();
        equipmentInfo.setCreateUser(getUserId());
        equipmentInfo.setCreateTime(now);
        equipmentInfo.setCreateTimeTs(now.getTime());
        equipmentInfoService.save(equipmentInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:equipmentinfo:update")
    public R update(@RequestBody EquipmentInfoEntity equipmentInfo){
        ValidatorUtils.validateEntity(equipmentInfo);
        Date now = new Date();
        equipmentInfo.setUpdateUser(now.getTime());
        equipmentInfo.setUpdateTime(now);
        equipmentInfo.setUpdateTimeTs(now.getTime());
        equipmentInfoService.updateById(equipmentInfo);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:equipmentinfo:delete")
    public R delete(@RequestBody Long[] equipIds){
        equipmentInfoService.removeByIds(Arrays.asList(equipIds));

        return R.ok();
    }
    /**
     * 更新装备合成公式json
     */
    @RequestMapping("/updateEquipJson")
    @RequiresPermissions("basicconfig:equipmentinfo:update")
    public R updateEquipJson(@RequestBody Long[] equipIds){
        equipmentInfoService.updateEquipJson(equipIds);

        return R.ok();
    }


    /**
     * 获取紫色，橙色装备
     */
    @RequestMapping("/getEquipmentInfoList")
    @RequiresPermissions("basicconfig:equipmentinfo:list")
    public R getEquipmentInfoList(){
        List<EquipmentInfoEntity> list = equipmentInfoService.queryList();
        return R.ok().put("list",list);
    }

}
