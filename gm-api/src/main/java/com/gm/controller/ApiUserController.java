/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.*;
import com.gm.common.validator.ValidatorUtils;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.basicconfig.rsp.HeroInfoDetailRsp;
import com.gm.modules.basicconfig.rsp.HeroLevelRsp;
import com.gm.modules.basicconfig.rsp.HeroSkillRsp;
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
import java.util.concurrent.ExecutionException;

/**
 * 用户信息接口
 *
 * @author Mark axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags = "用户信息接口")
public class ApiUserController {
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

    @Login
    @PostMapping("getUserHeroInfo")
    @ApiOperation("获取玩家英雄信息")
    public R getUserHeroInfo(@LoginUser UserEntity user) {
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("userId", user.getUserId());
        List<UserHeroInfoRsp> heroList = userHeroService.getUserAllHero(userHeroMap);
        return R.ok().put("heroList", heroList);
    }

    @PostMapping("getHeroInfo")
    @ApiOperation("根据英雄名称获取英雄信息")
    public R getHeroInfo(@RequestBody UserHeroInfoReq req) {
        // 安全校验 防止注入攻击;
        if (ValidatorUtils.securityVerify(req.getHeroName())) {
            throw new RRException(ErrorCode.REQUEST_PARAMETER_DATA_EXCEPTION.getDesc());
        }
        HeroInfoEntity heroInfo = heroInfoService.getOne(new QueryWrapper<HeroInfoEntity>()
                .eq("HERO_NAME", req.getHeroName())
                .eq("STATUS", Constant.enable)
        );
        Map<String, Object> map = new HashMap<>();
        map.put("name", heroInfo.getHeroName());
        map.put("imgUrl", heroInfo.getHeroImgUrl());
        map.put("description", heroInfo.getHeroDescription());
        return R.ok().put("data", map);
    }

    @PostMapping("getHeroInfoList")
    @ApiOperation("获取英雄列表")
    public R getHeroInfoList() {
        List<HeroInfoDetailRsp> list = heroInfoService.getHeroInfoList();
        int i = 0;
        while (i < list.size()) {
            // 设置英雄职业
            String[] heroRole = list.get(i).getHeroType().split(",");
            for (String role : heroRole) {
                switch (role) {
                    case "00":
                        list.get(i).getRoles().add(Constant.HeroRole.Warrior.getValue());
                        break;
                    case "01":
                        list.get(i).getRoles().add(Constant.HeroRole.Mage.getValue());
                        break;
                    case "02":
                        list.get(i).getRoles().add(Constant.HeroRole.Assassin.getValue());
                        break;
                    case "03":
                        list.get(i).getRoles().add(Constant.HeroRole.Tank.getValue());
                        break;
                    case "04":
                        list.get(i).getRoles().add(Constant.HeroRole.Support.getValue());
                        break;
                    case "05":
                        list.get(i).getRoles().add(Constant.HeroRole.Archer.getValue());
                        break;
                }
            }
            i++;
        }
        return R.ok().put("data", list);
    }



    @Login
    @PostMapping("getUserProps")
    @ApiOperation("获取玩家背包信息")
    public R getUserProps(@LoginUser UserEntity user) {
        // 初始GAIA系统
        fightCoreService.initTradeBalanceParameter(0);
        Map<String, Object> map = new HashMap<>();
        // 获取玩家英雄信息和穿戴的装备信息
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("userId", user.getUserId());
        List<UserHeroInfoRsp> heroList = userHeroService.getUserAllHero(userHeroMap);
        // 获取该英雄已穿戴的装备
        Map<String, Object> userWearMap = new HashMap<>();
        userWearMap.put("status", Constant.enable);
//        userWearMap.put("userHeroId", heroList.get(i).getUserHeroId());
        List<UserHeroEquipmentWearRsp> wearList = userHeroEquipmentWearService.getUserWearEQ(userWearMap);
        int i = 0;
        while (i < heroList.size()) {
            for (UserHeroEquipmentWearRsp wearRsp : wearList) {
                if (heroList.get(i).getUserHeroId().equals(wearRsp.getUserHeroId())) {
                    heroList.get(i).setWearEQList(wearList);
                }
            }
            i++;
        }
        map.put("heroList", heroList);

        // 获取玩家所有的英雄碎片
        Map<String, Object> heroFragMap = new HashMap<>();
        heroFragMap.put("userId", user.getUserId());
        heroFragMap.put("status", Constant.enable);
        List<UserHeroFragInfoRsp> heroFragList = userHeroFragService.getUserAllHeroFrag(heroFragMap);
        map.put("heroFragList", heroFragList);

        // 获取玩家所有的装备
        UserEquipmentEntity userEquipment = new UserEquipmentEntity();
        userEquipment.setStatus(Constant.enable);
        userEquipment.setUserId(user.getUserId());
        List<UserEquipInfoRsp> equipmentEntities = userEquipmentService.getUserEquip(userEquipment);
        // 矿工兑换数量比例
        BigDecimal minterRate = CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1));
        for (int e = 0; e < equipmentEntities.size(); e++) {
            BigDecimal newOracle = BigDecimal.valueOf(Arith.multiply(Arith.divide(equipmentEntities.get(e).getOracle(), minterRate), BigDecimal.valueOf(100)).intValue());
            equipmentEntities.get(e).setOracle(newOracle);
        }
        map.put("equipList", equipmentEntities);

        // 获取玩家所有的装备碎片
        List<UserEquipmentFragInfoRsp> equipmentFragEntities = userEquipmentFragService.getUserAllEquipFrag(user.getUserId());
        map.put("equipFragList", equipmentFragEntities);

        // 获取玩家所有的消耗品
        Map<String, Object> propsMap = new HashMap<>();

        // 获取玩家所有的经验道具
        Map<String, Object> expMap = new HashMap<>();
        expMap.put("userId", user.getUserId());
        List<UserExpInfoRsp> expList = userExpService.getUserExp(expMap);
        propsMap.put("expList", expList);

        map.put("consumables", propsMap);
        return R.ok(map);
    }

    @Login
    @PostMapping("getUserBalance")
    @ApiOperation("获取玩家余额")
    public R getUserBalance(@LoginUser UserEntity user) throws InvocationTargetException, IllegalAccessException {
        UserQueryBalanceRsp rsp = new UserQueryBalanceRsp();
        // 获取用户账户余额
        UserAccountEntity userAccount = getUserAccount(user);
        if (userAccount == null) {
            throw new RRException(ErrorCode.USER_GET_BAL_FAIL.getDesc());
        }
        BeanUtils.copyProperties(rsp, userAccount);
        return R.ok().put("userAccount", rsp);
    }

    /**
     * 获取用户账户余额
     *
     * @param user
     * @return
     */
    private UserAccountEntity getUserAccount(UserEntity user) {
        QueryWrapper<UserAccountEntity> wrapper = new QueryWrapper<UserAccountEntity>()
                .eq("USER_ID", user.getUserId());
        return userAccountService.getOne(wrapper);
    }

    @Login
    @PostMapping("getPlayerInfo")
    @ApiOperation("获取玩家信息")
    public R getPayerInfo(@LoginUser UserEntity user) throws InvocationTargetException, IllegalAccessException {
        // 获取玩家信息
        UserInfoRsp rsp = getUserInfo(user.getAddress());
        rsp.setNextLevelExp(rsp.getPromotionExperience());
        rsp.setFtgMax(Constant.FTG);
        rsp.setCurrentExp(ExpUtils.getCurrentExp(rsp.getExperienceTotal(), rsp.getPromotionExperience(), user.getExperienceObtain()));
        return R.ok().put("userInfo", rsp);
    }

    private UserInfoRsp getUserInfo(String address) {
        UserInfoRsp rsp = new UserInfoRsp();
        Map<String, Object> map = new HashMap<>();
        map.put("address", address);
        List<UserInfoRsp> list = userService.getPlayerInfo(map);
        Double mrCoins = Constant.ZERO_D;
        Double mrCoinsAgent = Constant.ZERO_D;
        int i = 0;
        while (i < list.size()) {
            if (list.get(i).getCurrency().equals(Constant.ZERO_)) {
                mrCoins = list.get(i).getMrCoins();
            } else if (list.get(i).getCurrency().equals(Constant.ONE_)) {
                mrCoinsAgent = list.get(i).getMrCoins();
            }
            i++;
        }
        if (list.size() > 0) {
            rsp = list.get(0);
            rsp.setMrCoins(mrCoins);
            rsp.setMrCoinsAgent(mrCoinsAgent);
        }
        return rsp;
    }

    @PostMapping("getPlayerInfoSimple")
    @ApiOperation("获取玩家信息")
    public R getPayerInfoSimple(@RequestBody UserInfoReq req) throws InvocationTargetException, IllegalAccessException {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        UserInfoRsp rsp = new UserInfoRsp();
        if (StringUtils.isNotBlank(req.getAddress())) {
            rsp = getUserInfo(req.getAddress());
        }
        return R.ok().put("userInfo", rsp);
    }

    @PostMapping("addEmail")
    @ApiOperation("玩家邮箱")
    public R addEmail(@RequestBody UserInfoReq req) throws InvocationTargetException, IllegalAccessException {
        // 表单校验
        if (!ValidatorUtils.checkEmail(req.getEmail())) {
            throw new RRException("Email format error!");
        }
        GmEmailEntity email = new GmEmailEntity();
        Date now = new Date();
        email.setEmail(req.getEmail());
        email.setStatus(Constant.enable);
        email.setCreateTime(now);
        email.setCreateTimeTs(now.getTime());
        gmEmailService.save(email);
        return R.ok();
    }


    @Login
    @PostMapping("getHeroDetail")
    @ApiOperation("获取英雄详细信息")
    public R getHeroDetail(@LoginUser UserEntity user, @RequestBody UserHeroInfoReq req) throws InvocationTargetException, IllegalAccessException {
        // 表单校验
        ValidatorUtils.validateEntity(req);
        // 获取英雄详细信息
        UserHeroInfoDetailRsp rsp = combatStatsUtilsService.getHeroInfoDetail(user, req.getUserHeroId(), Constant.disabled);
        if (rsp == null) {
            throw new RRException("Failed to get player hero information!");
        }

        // 获取系统全部装备
        List<EquipmentInfoEntity> equipments = equipmentInfoService.getEquipmentInfos(new HashMap<>());
        for (int i = 0; i < equipments.size(); i++) {
            if (equipments.get(i).getStatus().equals(Constant.disabled)) {
                equipments.get(i).setEquipName("");
                equipments.get(i).setDescription("mysterious equipment");
            }
        }
        // 获取英雄装备栏
        Map<String, Object> heroEquipMap = new HashMap<>();
        heroEquipMap.put("status", Constant.enable);
        heroEquipMap.put("heroId", rsp.getHeroId());
        List<HeroEquipmentEntity> heroEquipments = heroEquipmentService.getHeroEquipments(heroEquipMap);
        // 获取该英雄已穿戴的装备
        Map<String, Object> userWearMap = new HashMap<>();
        userWearMap.put("status", Constant.enable);
        userWearMap.put("userHeroId", rsp.getUserHeroId());
        List<UserHeroEquipmentWearRsp> wearList = userHeroEquipmentWearService.getUserWearEQ(userWearMap);
        // 获取装备栏中的装备合成公式
        Map<String, Object> eqSIEMap = new HashMap<>();
        eqSIEMap.put("STATUS", Constant.enable);
        List<EquipSynthesisItemEntity> eqSIEs = equipSynthesisItemService.getEquipSynthesisItemEntitys(eqSIEMap);
        JSONArray jsonArray = new JSONArray();
        for (HeroEquipmentEntity heroEquipment : heroEquipments) {
            for (EquipSynthesisItemEntity eqSIE : eqSIEs) {
                if (heroEquipment.getEquipId().equals(eqSIE.getEquipmentId())) {
                    // 获取英雄已激活/未激活装备
                    jsonArray.add(equipmentInfoService.updateEquipJson2(heroEquipment.getEquipId(), eqSIE, equipments, rsp, wearList));
                }
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("heroInfo", rsp);
        map.put("equipments", jsonArray);
        return R.ok().put("data", map);
    }

    @Login
    @PostMapping("withdraw")
    @ApiOperation("玩家提现")
    public R withdraw(@LoginUser UserEntity user, @RequestBody UseWithdrawReq useWithdrawReq) throws IOException {
        // 表单校验
        ValidatorUtils.validateEntity(useWithdrawReq);
        // 根据交易哈希查询交易
        Web3j web3j = transactionVerifyUtils.connect();
        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(useWithdrawReq.getRefundHash()).send();
        Optional<TransactionReceipt> transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt();
        if (!transactionReceipt.isPresent()) {
//            throw new RRException("query hash fail");
            return R.ok();
        }
        TransactionReceipt receipt = transactionReceipt.get();
        if (!"0x1".equals(receipt.getStatus())) {//不成功
            return R.ok();
        }
        String data = receipt.getLogs().get(0).getData();
        String one = data.substring(0, 66);// 时间戳
        String two = data.substring(66, 130);// gas费
        String there = data.substring(130, 194);// 提款金额
        BigInteger oneBigInteger = Numeric.toBigInt(one);
        BigDecimal twoBigDecimal = Convert.fromWei(Numeric.toBigInt("0x" + two).toString(), Convert.Unit.ETHER);
        BigDecimal thereBigDecimal = Convert.fromWei(Numeric.toBigInt("0x" + there).toString(), Convert.Unit.ETHER);
        useWithdrawReq.setWithdrawMoney(thereBigDecimal);
        useWithdrawReq.setApplyWithdrawGas(twoBigDecimal);
//        // 1.查询该会员上次提现时间
//        GmUserWithdrawEntity lastWithdraw = gmUserWithdrawService.lastWithdraw(user);
//        if (lastWithdraw != null) {
//            Date date = DateUtils.addDateHours(lastWithdraw.getCreateTime(), 24);// 上次提现时间加24小时，然后和当前时间做比较
//            if (date.after(new Date())) {// 24小时只能发起一次提现
//                throw new RRException(ErrorCode.WITHDRAW_OVER_TIMES.getDesc());
//            }
//        }
//        // 2.查询该会员消费等级
//        GmUserVipLevelEntity gmUserVipLevel = gmUserVipLevelService.getById(user.getVipLevelId());
//        // 3.查询该会员当前余额
//        UserAccountEntity userAccountEntity = userAccountService.queryByUserIdAndCur(user.getUserId(), useWithdrawReq.getWithdrawType());
//        if (userAccountEntity.getBalance() * gmUserVipLevel.getWithdrawLimit() < thereBigDecimal.doubleValue()) {
//            // 提现超额
//            throw new RRException(ErrorCode.WITHDRAW_OVER_MONEY.getDesc());
//        }
//        useWithdrawReq.setWithdrawHandlingFee(new BigDecimal(gmUserVipLevel.getWithdrawHandlingFee()));
        gmUserWithdrawService.applyWithdraw(user, useWithdrawReq);
//            gmUserWithdrawService.withdraw(user, gmUserVipLevel, useWithdrawReq);
        return R.ok();
    }

    @Login
    @PostMapping("fightingWithdrawList")
    @ApiOperation("战斗玩家提现记录")
    public R fightingWithdrawList(@LoginUser UserEntity user, @RequestParam Map<String, Object> params) {
        // 1.查询该用户战斗玩家提现记录
        PageUtils page = gmUserWithdrawService.queryWithdrawList(user.getUserId(), params, "0");
        return R.ok().put("page", page);
    }

    @Login
    @PostMapping("inviteWithdrawList")
    @ApiOperation("代理玩家提现记录")
    public R inviteWithdrawList(@LoginUser UserEntity user, @RequestParam Map<String, Object> params) {
        // 1.查询该用户代理账户提现记录
        PageUtils page = gmUserWithdrawService.queryWithdrawList(user.getUserId(), params, "1");
        return R.ok().put("page", page);
    }

    @Login
    @PostMapping("vipLevelList")
    @ApiOperation("消费等级信息列表")
    public R vipLevelList(@LoginUser UserEntity user) {
        // 1.查询消费等级列表
        List<GmUserVipLevelEntity> gmUserVipLevelEntities = gmUserVipLevelService.vipLevelList(user);
        // 2.查询该用户当前累计消费总额
        Double consumeMoney = transactionOrderService.queryTotalMoneyByUserId(user.getUserId());
        // 3.查询该用户的儿子的所有消费总额
        Double sonConsumeMoney = transactionOrderService.querySonTotalMoneyByFatherId(user.getUserId());
        // 4.查询该用户的下级人数
        Integer userInviteNum = userService.queryInviteNum(user);
        Map<String, Object> map = new HashMap<>();
        map.put("consumeMoney", consumeMoney);
        map.put("sonConsumeMoney", sonConsumeMoney);
        map.put("userInviteNum", userInviteNum);
        map.put("vipLevelList", gmUserVipLevelEntities);
        return R.ok().put("data", map);
    }

}
