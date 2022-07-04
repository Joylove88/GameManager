package com.gm.common.utils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2022/6/12 0012.
 */
public class CalculateTradeUtil {
    // 1个矿工可以孵化的鸡蛋(代币)数量 （1天有86400秒，被放大了1倍）
    public static BigDecimal EGGS_TO_HATCH_1MINERS = BigDecimal.valueOf(86400);
    private static BigDecimal PSN = BigDecimal.valueOf(10000);
    private static BigDecimal PSNH = BigDecimal.valueOf(5000);

    // 初始市场总鸡蛋数量
    public static BigDecimal marketEggs = BigDecimal.valueOf(864000000);
    // 资金池总资金
    public static BigDecimal FundPool = BigDecimal.valueOf(0);
    // 玩家矿工数量
    public static BigDecimal miners = BigDecimal.valueOf(0);
    // 玩家鸡蛋数量
    public static BigDecimal eggsBought = BigDecimal.valueOf(0);
    // 玩家上次购买矿工的时间
    public static BigDecimal lastHatch = BigDecimal.valueOf(0);
    // 获取玩家可获取的金币
    public static BigDecimal userGetGold = BigDecimal.valueOf(0);

    public static BigDecimal time = Arith.divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000));

    public static BigDecimal userpower =  BigDecimal.valueOf(0); // 用户战力
    public static BigDecimal totalPower = BigDecimal.valueOf(0); // 总战力

    // 只有战力变动时触发
    public static void updatePower() {
//
//        // 玩家鸡蛋数量
//        claimedEggs = BigDecimal.valueOf(0);
//        // 获取用户战力
//        userpower = Arith.divide(BigDecimal.valueOf(teamPower),BigDecimal.valueOf(100));
//        // 购买鸡蛋/更新矿工
//        CalculateTradeUtil.updateMiner();
//        System.out.println("marketEggs:" + CalculateTradeUtil.marketEggs);
    }

    /// @dev 奖励复投
    private static void hatchEggs() {
        // 获取我的鸡蛋
        BigDecimal eggsUsed = getMyEggs();
        // 计算新的矿工数量
        BigDecimal newMiners= Arith.divide(eggsUsed,CalculateTradeUtil.EGGS_TO_HATCH_1MINERS);
        miners = Arith.add(miners, newMiners);
        lastHatch = time;

        // 消弱矿工囤积
        marketEggs = Arith.add(marketEggs,Arith.divide(eggsUsed,BigDecimal.valueOf(5)));
    }
    /// @dev 计算可产出收益
    public static void sellEggs() {
        // 获取我的鸡蛋
        BigDecimal hasEggs = getMyEggs(); // 8640000000
        // 计算可以获取的奖励
        BigDecimal eggValue = calculateEggSell(hasEggs);
        // 获取战力率
        BigDecimal powerRate = Arith.divide(eggValue, totalPower);
        // 获取用户可获取的金币
        userGetGold = Arith.multiply(powerRate, FundPool);
        // 资金池减少
        FundPool = Arith.subtract(FundPool,userGetGold);
        System.out.println("powerRate:"+powerRate);
        System.out.println("玩家收益:"+userGetGold);
        System.out.println("资金池剩余:"+FundPool);
        totalPower = Arith.subtract(totalPower ,eggValue);
        System.out.println("eggValue:"+eggValue);
        lastHatch = time;
        marketEggs = Arith.add(marketEggs,hasEggs);
    }

    // eggs = getMyEggs
    // sun = calculateEggSell(eggs)
    // fee = devFee(sun)
    // 可以提取的奖励=sun - fee
    private static BigDecimal calculateEggSell(BigDecimal eggs) {
        return CalculateTradeUtil.calculateTrade(eggs,marketEggs,totalPower);
    }


    // 更新矿工
    public static void updateMiner() {
        System.out.println("CombatPower:" + userpower);
        // 获取系统全部用户全部队伍总战力
        totalPower = Arith.add(totalPower, userpower);
        eggsBought = calculateEggBuy(userpower, Arith.subtract(totalPower , userpower));
        hatchEggs();
    }

    private static BigDecimal calculateEggBuy(BigDecimal eth, BigDecimal contractBalance) {
        return CalculateTradeUtil.calculateTrade(eth,contractBalance,marketEggs);
    }

    private static BigDecimal getMyEggs() {
        return Arith.add(eggsBought,getEggsSinceLastHatch());
    }
    private static BigDecimal getEggsSinceLastHatch() {
//        BigDecimal secondsPassed = CalculateTradeUtil.min(CalculateTradeUtil.EGGS_TO_HATCH_1MINERS, Arith.subtract(time ,lastHatch));
        return Arith.multiply(CalculateTradeUtil.EGGS_TO_HATCH_1MINERS, miners);
    }

    // 贸易平衡算法
    private static BigDecimal calculateTrade(BigDecimal rt, BigDecimal rs, BigDecimal bs){
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

    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        int flag = a.compareTo(b);
        return flag == -1 ? a : b;
    }
}
