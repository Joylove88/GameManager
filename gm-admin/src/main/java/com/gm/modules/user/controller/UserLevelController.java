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

import com.gm.modules.user.entity.UserLevelEntity;
import com.gm.modules.user.service.UserLevelService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 玩家等级表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-07-02 15:28:42
 */
@RestController
@RequestMapping("user/userlevel")
public class UserLevelController {
    @Autowired
    private UserLevelService userLevelService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userlevel:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userLevelService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("user:userlevel:info")
    public R info(@PathVariable("id") Long id){
        UserLevelEntity userLevel = userLevelService.getById(id);

        return R.ok().put("userLevel", userLevel);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userlevel:save")
    public R save(@RequestBody UserLevelEntity userLevel){
        userLevelService.save(userLevel);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userlevel:update")
    public R update(@RequestBody UserLevelEntity userLevel){
        ValidatorUtils.validateEntity(userLevel);
        userLevelService.updateById(userLevel);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userlevel:delete")
    public R delete(@RequestBody Long[] ids){
        userLevelService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
