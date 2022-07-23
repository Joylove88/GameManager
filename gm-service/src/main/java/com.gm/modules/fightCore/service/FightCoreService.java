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
import com.gm.modules.user.dao.*;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.FightInfoReq;
import com.gm.modules.user.rsp.*;
import com.gm.modules.user.service.UserAccountService;
import com.gm.modules.user.service.UserBalanceDetailService;
import com.gm.modules.user.service.UserDailyOutputIncomeRecordService;
import org.apache.tomcat.jni.BIOCallback;
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
    private static List<BattleDetailsRsp> battleDetails = new ArrayList();

    /**
     * 开始战斗
     * @param user
     * @param req
     */
    public FightInfoRsp attck(UserEntity user, FightInfoReq req) {
        // 重置全局经验值
        EXP = 0L;
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

        if (Constant.BattleState.BATTLE1.getValue().equals(teamConfig.getStatus())) {
            throw new RRException("该队伍状态为正在战斗中，请等待战斗结束");
        }

        if (Constant.BattleState.BATTLE2.getValue().equals(teamConfig.getStatus())) {
            throw new RRException("该队伍状态为战斗结束，请先领取奖励后发起战斗");
        }

        Long teamPower = teamConfig.getTeamPower() != null ? teamConfig.getTeamPower() : 0;// 队伍战力

        // 创建集合存储每个英雄的属性
        List<AttributeEntity> attributes = new ArrayList<>();
        // 获取英雄1
        if(teamConfig.getUserHero1Id() != null){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero1Id());
            attributes.add(att);
        }
        // 获取英雄2
        if(teamConfig.getUserHero2Id() != null){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero2Id());
            attributes.add(att);
        }
        // 获取英雄3
        if(teamConfig.getUserHero3Id() != null){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero3Id());
            attributes.add(att);
        }
        // 获取英雄4
        if(teamConfig.getUserHero4Id() != null){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero4Id());
            attributes.add(att);
        }
        // 获取英雄5
        if(teamConfig.getUserHero5Id() != null){
            AttributeEntity att = getHeroStats(teamConfig.getUserHero5Id());
            attributes.add(att);
        }

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

        // 获取怪物信息
        Map<String,Object> monsterMap = new HashMap<>();
        monsterMap.put("STATUS", Constant.enable);
        monsterMap.put("DUNGEON_ID", dungeon.getId());
        List<GmMonsterConfigEntity> monsters = monsterConfigDao.selectByMap(monsterMap);
        if (monsters == null) {
            throw new RRException("怪物修炼中");
        } else {
            Collections.shuffle(monsters);
        }


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

        // 战斗状态
        long combatStatus = 0;

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
            if ( eventLevel == 5L ) {
                combatStatus = 2;
            } else {
                combatStatus = attcks(attributes, monsters, dungeonEvents.get(entry.getKey()).getEventLevel(), dungeon);
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

            // 获取当前副本奖励分配百分比
            CalculateTradeUtil.FundPool = Arith.multiply(CalculateTradeUtil.FundPool, BigDecimal.valueOf(dungeon.getRewardDistribution()));

            // 副本资金池余额（最新）
            BigDecimal dungeonPoolBal = CalculateTradeUtil.FundPool;
            System.out.println("当前副本池子余额: " + dungeonPoolBal);
            // 更新系统中保存的副本资金池余额
            dungeonPoolBal = Arith.subtract(dungeonPoolBal, CalculateTradeUtil.totalPlayersGold);
            sysConfigService.updateValueByKey(Constant.DUNGEON_POOLING_BALANCE, dungeonPoolBal.toString());

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
            // 该场战斗可得金币
            goldCoins = Arith.divide(uDOIRList.get(0).getRemainingGoldCoins(), BigDecimal.valueOf(fightNum));
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

            System.out.println("怪物全部击杀 战斗胜利");
            setBattleProcess(new BattleDetailsRsp(null, null, null, null, null, null,
                    null, null, null, "怪物全部击杀 战斗胜利", Constant.enable, null));
        }

        // 插入战斗记录
        GmCombatRecordEntity combatRecord = new GmCombatRecordEntity();
        combatRecord.setUserId(user.getUserId());
        combatRecord.setDungeonId(dungeon.getId());// 副本ID
        combatRecord.setTeamId(teamConfig.getId());// 队伍ID
        String state = Constant.disabled;
        // 战斗胜利的情况下触发
        if (combatStatus == 2) {
//            CombatDescription = "YOU WIN";
            state = Constant.enable;
            combatRecord.setGetExp(EXP);
            combatRecord.setGetUserExp(EXP);
            combatRecord.setGetGoldCoins(goldCoins);
            combatRecord.setGetEquip(dungeon.getRangeEquip());
            combatRecord.setGetProps(dungeon.getRangeProps());
        }
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
        rsp.setCombatId(combatRecord.getId());
        rsp.setBattleDetails(battleDetails);

        // 更新队伍战斗状态
        GmTeamConfigEntity upTeam = new GmTeamConfigEntity();
        upTeam.setId(teamConfig.getId());
//        upTeam.setStatus(Constant.enable);// 战斗中
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
        String poolBalance = sysConfigService.getValue(Constant.CASH_POOLING_BALANCE);
        CalculateTradeUtil.FundPool = new BigDecimal((Double.parseDouble(poolBalance) * 0.7) + "");
        System.out.println("获取资金池70%余额: " + CalculateTradeUtil.FundPool);

        // 玩家赚取总收入
        CalculateTradeUtil.totalPlayersGold = new BigDecimal(sysConfigService.getValue(Constant.PLAYERS_EARN_TOTAL_REVENUE));
        System.out.println("获取系统全部玩家赚取总收入: " + CalculateTradeUtil.totalPlayersGold);

        // 获取扣除用户总收益后的资金池余额
        CalculateTradeUtil.FundPool = Arith.subtract(CalculateTradeUtil.FundPool, CalculateTradeUtil.totalPlayersGold);
        System.out.println("获取扣除用户总收益后的资金池余额: " + CalculateTradeUtil.FundPool);

    }

    // 获取玩家矿工数量
    public Long getMiners(Long userId){
        // 获取玩家矿工信息
        GmMiningInfoEntity miningInfo = gmMiningInfoDao.selectOne(new QueryWrapper<GmMiningInfoEntity>()
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

    // 构建战斗过程
    private void setBattleProcess(BattleDetailsRsp rsp) {
        battleDetails.add(rsp);
    }

    // =========================差个装备属性累加
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
                System.out.println("突然" + "[LV" + mLevel + " " + mName + "] 向你发起了进攻" + " 开始战斗:");
                setBattleProcess(new BattleDetailsRsp(null,null, mLevel, mName, null, null, null, null, null,
                        "突然[LV{mLevel} {mName}] 向你发起了进攻 开始战斗:", Constant.disabled, null));

                System.out.println("[LV" + mLevel + " " + mName + "] 血量为" + mMaxHP);
                setBattleProcess(new BattleDetailsRsp(null, null, mLevel, mName, null, null, null, mMaxHP, null,
                        "[LV{mLevel} {mName}] 血量为" + mMaxHP, Constant.disabled, null));

                System.out.println("[LV" + hLevel + " " + hName + " " + hStar + "★] 血量为" + hMAXHP);
                setBattleProcess(new BattleDetailsRsp(hLevel, hName, null, null, hStar, null, null, hMAXHP, null,
                        "[LV{level} {name} {starCode}★] 血量为" + hMAXHP, Constant.enable, null));
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
                        mHP = mHP < 1 ? 0 : mHP;
                        String skillName = "【普通攻击】";
                        if (ras == 5) {
                            skillName = "【" + a.get(i).getSkillName() + "】";
                        }
                        // 回合数统计
                        totalFightNum ++;

                        System.out.println("[LV" + hLevel + " " + hName + " " + hStar + "★]" + "使用" + skillName + "对[LV" + mLevel + " " + mName + "]造成" + skillHarm[ras] + "的伤害，[LV" + mLevel + " " + mName + "]剩余" + (mHP < 1 ? 0 : mHP) + "的血量");
                        setBattleProcess(new BattleDetailsRsp(hLevel, hName, mLevel, mName, hStar, skillName,
                                skillHarm[ras], mHP, null,
                                "[LV{level} {name} {starCode}★]使用{skillName}对[LV{mLevel} {mName}]造成{dealDamage}的伤害，[LV{mLevel} {mName}]剩余{HP}的血量", Constant.enable, null));

                        // 怪物随机触发被动恢复血量
                        Random rbhp = new Random();
                        int backHp = rbhp.nextInt(99);
                        if (mHP > 0) {
                            if (backHp % 3 == 0) {
                                long addhp = addHP(mHP, mHPRegen);
                                mHP = addhp > mMaxHP ? mMaxHP : addhp; // 恢复的HP

                                System.out.println("[LV" + mLevel + " " + mName + "]" + "触发被动，恢复了" + mHPRegen + "的血量，" + "剩余" + (addhp) + "的血量");
                                setBattleProcess(new BattleDetailsRsp(null, null, mLevel, mName, null, null,null, addhp, mHP,
                                        "[LV{mLevel} {mName}]触发被动，恢复了{HPRegen}的血量，" + "剩余{HP}的血量", Constant.disabled, null));
                            }
                        }

                        // 怪物死亡 血量为0
                        if (mHP <= 0) {
                            monsterNum--;
                            EXP = EXP + monsters.get(m).getMonsterExp();
                            System.out.println("[LV" + hLevel + hName + " " + hStar + "★]" + "击杀了[LV" + mLevel + " " + mName + "]");
                            setBattleProcess(new BattleDetailsRsp(null, null, mLevel, mName, null, null, null, null, null,
                                    "[LV{level} {name} {starCode}★]击杀了[LV{mLevel} {mName}]", Constant.disabled, null));

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
            System.out.println("英雄全部被击杀 战斗失败");
            setBattleProcess(new BattleDetailsRsp(null, null,null, null, null, null,
                    null, null, null, "英雄全部被击杀 战斗失败", Constant.enable, null));
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
        long hStar = b.getHeroStar();

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
            hHP = (hHP < 1 ? 0 : hHP);
            String skillName = "【普通攻击】";
            if (ras == 2 || ras == 3) {
                skillName = "【专属技能】";
            } else if (ras == 4) {
                skillName += "触发了【致命一击】";
            }

            System.out.println("[LV" + mLevel + " " + mName + "]" + "使用" + skillName + "对[LV" + hLevel + " " + hName + " " + hStar + "★]造成" + skillHarm[ras] + "的伤害，[" + hName + "]剩余" + (hHP < 1 ? 0 : hHP) + "的血量");
            setBattleProcess(new BattleDetailsRsp(null, null, mLevel, mName, null, skillName, skillHarm[ras], null, null,
                    "[LV{mLevel} {mName}]使用{skillName}对[LV{level} {name} {starCode}★]造成{dealDamage}的伤害，[{name}]剩余{HP}的血量", Constant.disabled, null));

            // 随机恢复血量
            Random rbhp = new Random();
            int backHp = rbhp.nextInt(99);
            if (hHP > 0) {
                if (backHp % 4 == 0) {
                    long addhp = addHP(hHP, hHPRegen);
                    hHP = addhp > hMaxHP ? hMaxHP : addhp; // 被击后恢复的HP
                    System.out.println("[LV" + hLevel + " " + hName + " " + hStar + "★]" + "触发被动，恢复了" + hHPRegen + "的血量，" + "剩余" + (addhp) + "的血量");
                    setBattleProcess(new BattleDetailsRsp(hLevel, hName, null, null, hStar, null, null, addhp, hHPRegen,
                            "[LV{level} {name} {starCode}★]触发被动，恢复了{HPRegen}的血量，剩余{HP}的血量", Constant.enable, null));

                }
            }
            b.setHp(hHP);

            // 玩家英雄死亡 血量为0
            if (hHP <= 0) {
                status = 4;
                System.out.println("[LV" + mLevel + " " + mName + "]" + "击杀了[LV" + hLevel + " " + hName + " " + hStar + "★]");
                setBattleProcess(new BattleDetailsRsp(hLevel, hName, null, null, hStar, null, null, null, null,
                        "[LV{mLevel} {mName}]击杀了[LV{level} {name} {starCode}★]", Constant.enable, null));
            }
        }
        return status;
    }

    public AttributeEntity getHeroStats(long id){
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
        UserHeroEntity userHero = new UserHeroEntity();
        userHero.setGmUserHeroId(id);
        return userHeroDao.getUserHeroById(userHero);
    }

    public List<UserHeroInfoRsp> getTeamHeroInfoList(Long teamId, TeamInfoRsp rsp){
        if (teamId != null) {
            Map<String, Object> teamParams = new HashMap<>();
            teamParams.put("id", teamId);
            rsp = teamConfigDao.getTeamInfo(teamParams);
        }
        if (rsp == null){
            throw new RRException("领取奖励时获取队伍信息异常");
        }

        // 存储英雄集合
        List<UserHeroInfoRsp> userHeroInfoRsps = new ArrayList<>();
        // 获取英雄1
        if(rsp.getUserHero1Id() != null){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero1Id()));
        }
        // 获取英雄2
        if(rsp.getUserHero2Id() != null){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero2Id()));
        }
        // 获取英雄3
        if(rsp.getUserHero3Id() != null){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero3Id()));
        }
        // 获取英雄4
        if(rsp.getUserHero4Id() != null){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero4Id()));
        }
        // 获取英雄5
        if(rsp.getUserHero5Id() != null){
            userHeroInfoRsps.add(getUserHeroInfo(rsp.getUserHero5Id()));
        }

        return userHeroInfoRsps;
    }

    // 玩家点击领取奖励后系统发放奖品到玩家账户
    public FightClaimRsp claim(UserEntity user, long id) {
        FightClaimRsp rsp = new FightClaimRsp();

        GmCombatRecordEntity combatRecord = combatRecordDao.selectOne(new QueryWrapper<GmCombatRecordEntity>()
                .eq("ID",id)
        );
        if (combatRecord == null){
            throw new RRException("战斗记录失效");
        }

        rsp.setStatus(combatRecord.getStatus());

        if (Constant.enable.equals(combatRecord.getStatus())) {
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
     * 将json字符串转为Javabean
     *
     * @param json  json
     * @param clazz 目标对象的Class类型
     * @param <T>   泛型对象
     * @return 目标对象
     */
    public static <T> T json2bean(String json, Class<T> clazz) {
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


    }
}

