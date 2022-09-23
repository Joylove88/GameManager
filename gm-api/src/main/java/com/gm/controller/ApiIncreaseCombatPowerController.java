/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;
import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;
import com.gm.modules.basicconfig.entity.HeroStarEntity;
import com.gm.modules.basicconfig.entity.StarInfoEntity;
import com.gm.modules.basicconfig.rsp.TeamInfoRsp;
import com.gm.modules.basicconfig.service.*;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.UseExpReq;
import com.gm.modules.user.req.UserHeroInfoReq;
import com.gm.modules.user.rsp.UserEquipInfoRsp;
import com.gm.modules.user.rsp.UserHeroEquipmentWearRsp;
import com.gm.modules.user.rsp.UserHeroFragInfoRsp;
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
    @Autowired
    private StarInfoService starInfoService;
    @Autowired
    private HeroStarService heroStarService;
    @Autowired
    private CombatStatsUtilsService combatStatsUtilsService;

    @Login
    @PostMapping("equipment")
    @ApiOperation("英雄装备穿戴or激活")
    public R equipment(@LoginUser UserEntity user,  @RequestBody UserHeroInfoReq req) throws InvocationTargetException, IllegalAccessException {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄
        UserHeroEntity userHero = getUserHero(req);
        UserHeroInfoRsp rsp = new UserHeroInfoRsp();
        BeanUtils.copyProperties(rsp,userHero);
        // 获取玩家装备信息
        UserEquipmentEntity userEquipment = new UserEquipmentEntity();
        userEquipment.setGmUserId(user.getUserId());
        userEquipment.setGmUserEquipmentId(req.getGmUserEquipmentId());
        List<UserEquipInfoRsp> equipment = userEquipmentService.getUserEquip(userEquipment);
        if ( equipment.size() == 0 ) {
            throw new RRException("您未获得该装备或已激活");
        }
        for ( UserEquipInfoRsp equipInfoRsp : equipment ) {
            if ( equipInfoRsp.getStatus().equals(Constant.used) ) {
                throw new RRException("该装备已使用");
            }
        }

        // ===============校验子级装备是否已激活START===============
        // 获取该装备的合成公式
        EquipSynthesisItemEntity eqSIEs = equipSynthesisItemService.getEquipSyntheticFormula(equipment.get(0).getGmEquipmentId());
        // 如果合成公式不为空说明为可合成装备 只有可合成装备才进行校验
        if ( eqSIEs != null ) {
            // 封装该装备合成公式
            List list = combatStatsUtilsService.getEquipItems(eqSIEs);
            // 该合成公式所需的装备数量
            int equipNum = 0;
            int i = 0;
            while ( i < list.size() ){
                // 获取合成公式是否包含多件装备
                boolean b = list.get(i).toString().contains(",");
                // 多件装备
                if( b ){
                    String[] equipItems2 = list.get(i).toString().split(",");
                    for( String e1 : equipItems2 ){
                        equipNum++;
                    }
                } else {// 单件装备
                    equipNum++;
                }
                i++;
            }
            // 获取该英雄已穿戴的装备
            Map<String, Object> userWearMap = new HashMap<>();
            userWearMap.put("status", Constant.enable);
            userWearMap.put("userHeroId", req.getGmUserHeroId());
            userWearMap.put("parentEquipChain", req.getParentEquipChain());
            List<UserHeroEquipmentWearRsp> wearList = userHeroEquipmentWearService.getUserWearEQ(userWearMap);
            for ( UserHeroEquipmentWearRsp equipmentWearRsp : wearList ) {
                if ( equipmentWearRsp.getGmUserEquipId().equals(req.getGmUserEquipmentId()) ) {
                    throw new RRException("该装备已使用");
                }
            }
            // 开始校验：该装备合成公式的下级装备数量 是否与已激活装备数量相同 如果相同则校验通过
            if ( equipNum != wearList.size() ){
                throw new RRException("请先激活下层全部装备");
            }
        }

        // ===============校验子级装备是否已激活END===============


        // 装备战力
        long equpPower = 0;
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
        userHeroEquipmentWear.setGmHeroId(rsp.getGmHeroId());
        userHeroEquipmentWear.setGmUserHeroId(req.getGmUserHeroId());
        userHeroEquipmentWear.setGmUserEquipId(req.getGmUserEquipmentId());
        userHeroEquipmentWear.setParentEquipChain(req.getParentEquipChain());
        userHeroEquipmentWear.setStatus(Constant.enable);
        userHeroEquipmentWear.setGmUserId(user.getUserId());
        userHeroEquipmentWear.setCreateUser(user.getUserId());
        userHeroEquipmentWear.setCreateTime(now);
        userHeroEquipmentWear.setCreateTimeTs(now.getTime());
        userHeroEquipmentWear.setUpdateUser(user.getUserId());
        userHeroEquipmentWear.setUpdateTime(now);
        userHeroEquipmentWear.setUpdateTimeTs(now.getTime());
        userHeroEquipmentWearService.save(userHeroEquipmentWear);

        // 获取该装备战力（本次变化的战力）
        equpPower = equipment.get(0).getEquipPower();
        updateCombatPower(user, req.getGmUserHeroId(), equpPower);

        Map<String, Object> map = new HashMap();
        map.put("changePower", equpPower);
        return R.ok().put("data",map);
    }

    /**
     * 装备合成
     * @param user
     * @param req
     */
    private void equipmentSynthesis(@LoginUser UserEntity user,  @RequestBody UserHeroInfoReq req){
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄
        UserHeroEntity userHero = getUserHero(req);


    }

    /**
     * 校验英雄
     * @param req
     * @return
     */
    private UserHeroEntity getUserHero(UserHeroInfoReq req){
        // 获取英雄
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("gmUserHeroId", req.getGmUserHeroId());
        UserHeroEntity userHero = userHeroService.getUserHeroById(userHeroMap);
        if ( userHero == null ) {
            System.out.println("获取玩家英雄失败");
        }
        return userHero;
    }

    /**
     * 更新战力(判断该英雄是否在上阵中, 只有上阵中的英雄更新战力及矿工)
     * @param user
     * @param userHeroId
     * @param changePower
     */
    private void updateCombatPower(UserEntity user, Long userHeroId, Long changePower){
        // 获取队伍
        Map<String, Object> teamParams = new HashMap<>();
        teamParams.put("userId", user.getUserId());
        teamParams.put("gmUserHeroId", userHeroId);
        List<TeamInfoRsp> teamInfoRsps = teamConfigService.getTeamInfoList(teamParams);

        // 英雄已在队伍上阵
        if ( teamInfoRsps.size() > 0 ) {
            for (TeamInfoRsp teamInfoRsp : teamInfoRsps){
                // 获取本次操作之前的战力值
                long oldPower = teamInfoRsp.getTeamPower();
                // 获取最新队伍战力（队伍战力+本次操作改变的战力）
                long newPower = teamInfoRsp.getTeamPower() + changePower;
                GmTeamConfigEntity team = new GmTeamConfigEntity();
                team.setId(teamInfoRsp.getId());
                fightCoreService.updateCombat(changePower, oldPower, newPower, user, team);
            }
        }
    }

    @Login
    @PostMapping("upgradeStar")
    @ApiOperation("英雄升星")
    public R upgradeStar(@LoginUser UserEntity user, @RequestBody UserHeroInfoReq req) {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄
        UserHeroEntity userHero = getUserHero(req);
        // 获取玩家背包中的英雄碎片
        Map<String, Object> heroFragMap = new HashMap<>();
        heroFragMap.put("gmUserId", user.getUserId());
        heroFragMap.put("status", Constant.enable);
        heroFragMap.put("heroId", userHero.getGmHeroId());
        List<UserHeroFragInfoRsp> heroFragList = userHeroFragService.getUserAllHeroFrag(heroFragMap);
        // 获取当前星级+1
        Long starCode = userHero.getGmStarCode();
        if ( starCode < 5 ) {
            starCode += 1;
        } else {
            throw new RRException("星级已满");
        }
        // 获取当前阶段升星所需碎片
        StarInfoEntity starInfo = starInfoService.getOne(new QueryWrapper<StarInfoEntity>()
                .eq("GM_STAR_CODE", starCode)

        );
        // 本次增加的战力
        long changePower = 0;
        // 当前星级英雄战力
        long nowPower = 0;
        // 升级后的星级英雄战力
        long upPower = 0;
        // 先校验玩家背包英雄碎片是否足够本次升星操作
        if ( heroFragList.get(0).getHeroFragNum() >= starInfo.getUpStarFragNum() ) {
            try {
                // 获取当前星级英雄属性
                Map<String, Object> heroStarMap = new HashMap<>();
                heroStarMap.put("status", Constant.enable);
                heroStarMap.put("heroId", userHero.getGmHeroId());
                // 当前星级以及升级星级
                Long[] starCodes = {userHero.getGmStarCode(), starCode};
                heroStarMap.put("starCodes", starCodes);
                List<HeroStarEntity> heroStars = heroStarService.getRangeHeroStars(heroStarMap);
                if ( heroStars.size() < 2) {
                    throw new RRException("获取星级范围英雄失败");
                }
                // 获取当前星级英雄战力
                if (userHero.getGmStarCode().equals(heroStars.get(0).getGmStarCode())) {
                    nowPower = combatStatsUtilsService.getHeroPower(heroStars.get(0).getGmHealth(), heroStars.get(0).getGmMana(), heroStars.get(0).getGmHealthRegen(),
                            heroStars.get(0).getGmManaRegen(), heroStars.get(0).getGmArmor(), heroStars.get(0).getGmMagicResist(), heroStars.get(0).getGmAttackDamage(),
                            heroStars.get(0).getGmAttackSpell(), heroStars.get(0).getScale());
                }
                // 获取升级后的星级英雄战力
                if (starCode.equals(heroStars.get(1).getGmStarCode())) {
                    upPower = combatStatsUtilsService.getHeroPower(heroStars.get(1).getGmHealth(), heroStars.get(1).getGmMana(), heroStars.get(1).getGmHealthRegen(),
                            heroStars.get(1).getGmManaRegen(), heroStars.get(1).getGmArmor(), heroStars.get(1).getGmMagicResist(), heroStars.get(1).getGmAttackDamage(),
                            heroStars.get(1).getGmAttackSpell(), heroStars.get(1).getScale());
                }

                // 获取本次增加的战力
                changePower = upPower - nowPower;
                // 消耗英雄碎片
                heroFragMap.put("heroFragNum", starInfo.getUpStarFragNum());
                userHeroFragService.depleteHeroFrag(heroFragMap);
                // 英雄升星\更新英雄战力
                Date now = new Date();
                UserHeroEntity userHeroUp = new UserHeroEntity();
                userHeroUp.setGmUserHeroId(req.getGmUserHeroId());
                userHeroUp.setGmHeroStarId(heroStars.get(1).getGmHeroStarId());
                userHeroUp.setHeroPower(userHero.getHeroPower() + changePower);
                userHeroUp.setUpdateTime(now);
                userHeroUp.setUpdateTimeTs(now.getTime());
                userHeroService.updateById(userHeroUp);
                // 更新战力及矿工
                updateCombatPower(user, req.getGmUserHeroId(), changePower);
            } catch (RRException e) {
                e.printStackTrace();
                throw new RRException("升星失败:"+e.getMsg());
            }

        } else {
            throw new RRException("英雄碎片不足");
        }
        Map<String, Object> map = new HashMap();
        map.put("changePower", changePower);
        return R.ok().put("data",map);
    }

    @Login
    @PostMapping("eatExpForHero")
    @ApiOperation("使用经验药水")
    public R eatExpForHero(@LoginUser UserEntity user, @RequestBody UseExpReq useExpReq) {
        // 表单校验
        ValidatorUtils.validateEntity(useExpReq);
        // 经验药水数量
        if (useExpReq.getExpNum() == null && useExpReq.getExpNum() == 0){
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
        userExService.userHeroUseEx(user, useExpReq);
        return R.ok();
    }
}
