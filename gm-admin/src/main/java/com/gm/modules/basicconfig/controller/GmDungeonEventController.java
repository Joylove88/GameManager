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

import com.gm.modules.basicconfig.entity.GmDungeonEventEntity;
import com.gm.modules.basicconfig.service.GmDungeonEventService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 副本事件表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-06-10 15:57:41
 */
@RestController
@RequestMapping("basicconfig/gmdungeonevent")
public class GmDungeonEventController extends AbstractController{
    @Autowired
    private GmDungeonEventService gmDungeonEventService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:gmdungeonevent:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmDungeonEventService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("basicconfig:gmdungeonevent:info")
    public R info(@PathVariable("id") Long id){
        GmDungeonEventEntity gmDungeonEvent = gmDungeonEventService.getById(id);

        return R.ok().put("gmDungeonEvent", gmDungeonEvent);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:gmdungeonevent:save")
    public R save(@RequestBody GmDungeonEventEntity gmDungeonEvent){
        ValidatorUtils.validateEntity(gmDungeonEvent);
        Date now = new Date();
        gmDungeonEvent.setCreateUser(getUserId());
        gmDungeonEvent.setCreateTime(now);
        gmDungeonEvent.setCreateTimeTs(now.getTime());
        gmDungeonEventService.save(gmDungeonEvent);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:gmdungeonevent:update")
    public R update(@RequestBody GmDungeonEventEntity gmDungeonEvent){
        ValidatorUtils.validateEntity(gmDungeonEvent);
        Date now = new Date();
        gmDungeonEvent.setUpdateUser(now.getTime());
        gmDungeonEvent.setUpdateTime(now);
        gmDungeonEvent.setUpdateTimeTs(now.getTime());
        gmDungeonEventService.updateById(gmDungeonEvent);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:gmdungeonevent:delete")
    public R delete(@RequestBody Long[] ids){
        gmDungeonEventService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
