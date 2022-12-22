package com.gm.modules.market.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 市场在售物品
 * 
 * @author market
 * @email market@gmail.com
 * @date 2022-12-16 15:18:38
 */
@Data
@TableName("gm_market_online")
public class GmMarketOnlineEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 售卖者
	 */
	private Long userId;
	/**
	 * 物品类型（0：英雄，1：英雄碎片，2：装备，3：装备卷轴，4：药水）
	 */
	private String goodsType;
	/**
	 * 商品ID
	 */
	private Long goodsId;
	/**
	 * 状态(0：上架，1：已售，2：下架)
	 */
	private Integer status;
	/**
	 * 售卖价格
	 */
	private BigDecimal sellPrice;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建时间
	 */
	private Long createTimeTs;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 修改时间
	 */
	private Long updateTimeTs;

}
