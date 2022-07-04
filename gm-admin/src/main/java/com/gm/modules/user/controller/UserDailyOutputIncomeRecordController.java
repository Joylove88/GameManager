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

import com.gm.modules.user.entity.UserDailyOutputIncomeRecordEntity;
import com.gm.modules.user.service.UserDailyOutputIncomeRecordService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 玩家每日可产出收益记录表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-07-04 16:38:24
 */
@RestController
@RequestMapping("user/userdailyoutputincomerecord")
public class UserDailyOutputIncomeRecordController {
    @Autowired
    private UserDailyOutputIncomeRecordService userDailyOutputIncomeRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userdailyoutputincomerecord:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userDailyOutputIncomeRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("user:userdailyoutputincomerecord:info")
    public R info(@PathVariable("id") Long id){
        UserDailyOutputIncomeRecordEntity userDailyOutputIncomeRecord = userDailyOutputIncomeRecordService.getById(id);

        return R.ok().put("userDailyOutputIncomeRecord", userDailyOutputIncomeRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userdailyoutputincomerecord:save")
    public R save(@RequestBody UserDailyOutputIncomeRecordEntity userDailyOutputIncomeRecord){
        userDailyOutputIncomeRecordService.save(userDailyOutputIncomeRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userdailyoutputincomerecord:update")
    public R update(@RequestBody UserDailyOutputIncomeRecordEntity userDailyOutputIncomeRecord){
        ValidatorUtils.validateEntity(userDailyOutputIncomeRecord);
        userDailyOutputIncomeRecordService.updateById(userDailyOutputIncomeRecord);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userdailyoutputincomerecord:delete")
    public R delete(@RequestBody Long[] ids){
        userDailyOutputIncomeRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
