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

import com.gm.modules.basicconfig.entity.ExperiencePotionEntity;
import com.gm.modules.basicconfig.service.ExperiencePotionService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 经验药水表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:18:58
 */
@RestController
@RequestMapping("basicconfig/experiencepotion")
public class ExperiencePotionController extends AbstractController{
    @Autowired
    private ExperiencePotionService experiencePotionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:experiencepotion:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = experiencePotionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gmExPotionId}")
    @RequiresPermissions("basicconfig:experiencepotion:info")
    public R info(@PathVariable("gmExPotionId") Long gmExPotionId){
        ExperiencePotionEntity experiencePotion = experiencePotionService.getById(gmExPotionId);

        return R.ok().put("experiencePotion", experiencePotion);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:experiencepotion:save")
    public R save(@RequestBody ExperiencePotionEntity experiencePotion){
        ValidatorUtils.validateEntity(experiencePotion);
        Date now = new Date();
        experiencePotion.setCreateUser(getUserId());
        experiencePotion.setCreateTime(now);
        experiencePotion.setCreateTimeTs(now.getTime());
        experiencePotionService.save(experiencePotion);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:experiencepotion:update")
    public R update(@RequestBody ExperiencePotionEntity experiencePotion){
        ValidatorUtils.validateEntity(experiencePotion);
        Date now = new Date();
        experiencePotion.setCreateUser(getUserId());
        experiencePotion.setCreateTime(now);
        experiencePotion.setCreateTimeTs(now.getTime());
        experiencePotionService.updateById(experiencePotion);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:experiencepotion:delete")
    public R delete(@RequestBody Long[] gmExPotionIds){
        experiencePotionService.removeByIds(Arrays.asList(gmExPotionIds));

        return R.ok();
    }

}
