package com.gm.modules.basicconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-25 14:59:05
 */
@Data
@TableName("GM_STAR_INFO")
public class StarInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long starId;
	/**
	 * 星级级别
	 */
	@NotNull(message = "Star level cannot be empty!")
	private Integer starCode;
	/**
	 * 星级属性加成
	 */
	@NotNull(message = "The star attribute bonus cannot be empty!")
	private Double starBuff;
	/**
	 * 升星所需碎片数量
	 */
	private Integer upStarFragNum;
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
