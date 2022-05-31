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
    static BigDecimal EGGS_TO_HATCH_1MINERS = BigDecimal.valueOf(864000);
    static BigDecimal PSN = BigDecimal.valueOf(10000);
    static BigDecimal PSNH = BigDecimal.valueOf(5000);
    // 用户矿工数量
    static Map<String,BigDecimal> miners = new HashMap<>();
    // 用户鸡蛋数量
    static Map<String,BigDecimal> claimedEggs = new HashMap<>();
    // 用户上次购买矿工的时间
    static Map<String,BigDecimal> lastHatch = new HashMap<>();
    // 用户的推荐人
    String referrals;
    // 初始市场总鸡蛋数量
    static BigDecimal marketEggs = BigDecimal.valueOf(864000000l);
    // 总资金
    static BigDecimal FundPool = BigDecimal.valueOf(100000);

    static BigDecimal time = divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000));

    static Map<String,BigDecimal> userpower =  new HashMap<>(); //用户战力
    static BigDecimal totalPower = BigDecimal.valueOf(100000); // 主资金池余额
    static BigDecimal[] maplevel = {BigDecimal.valueOf(0.05)};
    static String[] userids = {"a0","a1","a2"};
//    static long totalPower = 19000;

    public static BigDecimal calculateCombatPower(String userid) {
        System.out.println("totalPower====："+totalPower);
        return BigDecimal.valueOf(Long.parseLong(userpower.get(userid).toString()));
    }
    public static long power(){
        Random random = new Random();
        int number = random.nextInt(10)+1;
        return number;
    }
    public static void main(String[] args) {
//        totalPower = multiply(totalPower,maplevel[0]);
        FundPool = multiply(FundPool,maplevel[0]);
        for (int i = 0 ; i < 1000; i++){
            //购买
            time = divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000));
            miners.put("a"+i,BigDecimal.valueOf(0));
            lastHatch.put("a"+i,time);
            claimedEggs.put("a"+i,BigDecimal.valueOf(0));

            userpower.put("a"+i,BigDecimal.valueOf(100));
            buyEggs("a"+i);
            System.out.println("marketEggs:" + marketEggs);
        }
//        FundPool = totalPower;
        for(int j = 0 ; j < 30; j++){
            System.out.println("蒂"+j+"天");
            //购买
            time = add(divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000)),multiply(EGGS_TO_HATCH_1MINERS,BigDecimal.valueOf(j)));
            for (int i = 0 ; i < 1000; i++){
                System.out.println("marketEggs:" + marketEggs);
                System.out.println("miners:" + miners.get("a"+i));
                System.out.println("lastHatch:" + lastHatch.get("a"+i));

                sellEggs("a"+i);
            }
        }
        System.out.println("totalPower:"+totalPower);

    }


    /// @dev 奖励复投
    public static void hatchEggs(String userid) {
        if(userid == userid) {
//            ref = address(0);
        }
//        if(referrals == address(0) && referrals!=userid){
//            referrals[msg.sender] = ref;
//        }
        BigDecimal eggsUsed = getMyEggs(userid);
        BigDecimal newMiners= divide(eggsUsed,EGGS_TO_HATCH_1MINERS);
        miners.put(userid,add(miners.get(userid) , newMiners));
        System.out.println("claimedEggs:" + claimedEggs.get(userid));
//        claimedEggs.put(userid,0);
        lastHatch.put(userid, time);

        // 奖励的1/10分给推荐人
//        claimedEggs[referrals[msg.sender]] = claimedEggs[referrals[msg.sender]] + eggsUsed / 10;

        // 消弱矿工囤积
        marketEggs = add(marketEggs,divide(eggsUsed,BigDecimal.valueOf(5)));
    }
    /// @dev 取出奖励
    public static void sellEggs(String userid) {
        BigDecimal hasEggs = getMyEggs(userid); // 8640000000
        BigDecimal eggValue = calculateEggSell(hasEggs);

        BigDecimal powerRate = divide(eggValue, totalPower);

        BigDecimal userGetGold = multiply(powerRate, FundPool);

        FundPool = subtract(FundPool,userGetGold);
        System.out.println("powerRate:"+powerRate);
        System.out.println("userGetGold:"+userGetGold);
        System.out.println("FundPool:"+FundPool);
//        BigDecimal fee = devFee(eggValue);
        totalPower = subtract(totalPower ,eggValue);
//        System.out.println("fee:"+fee);
        System.out.println("eggValue:"+eggValue);
        claimedEggs.put(userid,BigDecimal.valueOf(0));
        lastHatch.put(userid, time);
        marketEggs = add(marketEggs,hasEggs);
    }

    // eggs = getMyEggs
    // sun = calculateEggSell(eggs)
    // fee = devFee(sun)
    // 可以提取的奖励=sun - fee
    public static BigDecimal calculateEggSell(BigDecimal eggs) {
        return calculateTrade(eggs,marketEggs,totalPower);
    }

    // 贸易平衡算法
    public static BigDecimal calculateTrade(BigDecimal rt,BigDecimal rs, BigDecimal bs){
//        return (PSN*bs)/(PSNH+((PSN*rs+PSNH*rt)/rt));
        BigDecimal a = multiply(PSN,bs);
        BigDecimal b = divide(
                add(
                        multiply(PSN,rs),
                        multiply(PSNH,rt)
                ),
                rt);

        BigDecimal c = add(PSNH,b

        );

        return divide(
                a,
                c
        );
    }

    /// @dev 购买矿工
    public static void buyEggs(String userid) {
        System.out.println("CombatPower:"+calculateCombatPower(userid));
        totalPower = add(totalPower, calculateCombatPower(userid));
        BigDecimal eggsBought = calculateEggBuy(calculateCombatPower(userid), subtract(totalPower , calculateCombatPower(userid)));
//        eggsBought = subtract(eggsBought , devFee(eggsBought));
//        BigDecimal fee = devFee(calculateCombatPower(userid));
//        System.out.println("buyFee:"+fee);
        claimedEggs.put(userid, add(claimedEggs.get(userid) ,eggsBought));
        hatchEggs(userid);
    }

    public static BigDecimal calculateEggBuy(BigDecimal eth, BigDecimal contractBalance) {
        return calculateTrade(eth,contractBalance,marketEggs);
    }
    // 购买矿工兑换数量比例
//    public static long calculateEggBuySimple(BigDecimal eth) {
//        return calculateEggBuy(eth,totalPower);
//    }
//    public static BigDecimal devFee(BigDecimal amount) {
//        return divide(multiply(amount ,BigDecimal.valueOf(10)) , BigDecimal.valueOf(100));
//    }

    public static BigDecimal getMyEggs(String userid) {
        return add(claimedEggs.get(userid),getEggsSinceLastHatch(userid));
    }
    public static BigDecimal getEggsSinceLastHatch(String userid) {
        BigDecimal secondsPassed = min(EGGS_TO_HATCH_1MINERS, subtract(time ,lastHatch.get(userid)));
        return multiply(secondsPassed, miners.get(userid));
    }
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        int flag = a.compareTo(b);
        return flag == -1 ? a : b;
    }




    /**
     * @return java.math.BigDecimal 总和
     * 示例：BigDecimalUtils.add(参数,参数,参数,参数,...);
     * @Description 加法运算
     * @Param [param] 可变长度数组，把需要计算的数值填进来
     * @Author Lucky
     * @Date 2021/10/21
     */
    public static BigDecimal add(BigDecimal... param) {
        BigDecimal sumAdd = BigDecimal.valueOf(0);
        for (int i = 0; i < param.length; i++) {
            BigDecimal bigDecimal = param[i] == null ? new BigDecimal(0) : param[i];
            sumAdd = sumAdd.add(bigDecimal);
        }
        return sumAdd;
    }

    /**
     * 默认保留6位小数
     */
    private static final int scale = 6;
    /**
     * 四舍五入
     */
    private static final int roundingMode = 4;
    /**
     * 相除
     */
    public static BigDecimal divide(BigDecimal b1, BigDecimal b2) {
        return b1.divide(b2, scale, roundingMode);
    }

    /**
     * @return java.math.BigDecimal 计算结果
     * 示例：BigDecimalUtils.subtract(被减数,减数,减数,减数,...);
     * @Description 加法运算 如果被减数为null 结果就为0
     * @Param [param] 第一个为被减数 可以传入多个 因为参数是一个可变长度的数组
     * @Author Lucky
     * @Date 2021/10/21
     */
    public static BigDecimal subtract(BigDecimal... param) {
        BigDecimal sumLess = param[0];//被减数
        if (sumLess == null) return new BigDecimal(0);
        for (int i = 1; i < param.length; i++) {
            BigDecimal bigDecimal = param[i] == null ? new BigDecimal(0) : param[i];
            sumLess = sumLess.subtract(bigDecimal);
        }
        return sumLess;
    }

    /**
     * @return java.math.BigDecimal 计算结果 保留小数点后两位 规则为四舍五入
     * @Description 乘法运算 如一方参数为0或者null计算结果为0
     * @Param [first, last]
     * @Author Lucky
     * @Date 2021/10/21
     */
    public static BigDecimal multiply(BigDecimal first, BigDecimal last) {
        if (first == null) first = new BigDecimal(0);
        if (last == null) last = new BigDecimal(0);
        return first.multiply(last).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
