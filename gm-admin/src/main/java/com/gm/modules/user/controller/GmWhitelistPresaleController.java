package com.gm.modules.user.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.gm.common.utils.Constant;
import com.gm.common.utils.DateUtils;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gm.modules.user.entity.GmWhitelistPresaleEntity;
import com.gm.modules.user.service.GmWhitelistPresaleService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 预售白名单
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-12-15 18:36:47
 */
@RestController
@RequestMapping("user/gmwhitelistpresale")
public class GmWhitelistPresaleController extends AbstractController{
    @Autowired
    private GmWhitelistPresaleService gmWhitelistPresaleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:gmwhitelistpresale:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmWhitelistPresaleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("user:gmwhitelistpresale:info")
    public R info(@PathVariable("id") Long id){
        GmWhitelistPresaleEntity gmWhitelistPresale = gmWhitelistPresaleService.getById(id);

        return R.ok().put("gmWhitelistPresale", gmWhitelistPresale);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:gmwhitelistpresale:save")
    public R save(@RequestBody GmWhitelistPresaleEntity gmWhitelistPresale){
        ValidatorUtils.validateEntity(gmWhitelistPresale);
        Date now = new Date();
        gmWhitelistPresale.setQuantityUsed(Constant.ZERO_I);
        gmWhitelistPresale.setCreateUser(getUserId());
        gmWhitelistPresale.setCreateTime(now);
        gmWhitelistPresale.setCreateTimeTs(now.getTime());
        gmWhitelistPresaleService.save(gmWhitelistPresale);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:gmwhitelistpresale:update")
    public R update(@RequestBody GmWhitelistPresaleEntity gmWhitelistPresale){
        ValidatorUtils.validateEntity(gmWhitelistPresale);
        Date now = new Date();
        gmWhitelistPresale.setUpdateUser(now.getTime());
        gmWhitelistPresale.setUpdateTime(now);
        gmWhitelistPresale.setUpdateTimeTs(now.getTime());
        gmWhitelistPresaleService.updateById(gmWhitelistPresale);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:gmwhitelistpresale:delete")
    public R delete(@RequestBody Long[] ids){
        gmWhitelistPresaleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
