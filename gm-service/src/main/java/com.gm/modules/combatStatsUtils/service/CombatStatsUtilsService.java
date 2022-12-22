package com.gm.modules.combatStatsUtils.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.modules.basicconfig.dao.*;
import com.gm.modules.basicconfig.dto.AttributeEntity;
import com.gm.modules.basicconfig.dto.AttributeSimpleEntity;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.user.dao.*;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserEquipmentEntity;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玩家战斗力业务类
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("combatStatsUtilsService")
public class CombatStatsUtilsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatStatsUtilsService.class);
    @Autowired
    private HeroStarDao heroStarDao;
    @Autowired
    private UserHeroEquipmentWearDao userHeroEquipmentWearDao;
    @Autowired
    private HeroLevelDao heroLevelDao;
    @Autowired
    private HeroInfoDao heroInfoDao;
    @Autowired
    private HeroSkillDao heroSkillDao;
    @Autowired
    private StarInfoDao starInfoDao;
    @Autowired
    private UserEquipmentDao userEquipmentDao;

    // 获取英雄属性和已激活的装备属性
    public AttributeEntity getHeroBasicStats(Map<String,Object> map) {
        AttributeEntity attribute = new AttributeEntity();

        Object heroStarId = map.get("heroStarId");
        // 获取星级英雄方法
        HeroStarEntity heroStar = heroStarDao.selectOne(new QueryWrapper<HeroStarEntity>()
                .eq("STATUS", Constant.enable)
                .eq("HERO_STAR_ID", heroStarId)// 星级英雄编码
        );
        if(heroStar == null) {
            throw new RRException("官方已停用改英雄,英雄编码为:" + heroStarId);
        }

        // 获取星级
        StarInfoEntity starInfo = starInfoDao.selectById(heroStar.getGmStarId());
        if(starInfo == null){
            throw new RRException("获取星级信息失败");
        }
        attribute.setHeroStar(starInfo.getStarCode());

        // 获取英雄信息
        HeroInfoEntity heroInfo = heroInfoDao.selectById(heroStar.getGmHeroId());
        if (heroInfo == null){
            throw new RRException("获取英雄信息失败");
        }
        attribute.setHeroName(heroInfo.getHeroName());

        // 获取英雄技能信息
        HeroSkillEntity heroSkill = heroSkillDao.selectOne(new QueryWrapper<HeroSkillEntity>()
                .eq("STATUS", Constant.enable)
                .eq("HERO_STAR_ID", heroStarId)// 星级英雄编码
        );
        if (heroSkill == null) {
            throw new RRException("获取英雄技能信息失败");
        }
        attribute.setSkillName(heroSkill.getSkillName());
        attribute.setSkillType(heroSkill.getSkillType());
        attribute.setSkillSolt(heroSkill.getSkillSolt());
        attribute.setSkillStarLevel(heroSkill.getSkillStarCode());
        attribute.setSkillFixedDamage(heroSkill.getSkillFixedDamage());
        attribute.setSkillDescription(heroSkill.getSkillDescription());
        attribute.setSkillDamageBonusHero(heroSkill.getSkillDamageBonusHero());
        attribute.setSkillDamageBonusEquip(heroSkill.getSkillDamageBonusEquip());

        // 1.获取英雄当前等级
        HeroLevelEntity heroLevel = heroLevelDao.selectOne(new QueryWrapper<HeroLevelEntity>()
                .eq("HERO_LEVE_ID", map.get("heroLevelId"))
        );
        long level = heroLevel.getLevelCode();
        attribute.setHeroLevel(level);

        // 2.获取英雄成长属性并统计当前级别的属性
        long health = heroStar.getGmHealth();
        long mana = heroStar.getGmMana();
        double healthRegen = heroStar.getGmHealthRegen();
        double manaRegen = heroStar.getGmManaRegen();
        long armor = heroStar.getGmArmor();
        long magicResist = heroStar.getGmMagicResist();
        long attackDamage = heroStar.getGmAttackDamage();
        long attackSpell = heroStar.getGmAttackSpell();

        // 如果英雄等级大于1级进行成长属性值累加
        level = level - 1; // 默认1级 成长值计算去掉1级 从2级开始计算
        if (level > 0) {
            health = health + (heroStar.getGmGrowHealth() * level);// 成长生命值
            mana = mana + (heroStar.getGmGrowMana() * level);// 成长法力值
            healthRegen = healthRegen + (heroStar.getGmGrowHealthRegen() * level);// 成长生命值恢复
            manaRegen = manaRegen + (heroStar.getGmGrowManaRegen() * level);// 成长法力值恢复
            armor = armor + (heroStar.getGmGrowArmor() * level);// 成长护甲
            magicResist = magicResist + (heroStar.getGmGrowMagicResist() * level);// 成长魔抗
            attackDamage = attackDamage + (heroStar.getGmGrowAttackDamage() * level);// 成长攻击力
            attackSpell = attackSpell + (heroStar.getGmGrowAttackSpell() * level);// 成长法功
        }

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
        equipmentWearMap.put("USER_HERO_ID", map.get("userHeroId"));
        List<UserHeroEquipmentWearEntity> equipmentWears = userHeroEquipmentWearDao.selectByMap(equipmentWearMap);

        health = 0;
        mana = 0;
        healthRegen = 0;
        manaRegen = 0;
        armor = 0;
        magicResist = 0;
        attackDamage = 0;
        attackSpell = 0;

        for (UserHeroEquipmentWearEntity equipmentWear_ : equipmentWears){
            // 获取玩家拥有的装备
            UserEquipmentEntity userEquipment = userEquipmentDao.selectOne(new QueryWrapper<UserEquipmentEntity>()
                    .eq("STATUS", Constant.enable)
                    .eq("USER_EQUIPMENT_ID", equipmentWear_.getUserEquipId())
            );

            if (userEquipment == null){
                throw new RRException("玩家装备失效,玩家装备编码:" + userEquipment.getEquipmentId());
            }

            // 将全部已穿戴装备属性累加
            health = health + (userEquipment.getEquipHealth() != null ? userEquipment.getEquipHealth() : 0);//装备初始生命值
            mana = mana + (userEquipment.getEquipMana() != null ? userEquipment.getEquipMana() : 0);//装备初始法力值
            healthRegen = healthRegen + (userEquipment.getEquipHealthRegen() != null ? userEquipment.getEquipHealthRegen() : 0);//装备初始生命值恢复
            manaRegen = manaRegen + (userEquipment.getEquipManaRegen() != null ? userEquipment.getEquipManaRegen() : 0);//装备初始法力值恢复
            armor = armor + (userEquipment.getEquipArmor() != null ? userEquipment.getEquipArmor() : 0);//装备初始护甲
            magicResist = magicResist + (userEquipment.getEquipMagicResist() != null ? userEquipment.getEquipMagicResist() : 0);//装备初始魔抗
            attackDamage = attackDamage + (userEquipment.getEquipAttackDamage() != null ? userEquipment.getEquipAttackDamage() : 0);//装备初始攻击力
            attackSpell = attackSpell + (userEquipment.getEquipAttackSpell() != null ? userEquipment.getEquipAttackSpell() : 0);//装备初始法功
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
    public int getHeroPower(AttributeSimpleEntity attributeSimple){
        double heroPower = ((attributeSimple.getHp() * 0.1) + (attributeSimple.getMp() * 0.1) + attributeSimple.getAttackDamage()
                + ((attributeSimple.getArmor() + attributeSimple.getMagicResist()) * 4.5) +
                attributeSimple.getHpRegen() * 0.1 + attributeSimple.getMpRegen() * 0.3);
        return (int) heroPower;
    }

    /**
     * 获取英雄初始属性
     * @param heroInfo
     * @return
     */
    public AttributeSimpleEntity getHeroAttribute(HeroInfoEntity heroInfo, Double starBuff){
        long hp = Math.round(heroInfo.getHealth() * Constant.HERO_ATTRIBUTE_RATE);// 初始生命值
        long mp = Math.round(heroInfo.getMana() * Constant.HERO_ATTRIBUTE_RATE);// 初始法力值
        double hpRegen = Math.round(heroInfo.getHealthRegen() * Constant.HERO_ATTRIBUTE_RATE);// 初始生命值恢复
        double mpRegen = Math.round(heroInfo.getManaRegen() * Constant.HERO_ATTRIBUTE_RATE);// 初始法力值恢复
        long armor = Math.round(heroInfo.getArmor() * Constant.HERO_ATTRIBUTE_RATE);// 初始护甲
        long magicResist = Math.round(heroInfo.getMagicResist() * Constant.HERO_ATTRIBUTE_RATE);// 初始魔抗
        long attackDamage = Math.round(heroInfo.getAttackDamage() * Constant.HERO_ATTRIBUTE_RATE);// 初始攻击力
        long attackSpell = Math.round(heroInfo.getAttackSpell() * Constant.HERO_ATTRIBUTE_RATE);// 初始法功
//            long growHp = Math.round(heroInfos.get(heroIndex).getGmGrowHealth() * Constant.HERO_ATTRIBUTE_RATE);//成长属性-生命值
//            long growMp = Math.round(heroInfos.get(heroIndex).getGmGrowMana() * Constant.HERO_ATTRIBUTE_RATE);//成长属性-法力值
//            double growHpRegen = Math.round(heroInfos.get(heroIndex).getGmGrowHealthRegen() * Constant.HERO_ATTRIBUTE_RATE);//成长属性-生命值恢复
//            double growMpRegen = Math.round(heroInfos.get(heroIndex).getGmGrowManaRegen() * Constant.HERO_ATTRIBUTE_RATE);//成长属性-法力值恢复
//            long growArmor = Math.round(heroInfos.get(heroIndex).getGmGrowArmor() * Constant.HERO_ATTRIBUTE_RATE);//成长属性-护甲
//            long growMagicResist = Math.round(heroInfos.get(heroIndex).getGmGrowMagicResist() * Constant.HERO_ATTRIBUTE_RATE);//成长属性-魔抗
//            long growAttackDamage = Math.round(heroInfos.get(heroIndex).getGmGrowAttackDamage() * Constant.HERO_ATTRIBUTE_RATE);//成长属性-攻击力
//            long growAttackSpell = Math.round(heroInfos.get(heroIndex).getGmGrowAttackSpell() * Constant.HERO_ATTRIBUTE_RATE);//成长属性-法功
        return new AttributeSimpleEntity(starBuff, Constant.StarLv.Lv1.getValue(), hp, mp, hpRegen,
                mpRegen, armor, magicResist, attackDamage, attackSpell);
    }

    /**
     * 获取英雄升星后增加的属性
     * @return
     */
    public AttributeSimpleEntity getAttributesAddedAfterStarUpgrade(AttributeSimpleEntity attributeStar, AttributeSimpleEntity attributeStarPlus){
        long hp = attributeStarPlus.getHp() - attributeStar.getHp();// 增加的生命值
        long mp = attributeStarPlus.getMp() - attributeStar.getMp();// 增加的法力值
        double hpRegen = attributeStarPlus.getHpRegen() - attributeStar.getHpRegen();// 增加的生命值恢复
        double mpRegen = attributeStarPlus.getMpRegen() - attributeStar.getMpRegen();// 增加的法力值恢复
        long armor = attributeStarPlus.getArmor() - attributeStar.getArmor();// 增加的护甲
        long magicResist = attributeStarPlus.getMagicResist() - attributeStar.getMagicResist();// 增加的魔抗
        long attackDamage = attributeStarPlus.getAttackDamage() - attributeStar.getAttackDamage();// 增加的攻击力
        long attackSpell = attributeStarPlus.getAttackSpell() - attributeStar.getAttackSpell();// 增加的法功
        return new AttributeSimpleEntity(1.0, Constant.StarLv.Lv1.getValue(), hp, mp, hpRegen,
                mpRegen, armor, magicResist, attackDamage, attackSpell);
    }

    /**
     * 获取装备合成项
     * @param eqSIEs
     * @return
     */
    public List getEquipItems(EquipSynthesisItemEntity eqSIEs) {
        List list = new ArrayList();
        if ( StringUtils.isNotBlank(eqSIEs.getEquipSynthesisItem1())) {
            list.add(eqSIEs.getEquipSynthesisItem1());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getEquipSynthesisItem2())) {
            list.add(eqSIEs.getEquipSynthesisItem2());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getEquipSynthesisItem3())) {
            list.add(eqSIEs.getEquipSynthesisItem3());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getEquipWhite())) {
            list.add(eqSIEs.getEquipWhite());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getEquipBlue())) {
            list.add(eqSIEs.getEquipBlue());
        }
        return list;
    }

}

