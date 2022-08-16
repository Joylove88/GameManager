package com.gm.common.utils;

import sun.font.BidiUtils;

import java.math.BigDecimal;

/**
 * 金额计算工具类
 */
public class Arith {

    /**
     * 数字1
     */
    public static final BigDecimal one = new BigDecimal("1");
    /**
     * 数字0
     */
    public static final BigDecimal zero = new BigDecimal("0");
    /**
     * 默认保留6位小数
     */
    private static final int scale = 6;
    /**
     * 四舍五入
     */
    private static final int roundingMode = 4;

    /**
     * 相减
     */
    public static BigDecimal subtract(BigDecimal b1, BigDecimal b2) {
        return b1.subtract(b2);
    }

    /**
     * 相加
     */
    public static BigDecimal add(BigDecimal b1, BigDecimal b2) {
        return b1.add(b2);
    }

    /**
     * 相乘
     */
    public static BigDecimal multiply(BigDecimal b1, BigDecimal b2) {
        return b1.multiply(b2).divide(one, scale, roundingMode);
    }

    /**
     * 相除
     */
    public static BigDecimal divide(BigDecimal b1, BigDecimal b2) {
        return b1.divide(b2, scale, roundingMode);
    }

    /**
     * 获取范围内随机数 十分位
     */
    public static Double randomWithinRangeTen(Double max, Double min){
        int pow = (int) Math.pow(10, 1); // 用于提取指定小数位
        return Math.floor((Math.random() * (max - min) + min) * pow) / pow;
    }
    /**
     * 获取范围内随机数 百分位
     */
    public static Double randomWithinRangeHundred(Double max, Double min){
        int pow = (int) Math.pow(100, 1); // 用于提取指定小数位
        Double n = Math.floor((Math.random() * (max - min) + min) * pow) / pow;
        n = n > 1 ? 1 : n;
        return n;
    }

    /**
     * 随机20位整数
     */
    public static BigDecimal UUID20(){
        int randomId= (int) (Math.random() * (9999999-1000000 + 1) ) + 1000000;
        return new BigDecimal(System.currentTimeMillis() + "" + randomId);
    }

    public static void main(String[] args) {
        System.out.println(UUID20());
    }
}
