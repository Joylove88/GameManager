package com.gm.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:10:34
 */
@Data
@TableName("GM_USER_HERO")
public class UserHeroEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long userHeroId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * NFT_TOKENID
	 */
	private Long nftId;
	/**
	 * 用户获得的英雄ID
	 */
	private Long heroStarId;
	/**
	 * 用户获得的英雄ID
	 */
	private Long heroId;
	/**
	 * 星级
	 */
	private Integer starCode;
	/**
	 * 矿工数
	 */
	private BigDecimal minter;
	/**
	 * 神谕值
	 */
	private BigDecimal oracle;
	/**
	 * 矿工兑换比例（增幅,削减）
	 */
	private Double scale;
	/**
	 * 成长率
	 */
	private Double growthRate;
	/**
	 * 皮肤类型0普通1黄金...
	 */
	private Integer skinType;
	/**
	 * 英雄战力
	 */
	private Double heroPower;
	/**
	 * 累计获得的经验
	 */
	private Long experienceObtain;
	/**
	 * 铸造HASH
	 */
	private String mintHash;
	/**
	 * 初始生命值
	 */
	private Double health;
	/**
	 * 初始法力值
	 */
	private Double mana;
	/**
	 * 初始生命值恢复
	 */
	private Double healthRegen;
	/**
	 * 初始法力值恢复
	 */
	private Double manaRegen;
	/**
	 * 初始护甲
	 */
	private Double armor;
	/**
	 * 初始魔抗
	 */
	private Double magicResist;
	/**
	 * 初始攻击力
	 */
	private Double attackDamage;
	/**
	 * 初始法攻
	 */
	private Double attackSpell;
	/**
	 * 铸造状态('0':铸造中，'1':铸造成功，'2':铸造失败)
	 */
	private String mintStatus;
	/**
	 * 状态('0':禁用，'1':启用)
	 */
	private String status;
	/**
	 * 是否绑定('0':否，'1':是)
	 */
	private String isBind;
	/**
	 * 上阵状态('0':未上阵，'1':上阵中)
	 */
	private String statePlay;
	/**
	 * 英雄等级ID
	 */
	private Long heroLevelId;
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
	/**
	 * 英雄名称
	 */
	@TableField(exist = false)
	private String heroName;
	/**
	 * 玩家名称
	 */
	@TableField(exist = false)
	private String userName;
	/**
	 * 等级名称
	 */
	@TableField(exist = false)
	private String levelName;
	/**
	 * 英雄等级编码
	 */
	@TableField(exist = false)
	private String levelCode;
	/**
	 * 英雄图片地址
	 */
	@TableField(exist = false)
	private String heroImgUrl;
	/**
	 * 英雄图标地址
	 */
	@TableField(exist = false)
	private String heroIconUrl;
	/**
	 * 英雄龙骨地址
	 */
	@TableField(exist = false)
	private String heroKeelUrl;
	/**
	 * 英雄描述
	 */
	@TableField(exist = false)
	private String heroDescription;

}
