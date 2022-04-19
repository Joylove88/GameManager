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
    public static final String BLOCK_NUMBER = "block_number";
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
     * 12小时的毫秒数
     */
    public static final long nh = 1000 * 12 * 60 * 60;
    /**
     * 英雄
     */
    public static final String HERO = "1";
    /**
     * 装备
     */
    public static final String EQUIPMENT = "2";
    /**
     * 药水
     */
    public static final String EXPERIENCE = "3";
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
     *  抽奖次数 单抽
     */
    public static final double drawOne = 1;
    /**
     *  抽奖次数 十连抽
     */
    public static final double drawTen = 10;
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
     *  稀有度白色
     */
    public static final String rareCode1 = "1";
    /**
     *  稀有度绿色
     */
    public static final String rareCode2 = "2";
    /**
     *  稀有度蓝色
     */
    public static final String rareCode3 = "3";
    /**
     *  稀有度紫色
     */
    public static final String rareCode4 = "4";
    /**
     *  稀有度橙色
     */
    public static final String rareCode5 = "5";


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

}
