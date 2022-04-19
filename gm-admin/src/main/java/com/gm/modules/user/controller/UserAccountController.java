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

import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.service.UserAccountService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 用户资金账户
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
@RestController
@RequestMapping("user/useraccount")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:useraccount:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userAccountService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{accountId}")
    @RequiresPermissions("user:useraccount:info")
    public R info(@PathVariable("accountId") Long accountId){
        UserAccountEntity userAccount = userAccountService.getById(accountId);

        return R.ok().put("userAccount", userAccount);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:useraccount:save")
    public R save(@RequestBody UserAccountEntity userAccount){
        userAccountService.save(userAccount);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:useraccount:update")
    public R update(@RequestBody UserAccountEntity userAccount){
        ValidatorUtils.validateEntity(userAccount);
        userAccountService.updateById(userAccount);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:useraccount:delete")
    public R delete(@RequestBody Long[] accountIds){
        userAccountService.removeByIds(Arrays.asList(accountIds));

        return R.ok();
    }

}
