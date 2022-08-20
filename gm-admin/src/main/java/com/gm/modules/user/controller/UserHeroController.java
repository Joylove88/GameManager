package com.gm.modules.user.controller;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.user.entity.UserExperiencePotionEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.rsp.UserExpInfoRsp;
import com.gm.modules.user.service.UserExperiencePotionService;
import com.gm.modules.user.service.UserHeroService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    private UserExperiencePotionService userExService;

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
    @RequestMapping("/info/{gmUserHeroId}")
    @RequiresPermissions("user:userhero:info")
    public R info(@PathVariable("gmUserHeroId") Long gmUserHeroId){
        UserHeroEntity userHero = userHeroService.getById(gmUserHeroId);

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
    public R delete(@RequestBody Long[] gmUserHeroIds){
        userHeroService.removeByIds(Arrays.asList(gmUserHeroIds));

        return R.ok();
    }

    /**
     * 获取玩家拥有的经验药水
     */
    @RequestMapping("/getUserExs")
    @RequiresPermissions("user:userhero:list")
    public R getUserExs(@RequestBody UserExperiencePotionEntity userExperiencePotionEntity){
        List<UserExpInfoRsp> list = userExService.getUserEx(userExperiencePotionEntity);
        return R.ok().put("list",list);
    }
    /**
     * 对玩家英雄使用经验药水
     */
    @RequestMapping("/userHeroUseEx")
    @RequiresPermissions("user:userhero:list")
    public R useEx(@RequestBody UserExperiencePotionEntity userExperiencePotionEntity){
        userExService.userHeroUseEx(null, null);
        return R.ok();
    }
}
