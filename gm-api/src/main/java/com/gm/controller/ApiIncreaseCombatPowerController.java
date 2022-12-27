/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Arith;
import com.gm.common.utils.CalculateTradeUtil;
import com.gm.common.utils.Constant;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.dto.AttributeSimpleEntity;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.basicconfig.rsp.HeroLevelRsp;
import com.gm.modules.basicconfig.rsp.TeamInfoRsp;
import com.gm.modules.basicconfig.service.*;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.UseExpReq;
import com.gm.modules.user.req.UserHeroInfoReq;
import com.gm.modules.user.rsp.*;
import com.gm.modules.user.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 战力提升接口
 *
 * @author Mark axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags = "用户信息接口")
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
    private HeroLevelService heroLevelService;
    @Autowired
    private HeroInfoService heroInfoService;
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
    @Autowired
    private ExperiencePotionService experiencePotionService;

    @Login
    @PostMapping("equipment")
    @ApiOperation("英雄装备激活OR合成")
    @Transactional(rollbackFor = Exception.class)
    public R equipment(@LoginUser UserEntity user, @RequestBody UserHeroInfoReq req) throws InvocationTargetException, IllegalAccessException {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄
        UserHeroEntity userHero = getUserHero(req.getUserHeroId());
        UserHeroInfoRsp rsp = new UserHeroInfoRsp();
        BeanUtils.copyProperties(rsp, userHero);
        // 获取玩家装备信息
        UserEquipmentEntity userEquipment = new UserEquipmentEntity();
        userEquipment.setUserId(user.getUserId());
        userEquipment.setUserEquipmentId(req.getUserEquipmentId());
        List<UserEquipInfoRsp> equipment = userEquipmentService.getUserEquip(userEquipment);
        if (equipment.size() == 0) {
            throw new RRException("You do not have this equipment or it has been activated!");
        }
        for (UserEquipInfoRsp equipInfoRsp : equipment) {
            if (equipInfoRsp.getStatus().equals(Constant.used)) {
                throw new RRException("This equipment has been used!");
            }
        }

        // ===============校验子级装备是否已激活START===============
        verifyEquipment(req, equipment.get(0).getEquipmentId());
        // ===============校验子级装备是否已激活END===============


        // 装备战力
        long equpPower = 0;
        Date now = new Date();
        // 更新玩家装备状态为已穿戴/激活
        UserEquipmentEntity setUserEquip = new UserEquipmentEntity();
        setUserEquip.setUserEquipmentId(req.getUserEquipmentId());
        setUserEquip.setStatus(Constant.used);
        setUserEquip.setUserId(user.getUserId());
        setUserEquip.setUpdateTime(now);
        setUserEquip.setUpdateTimeTs(now.getTime());
        userEquipmentService.updateById(setUserEquip);
        boolean update = userEquipmentService.update(setUserEquip, new UpdateWrapper<UserEquipmentEntity>()
                .eq("STATUS", Constant.enable)
                .eq("USER_EQUIPMENT_ID", req.getUserEquipmentId())
        );
        if(!update){
            throw new RRException("equipment activated!");
        }
        // 穿戴表新增一条装备穿戴记录
        UserHeroEquipmentWearEntity userHeroEquipmentWear = new UserHeroEquipmentWearEntity();
        userHeroEquipmentWear.setHeroId(rsp.getHeroId());
        userHeroEquipmentWear.setUserHeroId(req.getUserHeroId());
        userHeroEquipmentWear.setUserEquipId(req.getUserEquipmentId());
        userHeroEquipmentWear.setParentEquipChain(req.getParentEquipChain());
        userHeroEquipmentWear.setStatus(Constant.enable);
        userHeroEquipmentWear.setUserId(user.getUserId());
        userHeroEquipmentWear.setCreateUser(user.getUserId());
        userHeroEquipmentWear.setCreateTime(now);
        userHeroEquipmentWear.setCreateTimeTs(now.getTime());
        userHeroEquipmentWear.setUpdateUser(user.getUserId());
        userHeroEquipmentWear.setUpdateTime(now);
        userHeroEquipmentWear.setUpdateTimeTs(now.getTime());
        userHeroEquipmentWearService.save(userHeroEquipmentWear);

        // 获取该装备战力（本次变化的战力）
        equpPower = equipment.get(0).getEquipPower();
        updateCombatPower(user, req.getUserHeroId(), setUserEquip.getUserEquipmentId(), equpPower, null);

        Map<String, Object> map = new HashMap();
        map.put("changePower", equpPower);
        return R.ok().put("data", map);
    }

    /**
     * 校验子级装备是否已激活（子级装备激活后才可激活父级装备）
     *
     * @param req
     * @param equipmentId
     */
    private void verifyEquipment(UserHeroInfoReq req, Long equipmentId) {
        // 获取该装备的合成公式
        EquipSynthesisItemEntity eqSIEs = equipSynthesisItemService.getEquipSyntheticFormula(equipmentId);
        // 如果合成公式不为空说明为可合成装备 只有可合成装备才进行校验
        if (eqSIEs != null) {
            // 封装该装备合成公式
            List list = combatStatsUtilsService.getEquipItems(eqSIEs);
            // 该合成公式所需的装备数量
            int equipNum = 0;
            int i = 0;
            while (i < list.size()) {
                // 获取合成公式是否包含多件装备
                boolean b = list.get(i).toString().contains(",");
                // 多件装备
                if (b) {
                    String[] equipItems2 = list.get(i).toString().split(",");
                    for (String e1 : equipItems2) {
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
            userWearMap.put("userHeroId", req.getUserHeroId());
            userWearMap.put("parentEquipChain", req.getParentEquipChain());
            List<UserHeroEquipmentWearRsp> wearList = userHeroEquipmentWearService.getUserWearEQ(userWearMap);
            for (UserHeroEquipmentWearRsp equipmentWearRsp : wearList) {
                if (equipmentWearRsp.getUserEquipId().equals(req.getUserEquipmentId())) {
                    throw new RRException("该装备已使用");
                }
            }
            // 开始校验：该装备合成公式的下级装备数量 是否与已激活装备数量相同 如果相同则校验通过
            if (equipNum != wearList.size()) {
                throw new RRException("请先激活下层全部装备");
            }
        }
    }

    /**
     * 装备合成
     *
     * @param user
     * @param req
     */
    @Login
    @PostMapping("equipmentSynthesis")
    @ApiOperation("装备合成")
    public R equipmentSynthesis(@LoginUser UserEntity user, @RequestBody UserHeroInfoReq req) {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄
        UserHeroEntity userHero = getUserHero(req.getUserHeroId());

        return R.ok().put("data", null);
    }

    /**
     * 校验英雄
     *
     * @param userHeroId
     * @return
     */
    private UserHeroEntity getUserHero(Long userHeroId) {
        // 获取英雄
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("userHeroId", userHeroId);
        UserHeroEntity userHero = userHeroService.getUserHeroById(userHeroMap);
        if (userHero == null) {
            System.out.println("获取玩家英雄失败");
        }
        return userHero;
    }

    /**
     * 更新战力(判断该英雄是否在上阵中, 只有上阵中的英雄更新战力及矿工)
     *
     * @param user
     * @param userHeroId
     * @param changePower
     */
    private void updateCombatPower(UserEntity user, Long userHeroId, Long userEquipId, Long changePower, Double scale) {
        // 获取队伍
        Map<String, Object> teamParams = new HashMap<>();
        teamParams.put("userId", user.getUserId());
        teamParams.put("userHeroId", userHeroId);
        List<TeamInfoRsp> teamInfoRsps = teamConfigService.getTeamInfoList(teamParams);

//        (30 / 60 * 68 + 60 / 60 * 10) / （68 + 10) * 60;
        // 获取玩家英雄信息
        UserHeroEntity hero = userHeroService.getById(userHeroId);
        BigDecimal newOracle = BigDecimal.ZERO;
        BigDecimal newMinter = BigDecimal.ZERO;
        // 初始GAIA系统
        fightCoreService.initTradeBalanceParameter(0);
        BigDecimal minterRate = CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1));
        if (userEquipId != null) {
            UserEquipmentEntity equip = userEquipmentService.getById(userEquipId);
            BigDecimal oracleRate = Arith.divide(hero.getOracle(), minterRate);
            BigDecimal heroPowerChange = Arith.multiply(oracleRate, BigDecimal.valueOf(hero.getHeroPower()));
            BigDecimal equipPowerChange = Arith.multiply(Arith.divide(equip.getOracle(), minterRate), BigDecimal.valueOf(equip.getEquipPower()));
            newOracle =
                    Arith.multiply(Arith.divide(Arith.add(heroPowerChange,equipPowerChange)
                    , BigDecimal.valueOf(hero.getHeroPower() + equip.getEquipPower())),
                            minterRate);
            newMinter = equip.getMinter();
        } else {
            // 获取英雄矿工数量
            CalculateTradeUtil.miners = hero.getMinter();
            System.out.println("获取当前玩家矿工数量: " + CalculateTradeUtil.miners);
            BigDecimal minter = CalculateTradeUtil.updateMiner(BigDecimal.valueOf(changePower * scale));
            newOracle = Arith.multiply(Arith.divide(Arith.add(Arith.multiply(Arith.divide(hero.getOracle(), minterRate), BigDecimal.valueOf(hero.getHeroPower())), BigDecimal.valueOf(changePower))
                    , BigDecimal.valueOf((hero.getHeroPower() + changePower))), minterRate);
            newMinter = minter;
        }

        hero.setMinter(Arith.add(hero.getMinter(), newMinter));// 更新矿工
        hero.setOracle(newOracle);// 更新神谕值
        long newHeroPower = hero.getHeroPower() + changePower;// 获取英雄提升后的战力（英雄战力+本次操作改变的战力）
        hero.setHeroPower(newHeroPower);// 更新战力
        userHeroService.updateById(hero);

        // 英雄已在队伍上阵
        if (teamInfoRsps.size() > 0) {
            for (TeamInfoRsp teamInfoRsp : teamInfoRsps) {
                // 获取队伍旧战力值
                long oldPower = teamInfoRsp.getTeamPower();
                // 获取最新队伍战力（队伍战力+本次操作改变的战力）
                long newPower = teamInfoRsp.getTeamPower() + changePower;
                // 获取最新队伍矿工数量（队伍矿工数+本次操作改变的矿工数）
                BigDecimal newTeanMinter = Arith.add(teamInfoRsp.getTeamMinter(), newMinter);

                // 获取队伍旧矿工数
                BigDecimal oldMinter = teamInfoRsp.getTeamMinter();
                GmTeamConfigEntity team = new GmTeamConfigEntity();
                team.setId(teamInfoRsp.getId());
                fightCoreService.updateCombat(changePower, oldPower, newPower, user, team, newMinter, oldMinter, newTeanMinter);
            }
        }

    }

    @Login
    @PostMapping("upgradeStar")
    @ApiOperation("英雄升星")
    public R upgradeStar(@LoginUser UserEntity user, @RequestBody UserHeroInfoReq req) {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 初始化当前星级英雄属性
        AttributeSimpleEntity attributeStar;
        // 初始化升星后英雄属性
        AttributeSimpleEntity attributeStarPlus;
        // 初始化本次增加的属性
        AttributeSimpleEntity attributeSimpleAdd;
        // 获取玩家英雄
        UserHeroEntity userHero = getUserHero(req.getUserHeroId());
        // 获取玩家背包中的英雄碎片
        Map<String, Object> heroFragMap = new HashMap<>();
        heroFragMap.put("userId", user.getUserId());
        heroFragMap.put("status", Constant.enable);
        heroFragMap.put("heroId", userHero.getHeroId());
        List<UserHeroFragInfoRsp> heroFragList = userHeroFragService.getUserAllHeroFrag(heroFragMap);
        // 获取当前星级+1
        int starCode = userHero.getStarCode();
        if (starCode < Constant.StarLv.Lv5.getValue()) {
            starCode += 1;
        } else {
            throw new RRException("full star!");
        }

        // 获取星级信息
        List<StarInfoEntity> starInfos = starInfoService.getStarInfoPro();
        // 初始化升星所需碎片
        int nextStarFragNum = 0;
        // 初始化当前星级属性加成
        double starBuff = 0d;
        // 初始化升星后的属性加成
        double starBuffPlus = 0d;
        for (StarInfoEntity starInfo : starInfos) {
            // 设置升星后的属性
            if (starInfo.getStarCode() == starCode) {
                // 获取升星所需碎片
                nextStarFragNum = starInfo.getUpStarFragNum();
                // 获取升星后的属性加成
                starBuffPlus = starInfo.getStarBuff();
            }

            // 设置当前星级属性
            if (starInfo.getStarCode() == userHero.getStarCode()) {
                // 获取当前星级属性加成
                starBuff = starInfo.getStarBuff();
            }
        }

        // 初始化本次增加的战力
        long changePower = 0;
        // 初始化当前星级英雄战力
        long nowPower = 0;
        // 初始化升星后英雄战力
        long upPower = 0;
        // 先校验玩家背包英雄碎片是否足够本次升星操作
        if (heroFragList.get(0).getHeroFragNum() >= nextStarFragNum) {
            try {
                // 获取英雄初始属性
                HeroInfoEntity heroInfo = heroInfoService.getById(userHero.getHeroId());

                // 获取当前星级英雄属性
                attributeStar = combatStatsUtilsService.getHeroAttribute(heroInfo, starBuff);
                // 获取当前星级英雄战力
                nowPower = combatStatsUtilsService.getHeroPower(attributeStar);

                // 获取升星后的英雄属性
                attributeStarPlus = combatStatsUtilsService.getHeroAttribute(heroInfo, starBuffPlus);
                // 获取升星后英雄战力
                upPower = combatStatsUtilsService.getHeroPower(attributeStarPlus);

                // 获取本次增加的属性
                attributeSimpleAdd = combatStatsUtilsService.getAttributesAddedAfterStarUpgrade(attributeStar, attributeStarPlus);

                // 获取本次增加的战力
                changePower = upPower - nowPower;

                // 获取英雄碎片的SCALE的平均值
                Double scale = heroFragList.get(0).getScale();
                // 消耗英雄碎片
                heroFragMap.put("heroFragNum", nextStarFragNum);
                userHeroFragService.depleteHeroFrag(heroFragMap);
                // 英雄升星\更新英雄战力
                Date now = new Date();
                UserHeroEntity userHeroUp = new UserHeroEntity();
                userHeroUp.setUserHeroId(req.getUserHeroId());
                userHero.setStarCode(starCode);
                userHeroUp.setHeroPower(userHero.getHeroPower() + changePower);// 累加战力
                userHeroUp.setHealth(userHero.getHealth() + attributeSimpleAdd.getHp());// 累加生命值
                userHeroUp.setMana(userHero.getMana() + attributeSimpleAdd.getMp());// 累加法力值
                userHeroUp.setHealthRegen(userHero.getHealthRegen() + attributeSimpleAdd.getHpRegen());// 累加生命值恢复
                userHeroUp.setManaRegen(userHero.getManaRegen() + attributeSimpleAdd.getMpRegen());// 累加法力值恢复
                userHeroUp.setArmor(userHero.getArmor() + attributeSimpleAdd.getArmor());// 累加护甲
                userHeroUp.setMagicResist(userHero.getMagicResist() + attributeSimpleAdd.getMagicResist());// 累加魔抗
                userHeroUp.setAttackDamage(userHero.getAttackDamage() + attributeSimpleAdd.getAttackDamage());// 累加攻击力
                userHeroUp.setAttackSpell(userHero.getAttackSpell() + attributeSimpleAdd.getAttackSpell());// 累加法功
                userHeroUp.setUpdateTime(now);
                userHeroUp.setUpdateTimeTs(now.getTime());
                userHeroService.updateById(userHeroUp);
                // 更新战力及矿工
                updateCombatPower(user, req.getUserHeroId(), null, changePower, scale);
            } catch (RRException e) {
                e.printStackTrace();
                throw new RRException("Star upgrade failed:" + e.getMsg());
            }

        } else {
            throw new RRException("Insufficient hero shards!");
        }
        Map<String, Object> map = new HashMap();
        map.put("changePower", changePower);
        return R.ok().put("data", map);
    }


    @PostMapping("getHeroLevels")
    @ApiOperation("获取英雄升级表")
    public R getHeroLevels() {
        // 获取英雄等级信息
        List<HeroLevelRsp> heroLevels = heroLevelService.getHeroLevels();
        return R.ok().put("heroLevels", heroLevels);
    }

    @Login
    @PostMapping("getExpProps")
    @ApiOperation("获取经验道具")
    public R getExpProps(@LoginUser UserEntity user, @RequestBody UseExpReq useExpReq) {
        // 表单校验
        ValidatorUtils.validateEntity(useExpReq);
        // 返回的信息
        List<UserExpInfoRsp> rsp = new ArrayList<>();
        // 获取英雄
        UserHeroEntity userHero = getUserHero(useExpReq.getUserHeroId());
        // 获取等级信息
        List<HeroLevelEntity> heroLevels;
        // 获取背包经验道具(未使用)
        UserExperiencePotionEntity exp = new UserExperiencePotionEntity();
        exp.setUserId(user.getUserId());
        List<UserExpInfoRsp> userExpProps = userExService.getUserEx(exp);
        // 获取经验道具基础信息
        List<ExperiencePotionEntity> expInfos = experiencePotionService.getExpInfos();
        Long expTotal = 0l;//总经验
        for (ExperiencePotionEntity expInfo : expInfos) {
            UserExpInfoRsp userExpInfo = new UserExpInfoRsp();
            userExpInfo.setExpName(expInfo.getExPotionName());
            userExpInfo.setExpRare(expInfo.getExPotionRareCode());
            userExpInfo.setExpValue(expInfo.getExValue());
            userExpInfo.setExpIconUrl(expInfo.getExIconUrl());
            userExpInfo.setExpDescription(expInfo.getExDescription());
            userExpInfo.setExpNum(Constant.ZERO);
            int i = 0;
            while (i < userExpProps.size()) {
                if (expInfo.getExPotionRareCode().equals(userExpProps.get(i).getExpRare())) {
                    userExpInfo.setExpNum(userExpProps.get(i).getExpNum());
                    // 计算全部经验道具累加后的总经验值
                    expTotal += userExpProps.get(i).getExpNum() * userExpProps.get(i).getExpValue();
                    // 通过总经验值计算本次可提升最高等级
                }

                i++;
            }
            rsp.add(userExpInfo);


        }
        Map<String, Object> rspMap = new HashMap<>();
        rspMap.put("expInfo", rsp);
        rspMap.put("expTotal", expTotal);
        return R.ok().put("data", rspMap);
    }

    @Login
    @PostMapping("useExpProps")
    @ApiOperation("使用经验药水")
    public R useExpProps(@LoginUser UserEntity user, @RequestBody UseExpReq useExpReq) {
        // 表单校验
        ValidatorUtils.validateEntity(useExpReq);
        // 经验药水数量
        if (useExpReq.getExpNum() == null && useExpReq.getExpNum() == 0) {
            throw new RRException(ErrorCode.EXP_NUM_NOT_NULL.getDesc());
        }
        // 药水稀有度
        if (StringUtils.isNotBlank(useExpReq.getExpRare())) {
            // 参数安全校验 防止注入攻击;
            if (ValidatorUtils.securityVerify(useExpReq.getExpRare())) {
                throw new RRException(ErrorCode.EXP_RARE_NOT_NULL.getDesc());
            }
        } else {
            throw new RRException(ErrorCode.EXP_RARE_NOT_NULL.getDesc());
        }
        userExService.userHeroUseEx(user, useExpReq);
        return R.ok();
    }
}
