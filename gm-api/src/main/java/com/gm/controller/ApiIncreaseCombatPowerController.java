/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;
import com.gm.modules.basicconfig.rsp.TeamInfoRsp;
import com.gm.modules.basicconfig.service.EquipSynthesisItemService;
import com.gm.modules.basicconfig.service.EquipmentInfoService;
import com.gm.modules.basicconfig.service.GmTeamConfigService;
import com.gm.modules.basicconfig.service.HeroEquipmentService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.UseExpReq;
import com.gm.modules.user.req.UserHeroInfoReq;
import com.gm.modules.user.rsp.UserEquipInfoRsp;
import com.gm.modules.user.rsp.UserHeroInfoRsp;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 战力提升接口
 *
 * @author Mark axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags="用户信息接口")
public class ApiIncreaseCombatPowerController {
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
    private GmTeamConfigService teamConfigService;
    @Autowired
    private FightCoreService fightCoreService;

    @Login
    @PostMapping("equipment")
    @ApiOperation("英雄装备穿戴or激活")
    public R equipment(@LoginUser UserEntity user,  @RequestBody UserHeroInfoReq req) throws InvocationTargetException, IllegalAccessException {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("gmUserHeroId", req.getGmUserHeroId());
        UserHeroEntity userHero = userHeroService.getUserHeroById(userHeroMap);
        if ( userHero == null ) {
            System.out.println("获取玩家英雄失败");
        }
        UserHeroInfoRsp rsp = new UserHeroInfoRsp();
        BeanUtils.copyProperties(rsp,userHero);
        // 获取装备
        UserEquipmentEntity userEquipment = new UserEquipmentEntity();
        userEquipment.setStatus(Constant.enable);
        userEquipment.setGmUserId(user.getUserId());
        userEquipment.setGmUserEquipmentId(req.getGmUserEquipmentId());
        List<UserEquipInfoRsp> equipmentEntities = userEquipmentService.getUserEquip(userEquipment);
        if ( equipmentEntities.size() == 0 ) {
            System.out.println("该装备不属于您");
        }
        // 装备战力
        long equpPower = 0;
        int i = 0;
        while ( i < equipmentEntities.size() ){
            Date now = new Date();
            // 更新玩家装备状态为已穿戴/激活
            UserEquipmentEntity setUserEquip = new UserEquipmentEntity();
            setUserEquip.setGmUserEquipmentId(req.getGmUserEquipmentId());
            setUserEquip.setStatus(Constant.used);
            setUserEquip.setGmUserId(user.getUserId());
            setUserEquip.setUpdateTime(now);
            setUserEquip.setUpdateTimeTs(now.getTime());
            userEquipmentService.updateById(setUserEquip);
            // 穿戴表新增一条装备穿戴记录
            UserHeroEquipmentWearEntity userHeroEquipmentWear = new UserHeroEquipmentWearEntity();
            userHeroEquipmentWear.setGmUserId(user.getUserId());
            userHeroEquipmentWear.setGmHeroId(rsp.getGmHeroId());
            userHeroEquipmentWear.setGmUserHeroId(req.getGmUserHeroId());
            userHeroEquipmentWear.setGmUserEquipId(req.getGmUserEquipmentId());
            userHeroEquipmentWear.setParentEquipChain(req.getParentEquipChain());
            userHeroEquipmentWear.setStatus(Constant.enable);
            userHeroEquipmentWear.setCreateUser(user.getUserId());
            userHeroEquipmentWear.setCreateTime(now);
            userHeroEquipmentWear.setCreateTimeTs(now.getTime());
            userHeroEquipmentWear.setUpdateUser(user.getUserId());
            userHeroEquipmentWear.setUpdateTime(now);
            userHeroEquipmentWear.setUpdateTimeTs(now.getTime());
            userHeroEquipmentWearService.save(userHeroEquipmentWear);

            // 更新战力(判断该英雄是否在上阵中, 只有上阵中的英雄更新战力及矿工)
            Map<String, Object> teamParams = new HashMap<>();
            teamParams.put("userId", user.getUserId());
            teamParams.put("gmUserHeroId", req.getGmUserHeroId());
            List<TeamInfoRsp> teamInfoRsps = teamConfigService.getTeamInfoList(teamParams);

            // 获取该装备战力
            equpPower = equipmentEntities.get(i).getEquipPower();

            // 英雄已在队伍上阵
            if ( teamInfoRsps.size() > 0 ) {
                for (TeamInfoRsp teamInfoRsp : teamInfoRsps){
                    // 获取本次操作之前的战力值
                    long oldPower = teamInfoRsp.getTeamPower();
                    // 获取最新队伍战力（队伍战力+本次操作改变的战力）
                    long newPower = teamInfoRsp.getTeamPower() + equpPower;
                    GmTeamConfigEntity team = new GmTeamConfigEntity();
                    team.setId(teamInfoRsp.getId());
                    fightCoreService.updateCombat(equpPower, oldPower, newPower, user, team);
                }
            }
            i++;
        }

        Map<String, Object> map = new HashMap();
        map.put("changePower", equpPower);
        return R.ok().put("data",map);
    }

    @Login
    @PostMapping("eatExpForHero")
    @ApiOperation("玩家对英雄使用经验药水功能")
    public R eatExpForHero(@LoginUser UserEntity user, @RequestBody UseExpReq useExpReq) {
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
