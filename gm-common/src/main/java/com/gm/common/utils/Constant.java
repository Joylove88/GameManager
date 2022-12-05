/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.common.utils;

import java.math.BigDecimal;

/**
 * 常量
 *
 * @author Mark sunlightcs@gmail.com
 */
public class Constant {
	/** 超级管理员ID */
	public static final int SUPER_ADMIN = 1;
    /** 数据权限过滤 */
	public static final String SQL_FILTER = "sql_filter";
   //前端类型
    public static final String USER_AGENT = "User-Agent";

    public static final String FRONT_TYPE_WEB = "web";
    public static final String FRONT_TYPE_H5 = "h5";
    public static final String FRONT_TYPE_ANDROID = "android";
    public static final String FRONT_TYPE_IOS = "ios";
    public static final String FRONT_TYPE_PC = "pc";
    /**
     * 市场总鸡蛋数量
     */
    public static final String MARKET_EGGS = "market_eggs";
    /**
     * 区块号
     */
    public static final String BLOCK_NUMBER = "block_number";

    /**
     * 玩家赚取总收入
     */
    public static final String PLAYERS_EARN_TOTAL_REVENUE = "players_earn_total_revenue";
   // 副本资金池余额（最新）
    public static final String DUNGEON_POOLING_BALANCE = "dungeon_pooling_balance";
    /**
     * 合约事件名称
     */
    public static final String EVENT_NAME = "Transfer(address,address,uint256)";

    /**
     *  0long
     */
    public static final Long ZERO = 0L;
    /**
     *  0int
     */
    public static final int ZERO_I = 0;
    /**
     *  0d
     */
    public static final Double ZERO_D = 0d;
    /**
     *  0字符串
     */
    public static final String ZERO_ = "0";
    /**
     *  1字符串
     */
    public static final String ONE_ = "1";
    /**
     *  玩家默认最大体力值
     */
    public static final Long FTG = 60L;
    /**
     *  英雄属性稀释
     */
    public static final Double HERO_ATTRIBUTE_RATE = 0.1;
//    /**
//     * 所有权
//     */
//    public static final String ADDRESS = "0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45";
//    /**
//     * BUSD
//     */
//    public static final String BUSD_ADDRESS = "0xeD24FC36d5Ee211Ea25A80239Fb8C4Cfd80f12Ee";
    /**
     * 资金池地址
     */
    public static final String CAPITAL_POOL_ADDRESS = "0xb1EE547128A61E3941aC26374038ecB79a3A21B5";
    /**
     * 英雄合约地址
     */
    public static final String NFT_HERO_ADDRESS = "0x25B15dE515eBBD047e026D64463801f044785cc6";
    /**
     * 经验合约地址
     */
    public static final String NFT_EX_ADDRESS = "0x50E39B4B42893c95A0797dbc0EDFB8Ec236620f4";
    /**
     * 装备合约地址
     */
    public static final String NFT_EQUIP_ADDRESS = "0x50E39B4B42893c95A0797dbc0EDFB8Ec236620f4";
    /**
     * 链上状态0
     */
    public static final String CHAIN_STATE0 = "0x0";
    /**
     * 链上状态1
     */
    public static final String CHAIN_STATE1 = "0x1";
    /**
     * hex zero
     */
    public static final String HEX_ZERO = "000000000000000000000000";
    /**
     * 1小时的毫秒数
     */
    public static final long HOUR1 = 1000 * 60 * 60;
    /**
     * 12小时的毫秒数
     */
    public static final long HOUR12 = 1000 * 12 * 60 * 60;
    /**
     * 24小时的毫秒数
     */
    public static final long HOUR24 = 1000 * 24 * 60 * 60;
    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";
    /**
     *  启用状态
     */
    public static final String enable = "1";
    /**
     *  禁用or待处理状态
     */
    public static final String disabled = "0";
    /**
     *  失败状态
     */
    public static final String failed = "2";
    /**
     *  已使用状态
     */
    public static final String used = "2";

	/**
	 * 菜单类型
	 */
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * 定时任务状态
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
    	NORMAL(0),
        /**
         * 暂停
         */
    	PAUSE(1);

        private int value;
        ScheduleStatus(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;
        CloudService(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * 账变类型
     */
    public enum TradeType {
        WITHDRAW("00"),//提现
        DUNGEON_REVENUE("01"),//副本产出
        DRAW_HERO("02"),//英雄召唤
        DRAW_EQUIP("03"),//装备召唤
        DRAW_EXP("04"),//经验召唤
        WITHDRAW_SERVICE("12"),//提现手续费
        WITHDRAW_FREEZE("16"),//后台提现冻结
        WITHDRAW_THAW("19"),//后台提现解冻
        WITHDRAW_SUCCESS("14");//后台提现成功
        private String value;
        TradeType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 召唤类型('1':英雄，'2':装备，'3':药水)
     */
    public enum SummonType {
        HERO("1"),//英雄
        EQUIPMENT("2"),//装备
        EXPERIENCE("3");//药水
        private String value;
        SummonType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 召唤次数
     */
    public enum SummonNum {
        NUM1(1),
        NUM2(2),
        NUM3(3),
        NUM4(4),
        NUM5(5),
        NUM6(6),
        NUM7(7),
        NUM8(8),
        NUM9(9),
        NUM10(10);
        private int value;
        SummonNum(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * 战斗状态
     */
    public enum BattleState {
        _IDLE("0"),// 未战斗
        _IN_BATTLE("1"),// 战斗中
        _BATTLE_IS_OVER("2");// 战斗结束
        private String value;
        BattleState(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 战斗结果
     */
    public enum BattleResult {
        _LOSE("0"),// LOSE
        _WIN("1");// WIN
        private String value;
        BattleResult(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 技能类型
     */
    public enum SkillType {
        _ATTACK("0"),// 输出
        _SUP("1"),// 恢复
        _SUP_ADD("2");// 加成
        private String value;
        SkillType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 怪物技能名称
     */
    public enum SkillNameM {
        LV1("[暗影一击]"),// 一级副本怪物专属技能
        LV2("[梦魇诅咒]"),// 二级副本怪物专属技能
        LV3("[狱火炼魂]"),// 三级副本怪物专属技能
        LV4("[邪龙之怒]"),// 四级副本怪物专属技能
        LV5("[神技·九天神雷]");// 五级副本怪物专属技能

        private String value;
        SkillNameM(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 装备稀有度
     */
    public enum RareCode {
        _WHITE("1"),// 稀有度白色
        _GREEN("2"),// 稀有度绿色
        _BLUE("3"),// 稀有度蓝色
        _PURPLE("4"),// 稀有度紫色
        _ORANGE("5");// 稀有度橙色

        private String value;
        RareCode(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 货币类型
     */
    public enum CurrencyType {
        _GOLD_COINS("0"),// 金币
        _CRYPTO("1");// 加密货币

        private String value;
        CurrencyType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 资金池
     */
    public enum CashPool {
        _MAIN("main_cash_pool"),// 主资金池余额
        _USER("user_cash_pool"),// 用户资金池
        _DUNGEON("dungeon_cash_pool"),// 副本资金池
        _REPO("repo_cash_pool"),// 回购资金池
        _TEAM("team_cash_pool");// 团队抽成资金池

        private String value;
        CashPool(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 资金池分配比例
     */
    public enum CashPoolScale {
        _USER(BigDecimal.valueOf(0)),// 用户比例
        _DUNGEON(BigDecimal.valueOf(0.75)),// 副本比例
        _REPO(BigDecimal.valueOf(0.2)),//回购比例
        _TEAM(BigDecimal.valueOf(0.05));// 团队比例

        private BigDecimal value;
        CashPoolScale(BigDecimal value) {
            this.value = value;
        }
        public BigDecimal getValue() {
            return value;
        }
    }

    /**
     * 数量
     */
    public enum Quantity {
        _ONE(1),
        _TWO(2),
        _THREE(3),
        _FOUR(4),
        _FIVE(5);

        private Integer value;
        Quantity(Integer value) {
            this.value = value;
        }
        public Integer getValue() {
            return value;
        }
    }

    /**
     * 皮肤类型
     */
    public enum SkinType {
        ORIGINAL(0),// 原始
        GOLD(1),// 黄金
        DIAMOND(2);// 钻石

        private Integer value;
        SkinType(Integer value) {
            this.value = value;
        }
        public Integer getValue() {
            return value;
        }
    }

    /**
     * 概率等级
     */
    public enum PrLv {
        PrLv1(1),
        PrLv2(2),
        PrLv3(3),
        PrLv4(4),
        PrLv5(5),
        PrLv6(6),
        PrLv7(7),
        PrLv8(8),
        PrLv9(9),
        PrLv10(10);

        private int value;
        PrLv(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * 星级
     */
    public enum StarLv {
        Lv1(1),
        Lv2(2),
        Lv3(3),
        Lv4(4),
        Lv5(5);

        private int value;
        StarLv(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * 获取类型（0：副本，1：召唤）
     */
    public enum FromType {
        DUNGEON("0"),
        SUMMON("1");

        private String value;
        FromType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 碎片类型（0：成品，1：碎片）
     */
    public enum FragType {
        WHOLE(0),
        FRAG(1);

        private int value;
        FragType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * 装备属性随机最大最小百分比
     */
    public enum EquipStatsRange {
        MIN(0.65),// 召唤产出
        MAX(1.01),// 召唤产出
        MIN_FREE(0.4),// 副本产出
        MAX_FREE(0.81);// 副本产出

        private double value;
        EquipStatsRange(double value) {
            this.value = value;
        }
        public double getValue() {
            return value;
        }
    }

    /**
     * 提现状态（0：申请提现，1：审核通过，2：审核失败，3:提现中，4：提现成功，5：提现失败）
     */
    public enum WithdrawStatus {
        APPLY(0),
        CHECK_SUCCESS(1),
        CHECK_FAIL(2),
        ING(3),
        SUCCESS(4),
        FAIL(5);

        private int value;
        WithdrawStatus(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

}
