/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.common.utils.ExpUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.EquipmentInfoEntity;
import com.gm.modules.basicconfig.entity.HeroEquipmentEntity;
import com.gm.modules.basicconfig.entity.HeroLevelEntity;
import com.gm.modules.basicconfig.entity.HeroSkillEntity;
import com.gm.modules.basicconfig.rsp.HeroSkillRsp;
import com.gm.modules.basicconfig.service.*;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.UserHeroInfoReq;
import com.gm.modules.user.rsp.*;
import com.gm.modules.user.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
    private HeroEquipmentService heroEquipmentService;
    @Autowired
    private EquipmentInfoService equipmentInfoService;
    @Autowired
    private EquipSynthesisItemService equipSynthesisItemService;
    @Autowired
    private UserHeroEquipmentWearService userHeroEquipmentWearService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private HeroLevelService heroLevelService;
    @Autowired
    private HeroSkillService heroSkillService;

    @Login
    @PostMapping("getUserHeroInfo")
    @ApiOperation("获取玩家英雄信息")
    public R getUserHeroInfo(@LoginUser UserEntity user) {
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("gmUserId", user.getUserId());
        List<UserHeroInfoRsp> heroList = userHeroService.getUserAllHero(userHeroMap);
        return R.ok().put("heroList",heroList);
    }

    @Login
    @PostMapping("getUserProps")
    @ApiOperation("获取玩家背包信息")
    public R getUserProps(@LoginUser UserEntity user) {
        Map<String, Object> map = new HashMap<>();
        // 获取玩家英雄信息和穿戴的装备信息
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("gmUserId", user.getUserId());
        List<UserHeroInfoRsp> heroList = userHeroService.getUserAllHero(userHeroMap);
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
        UserEquipmentEntity userEquipment = new UserEquipmentEntity();
        userEquipment.setStatus(Constant.enable);
        userEquipment.setGmUserId(user.getUserId());
        List<UserEquipInfoRsp> equipmentEntities = userEquipmentService.getUserEquip(userEquipment);
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
        UserAccountEntity userAccount = getUserAccount(user);
        if (userAccount == null){
            throw new RRException(ErrorCode.USER_GET_BAL_FAIL.getDesc());
        }
        BeanUtils.copyProperties(rsp,userAccount);
        return R.ok().put("userAccount",rsp);
    }

    /**
     * 获取用户账户余额
     * @param user
     * @return
     */
    private UserAccountEntity getUserAccount(UserEntity user){
        QueryWrapper<UserAccountEntity> wrapper = new QueryWrapper<UserAccountEntity>()
                .eq("USER_ID",user.getUserId());
        return userAccountService.getOne(wrapper);
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

        // 获取用户账户余额
        UserAccountEntity userAccount = getUserAccount(user);
        if (userAccount == null){
            throw new RRException(ErrorCode.USER_GET_BAL_FAIL.getDesc());
        }
        rsp.setPromotionExperience(userLevel.getPromotionExperience());
        rsp.setLevelCode(userLevel.getLevelCode());
        rsp.setFtgMax(Constant.FTG);
        rsp.setTotalAmount(userAccount.getTotalAmount());
        rsp.setCurrentExp(ExpUtils.getCurrentExp(userLevel.getExperienceTotal(), userLevel.getPromotionExperience(), user.getExperienceObtain()));
        return R.ok().put("userInfo",rsp);
    }


    @Login
    @PostMapping("getHeroDetail")
    @ApiOperation("获取英雄详细信息")
    public R getHeroDetail(@LoginUser UserEntity user,  @RequestBody UserHeroInfoReq req) throws InvocationTargetException, IllegalAccessException {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("gmUserHeroId", req.getGmUserHeroId());
        UserHeroEntity userHero = userHeroService.getUserHeroById(userHeroMap);
        if ( userHero == null ) {
            System.out.println("获取玩家英雄失败");
        }
        UserHeroInfoRsp rsp = new UserHeroInfoRsp();
        BeanUtils.copyProperties(rsp, userHero);
        // 获取英雄等级信息
        HeroLevelEntity heroLevel = heroLevelService.getById(userHero.getGmHeroLevelId());
        if (heroLevel == null){
            throw new RRException(ErrorCode.EXP_GET_FAIL.getDesc());
        }
        // 晋级到下一级所需经验值
        rsp.setPromotionExperience(heroLevel.getGmPromotionExperience());
        // 当前等级获取的经验值
        rsp.setCurrentExp(ExpUtils.getCurrentExp(heroLevel.getGmExperienceTotal(), heroLevel.getGmPromotionExperience(), user.getExperienceObtain()));
        // 获取系统全部装备
        Map<String, Object> equipMap = new HashMap<>();
        equipMap.put("STATUS", Constant.enable);
        List<EquipmentInfoEntity> equipments = equipmentInfoService.getEquipmentInfos(equipMap);
        // 获取英雄装备栏
        Map<String, Object> heroEquipMap = new HashMap<>();
        heroEquipMap.put("status",  Constant.enable);
        heroEquipMap.put("gmHeroId",  rsp.getGmHeroId());
        List<HeroEquipmentEntity> heroEquipments = heroEquipmentService.getHeroEquipments(heroEquipMap);
        JSONArray jsonArray = new JSONArray();
        for ( HeroEquipmentEntity heroEquipment : heroEquipments ) {
            // 获取英雄已激活/未激活装备
            jsonArray.add(equipmentInfoService.updateEquipJson2(heroEquipment.getGmEquipId(),equipments, rsp));

        }
        // 获取英雄技能
        Map<String, Object> skillMap = new HashMap<>();
        skillMap.put("status", Constant.enable);
        skillMap.put("gmHeroStarId", userHero.getGmHeroStarId());
        HeroSkillEntity heroSkill = heroSkillService.getHeroSkill(skillMap);
        if ( heroSkill == null ) {
            System.out.println("英雄技能获取失败");
        }
        HeroSkillRsp skillRsp = new HeroSkillRsp();
        BeanUtils.copyProperties(skillRsp,heroSkill);

        rsp.setHeroSkillRsp(skillRsp);

        Map<String, Object> map = new HashMap();
        map.put("heroInfo", rsp);
        map.put("equipments", jsonArray);
        return R.ok().put("data",map);
    }

}
