package com.gm.modules.basicconfig.controller;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.GmMonsterConfigEntity;
import com.gm.modules.basicconfig.service.GmMonsterConfigService;
import com.gm.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;



/**
 * 怪物配置表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@RestController
@RequestMapping("basicconfig/gmmonsterconfig")
public class GmMonsterConfigController extends AbstractController{
    @Autowired
    private GmMonsterConfigService gmMonsterConfigService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:gmmonsterconfig:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmMonsterConfigService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("basicconfig:gmmonsterconfig:info")
    public R info(@PathVariable("id") Long id){
        GmMonsterConfigEntity gmMonsterConfig = gmMonsterConfigService.getById(id);

        return R.ok().put("gmMonsterConfig", gmMonsterConfig);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:gmmonsterconfig:save")
    public R save(@RequestBody GmMonsterConfigEntity gmMonsterConfig){
        ValidatorUtils.validateEntity(gmMonsterConfig);
        Date now = new Date();
        gmMonsterConfig.setCreateUser(getUserId());
        gmMonsterConfig.setCreateTime(now);
        gmMonsterConfig.setCreateTimeTs(now.getTime());
        gmMonsterConfigService.save(gmMonsterConfig);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:gmmonsterconfig:update")
    public R update(@RequestBody GmMonsterConfigEntity gmMonsterConfig){
        ValidatorUtils.validateEntity(gmMonsterConfig);
        Date now = new Date();
        gmMonsterConfig.setUpdateUser(now.getTime());
        gmMonsterConfig.setUpdateTime(now);
        gmMonsterConfig.setUpdateTimeTs(now.getTime());
        gmMonsterConfigService.updateById(gmMonsterConfig);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:gmmonsterconfig:delete")
    public R delete(@RequestBody Long[] ids){
        gmMonsterConfigService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
