package com.gm.modules.basicconfig.controller;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;
import com.gm.modules.basicconfig.service.GmTeamConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 队伍配置表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@RestController
@RequestMapping("basicconfig/gmteamconfig")
public class GmTeamConfigController {
    @Autowired
    private GmTeamConfigService gmTeamConfigService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:gmteamconfig:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmTeamConfigService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("basicconfig:gmteamconfig:info")
    public R info(@PathVariable("id") Long id){
        GmTeamConfigEntity gmTeamConfig = gmTeamConfigService.getById(id);

        return R.ok().put("gmTeamConfig", gmTeamConfig);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:gmteamconfig:save")
    public R save(@RequestBody GmTeamConfigEntity gmTeamConfig){
        gmTeamConfigService.save(gmTeamConfig);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:gmteamconfig:update")
    public R update(@RequestBody GmTeamConfigEntity gmTeamConfig){
        ValidatorUtils.validateEntity(gmTeamConfig);
        gmTeamConfigService.updateById(gmTeamConfig);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:gmteamconfig:delete")
    public R delete(@RequestBody Long[] ids){
        gmTeamConfigService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
