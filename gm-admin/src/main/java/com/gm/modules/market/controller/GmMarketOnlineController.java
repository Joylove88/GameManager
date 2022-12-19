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

import com.gm.modules.market.entity.GmMarketOnlineEntity;
import com.gm.modules.market.service.GmMarketOnlineService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 市场在售物品
 *
 * @author market
 * @email market@gmail.com
 * @date 2022-12-16 15:18:38
 */
@RestController
@RequestMapping("market/gmmarketonline")
public class GmMarketOnlineController {
    @Autowired
    private GmMarketOnlineService gmMarketOnlineService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("market:gmmarketonline:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmMarketOnlineService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("market:gmmarketonline:info")
    public R info(@PathVariable("id") Long id){
        GmMarketOnlineEntity gmMarketOnline = gmMarketOnlineService.getById(id);

        return R.ok().put("gmMarketOnline", gmMarketOnline);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("market:gmmarketonline:save")
    public R save(@RequestBody GmMarketOnlineEntity gmMarketOnline){
        gmMarketOnlineService.save(gmMarketOnline);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("market:gmmarketonline:update")
    public R update(@RequestBody GmMarketOnlineEntity gmMarketOnline){
        ValidatorUtils.validateEntity(gmMarketOnline);
        gmMarketOnlineService.updateById(gmMarketOnline);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("market:gmmarketonline:delete")
    public R delete(@RequestBody Long[] ids){
        gmMarketOnlineService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
