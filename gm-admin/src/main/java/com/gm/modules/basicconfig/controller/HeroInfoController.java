package com.gm.modules.basicconfig.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.gm.common.annotation.SysLog;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.sys.entity.SysDictEntity;
import com.gm.modules.basicconfig.entity.HeroInfoEntity;
import com.gm.modules.basicconfig.service.HeroInfoService;
import com.gm.modules.sys.controller.AbstractController;
import com.gm.modules.sys.service.SysDictService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * 
 * 英雄基础信息配置
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:04:10
 */
@RestController
@RequestMapping("basicconfig/heroinfo")
public class HeroInfoController extends AbstractController {
    @Autowired
    private HeroInfoService heroInfoService;
    @Autowired
    private SysDictService sysDictService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:heroinfo:list")
    public R list(@RequestParam Map<String, Object> params){
//        //查询列表数据
//        QueryNew query = new QueryNew(params);//防止sql注入
//        Page pagination = new Page(query.getPage(), query.getLimit());//分页
//        List<HeroInfoEntity> heroInfoEntities = heroInfoService.queryList(pagination, query);
//        PageUtils page = new PageUtils(heroInfoEntities, (int) pagination.getTotal(), (int) pagination.getSize(), (int) pagination.getCurrent());
        PageUtils page = heroInfoService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{heroId}")
    @RequiresPermissions("basicconfig:heroinfo:info")
    public R info(@PathVariable("heroId") Long heroId){
        HeroInfoEntity heroInfo = heroInfoService.getById(heroId);

        return R.ok().put("heroInfo", heroInfo);
    }

    /**
     * 保存
     */
    @SysLog("新增基础英雄")
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:heroinfo:save")
    public R save(@RequestBody HeroInfoEntity heroInfo){
        //校验类型
        ValidatorUtils.validateEntity(heroInfo);
        Date now = new Date();
        heroInfo.setCreateUser(getUserCode());
        heroInfo.setCreateTime(now);
        heroInfo.setCreateTimeTs(now.getTime());
        heroInfoService.save(heroInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改基础英雄")
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:heroinfo:update")
    public R update(@RequestBody HeroInfoEntity heroInfo){
        ValidatorUtils.validateEntity(heroInfo);
        Date now = new Date();
        heroInfo.setUpdateUser(getUserCode());
        heroInfo.setUpdateTime(now);
        heroInfo.setUpdateTimeTs(now.getTime());
        heroInfoService.updateById(heroInfo);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除基础英雄")
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:heroinfo:delete")
    public R delete(@RequestBody Long[] heroIds){
        heroInfoService.removeByIds(Arrays.asList(heroIds));

        return R.ok();
    }

    /**
     * 获取英雄职业类型
     */
    @RequestMapping("/getHeroTypeList")
    @RequiresPermissions("basicconfig:heroinfo:list")
    public R getHeroTypeList(){
        List<SysDictEntity> list = sysDictService.getSysDict("GM_HERO_INFO","GM_HERO_TYPE");
        return R.ok().put("list",list);
    }

    /**
     * 获取英雄
     */
    @RequestMapping("/getHeroInfoList")
    @RequiresPermissions("basicconfig:heroinfo:list")
    public R getHeroInfoList(){
        List<HeroInfoEntity> list = heroInfoService.queryList();
        return R.ok().put("list",list);
    }


}
