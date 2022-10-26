package com.gm.modules.basicconfig.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.ProbabilityEntity;
import com.gm.modules.basicconfig.service.ProbabilityService;
import com.gm.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




/**
 * 抽奖概率配置
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-08 16:26:15
 */
@RestController
@RequestMapping("basicconfig/probability")
public class ProbabilityController extends AbstractController{
    @Autowired
    private ProbabilityService probabilityService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basicconfig:probability:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = probabilityService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{probabilityId}")
    @RequiresPermissions("basicconfig:probability:info")
    public R info(@PathVariable("probabilityId") Long probabilityId){
        ProbabilityEntity probability = probabilityService.getById(probabilityId);

        return R.ok().put("probability", probability);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basicconfig:probability:save")
    public R save(@RequestBody ProbabilityEntity probability){
        Date now = new Date();
        probability.setCreateUser(getUserId());
        probability.setCreateTime(now);
        probability.setCreateTimeTs(now.getTime());
        probabilityService.save(probability);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basicconfig:probability:update")
    public R update(@RequestBody ProbabilityEntity probability){
        ValidatorUtils.validateEntity(probability);
        Date now = new Date();
        probability.setUpdateUser(getUserId());
        probability.setUpdateTime(now);
        probability.setUpdateTimeTs(now.getTime());
        probabilityService.updateById(probability);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basicconfig:probability:delete")
    public R delete(@RequestBody Long[] probabilityIds){
        probabilityService.removeByIds(Arrays.asList(probabilityIds));

        return R.ok();
    }

}
