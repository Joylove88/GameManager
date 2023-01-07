package com.gm.modules.combatStatsUtils.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Arith;
import com.gm.common.utils.CalculateTradeUtil;
import com.gm.common.utils.Constant;
import com.gm.common.utils.ExpUtils;
import com.gm.modules.basicconfig.dto.AttributeEntity;
import com.gm.modules.basicconfig.dto.AttributeSimpleEntity;
import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;
import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;
import com.gm.modules.basicconfig.entity.HeroInfoEntity;
import com.gm.modules.basicconfig.entity.StarInfoEntity;
import com.gm.modules.basicconfig.rsp.HeroLevelRsp;
import com.gm.modules.basicconfig.rsp.HeroSkillRsp;
import com.gm.modules.basicconfig.rsp.TeamInfoRsp;
import com.gm.modules.basicconfig.service.GmTeamConfigService;
import com.gm.modules.basicconfig.service.HeroLevelService;
import com.gm.modules.basicconfig.service.HeroSkillService;
import com.gm.modules.basicconfig.service.StarInfoService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.sys.service.SysDictService;
import com.gm.modules.user.dao.UserHeroEquipmentWearDao;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserEquipmentEntity;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import com.gm.modules.user.rsp.UserHeroFragInfoDetailRsp;
import com.gm.modules.user.rsp.UserHeroFragInfoRsp;
import com.gm.modules.user.rsp.UserHeroInfoDetailRsp;
import com.gm.modules.user.rsp.UserHeroInfoDetailWithGrowRsp;
import com.gm.modules.user.service.UserEquipmentService;
import com.gm.modules.user.service.UserHeroFragService;
import com.gm.modules.user.service.UserHeroService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玩家战斗力业务类
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("combatStatsUtilsService")
public class CombatStatsUtilsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatStatsUtilsService.class);
    @Autowired
    private StarInfoService starInfoService;
    @Autowired
    private UserHeroEquipmentWearDao userHeroEquipmentWearDao;
    @Autowired
    private UserHeroService userHeroService;
    @Autowired
    private UserHeroFragService userHeroFragService;
    @Autowired
    private HeroLevelService heroLevelService;
    @Autowired
    private HeroSkillService heroSkillService;
    @Autowired
    private GmTeamConfigService teamConfigService;
    @Autowired
    private FightCoreService fightCoreService;
    @Autowired
    private UserEquipmentService userEquipmentService;
    @Autowired
    private SysDictService sysDictService;

    // 获取英雄属性和已激活的装备属性
    public AttributeEntity getHeroBasicStats(Long userHeroId) {
        AttributeEntity attribute = new AttributeEntity();
        // 获取玩家英雄详细信息
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("userHeroId", userHeroId);
        UserHeroInfoDetailRsp userHero = userHeroService.getUserHeroByIdDetailRsp(userHeroMap);
        if (userHero == null) {
            throw new RRException(ErrorCode.USER_HERO_GET_FAIL.getDesc() + "-ID: " + userHeroId);
        }
        attribute.setHeroStar(userHero.getStarCode());
        attribute.setHeroName(userHero.getHeroName());

        // 获取英雄技能信息
        // 获取英雄技能
        Map<String, Object> skillMap = new HashMap<>();
        skillMap.put("status", Constant.enable);
        skillMap.put("heroId", userHero.getHeroId());
        skillMap.put("skillStarCode", userHero.getStarCode());
        HeroSkillRsp skillRsp = heroSkillService.getHeroSkillRsp(skillMap);
        attribute.setSkillName(skillRsp.getSkillName());
        attribute.setSkillType(skillRsp.getSkillType());
        attribute.setSkillSolt(skillRsp.getSkillSolt());
        attribute.setSkillStarLevel(skillRsp.getSkillStarLevel());
        attribute.setSkillFixedDamage(skillRsp.getSkillFixedDamage());
        attribute.setSkillDescription(skillRsp.getSkillDescription());
        attribute.setSkillDamageBonusHero(skillRsp.getSkillDamageBonusHero());
        attribute.setSkillDamageBonusEquip(skillRsp.getSkillDamageBonusEquip());

        long level = userHero.getLevelCode();
        attribute.setHeroLevel(level);

        // 2.获取英雄成长属性并统计当前级别的属性
        long health = userHero.getHealth();
        long mana = userHero.getMana();
        double healthRegen = userHero.getHealthRegen();
        double manaRegen = userHero.getManaRegen();
        long armor = userHero.getArmor();
        long magicResist = userHero.getMagicResist();
        long attackDamage = userHero.getAttackDamage();
        long attackSpell = userHero.getAttackSpell();

        attribute.setHp(health);
        attribute.setMp(mana);
        attribute.setHpRegen(healthRegen);
        attribute.setMpRegen(manaRegen);
        attribute.setArmor(armor);
        attribute.setMagicResist(magicResist);
        attribute.setAttackDamage(attackDamage);
        attribute.setAttackSpell(attackSpell);

        // 3.获取该英雄身上全部穿戴装备的属性
        Map<String, Object> equipmentWearMap = new HashMap<>();
        equipmentWearMap.put("STATUS", Constant.enable);
        equipmentWearMap.put("USER_HERO_ID", userHeroId);
        List<UserHeroEquipmentWearEntity> equipmentWears = userHeroEquipmentWearDao.selectByMap(equipmentWearMap);

        health = 0;
        mana = 0;
        healthRegen = 0;
        manaRegen = 0;
        armor = 0;
        magicResist = 0;
        attackDamage = 0;
        attackSpell = 0;

        for (UserHeroEquipmentWearEntity equipmentWear_ : equipmentWears) {
            // 获取玩家拥有的装备
            UserEquipmentEntity userEquipment = userEquipmentService.getOne(new QueryWrapper<UserEquipmentEntity>()
                    .eq("STATUS", Constant.enable)
                    .eq("USER_EQUIPMENT_ID", equipmentWear_.getUserEquipId())
            );

            if (userEquipment == null) {
                throw new RRException("玩家装备失效,玩家装备编码:" + userEquipment.getEquipmentId());
            }

            // 将全部已穿戴装备属性累加
            health = health + (userEquipment.getHealth() != null ? userEquipment.getHealth() : 0);//装备初始生命值
            mana = mana + (userEquipment.getMana() != null ? userEquipment.getMana() : 0);//装备初始法力值
            healthRegen = healthRegen + (userEquipment.getHealthRegen() != null ? userEquipment.getHealthRegen() : 0);//装备初始生命值恢复
            manaRegen = manaRegen + (userEquipment.getManaRegen() != null ? userEquipment.getManaRegen() : 0);//装备初始法力值恢复
            armor = armor + (userEquipment.getArmor() != null ? userEquipment.getArmor() : 0);//装备初始护甲
            magicResist = magicResist + (userEquipment.getMagicResist() != null ? userEquipment.getMagicResist() : 0);//装备初始魔抗
            attackDamage = attackDamage + (userEquipment.getAttackDamage() != null ? userEquipment.getAttackDamage() : 0);//装备初始攻击力
            attackSpell = attackSpell + (userEquipment.getAttackSpell() != null ? userEquipment.getAttackSpell() : 0);//装备初始法功
        }

        // 将统计的已穿戴装备属性存入到战斗属性表
        attribute.setEquipHealth(health);
        attribute.setEquipMana(mana);
        attribute.setEquipHealthRegen(healthRegen);
        attribute.setEquipManaRegen(manaRegen);
        attribute.setEquipArmor(armor);
        attribute.setEquipMagicResist(magicResist);
        attribute.setEquipAttackDamage(attackDamage);
        attribute.setEquipAttackSpell(attackSpell);
        return attribute;
    }


    // 获取英雄战斗力
    public int getHeroPower(AttributeSimpleEntity attributeSimple) {
        double heroPower = ((attributeSimple.getHp() * 0.1) + (attributeSimple.getMp() * 0.1) + attributeSimple.getAttackDamage()
                + ((attributeSimple.getArmor() + attributeSimple.getMagicResist()) * 4.5) +
                attributeSimple.getHpRegen() * 0.1 + attributeSimple.getMpRegen() * 0.3);
        return (int) heroPower;
    }

    /**
     * 获取英雄初始属性
     *
     * @param heroInfo
     * @return
     */
    public AttributeSimpleEntity getHeroAttribute(HeroInfoEntity heroInfo, Double starBuff) {
        long hp = Math.round(heroInfo.getHealth() * Constant.HERO_ATTRIBUTE_RATE);// 初始生命值
        long mp = Math.round(heroInfo.getMana() * Constant.HERO_ATTRIBUTE_RATE);// 初始法力值
        double hpRegen = Math.round(heroInfo.getHealthRegen() * Constant.HERO_ATTRIBUTE_RATE);// 初始生命值恢复
        double mpRegen = Math.round(heroInfo.getManaRegen() * Constant.HERO_ATTRIBUTE_RATE);// 初始法力值恢复
        long armor = Math.round(heroInfo.getArmor() * Constant.HERO_ATTRIBUTE_RATE);// 初始护甲
        long magicResist = Math.round(heroInfo.getMagicResist() * Constant.HERO_ATTRIBUTE_RATE);// 初始魔抗
        long attackDamage = Math.round(heroInfo.getAttackDamage() * Constant.HERO_ATTRIBUTE_RATE);// 初始攻击力
        long attackSpell = Math.round(heroInfo.getAttackSpell() * Constant.HERO_ATTRIBUTE_RATE);// 初始法功
        return new AttributeSimpleEntity(hp, mp, hpRegen,
                mpRegen, armor, magicResist, attackDamage, attackSpell);
    }

    /**
     * 获取英雄升级后的属性
     *
     * @param heroInfo
     * @return
     */
    public AttributeSimpleEntity getHeroAttributeWithLv(UserHeroInfoDetailWithGrowRsp heroInfo, Integer Lv) {
        long hp = Math.round(heroInfo.getGrowHealth() * heroInfo.getGrowthRate() / Constant.GRI.O.getValue() * Lv);// 成长生命值
        long mp = Math.round(heroInfo.getGrowMana() * heroInfo.getGrowthRate() / Constant.GRI.O.getValue() * Lv);// 成长法力值
        double hpRegen = heroInfo.getGrowHealthRegen() * heroInfo.getGrowthRate() / Constant.GRI.O.getValue() * Lv;// 成长生命值恢复
        double mpRegen = heroInfo.getGrowManaRegen() * heroInfo.getGrowthRate() / Constant.GRI.O.getValue() * Lv;// 成长法力值恢复
        long armor = Math.round(heroInfo.getGrowArmor() * heroInfo.getGrowthRate() / Constant.GRI.O.getValue() * Lv);// 成长护甲
        long magicResist = Math.round(heroInfo.getGrowMagicResist() * heroInfo.getGrowthRate() / Constant.GRI.O.getValue() * Lv);// 成长魔抗
        long attackDamage = Math.round(heroInfo.getGrowAttackDamage() * heroInfo.getGrowthRate() / Constant.GRI.O.getValue() * Lv);// 成长攻击力
        long attackSpell = Math.round(heroInfo.getGrowAttackSpell() * heroInfo.getGrowthRate() / Constant.GRI.O.getValue() * Lv);// 成长法功
        return new AttributeSimpleEntity(hp, mp, hpRegen,
                mpRegen, armor, magicResist, attackDamage, attackSpell);
    }


    /**
     * 获取英雄升星后增加的属性
     *
     * @return
     */
    public AttributeSimpleEntity getAttributesAddedAfterStarUpgrade(AttributeSimpleEntity attributeStar, AttributeSimpleEntity attributeStarPlus) {
        long hp = attributeStarPlus.getHp() - attributeStar.getHp();// 增加的生命值
        long mp = attributeStarPlus.getMp() - attributeStar.getMp();// 增加的法力值
        double hpRegen = attributeStarPlus.getHpRegen() - attributeStar.getHpRegen();// 增加的生命值恢复
        double mpRegen = attributeStarPlus.getMpRegen() - attributeStar.getMpRegen();// 增加的法力值恢复
        long armor = attributeStarPlus.getArmor() - attributeStar.getArmor();// 增加的护甲
        long magicResist = attributeStarPlus.getMagicResist() - attributeStar.getMagicResist();// 增加的魔抗
        long attackDamage = attributeStarPlus.getAttackDamage() - attributeStar.getAttackDamage();// 增加的攻击力
        long attackSpell = attributeStarPlus.getAttackSpell() - attributeStar.getAttackSpell();// 增加的法功
        return new AttributeSimpleEntity(hp, mp, hpRegen,
                mpRegen, armor, magicResist, attackDamage, attackSpell);
    }

    /**
     * 获取装备合成项
     *
     * @param eqSIEs
     * @return
     */
    public List<String> getEquipItems(EquipSynthesisItemEntity eqSIEs) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(eqSIEs.getEquipSynthesisItem1())) {
            list.add(eqSIEs.getEquipSynthesisItem1());
        }
        if (StringUtils.isNotBlank(eqSIEs.getEquipSynthesisItem2())) {
            list.add(eqSIEs.getEquipSynthesisItem2());
        }
        if (StringUtils.isNotBlank(eqSIEs.getEquipSynthesisItem3())) {
            list.add(eqSIEs.getEquipSynthesisItem3());
        }
        if (StringUtils.isNotBlank(eqSIEs.getEquipWhite())) {
            list.add(eqSIEs.getEquipWhite());
        }
        if (StringUtils.isNotBlank(eqSIEs.getEquipBlue())) {
            list.add(eqSIEs.getEquipBlue());
        }
        return list;
    }

    /**
     * 更新战力(判断该英雄是否在上阵中, 只有上阵中的英雄更新战力及矿工)
     *
     * @param user
     * @param userHero
     * @param changePower
     */
    public UserHeroInfoDetailWithGrowRsp updateCombatPower(UserEntity user, UserHeroInfoDetailWithGrowRsp userHero, Long userEquipId, Long changePower, Double scale) {
        // 获取队伍
        Map<String, Object> teamParams = new HashMap<>();
        teamParams.put("userId", user.getUserId());
        teamParams.put("userHeroId", userHero.getUserHeroId());
        List<TeamInfoRsp> teamInfoRsps = teamConfigService.getTeamInfoList(teamParams);

//      (30 / 60 * 68 + 60 / 60 * 10) / （68 + 10) * 60;
//      更新后的英雄神谕值：(68 + 80) / 68 * 1 = 2.176470588235294
//      更新后的英雄神谕比: 2.176470588235294 / 2 = 108.8235294117647%
//      更新后的英雄矿工数：68 + 80 = 148

        BigDecimal newOracle = BigDecimal.ZERO;
        BigDecimal newMinter = BigDecimal.ZERO;
        // 初始GAIA系统
        fightCoreService.initTradeBalanceParameter(0);
        // 矿工兑换数量比例
        BigDecimal minterRate = CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1));
        if (userEquipId != null) {
            UserEquipmentEntity equip = userEquipmentService.getById(userEquipId);
            // 英雄矿工
            BigDecimal heroMinter = userHero.getMinter();
            // 装备矿工
            BigDecimal equipMinter = equip.getMinter();
            // 英雄的神谕值
            BigDecimal heroOracle = userHero.getOracle();
            newOracle = Arith.multiply(Arith.divide(Arith.add(heroMinter, equipMinter), heroOracle), heroOracle);
            newMinter = equip.getMinter();
        } else {
            // 获取英雄矿工数量
            CalculateTradeUtil.miners = userHero.getMinter();
            System.out.println("获取当前玩家矿工数量: " + CalculateTradeUtil.miners);
            // 英雄矿工
            BigDecimal heroMinter = userHero.getMinter();
            // 新增的矿工
            BigDecimal changeMinter = CalculateTradeUtil.updateMiner(BigDecimal.valueOf(changePower * scale));
            // 英雄的神谕值
            BigDecimal heroOracle = userHero.getOracle();
            newOracle = Arith.multiply(Arith.divide(Arith.add(heroMinter, changeMinter), heroOracle), heroOracle);
            newMinter = changeMinter;
        }

        // 英雄已在队伍上阵
        if (teamInfoRsps.size() > 0) {
            for (TeamInfoRsp teamInfoRsp : teamInfoRsps) {
                // 获取队伍旧战力值
                long oldPower = teamInfoRsp.getTeamPower();
                // 获取最新队伍战力（队伍战力+本次操作改变的战力）
                long newPower = teamInfoRsp.getTeamPower() + changePower;
                // 获取最新队伍矿工数量（队伍矿工数+本次操作改变的矿工数）
                BigDecimal newTeanMinter = Arith.add(teamInfoRsp.getTeamMinter(), newMinter);

                // 获取队伍旧矿工数
                BigDecimal oldMinter = teamInfoRsp.getTeamMinter();
                GmTeamConfigEntity team = new GmTeamConfigEntity();
                team.setId(teamInfoRsp.getId());
                fightCoreService.updateCombat(changePower, oldPower, newPower, user, team, newMinter, oldMinter, newTeanMinter);
            }
        }
        UserHeroInfoDetailWithGrowRsp rsp = new UserHeroInfoDetailWithGrowRsp();
        rsp.setMinter(Arith.add(userHero.getMinter(), newMinter));// 更新矿工
        rsp.setOracle(newOracle);// 更新神谕值
        return rsp;
    }


    /**
     * 获取英雄详细信息（接口返回用）
     *
     * @param user
     * @param userHeroId
     * @return
     */
    public UserHeroInfoDetailRsp getHeroInfoDetail(UserEntity user, Long userHeroId, String type) {
        // 获取英雄详细信息
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("userHeroId", userHeroId);
        UserHeroInfoDetailRsp rsp = userHeroService.getUserHeroByIdDetailRsp(userHeroMap);
        // 设置英雄职业
        String[] heroRole = rsp.getHeroRole().split(",");
        for (String role : heroRole) {
            switch (role) {
                case "00":
                    rsp.getRoles().add(Constant.HeroRole.Warrior.getValue());
                    break;
                case "01":
                    rsp.getRoles().add(Constant.HeroRole.Mage.getValue());
                    break;
                case "02":
                    rsp.getRoles().add(Constant.HeroRole.Assassin.getValue());
                    break;
                case "03":
                    rsp.getRoles().add(Constant.HeroRole.Tank.getValue());
                    break;
                case "04":
                    rsp.getRoles().add(Constant.HeroRole.Support.getValue());
                    break;
                case "05":
                    rsp.getRoles().add(Constant.HeroRole.Archer.getValue());
                    break;
            }
        }
        // 获取英雄下一个等级信息
        Map<String, Object> heroLvMap = new HashMap<>();
        int heroLv = rsp.getLevelCode() < 50 ? rsp.getLevelCode() + Constant.Quantity.Q1.getValue() : rsp.getLevelCode();
        heroLvMap.put("levelCode", heroLv);
        HeroLevelRsp heroLvRsp = heroLevelService.getHeroLevelByLvCode(heroLvMap);
        if (heroLvRsp == null) {
            throw new RRException(ErrorCode.EXP_GET_FAIL.getDesc());
        }

        // 晋级到下一级所需经验值
        rsp.setPromotionExperience(heroLvRsp.getPromotionExperience());
        // 当前等级获取的经验值
        rsp.setCurrentExp(ExpUtils.getCurrentExp(heroLvRsp.getExperienceTotal(), heroLvRsp.getPromotionExperience(), rsp.getExperienceObtain()));

        // 获取英雄技能
        Map<String, Object> skillMap = new HashMap<>();
        skillMap.put("status", Constant.enable);
        skillMap.put("heroId", rsp.getHeroId());
        skillMap.put("skillStarCode", rsp.getStarCode());
        HeroSkillRsp skillRsp = heroSkillService.getHeroSkillRsp(skillMap);
        rsp.setHeroSkillRsp(skillRsp);

        // 获取当前星级+1
        int starCode = rsp.getStarCode();
        if (starCode < Constant.StarLv.Lv5.getValue()) {
            starCode += Constant.StarLv.Lv1.getValue();
        }
        // 获取当前阶段升星所需碎片
        StarInfoEntity starInfo = starInfoService.getOne(new QueryWrapper<StarInfoEntity>()
                .eq("STAR_CODE", starCode)

        );

        // 获取玩家背包中的英雄碎片
        Map<String, Object> heroFragMap = new HashMap<>();
        heroFragMap.put("userId", user.getUserId());
        heroFragMap.put("status", Constant.enable);
        heroFragMap.put("heroId", rsp.getHeroId());
        UserHeroFragInfoDetailRsp heroFragCount = userHeroFragService.getUserAllHeroFragCount(heroFragMap);
        Integer shardNum = null != heroFragCount ? heroFragCount.getHeroFragNum() : Constant.ZERO_I;
        rsp.setShardNum(shardNum);
        rsp.setUpStarShardNum(starInfo.getUpStarFragNum());

        // 初始GAIA系统
        fightCoreService.initTradeBalanceParameter(0);
        // 矿工兑换数量比例
        BigDecimal minterRate = CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1));
        BigDecimal newOracle = BigDecimal.valueOf(Arith.multiply(Arith.divide(rsp.getOracle(), minterRate), BigDecimal.valueOf(100)).intValue());
        // 按当前市场行情计算神谕值
        rsp.setOracle(newOracle);
        return rsp;
    }

    /**
     * 校验英雄
     *
     * @param userHeroId
     * @return
     */
    public UserHeroInfoDetailWithGrowRsp getUserHero(Long userHeroId) {
        // 获取玩家英雄信息
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("userHeroId", userHeroId);
        UserHeroInfoDetailWithGrowRsp userHero = userHeroService.getUserHeroByIdDetailWithGrowRsp(userHeroMap);
        if (userHero == null) {
            throw new RRException(ErrorCode.USER_HERO_GET_FAIL.getDesc() + "=ID: " + userHeroId);
        }
        return userHero;
    }

    /**
     * 是否开放战力提升功能（0:关闭，1:打开）
     */
    public void isOpen() {
        String isOpen = sysDictService.getValueByNameAndKey("SYS_CONFIG", "IS_OPEN");
        if (isOpen.equals(Constant.disabled)) {
            throw new RRException("COMING SOON!");
        }
    }
}

