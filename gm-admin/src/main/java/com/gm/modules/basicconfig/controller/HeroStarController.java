package com.gm.modules.basicconfig.controller;

import java.util.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gm.common.annotation.SysLog;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.HeroStarEntity;
import com.gm.modules.basicconfig.entity.StarInfoEntity;
import com.gm.modules.basicconfig.service.StarInfoService;
import com.gm.modules.sys.controller.AbstractController;
import com.gm.common.utils.QueryNew;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.basicconfig.service.HeroStarService;


/**
 * 
 * 奖池英雄
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@RestController
@RequestMapping("basicconfig/herostar")
public class HeroStarController extends AbstractController {
    @Autowired
    private HeroStarService heroStarService;
    @Autowired
    private StarInfoService starInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:herostar:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = heroStarService.queryPage(params);
        //查询列表数据
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gmHeroStarId}")
    @RequiresPermissions("basicconfig:herostar:info")
    public R info(@PathVariable("gmHeroStarId") Long gmHeroStarId){
        HeroStarEntity heroStar = heroStarService.getById(gmHeroStarId);

        return R.ok().put("heroStar", heroStar);
    }

    /**
     * 保存
     */
    @SysLog("新增奖池英雄")
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:herostar:save")
    public R save(@RequestBody HeroStarEntity heroStar){
        ValidatorUtils.validateEntity(heroStar);
        if(heroStar.getGmStarId() == 1000){//选择0星 系统自动生成5个星级的英雄
            List<StarInfoEntity> starInfoEntityList = starInfoService.list();
            for(int i = 0; i < starInfoEntityList.size(); i++){
                HeroStarEntity heroStarS = new HeroStarEntity();
                Date now = new Date();
                heroStarS.setGmHeroId(heroStar.getGmHeroId());
                heroStarS.setGmStarId(starInfoEntityList.get(i).getGmStarId());
                heroStarS.setGmProbabilityId(starInfoEntityList.get(i).getGmStarCode());
                heroStarS.setStatus(heroStar.getStatus());
                heroStarS.setScale(heroStar.getScale());
                heroStarS.setCreateUser(getUserCode());
                heroStarS.setCreateTime(now);
                heroStarS.setCreateTimeTs(now.getTime());
                Long starAtt = starInfoEntityList.get(i).getGmStarAttributes();
                heroStarS.setGmHealth(heroStar.getGmHealth() * starAtt);
                heroStarS.setGmGrowHealth(heroStar.getGmGrowHealth());
                heroStarS.setGmHealthRegen(heroStar.getGmHealthRegen() * starAtt);
                heroStarS.setGmGrowHealthRegen(heroStar.getGmGrowHealthRegen());
                heroStarS.setGmMana(heroStar.getGmMana() * starAtt);
                heroStarS.setGmGrowMana(heroStar.getGmGrowMana());
                heroStarS.setGmManaRegen(heroStar.getGmManaRegen() * starAtt);
                heroStarS.setGmGrowManaRegen(heroStar.getGmGrowManaRegen());
                heroStarS.setGmArmor(heroStar.getGmArmor() * starAtt);
                heroStarS.setGmGrowArmor(heroStar.getGmGrowArmor());
                heroStarS.setGmMagicResist(heroStar.getGmMagicResist() * starAtt);
                heroStarS.setGmGrowMagicResist(heroStar.getGmGrowMagicResist());
                heroStarS.setGmAttackDamage(heroStar.getGmAttackDamage() * starAtt);
                heroStarS.setGmGrowAttackDamage(heroStar.getGmGrowAttackDamage());
                heroStarService.save(heroStarS);
            }
        } else {
            Date now = new Date();
            heroStar.setCreateUser(getUserCode());
            heroStar.setCreateTime(now);
            heroStar.setCreateTimeTs(now.getTime());
            heroStarService.save(heroStar);
        }

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改奖池英雄")
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:herostar:update")
    public R update(@RequestBody HeroStarEntity heroStar){
        ValidatorUtils.validateEntity(heroStar);
        Date now = new Date();
        heroStar.setUpdateUser(getUserCode());
        heroStar.setUpdateTime(now);
        heroStar.setUpdateTimeTs(now.getTime());
        heroStarService.updateById(heroStar);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除奖池英雄")
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:herostar:delete")
    public R delete(@RequestBody Long[] gmHeroStarIds){
        heroStarService.removeByIds(Arrays.asList(gmHeroStarIds));

        return R.ok();
    }

    /**
     * 获取星级英雄
     */
    @RequestMapping("/getHeroStars")
    @RequiresPermissions("basicconfig:herostar:list")
    public R getHeroStars(){
        List<HeroStarEntity> list = heroStarService.getHeroStars();
        return R.ok().put("list",list);
    }
}
