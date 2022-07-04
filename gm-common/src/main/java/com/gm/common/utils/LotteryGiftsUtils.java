/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.common.utils;

import java.util.*;

/**
 * 抽奖算法
 *
 * @author Mark sunlightcs@gmail.com
 */
public class LotteryGiftsUtils {
    /**
     * 抽奖
     *
     * @param orignalRates 原始的概率列表，保证顺序和实际物品对应
     * @return 物品的索引
     */
    public static int lottery(List<Double> orignalRates) {
        if (orignalRates == null || orignalRates.isEmpty()) {
            return -1;
        }

        int size = orignalRates.size();

        // 计算总概率，这样可以保证不一定总概率是1
        double sumRate = 0d;
        for (double rate : orignalRates) {
            sumRate += rate;
        }

        // 计算每个物品在总概率的基础下的概率情况
        List<Double> sortOrignalRates = new ArrayList<Double>(size);
        Double tempSumRate = 0d;
        for (double rate : orignalRates) {
            tempSumRate += rate;
            sortOrignalRates.add(tempSumRate / sumRate);
        }

        // 根据区块值来获取抽取到的物品索引
        double nextDouble = Math.random();
        sortOrignalRates.add(nextDouble);
        Collections.sort(sortOrignalRates);

        return sortOrignalRates.indexOf(nextDouble);
    }

    public static Map<Integer, Integer> gifPron(List<Double> orignalRates,long Num){
        // statistics
        Map<Integer, Integer> count = new HashMap<Integer, Integer>();
        //抽奖次数
//        // 如果类型为1 说明是单抽模式.类型为2 说明是十连抽模式
//        double num = type == 2 ? Constant.drawTen : Constant.drawOne;
        for (int d = 0; d < Num; d++) {
            int orignalIndex = lottery(orignalRates);

            //出现相同奖品则过滤掉并增加抽奖次数。
            Integer value = count.get(orignalIndex);
            if (value != null){
                Num++;
            }
            count.put(orignalIndex, value == null ? 1 : value + 1);
        }
        return count;
    }

}
