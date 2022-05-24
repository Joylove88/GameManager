/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;

import com.gm.annotation.LoginUser;
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
    static double EGGS_TO_HATCH_1MINERS = 864000;
    static double PSN = 10000;
    static double PSNH = 5000;
    // 用户矿工数量
    static Map<String,Object> miners = new HashMap<>();
    // 用户鸡蛋数量
    static Map<String,Object> claimedEggs = new HashMap<>();
    // 用户上次购买矿工的时间
    static Map<String,Object> lastHatch = new HashMap<>();
    // 用户的推荐人
    String referrals;
    // 初始市场总鸡蛋数量
    static double marketEggs = 864000000l;

    static double time = System.currentTimeMillis()/1000;

    static Map<String,Object> userpower =  new HashMap<>(); //用户战力
    static double ownerBalance = 10000; // 主资金池余额
    static double[] maplevel = {0.05,0.1,0.15,0.3,0.4};
    static String[] userids = {"a0","a1","a2"};
    static double totalPower = 19000;


    public static double calculateCombatPower(String userid) {
        return (ownerBalance / totalPower) * Double.parseDouble(String.valueOf(userpower.get(userid))) * maplevel[0] / 60;
    }

    public static void main(String[] args) {
    for(int j = 0 ; j < 100000; j++){
        for (int i = 0 ; i < 6; i++){
            //购买
            time = System.currentTimeMillis()/1000;
            miners.put("a0",0);
            miners.put("a1",0);
            miners.put("a2",0);
            miners.put("a3",0);
            miners.put("a4",0);
            miners.put("a5",0);
            lastHatch.put("a0",time);
            lastHatch.put("a1",time);
            lastHatch.put("a2",time);
            lastHatch.put("a3",time);
            lastHatch.put("a4",time);
            lastHatch.put("a5",time);
            claimedEggs.put("a0",0);
            claimedEggs.put("a1",0);
            claimedEggs.put("a2",0);
            claimedEggs.put("a3",0);
            claimedEggs.put("a4",0);
            claimedEggs.put("a5",0);

            userpower.put("a0",1000);
            userpower.put("a1",1500);
            userpower.put("a2",5000);
            userpower.put("a3",2000);
            userpower.put("a4",6500);
            userpower.put("a5",3000);
            System.out.println("========================================");
            buyEggs("a"+i);
            System.out.println("marketEggs:" + marketEggs);

            System.out.println("miners:" + miners.get("a"+i));
            System.out.println("lastHatch:" + lastHatch.get("a"+i));
            sellEggs("a"+i);
        }
    }
        System.out.println("ownerBalance:"+ownerBalance);


    }

    // 贸易平衡算法
    public static double calculateTrade(double rt,double rs, double bs){
        return (PSN*bs)/(PSNH+((PSN*rs+PSNH*rt)/rt));
    }

    /// @dev 奖励复投
    public static void hatchEggs(String userid) {
        if(userid == userid) {
//            ref = address(0);
        }
//        if(referrals == address(0) && referrals!=userid){
//            referrals[msg.sender] = ref;
//        }
        double eggsUsed = getMyEggs(userid);
        double newMiners= eggsUsed / EGGS_TO_HATCH_1MINERS;
        miners.put(userid,Double.parseDouble(String.valueOf(miners.get(userid))) + newMiners);
        System.out.println("claimedEggs:" + claimedEggs.get(userid));
//        claimedEggs.put(userid,0);
        lastHatch.put(userid, time);

        // 奖励的1/10分给推荐人
//        claimedEggs[referrals[msg.sender]] = claimedEggs[referrals[msg.sender]] + eggsUsed / 10;

        // 消弱矿工囤积
        marketEggs = marketEggs + eggsUsed / 5;
    }
    /// @dev 取出奖励
    public static void sellEggs(String userid) {
        double hasEggs = getMyEggs(userid); // 8640000000
        double eggValue = calculateEggSell(hasEggs);
        double fee = devFee(eggValue);
        ownerBalance = ownerBalance - fee;
        System.out.println("fee:"+fee);
        System.out.println("eggValue:"+eggValue);
        claimedEggs.put(userid,0);
        lastHatch.put(userid, time);
        marketEggs = marketEggs + hasEggs;
    }

    // eggs = getMyEggs
    // sun = calculateEggSell(eggs)
    // fee = devFee(sun)
    // 可以提取的奖励=sun - fee
    public static double calculateEggSell(double eggs) {
        return calculateTrade(eggs,marketEggs,ownerBalance);
    }

    /// @dev 购买矿工
    public static void buyEggs(String userid) {
        System.out.println("CombatPower:"+calculateCombatPower(userid));
        double eggsBought = calculateEggBuy(calculateCombatPower(userid), ownerBalance - calculateCombatPower(userid));
        eggsBought = eggsBought - devFee(eggsBought);
        double fee = devFee(calculateCombatPower(userid));
        System.out.println("buyFee:"+fee);
        claimedEggs.put(userid,Double.parseDouble(String.valueOf(claimedEggs.get(userid))) + eggsBought);
        hatchEggs(userid);
    }

    public static double calculateEggBuy(double eth, double contractBalance) {
        return calculateTrade(eth,contractBalance,marketEggs);
    }
    // 购买矿工兑换数量比例
    public static double calculateEggBuySimple(long eth) {
        return calculateEggBuy(eth,ownerBalance);
    }
    public static double devFee(double amount) {
        return amount * 10 / 100;
    }

    public static double getMyEggs(String userid) {
        return Double.parseDouble(String.valueOf(claimedEggs.get(userid))) + getEggsSinceLastHatch(userid);
    }
    public static double getEggsSinceLastHatch(String userid) {
        double secondsPassed = min(EGGS_TO_HATCH_1MINERS, time - Double.parseDouble(String.valueOf(lastHatch.get(userid))));
        return secondsPassed * Double.parseDouble(String.valueOf(miners.get(userid)));
    }
    public static double min(double a, double b) {
        return a < b ? a : b;
    }
}
