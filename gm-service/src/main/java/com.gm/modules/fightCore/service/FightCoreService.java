package com.gm.modules.fightCore.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Arith;
import com.gm.common.utils.CalculateTradeUtil;
import com.gm.common.utils.Constant;
import com.gm.common.utils.LotteryGiftsUtils;
import com.gm.modules.basicconfig.dao.*;
import com.gm.modules.basicconfig.dto.AttributeEntity;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.basicconfig.rsp.TeamInfoRsp;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.dao.GmMiningInfoDao;
import com.gm.modules.user.dao.UserDao;
import com.gm.modules.user.dao.UserHeroDao;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.FightInfoReq;
import com.gm.modules.user.rsp.*;
import com.gm.modules.user.service.UserAccountService;
import com.gm.modules.user.service.UserBalanceDetailService;
import com.gm.modules.user.service.UserDailyOutputIncomeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 战斗核心业务类
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
    private GmMiningInfoDao miningInfoDao;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserBalanceDetailService userBalanceDetailService;
    @Autowired
    private EquipmentInfoDao equipmentInfoDao;
    @Autowired
    private GmCombatRecordDao combatRecordDao;
    @Autowired
    private DrawGiftService drawGiftService;
    @Autowired
    private UserDailyOutputIncomeRecordService userDailyOutputIncomeRecordService;


    /**
     * 经验值
     */
    private static Long EXP = 0L;
    /**
     * 战斗状态
     */
    private static int combatStatus = 0;// 战斗状态
    /**
     * 战斗过程
     */
    private static List<BattleDetailsRsp> battleDetails = new ArrayList<>();
    /**
     * 英雄集合
     */
    private static List<AttributeEntity> heros = null;
    /**
     * 怪物集合
     */
    private static List<GmMonsterConfigEntity> monsters = null;

    /**
     * 存活的英雄数量
     */
    private static int survivingHero = 0;

    /**
     * 存活的怪物数量
     */
    private static int survivingMonster = 0;

    /**
     * 初始化战斗
     * @param user
     * @param req
     */
    public FightInfoRsp initAttack(UserEntity user, FightInfoReq req) {
        System.out.println("初始化战斗场景");
        // 重置全局经验值
        EXP = 0L;
        // 重置战斗状态
        combatStatus = 0;
        // 重置战斗过程
        battleDetails = new ArrayList<>();
        // 重置英雄集合
        heros = null;
        // 重置怪物集合
        monsters = null;
        // 重置存活的英雄数量
        survivingHero = 0;
        // 重置存活的怪物数量
        survivingMonster = 0;

        Date now = new Date();
        long currentTime = System.currentTimeMillis() + 1000 * 60 * 60;
        Date end = new Date(currentTime);

        if (user == null){
            throw new RRException("玩家不存在");
        }

        if (!Constant.enable.equals(user.getStatus())) {
            throw new RRException("玩家状态失效");
        }
        // 获取队伍中的英雄信息及英雄技能英雄装备信息
        // 获取未战斗的队伍
        GmTeamConfigEntity teamConfig = teamConfigDao.selectOne(new QueryWrapper<GmTeamConfigEntity>()
                        .eq("ID", req.getTeamId())// 玩家队伍ID
        );

        if (teamConfig == null){
            throw new RRException("战斗时获取队伍失败");
        }

        if (Constant.BattleState._IN_BATTLE.getValue().equals(teamConfig.getStatus())) {
            throw new RRException("该队伍状态为正在战斗中，请等待战斗结束");
        }

        if (Constant.BattleState._BATTLE_IS_OVER.getValue().equals(teamConfig.getStatus())) {
            throw new RRException("该队伍状态为战斗结束，请先领取奖励后发起战斗");
        }

        Long teamPower = teamConfig.getTeamPower() != null ? teamConfig.getTeamPower() : 0;// 队伍战力
        System.out.println("加载英雄中...");
        // 创建集合存储每个英雄的属性
        List<AttributeEntity> attributes = new ArrayList<>();
        // 获取英雄1
        if(teamConfig.getUserHero1Id() != null && !teamConfig.getUserHero1Id().equals(0L)){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero1Id());
            attributes.add(att);
        }
        // 获取英雄2
        if(teamConfig.getUserHero2Id() != null && !teamConfig.getUserHero2Id().equals(0L)){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero2Id());
            attributes.add(att);
        }
        // 获取英雄3
        if(teamConfig.getUserHero3Id() != null && !teamConfig.getUserHero3Id().equals(0L)){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero3Id());
            attributes.add(att);
        }
        // 获取英雄4
        if(teamConfig.getUserHero4Id() != null && !teamConfig.getUserHero4Id().equals(0L)){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero4Id());
            attributes.add(att);
        }
        // 获取英雄5
        if(teamConfig.getUserHero5Id() != null && !teamConfig.getUserHero5Id().equals(0L)){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero5Id());
            attributes.add(att);
        }
        System.out.println("英雄加载完成");

        System.out.println("加载副本中...");
        // 获取副本信息
        GmDungeonConfigEntity dungeon = dungeonConfigDao.selectById(req.getDungeonId());
        if (dungeon == null){
            throw new RRException("副本已关闭");
        }

        // 玩家体力值校验(小于副本所需体力无法战斗)
        if(user.getFtg() < dungeon.getRequiresStamina()){
            throw new RRException("玩家疲劳值不足");
        }


        // 获取副本事件信息
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("STATUS", Constant.enable);
        eventMap.put("DUNGEON_ID", dungeon.getId());
        List<GmDungeonEventEntity> dungeonEvents = dungeonEventDao.selectByMap(eventMap);
        if (dungeonEvents == null) {
            throw new RRException("获取副本事件信息失败");
        }
        System.out.println("副本加载完成");

        System.out.println("加载怪物中...");
        // 获取怪物信息
        Map<String,Object> monsterMap = new HashMap<>();
        monsterMap.put("STATUS", Constant.enable);
        monsterMap.put("DUNGEON_ID", dungeon.getId());
        List<GmMonsterConfigEntity> monsterList = monsterConfigDao.selectByMap(monsterMap);
        if (monsterList == null) {
            throw new RRException("怪物修炼中");
        } else {
            Collections.shuffle(monsterList);
        }
        System.out.println("怪物加载完成");


        // 扣除玩家体力
        UserEntity userFTG = new UserEntity();
        if (user != null) {
            if (user.getFtg() < dungeon.getRequiresStamina()){
                throw new RRException("玩家疲劳值不足");
            }
            userFTG.setUserId(user.getUserId());
            userFTG.setFtg(user.getFtg() - dungeon.getRequiresStamina());
            userDao.updateById(userFTG);
        } else {
            throw new RRException("玩家信息获取失败");
        }

        // 开始战斗
        // 随机战斗副本，战斗描述
        System.out.println("尊敬的领主指引[" + teamConfig.getTeamName() + "] 战力[" + teamPower + "] 进入[" + dungeon.getDungeonName() + "]");
        setBattleProcess(new BattleDetailsRsp(null, dungeon.getDungeonName(),null, null,  null, null,
                null, null, null,
                "尊敬的领主指引[" + teamConfig.getTeamName() + "] 战力[{teamPower}] 进入[{name}]", Constant.enable, teamPower));

        //获取每个事件的概率
        List<Double> orignalRates = new ArrayList<Double>(dungeonEvents.size());
        for (GmDungeonEventEntity event: dungeonEvents) {
            double probabilityN = event.getEventPron();
            if (probabilityN < 0) {
                probabilityN = 0;
            }
            orignalRates.add(probabilityN);
        }
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, Constant.DrawNum.DRAW1.getValue());
        // 随机的事件
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {

            String eventDescription = dungeonEvents.get(entry.getKey()).getEventDescription();
            long eventLevel = dungeonEvents.get(entry.getKey()).getEventLevel();
            System.out.println("事件等级：" + eventLevel);
            eventDescription = eventDescription.replace("-","["  + dungeon.getDungeonName() + "]");

            setBattleProcess(new BattleDetailsRsp(null, null, null, null, null, null,
                    null, null, null, eventDescription, Constant.enable, null));

            System.out.println(eventDescription);

            // 事件等级5 为福利关卡 无需战斗 玩家直接胜利 // 惩罚关卡 无需战斗直接失败
            if ( eventLevel == 5 ) {
                combatStatus = 2;
            } else if ( eventLevel == 4 ) {
                combatStatus = 4;
            } else {
//                combatStatus = attcks(attributes, monsterList, dungeonEvents.get(entry.getKey()).getEventLevel(), dungeon);
                heros = attributes;
                monsters = monsterList;
                survivingHero = attributes.size();
                attack(dungeon);
            }
        }

        // 获取玩家矿工数量
        getMiners(user.getUserId());

        // 获取剩余可战斗场次
        Long fightNum = user.getFtg() / dungeon.getRequiresStamina();

        // 首先获取玩家每日可产出收益记录表是否24小时内该队伍存在数据 3个队伍当日最多存在一条记录
        List<UserDailyOutputIncomeRecordEntity> uDOIRList = userDailyOutputIncomeRecordService.getDataFrom24Hr(user.getUserId());

        // 说明24小时内不存在战斗， 生成首次战斗数据，该场战斗属于第N次战斗 (无需更新 24小时候重新计算)需要按照综合指数更新当日可产出最大金币数, 当日剩余奖励金币数
        if (uDOIRList.size() < 1) {
            // 开始计算玩家收益

            // 玩家收益参数
            BigDecimal addMoney;
            // 经济平衡系统初始化方法
            initTradeBalanceParameter();

//            // 获取当前副本奖励分配百分比
//            CalculateTradeUtil.FundPool = Arith.multiply(CalculateTradeUtil.FundPool, BigDecimal.valueOf(dungeon.getRewardDistribution()));
//
//            // 副本资金池余额（最新）
//            BigDecimal dungeonPoolBal = CalculateTradeUtil.FundPool;
//            System.out.println("当前副本池子余额: " + dungeonPoolBal);
//            // 更新系统中保存的副本资金池余额
//            dungeonPoolBal = Arith.subtract(dungeonPoolBal, CalculateTradeUtil.totalPlayersGold);
//            sysConfigService.updateValueByKey(Constant.DUNGEON_POOLING_BALANCE, dungeonPoolBal.toString());

            // 出售/计算收益
            System.out.println("marketEggs: " + CalculateTradeUtil.marketEggs);
            System.out.println("miners: " + CalculateTradeUtil.miners);

            CalculateTradeUtil.sellEggs();
            System.out.println("totalPower: "+CalculateTradeUtil.totalPower);

            // 可产出的金币
            addMoney = CalculateTradeUtil.userGetGold;

            // 更新系统中保存的市场总鸡蛋
            sysConfigService.updateValueByKey(Constant.MARKET_EGGS, CalculateTradeUtil.marketEggs.toString());

            // 24小时内无战斗，插入首次记录
            UserDailyOutputIncomeRecordEntity uDOIR = new UserDailyOutputIncomeRecordEntity();
            uDOIR.setFirstTime(now);
            uDOIR.setMaxGoldCoins(addMoney); // 当日可产出最大金币数
            uDOIR.setRemainingGoldCoins(addMoney); // 当日剩余奖励金币数
            uDOIR.setMaxFight(fightNum); // 可战斗场次
            uDOIR.setUserId(user.getUserId());
            uDOIR.setStatus(Constant.enable);
            uDOIR.setCreateTime(now); // 创建时间
            uDOIR.setCreateTimeTs(now.getTime());// 创建时间戳
            userDailyOutputIncomeRecordService.save(uDOIR);
            uDOIRList.add(uDOIR);
        }

        // 该场战斗可得金币
        BigDecimal goldCoins = BigDecimal.valueOf(0);
        // 怪物全部击杀 战斗胜利 获取奖励结算信息
        if ( combatStatus == 2){
            // 获取当前副本奖励分配百分比
            goldCoins = Arith.multiply(uDOIRList.get(0).getRemainingGoldCoins(), BigDecimal.valueOf(dungeon.getRewardDistribution()));
            // 该场战斗可得金币
            goldCoins = Arith.divide(goldCoins, BigDecimal.valueOf(fightNum));
            // 更新当日剩余奖励金币数
            UserDailyOutputIncomeRecordEntity uDOIR = new UserDailyOutputIncomeRecordEntity();
            uDOIR.setId(uDOIRList.get(0).getId());
            uDOIR.setRemainingGoldCoins(Arith.subtract(uDOIRList.get(0).getRemainingGoldCoins(), goldCoins));
            uDOIR.setUpdateTime(now); // 更新时间
            uDOIR.setUpdateTimeTs(now.getTime()); // 更新时间戳
            userDailyOutputIncomeRecordService.updateById(uDOIR);

            // 更新系统中保存的玩家赚取总收入
            CalculateTradeUtil.totalPlayersGold = Arith.add(CalculateTradeUtil.totalPlayersGold, goldCoins);
            sysConfigService.updateValueByKey(Constant.PLAYERS_EARN_TOTAL_REVENUE, CalculateTradeUtil.totalPlayersGold.toString());

        }

        // 插入战斗记录
        GmCombatRecordEntity combatRecord = new GmCombatRecordEntity();
        combatRecord.setUserId(user.getUserId());
        combatRecord.setDungeonId(dungeon.getId());// 副本ID
        combatRecord.setTeamId(teamConfig.getId());// 队伍ID
        String state = Constant.BattleResult._LOSE.getValue();
        // 战斗胜利的情况下触发
        if (combatStatus == 2) {
//            CombatDescription = "YOU WIN";
            state = Constant.BattleResult._WIN.getValue();
            combatRecord.setGetExp(EXP);
            combatRecord.setGetUserExp(EXP);
            combatRecord.setGetEquip(dungeon.getRangeEquip());
            combatRecord.setGetProps(dungeon.getRangeProps());
        }
        combatRecord.setGetGoldCoins(goldCoins);
        String combatDescription = "";
        JSONArray jsonObject = (JSONArray) JSONArray.toJSON(battleDetails);
        combatDescription = jsonObject.toJSONString();
        combatRecord.setCombatDescription(combatDescription);
        combatRecord.setStatus(state);
        combatRecord.setStartTime(now);
        combatRecord.setStartTimeTs(now.getTime());
        combatRecord.setEndTime(end);
        combatRecord.setEndTimeTs(end.getTime());
        combatRecordDao.insert(combatRecord);

        FightInfoRsp rsp = new FightInfoRsp();
        rsp.setTeamId(req.getTeamId());
        rsp.setCombatId(combatRecord.getId());
        rsp.setBattleDetails(battleDetails);
        // 获取倒计时
        Long endSec = (end.getTime() - now.getTime()) / 1000;
        if (endSec > 0) {
            rsp.setEndSec(endSec);
        }
        rsp.setStartTimeTs(now.getTime());
        rsp.setCountdown("00:00:00");

        // 更新队伍战斗状态
        GmTeamConfigEntity upTeam = new GmTeamConfigEntity();
        upTeam.setId(teamConfig.getId());
        upTeam.setStatus(Constant.BattleState._IN_BATTLE.getValue());// 战斗中===================================
        upTeam.setStartTime(now);
        upTeam.setStartTimeTs(now.getTime());
        upTeam.setEndTime(end);
        upTeam.setEndTimeTs(end.getTime());
        upTeam.setCombatId(combatRecord.getId());
        teamConfigDao.updateById(upTeam);

        return rsp;
    }

    /**
     * 初始化经济平衡系统参数
     */
    public void initTradeBalanceParameter(){
        System.out.println("初始化经济平衡系统......");

        // 获取市场总鸡蛋数量
        String totalEggs = sysConfigService.getValue(Constant.MARKET_EGGS);
        System.out.println("获取市场总鸡蛋数量: "+totalEggs);
        CalculateTradeUtil.marketEggs = new BigDecimal(totalEggs);

        // 获取系统全部玩家总战力
        CalculateTradeUtil.totalPower = BigDecimal.valueOf(getTotalPower());
        System.out.println("获取系统全部玩家总战力: " + CalculateTradeUtil.totalPower);

        // 获取资金池70%余额
        String poolBalance = sysConfigService.getValue(Constant.CashPool._MAIN.getValue());
        CalculateTradeUtil.FundPool = new BigDecimal((Double.parseDouble(poolBalance) * 0.7) + "");
        System.out.println("获取资金池70%余额: " + CalculateTradeUtil.FundPool);

        // 玩家赚取总收入
        CalculateTradeUtil.totalPlayersGold = new BigDecimal(sysConfigService.getValue(Constant.PLAYERS_EARN_TOTAL_REVENUE));
        System.out.println("获取系统全部玩家赚取总收入: " + CalculateTradeUtil.totalPlayersGold);

        // 获取扣除用户总收益后的资金池余额
        CalculateTradeUtil.FundPool = Arith.subtract(CalculateTradeUtil.FundPool, CalculateTradeUtil.totalPlayersGold);
        System.out.println("获取扣除用户总收益后的资金池余额: " + CalculateTradeUtil.FundPool);

    }

    /**
     * 获取玩家矿工数量
     * @param userId
     * @return
     */
    public Long getMiners(Long userId){
        // 获取玩家矿工信息
        GmMiningInfoEntity miningInfo = miningInfoDao.selectOne(new QueryWrapper<GmMiningInfoEntity>()
                .eq("STATUS", Constant.enable)
                .eq("USER_ID", userId)// 玩家ID
        );
        if (miningInfo == null) {
            throw new RRException("玩家矿工获取失败");
        }

        // 玩家矿工数量
        CalculateTradeUtil.miners = new BigDecimal(miningInfo.getMiners());
        System.out.println("获取当前玩家矿工数量: " +CalculateTradeUtil.miners);
        return miningInfo.getId();
    }

    /**
     * 获取系统全部用户总战力
     * @return
     */
    private Long getTotalPower(){
        long total = 0;
        Map<String,Object> map = new HashMap<>();
        List<UserEntity> users = userDao.selectByMap(map);
        for (UserEntity userEntity : users){
            total = total + userEntity.getTotalPower();
        }
        return total;
    }

    /**
     * 构建战斗过程
     * @param rsp
     */
    private void setBattleProcess(BattleDetailsRsp rsp) {
        battleDetails.add(rsp);
    }

    /**
     * 攻击方法
     */
    private void attack(GmDungeonConfigEntity dungeon){
        // 加载英雄属性
        int initHero = 0;
        while ( initHero < heros.size() ){
            attributeAdd(heros.get(initHero), initHero);
            // 英雄等级
            long hLevel = heros.get(initHero).getHeroLevel();
            // 英雄星级
            long hStar = heros.get(initHero).getHeroStar();
            // 英雄名称
            String hName = heros.get(initHero).getHeroName();
            // 英雄血量
            long hHp = heros.get(initHero).getHp();
            // 英雄魔法值
            long hMP = heros.get(initHero).getMp();
            System.out.println("[LV" + hLevel + " " + hName + " " + hStar + "★] 剩余HP: " + hHp);
            setBattleProcess(new BattleDetailsRsp(hLevel, hName, null, null, hStar, null, null, hHp, null,
                    "[LV{level} {name} {starCode}★] HP: " + hHp, Constant.enable, null));
            initHero++;
        }


        // 随机怪物出现的数量
        Random rm = new Random();
        int rmon = rm.nextInt(dungeon.getMonsterNum());
        if ( rmon < 3 ) {
            rmon = 3;
        }
        // 怪物剩余数量
        survivingMonster = rmon;
        int initMoster = 0;
        while ( initMoster < rmon ){
            // 怪物名称
            String mname = monsters.get(initMoster).getMonsterName();
            // 怪物等级
            long mlevel = monsters.get(initMoster).getMonsterLevel();
            // 获取当前怪物最大血量
            long mMaxHP = monsters.get(initMoster).getMonsterHealth();
//            System.out.println("[LV" + mlevel + " " + mname + "] 剩余HP: " + mMaxHP);
//            setBattleProcess(new BattleDetailsRsp(null, null, mlevel, mname, null, null, null, mMaxHP, null,
//                    "[LV{mlevel} {mname}] HP: " + mMaxHP, Constant.disabled, null));
            initMoster++;
        }

        int h = 1;// 回合数
        while (true){
            System.out.println("英雄剩余数量：" + survivingHero);
            System.out.println("怪物剩余数量：" + survivingMonster);
            System.out.println("---------------第" + h + "回合---------------");
            System.out.println("探索中...");
            setBattleProcess(new BattleDetailsRsp(null, null, null, null, null, null,
                    null, null, null, "探索中...", Constant.enable, null));
            System.out.println("怪物出现了,接近中...");
            setBattleProcess(new BattleDetailsRsp(null,null, null, null, null, null, null, null, null,
                    "怪物出现了,接近中...", Constant.disabled, null));
            // 玩家攻击怪物
            int hero = 0;
            while ( hero < survivingHero ){
                // 开始攻击
                HAttackM(heros.get(hero), dungeon, hero);
                // 怪物全部死亡，玩家阵营胜利
                if ( survivingMonster < 1) {
                    combatStatus = 2;
                    System.out.println("怪物全部击杀 战斗胜利");
                    setBattleProcess(new BattleDetailsRsp(null, null, null, null, null, null,
                            null, null, null, "怪物全部击杀 战斗胜利", Constant.enable, null));
                    break;
                }
                hero++;
            }
            if ( combatStatus == 2) break;
            // 怪物攻击玩家
            int mos = 0;
            while ( mos < survivingMonster){
                MAttackH(monsters.get(mos), dungeon, mos);
                // 英雄全部死亡，玩家阵营失败
                if ( survivingHero < 1 ) {
                    combatStatus = 4;
                    System.out.println("英雄全部被击杀 战斗失败");
                    setBattleProcess(new BattleDetailsRsp(null, null,null, null, null, null,
                            null, null, null, "英雄全部阵亡 战斗失败", Constant.enable, null));
                    break;
                }
                mos++;
            }
            if ( combatStatus == 4 ) break;
            h++;
        }
    }

    /**
     * 累加属性
     */
    private void attributeAdd(AttributeEntity a, int i) {
        // 装备血量
        long hMAXHPEQ = a.getEquipHealth() != null ? a.getEquipHealth() : 0;
        // 将装备血量累加到英雄身上
        hMAXHPEQ = a.getHp() + hMAXHPEQ;
        heros.get(i).setHp(hMAXHPEQ);
        heros.get(i).setMaxHp(hMAXHPEQ);
        heros.get(i).setAddMaxHp(hMAXHPEQ);

        // 装备魔法值
        long hMAXMPEQ = a.getEquipMana() != null ? a.getEquipMana() : 0;
        // 将装备魔法值累加到英雄身上
        hMAXMPEQ = a.getMp() + hMAXMPEQ;
        heros.get(i).setMp(hMAXMPEQ);
        heros.get(i).setMaxMp(hMAXMPEQ);
        // 装备普攻
        long attackDamagEQ = a.getEquipAttackDamage() != null ? a.getEquipAttackDamage() : 0;
        // 将装备普攻累加到英雄身上
        attackDamagEQ = a.getAttackDamage() + attackDamagEQ;
        heros.get(i).setAttackDamage(attackDamagEQ);
        // 装备法功
        long attackSpellEQ = a.getEquipAttackSpell() != null ? a.getEquipAttackSpell() : 0;
        // 将装备法功累加到英雄上
        attackSpellEQ = a.getAttackSpell() + attackSpellEQ;
        heros.get(i).setAttackSpell(attackSpellEQ);
        // 装备护甲
        long armorEQ = a.getEquipArmor() != null ? a.getEquipArmor() : 0;
        // 将装备护甲累加到英雄上
        armorEQ = a.getArmor() + armorEQ;
        heros.get(i).setArmor(armorEQ);
        // 装备魔抗
        long magicResistEQ = a.getEquipMagicResist() != null ? a.getEquipMagicResist() : 0;
        // 将装备魔抗累加到英雄上
        magicResistEQ = a.getMagicResist() + magicResistEQ;
        heros.get(i).setMagicResist(magicResistEQ);
    }

    /**
     * 玩家英雄攻击怪物
     * @param a
     * @param dungeon
     * @param index
     */
    private void HAttackM(AttributeEntity a, GmDungeonConfigEntity dungeon, int index){
        //======================玩家英雄信息====================
        // 英雄等级
        long hLevel = a.getHeroLevel();
        // 英雄星级
        long hStar = a.getHeroStar();
        // 英雄名称
        String hName = a.getHeroName();
        // 英雄血量
        long hHp = a.getHp();
        // 英雄魔法值
        long hMP = a.getMp();
        // 英雄最大魔法值
        long hMaxMP = a.getMaxMp();

        Double skillDamage = 0.0;
        // 英雄普攻
        long attackDamag = a.getAttackDamage();
        // 英雄法功
        long attackSpell = a.getAttackSpell() != null ? a.getAttackSpell() : 0;
        // 英雄技能恢复血量
        long hSkillHp = 0L;
        if (a.getSkillDamageBonusHero() != null) {
            // 获取物攻英雄技能伤害
            skillDamage = a.getSkillFixedDamage() + (attackDamag * (a.getSkillDamageBonusHero() / 10));
        } else if (a.getSkillDamageBonusEquip() != null) {
            // 获取法攻英雄技能伤害
            skillDamage = a.getSkillFixedDamage() + (attackSpell * (a.getSkillDamageBonusEquip() / 10));
            hSkillHp = (long) (a.getSkillFixedDamage() + (attackSpell * (a.getSkillDamageBonusEquip() / 10)));
        }

        //======================怪物信息======================
        // 随机某个位置的怪物
//        Random rm = new Random();
//        int attM = rm.nextInt(survivingMonster);
//        if ( attM < 1 ) {
//            attM = 0;
//        }
        int attM = survivingMonster - 1;
        // 怪物名称
        String mname = monsters.get(attM).getMonsterName();
        // 怪物等级
        long mlevel = monsters.get(attM).getMonsterLevel();
        // 获取当前怪物最大血量
        long mMaxHP = monsters.get(attM).getMonsterHealth();
        // 获取怪物最新血量
        long mHP = monsters.get(attM).getMonsterHealth();
        // 获取怪物恢复血量
        long mHPRegen = monsters.get(attM).getMonsterHealthRegen();
        // 获取怪物护甲
        long mArmor = monsters.get(attM).getMonsterArmor();
        // 获取怪物魔抗
        long mMagicResist = monsters.get(attM).getMonsterMagicResist();


        // 普攻伤害
        long at1 = (attackDamag - (mArmor + mMagicResist));
        // 技能伤害
        long at3 = (long) (skillDamage - (mArmor + mMagicResist));
        // 存放普功+技能伤害
        long[] skillHarm = {at1, at1, at1, at1, at1, at3};
        //随机普功、技能
        Random rk = new Random();
        int ras = rk.nextInt(skillHarm.length);
        String skillName = "[普通攻击]";
        String skillDescription = a.getSkillDescription();
        // 怪物血量大于0执行
        if (mHP >= 0) {
            // 随机的技能位置不能为负数
            if (skillHarm[ras] < 1) {
                skillHarm[ras] = 0;
            }

            // 攻击内容
            String attContent = "[LV{level} {name} {starCode}★] 释放 ";
            String attContentLog = "[LV" + hLevel + " " + hName + " " + hStar + "★] 释放 ";

            // 低于当前释放技能所需MP 直接改为释放普攻
            double MPPerTime = hMaxMP * 0.2;
            if (hMP < MPPerTime) {
                System.out.println("MP不足，释放普攻");
                ras = 0;
            }
            // ras=5 攻击方式为技能
            if (ras == 5) {
                // 消耗MP
                hMP = (long) (hMP - MPPerTime);
                heros.get(index).setMp(hMP);

                // 技能名称
                skillName = "[" + a.getSkillName() + "]";

                attContent += "{skillName}";
                attContentLog += skillName;

                // 输出英雄
                if ( Constant.SkillType._ATTACK.getValue().equals(a.getSkillType()) ) {
                    mHP = mHP - skillHarm[ras];
                    mHP = mHP < 1 ? 0 : mHP;
                    attContent += " 对[LV{mlevel} {mname}]造成{dealDamage}的伤害，[LV{mlevel} {mname}]剩余{HP}HP";
                    attContentLog += " 对[LV" + mlevel + " " + mname + "]造成" + skillHarm[ras] + "的伤害，[LV" + mlevel + " " + mname + "]剩余" + (mHP < 1 ? 0 : mHP) + "HP";
                } else if ( Constant.SkillType._SUP.getValue().equals(a.getSkillType()) ||
                        Constant.SkillType._SUP_ADD.getValue().equals(a.getSkillType()) ) {// 辅助类英雄
                    // 对单体英雄或全体英雄恢复血量或血量加成
                    int huifu = 0;
                    while ( huifu < heros.size() ) {
                        // 全体玩家恢复生命值技能
                        if ( Constant.SkillType._SUP.getValue().equals(a.getSkillType()) ) {

                            long hp = heros.get(huifu).getHp();// 获取英雄的生命值
                            long addhp = addHP(hp, hSkillHp);
                            addhp = addhp > hp ? hp : addhp;// 恢复的HP
                            heros.get(huifu).setHp(addhp);
                            attContent += " 全体英雄恢复HP:" + addhp;
                            attContentLog += " 全体英雄恢复HP:" + addhp;

                        } else if ( Constant.SkillType._SUP_ADD.getValue().equals(a.getSkillType()) ) {// 玩家属性加成技能
                            // 随机给某个英雄释放辅助技能
                            Random rdSUP = new Random();
                            int supNum = rdSUP.nextInt(heros.size());
                            supNum = supNum < 1 ? 0 : supNum;
                            // 英雄等级
                            long hLevelSUP = heros.get(supNum).getHeroLevel();
                            // 英雄星级
                            long hStarSUP = heros.get(supNum).getHeroStar();
                            // 英雄名称
                            String hNameSUP = heros.get(supNum).getHeroName();
                            if ( heros.get(supNum).getMaxHp().equals(heros.get(supNum).getAddMaxHp()) ) {
                                long hp = heros.get(supNum).getHp();// 获取英雄的生命值
                                long addhp = addHP(hp, hSkillHp);
                                heros.get(supNum).setHp(addhp);
                                heros.get(supNum).setAddMaxHp(addhp);
                            }
                            attContent += "对[LV" + hLevelSUP + " " + hNameSUP + " " + hStarSUP + "★] 额外增加HP" + hSkillHp;
                            attContentLog += "对[LV" + hLevelSUP + " " + hNameSUP + " " + hStarSUP + "★] 额外增加HP" + hSkillHp;
                            huifu = heros.size();
                        }
                        huifu++;
                    }
                }
            } else {
                attContent += "{skillName}";
                attContentLog += skillName;
                mHP = mHP - skillHarm[ras];
                mHP = mHP < 1 ? 0 : mHP;
                attContent += " 对[LV{mlevel} {mname}]造成{dealDamage}的伤害，[LV{mlevel} {mname}]剩余{HP}HP";
                attContentLog += " 对[LV" + mlevel + " " + mname + "]造成" + skillHarm[ras] + "的伤害，[LV" + mlevel + " " + mname + "]剩余" + (mHP < 1 ? 0 : mHP) + "HP";
            }

            monsters.get(attM).setMonsterHealth(mHP);

            System.out.println(attContentLog);
            setBattleProcess(new BattleDetailsRsp(hLevel, hName, mlevel, mname, hStar, skillName,
                    skillHarm[ras], mHP, null,
                    attContent, Constant.enable, null));

            // 怪物随机触发被动恢复血量
            Random rbhp = new Random();
            int backHp = rbhp.nextInt(99);
            if (mHP > 0) {
                if (backHp % 3 == 0) {
                    long addhp = addHP(mHP, mHPRegen);
                    mHP = addhp > mMaxHP ? mMaxHP : addhp; // 恢复后的HP

                    System.out.println("[LV" + mlevel + " " + mname + "]" + "触发被动，恢复了" + mHPRegen + "HP，" + "剩余" + (mHP) + "HP");
                    setBattleProcess(new BattleDetailsRsp(null, null, mlevel, mname, null, null,null, mHP, mHPRegen,
                            "[LV{mlevel} {mname}]触发被动，恢复了{HPRegen}HP，" + "剩余{HP}HP", Constant.disabled, null));
                }
            }

            // 怪物死亡 血量为0
            if (mHP <= 0) {
                survivingMonster--;
                EXP = EXP + monsters.get(attM).getMonsterExp();
                System.out.println("[LV" + hLevel + hName + " " + hStar + "★]" + "击杀了[LV" + mlevel + " " + mname + "]");
                setBattleProcess(new BattleDetailsRsp(hLevel, hName, mlevel, mname, hStar, null, null, null, null,
                        "[LV{level} {name} {starCode}★]击杀了[LV{mlevel} {mname}]", Constant.disabled, null));

                System.out.println("怪物剩余量: " + survivingMonster);
            }
        }
    }

    /**
     * 怪物攻击玩家英雄
     * @param m
     * @param dungeon
     * @param index
     */
    private void MAttackH(GmMonsterConfigEntity m, GmDungeonConfigEntity dungeon, int index){
        // 随机某个位置的英雄
//        Random rm = new Random();
//        int attH = rm.nextInt(survivingHero);
//        if ( attH < 1 ) {
//            attH = 0;
//        }
        int attH = survivingHero - 1;
        //======================玩家英雄信息====================
        // 英雄名称
        String hName = heros.get(attH).getHeroName();
        // 英雄等级
        long hLevel = heros.get(attH).getHeroLevel();
        // 当前英雄最大血量
        long hMaxHP = heros.get(attH).getHp();
        // 英雄最新血量
        long hHP = heros.get(attH).getHp();
        // 英雄恢复血量
        long hHPRegen = heros.get(attH).getHpRegen();
        // 英雄护甲
        long hArmor = heros.get(attH).getArmor();
        // 英雄魔抗
        long hMagicResist = heros.get(attH).getMagicResist();
        // 英雄星级
        long hStar = heros.get(attH).getHeroStar();

        //======================怪物信息======================
        // 怪物名称
        String mname = m.getMonsterName();
        // 怪物等级
        long mlevel = m.getMonsterLevel();
        // 怪物普通攻击
        long mAttackDamag = m.getMonsterAttackDamage();
        // 怪物的专属技能伤害
        long uniqueSkill = mAttackDamag * m.getUniqueSkillM() - hArmor;
        // 怪物的致命一击伤害
        long criticalHit = mAttackDamag * m.getCriticalHitM() - hArmor;

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
            hHP = (hHP < 1 ? 0 : hHP);
            String skillName = "[普通攻击]";
            if (ras == 2 || ras == 3) {
                if ( dungeon.getDungeonAward().equals(2L) ) {
                    skillName = Constant.SkillNameM.LV2.getValue();
                } else if ( dungeon.getDungeonAward().equals(3L) ) {
                    skillName = Constant.SkillNameM.LV3.getValue();
                } else if ( dungeon.getDungeonAward().equals(4L) ) {
                    skillName = Constant.SkillNameM.LV4.getValue();
                } else if ( dungeon.getDungeonAward().equals(5L) ) {
                    skillName = Constant.SkillNameM.LV5.getValue();
                } else {
                    skillName = Constant.SkillNameM.LV1.getValue();
                }
            } else if (ras == 4) {
                skillName += "触发了[致命一击]";
            }

            System.out.println("[LV" + mlevel + " " + mname + "]" + "释放" + skillName + "对[LV" + hLevel + " " + hName + " " + hStar + "★]造成" + skillHarm[ras] + "的伤害，[" + hName + "]剩余" + (hHP < 1 ? 0 : hHP) + "HP");
            setBattleProcess(new BattleDetailsRsp(hLevel, hName, mlevel, mname, hStar, skillName, skillHarm[ras], hHP, null,
                    "[LV{mlevel} {mname}]使用{skillName}对[LV{level} {name} {starCode}★]造成{dealDamage}的伤害，[{name}]剩余{HP}HP", Constant.disabled, null));

            heros.get(attH).setHp(hHP);

            // 玩家英雄死亡 血量为0
            if (hHP <= 0) {
                survivingHero--;
                System.out.println("[LV" + mlevel + " " + mname + "]" + "击杀了[LV" + hLevel + " " + hName + " " + hStar + "★]");
                setBattleProcess(new BattleDetailsRsp(hLevel, hName, null, null, hStar, null, null, null, null,
                        "[LV{mlevel} {mname}]击杀了[LV{level} {name} {starCode}★]", Constant.enable, null));
            }
        }
    }

    /**
     * HP恢复
     * @param HP
     * @param HpRegen
     * @return
     */
    private long addHP(long HP, long HpRegen){
        return HP + HpRegen;
    }

    /**
     * 通过玩家英雄的等级、星级、装备获取该英雄的全部属性
     * @param id
     * @return
     */
    private AttributeEntity getHeroStats(long id){
        AttributeEntity attribute = new AttributeEntity();

        UserHeroEntity userHero = userHeroDao.selectOne(new QueryWrapper<UserHeroEntity>()
                .eq("STATUS", Constant.enable)
                .eq("GM_USER_HERO_ID", id)// 玩家英雄ID
        );
        if(userHero == null){
            throw new RRException("英雄已销毁或已售出");
        }

        Map<String,Object> map = new HashMap<>();
        map.put("heroStarId",userHero.getGmHeroStarId());
        map.put("heroLevelId",userHero.getGmHeroLevelId());
        map.put("userHeroId",userHero.getGmUserHeroId());

        // 通过玩家英雄的等级、星级、装备获取该英雄的全部属性
        attribute = combatStatsUtilsService.getHeroBasicStats(map);
        if(attribute == null){
            throw new RRException("获取英雄属性失败" + userHero.getGmHeroId());
        }

        return attribute;
    }

    private UserHeroInfoRsp getUserHeroInfo(long id){
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("gmUserHeroId", id);
        UserHeroInfoRsp userHero = userHeroDao.getUserHeroByIdRsp(userHeroMap);
        if ( userHero == null ) {
            System.out.println("英雄获取失败getUserHeroInfo");
        }
        return userHero;
    }

    public List<UserHeroInfoRsp> getTeamHeroInfoList(Long teamId, TeamInfoRsp rsp){
        if (teamId != null) {
            Map<String, Object> teamParams = new HashMap<>();
            teamParams.put("id", teamId);
            rsp = teamConfigDao.getTeamInfo(teamParams);
        }
        if (rsp == null){
            throw new RRException("获取队伍信息异常");
        }

        // 存储英雄集合
        List<UserHeroInfoRsp> userHeroInfoRsps = new ArrayList<>();
        // 获取英雄1
        if(rsp.getUserHero1Id() != null && rsp.getUserHero1Id() != 0){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero1Id()));
        }
        // 获取英雄2
        if(rsp.getUserHero2Id() != null && rsp.getUserHero2Id() != 0){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero2Id()));
        }
        // 获取英雄3
        if(rsp.getUserHero3Id() != null && rsp.getUserHero3Id() != 0){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero3Id()));
        }
        // 获取英雄4
        if(rsp.getUserHero4Id() != null && rsp.getUserHero4Id() != 0){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero4Id()));
        }
        // 获取英雄5
        if(rsp.getUserHero5Id() != null && rsp.getUserHero5Id() != 0){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero5Id()));
        }

        return userHeroInfoRsps;
    }


    /**
     * 玩家点击领取奖励后系统发放奖品到玩家账户
     * @param user
     * @param id
     * @return
     */
    public FightClaimRsp claim(UserEntity user, long id) {
        FightClaimRsp rsp = new FightClaimRsp();
        GmCombatRecordEntity combatRecord = combatRecordDao.selectOne(new QueryWrapper<GmCombatRecordEntity>()
                .eq("ID",id)
        );
        if (combatRecord == null){
            throw new RRException("战斗记录失效");
        }

        // 校验战斗时间是否结束
        Date now = new Date();
        if (combatRecord.getEndTimeTs() > now.getTime()) {
            throw new RRException("该场战斗还未完成，请等待...");
        }

        // 校验玩家是否已领取奖励
        Map<String, Object> balanceMap = new HashMap<>();
        balanceMap.put("sourceId", combatRecord.getId());
        List<UserBalanceDetailEntity> balanceDetails = userBalanceDetailService.getUserBalanceDetail(balanceMap);
        if ( balanceDetails.size() > 0 ){
            throw new RRException("已领取奖励，请刷新页面");
        }

        rsp.setStatus(combatRecord.getStatus());

        // 更新队伍状态为未战斗
        GmTeamConfigEntity team = new GmTeamConfigEntity();
        team.setId(combatRecord.getTeamId());
        team.setStatus(Constant.BattleState._IDLE.getValue());
        teamConfigDao.updateById(team);

        // 战斗胜利为玩家发放奖励
        if (Constant.BattleResult._WIN.getValue().equals(combatRecord.getStatus()) && balanceDetails.size() == 0) {
            // 更新玩家账户余额
            boolean effect = userAccountService.updateAccountAdd(user.getUserId(), combatRecord.getGetGoldCoins());
            if (!effect) {
                throw new RRException("账户金额更新失败!");// 账户金额更新失败
            }
            rsp.setGetGoldCoins(combatRecord.getGetGoldCoins());// 获得的金币
            rsp.setGetExp(combatRecord.getGetExp());
            rsp.setGetUserExp(combatRecord.getGetUserExp());

            // 插入账变明细
            UserBalanceDetailEntity balanceDetail = new UserBalanceDetailEntity();
            balanceDetail.setUserId(user.getUserId());
            balanceDetail.setAmount(combatRecord.getGetGoldCoins().doubleValue());
            balanceDetail.setTradeType(Constant.TradeType.DUNGEON_REVENUE.getValue());
            balanceDetail.setTradeDesc("战斗收益");
            balanceDetail.setSourceId(id);// 战斗记录ID
            userBalanceDetailService.insertBalanceDetail(balanceDetail);

            // 插入英雄集合
            rsp.setUserHeroInfoRsps(getTeamHeroInfoList(combatRecord.getTeamId(), null));

            // 通过副本获取爆率等级
            List<Object> gifts = new ArrayList<>();
            GmDungeonConfigEntity dungeonConfig = dungeonConfigDao.selectById(combatRecord.getDungeonId());
            if (dungeonConfig != null) {
                try {
                    // 奖励玩家装备 通过副本爆率控制概率
                    Random rd = new Random();
                    int rdEQ = rd.nextInt(99);
                    if (rdEQ % dungeonConfig.getBurstRate() == 0) {
                        gifts = drawGiftService.dungeonRewardsEQ(user, dungeonConfig, combatRecord);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 存储装备集合
            List<UserEquipInfoRsp> userEquipInfoRsps = new ArrayList<>();
            // 产出装备
            if (gifts.size() > 0) {
                int i = 0;
                while ( i < gifts.size()){
                    UserEquipInfoRsp userEquipInfoRsp = json2bean(gifts.get(i).toString(), UserEquipInfoRsp.class);
                    userEquipInfoRsps.add(userEquipInfoRsp);
                    i++;
                }
                rsp.setUserEquipInfoRsps(userEquipInfoRsps);
            }
        }
        return rsp;

    }

    /**
     * 更新玩家战力，队伍战力，矿工
     * @param changePower
     * @param user
     * @param team
     */
    public void updateCombat(long changePower, long oldPower, long newPower, UserEntity user, GmTeamConfigEntity team){
        Date now = new Date();
        // 如果更新战力为0 则无需更新矿工 （针对不同顺序）
        if (changePower != 0) {

            // 获取玩家矿工数量
            Long minId = getMiners(user.getUserId());

            // 初始化经济平衡方法
            initTradeBalanceParameter();

            // 通过经济平衡系统更新玩家矿工
            CalculateTradeUtil.updateMiner(BigDecimal.valueOf(changePower));

            // 更新系统中保存的市场总鸡蛋
            sysConfigService.updateValueByKey(Constant.MARKET_EGGS, CalculateTradeUtil.marketEggs.toString());

            // 更新玩家矿工数据
            GmMiningInfoEntity mini = new GmMiningInfoEntity();
            mini.setId(minId);
            mini.setMiners(CalculateTradeUtil.miners.toString());
            mini.setUpdateTime(now);
            mini.setUpdateTimeTs(now.getTime());
            miningInfoDao.updateById(mini);

            // 更新玩家战力
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(user.getUserId());
            Long totalPower = (user.getTotalPower() - oldPower) + newPower;// 获取最新用户战力
            userEntity.setTotalPower(totalPower);
            userEntity.setUpdateTime(now);
            userEntity.setUpdateTimeTs(now.getTime());
            userDao.updateById(userEntity);
        }

        // 队伍战力
        team.setTeamPower(newPower);
        // 更新队伍
        team.setId(team.getId());
        team.setUpdateUser(user.getUserId());
        team.setUpdateTime(now);
        team.setUpdateTimeTs(now.getTime());
        teamConfigDao.setTeamHero(team);
    }


    /**
     * 将json字符串转为Javabean
     *
     * @param json  json
     * @param clazz 目标对象的Class类型
     * @param <T>   泛型对象
     * @return 目标对象
     */
    private static <T> T json2bean(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }

    public static void main(String[] args) {
        long currentTime = System.currentTimeMillis() + Constant.HOUR24;
        Date date = new Date(currentTime);
        System.out.println(date);
//        // 奖励玩家装备 随机概率
//        int i =0;
//        int num = 0;
//        while (i < 1){
//            Random rbhp = new Random();
//            int backHp = rbhp.nextInt(99);
//            if (backHp % 6 == 0) {
//                num ++;
//                System.out.println("您成功爆装备了");
//            }
//            i++;
//        }
//        System.out.println(num);
//        int j = 0 ;
//        int num = 0;
//        while (j < 1000){
//            Double max = 1.01;
//            Double min = 0.5;
//            Double s = Arith.randomWithinRangeHundred(max, min);
//            if (s <= 1.0) {
//                num++;
//            }
////            long ss = 20;
////            ss = (long) (ss * s);
//            System.out.println(s);
////            System.out.println(ss);
//            j++;
//        }
//        System.out.println(num);
//        int rannum= (int)(Math.random()*(9999999-1000000 + 1))+ 1000000;
//        System.out.println(Arith.UUID20());

        int num = 0;
        int i =0;
        while (i<100){
            Random rd = new Random();
            int rdEQ = rd.nextInt(99);
            if (rdEQ % 16 == 0) {
                num++;
                System.out.println("爆装备了");
            }
            i++;
        }


        System.out.println("爆装数量："+ num);

    }
}

