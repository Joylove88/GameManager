package com.gm.modules.basicconfig.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.sys.controller.AbstractController;
import com.gm.modules.user.entity.UserEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.basicconfig.entity.GmDungeonConfigEntity;
import com.gm.modules.basicconfig.service.GmDungeonConfigService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 副本配置表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@RestController
@RequestMapping("basicconfig/gmdungeonconfig")
public class GmDungeonConfigController extends AbstractController{
    @Autowired
    private GmDungeonConfigService gmDungeonConfigService;
    @Autowired
    private FightCoreService fightCoreService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:gmdungeonconfig:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmDungeonConfigService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("basicconfig:gmdungeonconfig:info")
    public R info(@PathVariable("id") Long id){
        GmDungeonConfigEntity gmDungeonConfig = gmDungeonConfigService.getById(id);

        return R.ok().put("gmDungeonConfig", gmDungeonConfig);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:gmdungeonconfig:save")
    public R save(@RequestBody GmDungeonConfigEntity gmDungeonConfig){
        ValidatorUtils.validateEntity(gmDungeonConfig);
        Date now = new Date();
        gmDungeonConfig.setCreateUser(getUserId());
        gmDungeonConfig.setCreateTime(now);
        gmDungeonConfig.setCreateTimeTs(now.getTime());
        gmDungeonConfigService.save(gmDungeonConfig);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:gmdungeonconfig:update")
    public R update(@RequestBody GmDungeonConfigEntity gmDungeonConfig){
        ValidatorUtils.validateEntity(gmDungeonConfig);
        Date now = new Date();
        gmDungeonConfig.setUpdateUser(now.getTime());
        gmDungeonConfig.setUpdateTime(now);
        gmDungeonConfig.setUpdateTimeTs(now.getTime());
        gmDungeonConfigService.updateById(gmDungeonConfig);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:gmdungeonconfig:delete")
    public R delete(@RequestBody Long[] ids){
        gmDungeonConfigService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 模拟战斗
     */
    @RequestMapping("/testFight")
    public R useEx(@RequestBody GmDungeonConfigEntity gmDungeonConfig){
        fightCoreService.attck(new UserEntity());
        return R.ok();
    }

    /**
     * 获取副本
     */
    @RequestMapping("/getDungeons")
    @RequiresPermissions("basicconfig:gmdungeonconfig:list")
    public R getDungeons(){
        List<GmDungeonConfigEntity> list = gmDungeonConfigService.getDungeons();
        return R.ok().put("list",list);
    }

}
