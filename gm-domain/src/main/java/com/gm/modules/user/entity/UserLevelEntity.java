package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家等级表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-07-02 15:28:42
 */
@Data
@TableName("gm_user_level")
public class UserLevelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 等级名称
	 */
	private String levelName;
	/**
	 * 等级描述
	 */
	private String levelDesc;
	/**
	 * 等级编码
	 */
	private Long levelCode;
	/**
	 * 是否默认，1是，0否
	 */
	private String flag;
	/**
	 * 是否删除，1是，0否
	 */
	private String deleted;
	/**
	 * 晋级到下一级所需经验值
	 */
	private Long promotionExperience;
	/**
	 * 升级所需累计经验
	 */
	private Long experienceTotal;
	/**
	 * 创建人
	 */
	private String createUser;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建时间
	 */
	private Long createTimeTs;
	/**
	 * 修改人
	 */
	private String updateUser;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;

}
