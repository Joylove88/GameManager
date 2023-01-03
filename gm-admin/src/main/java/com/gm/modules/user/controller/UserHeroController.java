package com.gm.modules.user.controller;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.user.entity.UserExperienceEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.rsp.UserExpInfoRsp;
import com.gm.modules.user.service.UserExperienceService;
import com.gm.modules.user.service.UserHeroService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:10:34
 */
@RestController
@RequestMapping("user/userhero")
public class UserHeroController {
    @Autowired
    private UserHeroService userHeroService;
    @Autowired
    private UserExperienceService userExService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userhero:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userHeroService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{userHeroId}")
    @RequiresPermissions("user:userhero:info")
    public R info(@PathVariable("userHeroId") Long userHeroId){
        UserHeroEntity userHero = userHeroService.getById(userHeroId);

        return R.ok().put("userHero", userHero);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userhero:save")
    public R save(@RequestBody UserHeroEntity userHero){
        userHeroService.save(userHero);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userhero:update")
    public R update(@RequestBody UserHeroEntity userHero){
        ValidatorUtils.validateEntity(userHero);
        userHeroService.updateById(userHero);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userhero:delete")
    public R delete(@RequestBody Long[] userHeroIds){
        userHeroService.removeByIds(Arrays.asList(userHeroIds));

        return R.ok();
    }

    /**
     * 获取玩家拥有的经验道具
     */
    @RequestMapping("/getUserExs")
    @RequiresPermissions("user:userhero:list")
    public R getUserExs(@RequestBody UserExperienceEntity userExperienceEntity){
        List<UserExpInfoRsp> list = userExService.getUserExp(new HashMap<>());
        return R.ok().put("list",list);
    }
    /**
     * 对玩家英雄使用经验道具
     */
    @RequestMapping("/userHeroUseEx")
    @RequiresPermissions("user:userhero:list")
    public R useEx(@RequestBody UserExperienceEntity userExperienceEntity){
        userExService.userHeroUseExp(null, null);
        return R.ok();
    }
}
