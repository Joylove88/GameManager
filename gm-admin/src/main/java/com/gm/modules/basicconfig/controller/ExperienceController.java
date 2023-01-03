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

import com.gm.modules.basicconfig.entity.ExperienceEntity;
import com.gm.modules.basicconfig.service.ExperienceService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 经验道具表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:18:58
 */
@RestController
@RequestMapping("basicconfig/experience")
public class ExperienceController extends AbstractController{
    @Autowired
    private ExperienceService experienceService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:experience:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = experienceService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("basicconfig:experience:info")
    public R info(@PathVariable("id") Long id){
        ExperienceEntity experience = experienceService.getById(id);

        return R.ok().put("experience", experience);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:experience:save")
    public R save(@RequestBody ExperienceEntity experience){
        ValidatorUtils.validateEntity(experience);
        Date now = new Date();
        experience.setCreateUser(getUserId());
        experience.setCreateTime(now);
        experience.setCreateTimeTs(now.getTime());
        experienceService.save(experience);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:experience:update")
    public R update(@RequestBody ExperienceEntity experience){
        ValidatorUtils.validateEntity(experience);
        Date now = new Date();
        experience.setCreateUser(getUserId());
        experience.setCreateTime(now);
        experience.setCreateTimeTs(now.getTime());
        experienceService.updateById(experience);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:experience:delete")
    public R delete(@RequestBody Long[] ids){
        experienceService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
