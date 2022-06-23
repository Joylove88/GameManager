package com.gm.common.utils;

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
}
