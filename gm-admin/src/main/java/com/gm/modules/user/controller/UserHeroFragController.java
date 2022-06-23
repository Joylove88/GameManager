package com.gm.modules.user.controller;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroFragEntity;
import com.gm.modules.user.service.UserHeroFragService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 
 * 会员拥有的英雄碎片
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-06 18:25:15
 */
@RestController
@RequestMapping("user/userherofrag")
public class UserHeroFragController {
    @Autowired
    private UserHeroFragService userHeroFragService;
    @Autowired
    private DrawGiftService drawGiftService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:userherofrag:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userHeroFragService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gmUserHeroFragId}")
    @RequiresPermissions("user:userherofrag:info")
    public R info(@PathVariable("gmUserHeroFragId") Long gmUserHeroFragId){
        UserHeroFragEntity userHeroFrag = userHeroFragService.getById(gmUserHeroFragId);

        return R.ok().put("userHeroFrag", userHeroFrag);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userherofrag:save")
    public R save(@RequestBody UserHeroFragEntity userHeroFrag){
        userHeroFragService.save(userHeroFrag);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userherofrag:update")
    public R update(@RequestBody UserHeroFragEntity userHeroFrag){
        ValidatorUtils.validateEntity(userHeroFrag);
        userHeroFragService.updateById(userHeroFrag);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userherofrag:delete")
    public R delete(@RequestBody Long[] gmUserHeroFragIds){
        userHeroFragService.removeByIds(Arrays.asList(gmUserHeroFragIds));

        return R.ok();
    }

    /**
     * 英雄模拟抽奖
     */
    @RequestMapping("/testDrawStart")
    public R testDrawStart(@RequestBody Long drawType) throws Exception {
        //开始抽奖
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        DrawForm drawForm = new DrawForm();
        drawForm.setTransactionHash("0x332bc39ef2149dccd759bd25df180e8ff035bfdf732aa9812c05235f7c65d4df");
        drawForm.setDrawType(drawType.toString());
        drawForm.setItemType("1");
        List s = drawGiftService.heroDrawStart(userEntity, drawForm);
        return R.ok().put("s",s);
    }

    /**
     * 装备模拟抽奖
     */
    @RequestMapping("/testEQDrawStart")
    public R testEQDrawStart(@RequestBody Long drawType)throws Exception{
        //开始抽奖
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        DrawForm drawForm = new DrawForm();
        drawForm.setTransactionHash("0x332bc39ef2149dccd759bd25df180e8ff035bfdf732aa9812c05235f7c65d4df");
        drawForm.setDrawType(drawType.toString());
        List s = drawGiftService.equipDrawStart(userEntity,drawForm);
        return R.ok().put("s",s);
    }

    /**
     * 经验药水模拟抽奖
     */
    @RequestMapping("/testEXDrawStart")
    public R testEXDrawStart(@RequestBody Long drawType){
        //开始抽奖
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        DrawForm drawForm = new DrawForm();
        drawForm.setTransactionHash("0x332bc39ef2149dccd759bd25df180e8ff035bfdf732aa9812c05235f7c65d4df");
        drawForm.setDrawType(drawType.toString());
        List s = drawGiftService.exDrawStart(userEntity,drawForm);
        return R.ok().put("s",s);
    }

}
