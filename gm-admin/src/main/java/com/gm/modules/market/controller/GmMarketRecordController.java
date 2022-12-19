package com.gm.modules.market.controller;

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

import com.gm.modules.market.entity.GmMarketRecordEntity;
import com.gm.modules.market.service.GmMarketRecordService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 市场交易订单
 *
 * @author market
 * @email market@gmail.com
 * @date 2022-12-16 15:18:36
 */
@RestController
@RequestMapping("market/gmmarketrecord")
public class GmMarketRecordController {
    @Autowired
    private GmMarketRecordService gmMarketRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("market:gmmarketrecord:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmMarketRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("market:gmmarketrecord:info")
    public R info(@PathVariable("id") Long id){
        GmMarketRecordEntity gmMarketRecord = gmMarketRecordService.getById(id);

        return R.ok().put("gmMarketRecord", gmMarketRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("market:gmmarketrecord:save")
    public R save(@RequestBody GmMarketRecordEntity gmMarketRecord){
        gmMarketRecordService.save(gmMarketRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("market:gmmarketrecord:update")
    public R update(@RequestBody GmMarketRecordEntity gmMarketRecord){
        ValidatorUtils.validateEntity(gmMarketRecord);
        gmMarketRecordService.updateById(gmMarketRecord);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("market:gmmarketrecord:delete")
    public R delete(@RequestBody Long[] ids){
        gmMarketRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
