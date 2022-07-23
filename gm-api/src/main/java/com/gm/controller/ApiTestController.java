/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;

import com.gm.annotation.LoginUser;
import com.gm.common.utils.Arith;
import com.gm.common.utils.R;
import com.gm.annotation.Login;
import com.gm.modules.user.entity.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.*;


/**
 * 测试接口
 *
 * @author Mark axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags="测试接口")
public class ApiTestController {

    @Login
    @GetMapping("userInfo")
    @ApiOperation(value="获取用户信息", response=UserEntity.class)
    public R userInfo(@ApiIgnore @LoginUser UserEntity user){
        return R.ok().put("user", user);
    }

    @Login
    @GetMapping("userId")
    @ApiOperation("获取用户ID")
    public R userInfo(@ApiIgnore @RequestAttribute("userId") Integer userId){
        return R.ok().put("userId", userId);
    }

    @GetMapping("notToken")
    @ApiOperation("忽略Token验证测试")
    public R notToken(){
        return R.ok().put("msg", "无需token也能访问。。。");
    }




    // 1个矿工可以孵化的鸡蛋(代币)数量 （1天有86400秒，被放大了10倍）
    private static BigDecimal EGGS_TO_HATCH_1MINERS = BigDecimal.valueOf(864000);
    private static BigDecimal PSN = BigDecimal.valueOf(10000);
    private static BigDecimal PSNH = BigDecimal.valueOf(5000);
    // 用户矿工数量
    private static Map<String,BigDecimal> miners = new HashMap<>();
    // 用户鸡蛋数量
    private static Map<String,BigDecimal> claimedEggs = new HashMap<>();
    // 用户上次购买矿工的时间
    private static Map<String,BigDecimal> lastHatch = new HashMap<>();
    // 初始市场总鸡蛋数量
    private static BigDecimal marketEggs = BigDecimal.valueOf(864000000);
    // 资金池总资金
    private static BigDecimal FundPool = BigDecimal.valueOf(10);

    private static BigDecimal time = Arith.divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000));

    private static Map<String,BigDecimal> userpower =  new HashMap<>(); // 用户战力
    private static BigDecimal totalPower = BigDecimal.valueOf(1000); // 总战力
    private static BigDecimal[] maplevel = {BigDecimal.valueOf(0.05)}; // 地图奖励分配百分比
    static String[] userids = {"a0","a1","a2"};
//    static long totalPower = 19000;

    private static BigDecimal calculateCombatPower(String userid) {
        System.out.println("totalPower====："+totalPower);
        return userpower.get(userid);
    }
    public static long power(){
        Random random = new Random();
        int number = random.nextInt(10)+1;
        return number;
    }
    public static void main(String[] args) {
        FundPool = Arith.multiply(FundPool,maplevel[0]);
        FundPool = BigDecimal.valueOf(10000);
        System.out.println("此地图池子余额:" + FundPool);
        for (int i = 0 ; i < 1; i++){
            //购买
            time = Arith.divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000));
            miners.put("a"+i,BigDecimal.valueOf(19.702970));
            lastHatch.put("a"+i,time);
            claimedEggs.put("a"+i,BigDecimal.valueOf(0));

            userpower.put("a"+i,Arith.divide(BigDecimal.valueOf(1000),BigDecimal.valueOf(100)));
            buyEggs("a"+i);
            System.out.println("marketEggs:" + marketEggs);
        }
//        FundPool = totalPower;
        for(int j = 0 ; j < 1; j++){
            System.out.println("蒂"+j+"天");
            //购买
            time = Arith.add(Arith.divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000)),Arith.multiply(EGGS_TO_HATCH_1MINERS,BigDecimal.valueOf(j)));
            for (int i = 0 ; i < 1; i++){
                System.out.println("marketEggs:" + marketEggs);
                System.out.println("miners:" + miners.get("a"+i));
                System.out.println("lastHatch:" + lastHatch.get("a"+i));

                sellEggs("a"+i);
            }
        }
        System.out.println("totalPower:"+totalPower);
        System.out.println("marketEggs:"+marketEggs);

    }


    /// @dev 奖励复投
    private static void hatchEggs(String userid) {
        if(userid == userid) {
//            ref = address(0);
        }
//        if(referrals == address(0) && referrals!=userid){
//            referrals[msg.sender] = ref;
//        }
        BigDecimal eggsUsed = getMyEggs(userid);
        BigDecimal newMiners= Arith.divide(eggsUsed,EGGS_TO_HATCH_1MINERS);
        miners.put(userid,Arith.add(miners.get(userid) , newMiners));
        System.out.println("claimedEggs:" + claimedEggs.get(userid));
//        claimedEggs.put(userid,BigDecimal.valueOf(0));
        lastHatch.put(userid, time);

        // 消弱矿工囤积
        marketEggs = Arith.add(marketEggs,Arith.divide(eggsUsed,BigDecimal.valueOf(5)));
    }
    /// @dev 取出奖励
    private static void sellEggs(String userid) {
        BigDecimal hasEggs = getMyEggs(userid); // 8640000000
        BigDecimal eggValue = calculateEggSell(hasEggs);

        // 获取战力率
        BigDecimal powerRate = Arith.divide(eggValue, totalPower);
        // 获取用户可获取的金币
        BigDecimal userGetGold = Arith.multiply(powerRate, FundPool);
        // 资金池减少
        FundPool = Arith.subtract(FundPool,userGetGold);
        System.out.println("powerRate:"+powerRate);
        System.out.println("userGetGold:"+userGetGold);
        System.out.println("FundPool:"+FundPool);
//        BigDecimal fee = devFee(eggValue);
        totalPower = Arith.subtract(totalPower ,eggValue);
//        System.out.println("fee:"+fee);
        System.out.println("eggValue:"+eggValue);
        claimedEggs.put(userid,BigDecimal.valueOf(0));
        lastHatch.put(userid, time);
        marketEggs = Arith.add(marketEggs,hasEggs);
    }

    // eggs = getMyEggs
    // sun = calculateEggSell(eggs)
    // fee = devFee(sun)
    // 可以提取的奖励=sun - fee
    private static BigDecimal calculateEggSell(BigDecimal eggs) {
        return calculateTrade(eggs,marketEggs,totalPower);
    }

    // 经济平衡算法
    private static BigDecimal calculateTrade(BigDecimal rt, BigDecimal rs, BigDecimal bs){
//        return (PSN*bs)/(PSNH+((PSN*rs+PSNH*rt)/rt));
        BigDecimal a = Arith.multiply(PSN,bs);
        BigDecimal b = Arith.divide(
                Arith.add(
                        Arith.multiply(PSN,rs),
                        Arith.multiply(PSNH,rt)
                ),
                rt);

        BigDecimal c = Arith.add(PSNH,b

        );

        return Arith.divide(
                a,
                c
        );
    }

    /// @dev 购买矿工
    private static void buyEggs(String userid) {
        System.out.println("CombatPower:"+calculateCombatPower(userid));
        totalPower = Arith.add(totalPower, calculateCombatPower(userid));
        BigDecimal eggsBought = calculateEggBuy(calculateCombatPower(userid), Arith.subtract(totalPower , calculateCombatPower(userid)));
        claimedEggs.put(userid, Arith.add(claimedEggs.get(userid) ,eggsBought));
        hatchEggs(userid);
    }

    private static BigDecimal calculateEggBuy(BigDecimal eth, BigDecimal contractBalance) {
        return calculateTrade(eth,contractBalance,marketEggs);
    }

    private static BigDecimal getMyEggs(String userid) {
        return Arith.add(claimedEggs.get(userid),getEggsSinceLastHatch(userid));
    }
    private static BigDecimal getEggsSinceLastHatch(String userid) {
        BigDecimal secondsPassed = min(EGGS_TO_HATCH_1MINERS, Arith.subtract(time ,lastHatch.get(userid)));
        return Arith.multiply(secondsPassed, miners.get(userid));
    }
    private static BigDecimal min(BigDecimal a, BigDecimal b) {
        int flag = a.compareTo(b);
        return flag == -1 ? a : b;
    }


}
