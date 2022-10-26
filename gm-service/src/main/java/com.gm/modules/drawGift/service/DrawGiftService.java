package com.gm.modules.drawGift.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.utils.*;
import com.gm.modules.basicconfig.dao.*;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.basicconfig.dto.*;
import com.gm.modules.basicconfig.rsp.GiftBoxHeroRsp;
import com.gm.modules.basicconfig.service.StarInfoService;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.dao.*;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.service.UserHeroFragService;
import com.gm.modules.user.service.UserHeroService;
import org.apache.commons.beanutils.BeanUtils;
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
    private static List<GiftBoxHeroRsp> giftBoxHeroRsps = new ArrayList<>();

    /**
     * 进入召唤
     * @param user
     * @param form
     * @return
     * @throws Exception
     */
    public List<Object> startSummon(UserEntity user, DrawForm form) throws Exception {
        List<Object> gifts = new ArrayList<>();
        now = new Date();
        if (Constant.SummonType.HERO.getValue().equals(form.getSummonType())){
            // 初始化返回英雄召唤集合
            giftBoxHeroRsps = new ArrayList<>();
            // 英雄召唤
            gifts = heroSummonPool(user,form);
        } else if (Constant.SummonType.EQUIPMENT.getValue().equals(form.getSummonType())){
            // 装备召唤
            gifts = equipmentSummonPool(user,form);
        } else if (Constant.SummonType.EXPERIENCE.getValue().equals(form.getSummonType())){
            // 经验召唤
            gifts = expSummonPool(user,form);
        }
        return gifts;
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
            giftBoxHeroRsp.setHeroType(Constant.ZERO_I);// 英雄类型：0星级英雄，1英雄碎片
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
            giftBoxHeroRsp.setHeroFragNum(Constant.Quantity._ONE.getValue());// 英雄碎片数量，如果为星级英雄 数量固定1
            giftBoxHeroRsp.setHeroType(Constant.ZERO_I);// 英雄类型：0星级英雄，1英雄碎片
            giftBoxHeroRsp.setSkinType(null);// 皮肤类型
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
     * @param drawForm
     * @return
     * @throws Exception
     */
    public List<Object> heroSummonPool(UserEntity user,DrawForm drawForm) throws Exception {
        // 先获取召唤概率等级 概率等级越高 获奖几率越低
        Map<String,Object> proMap = new HashMap<>();
        proMap.put("STATUS", Constant.enable);
        proMap.put("PR_TYPE", Constant.SummonType.HERO.getValue());
        List<ProbabilityEntity> probability = probabilityDao.selectByMap(proMap);
        // 创建一个储存召唤物品+概率的集合
        List<GiftBoxPrDto> gifts = new ArrayList<>();
        // 存储每件物品的中奖概率
        List<Double> orignalRates = new ArrayList<>(gifts.size());

        for (ProbabilityEntity aProbability : probability) {
            // 通过召唤获取概率等级
            GiftBoxPrDto gift = new GiftBoxPrDto();
            gift.setPr(aProbability.getPr());
            gift.setPrLv(aProbability.getPrLv());
            gifts.add(gift);
            if (aProbability.getPr() < 0) {
                gift.setPr(0d);
            }
            orignalRates.add(gift.getPr());
        }

        // 抽奖次数
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, drawForm.getSummonNum());

        // 奖品集合
        List<Object> giftBoxs = new ArrayList<>();
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            // 当前概率等级奖品的数量
            int giftBoxNum = entry.getValue();
            // 使用万能策略模式
            UniversalStrategyModeTool<Integer> ifFunction = new UniversalStrategyModeTool<>(new HashMap<>());
            ifFunction
                    .add(Constant.PrLv.PrLv1.getValue(), ()->
                            // 1级概率产出1-3碎片
                            sendHeroFlagForPlayer(giftBoxNum, user.getUserId(), drawForm.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv2.getValue(), ()->
                            // 2级概率产出1星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv2.getValue(), giftBoxNum, user.getUserId(), drawForm.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv3.getValue(), ()->
                            // 3级概率产出2星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv3.getValue(), giftBoxNum, user.getUserId(), drawForm.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv4.getValue(), ()->
                            // 4级概率产出3星英雄
                                sendHeroForPlayer(Constant.PrLv.PrLv4.getValue(), giftBoxNum, user.getUserId(), drawForm.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv5.getValue(), ()->
                            // 5级概率产出4星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv5.getValue(), giftBoxNum, user.getUserId(), drawForm.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv6.getValue(), ()->
                            // 6级概率产出5星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv6.getValue(), giftBoxNum, user.getUserId(), drawForm.getTransactionHash())
                    )
                    .add(Constant.PrLv.PrLv7.getValue(), ()->
                            // 7级概率产出黄金1星英雄
                            sendHeroForPlayer(Constant.PrLv.PrLv7.getValue(), giftBoxNum, user.getUserId(), drawForm.getTransactionHash())
                    );
            ifFunction.doIf(gifts.get(entry.getKey()).getPrLv());
            //            LOGGER.info(gifts.get(entry.getKey()).getGmHeroStarId() + ", count=" + entry.getValue() + ", probability="
//                    + entry.getValue());
        }
        return giftBoxs;
    }

    // 装备召唤池
    public List<Object> equipmentSummonPool(UserEntity user, DrawForm drawForm) throws Exception {
        // 奖品发放集合
        List<Object> gifList;

        // 先获取召唤概率等级 概率等级越高 获奖几率越低
        Map<String, Object> proMap = new HashMap<>();
        proMap.put("STATUS", Constant.enable);
        proMap.put("GM_TYPE", Constant.SummonType.EQUIPMENT.getValue());
        List<ProbabilityEntity> probability = probabilityDao.selectByMap(proMap);
        // 创建一个储存召唤物品+概率的集合
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


    // 经验召唤池
    public List<Object> expSummonPool(UserEntity user, DrawForm drawForm){
        // 先获取召唤概率等级 概率等级越高 获奖几率越低
        Map<String,Object> proMap = new HashMap<>();
        proMap.put("STATUS", Constant.enable);
        proMap.put("GM_TYPE", Constant.SummonType.EXPERIENCE.getValue());
        List<ProbabilityEntity> probability = probabilityDao.selectByMap(proMap);
        // 创建一个储存召唤物品+概率的集合
        List<ExDrawGiftDtoEntity> gifts = new ArrayList<>();
        for (ProbabilityEntity aProbability : probability) {
            double pron = aProbability.getPr();
            // 通过装备稀有度等级获取对应的装备
            Map<String, Object> exMap = new HashMap<>();
            exMap.put("STATUS", 1);
            List<ExperiencePotionEntity> exs = experiencePotionDao.selectByMap(exMap);
            for (ExperiencePotionEntity ex : exs) {
                ExDrawGiftDtoEntity entity = new ExDrawGiftDtoEntity();
                long pronLv = aProbability.getPrLv();
                long exPotionId = ex.getExPotionId();
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
                    if (exRC.equals(Constant.RareCode._WHITE.getValue())) {
                        // 如果稀有度为白色
                        entity.setExNum(2L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._GREEN.getValue())) {
                        // 如果稀有度为绿色
                        entity.setExNum(1L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                } else if (pronLv == 2) {
                    // 概率等级为2
                    if (exRC.equals(Constant.RareCode._WHITE.getValue())) {
                        // 如果稀有度为白色
                        entity.setExNum(4L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._GREEN.getValue())) {
                        // 如果稀有度为绿色
                        entity.setExNum(2L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._BLUE.getValue())) {
                        // 如果稀有度为蓝色
                        entity.setExNum(1L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                } else if (pronLv == 3) {
                    // 概率等级为3
                    if (exRC.equals(Constant.RareCode._WHITE.getValue())) {
                        // 如果稀有度为白色
                        entity.setExNum(6L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._GREEN.getValue())) {
                        // 如果稀有度为绿色
                        entity.setExNum(3L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._BLUE.getValue())) {
                        // 如果稀有度为蓝色
                        entity.setExNum(2L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._PURPLE.getValue())) {
                        // 如果稀有度为紫色
                        entity.setExNum(1L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                } else if (pronLv == 4) {
                    // 概率等级为3
                    if (exRC.equals(Constant.RareCode._WHITE.getValue())) {
                        // 如果稀有度为白色
                        entity.setExNum(8L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._GREEN.getValue())) {
                        // 如果稀有度为绿色
                        entity.setExNum(5L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._BLUE.getValue())) {
                        // 如果稀有度为蓝色
                        entity.setExNum(3L);
                        gifts.add(AddExGiftInfo(entity));
                    }
                    if (exRC.equals(Constant.RareCode._PURPLE.getValue())) {
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

        // 如果类型为1 说明是单抽模式.类型为2 说明是十连抽模式
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, drawForm.getSummonNum());

        // 奖品集合
        List<Object> gifList = new ArrayList<>();
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            Date now = new Date();
            Long exNum = gifts.get(entry.getKey()).getExNum();
            // 加一层校验 召唤池经验药水数量不小于0
            if(exNum > 0) {
                for (int fn = 0; fn < exNum; fn++) {
                    UserExperiencePotionEntity userEx = new UserExperiencePotionEntity();
                    userEx.setExPotionId(gifts.get(entry.getKey()).getExId());
                    userEx.setUserId(user.getUserId());
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

    // 装备召唤公用接口
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

        // 召唤次数
        int drawNum = 0;
        // 如果为副本产出分为3级别每级对应召唤次数 为召唤产出固定单抽十连抽
        if (combatRecordEntity != null){
            // 随机副本产出装备数量
            Random rm = new Random();
            int rmon = rm.nextInt(4);
            if ( rmon < 1 ) {
                rmon = 1;
            }
            drawNum = rmon;
        } else {
            drawNum = drawForm.getSummonNum();
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
                userEquip.setEquipmentId(gifts.get(entry.getKey()).getEquipId());
                userEquip.setUserId(user.getUserId());
                userEquip.setStatus(Constant.enable);
                userEquip.setScale(gifts.get(entry.getKey()).getScale());// 矿工比例
                userEquip.setCreateTime(now);
                userEquip.setCreateTimeTs(now.getTime());

                long health = gifts.get(entry.getKey()).getEquipHealth() != null ? gifts.get(entry.getKey()).getEquipHealth() : 0;//初始生命值
                long mana = gifts.get(entry.getKey()).getEquipMana() != null ? gifts.get(entry.getKey()).getEquipMana() : 0;//初始法力值
                long healthRegen = gifts.get(entry.getKey()).getEquipHealthRegen() != null ? gifts.get(entry.getKey()).getEquipHealthRegen() : 0;//初始生命值恢复
                long manaRegen = gifts.get(entry.getKey()).getEquipManaRegen() != null ? gifts.get(entry.getKey()).getEquipManaRegen() : 0;//初始法力值恢复
                long armor = gifts.get(entry.getKey()).getEquipArmor() != null ? gifts.get(entry.getKey()).getEquipArmor() : 0;//gmArmor
                long magicResist = gifts.get(entry.getKey()).getEquipMagicResist() != null ? gifts.get(entry.getKey()).getEquipMagicResist() : 0;//初始魔抗
                long attackDamage = gifts.get(entry.getKey()).getEquipAttackDamage() != null ? gifts.get(entry.getKey()).getEquipAttackDamage() : 0;//初始攻击力
                long attackSpell = gifts.get(entry.getKey()).getEquipAttackSpell() != null ? gifts.get(entry.getKey()).getEquipAttackSpell() : 0;//初始法攻
                // 生成NFT唯一编码
                userEquip.setNftId(Arith.UUID20());
                // 区分召唤或副本产出
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
                double equipPower = (health * 0.1) + (mana * 0.1) + attackDamage + attackSpell + ((armor + magicResist) * 4.5) + healthRegen * 0.1 + manaRegen * 0.3;
                userEquip.setEquipPower((long) equipPower);
                // 计算矿工数
                BigDecimal minter = CalculateTradeUtil.updateMiner(BigDecimal.valueOf(userEquip.getEquipPower()));
                userEquip.setMinter(minter);// 矿工数
                userEquip.setOracle(CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1)));// 神谕值
                // 更新系统中保存的市场总鸡蛋
                sysConfigService.updateValueByKey(Constant.MARKET_EGGS, CalculateTradeUtil.marketEggs.toString());

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
                // 加一层校验 召唤池英雄碎片数量不小于0
                if(fragNum > 0) {
                    for (int fn = 0; fn < fragNum; fn++) {
                        UserEquipmentFragEntity userEquipFrag = new UserEquipmentFragEntity();
                        userEquipFrag.setEquipmentFragId(gifts.get(entry.getKey()).getEquipmentFragId());
                        userEquipFrag.setUserId(user.getUserId());
                        userEquipFrag.setUserEquipFragNum(fragNum);
                        userEquipFrag.setStatus(Constant.enable);
                        // 区分召唤或副本产出
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
            eqMap.put("EQUIP_RARECODE", aProbability.getPrLv());
            List<EquipmentInfoEntity> equips = equipmentInfoDao.selectByMap(eqMap);
            for (EquipmentInfoEntity equip : equips) {
                EquipDrawGiftDtoEntity drawDtoEntity = new EquipDrawGiftDtoEntity();
                BeanUtils.copyProperties(drawDtoEntity,equip);
                drawDtoEntity.setPron(aProbability.getPr() * 0.8);
                drawDtoEntity.setDType(0L);

                if (aProbability.getPrLv() == 1) {
                    // 概率为1级
                    gifts.add(drawDtoEntity);
                } else if (aProbability.getPrLv() == 2) {
                    // 概率为2级
                    gifts.add(drawDtoEntity);
                } else if (aProbability.getPrLv() == 3) {
                    // 概率为3级
                    gifts.add(drawDtoEntity);
                } else if (aProbability.getPrLv() == 4) {
                    // 概率为4级
                    gifts.add(drawDtoEntity);
                } else if (aProbability.getPrLv() == 5) {
                    // 概率为5级
                    gifts.add(drawDtoEntity);
                }
            }

            // 获取奖池全部装备卷轴碎片，目前装备卷轴碎片仅包含绿色，紫色，橙色
            List<EquipmentFragEntity> equipFrags = equinpmentFragDao.getEquipFragInfo();
            for (EquipmentFragEntity equipFrag : equipFrags) {
                EquipDrawGiftDtoEntity entity = new EquipDrawGiftDtoEntity();
                entity.setEquipmentFragId(equipFrag.getEquipmentFragId());
                entity.setPron(aProbability.getPr());
                entity.setEquipName(equipFrag.getEquipName());
                entity.setEquipIconUrl(equipFrag.getEquipFragIconUrl());
                String eqRC = equipFrag.getEquipRarecode();
                entity.setEquipRarecode(eqRC);
                if (aProbability.getPrLv() == 1) {
                    // 概率为1级
                    // 如果装备卷轴碎片稀有度为绿色将封装1-3级的概率
                    if (eqRC.equals(Constant.RareCode._GREEN.getValue())) {
                        entity.setEquipmentFragNum(1L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                } else if (aProbability.getPrLv() == 2) {
                    // 概率为2级
                    // 如果装备卷轴碎片稀有度为绿色将封装1-3级的概率
                    if (eqRC.equals(Constant.RareCode._GREEN.getValue())) {
                        entity.setEquipmentFragNum(2L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                } else if (aProbability.getPrLv() == 3) {
                    // 概率为3级
                    // 如果装备卷轴碎片稀有度为绿色将封装1-3级的概率
                    if (eqRC.equals(Constant.RareCode._GREEN.getValue())) {
                        entity.setEquipmentFragNum(3L);
                        gifts.add(AddEqGiftInfo(entity));
                    }

                    // 如果装备卷轴碎片稀有度为紫色将封装3-4级的概率
                    if (eqRC.equals(Constant.RareCode._PURPLE.getValue())) {
                        entity.setEquipmentFragNum(1L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                } else if (aProbability.getPrLv() == 4) {
                    // 概率为4级
                    // 如果装备卷轴碎片稀有度为紫色将封装3-4级的概率
                    if (eqRC.equals(Constant.RareCode._PURPLE.getValue())) {
                        entity.setEquipmentFragNum(2L);
                        gifts.add(AddEqGiftInfo(entity));
                    }
                } else if (aProbability.getPrLv() == 5) {
                    // 概率为5级
                    // 如果装备卷轴碎片稀有度为橙色将封装4级的概率
                    if (eqRC.equals(Constant.RareCode._ORANGE.getValue())) {
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
                .eq("PR_TYPE", Constant.SummonType.EQUIPMENT.getValue())
                .in("PR_LV", eqRCList)
        );

        // 创建一个储存装备物品+概率的集合
        List<EquipDrawGiftDtoEntity> gifts = EquipmentList(probability);
        DrawForm drawForm = new DrawForm();
        drawForm.setSummonNum(Constant.SummonNum.NUM1.getValue());// 单抽

        // 开始奖品发放
        gifList = EQDrawGiftPub(gifts, user, drawForm, combatRecord);
        return gifList;
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

