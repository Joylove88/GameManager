package com.gm.modules.drawGift.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.LotteryGiftsUtils;
import com.gm.modules.basicconfig.dao.*;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.basicconfig.dto.*;
import com.gm.modules.order.dao.TransactionOrderDao;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.dao.*;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.entity.*;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("drawGiftService")
public class DrawGiftService{
    private static final Logger LOGGER = LoggerFactory.getLogger(DrawGiftService.class);
    @Autowired
    private UserHeroFragDao userHeroFragDao;
    @Autowired
    private UserHeroDao userHeroDao;
    @Autowired
    private HeroStarDao heroStarDao;
    @Autowired
    private HeroFragDao heroFragDao;
    @Autowired
    private ProbabilityDao probabilityDao;
    @Autowired
    private EquipmentInfoDao equipmentInfoDao;
    @Autowired
    private EquipmentFragDao equinpmentFragDao;
    @Autowired
    private ExperiencePotionDao experiencePotionDao;
    @Autowired
    private UserEquipmentDao userEquipmentDao;
    @Autowired
    private UserEquipmentFragDao userEquipmentFragDao;
    @Autowired
    private UserExperiencePotionDao userExperiencePotionDao;
    @Autowired
    private TransactionOrderDao transactionOrderDao;
    @Autowired
    private SysConfigService sysConfigService;

    // 英雄抽奖池
    public List<Object> heroDrawStart(UserEntity user,DrawForm drawForm) throws Exception {
        // 先获取抽奖概率等级 概率等级越高 获奖几率越低
        Map<String,Object> proMap = new HashMap<>();
        proMap.put("STATUS", Constant.enable);
        proMap.put("GM_TYPE", Constant.ItemType.HERO.getValue());
        List<ProbabilityEntity> probability = probabilityDao.selectByMap(proMap);
        // 创建一个储存抽奖物品+概率的集合
        List<HeroDrawGiftDtoEntity> gifts = new ArrayList<>();
        for (ProbabilityEntity aProbability : probability) {
            // 通过抽奖概率等级获取奖池对应的英雄
            List<HeroStarEntity> heroStarEntities = heroStarDao.getHeroStarPro(aProbability.getGmProbabilityId());
            for (HeroStarEntity heroStarEntity : heroStarEntities) {
                HeroDrawGiftDtoEntity drawDtoEntity = new HeroDrawGiftDtoEntity();
//                drawDtoEntity.setGmHeroId(heroStarEntity.getGmHeroId());
//                drawDtoEntity.setGmHeroStarId(heroStarEntity.getGmHeroStarId());
//                drawDtoEntity.setHeroName(heroStarEntity.getHeroName());
//                drawDtoEntity.setGmStarCode(heroStarEntity.getGmStarCode());
//                drawDtoEntity.setHeroIconUrl(heroStarEntity.getHeroIconUrl());
                BeanUtils.copyProperties(drawDtoEntity,heroStarEntity);
                drawDtoEntity.setPron(aProbability.getGmPron() * 0.8);
                if (aProbability.getGmPronLv() == 1) {
                    // 概率为1级
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 0));
                } else if (aProbability.getGmPronLv() == 2) {
                    // 概率为2级
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 0));
                } else if (aProbability.getGmPronLv() == 3) {
                    // 概率为3级
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 0));
                } else if (aProbability.getGmPronLv() == 4) {
                    // 概率为4级
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 0));
                } else if (aProbability.getGmPronLv() == 5) {
                    // 概率为5级
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 0));
                }

            }

            // 获取奖池全部英雄碎片
            List<HeroFragEntity> heroFragEntities = heroFragDao.getHeroFragPro();
            for (HeroFragEntity heroFragEntity : heroFragEntities) {
                HeroDrawGiftDtoEntity drawDtoEntity = new HeroDrawGiftDtoEntity();
                drawDtoEntity.setGmHeroFragId(heroFragEntity.getGmHeroFragId());
                drawDtoEntity.setHeroName(heroFragEntity.getHeroName());
                drawDtoEntity.setHeroIconUrl(heroFragEntity.getHeroFragIconUrl());

                if (aProbability.getGmPronLv() == 1) {
                    // 概率为1级
                    drawDtoEntity.setPron(aProbability.getGmPron());
                    drawDtoEntity.setGmHeroFragNum(1L);
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 1));
                } else if (aProbability.getGmPronLv() == 2) {
                    // 概率为2级
                    drawDtoEntity.setPron(aProbability.getGmPron());
                    drawDtoEntity.setGmHeroFragNum(2L);
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 1));
                } else if (aProbability.getGmPronLv() == 3) {
                    // 概率为3级
                    drawDtoEntity.setPron(aProbability.getGmPron());
                    drawDtoEntity.setGmHeroFragNum(3L);
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 1));
                } else if (aProbability.getGmPronLv() == 4) {
                    // 概率为4级
                    drawDtoEntity.setPron(aProbability.getGmPron());
                    drawDtoEntity.setGmHeroFragNum(4L);
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 1));
                } else if (aProbability.getGmPronLv() == 5) {
                    // 概率为5级
                    drawDtoEntity.setPron(aProbability.getGmPron());
                    drawDtoEntity.setGmHeroFragNum(5L);
                    gifts.add(AddHdGiftInfo(drawDtoEntity, 1));
                }

            }
        }
        //获取每件物品的中奖概率
        List<Double> orignalRates = new ArrayList<Double>(gifts.size());
        for (HeroDrawGiftDtoEntity gift: gifts) {
            double probabilityN = gift.getPron();
            if (probabilityN < 0) {
                probabilityN = 0;
            }
            orignalRates.add(probabilityN);
        }

        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates,Long.parseLong(drawForm.getDrawType()));

        // 奖品集合
        List<Object> gifList = new ArrayList<>();
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            Date now = new Date();
            // 判断奖品是否为星级英雄 Dtype=0 为星级英雄, Dtype=1 为英雄碎片
            if(gifts.get(entry.getKey()).getDType() == 0){
                UserHeroEntity userHeroEntity = new UserHeroEntity();
                userHeroEntity.setGmHeroId(gifts.get(entry.getKey()).getGmHeroId());
                userHeroEntity.setGmHeroStarId(gifts.get(entry.getKey()).getGmHeroStarId());
                userHeroEntity.setGmHeroLevelId(100000009L);
                userHeroEntity.setGmUserId(user.getUserId());
                userHeroEntity.setStatus(Constant.enable);
                userHeroEntity.setMintStatus(Constant.enable);
                userHeroEntity.setMintHash(drawForm.getTransactionHash());
                userHeroEntity.setCreateTime(now);
                userHeroEntity.setCreateTimeTs(now.getTime());
                // 生成NFT唯一编码
                userHeroEntity.setNftId(Arith.UUID20().longValue());
                // 统计英雄战力
                double heroPower = 0;
                long health = gifts.get(entry.getKey()).getGmHealth();//初始生命值
                long mana = gifts.get(entry.getKey()).getGmMana();//初始法力值
                long healthRegen = gifts.get(entry.getKey()).getGmHealthRegen();//初始生命值恢复
                long manaRegen = gifts.get(entry.getKey()).getGmManaRegen();//初始法力值恢复
                long armor = gifts.get(entry.getKey()).getGmArmor();//gmArmor
                long magicResist = gifts.get(entry.getKey()).getGmMagicResist();//初始魔抗
                long attackDamage = gifts.get(entry.getKey()).getGmAttackDamage();//初始攻击力
                long gmAttackSpell = gifts.get(entry.getKey()).getGmAttackSpell();//初始法功
                heroPower = (health * 0.1) + (mana * 0.1) + attackDamage + ((armor + magicResist) * 4.5) + healthRegen * 0.1 + manaRegen * 0.3;
                userHeroEntity.setHeroPower((long) heroPower);

                userHeroDao.insert(userHeroEntity);
            } else if (gifts.get(entry.getKey()).getDType() == 1){
                Long fragNum = gifts.get(entry.getKey()).getGmHeroFragNum();
                // 加一层校验 抽奖池英雄碎片数量不小于0
                if(fragNum > 0) {
                    for (int fn = 0; fn < fragNum; fn++) {
                        UserHeroFragEntity userHeroFragEntity = new UserHeroFragEntity();
                        userHeroFragEntity.setGmHeroFragId(gifts.get(entry.getKey()).getGmHeroFragId());
                        userHeroFragEntity.setGmUserHeroFragNum(fragNum);
                        userHeroFragEntity.setGmUserId(user.getUserId());
                        userHeroFragEntity.setStatus(Constant.enable);
                        userHeroFragEntity.setMintStatus(Constant.enable);
                        userHeroFragEntity.setMintHash(drawForm.getTransactionHash());
                        userHeroFragEntity.setCreateTime(now);
                        userHeroFragEntity.setCreateTimeTs(now.getTime());
                        userHeroFragDao.insert(userHeroFragEntity);
                    }
                }
            }
            Map<String,Object> map = new HashMap<>();
            long dType = gifts.get(entry.getKey()).getDType();
            map.put("heroName", gifts.get(entry.getKey()).getHeroName());
            map.put("heroIconUrl",gifts.get(entry.getKey()).getHeroIconUrl());
            map.put("starCode", dType == 0 ? gifts.get(entry.getKey()).getGmStarCode() : 0);
            map.put("heroFragNum", dType == 1 ? gifts.get(entry.getKey()).getGmHeroFragNum() : 1);
            map.put("heroType", gifts.get(entry.getKey()).getDType());
            gifList.add(map);
            LOGGER.info(gifts.get(entry.getKey()).getGmHeroStarId() + ", count=" + entry.getValue() + ", probability="
                    + entry.getValue());
        }
        return gifList;
    }

    private HeroDrawGiftDtoEntity AddHdGiftInfo(HeroDrawGiftDtoEntity hd, long type)throws Exception{
        HeroDrawGiftDtoEntity heroDrawGiftDtoEntity = new HeroDrawGiftDtoEntity();
        if (type == 0){
//            heroDrawGiftDtoEntity.setGmHeroId(hd.getGmHeroId());
//            heroDrawGiftDtoEntity.setGmHeroStarId(hd.getGmHeroStarId());
//            heroDrawGiftDtoEntity.setHeroName(hd.getHeroName());
//            heroDrawGiftDtoEntity.setGmStarCode(hd.getGmStarCode());
//            heroDrawGiftDtoEntity.setHeroIconUrl(hd.getHeroIconUrl());
//            heroDrawGiftDtoEntity.setPron(hd.getPron());
            BeanUtils.copyProperties(heroDrawGiftDtoEntity,hd);
            heroDrawGiftDtoEntity.setDType(type);
        } else {
//            heroDrawGiftDtoEntity.setGmHeroFragId(hd.getGmHeroFragId());
//            heroDrawGiftDtoEntity.setPron(hd.getPron());
//            heroDrawGiftDtoEntity.setHeroName(hd.getHeroName());
//            heroDrawGiftDtoEntity.setHeroIconUrl(hd.getHeroIconUrl());
//            heroDrawGiftDtoEntity.setGmHeroFragNum(hd.getGmHeroFragNum());
            BeanUtils.copyProperties(heroDrawGiftDtoEntity,hd);
            heroDrawGiftDtoEntity.setDType(type);

        }
        return heroDrawGiftDtoEntity;
    }


    // 装备抽奖池
    public List<Object> equipDrawStart(UserEntity user, DrawForm drawForm) throws Exception {
        // 奖品发放集合
        List<Object> gifList;

        // 先获取抽奖概率等级 概率等级越高 获奖几率越低
        Map<String, Object> proMap = new HashMap<>();
        proMap.put("STATUS", Constant.enable);
        proMap.put("GM_TYPE", Constant.ItemType.EQUIPMENT.getValue());
        List<ProbabilityEntity> probability = probabilityDao.selectByMap(proMap);
        // 创建一个储存抽奖物品+概率的集合
        List<EquipDrawGiftDtoEntity> gifts = EquipmentList(probability);
        // 开始奖品发放
        gifList = EQDrawGiftPub(gifts, user, drawForm, null);
        return gifList;
    }

    private EquipDrawGiftDtoEntity AddEqGiftInfo(EquipDrawGiftDtoEntity entity){
        EquipDrawGiftDtoEntity entity1 = new EquipDrawGiftDtoEntity();
        entity1.setEquipmentFragId(entity.getEquipmentFragId());
        entity1.setPron(entity.getPron());
        entity1.setEquipRarecode(entity.getEquipRarecode());
        entity1.setEquipName(entity.getEquipName());
        entity1.setEquipIconUrl(entity.getEquipIconUrl());
        entity1.setEquipmentFragNum(entity.getEquipmentFragNum());
        entity1.setDType(1L);
        return entity1;
    }


    // 经验药水抽奖池
    public List<Object> exDrawStart(UserEntity user, DrawForm drawForm){
        // 先获取抽奖概率等级 概率等级越高 获奖几率越低
        Map<String,Object> proMap = new HashMap<>();
        proMap.put("STATUS", Constant.enable);
        proMap.put("GM_TYPE", Constant.ItemType.EXPERIENCE.getValue());
        List<ProbabilityEntity> probability = probabilityDao.selectByMap(proMap);
        // 创建一个储存抽奖物品+概率的集合
        List<ExDrawGiftDtoEntity> gifts = new ArrayList<>();
        for (ProbabilityEntity aProbability : probability) {
            double pron = aProbability.getGmPron();
            // 通过装备稀有度等级获取对应的装备
            Map<String, Object> exMap = new HashMap<>();
            exMap.put("STATUS", 1);
            List<ExperiencePotionEntity> exs = experiencePotionDao.selectByMap(exMap);
            for (ExperiencePotionEntity ex : exs) {
                ExDrawGiftDtoEntity entity = new ExDrawGiftDtoEntity();
                long pronLv = aProbability.getGmPronLv();
                long exPotionId = ex.getGmExPotionId();
                String exRC = ex.getExPotionRareCode();
                entity.setExId(exPotionId);
                entity.setPron(pron);
                entity.setExValue(ex.getExValue());
                entity.setExName(ex.getExPotionName());
                entity.setExIconUrl(ex.getExIconUrl());
                entity.setExRare(ex.getExPotionRareCode());
                entity.setExDescription(ex.getExDescription());
                if (pronLv == 1) {
                    // 概率等级为1
                    if (exRC.equals(Constant.rareCode1)) {
                        // 如果稀有度为白色
                        entity.setExNum(2L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode2)) {
                        // 如果稀有度为绿色
                        entity.setExNum(1L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                } else if (pronLv == 2) {
                    // 概率等级为2
                    if (exRC.equals(Constant.rareCode1)) {
                        // 如果稀有度为白色
                        entity.setExNum(4L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode2)) {
                        // 如果稀有度为绿色
                        entity.setExNum(2L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode3)) {
                        // 如果稀有度为蓝色
                        entity.setExNum(1L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                } else if (pronLv == 3) {
                    // 概率等级为3
                    if (exRC.equals(Constant.rareCode1)) {
                        // 如果稀有度为白色
                        entity.setExNum(6L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode2)) {
                        // 如果稀有度为绿色
                        entity.setExNum(3L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode3)) {
                        // 如果稀有度为蓝色
                        entity.setExNum(2L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode4)) {
                        // 如果稀有度为紫色
                        entity.setExNum(1L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                } else if (pronLv == 4) {
                    // 概率等级为3
                    if (exRC.equals(Constant.rareCode1)) {
                        // 如果稀有度为白色
                        entity.setExNum(8L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode2)) {
                        // 如果稀有度为绿色
                        entity.setExNum(5L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode3)) {
                        // 如果稀有度为蓝色
                        entity.setExNum(3L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.rareCode4)) {
                        // 如果稀有度为紫色
                        entity.setExNum(2L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                }
            }
        }
        //获取每件物品的中奖概率
        List<Double> orignalRates = new ArrayList<Double>(gifts.size());
        for (ExDrawGiftDtoEntity gift: gifts) {
            double probabilityN = gift.getPron();
            if (probabilityN < 0) {
                probabilityN = 0;
            }
            orignalRates.add(probabilityN);
        }

        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates,Long.parseLong(drawForm.getDrawType()));

        // 奖品集合
        List<Object> gifList = new ArrayList<>();
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            Date now = new Date();
            Long exNum = gifts.get(entry.getKey()).getExNum();
            // 加一层校验 抽奖池经验药水数量不小于0
            if(exNum > 0) {
                for (int fn = 0; fn < exNum; fn++) {
                    UserExperiencePotionEntity userEx = new UserExperiencePotionEntity();
                    userEx.setGmExPotionId(gifts.get(entry.getKey()).getExId());
                    userEx.setGmUserId(user.getUserId());
                    userEx.setUserExNum(exNum);
                    userEx.setStatus(Constant.enable);
                    userEx.setMintStatus(Constant.enable);
                    userEx.setMintHash(drawForm.getTransactionHash());
                    userEx.setCreateTime(now);
                    userEx.setCreateTimeTs(now.getTime());
                    userExperiencePotionDao.insert(userEx);
                }
            }
            Map<String,Object> map = new HashMap<>();
            map.put("exNum", gifts.get(entry.getKey()).getExNum());
            map.put("exName", gifts.get(entry.getKey()).getExName());
            map.put("exRare", gifts.get(entry.getKey()).getExRare());
            map.put("exValue", gifts.get(entry.getKey()).getExValue());
            map.put("exIconUrl",gifts.get(entry.getKey()).getExIconUrl());
            map.put("exDescription", gifts.get(entry.getKey()).getExDescription());
            gifList.add(map);
            LOGGER.info(gifts.get(entry.getKey()).getExId() + ", count=" + entry.getValue() + ", probability="
                    + entry.getValue());
        }
        return gifList;
    }

    private ExDrawGiftDtoEntity AddExGiftInfo(ExDrawGiftDtoEntity entity){
        ExDrawGiftDtoEntity ex = new ExDrawGiftDtoEntity();
        ex.setExId(entity.getExId());
        ex.setPron(entity.getPron());
        ex.setExValue(entity.getExValue());
        ex.setExName(entity.getExName());
        ex.setExNum(entity.getExNum());
        ex.setExIconUrl(entity.getExIconUrl());
        ex.setExRare(entity.getExRare());
        ex.setExDescription(entity.getExDescription());
        return ex;
    }

    // 装备抽奖公用接口
    private List<Object> EQDrawGiftPub(List<EquipDrawGiftDtoEntity> gifts,UserEntity user, DrawForm drawForm, GmCombatRecordEntity combatRecordEntity){

        //获取每件物品的中奖概率
        List<Double> orignalRates = new ArrayList<Double>(gifts.size());
        for (EquipDrawGiftDtoEntity gift: gifts) {
            double probabilityN = gift.getPron();
            if (probabilityN < 0) {
                probabilityN = 0;
            }
            orignalRates.add(probabilityN);
        }

        // 抽奖次数
        long drawNum = 0;
        // 如果为副本产出分为3级别每级对应抽奖次数 为抽奖产出固定单抽十连抽
        if (combatRecordEntity != null){
            // 随机副本产出装备数量
            Random rm = new Random();
            int rmon = rm.nextInt(4);
            if ( rmon < 1 ) {
                rmon = 1;
            }
            drawNum = rmon;
        } else {
            if ("1".equals(drawForm.getDrawType())) {
                drawNum = Constant.DrawNum.DRAW1.getValue();
            } else if ("2".equals(drawForm.getDrawType())){
                drawNum = Constant.DrawNum.DRAW10.getValue();
            }
        }

        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, drawNum);

        // 奖品集合
        List<Object> gifList = new ArrayList<>();
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            Date now = new Date();
            // 判断奖品是否为星级英雄 Dtype=0 为装备, Dtype=1 为装备卷轴碎片
            if(gifts.get(entry.getKey()).getDType() == 0){
                UserEquipmentEntity userEquip = new UserEquipmentEntity();
                userEquip.setGmEquipmentId(gifts.get(entry.getKey()).getEquipId());
                userEquip.setGmUserId(user.getUserId());
                userEquip.setStatus(Constant.enable);

                userEquip.setCreateTime(now);
                userEquip.setCreateTimeTs(now.getTime());
                // 统计装备战力
                double equipPower = 0;
                long health = gifts.get(entry.getKey()).getEquipHealth() != null ? gifts.get(entry.getKey()).getEquipHealth() : 0;//初始生命值
                long mana = gifts.get(entry.getKey()).getEquipMana() != null ? gifts.get(entry.getKey()).getEquipMana() : 0;//初始法力值
                long healthRegen = gifts.get(entry.getKey()).getEquipHealthRegen() != null ? gifts.get(entry.getKey()).getEquipHealthRegen() : 0;//初始生命值恢复
                long manaRegen = gifts.get(entry.getKey()).getEquipManaRegen() != null ? gifts.get(entry.getKey()).getEquipManaRegen() : 0;//初始法力值恢复
                long armor = gifts.get(entry.getKey()).getEquipArmor() != null ? gifts.get(entry.getKey()).getEquipArmor() : 0;//gmArmor
                long magicResist = gifts.get(entry.getKey()).getEquipMagicResist() != null ? gifts.get(entry.getKey()).getEquipMagicResist() : 0;//初始魔抗
                long attackDamage = gifts.get(entry.getKey()).getEquipAttackDamage() != null ? gifts.get(entry.getKey()).getEquipAttackDamage() : 0;//初始攻击力
                long attackSpell = gifts.get(entry.getKey()).getEquipAttackSpell() != null ? gifts.get(entry.getKey()).getEquipAttackSpell() : 0;//初始法攻
                // 生成NFT唯一编码
                userEquip.setNftId(Arith.UUID20().longValue());
                // 区分抽奖或副本产出
                if (combatRecordEntity != null) {
                    userEquip.setFromType("0");
                    userEquip.setSourceId(combatRecordEntity.getId());
                } else {
                    userEquip.setFromType("1");
                    userEquip.setMintStatus(Constant.enable);
                    userEquip.setMintHash(drawForm.getTransactionHash());
                }
                // 随机装备属性
                Double rap = Arith.randomWithinRangeHundred(Constant.eqAttMax, Constant.eqAttMin);
                userEquip.setEquipHealth((long) (health * rap));
                userEquip.setEquipMana((long) (mana * rap));
                userEquip.setEquipHealthRegen((long) (healthRegen * rap));
                userEquip.setEquipManaRegen((long) (manaRegen * rap));
                userEquip.setEquipArmor((long) (armor * rap));
                userEquip.setEquipMagicResist((long) (magicResist * rap));
                userEquip.setEquipAttackDamage((long) (attackDamage * rap));
                userEquip.setEquipAttackSpell((long) (attackSpell * rap));
                // 更新装备战力
                equipPower = (health * 0.1) + (mana * 0.1) + attackDamage + attackSpell + ((armor + magicResist) * 4.5) + healthRegen * 0.1 + manaRegen * 0.3;
                userEquip.setEquipPower((long) equipPower);

                userEquipmentDao.insert(userEquip);

                JSONObject jsonEQ = new JSONObject();
                jsonEQ.put("equipName", gifts.get(entry.getKey()).getEquipName());
                jsonEQ.put("equipIconUrl",gifts.get(entry.getKey()).getEquipIconUrl());
                jsonEQ.put("equipRarecode", gifts.get(entry.getKey()).getEquipRarecode());
                jsonEQ.put("equipLevel", gifts.get(entry.getKey()).getEquipLevel());
                jsonEQ.put("equipPower", equipPower);
                jsonEQ.put("equipType", gifts.get(entry.getKey()).getDType());
                jsonEQ.put("equipHealth", gifts.get(entry.getKey()).getEquipHealth());
                jsonEQ.put("equipMana", gifts.get(entry.getKey()).getEquipMana());
                jsonEQ.put("equipHealthRegen", gifts.get(entry.getKey()).getEquipHealthRegen());
                jsonEQ.put("equipManaRegen", gifts.get(entry.getKey()).getEquipManaRegen());
                jsonEQ.put("equipArmor", gifts.get(entry.getKey()).getEquipArmor());
                jsonEQ.put("equipMagicResist", gifts.get(entry.getKey()).getEquipMagicResist());
                jsonEQ.put("equipAttackDamage", gifts.get(entry.getKey()).getEquipAttackDamage());
                jsonEQ.put("equipAttackSpell", gifts.get(entry.getKey()).getEquipAttackSpell());
                gifList.add(jsonEQ);
            } else if (gifts.get(entry.getKey()).getDType() == 1){
                Long fragNum = gifts.get(entry.getKey()).getEquipmentFragNum();
                // 加一层校验 抽奖池英雄碎片数量不小于0
                if(fragNum > 0) {
                    for (int fn = 0; fn < fragNum; fn++) {
                        UserEquipmentFragEntity userEquipFrag = new UserEquipmentFragEntity();
                        userEquipFrag.setGmEquipmentFragId(gifts.get(entry.getKey()).getEquipmentFragId());
                        userEquipFrag.setGmUserId(user.getUserId());
                        userEquipFrag.setGmUserEquipFragNum(fragNum);
                        userEquipFrag.setStatus(Constant.enable);
                        // 区分抽奖或副本产出
                        if (combatRecordEntity != null) {
                            userEquipFrag.setFromType("0");
                            userEquipFrag.setSourceId(combatRecordEntity.getId());
                        } else {
                            userEquipFrag.setFromType("1");
                            userEquipFrag.setMintStatus(Constant.enable);
                            userEquipFrag.setMintHash(drawForm.getTransactionHash());
                        }
                        userEquipFrag.setCreateTime(now);
                        userEquipFrag.setCreateTimeTs(now.getTime());
                        userEquipmentFragDao.insert(userEquipFrag);
                    }
                }
                JSONObject jsonEQ = new JSONObject();
                jsonEQ.put("equipName", gifts.get(entry.getKey()).getEquipName());
                jsonEQ.put("equipIconUrl",gifts.get(entry.getKey()).getEquipIconUrl());
                jsonEQ.put("equipFragNum", gifts.get(entry.getKey()).getEquipmentFragNum());
                jsonEQ.put("equipType", gifts.get(entry.getKey()).getDType());
                gifList.add(jsonEQ);
            }

            LOGGER.info(gifts.get(entry.getKey()).getEquipId() + ", count=" + entry.getValue() + ", probability="
                    + entry.getValue());
        }
        return gifList;
    }

    // 储存装备物品集合
    private List<EquipDrawGiftDtoEntity> EquipmentList(List<ProbabilityEntity> probabilityEntities) throws Exception {
        // 创建一个储存装备物品+概率的集合
        List<EquipDrawGiftDtoEntity> gifts = new ArrayList<>();
        for (ProbabilityEntity aProbability : probabilityEntities) {
            // 通过装备稀有度等级获取对应的装备
            Map<String, Object> eqMap = new HashMap<>();
            eqMap.put("STATUS", Constant.enable);
            eqMap.put("EQUIP_RARECODE", aProbability.getGmPronLv());
            List<EquipmentInfoEntity> equips = equipmentInfoDao.selectByMap(eqMap);
            for (EquipmentInfoEntity equip : equips) {
                EquipDrawGiftDtoEntity drawDtoEntity = new EquipDrawGiftDtoEntity();
                BeanUtils.copyProperties(drawDtoEntity,equip);
                drawDtoEntity.setPron(aProbability.getGmPron() * 0.8);
                drawDtoEntity.setDType(0L);

                if (aProbability.getGmPronLv() == 1) {
                    // 概率为1级
                    gifts.add(drawDtoEntity);
                } else if (aProbability.getGmPronLv() == 2) {
                    // 概率为2级
                    gifts.add(drawDtoEntity);
                } else if (aProbability.getGmPronLv() == 3) {
                    // 概率为3级
                    gifts.add(drawDtoEntity);
                } else if (aProbability.getGmPronLv() == 4) {
                    // 概率为4级
                    gifts.add(drawDtoEntity);
                } else if (aProbability.getGmPronLv() == 5) {
                    // 概率为5级
                    gifts.add(drawDtoEntity);
                }
            }

            // 获取奖池全部装备卷轴碎片，目前装备卷轴碎片仅包含绿色，紫色，橙色
            List<EquipmentFragEntity> equipFrags = equinpmentFragDao.getEquipFragInfo();
            for (EquipmentFragEntity equipFrag : equipFrags) {
                EquipDrawGiftDtoEntity entity = new EquipDrawGiftDtoEntity();
                entity.setEquipmentFragId(equipFrag.getEquipmentFragId());
                entity.setPron(aProbability.getGmPron());
                entity.setEquipName(equipFrag.getEquipName());
                entity.setEquipIconUrl(equipFrag.getEquipFragIconUrl());
                String eqRC = equipFrag.getEquipRarecode();
                entity.setEquipRarecode(eqRC);
                if (aProbability.getGmPronLv() == 1) {
                    // 概率为1级
                    // 如果装备卷轴碎片稀有度为绿色将封装1-3级的概率
                    if (eqRC.equals(Constant.rareCode2)) {
                        entity.setEquipmentFragNum(1L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                } else if (aProbability.getGmPronLv() == 2) {
                    // 概率为2级
                    // 如果装备卷轴碎片稀有度为绿色将封装1-3级的概率
                    if (eqRC.equals(Constant.rareCode2)) {
                        entity.setEquipmentFragNum(2L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                } else if (aProbability.getGmPronLv() == 3) {
                    // 概率为3级
                    // 如果装备卷轴碎片稀有度为绿色将封装1-3级的概率
                    if (eqRC.equals(Constant.rareCode2)) {
                        entity.setEquipmentFragNum(3L);
                        gifts.add(AddEqGiftInfo(entity));
                    }

                    // 如果装备卷轴碎片稀有度为紫色将封装3-4级的概率
                    if (eqRC.equals(Constant.rareCode4)) {
                        entity.setEquipmentFragNum(1L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                } else if (aProbability.getGmPronLv() == 4) {
                    // 概率为4级
                    // 如果装备卷轴碎片稀有度为紫色将封装3-4级的概率
                    if (eqRC.equals(Constant.rareCode4)) {
                        entity.setEquipmentFragNum(2L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                } else if (aProbability.getGmPronLv() == 5) {
                    // 概率为5级
                    // 如果装备卷轴碎片稀有度为橙色将封装4级的概率
                    if (eqRC.equals(Constant.rareCode5)) {
                        entity.setEquipmentFragNum(1L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                }


            }
        }
        return gifts;
    }

    // 副本产出的装备
    public List<Object> dungeonRewardsEQ(UserEntity user, GmDungeonConfigEntity dungeon, GmCombatRecordEntity combatRecord) throws Exception {
        // 奖品发放集合
        List<Object> gifList;
        // 先获概率等级 概率等级越高 获奖几率越低
        String[] eqRCs = dungeon.getRangeEquip().split("-");
        List<String> eqRCList = new ArrayList<>();
        eqRCList.addAll(Arrays.asList(eqRCs));

        List<ProbabilityEntity> probability = probabilityDao.selectList(new QueryWrapper<ProbabilityEntity>()
                .eq("STATUS", Constant.enable)
                .eq("GM_TYPE", Constant.ItemType.EQUIPMENT.getValue())
                .in("GM_PRON_LV", eqRCList)
        );

        // 创建一个储存装备物品+概率的集合
        List<EquipDrawGiftDtoEntity> gifts = EquipmentList(probability);
        DrawForm drawForm = new DrawForm();
        drawForm.setDrawType(Constant.enable);// 单抽

        // 开始奖品发放
        gifList = EQDrawGiftPub(gifts, user, drawForm, combatRecord);
        return gifList;
    }


    public static void main(String[] args) {
        List<DrawDtoTestEntity> gifts = new ArrayList<>();
        DrawDtoTestEntity nothing1 = new DrawDtoTestEntity("英雄碎片1", 35.5);
        DrawDtoTestEntity nothing2 = new DrawDtoTestEntity("英雄碎片2", 10.5);
        DrawDtoTestEntity nothing3 = new DrawDtoTestEntity("英雄碎片3", 5.5);
        DrawDtoTestEntity nothing4 = new DrawDtoTestEntity("英雄碎片4", 0.25);
        DrawDtoTestEntity nothing5 = new DrawDtoTestEntity("英雄碎片5", 0.025);
        DrawDtoTestEntity hero1 = new DrawDtoTestEntity("1☆英雄A", 25.215);
        DrawDtoTestEntity hero2 = new DrawDtoTestEntity("2☆英雄A", 15.35);
        DrawDtoTestEntity hero3 = new DrawDtoTestEntity("3☆英雄A", 7.15);
        DrawDtoTestEntity hero4 = new DrawDtoTestEntity("4☆英雄A", 0.1);
        DrawDtoTestEntity hero5 = new DrawDtoTestEntity("5☆英雄A", 0.01);
        DrawDtoTestEntity hero1B = new DrawDtoTestEntity("1☆英雄B", 25.215);
        DrawDtoTestEntity hero2B = new DrawDtoTestEntity("2☆英雄B", 15.35);
        DrawDtoTestEntity hero3B = new DrawDtoTestEntity("3☆英雄B", 7.15);
        DrawDtoTestEntity hero4B = new DrawDtoTestEntity("4☆英雄B", 0.1);
        DrawDtoTestEntity hero5B = new DrawDtoTestEntity("5☆英雄B", 0.01);
        DrawDtoTestEntity hero1C = new DrawDtoTestEntity("1☆英雄C", 25.215);
        DrawDtoTestEntity hero2C = new DrawDtoTestEntity("2☆英雄C", 15.35);
        DrawDtoTestEntity hero3C = new DrawDtoTestEntity("3☆英雄C", 7.15);
        DrawDtoTestEntity hero4C = new DrawDtoTestEntity("4☆英雄C", 0.1);
        DrawDtoTestEntity hero5C = new DrawDtoTestEntity("5☆英雄C", 0.01);
        DrawDtoTestEntity hero1D = new DrawDtoTestEntity("1☆英雄D", 25.215);
        DrawDtoTestEntity hero2D = new DrawDtoTestEntity("2☆英雄D", 15.35);
        DrawDtoTestEntity hero3D = new DrawDtoTestEntity("3☆英雄D", 7.15);
        DrawDtoTestEntity hero4D = new DrawDtoTestEntity("4☆英雄D", 0.1);
        DrawDtoTestEntity hero5D = new DrawDtoTestEntity("5☆英雄D", 0.01);

        gifts.add(nothing1);
        gifts.add(nothing2);
        gifts.add(nothing3);
        gifts.add(nothing4);
        gifts.add(nothing5);
        gifts.add(hero1);
        gifts.add(hero2);
        gifts.add(hero3);
        gifts.add(hero4);
        gifts.add(hero5);
        gifts.add(hero1B);
        gifts.add(hero2B);
        gifts.add(hero3B);
        gifts.add(hero4B);
        gifts.add(hero5B);
        gifts.add(hero1C);
        gifts.add(hero2C);
        gifts.add(hero3C);
        gifts.add(hero4C);
        gifts.add(hero5C);
        gifts.add(hero1D);
        gifts.add(hero2D);
        gifts.add(hero3D);
        gifts.add(hero4D);
        gifts.add(hero5D);


        //获取每件物品的中奖概率
        List<Double> orignalRates = new ArrayList<Double>(gifts.size());
        for (DrawDtoTestEntity gift: gifts) {
            double probability = gift.getGmProbability();
            if (probability < 0) {
                probability = 0;
            }
            orignalRates.add(probability);
        }

        // statistics
        Map<Integer, Integer> count = new HashMap<Integer, Integer>();
        //抽奖次数
        double num = Constant.DrawNum.DRAW1.getValue();
        for (int i = 0; i < num; i++) {
            int orignalIndex = LotteryGiftsUtils.lottery(orignalRates);

            Integer value = count.get(orignalIndex);
            count.put(orignalIndex, value == null ? 1 : value + 1);
        }

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            System.out.println(gifts.get(entry.getKey()).getHeroName() + ", count=" + entry.getValue() + ", probability="
                    + entry.getValue() / num);
        }
    }

}

