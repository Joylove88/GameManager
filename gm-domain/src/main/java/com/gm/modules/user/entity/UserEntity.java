package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-08 16:26:15
 */
@Data
@TableName("GM_USER")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long userId;
	/**
	 * 用户游戏名
	 */
	private String userName;
	/**
	 * 用户钱包地址
	 */
	private String userWalletAddress;
	/**
	 * 用户nonce
	 */
	private Double nonce;
	/**
	 * 签名日期
	 */
	private Date signDate;
	/**
	 * 用户级别
	 */
	private Long userLevel;
	/**
	 * 用户类型:普通0，1代理商
	 */
	private String userType;
	/**
	 * 客户端ip
	 */
	private String clientIp;
	/**
	 * 状态：0失效，1生效
	 */
	private String status;
	/**
	 * 账户状态:0冻结,1正常
	 */
	private String accountStatus;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建时间
	 */
	private Long createTimeTs;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 更新时间
	 */
	private Long updateTimeTs;
	/**
	 * 推广码
	 */
	private String expandCode;
	/**
	 * 设备类型
	 */
	private String deviceType;
	/**
	 * 创建人
	 */
	private Long createUser;
	/**
	 * 修改人
	 */
	private Long updateUser;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 头像url
	 */
	private String imgUrl;
	/**
	 * 用户在线状态：00离线，01：PC在线，02：H5在线，03：android在线，04：iOS在线
	 */
	private String onlineFlag;

}
