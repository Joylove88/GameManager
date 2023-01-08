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
import com.gm.common.exception.RRException;
import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;
import com.gm.modules.basicconfig.entity.StarInfoEntity;
import com.gm.modules.basicconfig.rsp.HeroLevelRsp;
import com.gm.modules.basicconfig.service.*;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserEquipmentEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import com.gm.modules.user.req.UseExpPropReq;
import com.gm.modules.user.req.UserHeroInfoReq;
import com.gm.modules.user.rsp.*;
import com.gm.modules.user.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
@Api(tags = "用户信息接口")
public class ApiIncreaseCombatPowerController {
    @Autowired
    private UserHeroService userHeroService;
    @Autowired
    private UserHeroFragService userHeroFragService;
    @Autowired
    private UserEquipmentService userEquipmentService;
    @Autowired
    private UserExperienceService userExService;
    @Autowired
    private HeroLevelService heroLevelService;
    @Autowired
    private HeroInfoService heroInfoService;
    @Autowired
    private EquipSynthesisItemService equipSynthesisItemService;
    @Autowired
    private UserHeroEquipmentWearService userHeroEquipmentWearService;
    @Autowired
    private StarInfoService starInfoService;
    @Autowired
    private CombatStatsUtilsService combatStatsUtilsService;
    @Autowired
    private ExperienceService experienceService;


    @Login
    @PostMapping("equipment")
    @ApiOperation("英雄装备激活OR合成")
    @Transactional(rollbackFor = Exception.class)
    public R equipment(@LoginUser UserEntity user, @RequestBody UserHeroInfoReq req) throws InvocationTargetException, IllegalAccessException {
        // 校验该功能是否开放
        combatStatsUtilsService.isOpen();
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄
        UserHeroInfoDetailWithGrowRsp userHero = combatStatsUtilsService.getUserHero(req.getUserHeroId());
        // 初始化玩家英雄信息
        UserHeroInfoDetailRsp rsp;
        // 获取玩家装备信息
        Map<String, Object> userEquipMap = new HashMap<>();
        userEquipMap.put("status", Constant.enable);
        userEquipMap.put("userId", user.getUserId());
        userEquipMap.put("userEquipmentId", req.getUserEquipmentId());
        UserEquipInfoDetailRsp equipment = userEquipmentService.getUserEquipById(userEquipMap);
        if (equipment == null) {
            throw new RRException("You do not have this equipment or it has been activated!");
        }

        if (equipment.getStatus().equals(Constant.used)) {
            throw new RRException("This equipment has been used!");
        }

        // ===============校验子级装备是否已激活,装备等级是否超出英雄等级限制START===============
        verifyEquipment(req, userHero, equipment);
        // ===============校验子级装备是否已激活,装备等级是否超出英雄等级限制制END===============

        // 装备战力
        long equpPower = 0;
        Date now = new Date();
        // 穿戴表新增一条装备穿戴记录
        UserHeroEquipmentWearEntity userHeroEquipmentWear = new UserHeroEquipmentWearEntity();
        userHeroEquipmentWear.setHeroId(userHero.getHeroId());
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
        equpPower = equipment.getEquipPower();
        // 计算新的scale
        Double scale = user.getScale() * (((userHero.getScale() * userHero.getHeroPower()) + (equipment.getScale() * equpPower)) / (userHero.getHeroPower() + equpPower));
        // 更新玩家装备状态为已穿戴/激活
        UserEquipmentEntity setUserEquip = new UserEquipmentEntity();
        setUserEquip.setUserEquipmentId(req.getUserEquipmentId());
        setUserEquip.setStatus(Constant.used);
        setUserEquip.setUserId(user.getUserId());
        setUserEquip.setUpdateTime(now);
        setUserEquip.setUpdateTimeTs(now.getTime());
        boolean update = userEquipmentService.update(setUserEquip, new UpdateWrapper<UserEquipmentEntity>()
                .eq("STATUS", Constant.enable)
                .eq("USER_EQUIPMENT_ID", req.getUserEquipmentId())
        );
        if (!update) {
            throw new RRException("equipment activated!");
        }
        // 更新矿工、神谕值以及队伍战力、矿工，玩家战力、矿工
        UserHeroInfoDetailWithGrowRsp rspI = combatStatsUtilsService.updateCombatPower(user, userHero, null, equpPower, scale);
        // 更新英雄scale和矿工和神谕值
        UserHeroEntity userHeroUp = new UserHeroEntity();
        userHeroUp.setScale(scale);
        userHeroUp.setHeroPower(userHero.getHeroPower() + equpPower);
        userHeroUp.setUserHeroId(userHero.getUserHeroId());
        userHeroUp.setOracle(rspI.getOracle());
        userHeroUp.setMinter(Arith.add(userHero.getMinter(), rspI.getMinter()));
        userHeroUp.setUpdateTime(now);
        userHeroUp.setUpdateTimeTs(now.getTime());
        userHeroService.updateById(userHeroUp);

        // 装备激活成功后重新获取英雄详细信息并返回
        rsp = combatStatsUtilsService.getHeroInfoDetail(user, req.getUserHeroId(), Constant.enable);
        Map<String, Object> map = new HashMap<>();
        map.put("heroInfo", rsp);
        return R.ok().put("data", map);
    }

    /**
     * 校验子级装备是否已激活（子级装备激活后才可激活父级装备）,装备等级是否超出英雄等级限制
     * @param req
     * @param userHero
     * @param equipment
     */
    private void verifyEquipment(UserHeroInfoReq req, UserHeroInfoDetailWithGrowRsp userHero, UserEquipInfoDetailRsp equipment) {
        // 验证等级限制
        if (equipment.getEquipLevel() > userHero.getLevelCode()) {
            throw new RRException("The hero level is lower than the equipment level and cannot be activated!");
        }
        // 获取该装备的合成公式
        EquipSynthesisItemEntity eqSIEs = equipSynthesisItemService.getEquipSyntheticFormula(equipment.getEquipmentId());
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
                    throw new RRException("This equipment has been used!");
                }
            }
            // 开始校验：该装备合成公式的下级装备数量 是否与已激活装备数量相同 如果相同则校验通过
            if (equipNum != wearList.size()) {
                throw new RRException("Please activate other parts of this equipment first!");
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

        return R.ok().put("data", null);
    }


    @Login
    @PostMapping("upgradeStar")
    @ApiOperation("英雄升星")
    @Transactional(rollbackFor = Exception.class)
    public R upgradeStar(@LoginUser UserEntity user, @RequestBody UserHeroInfoReq req) {
        // 校验该功能是否开放
        combatStatsUtilsService.isOpen();
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取玩家英雄
        UserHeroInfoDetailWithGrowRsp userHero = combatStatsUtilsService.getUserHero(req.getUserHeroId());
        // 初始化玩家英雄信息
        UserHeroInfoDetailRsp rsp;
        // 获取玩家背包中的英雄碎片
        Map<String, Object> heroFragMap = new HashMap<>();
        heroFragMap.put("userId", user.getUserId());
        heroFragMap.put("status", Constant.enable);
        heroFragMap.put("heroId", userHero.getHeroId());
        UserHeroFragInfoDetailRsp heroFragCount = userHeroFragService.getUserAllHeroFragCount(heroFragMap);
        // 获取当前星级+1
        int starCode = userHero.getStarCode();
        if (starCode < Constant.StarLv.Lv5.getValue()) {
            starCode += 1;
        } else {
            throw new RRException("Already the highest star!");
        }

        // 获取星级信息
        List<StarInfoEntity> starInfos = starInfoService.getStarInfoPro();
        // 初始化升星所需碎片
        int nextStarFragNum = 0;
        for (StarInfoEntity starInfo : starInfos) {
            // 设置升星后的属性
            if (starInfo.getStarCode().equals(starCode)) {
                // 获取升星所需碎片
                nextStarFragNum = starInfo.getUpStarFragNum();
            }
        }

        // 先校验玩家背包英雄碎片是否足够本次升星操作
        if (heroFragCount.getHeroFragNum() >= nextStarFragNum) {
            try {
                // 获取英雄碎片的SCALE平均值
                Double scale = userHero.getScale() * heroFragCount.getScale();
                // 消耗英雄碎片
                heroFragMap.put("heroFragNum", nextStarFragNum);
                userHeroFragService.depleteHeroFrag(heroFragMap);
                // 英雄升星\更新英雄战力
                Date now = new Date();
                UserHeroEntity userHeroUp = new UserHeroEntity();
                userHeroUp.setUserHeroId(req.getUserHeroId());
                userHeroUp.setStarCode(starCode);
                userHeroUp.setScale(scale);
                userHeroUp.setUpdateTime(now);
                userHeroUp.setUpdateTimeTs(now.getTime());
                userHeroService.updateById(userHeroUp);
                // 升星成功后重新获取英雄详细信息并返回
                rsp = combatStatsUtilsService.getHeroInfoDetail(user, req.getUserHeroId(), Constant.enable);
            } catch (RRException e) {
                e.printStackTrace();
                throw new RRException("Star upgrade failed:" + e.getMsg());
            }

        } else {
            throw new RRException("Insufficient hero shards!");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("heroInfo", rsp);
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
    @PostMapping("useExpProps")
    @ApiOperation("使用经验道具")
    @Transactional(rollbackFor = Exception.class)
    public R useExpProps(@LoginUser UserEntity user, @RequestBody UseExpPropReq useExpPropReq) {
        // 校验该功能是否开放
        combatStatsUtilsService.isOpen();
        // 表单校验
        ValidatorUtils.validateEntity(useExpPropReq);
        userExService.userHeroUseExp(user, useExpPropReq);
        // 升级成功后重新获取英雄详细信息并返回
        UserHeroInfoDetailRsp rsp = combatStatsUtilsService.getHeroInfoDetail(user, useExpPropReq.getUserHeroId(), Constant.enable);
        Map<String, Object> map = new HashMap<>();
        map.put("heroInfo", rsp);
        return R.ok().put("data", map);
    }
}
