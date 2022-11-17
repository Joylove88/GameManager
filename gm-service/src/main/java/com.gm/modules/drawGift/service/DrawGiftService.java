package com.gm.modules.drawGift.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.utils.*;
import com.gm.modules.basicconfig.dao.ExperiencePotionDao;
import com.gm.modules.basicconfig.dao.HeroFragDao;
import com.gm.modules.basicconfig.dao.HeroInfoDao;
import com.gm.modules.basicconfig.dao.ProbabilityDao;
import com.gm.modules.basicconfig.dto.AttributeSimpleEntity;
import com.gm.modules.basicconfig.dto.DrawDtoTestEntity;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.basicconfig.rsp.GiftBoxEquipmentRsp;
import com.gm.modules.basicconfig.rsp.GiftBoxExpRsp;
import com.gm.modules.basicconfig.rsp.GiftBoxHeroRsp;
import com.gm.modules.basicconfig.service.EquipmentFragService;
import com.gm.modules.basicconfig.service.EquipmentInfoService;
import com.gm.modules.basicconfig.service.StarInfoService;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.SummonReq;
import com.gm.modules.user.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 抽奖业务类
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("drawGiftService")
public class DrawGiftService{
    private static final Logger LOGGER = LoggerFactory.getLogger(DrawGiftService.class);
    @Autowired
    private UserHeroFragService userHeroFragService;
    @Autowired
    private StarInfoService starInfoDao;
    @Autowired
    private UserHeroService userHeroService;
    @Autowired
    private HeroInfoDao heroInfoDao;
    @Autowired
    private HeroFragDao heroFragDao;
    @Autowired
    private ProbabilityDao probabilityDao;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private EquipmentInfoService equipmentInfoService;
    @Autowired
    private EquipmentFragService equipmentFragService;
    @Autowired
    private ExperiencePotionDao experiencePotionDao;
    @Autowired
    private UserEquipmentService userEquipmentService;
    @Autowired
    private UserEquipmentFragService userEquipmentFragService;
    @Autowired
    private UserExperiencePotionService userExperiencePotionService;
    @Autowired
    private CombatStatsUtilsService combatStatsUtilsService;
    @Autowired
    private FightCoreService fightCoreService;
    /**
     * 当前服务器时间
     */
    private static Date now = new Date();
    /**
     * 皮肤等级0普通1黄金...
     */
    private static int skinType = 0;
    /**
     * 初始化属性
     */
    private static AttributeSimpleEntity attributeSimple;
    /**
     * 返回英雄召唤集合
     */
    public static List<GiftBoxHeroRsp> giftBoxHeroRsps = new ArrayList<>();
    /**
     * 返回装备\卷轴召唤集合
     */
    public static List<GiftBoxEquipmentRsp> giftBoxEquipmentRsps = new ArrayList<>();
    /**
     * 返回经验道具召唤集合
     */
    public static List<GiftBoxExpRsp> giftBoxExpRsps = new ArrayList<>();

    /**
     * 进入召唤
     * @param user
     * @param form
     * @return
     * @throws Exception
     */
    public List startSummon(UserEntity user, SummonReq form) throws Exception {
        // 初始化时间
        now = new Date();
        // 初始化返回英雄召唤集合
        giftBoxHeroRsps = new ArrayList<>();
        // 初始化返回装备\卷轴召唤集合
        giftBoxEquipmentRsps = new ArrayList<>();
        // 初始化返回经验召唤集合
        giftBoxExpRsps = new ArrayList<>();
        // 初始化响应集合
        List list = new ArrayList();
        Map<String,Object> proMap = new HashMap<>();
        proMap.put("STATUS", Constant.enable);
        // 获取全部物品概率
        List<ProbabilityEntity> probabilitys = probabilityDao.selectByMap(proMap);
        // 初始化概率存储器
        List<Double> orignalRates = new ArrayList<>(probabilitys.size());
        // 进入召唤逻辑
        if (Constant.SummonType.HERO.getValue().equals(form.getSummonType())){
            // 提取英雄概率
            for (ProbabilityEntity pr : probabilitys) {
                if (pr.getPrType().equals(Constant.SummonType.HERO.getValue())){
                    orignalRates.add(pr.getPr());
                }
            }
            // 英雄召唤
            heroSummonPool(user, form, orignalRates);
            list = giftBoxHeroRsps;
        } else if (Constant.SummonType.EQUIPMENT.getValue().equals(form.getSummonType())){
            // 提取装备概率
            for (ProbabilityEntity pr : probabilitys) {
                if (pr.getPrType().equals(Constant.SummonType.EQUIPMENT.getValue())){
                    orignalRates.add(pr.getPr());
                }
            }
            // 装备召唤
            equipmentSummonPool(user, form, null, orignalRates);
            list = giftBoxEquipmentRsps;
        } else if (Constant.SummonType.EXPERIENCE.getValue().equals(form.getSummonType())){
            // 提取经验道具概率
            for (ProbabilityEntity pr : probabilitys) {
                if (pr.getPrType().equals(Constant.SummonType.EXPERIENCE.getValue())){
                    orignalRates.add(pr.getPr());
                }
            }
            // 经验召唤
            expSummonPool(user, form, orignalRates);
            list = giftBoxExpRsps;
        }
        return list;
    }

    /**
     * 系统发放成品英雄到玩家背包
     * @param prLv
     * @param giftBoxNum
     * @param userId
     * @param txHash
     * @return
     */
    private void sendHeroForPlayer(int prLv, int giftBoxNum, Long userId, String txHash){
        // 获取系统全部英雄
        List<HeroInfoEntity> heroInfos = heroInfoDao.getHeroInfoPro();
        // 获取星级及属性加成信息
        List<StarInfoEntity> starInfos = starInfoDao.getStarInfoPro();
        // 初始化英雄存储集合
        List<UserHeroEntity> userHeroAdds = new ArrayList<>();
        // 初始化皮肤类型
        skinType = 0;
        // 初始化英雄属性
        attributeSimple = null;
        // 根据抽中的盒子数量添加
        int i = 0;
        while (i < giftBoxNum){
            // 随机某个英雄
            Random randomHeroIndex = new Random();
            int heroIndex = randomHeroIndex.nextInt(heroInfos.size());
            // 获取英雄初始属性
            attributeSimple = combatStatsUtilsService.getHeroAttribute(heroInfos.get(heroIndex), 1.0);
            // 使用万能策略模式计算各星级属性
            UniversalStrategyModeTool<Integer> ifFunction = new UniversalStrategyModeTool<>(new HashMap<>());
            ifFunction
                    .add(Constant.PrLv.PrLv2.getValue(), ()->
                            // 1星英雄属性
                            getStarWithAttribute(starInfos.get(0), Constant.SkinType.ORIGINAL.getValue(), attributeSimple)
                    )
                    .add(Constant.PrLv.PrLv3.getValue(), ()->
                            // 2星英雄属性
                            getStarWithAttribute(starInfos.get(1), Constant.SkinType.ORIGINAL.getValue(), attributeSimple)
                    )
                    .add(Constant.PrLv.PrLv4.getValue(), ()->
                            // 3星英雄属性
                            getStarWithAttribute(starInfos.get(2), Constant.SkinType.ORIGINAL.getValue(), attributeSimple)
                    )
                    .add(Constant.PrLv.PrLv5.getValue(), ()->
                            // 4星英雄属性
                            getStarWithAttribute(starInfos.get(3), Constant.SkinType.ORIGINAL.getValue(), attributeSimple)
                    )
                    .add(Constant.PrLv.PrLv6.getValue(), ()->
                            // 5星英雄属性
                            getStarWithAttribute(starInfos.get(4), Constant.SkinType.ORIGINAL.getValue(), attributeSimple)
                    )
                    .add(Constant.PrLv.PrLv7.getValue(), ()->
                            // 黄金1星英雄属性
                            getStarWithAttribute(starInfos.get(0), Constant.SkinType.GOLD.getValue(), attributeSimple)
                    );
            ifFunction.doIf(prLv);

            // 开始发放英雄至玩家背包
            double scale = heroInfos.get(heroIndex).getScale();
            UserHeroEntity userHero = new UserHeroEntity();
            userHero.setHeroId(heroInfos.get(heroIndex).getHeroId());// 英雄ID
            userHero.setHeroLevelId(100000009L);// 初始等级1
            userHero.setUserId(userId);// 用户ID
            userHero.setStatus(Constant.enable);
            userHero.setStatePlay(Constant.disabled);// 默认：未上阵
            userHero.setMintStatus(Constant.enable);
            userHero.setMintHash(txHash);// 交易hash
            userHero.setScale(scale);// 矿工比例
            userHero.setCreateTime(now);// 召唤时间
            userHero.setCreateTimeTs(now.getTime());
            userHero.setNftId(Arith.UUID20());// 生成NFT唯一编码
            // 设置星级
            userHero.setStarCode(attributeSimple.getStarCode());
            // 设置皮肤
            userHero.setSkinType(skinType);
            // ===玩家英雄赋予属性===
            userHero.setHealth(attributeSimple.getHp());
            userHero.setMana(attributeSimple.getMp());
            userHero.setHealthRegen(attributeSimple.getHpRegen());
            userHero.setManaRegen(attributeSimple.getMpRegen());
            userHero.setArmor(attributeSimple.getArmor());
            userHero.setMagicResist(attributeSimple.getMagicResist());
            userHero.setAttackDamage(attributeSimple.getAttackDamage());
            userHero.setAttackSpell(attributeSimple.getAttackSpell());

            // 英雄战力
            double heroPower = 0d;
            // 计算英雄战力
            heroPower = combatStatsUtilsService.getHeroPower(attributeSimple, scale);
            userHero.setHeroPower((long) heroPower);
            // 初始化GAIA经济系统
            fightCoreService.initTradeBalanceParameter();
            // 计算矿工数
            BigDecimal minter = CalculateTradeUtil.updateMiner(BigDecimal.valueOf(userHero.getHeroPower()));
            userHero.setMinter(minter);// 矿工数
            userHero.setOracle(CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1)));// 神谕值
            // 更新系统中保存的市场总鸡蛋
            sysConfigService.updateValueByKey(Constant.MARKET_EGGS, CalculateTradeUtil.marketEggs.toString());
            userHeroAdds.add(userHero);

            // 存储英雄召唤返回集合
            GiftBoxHeroRsp giftBoxHeroRsp = new GiftBoxHeroRsp();
            giftBoxHeroRsp.setHeroName(heroInfos.get(heroIndex).getHeroName());// 英雄名称
            giftBoxHeroRsp.setHeroIconUrl(heroInfos.get(heroIndex).getHeroIconUrl());// 英雄图标
            giftBoxHeroRsp.setHeroFragNum(Constant.Quantity._ONE.getValue());// 英雄碎片数量，如果为星级英雄 数量固定1
            giftBoxHeroRsp.setHeroType(Constant.FragType.WHOLE.getValue());// 英雄类型：0星级英雄，1英雄碎片
            giftBoxHeroRsp.setStarCode(attributeSimple.getStarCode());// 英雄星级
            int skinType = prLv == Constant.PrLv.PrLv7.getValue() ? Constant.SkinType.GOLD.getValue() : Constant.SkinType.ORIGINAL.getValue();
            giftBoxHeroRsp.setSkinType(skinType);// 皮肤类型
            giftBoxHeroRsps.add(giftBoxHeroRsp);
            i++;
        }
        if (userHeroAdds.size() > 0) {
            userHeroService.saveBatch(userHeroAdds);
        }
    }

    /**
     * 通过星级百分比计算对应的属性
     * 获取皮肤类型
     * @param starInfo
     * @param st
     * @param a
     */
    private void getStarWithAttribute(StarInfoEntity starInfo, int st, AttributeSimpleEntity a){
        // 皮肤类型赋值
        skinType = st;
        // 星级属性赋值
        attributeSimple = new AttributeSimpleEntity(starInfo.getStarBuff(), starInfo.getStarCode(), a.getHp(), a.getMp(), a.getHpRegen(), a.getMpRegen(),
                a.getArmor(), a.getMagicResist(), a.getAttackDamage(), a.getAttackSpell());
    }

    /**
     * 系统发放随机1-3数量的英雄碎片到玩家背包
     * @param giftBoxNum 盒子数量
     * @param userId 玩家ID
     * @param txHash 交易HASH
     * @return
     */
    private void sendHeroFlagForPlayer(int giftBoxNum, Long userId, String txHash){
        // 获取系统全部英雄碎片信息
        List<HeroFragEntity> heroFrags = heroFragDao.getHeroFragPro();
        // 存储要添加的碎片集合
        List<UserHeroFragEntity> userHeroFragAdds = new ArrayList<>();
        int i = 0;
        while (i < giftBoxNum){
            // 随机某个英雄的碎片
            Random randomHeroIndex = new Random();
            int heroIndex = randomHeroIndex.nextInt(heroFrags.size());

            // 随机1-3英雄碎片数量
            Random randomFlagNum = new Random();
            int flagNum = randomFlagNum.nextInt(4);
            if ( flagNum == 0 ) {
                flagNum = 1;
            }

            int j = 0;
            while (j < flagNum) {
                // 开始发放碎片至玩家背包
                UserHeroFragEntity userHeroFrag = new UserHeroFragEntity();
                userHeroFrag.setHeroFragId(heroFrags.get(heroIndex).getHeroFragId());
                userHeroFrag.setUserHeroFragNum(Constant.Quantity._ONE.getValue());
                userHeroFrag.setUserId(userId);
                userHeroFrag.setStatus(Constant.enable);
                userHeroFrag.setMintStatus(Constant.enable);
                userHeroFrag.setMintHash(txHash);
                userHeroFrag.setCreateTime(now);
                userHeroFrag.setCreateTimeTs(now.getTime());
                userHeroFragAdds.add(userHeroFrag);
                j++;
            }

            // 存储英雄召唤返回集合
            GiftBoxHeroRsp giftBoxHeroRsp = new GiftBoxHeroRsp();
            giftBoxHeroRsp.setHeroName(heroFrags.get(heroIndex).getHeroName());// 英雄名称
            giftBoxHeroRsp.setHeroIconUrl(heroFrags.get(heroIndex).getHeroIconUrl());// 英雄图标
            giftBoxHeroRsp.setStarCode(Constant.ZERO_I);// 英雄星级
            giftBoxHeroRsp.setHeroFragNum(flagNum);// 英雄碎片数量，如果为星级英雄 数量固定1
            giftBoxHeroRsp.setHeroType(Constant.FragType.FRAG.getValue());// 英雄类型：0星级英雄，1英雄碎片
            giftBoxHeroRsp.setSkinType(-1);// 皮肤类型
            giftBoxHeroRsps.add(giftBoxHeroRsp);

            i++;
        }
        // 批量插入
        if (userHeroFragAdds.size() > 0) {
            userHeroFragService.saveBatch(userHeroFragAdds);
        }
    }

    /**
     * 英雄召唤池
     * @param user
     * @param summonReq
     * @return
     * @throws Exception
     */
    private void heroSummonPool(UserEntity user,SummonReq summonReq, List<Double> orignalRates) throws Exception {
        // 抽奖次数
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, summonReq.getSummonNum());
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            // 当前概率等级奖品的数量
            int giftBoxNum = entry.getValue();
            // 使用万能策略模式
            UniversalStrategyModeTool<Integer> ifFunction = new UniversalStrategyModeTool<>(new HashMap<>());
            ifFunction
                    .add(Constant.PrLv.PrLv1.getValue(), ()->
                            // 1级概率产出1-3碎片
                            sendHeroFlagForPlayer(giftBoxNum, user.getUserId(), summonReq.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv2.getValue(), ()->
                            // 2级概率产出1星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv2.getValue(), giftBoxNum, user.getUserId(), summonReq.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv3.getValue(), ()->
                            // 3级概率产出2星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv3.getValue(), giftBoxNum, user.getUserId(), summonReq.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv4.getValue(), ()->
                            // 4级概率产出3星英雄
                                sendHeroForPlayer(Constant.PrLv.PrLv4.getValue(), giftBoxNum, user.getUserId(), summonReq.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv5.getValue(), ()->
                            // 5级概率产出4星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv5.getValue(), giftBoxNum, user.getUserId(), summonReq.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv6.getValue(), ()->
                            // 6级概率产出5星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv6.getValue(), giftBoxNum, user.getUserId(), summonReq.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv7.getValue(), ()->
                            // 7级概率产出黄金1星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv7.getValue(), giftBoxNum, user.getUserId(), summonReq.getTransactionHash())
                    );
            ifFunction.doIf(entry.getKey() + Constant.Quantity._ONE.getValue());
            //            LOGGER.info(gifts.get(entry.getKey()).getGmHeroStarId() + ", count=" + entry.getValue() + ", probability="
//                    + entry.getValue());
        }
    }

    /**
     * 系统发放装备到玩家背包
     * @param prLv 概率等级
     * @param map 条件集合
     * @param combatRecord 战斗记录
     */
    private void sendEquipmentForPlayer(int prLv, Map<String, Object> map, GmCombatRecordEntity combatRecord){
        // 获取盒子数量
        int giftBoxNum = Integer.parseInt(map.get("giftBoxNum").toString());
        // 获取指定稀有度的装备
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("STATUS", Constant.enable);
        infoMap.put("EQUIP_RARECODE", prLv + "");
        List<EquipmentInfoEntity> equipmentInfos = equipmentInfoService.getEquipmentInfos(infoMap);
        // 存储发放的装备集合
        List<UserEquipmentEntity> userEquipmentAdds = new ArrayList<>();
        int i = 0;
        while (i < giftBoxNum){
            // 随机生成一件装备下标
            Random randomHeroIndex = new Random();
            int equipmentIndex = randomHeroIndex.nextInt(equipmentInfos.size());
            // 开始发放装备至玩家背包
            UserEquipmentEntity userEquipment = new UserEquipmentEntity();
            userEquipment.setEquipmentId(equipmentInfos.get(equipmentIndex).getEquipId());
            userEquipment.setUserId(Long.valueOf(map.get("userId").toString()));
            userEquipment.setStatus(Constant.enable);
            userEquipment.setScale(equipmentInfos.get(equipmentIndex).getScale());// 矿工比例
            // 生成NFT唯一编码
            userEquipment.setNftId(Arith.UUID20());

            // 获取随机属性最大百分比
            Double eqAttMin = Constant.EquipStatsRange.MIN.getValue();
            // 获取随机属性最小百分比
            Double eqAttMax = Constant.EquipStatsRange.MAX.getValue();
            // 如果战斗记录为空本次为召唤反之为副本产出
            if (combatRecord == null) {
                String txHash = null == map.get("txHash") ? null : map.get("txHash").toString();
                userEquipment.setFromType(Constant.FromType.SUMMON.getValue());
                userEquipment.setMintHash(txHash);
                userEquipment.setMintStatus(Constant.enable);
            } else {
                eqAttMin = Constant.EquipStatsRange.MIN_FREE.getValue();
                eqAttMax = Constant.EquipStatsRange.MAX_FREE.getValue();
                userEquipment.setFromType(Constant.FromType.DUNGEON.getValue());
                userEquipment.setSourceId(combatRecord.getId());
            }
            // 随机装备属性
            Double rap = Arith.randomWithinRangeHundred(eqAttMax, eqAttMin);
            long health = (long) (equipmentInfos.get(equipmentIndex).getEquipHealth() * rap);// 初始生命值
            long mana = (long) (equipmentInfos.get(equipmentIndex).getEquipMana() * rap);// 初始法力值
            double healthRegen = (equipmentInfos.get(equipmentIndex).getEquipHealthRegen() * rap);// 初始生命值恢复
            double manaRegen = (equipmentInfos.get(equipmentIndex).getEquipManaRegen() * rap);// 初始法力值恢复
            long armor = (long) (equipmentInfos.get(equipmentIndex).getEquipArmor() * rap);// 初始护甲
            long magicResist = (long) (equipmentInfos.get(equipmentIndex).getEquipMagicResist() * rap);// 初始魔抗
            long attackDamage = (long) (equipmentInfos.get(equipmentIndex).getEquipAttackDamage() * rap);// 初始攻击力
            long attackSpell = (long) (equipmentInfos.get(equipmentIndex).getEquipAttackSpell() * rap);// 初始法攻
            userEquipment.setEquipHealth(health);
            userEquipment.setEquipMana(mana);
            userEquipment.setEquipHealthRegen(healthRegen);
            userEquipment.setEquipManaRegen(manaRegen);
            userEquipment.setEquipArmor(armor);
            userEquipment.setEquipMagicResist(magicResist);
            userEquipment.setEquipAttackDamage(attackDamage);
            userEquipment.setEquipAttackSpell(attackSpell);
            // 更新装备战力
            double equipPower = (health * 0.1) + (mana * 0.1) + attackDamage + attackSpell + ((armor + magicResist) * 4.5) + healthRegen * 0.1 + manaRegen * 0.3;
            userEquipment.setEquipPower((long) equipPower);
            // 初始化GAIA经济系统
            fightCoreService.initTradeBalanceParameter();
            // 计算矿工数
            BigDecimal minter = CalculateTradeUtil.updateMiner(BigDecimal.valueOf(userEquipment.getEquipPower()));
            userEquipment.setMinter(minter);// 矿工数
            userEquipment.setOracle(CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1)));// 神谕值
            // 更新系统中保存的市场总鸡蛋
            sysConfigService.updateValueByKey(Constant.MARKET_EGGS, CalculateTradeUtil.marketEggs.toString());

            userEquipment.setCreateTime(now);
            userEquipment.setCreateTimeTs(now.getTime());
            userEquipmentAdds.add(userEquipment);
            // 存储装备卷轴召唤返回集合
            GiftBoxEquipmentRsp giftBoxEquipmentRsp = new GiftBoxEquipmentRsp();
            giftBoxEquipmentRsp.setEquipName(equipmentInfos.get(equipmentIndex).getEquipName());// 名称
            giftBoxEquipmentRsp.setEquipIconUrl(equipmentInfos.get(equipmentIndex).getEquipIconUrl());// 图标
            giftBoxEquipmentRsp.setEquipRareCode(Integer.valueOf(equipmentInfos.get(equipmentIndex).getEquipRarecode()));// 稀有度
            giftBoxEquipmentRsp.setEquipType(Constant.FragType.WHOLE.getValue());// 装备类型：0 装备,1 卷轴
            giftBoxEquipmentRsp.setEquipFragNum(Constant.Quantity._ONE.getValue());// 数量
            giftBoxEquipmentRsps.add(giftBoxEquipmentRsp);
            i++;
        }
        // 批量插入
        if (userEquipmentAdds.size() > 0) {
            userEquipmentService.saveBatch(userEquipmentAdds);
        }
    }

    /**
     * 系统发放1数量的装备卷轴到玩家背包
     * @param prLv 概率等级
     * @param map 条件集合
     * @param combatRecord 战斗记录
     */
    private void sendEquipmentFlagForPlayer(int prLv, Map<String, Object> map, GmCombatRecordEntity combatRecord){
        // 获取盒子数量
        int giftBoxNum = Integer.parseInt(map.get("giftBoxNum").toString());
        // 获取指定稀有度的装备卷轴
        Map<String, Object> fragMap = new HashMap<>();
        fragMap.put("status", Constant.enable);
        fragMap.put("rareCode", prLv + "");
        List<EquipmentFragEntity> equipmentFrags = equipmentFragService.getEquipmentFragPro(fragMap);
        // 存储发放的装备卷轴集合
        List<UserEquipmentFragEntity> userEquipmentFragAdds = new ArrayList<>();
        int i = 0;
        while (i < giftBoxNum){
            // 随机生成卷轴下标
            Random randomHeroIndex = new Random();
            int equipmentFragIndex = randomHeroIndex.nextInt(equipmentFrags.size());
            // 开始发放卷轴至玩家背包
            UserEquipmentFragEntity userEquipmentFrag = new UserEquipmentFragEntity();
            userEquipmentFrag.setUserEquipmentFragId(equipmentFrags.get(equipmentFragIndex).getEquipmentFragId());
            userEquipmentFrag.setUserId(Long.valueOf(map.get("userId").toString()));
            userEquipmentFrag.setStatus(Constant.enable);
            // 如果战斗记录为空本次为召唤反之为副本产出
            if (combatRecord == null) {
                String txHash = null == map.get("txHash") ? null : map.get("txHash").toString();
                userEquipmentFrag.setFromType(Constant.FromType.SUMMON.getValue());
                userEquipmentFrag.setMintHash(txHash);
                userEquipmentFrag.setMintStatus(Constant.enable);
            } else {
                userEquipmentFrag.setFromType(Constant.FromType.DUNGEON.getValue());
                userEquipmentFrag.setSourceId(combatRecord.getId());
            }
            userEquipmentFrag.setUserEquipFragNum(Constant.Quantity._ONE.getValue());
            userEquipmentFrag.setCreateTime(now);
            userEquipmentFrag.setCreateTimeTs(now.getTime());
            userEquipmentFragAdds.add(userEquipmentFrag);
            // 存储装备卷轴召唤返回集合
            GiftBoxEquipmentRsp giftBoxEquipmentRsp = new GiftBoxEquipmentRsp();
            giftBoxEquipmentRsp.setEquipName(equipmentFrags.get(equipmentFragIndex).getEquipName());// 名称
            giftBoxEquipmentRsp.setEquipIconUrl(equipmentFrags.get(equipmentFragIndex).getEquipFragIconUrl());// 图标
            giftBoxEquipmentRsp.setEquipRareCode(Integer.valueOf(equipmentFrags.get(equipmentFragIndex).getEquipRarecode()));// 稀有度
            giftBoxEquipmentRsp.setEquipType(Constant.FragType.FRAG.getValue());// 装备类型：0 装备,1 卷轴
            giftBoxEquipmentRsp.setEquipFragNum(Constant.Quantity._ONE.getValue());// 数量
            giftBoxEquipmentRsps.add(giftBoxEquipmentRsp);
            i++;
        }
        // 批量插入
        if (userEquipmentFragAdds.size() > 0) {
            userEquipmentFragService.saveBatch(userEquipmentFragAdds);
        }
    }

    /**
     * 装备\卷轴召唤池
     * @param user
     * @param summonReq
     * @param combatRecord
     * @param orignalRates
     */
    private void equipmentSummonPool(UserEntity user, SummonReq summonReq, GmCombatRecordEntity combatRecord, List<Double> orignalRates){
        Map<String, Object> map = new HashMap<>();
        // 如果战斗记录为空本次为召唤反之为副本产出
        if (combatRecord == null){
            map.put("txHash", summonReq.getTransactionHash());
        }
        // 抽奖次数
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, summonReq.getSummonNum());
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            // 当前概率等级奖品的数量
            int giftBoxNum = entry.getValue();
            map.put("giftBoxNum", giftBoxNum);
            map.put("userId", user.getUserId());
            // 使用万能策略模式
            UniversalStrategyModeTool<Integer> ifFunction = new UniversalStrategyModeTool<>(new HashMap<>());
            ifFunction
                    .add(Constant.PrLv.PrLv1.getValue(), ()->
                            // 1级概率产出白色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv1.getValue(), map, combatRecord)
                    )
                    .add(Constant.PrLv.PrLv2.getValue(), ()->
                            // 2级概率产出绿色卷轴*1
                            sendEquipmentFlagForPlayer(Constant.PrLv.PrLv2.getValue(),  map, combatRecord)
                    )
                    .add(Constant.PrLv.PrLv3.getValue(), ()->
                            // 3级概率产出绿色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv2.getValue(), map, combatRecord)
                    )
                    .add(Constant.PrLv.PrLv4.getValue(), ()->
                            // 4级概率产出紫色卷轴*1
                            sendEquipmentFlagForPlayer(Constant.PrLv.PrLv4.getValue(),  map, combatRecord)
                    )
                    .add(Constant.PrLv.PrLv5.getValue(), ()->
                            // 5级概率产出蓝色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv3.getValue(), map, combatRecord)
                    )
                    .add(Constant.PrLv.PrLv6.getValue(), ()->
                            // 6级概率产出紫色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv4.getValue(), map, combatRecord)
                    )
                    .add(Constant.PrLv.PrLv7.getValue(), ()->
                            // 7级概率产出橙色卷轴*1
                            sendEquipmentFlagForPlayer(Constant.PrLv.PrLv5.getValue(), map, combatRecord)
                    )
                    .add(Constant.PrLv.PrLv8.getValue(), ()->
                            // 8级概率产出橙色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv5.getValue(), map, combatRecord)
                    );
            ifFunction.doIf(entry.getKey() + Constant.Quantity._ONE.getValue());
//            LOGGER.info(gifts.get(entry.getKey()).getEquipId() + ", count=" + entry.getValue() + ", probability="
//                    + entry.getValue());
        }
    }

    /**
     * 副本产出的装备
     * @param user
     * @param dungeon
     * @param combatRecord
     * @return
     * @throws Exception
     */
    public void claimEquipment(UserEntity user, GmDungeonConfigEntity dungeon, GmCombatRecordEntity combatRecord) throws Exception {
        // 初始化时间
        now = new Date();
        // 初始化返回英雄召唤集合
        giftBoxHeroRsps = new ArrayList<>();
        // 初始化返回装备\卷轴召唤集合
        giftBoxEquipmentRsps = new ArrayList<>();
        // 先获概率等级 概率等级越高 获奖几率越低
        String[] eqRCs = dungeon.getRangeEquip().split("-");
        List<String> eqRCList = new ArrayList<>();
        eqRCList.addAll(Arrays.asList(eqRCs));

        List<ProbabilityEntity> probabilitys = probabilityDao.selectList(new QueryWrapper<ProbabilityEntity>()
                .eq("STATUS", Constant.enable)
                .eq("PR_TYPE", Constant.SummonType.EQUIPMENT.getValue())
                .in("PR_LV", eqRCList)
        );

        // 存储每件物品的中奖概率
        List<Double> orignalRates = new ArrayList<>(probabilitys.size());
        for (ProbabilityEntity pr : probabilitys) {
            orignalRates.add(pr.getPr());
        }
        // 随机副本产出装备数量
        Random rm = new Random();
        int rmon = rm.nextInt(3);
        if (rmon < 1) {
            rmon = 1;
        }
        SummonReq summonReq = new SummonReq();
        summonReq.setSummonNum(rmon);
        // 开始奖品发放
        equipmentSummonPool(user, summonReq, combatRecord, orignalRates);
    }

    /**
     * 经验召唤池
     * @param user
     * @param summonReq
     * @param orignalRates
     * @return
     */
    private void expSummonPool(UserEntity user, SummonReq summonReq, List<Double> orignalRates){
        Map<String, Object> map = new HashMap<>();
        map.put("txHash", summonReq.getTransactionHash());
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, summonReq.getSummonNum());
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            // 当前概率等级奖品的数量
            int giftBoxNum = entry.getValue();
            map.put("giftBoxNum", giftBoxNum);
            map.put("userId", user.getUserId());
            // 使用万能策略模式
            UniversalStrategyModeTool<Integer> ifFunction = new UniversalStrategyModeTool<>(new HashMap<>());
            ifFunction
                    .add(Constant.PrLv.PrLv1.getValue(), ()->
                            // 1级概率产出2白色，1绿色
                            sendExpPropForPlayer(Constant.PrLv.PrLv1.getValue(), map)
                    )
                    .add(Constant.PrLv.PrLv2.getValue(), ()->
                            // 2级概率产出4白色，2绿色，1蓝色
                            sendExpPropForPlayer(Constant.PrLv.PrLv2.getValue(),  map)
                    )
                    .add(Constant.PrLv.PrLv3.getValue(), ()->
                            // 3级概率产出6白色，4绿色，2蓝色，1紫色
                            sendExpPropForPlayer(Constant.PrLv.PrLv2.getValue(), map)
                    )
                    .add(Constant.PrLv.PrLv4.getValue(), ()->
                            // 3级概率产出8白色，5绿色，3蓝色，2紫色
                            sendExpPropForPlayer(Constant.PrLv.PrLv4.getValue(),  map)
                    );
            ifFunction.doIf(entry.getKey() + Constant.Quantity._ONE.getValue());
//            LOGGER.info(gifts.get(entry.getKey()).getEquipId() + ", count=" + entry.getValue() + ", probability="
//                    + entry.getValue());
        }
    }

    /**
     * 系统发放经验道具至玩家背包
     * @param prLv
     * @param map
     */
    private void sendExpPropForPlayer(int prLv, Map<String, Object> map){
        // 获取盒子数量
        int giftBoxNum = Integer.parseInt(map.get("giftBoxNum").toString());
        // 获取系统全部经验信息
        Map<String, Object> expMap = new HashMap<>();
        expMap.put("status", Constant.enable);
        List<ExperiencePotionEntity> exps = experiencePotionDao.getExpInfos(expMap);
        // 存储要添加的经验的集合
        List<UserExperiencePotionEntity> userExpAdds = new ArrayList<>();
        int i = 0;
        while (i < giftBoxNum){
            int expsI = prLv - 1;
            int j = 0;
            while (j < prLv) {
                // 开始发放经验至玩家背包
                String txHash = null == map.get("txHash") ? null : map.get("txHash").toString();
                UserExperiencePotionEntity userExp = new UserExperiencePotionEntity();
                userExp.setExPotionId(exps.get(expsI).getExPotionId());
                userExp.setUserId(Long.valueOf(map.get("userId").toString()));
                userExp.setUserExNum(Constant.Quantity._ONE.getValue());
                userExp.setStatus(Constant.enable);
                userExp.setMintStatus(Constant.enable);
                userExp.setMintHash(txHash);
                userExp.setCreateTime(now);
                userExp.setCreateTimeTs(now.getTime());
                userExpAdds.add(userExp);
                j++;
            }

            // 存储英雄召唤返回集合
            GiftBoxExpRsp giftBoxExpRsp = new GiftBoxExpRsp();
            giftBoxExpRsp.setExpName(exps.get(expsI).getExPotionName());// 经验名称
            giftBoxExpRsp.setExpNum(prLv);// 经验数量
            giftBoxExpRsp.setExpRare(Integer.valueOf(exps.get(expsI).getExPotionRareCode()));// 经验稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
            giftBoxExpRsp.setExpValue(exps.get(expsI).getExValue());// 经验值
            giftBoxExpRsp.setExpIconUrl(exps.get(expsI).getExIconUrl());// 图标地址
            giftBoxExpRsp.setExpDescription(exps.get(expsI).getExDescription());// 经验描述
            giftBoxExpRsps.add(giftBoxExpRsp);
            i++;
        }
        // 批量插入
        if (userExpAdds.size() > 0) {
            userExperiencePotionService.saveBatch(userExpAdds);
        }
    }

    public static void main(String[] args) {
        List<DrawDtoTestEntity> gifts = new ArrayList<>();
        DrawDtoTestEntity nothing1 = new DrawDtoTestEntity("英雄碎片1-3", 50.0);
//        DrawDtoTestEntity nothing2 = new DrawDtoTestEntity("英雄碎片2", 10.5);
//        DrawDtoTestEntity nothing3 = new DrawDtoTestEntity("英雄碎片3", 5.5);
//        DrawDtoTestEntity nothing4 = new DrawDtoTestEntity("英雄碎片4", 0.25);
//        DrawDtoTestEntity nothing5 = new DrawDtoTestEntity("英雄碎片5", 0.025);
        DrawDtoTestEntity hero1 = new DrawDtoTestEntity("1☆英雄A", 33.35);
        DrawDtoTestEntity hero2 = new DrawDtoTestEntity("2☆英雄A", 11.11);
        DrawDtoTestEntity hero3 = new DrawDtoTestEntity("3☆英雄A", 3.7);
        DrawDtoTestEntity hero4 = new DrawDtoTestEntity("4☆英雄A", 1.23);
        DrawDtoTestEntity hero5 = new DrawDtoTestEntity("5☆英雄A", 0.41);
        DrawDtoTestEntity hero6 = new DrawDtoTestEntity("黄金英雄", 0.2);
//        DrawDtoTestEntity hero1B = new DrawDtoTestEntity("1☆英雄B", 25.215);
//        DrawDtoTestEntity hero2B = new DrawDtoTestEntity("2☆英雄B", 15.35);
//        DrawDtoTestEntity hero3B = new DrawDtoTestEntity("3☆英雄B", 7.15);
//        DrawDtoTestEntity hero4B = new DrawDtoTestEntity("4☆英雄B", 0.1);
//        DrawDtoTestEntity hero5B = new DrawDtoTestEntity("5☆英雄B", 0.01);
//        DrawDtoTestEntity hero1C = new DrawDtoTestEntity("1☆英雄C", 25.215);
//        DrawDtoTestEntity hero2C = new DrawDtoTestEntity("2☆英雄C", 15.35);
//        DrawDtoTestEntity hero3C = new DrawDtoTestEntity("3☆英雄C", 7.15);
//        DrawDtoTestEntity hero4C = new DrawDtoTestEntity("4☆英雄C", 0.1);
//        DrawDtoTestEntity hero5C = new DrawDtoTestEntity("5☆英雄C", 0.01);
//        DrawDtoTestEntity hero1D = new DrawDtoTestEntity("1☆英雄D", 25.215);
//        DrawDtoTestEntity hero2D = new DrawDtoTestEntity("2☆英雄D", 15.35);
//        DrawDtoTestEntity hero3D = new DrawDtoTestEntity("3☆英雄D", 7.15);
//        DrawDtoTestEntity hero4D = new DrawDtoTestEntity("4☆英雄D", 0.1);
//        DrawDtoTestEntity hero5D = new DrawDtoTestEntity("5☆英雄D", 0.01);

        gifts.add(nothing1);
//        gifts.add(nothing2);
//        gifts.add(nothing3);
//        gifts.add(nothing4);
//        gifts.add(nothing5);
        gifts.add(hero1);
        gifts.add(hero2);
        gifts.add(hero3);
        gifts.add(hero4);
        gifts.add(hero5);
        gifts.add(hero6);
//        gifts.add(hero1B);
//        gifts.add(hero2B);
//        gifts.add(hero3B);
//        gifts.add(hero4B);
//        gifts.add(hero5B);
//        gifts.add(hero1C);
//        gifts.add(hero2C);
//        gifts.add(hero3C);
//        gifts.add(hero4C);
//        gifts.add(hero5C);
//        gifts.add(hero1D);
//        gifts.add(hero2D);
//        gifts.add(hero3D);
//        gifts.add(hero4D);
//        gifts.add(hero5D);


        //获取每件物品的中奖概率
        List<Double> orignalRates = new ArrayList<Double>(gifts.size());
        for (DrawDtoTestEntity gift: gifts) {
            double probability = gift.getGmProbability();
            if (probability < 0) {
                probability = 0;
            }
            orignalRates.add(probability);
        }
        int j = 0;
        while (j < 1){
            // statistics
            Map<Integer, Integer> count = new HashMap<Integer, Integer>();
            //召唤次数
            double num = 10;
            for (int i = 0; i < num; i++) {
                int orignalIndex = LotteryGiftsUtils.lottery(orignalRates);

                Integer value = count.get(orignalIndex);
                count.put(orignalIndex, value == null ? 1 : value + 1);
            }

            for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
                System.out.println(gifts.get(entry.getKey()).getHeroName() + ", count=" + entry.getValue() + ", probability="
                        + entry.getValue() / num);
            }
            j++;
        }
    }

}

