package com.gm.common.utils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2022/6/12 0012.
 */
public class CalculateTradeUtil {
    // 1个矿工可以孵化的鸡蛋(代币)数量 （1天有86400秒，被放大了1倍）
    public static BigDecimal EGGS_TO_HATCH_1MINERS = BigDecimal.valueOf(86400);
    public static BigDecimal EGGS_TO_HATCH_1MINERS30 = BigDecimal.valueOf(86400*30);
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
    // 获取玩家可获取的金币
    public static BigDecimal userGetGold = BigDecimal.valueOf(0);
    // 玩家赚取总收入
    public static BigDecimal totalPlayersGold = BigDecimal.valueOf(0);


    public static BigDecimal time = Arith.divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000));

    /// @dev 奖励复投
    private static BigDecimal hatchEggs(BigDecimal changePower) {
        // 获取我的鸡蛋
        BigDecimal eggsUsed = eggsBought;
        // 计算新的矿工数量
        BigDecimal newMiners= Arith.divide(eggsUsed,CalculateTradeUtil.EGGS_TO_HATCH_1MINERS30);
        if (changePower.compareTo(BigDecimal.ZERO) == 1) {
            miners = Arith.add(miners, newMiners);
            // 消弱矿工囤积
            marketEggs = Arith.add(marketEggs, Arith.divide(eggsUsed, BigDecimal.valueOf(5)));
        } else if (changePower.compareTo(BigDecimal.ZERO) == -1) {
            miners = Arith.subtract(miners, newMiners);
            // 消弱矿工囤积
            marketEggs = Arith.add(marketEggs, eggsUsed);
        }

        return miners;
    }
    /// @dev 计算可产出收益
    public static void sellEggs() {
        // 获取我的鸡蛋
        BigDecimal hasEggs = getMyEggs(); // 8640000000
        // 计算可以获取的奖励
        BigDecimal eggValue = calculateEggSell(hasEggs);
        // 获取用户可获取的金币
        userGetGold = eggValue;
        // 资金池减少
        FundPool = Arith.subtract(FundPool,userGetGold);
        System.out.println("玩家一天的最大可产出收益: " + userGetGold);
        System.out.println("资金池剩余: " + FundPool);
        System.out.println("eggValue: " + eggValue);
        marketEggs = Arith.add(marketEggs, hasEggs);
    }

    // eggs = getMyEggs
    // sun = calculateEggSell(eggs)
    // fee = devFee(sun)
    // 可以提取的奖励=sun - fee
    private static BigDecimal calculateEggSell(BigDecimal eggs) {
        return CalculateTradeUtil.calculateTrade(eggs,marketEggs,FundPool);
    }


    // 更新矿工
    public static BigDecimal updateMiner(BigDecimal changePower) {
        // 如果改变的值为0 则跳出
        if (changePower.compareTo(BigDecimal.valueOf(0)) == 0) return BigDecimal.valueOf(0);
        eggsBought = calculateEggBuy(changePower.abs(), FundPool);
        return hatchEggs(changePower);
    }

    // 矿工兑换数量比例
    public static BigDecimal calculateRateOfMinter(BigDecimal userPower) {
        BigDecimal eggsBought = calculateEggBuy(userPower, FundPool);
        return Arith.divide(eggsBought,CalculateTradeUtil.EGGS_TO_HATCH_1MINERS30);
    }

    private static BigDecimal calculateEggBuy(BigDecimal userPower, BigDecimal totalPowerAndFundPool) {
        return CalculateTradeUtil.calculateTrade(userPower,totalPowerAndFundPool, marketEggs);
    }

    private static BigDecimal getMyEggs() {
        return Arith.add(eggsBought, getEggsSinceLastHatch());
    }
    private static BigDecimal getEggsSinceLastHatch() {
//        BigDecimal secondsPassed = CalculateTradeUtil.min(CalculateTradeUtil.EGGS_TO_HATCH_1MINERS, Arith.subtract(time ,lastHatch));
        return Arith.multiply(CalculateTradeUtil.EGGS_TO_HATCH_1MINERS, miners);
    }

    // 经济平衡算法
    private static BigDecimal calculateTrade(BigDecimal rt, BigDecimal rs, BigDecimal bs){
        BigDecimal a = Arith.multiply(PSN, bs);
        BigDecimal b = Arith.divide(
                Arith.add(
                        Arith.multiply(PSN, rs),
                        Arith.multiply(PSNH, rt)
                ),
                rt);

        BigDecimal c = Arith.add(PSNH, b

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

    public static void main(String[] args) {
        BigDecimal rate = BigDecimal.valueOf(1);
        BigDecimal power = BigDecimal.valueOf(355);
        marketEggs = new BigDecimal("1803875022.049365");
        System.out.println("marketEggs:"+marketEggs);
        FundPool = BigDecimal.valueOf(9999.646475);
        System.out.println("FundPool:"+FundPool);
        System.out.println("oracle:"+calculateRateOfMinter(rate));
        System.out.println("minter:"+updateMiner(power));
        System.out.println("minter1:"+calculateRateOfMinter(rate));
    }
}
