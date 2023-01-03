package com.gm.modules.user.controller;

import java.util.Arrays;
import java.util.Map;

import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.user.entity.UserExperienceEntity;
import com.gm.modules.user.service.UserExperienceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 玩家经验道具信息
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:47:36
 */
@RestController
@RequestMapping("user/userexperience")
public class UserExperienceController {
    @Autowired
    private UserExperienceService userExperienceService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userexperience:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userExperienceService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("user:userexperience:info")
    public R info(@PathVariable("id") Long id){
        UserExperienceEntity userexperience = userExperienceService.getById(id);

        return R.ok().put("userexperience", userexperience);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userexperience:save")
    public R save(@RequestBody UserExperienceEntity userexperience){
        userExperienceService.save(userexperience);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userexperience:update")
    public R update(@RequestBody UserExperienceEntity userexperience){
        ValidatorUtils.validateEntity(userexperience);
        userExperienceService.updateById(userexperience);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userexperience:delete")
    public R delete(@RequestBody Long[] ids){
        userExperienceService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
