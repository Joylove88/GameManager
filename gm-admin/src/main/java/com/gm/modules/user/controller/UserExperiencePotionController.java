package com.gm.modules.user.controller;

import java.util.Arrays;
import java.util.Map;

import com.gm.common.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.user.entity.UserExperiencePotionEntity;
import com.gm.modules.user.service.UserExperiencePotionService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 玩家经验药水信息表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:47:36
 */
@RestController
@RequestMapping("user/userexperiencepotion")
public class UserExperiencePotionController {
    @Autowired
    private UserExperiencePotionService userExperiencePotionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userexperiencepotion:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userExperiencePotionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{userExPotionId}")
    @RequiresPermissions("user:userexperiencepotion:info")
    public R info(@PathVariable("userExPotionId") Long userExPotionId){
        UserExperiencePotionEntity userExperiencePotion = userExperiencePotionService.getById(userExPotionId);

        return R.ok().put("userExperiencePotion", userExperiencePotion);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userexperiencepotion:save")
    public R save(@RequestBody UserExperiencePotionEntity userExperiencePotion){
        userExperiencePotionService.save(userExperiencePotion);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userexperiencepotion:update")
    public R update(@RequestBody UserExperiencePotionEntity userExperiencePotion){
        ValidatorUtils.validateEntity(userExperiencePotion);
        userExperiencePotionService.updateById(userExperiencePotion);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userexperiencepotion:delete")
    public R delete(@RequestBody Long[] userExPotionIds){
        userExperiencePotionService.removeByIds(Arrays.asList(userExPotionIds));

        return R.ok();
    }

}
