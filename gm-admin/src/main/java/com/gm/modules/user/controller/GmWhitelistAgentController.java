package com.gm.modules.user.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.user.entity.GmWhitelistAgentEntity;
import com.gm.modules.user.service.GmWhitelistAgentService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 代理白名单
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-12-15 18:36:47
 */
@RestController
@RequestMapping("user/gmwhitelistagent")
public class GmWhitelistAgentController extends AbstractController{
    @Autowired
    private GmWhitelistAgentService gmWhitelistAgentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:gmwhitelistagent:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = gmWhitelistAgentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("user:gmwhitelistagent:info")
    public R info(@PathVariable("id") Long id){
        GmWhitelistAgentEntity gmWhitelistAgent = gmWhitelistAgentService.getById(id);

        return R.ok().put("gmWhitelistAgent", gmWhitelistAgent);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:gmwhitelistagent:save")
    public R save(@RequestBody GmWhitelistAgentEntity gmWhitelistAgent){
        ValidatorUtils.validateEntity(gmWhitelistAgent);
        Date now = new Date();
        gmWhitelistAgent.setCreateUser(getUserId());
        gmWhitelistAgent.setCreateTime(now);
        gmWhitelistAgent.setCreateTimeTs(now.getTime());
        gmWhitelistAgentService.save(gmWhitelistAgent);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:gmwhitelistagent:update")
    public R update(@RequestBody GmWhitelistAgentEntity gmWhitelistAgent){
        ValidatorUtils.validateEntity(gmWhitelistAgent);
        Date now = new Date();
        gmWhitelistAgent.setUpdateUser(now.getTime());
        gmWhitelistAgent.setUpdateTime(now);
        gmWhitelistAgent.setUpdateTimeTs(now.getTime());
        gmWhitelistAgentService.updateById(gmWhitelistAgent);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:gmwhitelistagent:delete")
    public R delete(@RequestBody Long[] ids){
        gmWhitelistAgentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
