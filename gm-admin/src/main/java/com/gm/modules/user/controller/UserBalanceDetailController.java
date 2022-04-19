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

import com.gm.modules.user.entity.UserBalanceDetailEntity;
import com.gm.modules.user.service.UserBalanceDetailService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 用户资金明细：记录余额变动
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
@RestController
@RequestMapping("user/userbalancedetail")
public class UserBalanceDetailController {
    @Autowired
    private UserBalanceDetailService userBalanceDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userbalancedetail:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userBalanceDetailService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{userBalanceDetailId}")
    @RequiresPermissions("user:userbalancedetail:info")
    public R info(@PathVariable("userBalanceDetailId") Long userBalanceDetailId){
        UserBalanceDetailEntity userBalanceDetail = userBalanceDetailService.getById(userBalanceDetailId);

        return R.ok().put("userBalanceDetail", userBalanceDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userbalancedetail:save")
    public R save(@RequestBody UserBalanceDetailEntity userBalanceDetail){
        userBalanceDetailService.save(userBalanceDetail);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userbalancedetail:update")
    public R update(@RequestBody UserBalanceDetailEntity userBalanceDetail){
        ValidatorUtils.validateEntity(userBalanceDetail);
        userBalanceDetailService.updateById(userBalanceDetail);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userbalancedetail:delete")
    public R delete(@RequestBody Long[] userBalanceDetailIds){
        userBalanceDetailService.removeByIds(Arrays.asList(userBalanceDetailIds));

        return R.ok();
    }

}
