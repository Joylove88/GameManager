package com.gm.modules.basicconfig.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gm.common.annotation.SysLog;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.basicconfig.entity.StarInfoEntity;
import com.gm.modules.basicconfig.service.StarInfoService;


/**
 * 
 * 英雄星级配置
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-25 14:59:05
 */
@RestController
@RequestMapping("basicconfig/starinfo")
public class StarInfoController extends AbstractController{
    @Autowired
    private StarInfoService starInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:starinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = starInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gmStarId}")
    @RequiresPermissions("basicconfig:starinfo:info")
    public R info(@PathVariable("gmStarId") Long gmStarId){
        StarInfoEntity starInfo = starInfoService.getById(gmStarId);

        return R.ok().put("starInfo", starInfo);
    }

    /**
     * 保存
     */
    @SysLog("新增星级配置")
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:starinfo:save")
    public R save(@RequestBody StarInfoEntity starInfo){
        //校验
        ValidatorUtils.validateEntity(starInfo);
        Date now = new Date();
        starInfo.setCreateUser(getUserCode());
        starInfo.setCreateTime(now);
        starInfo.setCreateTimeTs(now.getTime());

        starInfoService.save(starInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改星级配置")
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:starinfo:update")
    public R update(@RequestBody StarInfoEntity starInfo){
        ValidatorUtils.validateEntity(starInfo);
        Date now = new Date();
        starInfo.setUpdateUser(getUserCode());
        starInfo.setUpdateTime(now);
        starInfo.setUpdateTimeTs(now.getTime());
        starInfoService.updateById(starInfo);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除星级配置")
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:starinfo:delete")
    public R delete(@RequestBody Long[] gmStarIds){
        starInfoService.removeByIds(Arrays.asList(gmStarIds));

        return R.ok();
    }

    /**
     * 获取星级
     */
    @RequestMapping("/getStarInfoList")
    @RequiresPermissions("basicconfig:starinfo:list")
    public R getStarInfoList(){
        List<StarInfoEntity> list = starInfoService.list();
        return R.ok().put("list", list);
    }

}
