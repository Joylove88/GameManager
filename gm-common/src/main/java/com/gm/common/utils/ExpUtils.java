package com.gm.common.utils;

/**
 * 经验值计算工具类
 */
public class ExpUtils {
    /**
     * 获取当前等级阶段的经验值
     * @param experienceTotal 升级所需累计经验
     * @param promotionExperience 晋级到下一级所需经验值
     * @param experienceObtain 累计获得的经验
     * @return
     */
    public static Long getCurrentExp(Long experienceTotal, Long promotionExperience, Long experienceObtain){
        Long exp = experienceTotal - experienceObtain;
        exp = promotionExperience - exp;
        System.out.println(exp);
        return exp;
    }

    public static void main(String[] args) {
        System.out.println(getCurrentExp(2340L, 270L, 2341L));
    }
}
