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

import com.gm.modules.user.entity.UserLoginLogEntity;
import com.gm.modules.user.service.UserLoginLogService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 用户登录日志
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-08 16:00:57
 */
@RestController
@RequestMapping("user/userloginlog")
public class UserLoginLogController {
    @Autowired
    private UserLoginLogService userLoginLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userloginlog:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userLoginLogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("user:userloginlog:info")
    public R info(@PathVariable("id") Long id){
        UserLoginLogEntity userLoginLog = userLoginLogService.getById(id);

        return R.ok().put("userLoginLog", userLoginLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userloginlog:save")
    public R save(@RequestBody UserLoginLogEntity userLoginLog){
        userLoginLogService.save(userLoginLog);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userloginlog:update")
    public R update(@RequestBody UserLoginLogEntity userLoginLog){
        ValidatorUtils.validateEntity(userLoginLog);
        userLoginLogService.updateById(userLoginLog);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userloginlog:delete")
    public R delete(@RequestBody Long[] ids){
        userLoginLogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
