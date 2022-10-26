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

import com.gm.modules.basicconfig.entity.HeroSkillEntity;
import com.gm.modules.basicconfig.service.HeroSkillService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 英雄技能表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-18 19:47:10
 */
@RestController
@RequestMapping("basicconfig/heroskill")
public class HeroSkillController extends AbstractController{
    @Autowired
    private HeroSkillService heroSkillService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:heroskill:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = heroSkillService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{heroSkillId}")
    @RequiresPermissions("basicconfig:heroskill:info")
    public R info(@PathVariable("heroSkillId") Long heroSkillId){
        HeroSkillEntity heroSkill = heroSkillService.getById(heroSkillId);

        return R.ok().put("heroSkill", heroSkill);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:heroskill:save")
    public R save(@RequestBody HeroSkillEntity heroSkill){
        ValidatorUtils.validateEntity(heroSkill);
        Date now = new Date();
        heroSkill.setCreateUser(getUserId());
        heroSkill.setCreateTime(now);
        heroSkill.setCreateTimeTs(now.getTime());
        heroSkillService.save(heroSkill);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:heroskill:update")
    public R update(@RequestBody HeroSkillEntity heroSkill){
        ValidatorUtils.validateEntity(heroSkill);
        Date now = new Date();
        heroSkill.setCreateUser(getUserId());
        heroSkill.setCreateTime(now);
        heroSkill.setCreateTimeTs(now.getTime());
        heroSkillService.updateById(heroSkill);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:heroskill:delete")
    public R delete(@RequestBody Long[] heroSkillIds){
        heroSkillService.removeByIds(Arrays.asList(heroSkillIds));

        return R.ok();
    }

}
