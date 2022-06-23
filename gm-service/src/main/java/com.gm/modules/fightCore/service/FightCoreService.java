package com.gm.modules.fightCore.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.utils.Arith;
import com.gm.common.utils.CalculateTradeUtil;
import com.gm.common.utils.Constant;
import com.gm.common.utils.LotteryGiftsUtils;
import com.gm.modules.basicconfig.dao.*;
import com.gm.modules.basicconfig.dto.AttributeEntity;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.dao.*;
import com.gm.modules.user.entity.GmMiningInfoEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.entity.UserHeroEquipmentWearEntity;
import javafx.beans.binding.ObjectExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("fightCoreService")
public class FightCoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FightCoreService.class);
    @Autowired
    private UserHeroDao userHeroDao;
    @Autowired
    private HeroStarDao heroStarDao;
    @Autowired
    private GmTeamConfigDao teamConfigDao;
    @Autowired
    private CombatStatsUtilsService combatStatsUtilsService;
    @Autowired
    private GmDungeonConfigDao dungeonConfigDao;
    @Autowired
    private GmMonsterConfigDao monsterConfigDao;
    @Autowired
    private GmDungeonEventDao dungeonEventDao;
    @Autowired
    private GmMiningInfoDao gmMiningInfoDao;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private UserDao userDao;


    private static Long userId = 1l;


    public void attck(UserEntity user) {
        // 获取最新战力
        long teamPower = 0;

        // 获取队伍中的英雄信息及英雄技能英雄装备信息
        // 获取未战斗的队伍
        GmTeamConfigEntity gmTeamConfig = teamConfigDao.selectOne(new QueryWrapper<GmTeamConfigEntity>()
                        .eq("STATUS", Constant.disabled)
                        .eq("ID",userId)// 玩家英雄ID
        );
        if (gmTeamConfig == null){
            System.out.println("获取队伍失败");
        }

        // 创建集合存储每个英雄的属性
        List<AttributeEntity> attributes = new ArrayList<>();
        // 获取英雄1
        if(gmTeamConfig.getHero1Id() != null){
            AttributeEntity att = getHeroStats(gmTeamConfig.getHero1Id());
            attributes.add(att);
            long heroPower = combatStatsUtilsService.getHeroPower(att);
            teamPower = teamPower + heroPower;
        }
        // 获取英雄2
        if(gmTeamConfig.getHero2Id() != null){
            AttributeEntity att = getHeroStats(gmTeamConfig.getHero2Id());
            attributes.add(att);
            long heroPower = combatStatsUtilsService.getHeroPower(att);
            teamPower = teamPower + heroPower;
        }
        // 获取英雄3
        if(gmTeamConfig.getHero3Id() != null){
            AttributeEntity att = getHeroStats(gmTeamConfig.getHero3Id());
            attributes.add(att);
            long heroPower = combatStatsUtilsService.getHeroPower(att);
            teamPower = teamPower + heroPower;
        }
        // 获取英雄4
        if(gmTeamConfig.getHero4Id() != null){
            AttributeEntity att = getHeroStats(gmTeamConfig.getHero4Id());
            attributes.add(att);
            long heroPower = combatStatsUtilsService.getHeroPower(att);
            teamPower = teamPower + heroPower;
        }
        // 获取英雄5
        if(gmTeamConfig.getHero5Id() != null){
            AttributeEntity att = getHeroStats(gmTeamConfig.getHero5Id());
            attributes.add(att);
            long heroPower = combatStatsUtilsService.getHeroPower(att);
            teamPower = teamPower + heroPower;
        }

        // 获取副本信息
        GmDungeonConfigEntity dungeon = dungeonConfigDao.selectById(1532633913572757505l);
        if (dungeon == null){
            System.out.println("副本已关闭");
        }

        // 玩家体力值校验(小于副本所需体力无法战斗)
        if(user.getFtg() < dungeon.getRequiresStamina()){
            System.out.println("玩家疲劳值不足");
        }

        // 获取副本事件信息
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("STATUS", Constant.enable);
        eventMap.put("DUNGEON_ID", dungeon.getId());
        List<GmDungeonEventEntity> dungeonEvents = dungeonEventDao.selectByMap(eventMap);
        if (dungeonEvents == null) {
            System.out.println("获取副本事件信息失败");
        }

        // 获取怪物信息
        Map<String,Object> monsterMap = new HashMap<>();
        monsterMap.put("STATUS", Constant.enable);
        monsterMap.put("DUNGEON_ID", dungeon.getId());
        List<GmMonsterConfigEntity> monsters = monsterConfigDao.selectByMap(monsterMap);
        if (monsters == null) {
            System.out.println("怪物修炼中");
        } else {
            Collections.shuffle(monsters);
        }

        // 更新队伍战力 更新队伍战斗状态（后期开放)
        GmTeamConfigEntity upTeam = new GmTeamConfigEntity();
        upTeam.setId(gmTeamConfig.getId());
//        upTeam.setStatus(Constant.enable);// 战斗中
        upTeam.setTeamPower(teamPower);
        teamConfigDao.updateById(upTeam);

        // 扣除玩家体力
        UserEntity userFTG = new UserEntity();
        if (user != null) {
            userFTG.setUserId(user.getUserId());
            userFTG.setFtg(user.getFtg() - dungeon.getRequiresStamina());
            userDao.updateById(userFTG);
        } else {
            System.out.println("玩家信息获取失败");
        }

        // 开始战斗
        // 随机战斗副本，战斗描述
        System.out.println("尊敬的领主指引[" + gmTeamConfig.getTeamName() + "] 战力[" + upTeam.getTeamPower() + "] 进入[" + dungeon.getDungeonName() + "]");
        // 战斗状态
        long status = 0;

        //获取每个事件的概率
        List<Double> orignalRates = new ArrayList<Double>(dungeonEvents.size());
        for (GmDungeonEventEntity event: dungeonEvents) {
            double probabilityN = event.getEventPron();
            if (probabilityN < 0) {
                probabilityN = 0;
            }
            orignalRates.add(probabilityN);
        }
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates,(long) Constant.drawOne);
        // 随机的事件
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {

            String eventDescription = dungeonEvents.get(entry.getKey()).getEventDescription();
            long eventLevel = dungeonEvents.get(entry.getKey()).getEventLevel();
            System.out.println("事件等级：" + eventLevel);
            eventDescription = eventDescription.replace("-","["  + dungeon.getDungeonName() + "]");
            System.out.println(eventDescription);

            // 事件等级5 为福利关卡 无需战斗 玩家直接胜利
            if ( eventLevel == 5L ) {
                status = 2;
            } else {
                status = attcks(attributes, monsters, dungeonEvents.get(entry.getKey()).getEventLevel(), dungeon);
            }
        }

        // 怪物全部击杀 战斗胜利 获取奖励结算信息
        if ( status == 2){
            // 获取系统全部玩家总战力
            CalculateTradeUtil.totalPower = BigDecimal.valueOf(getTotalPower());

            // 获取玩家矿工信息
            GmMiningInfoEntity miningInfo = gmMiningInfoDao.selectOne(new QueryWrapper<GmMiningInfoEntity>()
                            .eq("STATUS", Constant.enable)
                            .eq("USER_ID",userId)// 玩家ID
            );

            if (miningInfo == null) {
                System.out.println("玩家矿工获取失败");
            }

            // 开始计算玩家收益
            // 获取市场总鸡蛋数量
            String totalEggs = sysConfigService.getValue(Constant.MARKET_EGGS);
            CalculateTradeUtil.marketEggs = new BigDecimal(totalEggs);
            // 玩家赚取总收入
            String totalPlayersGold = sysConfigService.getValue(Constant.PLAYERS_EARN_TOTAL_REVENUE);
            // 玩家矿工数量
            if (miningInfo != null) {
                CalculateTradeUtil.miners = new BigDecimal(miningInfo.getMiners());
            }
            // 玩家鸡蛋数量
            if (miningInfo != null) {
                CalculateTradeUtil.claimedEggs = new BigDecimal(miningInfo.getClaimedEggs());
            }
            // 玩家上次购买矿工的时间
            if (miningInfo != null) {
                CalculateTradeUtil.lastHatch = new BigDecimal(miningInfo.getLastHatch());
            }
            // 获取资金池70%余额
            String poolBalance = sysConfigService.getValue(Constant.CASH_POOLING_BALANCE);
            CalculateTradeUtil.FundPool = new BigDecimal((Double.parseDouble(poolBalance) * 0.7) + "");
            // 副本资金池余额（最新）
            BigDecimal dungeonPoolBal = CalculateTradeUtil.FundPool;
            // 获取扣除用户总收益后的资金池余额
            CalculateTradeUtil.FundPool = Arith.subtract(CalculateTradeUtil.FundPool,new BigDecimal(totalPlayersGold));
            // 获取当前副本奖励分配百分比
            CalculateTradeUtil.FundPool = Arith.multiply(CalculateTradeUtil.FundPool,BigDecimal.valueOf(dungeon.getRewardDistribution()));

            System.out.println("当前副本池子余额:" + CalculateTradeUtil.FundPool);

            // 购买
            CalculateTradeUtil.claimedEggs = BigDecimal.valueOf(0);
            // 获取用户战力
            CalculateTradeUtil.userpower = Arith.divide(BigDecimal.valueOf(teamPower),BigDecimal.valueOf(100));
            // 购买鸡蛋
            CalculateTradeUtil.buyEggs();
            System.out.println("marketEggs:" + CalculateTradeUtil.marketEggs);

            // 出售
            CalculateTradeUtil.time = Arith.add(Arith.divide(BigDecimal.valueOf(System.currentTimeMillis()),BigDecimal.valueOf(1000)),Arith.multiply(CalculateTradeUtil.EGGS_TO_HATCH_1MINERS,BigDecimal.valueOf(1)));
            System.out.println("marketEggs:" + CalculateTradeUtil.marketEggs);
            System.out.println("miners:" + CalculateTradeUtil.miners);
            System.out.println("lastHatch:" + CalculateTradeUtil.lastHatch);

            CalculateTradeUtil.sellEggs();
            System.out.println("totalPower:"+CalculateTradeUtil.totalPower);

            // 更新玩家矿工收据
            GmMiningInfoEntity mini = new GmMiningInfoEntity();
            mini.setId(miningInfo.getId());
            mini.setMiners(CalculateTradeUtil.miners.toString());
            mini.setLastHatch(CalculateTradeUtil.lastHatch.toString());
            gmMiningInfoDao.updateById(mini);
            // 更新系统中保存的市场总鸡蛋
            sysConfigService.updateValueByKey(Constant.MARKET_EGGS, CalculateTradeUtil.marketEggs.toString());
            // 更新系统中保存的玩家赚取总收入
            totalPlayersGold = Arith.add(new BigDecimal(totalPlayersGold), CalculateTradeUtil.userGetGold).toString();
            sysConfigService.updateValueByKey(Constant.PLAYERS_EARN_TOTAL_REVENUE, totalPlayersGold);
            // 更新系统中保存的副本资金池余额
            dungeonPoolBal = Arith.subtract(dungeonPoolBal, new BigDecimal(totalPlayersGold));
            sysConfigService.updateValueByKey(Constant.DUNGEON_POOLING_BALANCE, dungeonPoolBal.toString());
            System.out.println("===================怪物全部击杀 战斗胜利===================");
        }



    }

    // 获取系统全部用户总战力
    private Long getTotalPower(){
        long total = 0;
        Map<String,Object> map = new HashMap<>();
        List<UserEntity> users = userDao.selectByMap(map);
        for (UserEntity userEntity : users){
            total = total + userEntity.getTotalPower();
        }
        return total;
    }

    private long attcks(List<AttributeEntity> a ,List<GmMonsterConfigEntity> monsters, long fightEvet, GmDungeonConfigEntity dungeon) {
        long status = 0;
        int i = 0;
        // 回合数
        int totalFightNum = 0;

        int heroNum = a.size();
        // 随机怪物出现的数量
        Random rm = new Random();
        int rmon = rm.nextInt(dungeon.getMonsterNum());
        if ( rmon < 1 ) {
            rmon = 6;
        }
        // 怪物数量
        int monsterNum = rmon;
        System.out.println("怪物数量：" + monsterNum);

        while (i < a.size()) {

            // 怪物全部击杀 玩家胜利
            if (monsterNum < 1) {
                // 战斗状态 2为玩家胜利
                status = 2;
                break;
            }
            String fightContent = "";

            // 循环攻击每只怪物
            for (int m = 0; m < rmon; m++) {
                // 怪物全部击杀 玩家胜利
                if (monsterNum < 1) {
                    break;
                }
                // 玩家英雄全部被击杀 玩家失败
                if(heroNum < 1) break;
                Double skillDamage = 0.0;
                // 获取玩家普通攻击
                long attackDamag = a.get(i).getAttackDamage();
                if (a.get(i).getSkillDamageBonusHero() != null) {
                    // 获取法攻英雄技能伤害
                    skillDamage = a.get(i).getSkillFixedDamage() + (attackDamag * (a.get(i).getSkillDamageBonusHero()));

                } else if (a.get(i).getSkillDamageBonusEquip() != null) {
                    // 获取物攻英雄技能伤害
                    skillDamage = a.get(i).getSkillFixedDamage() + (a.get(i).getEquipAttackDamage() * (a.get(i).getSkillDamageBonusEquip()));
                }

                // 怪物名称
                String mName = monsters.get(m).getMonsterName();
                // 怪物等级
                long mLevel = monsters.get(m).getMonsterLevel();
                System.out.println("突然" + "[LV" + mLevel + " " + mName + "] 向你发起了进攻" + " 开始战斗:");

                // 获取当前怪物最大血量
                long mMaxHP = monsters.get(m).getMonsterHealth();
                // 获取怪物最新血量
                long mHP = monsters.get(m).getMonsterHealth();
                // 获取怪物恢复血量
                long mHPRegen = monsters.get(m).getMonsterHealthRegen();
                // 获取怪物护甲
                long mArmor = monsters.get(m).getMonsterArmor();
                // 获取怪物魔抗
                long mMagicResist = monsters.get(m).getMonsterMagicResist();

                // 英雄等级
                long hLevel = a.get(i).getHeroLevel();
                // 英雄星级
                long hStar = a.get(i).getHeroStar();
                // 英雄名称
                String hName = a.get(i).getHeroName();
                // 英雄血量
                long hMAXHP = a.get(i).getHp();
                System.out.println("[LV" + mLevel + " " + mName + " " + hStar + "★] 血量为" + mMaxHP);
                System.out.println("[LV" + hLevel + " " + hName + " " + hStar + "★] 血量为" + hMAXHP);
                while (true) {
                    // 存放普功+技能伤害
                    long at1 = (attackDamag - (mArmor + mMagicResist));
                    long at2 = (long) ((attackDamag * 1.5) - (mArmor + mMagicResist));
                    long at3 = (long) (skillDamage - (mArmor + mMagicResist));
                    long[] skillHarm = {at1, at1, at1, at2, at2, at3};
                    //随机普功、技能
                    Random rk = new Random();
                    int ras = rk.nextInt(skillHarm.length);

                    if (mHP >= 0) {
                        if (skillHarm[ras] < 1) {
                            skillHarm[ras] = 0;
                        }
                        mHP = mHP - skillHarm[ras];
                        String skillName = "使用【普通攻击】";
                        if (ras == 5) {
                            skillName = "使用【" + a.get(i).getSkillName() + "】";
                        }
                        // 回合数统计
                        totalFightNum ++;

                        System.out.println("[LV" + a.get(i).getHeroLevel() + " " + a.get(i).getHeroName() + " " + a.get(i).getHeroStar() + "★]" + skillName + "对[LV" + mLevel + " " + mName + "]造成" + skillHarm[ras] + "的伤害，[LV" + mLevel + " " + mName + "]剩余" + (mHP < 1 ? 0 : mHP) + "的血量");
                        // 随机触发被动恢复血量
                        Random rbhp = new Random();
                        int backHp = rbhp.nextInt(99);
                        if (mHP > 0) {
                            if (backHp % 3 == 0) {
                                long addhp = addHP(mHP, mHPRegen);
                                mHP = addhp > mMaxHP ? mMaxHP : addhp; // 恢复的HP
                                System.out.println("[LV" + mLevel + " " + mName + "]" + "触发被动，恢复了" + mHPRegen + "的血量，" + "剩余" + (addhp) + "的血量");
                            }
                        }

                        // 怪物死亡 血量为0
                        if (mHP <= 0) {
                            monsterNum--;
                            System.out.println("----------------------[LV" + a.get(i).getHeroLevel() + a.get(i).getHeroName() + " " + a.get(i).getHeroStar() + "★]" + "击杀了[LV" + mLevel + " " + mName + "]----------------------");
                            System.out.println("怪物剩余量:" + monsterNum);
                            break;
                        }

                        // 怪物反击玩家英雄
                        long atkHeroStatus = attckHero(monsters.get(m),a.get(i));
                        if (atkHeroStatus == 4) {
                            i++;
                            heroNum--;
                            if(heroNum < 1) break;
                        }

                    }
                }
            }
        }

        // 玩家英雄全部被击杀 玩家失败
        if(heroNum < 1) {
            status = 4;
            System.out.println("===================英雄全部被击杀 战斗失败===================");
        }
        System.out.println("回合数："+totalFightNum);
        return status;
    }

    // 恢复血量
    private long addHP(long HP, long HpRegen){
        return HP + HpRegen;
    }
    private long attckHero(GmMonsterConfigEntity a, AttributeEntity b){
        long status = 0;
        // 英雄名称
        String hName = b.getHeroName();
        // 英雄等级
        long hLevel = b.getHeroLevel();
        // 当前英雄最大血量
        long hMaxHP = b.getHp();
        // 英雄最新血量
        long hHP = b.getHp();
        // 英雄恢复血量
        long hHPRegen = b.getHpRegen();
        // 英雄护甲
        long hArmor = b.getArmor();
        // 英雄魔抗
        long hMagicResist = b.getMagicResist();
        // 英雄星级
        long hstar = b.getHeroStar();

        // 怪物名称
        String mName = a.getMonsterName();
        // 怪物等级
        long mLevel = a.getMonsterLevel();
        // 怪物普通攻击
        long mAttackDamag = a.getMonsterAttackDamage();
        // 怪物的专属技能伤害
        long uniqueSkill = mAttackDamag * a.getUniqueSkillM() - hArmor;
        // 怪物的致命一击伤害
        long criticalHit = mAttackDamag * a.getCriticalHitM() - hArmor;

        // 存放普功+技能伤害
        long at1 = (mAttackDamag - (hArmor));
        long at2 = (mAttackDamag * 2) - (hArmor);
        long[] skillHarm = {at1, at2, uniqueSkill, uniqueSkill, criticalHit};
        //随机普功、技能
        Random rk = new Random();
        int ras = rk.nextInt(skillHarm.length);
        if (skillHarm[ras] < 1) {
            skillHarm[ras] = 0;
        }

        if (hHP >= 0) {
            if (skillHarm[ras] < 1) {
                skillHarm[ras] = 1;
            }
            hHP = hHP - skillHarm[ras];
            String skillName = "使用【普通攻击】";
            if (ras == 2 || ras == 3) {
                skillName = "使用【专属技能】";
            } else if (ras == 4) {
                skillName += "触发了【致命一击】";
            }
            System.out.println("[LV" + mLevel + " " + mName + "]" + skillName + "对[LV" + hLevel + " " + hName + " " + hstar + "★]造成" + skillHarm[ras] + "的伤害，[" + hName + "]剩余" + (hHP < 1 ? 0 : hHP) + "的血量");
            // 随机恢复血量
            Random rbhp = new Random();
            int backHp = rbhp.nextInt(99);
            if (hHP > 0) {
                if (backHp % 4 == 0) {
                    long addhp = addHP(hHP, hHPRegen);
                    hHP = addhp > hMaxHP ? hMaxHP : addhp; // 被击后恢复的HP
                    System.out.println("[LV" + hLevel + " " + hName + "]" + "触发被动，恢复了" + hHPRegen + "的血量，" + "剩余" + (addhp) + "的血量");
                }
            }
            b.setHp(hHP);

            // 玩家英雄死亡 血量为0
            if (hHP <= 0) {
                status = 4;
                System.out.println("----------------------[LV" + mLevel + " " + mName + "]" + "击杀了[LV" + hLevel + " " + hName + " " + hstar + "★]----------------------");
            }
        }
        return status;
    }

    private AttributeEntity getHeroStats(long id){
        AttributeEntity attribute = new AttributeEntity();

        UserHeroEntity userHero = userHeroDao.selectOne(new QueryWrapper<UserHeroEntity>()
                .eq("STATUS", Constant.enable)
                .eq("GM_USER_HERO_ID", id)// 玩家英雄ID
        );
        if(userHero == null){
            System.out.println("英雄已销毁或已售出");
        }

        Map<String,Object> map = new HashMap<>();
        map.put("heroStarId",userHero.getGmHeroStarId());
        map.put("heroLevelId",userHero.getGmHeroLevelId());
        map.put("userHeroId",userHero.getGmUserHeroId());

        // 通过玩家英雄的等级、星级、装备获取该英雄的全部属性
        attribute = combatStatsUtilsService.getHeroBasicStats(map);
        if(attribute == null){
            System.out.println("获取英雄属性失败" + userHero.getGmHeroId());
        }

        return attribute;
    }


}

