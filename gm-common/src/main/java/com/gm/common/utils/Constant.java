/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.common.utils;

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

    // 装备属性随机最大百分比
    public static final Double eqAttMax = 1.01;
    // 装备属性随机最小百分比
    public static final Double eqAttMin = 0.5;
    // 装备属性随机最小百分比(抽奖)
    public static final Double eqAttCryptoMin = 0.5;
    /**
     * 合约事件名称
     */
    public static final String EVENT_NAME = "Transfer(address,address,uint256)";

    /**
     *  0
     */
    public static final Long ZERO = 0L;
    /**
     *  0d
     */
    public static final Double ZERO_D = 0d;
    /**
     *  0值
     */
    public static final String ZERO_ = "0";
    /**
     *  玩家默认最大体力值
     */
    public static final Long FTG = 60L;
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
     *  概率等级1
     */
    public static final double pronOne = 15;
    /**
     *  概率等级2
     */
    public static final double pronTwo = 5;
    /**
     *  概率等级3
     */
    public static final double pronThree = 1;
    /**
     *  概率等级4
     */
    public static final double pronFour = 0.08;
    /**
     *  概率等级5
     */
    public static final double pronFive = 0.001;

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
        DRAW_HERO("02"),//英雄抽奖
        DRAW_EQUIP("03"),//装备抽奖
        DRAW_EXP("04"),//经验抽奖
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
     * 物品类型
     */
    public enum ItemType {
        HERO("1"),//英雄
        EQUIPMENT("2"),//装备
        EXPERIENCE("3");//药水
        private String value;

        ItemType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    /**
     * 抽奖次数
     */
    public enum DrawNum {
        DRAW1(1),
        DRAW2(2),
        DRAW3(3),
        DRAW4(4),
        DRAW5(5),
        DRAW6(6),
        DRAW7(7),
        DRAW8(8),
        DRAW9(9),
        DRAW10(10);
        private int value;

        DrawNum(int value) {
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
        _IDLE("0"), // 未战斗
        _IN_BATTLE("1"), // 战斗中
        _BATTLE_IS_OVER("2"); // 战斗结束
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
        _LOSE("0"), // LOSE
        _WIN("1"); // WIN
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
        _ATTACK("0"), // 输出
        _SUP("1"), // 恢复
        _SUP_ADD("2"); // 加成
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
        LV1("[暗影一击]"), // 一级副本怪物专属技能
        LV2("[梦魇诅咒]"), // 二级副本怪物专属技能
        LV3("[狱火炼魂]"), // 三级副本怪物专属技能
        LV4("[邪龙之怒]"), // 四级副本怪物专属技能
        LV5("[神技·九天神雷]"); // 五级副本怪物专属技能

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
        _WHITE("1"), // 稀有度白色
        _GREEN("2"), // 稀有度绿色
        _BLUE("3"), // 稀有度蓝色
        _PURPLE("4"), // 稀有度紫色
        _ORANGE("5"); // 稀有度橙色

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
        _GOLD_COINS("0"), // 金币
        _CRYPTO("1"); // 加密货币

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
        _MAIN("main_cash_pool"), // 主资金池余额
        _USER("user_cash_pool"), // 用户资金池
        _DUNGEON("dungeon_cash_pool"), // 副本资金池
        _REPO("repo_cash_pool"), // 回购资金池
        _TEAM("team_cash_pool"); // 团队抽成资金池

        private String value;

        CashPool(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
