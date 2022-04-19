package com.gm.modules.basicconfig.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gm.common.annotation.SysLog;
import com.gm.common.utils.R;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.QueryNew;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.HeroFragEntity;
import com.gm.modules.basicconfig.service.HeroFragService;
import com.gm.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * 
 * 英雄碎片配置
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-27 20:23:36
 */
@RestController
@RequestMapping("basicconfig/herofrag")
public class HeroFragController extends AbstractController {
    @Autowired
    private HeroFragService heroFragService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:herofrag:list")
    public R list(@RequestParam Map<String, Object> params){
        //查询列表数据
        QueryNew query = new QueryNew(params);//防止sql注入
        Page pagination = new Page(query.getPage(), query.getLimit());//分页
        List<HeroFragEntity> heroFragEntities = heroFragService.queryList(pagination, query);
        PageUtils page = new PageUtils(heroFragEntities, (int) pagination.getTotal(), (int) pagination.getSize(), (int) pagination.getCurrent());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gmHeroFragId}")
    @RequiresPermissions("basicconfig:herofrag:info")
    public R info(@PathVariable("gmHeroFragId") Long gmHeroFragId){
        HeroFragEntity heroFrag = heroFragService.getById(gmHeroFragId);

        return R.ok().put("heroFrag", heroFrag);
    }

    /**
     * 保存
     */
    @SysLog("新增奖池英雄碎片")
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:herofrag:save")
    public R save(@RequestBody HeroFragEntity heroFrag){
        Date now = new Date();
        heroFrag.setCreateUser(getUserCode());
        heroFrag.setCreateTime(now);
        heroFrag.setCreateTimeTs(now.getTime());
        heroFragService.save(heroFrag);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改奖池英雄碎片")
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:herofrag:update")
    public R update(@RequestBody HeroFragEntity heroFrag){
        ValidatorUtils.validateEntity(heroFrag);
        Date now = new Date();
        heroFrag.setUpdateUser(getUserCode());
        heroFrag.setUpdateTime(now);
        heroFrag.setUpdateTimeTs(now.getTime());
        heroFragService.updateById(heroFrag);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除奖池英雄碎片")
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:herofrag:delete")
    public R delete(@RequestBody Long[] gmHeroFragIds){
        heroFragService.removeByIds(Arrays.asList(gmHeroFragIds));

        return R.ok();
    }

}
