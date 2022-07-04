/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.ExpUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.UseExpReq;
import com.gm.modules.user.rsp.*;
import com.gm.modules.user.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息接口
 *
 * @author Mark axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags="用户信息接口")
public class ApiUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserHeroService userHeroService;
    @Autowired
    private UserTokenService tokenService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserHeroFragService userHeroFragService;
    @Autowired
    private UserEquipmentService userEquipmentService;
    @Autowired
    private UserEquipmentFragService userEquipmentFragService;
    @Autowired
    private UserExperiencePotionService userExService;
    @Autowired
    private UserHeroEquipmentWearService userHeroEquipmentWearService;
    @Autowired
    private UserLevelService userLevelService;

    @Login
    @PostMapping("getUserHeroInfo")
    @ApiOperation("获取玩家英雄信息")
    public R getUserHeroInfo(@LoginUser UserEntity user){
        List<UserHeroInfoRsp> heroList = userHeroService.getUserAllHero(user.getUserId());
        return R.ok().put("heroList",heroList);
    }

    @Login
    @PostMapping("getUserProps")
    @ApiOperation("获取玩家背包信息")
    public R getUserProps(@LoginUser UserEntity user){
        Map<String, Object> map = new HashMap<>();
        // 获取玩家英雄信息和穿戴的装备信息
        List<UserHeroInfoRsp> heroList = userHeroService.getUserAllHero(user.getUserId());
        int i = 0;
        while (i < heroList.size()) {
            // 获取该英雄已穿戴的装备
            List<UserHeroEquipmentWearRsp> wearList = userHeroEquipmentWearService.getUserWearEQ(heroList.get(i).getGmUserHeroId());
            if (wearList.size() > 0) {
                heroList.get(i).setWearEQList(wearList);
            }
            i++;
        }
        map.put("heroList",heroList);
        // 获取英雄碎片
        List<UserHeroFragInfoRsp> heroFragList = userHeroFragService.getUserAllHeroFrag(user.getUserId());
        map.put("heroFragList",heroFragList);
        // 获取装备
        List<UserEquipInfoRsp> equipmentEntities = userEquipmentService.getUserAllEquip(user.getUserId());
        map.put("equipList",equipmentEntities);
        // 获取装备碎片
        List<UserEquipmentFragInfoRsp> equipmentFragEntities = userEquipmentFragService.getUserAllEquipFrag(user.getUserId());
        map.put("equipFragList",equipmentFragEntities);

        // 获取消耗品
        Map<String, Object> forUseMap = new HashMap<>();

        // 获取经验道具
        UserExperiencePotionEntity exp = new UserExperiencePotionEntity();
        exp.setGmUserId(user.getUserId());
        List<UserExpInfoRsp> expList = userExService.getUserEx(exp);
        forUseMap.put("expList",expList);

        map.put("consumables",forUseMap);
        return R.ok(map);
    }

    @Login
    @PostMapping("getUserBalance")
    @ApiOperation("获取玩家余额")
    public R getUserBalance(@LoginUser UserEntity user) throws InvocationTargetException, IllegalAccessException {
        UserQueryBalanceRsp rsp = new UserQueryBalanceRsp();
        // 获取用户账户余额
        QueryWrapper<UserAccountEntity> wrapper = new QueryWrapper<UserAccountEntity>()
                .eq("USER_ID",user.getUserId());
        UserAccountEntity userAccount = userAccountService.getOne(wrapper);
        if (userAccount == null){
            throw new RRException(ErrorCode.USER_GET_BAL_FAIL.getDesc());
        }
        BeanUtils.copyProperties(rsp,userAccount);
        return R.ok().put("userAccount",rsp);
    }

    @Login
    @PostMapping("getUserInfo")
    @ApiOperation("获取玩家信息")
    public R getUserTotalPower(@LoginUser UserEntity user) throws InvocationTargetException, IllegalAccessException {
        UserInfoRsp rsp = new UserInfoRsp();
        // 获取玩家等级信息
        UserLevelEntity userLevel = userLevelService.getById(user.getUserLevelId());
        if (userLevel == null){
            throw new RRException(ErrorCode.EXP_GET_FAIL.getDesc());
        }
        BeanUtils.copyProperties(rsp,user);
        rsp.setPromotionExperience(userLevel.getPromotionExperience());
        rsp.setLevelCode(userLevel.getLevelCode());
        rsp.setCurrentExp(ExpUtils.getCurrentExp(userLevel.getExperienceTotal(), userLevel.getPromotionExperience(), user.getExperienceObtain()));
        return R.ok().put("userInfo",rsp);
    }


    @Login
    @PostMapping("playerUseExpForHero")
    @ApiOperation("玩家对英雄使用经验药水功能")
    public R playerUseExpForHero(@LoginUser UserEntity user, @RequestBody UseExpReq useExpReq) {
        // 表单校验
        ValidatorUtils.validateEntity(useExpReq);
        // 经验药水数量
        if (useExpReq.getExpNum() != null){
        } else {
            throw new RRException(ErrorCode.EXP_NUM_NOT_NULL.getDesc());
        }
        // 药水稀有度
        if (StringUtils.isNotBlank(useExpReq.getExpRare())){
            // 参数安全校验 防止注入攻击;
            if(ValidatorUtils.securityVerify(useExpReq.getExpRare())){
                throw new RRException(ErrorCode.EXP_RARE_NOT_NULL.getDesc());
            }
        } else {
            throw new RRException(ErrorCode.EXP_RARE_NOT_NULL.getDesc());
        }
        UserExperiencePotionEntity userEXP = new UserExperiencePotionEntity();
        userEXP.setGmUserId(user.getUserId());
        userEXP.setExPotionRareCode(useExpReq.getExpRare());
        userEXP.setUserExNum(useExpReq.getExpNum());
        userEXP.setGmUserHeroId(userEXP.getGmUserHeroId());
        userExService.userHeroUseEx(userEXP);
        return R.ok();
    }
}
