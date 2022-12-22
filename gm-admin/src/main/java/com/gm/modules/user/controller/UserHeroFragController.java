package com.gm.modules.user.controller;

import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.dto.SummonedEventDto;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroFragEntity;
import com.gm.modules.user.req.SummonReq;
import com.gm.modules.user.service.UserHeroFragService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员拥有的英雄碎片
 *
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
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = userHeroFragService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{userHeroFragId}")
    @RequiresPermissions("user:userherofrag:info")
    public R info(@PathVariable("userHeroFragId") Long userHeroFragId) {
        UserHeroFragEntity userHeroFrag = userHeroFragService.getById(userHeroFragId);

        return R.ok().put("userHeroFrag", userHeroFrag);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("user:userherofrag:save")
    public R save(@RequestBody UserHeroFragEntity userHeroFrag) {
        userHeroFragService.save(userHeroFrag);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("user:userherofrag:update")
    public R update(@RequestBody UserHeroFragEntity userHeroFrag) {
        ValidatorUtils.validateEntity(userHeroFrag);
        userHeroFragService.updateById(userHeroFrag);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("user:userherofrag:delete")
    public R delete(@RequestBody Long[] userHeroFragIds) {
        userHeroFragService.removeByIds(Arrays.asList(userHeroFragIds));

        return R.ok();
    }

    /**
     * 英雄模拟抽奖
     */
    @RequestMapping("/testDrawStart")
    public R testDrawStart(@RequestBody Integer summonNum) throws Exception {
        //开始抽奖
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1508401841157644289L);
        SummonReq summonReq = new SummonReq();
        summonReq.setTransactionHash("0x332bc39ef2149dccd759bd25df180e8ff035bfdf732aa9812c05235f7c65d4df");
        summonReq.setSummonNum(summonNum);
        summonReq.setSummonType("1");
        // 获取预售白名单折扣率后的信息
        SummonedEventDto summonedEventDto = drawGiftService.getSummonedPrice(userEntity.getAddress());
        drawGiftService.startSummon(userEntity, summonReq, summonedEventDto);
        return R.ok();
    }

    /**
     * 装备模拟抽奖
     */
    @RequestMapping("/testEQDrawStart")
    public R testEQDrawStart(@RequestBody Integer summonNum) throws Exception {
        //开始抽奖
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1508401841157644289L);
        SummonReq summonReq = new SummonReq();
        summonReq.setTransactionHash("0x332bc39ef2149dccd759bd25df180e8ff035bfdf732aa9812c05235f7c65d4df");
        summonReq.setSummonNum(summonNum);
        // 获取预售白名单折扣率后的信息
        SummonedEventDto summonedEventDto = drawGiftService.getSummonedPrice(userEntity.getAddress());
        drawGiftService.startSummon(userEntity, summonReq, summonedEventDto);
        return R.ok();
    }

    /**
     * 经验药水模拟抽奖
     */
    @RequestMapping("/testEXDrawStart")
    public R testEXDrawStart(@RequestBody Integer summonNum) throws Exception {
        //开始抽奖
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1508401841157644289L);
        SummonReq summonReq = new SummonReq();
        summonReq.setTransactionHash("0x332bc39ef2149dccd759bd25df180e8ff035bfdf732aa9812c05235f7c65d4df");
        summonReq.setSummonNum(summonNum);
        // 获取预售白名单折扣率后的信息
        SummonedEventDto summonedEventDto = drawGiftService.getSummonedPrice(userEntity.getAddress());
        drawGiftService.startSummon(userEntity, summonReq, summonedEventDto);
        return R.ok();
    }

}
