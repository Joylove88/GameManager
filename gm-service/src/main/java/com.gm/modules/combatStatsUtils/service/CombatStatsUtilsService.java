package com.gm.modules.combatStatsUtils.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.utils.Constant;
import com.gm.modules.basicconfig.dao.*;
import com.gm.modules.basicconfig.dto.AttributeEntity;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.user.dao.*;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取英雄属性，更新战力
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
            System.out.println("官方已停用改英雄,英雄编码为:" + heroStarId);
        }

        // 获取星级
        StarInfoEntity starInfo = starInfoDao.selectById(heroStar.getGmStarId());
        if(starInfo == null){
            System.out.println("获取星级信息失败");
        }
        attribute.setHeroStar(starInfo.getGmStarCode());

        // 获取英雄信息
        HeroInfoEntity heroInfo = heroInfoDao.selectById(heroStar.getGmHeroId());
        if (heroInfo == null){
            System.out.println("获取英雄信息失败");
        }
        attribute.setHeroName(heroInfo.getHeroName());

        // 获取英雄技能信息
        HeroSkillEntity heroSkill = heroSkillDao.selectOne(new QueryWrapper<HeroSkillEntity>()
                .eq("STATUS", Constant.enable)
                .eq("GM_HERO_STAR_ID", heroStarId)// 星级英雄编码
        );
        if (heroSkill == null) {
            System.out.println("获取英雄技能信息失败");
        }
        attribute.setSkillName(heroSkill.getSkillName());
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
            // 获取装备信息方法
            EquipmentInfoEntity equipmentInfo = equipmentInfoDao.selectOne(new QueryWrapper<EquipmentInfoEntity>()
                    .eq("STATUS", Constant.enable)
                    .eq("EQUIP_ID", equipmentWear_.getGmEquipId())
            );

            if (equipmentInfo == null){
                System.out.println("官方已停用改装备,装备编码:" + equipmentWear_.getGmEquipId());
            }
            // 将全部已穿戴装备属性累加
            health = health + (equipmentInfo.getEquipHealth() != null ? equipmentInfo.getEquipHealth() : 0);//装备初始生命值
            mana = mana + (equipmentInfo.getEquipMana() != null ? equipmentInfo.getEquipMana() : 0);//装备初始法力值
            healthRegen = healthRegen + (equipmentInfo.getEquipHealthRegen() != null ? equipmentInfo.getEquipHealthRegen() : 0);//装备初始生命值恢复
            manaRegen = manaRegen + (equipmentInfo.getEquipManaRegen() != null ? equipmentInfo.getEquipManaRegen() : 0);//装备初始法力值恢复
            armor = armor + (equipmentInfo.getEquipArmor() != null ? equipmentInfo.getEquipArmor() : 0);//装备初始护甲
            magicResist = magicResist + (equipmentInfo.getEquipMagicResist() != null ? equipmentInfo.getEquipMagicResist() : 0);//装备初始魔抗
            attackDamage = attackDamage + (equipmentInfo.getEquipAttackDamage() != null ? equipmentInfo.getEquipAttackDamage() : 0);//装备初始攻击力
            attackSpell = attackSpell + (equipmentInfo.getEquipAttackSpell() != null ? equipmentInfo.getEquipAttackSpell() : 0);//装备初始法功
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


    // 获取英雄最新战斗力 (包含已穿戴全部装备属性)
    public Long getHeroPower(AttributeEntity attribute){
        double heroPower = 0;
        long health = attribute.getHp();//初始生命值
        long mana = attribute.getMp();//初始法力值
        long healthRegen = attribute.getHpRegen();//初始生命值恢复
        long manaRegen = attribute.getMpRegen();//初始法力值恢复
        long armor = attribute.getArmor();//gmArmor
        long magicResist = attribute.getMagicResist();//初始魔抗
        long attackDamage = attribute.getAttackDamage();//初始攻击力
        long gmAttackSpell = attribute.getAttackSpell();//初始法功
        heroPower = (health * 0.1) + (mana * 0.1) + attackDamage + ((armor + magicResist) * 4.5) + healthRegen * 0.1 + manaRegen * 0.3;

        // 统计装备战力
        double equipPower = 0;
        long eqHealth = attribute.getEquipHealth() != null ? attribute.getEquipHealth() : 0;//初始生命值
        long eqMana = attribute.getEquipMana() != null ? attribute.getEquipMana() : 0;//初始法力值
        long eqHealthRegen = attribute.getEquipHealthRegen() != null ? attribute.getEquipHealthRegen() : 0;//初始生命值恢复
        long eqManaRegen = attribute.getEquipManaRegen() != null ? attribute.getEquipManaRegen() : 0;//初始法力值恢复
        long eqArmor = attribute.getEquipArmor() != null ? attribute.getEquipArmor() : 0;//gmArmor
        long eqMagicResist = attribute.getEquipMagicResist() != null ? attribute.getEquipMagicResist() : 0;//初始魔抗
        long eqAttackDamage = attribute.getEquipAttackDamage() != null ? attribute.getEquipAttackDamage() : 0;//初始攻击力
        long eqAttackSpell = attribute.getEquipAttackSpell() != null ? attribute.getEquipAttackSpell() : 0;//初始法攻
        equipPower = (eqHealth * 0.1) + (eqMana * 0.1) + eqAttackDamage + eqAttackSpell + ((eqArmor + eqMagicResist) * 4.5) + eqHealthRegen * 0.1 + eqManaRegen * 0.3;

        heroPower = heroPower + equipPower;
        return (long) heroPower;
    }

    // 更新玩家总战力（3个队伍的英雄战力总和），每个提升战力方法都将调用该函数
    public void setTotalPower(Long userId){
        long totalPower = 0;

        Map<String,Object> teamMap = new HashMap<>();
        teamMap.put("USER_ID", userId);
        List<GmTeamConfigEntity> teams = teamConfigDao.selectByMap(teamMap);
        if (teams == null) {
            System.out.println("获取玩家队伍信息异常");
        }
        for(GmTeamConfigEntity teamConfig : teams){
            totalPower = totalPower + teamConfig.getTeamPower();
        }

        // 更新玩家总战力
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setTotalPower(totalPower);
        userDao.updateById(user);

    }



}

