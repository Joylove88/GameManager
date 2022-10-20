package com.gm.modules.combatStatsUtils.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.modules.basicconfig.dao.*;
import com.gm.modules.basicconfig.dto.AttributeEntity;
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
    private EquipmentInfoDao equipmentInfoDao;
    @Autowired
    private GmTeamConfigDao teamConfigDao;
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
    private UserDao userDao;
    @Autowired
    private UserEquipmentDao userEquipmentDao;


    // 获取英雄属性
    public AttributeEntity getHeroBasicStats(Map<String,Object> map) {
        AttributeEntity attribute = new AttributeEntity();

        Object heroStarId = map.get("heroStarId");
        // 获取星级英雄方法
        HeroStarEntity heroStar = heroStarDao.selectOne(new QueryWrapper<HeroStarEntity>()
                .eq("STATUS", Constant.enable)
                .eq("GM_HERO_STAR_ID", heroStarId)// 星级英雄编码
        );
        if(heroStar == null) {
            throw new RRException("官方已停用改英雄,英雄编码为:" + heroStarId);
        }

        // 获取星级
        StarInfoEntity starInfo = starInfoDao.selectById(heroStar.getGmStarId());
        if(starInfo == null){
            throw new RRException("获取星级信息失败");
        }
        attribute.setHeroStar(starInfo.getGmStarCode());

        // 获取英雄信息
        HeroInfoEntity heroInfo = heroInfoDao.selectById(heroStar.getGmHeroId());
        if (heroInfo == null){
            throw new RRException("获取英雄信息失败");
        }
        attribute.setHeroName(heroInfo.getHeroName());

        // 获取英雄技能信息
        HeroSkillEntity heroSkill = heroSkillDao.selectOne(new QueryWrapper<HeroSkillEntity>()
                .eq("STATUS", Constant.enable)
                .eq("GM_HERO_STAR_ID", heroStarId)// 星级英雄编码
        );
        if (heroSkill == null) {
            throw new RRException("获取英雄技能信息失败");
        }
        attribute.setSkillName(heroSkill.getSkillName());
        attribute.setSkillType(heroSkill.getSkillType());
        attribute.setSkillSolt(heroSkill.getSkillSolt());
        attribute.setSkillStarLevel(heroSkill.getSkillStarLevel());
        attribute.setSkillFixedDamage(heroSkill.getSkillFixedDamage());
        attribute.setSkillDescription(heroSkill.getSkillDescription());
        attribute.setSkillDamageBonusHero(heroSkill.getSkillDamageBonusHero());
        attribute.setSkillDamageBonusEquip(heroSkill.getSkillDamageBonusEquip());

        // 1.获取英雄当前等级
        HeroLevelEntity heroLevel = heroLevelDao.selectOne(new QueryWrapper<HeroLevelEntity>()
                .eq("GM_HERO_LEVE_ID", map.get("heroLevelId"))
        );
        long level = heroLevel.getGmLevelCode();
        attribute.setHeroLevel(level);

        // 2.获取英雄成长属性并统计当前级别的属性
        long health = heroStar.getGmHealth();
        long mana = heroStar.getGmMana();
        long healthRegen = heroStar.getGmHealthRegen();
        long manaRegen = heroStar.getGmManaRegen();
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
        equipmentWearMap.put("GM_USER_HERO_ID", map.get("userHeroId"));
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
                    .eq("GM_USER_EQUIPMENT_ID", equipmentWear_.getGmUserEquipId())
            );

            if (userEquipment == null){
                throw new RRException("玩家装备失效,玩家装备编码:" + userEquipment.getGmEquipmentId());
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
    public Long getHeroPower(long health, long mana, long healthRegen, long manaRegen, long armor, long magicResist, long attackDamage, long gmAttackSpell, double scale){
        double heroPower = ((health * 0.1) + (mana * 0.1) + attackDamage + ((armor + magicResist) * 4.5) + healthRegen * 0.1 + manaRegen * 0.3) * scale;
        return (long) heroPower;
    }

    /**
     * 获取装备合成项
     * @param eqSIEs
     * @return
     */
    public List getEquipItems(EquipSynthesisItemEntity eqSIEs) {
        List list = new ArrayList();
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipSynthesisItem1())) {
            list.add(eqSIEs.getGmEquipSynthesisItem1());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipSynthesisItem2())) {
            list.add(eqSIEs.getGmEquipSynthesisItem2());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipSynthesisItem3())) {
            list.add(eqSIEs.getGmEquipSynthesisItem3());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipWhite())) {
            list.add(eqSIEs.getGmEquipWhite());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipBlue())) {
            list.add(eqSIEs.getGmEquipBlue());
        }
        return list;
    }

}

