/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.*;
import com.gm.common.validator.ValidatorUtils;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;
import com.gm.modules.basicconfig.entity.EquipmentInfoEntity;
import com.gm.modules.basicconfig.entity.HeroEquipmentEntity;
import com.gm.modules.basicconfig.entity.HeroInfoEntity;
import com.gm.modules.basicconfig.rsp.HeroInfoDetailRsp;
import com.gm.modules.basicconfig.service.*;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.UseWithdrawReq;
import com.gm.modules.user.req.UserHeroInfoReq;
import com.gm.modules.user.req.UserInfoReq;
import com.gm.modules.user.rsp.*;
import com.gm.modules.user.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 用户信息接口
 *
 * @author Mark axiang
 */
@RestController
@RequestMapping("/metadata")
@Api(tags = "用户信息接口")
public class ApiMetadataController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserHeroService userHeroService;
    @Autowired
    private UserTokenService tokenService;
    @Autowired
    private StarInfoService starInfoService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private GmEmailService gmEmailService;
    @Autowired
    private HeroInfoService heroInfoService;
    @Autowired
    private UserHeroFragService userHeroFragService;
    @Autowired
    private UserEquipmentService userEquipmentService;
    @Autowired
    private UserEquipmentFragService userEquipmentFragService;
    @Autowired
    private UserExperienceService userExpService;
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
    @Autowired
    private GmUserWithdrawService gmUserWithdrawService;
    @Autowired
    private GmUserVipLevelService gmUserVipLevelService;
    @Autowired
    private UserExperienceService userExperienceService;
    @Autowired
    private FightCoreService fightCoreService;
    @Autowired
    private CombatStatsUtilsService combatStatsUtilsService;
    @Autowired
    private TransactionOrderService transactionOrderService;
    @Autowired
    private TransactionVerifyUtils transactionVerifyUtils;

    /**
     * 信息
     */
    @RequestMapping("/freehero/{nftId}.json")
    @ApiOperation("获取元数据")
    public Map<String, Object> metaData(@PathVariable("nftId") Long nftId){
        // 通过NFTID获取玩家英雄信息
        Map<String, Object> map = new HashMap<>();
        map.put("nftId", nftId);
        UserHeroInfoDetailRsp rsp = userHeroService.getUserHeroByIdDetailRsp(map);
        // 设置返回信息
        Map<String, Object> rspMap = new LinkedHashMap<>();
        rspMap.put("description", rsp.getHeroDescription());// 描述
        rspMap.put("external_url", "https://metarunes.games");// 外部地址
        rspMap.put("image", rsp.getHeroImgUrl());// 图片
        rspMap.put("name", rsp.getHeroName());// 名称

        // 设置属性集合
        Map<String, Object> attMap = new LinkedHashMap<>();
        attMap.put("CE", rsp.getHeroPower());
        attMap.put("LEVEL", rsp.getLevelCode());
        attMap.put("STAR", rsp.getStarCode());
        String skin = Objects.equals(rsp.getSkinType(), Constant.SkinType.GOLD.getValue()) ? "GOLD" : "ORIGINAL";
        attMap.put("SKIN", skin);
        // 初始GAIA系统
        fightCoreService.initTradeBalanceParameter(0);
        // 矿工兑换数量比例
        BigDecimal minterRate = CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1));
        BigDecimal newOracle = Arith.multiply(Arith.divide(rsp.getOracle(), minterRate), BigDecimal.valueOf(100));
        // // 按当前市场行情计算神谕值
        attMap.put("HP", rsp.getHealth());
        attMap.put("MP", rsp.getMana());
        attMap.put("ATTACK DAMAGE", rsp.getAttackDamage());
        attMap.put("ABILITY POWER", rsp.getAttackSpell());
        attMap.put("ARMOR", rsp.getArmor());
        attMap.put("MAGIC RESISTANCE", rsp.getMagicResist());
        attMap.put("GROWTH", rsp.getGrowthRate());
        attMap.put("ORACLE", newOracle);
//        attMap.put("HP Recovery", rsp.getHealthRegen());
//        attMap.put("MP Recovery", rsp.getManaRegen());
        JSONArray attArray = new JSONArray();
        for (String key : attMap.keySet()) {
            String value = attMap.get(key).toString();
            JSONObject att = new JSONObject();
            att.put("value", value);
            att.put("trait_type", key);
            attArray.add(att);
        }
        rspMap.put("attributes", attArray);
        return rspMap;
    }

}
