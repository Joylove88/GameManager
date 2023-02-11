package com.gm.modules.drawGift.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.common.utils.*;
import com.gm.modules.basicconfig.dao.ExperienceDao;
import com.gm.modules.basicconfig.dao.HeroFragDao;
import com.gm.modules.basicconfig.dao.HeroInfoDao;
import com.gm.modules.basicconfig.dao.ProbabilityDao;
import com.gm.modules.basicconfig.dto.AttributeSimpleEntity;
import com.gm.modules.basicconfig.dto.DrawDtoTestEntity;
import com.gm.modules.basicconfig.dto.SummonedEventDto;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.basicconfig.rsp.GiftBoxEquipmentRsp;
import com.gm.modules.basicconfig.rsp.GiftBoxExpRsp;
import com.gm.modules.basicconfig.rsp.GiftBoxHeroRsp;
import com.gm.modules.basicconfig.service.EquipmentFragService;
import com.gm.modules.basicconfig.service.EquipmentInfoService;
import com.gm.modules.basicconfig.service.StarInfoService;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.fundsAccounting.service.FundsAccountingService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.sys.service.SysDictService;
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
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("drawGiftService")
public class DrawGiftService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DrawGiftService.class);
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private UserAccountService userAccountService;
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
    private ExperienceDao experienceDao;
    @Autowired
    private UserEquipmentService userEquipmentService;
    @Autowired
    private UserEquipmentFragService userEquipmentFragService;
    @Autowired
    private UserExperienceService userExperienceService;
    @Autowired
    private CombatStatsUtilsService combatStatsUtilsService;
    @Autowired
    private FightCoreService fightCoreService;
    @Autowired
    private GmWhitelistPresaleService whitelistPresaleService;
    @Autowired
    private FundsAccountingService fundsAccountingService;
    /**
     * 当前服务器时间
     */
    private static Date now = new Date();
    /**
     * 皮肤等级0普通1黄金...
     */
    private static int skinType = 0;
    /**
     * 可购买数量
     */
    private static int quantity = 0;
    /**
     * NFT_TOKENID集合
     */
    private List<String> tokenIds = new ArrayList<>();
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
     *
     * @param user
     * @param form
     * @return
     * @throws Exception
     */
    public List startSummon(UserEntity user, SummonReq form, SummonedEventDto summonedEventDto) throws Exception {
        // 初始化时间
        now = new Date();
        // 初始化返回英雄召唤集合
        giftBoxHeroRsps = new ArrayList<>();
        // 初始化返回装备\卷轴召唤集合
        giftBoxEquipmentRsps = new ArrayList<>();
        // 初始化返回经验召唤集合
        giftBoxExpRsps = new ArrayList<>();
        // 初始化NFT_TOKENID集合
        tokenIds = form.getTokenIds();
        // 初始化响应集合
        List list = new ArrayList();
        Map<String, Object> proMap = new HashMap<>();
        proMap.put("STATUS", Constant.enable);
        // 获取全部物品概率
        List<ProbabilityEntity> probabilitys = probabilityDao.selectByMap(proMap);
        // 初始化概率存储器
        List<Double> orignalRates = new ArrayList<>(probabilitys.size());
        // 进入召唤逻辑
        if (Constant.SummonType.HERO.getValue().equals(form.getSummonType())) {
            // 提取英雄概率
            for (ProbabilityEntity pr : probabilitys) {
                if (pr.getPrType().equals(Constant.SummonType.HERO.getValue())) {
                    orignalRates.add(pr.getPr());
                }
            }
            // 英雄召唤
            heroSummonPool(user, form, orignalRates, summonedEventDto);
            list = giftBoxHeroRsps;
        } else if (Constant.SummonType.EQUIPMENT.getValue().equals(form.getSummonType())) {
            // 提取装备概率
            for (ProbabilityEntity pr : probabilitys) {
                if (pr.getPrType().equals(Constant.SummonType.EQUIPMENT.getValue())) {
                    orignalRates.add(pr.getPr());
                }
            }
            // 装备召唤
            equipmentSummonPool(user, form, null, orignalRates, summonedEventDto);
            list = giftBoxEquipmentRsps;
        } else if (Constant.SummonType.EXPERIENCE.getValue().equals(form.getSummonType())) {
            // 提取经验道具概率
            for (ProbabilityEntity pr : probabilitys) {
                if (pr.getPrType().equals(Constant.SummonType.EXPERIENCE.getValue())) {
                    orignalRates.add(pr.getPr());
                }
            }
            // 经验召唤
            expSummonPool(user, form, orignalRates, summonedEventDto);
            list = giftBoxExpRsps;
        }

        // 更新当前玩家召唤(购买)数量 返金币
        if (summonedEventDto.getId() != null) {
            updateWhitelistPreSale(user.getUserId(), summonedEventDto, form.getSummonNum(), form.getCurType());
        }
        return list;
    }

    /**
     * 活动：预售白名单
     *
     * @param address
     * @return
     */
    public SummonedEventDto getSummonedPrice(String address) {
        // 获取召唤返利开关：0：关闭，1开启
        String summonRebateSwitch = sysConfigService.getValue(Constant.SysConfig.SUMMON_REBATE_SWITCH.getValue());
        // 获取原价
        String priceDict = sysDictService.getValueByNameAndKey("GM_DRAW_CONFIG", "GM_DRAW_TYPE");
        JSONObject priceJson = JSONObject.parseObject(priceDict);
        // 活动ID
        Long id = null;
        // 单抽原价格
        Double onePrice = Double.valueOf(priceJson.getString("ONE"));
        // 十连抽原价格
        Double tenPrice = Double.valueOf(priceJson.getString("TEN"));
        // 单抽折扣后的价格
        Double onePriceNew = Double.valueOf(priceJson.getString("ONE"));
        // 十连抽折扣后的价格
        Double tenPriceNew = Double.valueOf(priceJson.getString("TEN"));
        // 数量限制
        Integer quantityAvailable = 0;
        // 已购买数量
        Integer quantityUsed = 0;
        // 折扣率
        Double discountRate = 1d;
        // 返单抽金币（加密货币抽奖时用到）
        Double rebateOne = Constant.ZERO_D;
        // 返十连抽金币（加密货币抽奖时用到）
        Double rebateTen = Constant.ZERO_D;
        // 获取预售白名单信息，如果该玩家不在名单内，或超出活动日期 则无法获取到。
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("type", Constant.ZERO_);// 0:预售
        reqMap.put("address", address);// 地址
        List<GmWhitelistPresaleEntity> whitelistPresales = whitelistPresaleService.getWhitelistPresale(reqMap);
        int i = 0;
        // 插入折扣后的价格
        while (i < whitelistPresales.size()) {
            // 获取可购买数量
            quantity = whitelistPresales.get(i).getQuantityAvailable() - whitelistPresales.get(i).getQuantityUsed();
            Double disRate = whitelistPresales.get(i).getDiscountRate();
            id = whitelistPresales.get(i).getId();
            // 获取原价次数(当可购买数量小于十连抽数量时会用到)
            int originalNum = Constant.SummonNum.NUM10.getValue() - quantity;
            rebateOne = onePrice - onePrice * disRate;// 返单抽金币
            rebateTen = quantity < Constant.SummonNum.NUM10.getValue() ? ((quantity * onePrice) - (quantity * onePrice * disRate)) : tenPrice - tenPrice * disRate;// 返十连抽金币
            onePriceNew = onePrice * disRate;// 单抽价格
            tenPriceNew = quantity < Constant.SummonNum.NUM10.getValue() ? ((quantity * onePrice * disRate) + (originalNum * onePrice)) : tenPrice * disRate;// 十连抽价格
            quantityUsed = whitelistPresales.get(i).getQuantityUsed();// 已购买数量
            quantityAvailable = whitelistPresales.get(i).getQuantityAvailable();// 数量限制
            discountRate = whitelistPresales.get(i).getDiscountRate();// 折扣率
            i++;
        }
        SummonedEventDto rsp = new SummonedEventDto();
        rsp.setId(id);
        rsp.setOnePrice(Arith.formatDouble(onePrice));
        rsp.setTenPrice(Arith.formatDouble(tenPrice));
        rsp.setOnePriceNew(Arith.formatDouble(onePriceNew));
        rsp.setTenPriceNew(Arith.formatDouble(tenPriceNew));
        rsp.setRebateOne(Arith.formatDouble(rebateOne));
        rsp.setRebateTen(Arith.formatDouble(rebateTen));
        rsp.setQuantityUsed(quantityUsed);
        rsp.setDiscountRate(discountRate);
        rsp.setQuantityAvailable(quantityAvailable);
        rsp.setSummonRebateSwitch(summonRebateSwitch);
        return rsp;
    }

    /**
     * 更新当前玩家召唤(购买)数量 并返金币
     *
     * @param summonedEventDto
     * @param num
     */
    private void updateWhitelistPreSale(Long userId, SummonedEventDto summonedEventDto, Integer num, String curType) {
        // 本次召唤次数
        Integer summonNum = num;
        GmWhitelistPresaleEntity whitelistPresale = new GmWhitelistPresaleEntity();
        whitelistPresale.setId(summonedEventDto.getId());
        // 如果已购买数量大于可购买数量 直接更新为与可购买数量同值
        num = summonedEventDto.getQuantityUsed() + num > summonedEventDto.getQuantityAvailable() ? summonedEventDto.getQuantityAvailable() : summonedEventDto.getQuantityUsed() + num;
        whitelistPresale.setQuantityUsed(num);
        whitelistPresaleService.updateById(whitelistPresale);

        // 如玩家支付类型为加密货币并且召唤返利开启的时候则进行返金币
        if (curType.equals(Constant.ONE_) && summonedEventDto.getSummonRebateSwitch().equals(Constant.enable)) {
            // 如十连抽则返十连抽计算后的金币 单抽则返单抽计算后的金币
            BigDecimal addMoney = BigDecimal.valueOf(summonNum == Constant.SummonNum.NUM10.getValue() ? summonedEventDto.getRebateTen() : summonedEventDto.getRebateOne());
            // 更新玩家账户余额
            boolean effect = userAccountService.updateAccountAdd(userId, addMoney, Constant.ZERO_);
            // 用户池入账
            fundsAccountingService.setCashPoolAdd(Constant.CashPool._USER.getValue(), addMoney);
            if (!effect) {
                LOGGER.error("Player account deduction failed!");// 账户金额更新失败
            }
        }
    }

    /**
     * 系统发放成品英雄到玩家背包
     *
     * @param prLv
     * @param giftBoxNum
     * @param user
     * @param summonReq
     * @return
     */
    private void sendHeroForPlayer(Integer prLv, Integer giftBoxNum, UserEntity user, SummonReq summonReq, SummonedEventDto summonedEventDto, Integer summonNum) {
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
        for (int i = 0; i < giftBoxNum; i++) {
            // 随机某个英雄
            Random randomHeroIndex = new Random();
            int heroIndex = randomHeroIndex.nextInt(heroInfos.size());
            // 获取英雄初始属性
            attributeSimple = combatStatsUtilsService.getHeroAttribute(heroInfos.get(heroIndex), 1.0);
            // 英雄星级
            Integer starCode = starInfos.get(0).getStarCode();// 默认1星
            // 星级成长属性加成
            Double starBuff = starInfos.get(0).getStarBuff();// 默认1星属性加成
            // 成长率范围-最小
            Double GRMIN = 1d;
            // 成长率范围-最大
            Double GRMAX = starBuff + Constant.GRI.L.getValue();
            // 成长率
            Double growthRate = 1d;
            switch (prLv) {
                case 2:
                    // 1星英雄 设置成长率范围
                    // 生成随机成长率
                    growthRate = Arith.randomWithinRangeHundred(GRMIN, GRMAX);
                    growthRate = growthRate > starBuff ? starBuff : growthRate;
                    break;
                case 3:
                    // 2星英雄 设置成长率范围
                    starCode = starInfos.get(1).getStarCode();
                    starBuff = starInfos.get(1).getStarBuff();
                    // 最小值取上一个星级的加成
                    GRMIN = starInfos.get(0).getStarBuff();
                    GRMAX = starBuff + Constant.GRI.L.getValue();
                    // 生成随机成长率
                    growthRate = Arith.randomWithinRangeHundred(GRMIN, GRMAX);
                    growthRate = growthRate > starBuff ? starBuff : growthRate;
                    break;
                case 4:
                    // 3星英雄 设置成长率范围
                    starCode = starInfos.get(2).getStarCode();
                    starBuff = starInfos.get(2).getStarBuff();
                    // 最小值取上一个星级的加成
                    GRMIN = starInfos.get(1).getStarBuff();
                    GRMAX = starBuff + Constant.GRI.L.getValue();
                    // 生成随机成长率
                    growthRate = Arith.randomWithinRangeHundred(GRMIN, GRMAX);
                    growthRate = growthRate > starBuff ? starBuff : growthRate;
                    break;
                case 5:
                    // 4星英雄 设置成长率范围
                    starCode = starInfos.get(3).getStarCode();
                    starBuff = starInfos.get(3).getStarBuff();
                    // 最小值取上一个星级的加成
                    GRMIN = starInfos.get(2).getStarBuff();
                    GRMAX = starBuff + Constant.GRI.H.getValue();
                    // 生成随机成长率
                    growthRate = Arith.randomWithinRangeHundred(GRMIN, GRMAX);
                    growthRate = growthRate > starBuff ? starBuff : growthRate;
                    break;
                case 6:
                    // 5星英雄 设置成长率范围
                    starCode = starInfos.get(4).getStarCode();
                    starBuff = starInfos.get(4).getStarBuff();
                    // 最小值取上一个星级的加成
                    GRMIN = starInfos.get(3).getStarBuff();
                    GRMAX = starBuff + Constant.GRI.H.getValue();
                    // 生成随机成长率
                    growthRate = Arith.randomWithinRangeHundred(GRMIN, GRMAX);
                    growthRate = growthRate > starBuff ? starBuff : growthRate;
                    break;
                case 7:
                    // 黄金1星英雄 设置成长率范围
                    // 生成随机成长率
                    growthRate = Arith.randomWithinRangeHundred(GRMIN, GRMAX);
                    growthRate = growthRate > starBuff ? starBuff : growthRate;
                    // 设置皮肤类型为黄金
                    skinType = Constant.SkinType.GOLD.getValue();
                    break;
            }

            // 开始发放英雄至玩家背包
            double scale = heroInfos.get(heroIndex).getScale();
            // 验证活动有效性 验证当前玩家召唤（购买）数量是否已达到限制数量
            if (summonedEventDto.getId() != null && quantity > 0) {
                scale = scale * summonedEventDto.getDiscountRate();
                quantity--;
            }

            UserHeroEntity userHero = new UserHeroEntity();
            // 本次抽奖为免费领取NFT进入时则稀释SCALE，设置英雄为绑定状态，设置物品转入转出状态为转出
            if (summonReq.getIsFree().equals(Constant.enable)) {
                scale = scale * 1 / 68;
                userHero.setStatus(Constant.used);// 默认转出
                userHero.setIsBind(Constant.enable);
            } else {
                userHero.setStatus(Constant.enable);
                userHero.setIsBind(Constant.disabled);
            }
            userHero.setHeroId(heroInfos.get(heroIndex).getHeroId());// 英雄ID
            userHero.setHeroLevelId(100000009L);// 初始等级1
            userHero.setUserId(user.getUserId());// 用户ID
            userHero.setStatePlay(Constant.disabled);// 默认：未上阵
            userHero.setMintStatus(Constant.enable);
            userHero.setMintHash(summonReq.getTransactionHash());// 交易hash
            userHero.setScale(scale);// 矿工比例
            userHero.setCreateTime(now);// 召唤时间
            userHero.setCreateTimeTs(now.getTime());
            LOGGER.info("heroTokenIds: " + tokenIds);
            if (summonReq.getCurType().equals(Constant.CurrencyType._CRYPTO.getValue())) {
                userHero.setNftId(Long.parseLong(tokenIds.get(0)));// NFT_tokenID
                tokenIds.remove(0);
            }

            // 设置成长率
            userHero.setGrowthRate(growthRate);
            // 设置星级
            userHero.setStarCode(starCode);
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
            heroPower = combatStatsUtilsService.getHeroPower(attributeSimple);
            userHero.setHeroPower(heroPower);
            // 初始化GAIA经济系统
            fightCoreService.initTradeBalanceParameter(1);
            // 计算矿工数
            // 获取scale后的战力值
            BigDecimal minter = CalculateTradeUtil.updateMiner(BigDecimal.valueOf(heroPower * scale));
            userHero.setMinter(minter);// 矿工数
            // 更新系统中保存的市场总鸡蛋
            sysConfigService.updateValueByKey(Constant.SysConfig.MARKET_EGGS.getValue(), CalculateTradeUtil.marketEggs.toString());
            // 每次抽奖总战力累加
            CalculateTradeUtil.totalPower = Arith.add(CalculateTradeUtil.totalPower, BigDecimal.valueOf(heroPower));
            // 每次抽奖副本资金池累加
            BigDecimal dungeonFee = Arith.divide(Arith.multiply(summonReq.getRealFee(), Constant.CashPoolScale._DUNGEON.getValue()), BigDecimal.valueOf(summonReq.getSummonNum()));// 获取该订单金额的75%
            CalculateTradeUtil.FundPool = Arith.add(CalculateTradeUtil.FundPool, dungeonFee);
            // 神谕值
            userHero.setOracle(CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1)));
            userHeroAdds.add(userHero);

            // 存储英雄召唤返回集合
            GiftBoxHeroRsp giftBoxHeroRsp = new GiftBoxHeroRsp();
            giftBoxHeroRsp.setHeroName(heroInfos.get(heroIndex).getHeroName());// 英雄名称
            giftBoxHeroRsp.setHeroIconUrl(heroInfos.get(heroIndex).getHeroIconUrl());// 英雄图标
            giftBoxHeroRsp.setHeroFragNum(Constant.Quantity.Q1.getValue());// 英雄碎片数量，如果为星级英雄 数量固定1
            giftBoxHeroRsp.setHeroType(Constant.FragType.WHOLE.getValue());// 英雄类型：0星级英雄，1英雄碎片
            giftBoxHeroRsp.setStarCode(starCode);// 英雄星级
            giftBoxHeroRsp.setBoxNum(giftBoxNum);
            int skinType = prLv == Constant.PrLv.PrLv7.getValue() ? Constant.SkinType.GOLD.getValue() : Constant.SkinType.ORIGINAL.getValue();
            giftBoxHeroRsp.setSkinType(skinType);// 皮肤类型
            giftBoxHeroRsps.add(giftBoxHeroRsp);
        }
        if (userHeroAdds.size() > 0) {
            userHeroService.saveBatch(userHeroAdds);
        }
    }

    /**
     * 系统发放随机1-3数量的英雄碎片到玩家背包
     *
     * @param giftBoxNum       盒子数量
     * @param user             玩家信息
     * @param summonReq        交易HASH
     * @param summonedEventDto 活动信息
     * @return
     */
    private void sendHeroFlagForPlayer(Integer giftBoxNum, UserEntity user, SummonReq summonReq, SummonedEventDto summonedEventDto) {
        // 获取系统全部英雄碎片信息
        List<HeroFragEntity> heroFrags = heroFragDao.getHeroFragPro();
        // 存储要添加的碎片集合
        List<UserHeroFragEntity> userHeroFragAdds = new ArrayList<>();
        for (int i = 0; i < giftBoxNum; i++) {
            // 随机某个英雄的碎片
            Random randomHeroIndex = new Random();
            int heroIndex = randomHeroIndex.nextInt(heroFrags.size());

            // 随机1-3英雄碎片数量
            Random randomFlagNum = new Random();
            int flagNum = randomFlagNum.nextInt(4);
            if (flagNum == 0) {
                flagNum = 1;
            }

            // 开始发放碎片至玩家背包
            double scale = heroFrags.get(heroIndex).getScale();
            // 验证活动有效性 验证当前玩家召唤（购买）数量是否已达到限制数量
            if (summonedEventDto.getId() != null && quantity > 0) {
                scale = scale * summonedEventDto.getDiscountRate();
                quantity--;
            }
            for (int j = 0; j < flagNum; j++) {
                UserHeroFragEntity userHeroFrag = new UserHeroFragEntity();
                userHeroFrag.setHeroFragId(heroFrags.get(heroIndex).getHeroFragId());
                userHeroFrag.setUserHeroFragNum(Constant.Quantity.Q1.getValue());
                userHeroFrag.setUserId(user.getUserId());
                userHeroFrag.setScale(scale);
                userHeroFrag.setStatus(Constant.enable);
                userHeroFrag.setMintStatus(Constant.enable);
                userHeroFrag.setMintHash(summonReq.getTransactionHash());
                userHeroFrag.setCreateTime(now);
                userHeroFrag.setCreateTimeTs(now.getTime());
                Long nftTokenId = summonReq.getCurType().equals(Constant.CurrencyType._CRYPTO.getValue()) ? Long.parseLong(tokenIds.get(0)) : null;
                userHeroFrag.setNftId(nftTokenId);// NFT_tokenID
                userHeroFragAdds.add(userHeroFrag);
            }
            LOGGER.info("heroFlagTokenIds: " + tokenIds);
            if (summonReq.getCurType().equals(Constant.CurrencyType._CRYPTO.getValue())) {
                tokenIds.remove(0);// 删除当前位置的tokenID
            }

            // 每次抽奖副本资金池累加
            BigDecimal dungeonFee = Arith.divide(Arith.multiply(summonReq.getRealFee(), Constant.CashPoolScale._DUNGEON.getValue()), BigDecimal.valueOf(summonReq.getSummonNum()));// 获取该订单金额的75%
            CalculateTradeUtil.FundPool = Arith.add(CalculateTradeUtil.FundPool, dungeonFee);

            // 存储英雄召唤返回集合
            GiftBoxHeroRsp giftBoxHeroRsp = new GiftBoxHeroRsp();
            giftBoxHeroRsp.setHeroName(heroFrags.get(heroIndex).getHeroName());// 英雄名称
            giftBoxHeroRsp.setHeroIconUrl(heroFrags.get(heroIndex).getHeroFragIconUrl());// 英雄图标
            giftBoxHeroRsp.setStarCode(Constant.ZERO_I);// 英雄星级
            giftBoxHeroRsp.setHeroFragNum(flagNum);// 英雄碎片数量，如果为星级英雄 数量固定1
            giftBoxHeroRsp.setHeroType(Constant.FragType.FRAG.getValue());// 英雄类型：0星级英雄，1英雄碎片
            giftBoxHeroRsp.setSkinType(-1);// 皮肤类型

            giftBoxHeroRsps.add(giftBoxHeroRsp);
        }
        // 批量插入
        if (userHeroFragAdds.size() > 0) {
            userHeroFragService.saveBatch(userHeroFragAdds);
        }
    }

    /**
     * 英雄召唤池
     *
     * @param user
     * @param summonReq
     * @return
     * @throws Exception
     */
    private void heroSummonPool(UserEntity user, SummonReq summonReq, List<Double> orignalRates, SummonedEventDto summonedEventDto) throws Exception {
        // 获取副本池金额
        String poolBalance = sysConfigService.getValue(Constant.CashPool._DUNGEON.getValue());
        CalculateTradeUtil.FundPool = new BigDecimal(poolBalance);
        LOGGER.info("获取副本池金额: " + CalculateTradeUtil.FundPool);
        // 如果本次召唤为免费NFT则固定发放1星英雄
        if (summonReq.getIsFree().equals(Constant.enable)) {
            // 2级概率产出1星英雄
            sendHeroForPlayer(Constant.PrLv.PrLv2.getValue(), Constant.Quantity.Q1.getValue(), user, summonReq, summonedEventDto, summonReq.getSummonNum());
        } else {
            // 抽奖次数
            Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, summonReq.getSummonNum());
            // 奖品发放
            for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
                // 当前概率等级奖品的数量
                int giftBoxNum = entry.getValue();
                LOGGER.info("heroGiftBoxNum: " + giftBoxNum);
                // 使用万能策略模式
                switch (entry.getKey()) {
                    case 0:
                        // 1级概率产出1-3碎片
                        sendHeroFlagForPlayer(giftBoxNum, user, summonReq, summonedEventDto);
                        break;
                    case 1:
                        // 2级概率产出1星英雄
                        sendHeroForPlayer(Constant.PrLv.PrLv2.getValue(), giftBoxNum, user, summonReq, summonedEventDto, summonReq.getSummonNum());
                        break;
                    case 2:
                        // 3级概率产出2星英雄
                        sendHeroForPlayer(Constant.PrLv.PrLv3.getValue(), giftBoxNum, user, summonReq, summonedEventDto, summonReq.getSummonNum());
                        break;
                    case 3:
                        // 4级概率产出3星英雄
                        sendHeroForPlayer(Constant.PrLv.PrLv4.getValue(), giftBoxNum, user, summonReq, summonedEventDto, summonReq.getSummonNum());
                        break;
                    case 4:
                        // 5级概率产出4星英雄
                        sendHeroForPlayer(Constant.PrLv.PrLv5.getValue(), giftBoxNum, user, summonReq, summonedEventDto, summonReq.getSummonNum());
                        break;
                    case 5:
                        // 6级概率产出5星英雄
                        sendHeroForPlayer(Constant.PrLv.PrLv6.getValue(), giftBoxNum, user, summonReq, summonedEventDto, summonReq.getSummonNum());
                        break;
                    case 6:
                        // 7级概率产出黄金1星英雄
                        sendHeroForPlayer(Constant.PrLv.PrLv7.getValue(), giftBoxNum, user, summonReq, summonedEventDto, summonReq.getSummonNum());
                        break;
                    default:
                        // 1级概率产出1-3碎片
                        sendHeroFlagForPlayer(giftBoxNum, user, summonReq, summonedEventDto);
                        break;
                }
                //            LOGGER.info(gifts.get(entry.getKey()).getGmHeroStarId() + ", count=" + entry.getValue() + ", probability="
//                    + entry.getValue());
            }
        }
    }

    /**
     * 系统发放装备到玩家背包
     *
     * @param prLv         概率等级
     * @param giftBoxNum   盒子数量
     * @param summonReq    请求参数
     * @param combatRecord 战斗记录
     */
    private void sendEquipmentForPlayer(Integer prLv, Integer giftBoxNum, SummonReq summonReq, GmCombatRecordEntity combatRecord, UserEntity user, SummonedEventDto summonedEventDto) {
        // 获取指定稀有度的装备
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("STATUS", Constant.enable);
        infoMap.put("EQUIP_RARECODE", prLv + "");
        List<EquipmentInfoEntity> equipmentInfos = equipmentInfoService.getEquipmentInfos(infoMap);
        // 存储发放的装备集合
        List<UserEquipmentEntity> userEquipmentAdds = new ArrayList<>();
        for (int i = 0; i < giftBoxNum; i++) {
            // 随机生成一件装备下标
            Random randomHeroIndex = new Random();
            int equipmentIndex = randomHeroIndex.nextInt(equipmentInfos.size());
            // 开始发放装备至玩家背包
            double scale = equipmentInfos.get(equipmentIndex).getScale();
            UserEquipmentEntity userEquipment = new UserEquipmentEntity();
            userEquipment.setEquipmentId(equipmentInfos.get(equipmentIndex).getEquipId());
            userEquipment.setUserId(user.getUserId());
            userEquipment.setStatus(Constant.enable);

            // 获取随机属性最大百分比
            Double eqAttMin = Constant.EquipStatsRange.MIN.getValue();
            // 获取随机属性最小百分比
            Double eqAttMax = Constant.EquipStatsRange.MAX.getValue();
            // 如果战斗记录为空本次为召唤反之为副本产出
            if (combatRecord == null) {
                LOGGER.info("equipTokenIds: " + tokenIds);
                if (summonReq.getCurType().equals(Constant.CurrencyType._CRYPTO.getValue())) {
                    userEquipment.setNftId(Long.parseLong(tokenIds.get(0)));// NFT_tokenID
                    tokenIds.remove(0);// 删除当前位置的tokenID
                }
                userEquipment.setMintHash(summonReq.getTransactionHash());
                userEquipment.setFromType(Constant.FromType.SUMMON.getValue());
                userEquipment.setMintStatus(Constant.enable);
                // 验证活动有效性 验证当前玩家召唤（购买）数量是否已达到限制数量
                if (summonedEventDto.getId() != null && quantity > 0) {
                    scale = scale * summonedEventDto.getDiscountRate();
                    quantity--;
                }
            } else {
                eqAttMin = Constant.EquipStatsRange.MIN_FREE.getValue();
                eqAttMax = Constant.EquipStatsRange.MAX_FREE.getValue();
                userEquipment.setFromType(Constant.FromType.DUNGEON.getValue());
                userEquipment.setSourceId(combatRecord.getId());
            }
            userEquipment.setScale(scale);// 矿工比例

            // 随机装备属性
            Double rap = Arith.randomWithinRangeHundredEquip(eqAttMin, eqAttMax);
            double health = equipmentInfos.get(equipmentIndex).getHealth() * rap;// 初始生命值
            double mana = equipmentInfos.get(equipmentIndex).getMana() * rap;// 初始法力值
            double healthRegen = equipmentInfos.get(equipmentIndex).getHealthRegen() * rap;// 初始生命值恢复
            double manaRegen = equipmentInfos.get(equipmentIndex).getManaRegen() * rap;// 初始法力值恢复
            double armor = equipmentInfos.get(equipmentIndex).getArmor() * rap;// 初始护甲
            double magicResist = equipmentInfos.get(equipmentIndex).getMagicResist() * rap;// 初始魔抗
            double attackDamage = equipmentInfos.get(equipmentIndex).getAttackDamage() * rap;// 初始攻击力
            double attackSpell = equipmentInfos.get(equipmentIndex).getAttackSpell() * rap;// 初始法攻
            userEquipment.setHealth(health);
            userEquipment.setMana(mana);
            userEquipment.setHealthRegen(healthRegen);
            userEquipment.setManaRegen(manaRegen);
            userEquipment.setArmor(armor);
            userEquipment.setMagicResist(magicResist);
            userEquipment.setAttackDamage(attackDamage);
            userEquipment.setAttackSpell(attackSpell);
            // 更新装备战力
            double equipPower = (health * 0.1) + (mana * 0.1) + attackDamage + attackSpell + ((armor + magicResist) * 4.5) + healthRegen * 0.1 + manaRegen * 0.3;
            userEquipment.setEquipPower(equipPower);
            // 初始化GAIA经济系统
            fightCoreService.initTradeBalanceParameter(1);
            // 计算矿工数
            BigDecimal minter = CalculateTradeUtil.updateMiner(BigDecimal.valueOf(equipPower * scale));
            userEquipment.setMinter(minter);// 矿工数
            // 更新系统中保存的市场总鸡蛋
            sysConfigService.updateValueByKey(Constant.SysConfig.MARKET_EGGS.getValue(), CalculateTradeUtil.marketEggs.toString());
            // 每次抽奖总战力累加
            CalculateTradeUtil.totalPower = Arith.add(CalculateTradeUtil.totalPower, BigDecimal.valueOf(equipPower));
            // 每次抽奖副本资金池累加
            BigDecimal dungeonFee = Arith.divide(Arith.multiply(summonReq.getRealFee(), Constant.CashPoolScale._DUNGEON.getValue()), BigDecimal.valueOf(summonReq.getSummonNum()));// 获取该订单金额的75%
            CalculateTradeUtil.FundPool = Arith.add(CalculateTradeUtil.FundPool, dungeonFee);
            // 神谕值
            userEquipment.setOracle(CalculateTradeUtil.calculateRateOfMinter(BigDecimal.valueOf(1)));

            userEquipment.setCreateTime(now);
            userEquipment.setCreateTimeTs(now.getTime());
            userEquipmentAdds.add(userEquipment);
            // 存储装备卷轴召唤返回集合
            GiftBoxEquipmentRsp giftBoxEquipmentRsp = new GiftBoxEquipmentRsp();
            giftBoxEquipmentRsp.setEquipName(equipmentInfos.get(equipmentIndex).getEquipName());// 名称
            giftBoxEquipmentRsp.setEquipIconUrl(equipmentInfos.get(equipmentIndex).getEquipIconUrl());// 图标
            giftBoxEquipmentRsp.setEquipRareCode(Integer.valueOf(equipmentInfos.get(equipmentIndex).getEquipRarecode()));// 稀有度
            giftBoxEquipmentRsp.setEquipType(Constant.FragType.WHOLE.getValue());// 装备类型：0 装备,1 卷轴
            giftBoxEquipmentRsp.setEquipFragNum(Constant.Quantity.Q1.getValue());// 数量
            giftBoxEquipmentRsps.add(giftBoxEquipmentRsp);
        }
        // 批量插入
        if (userEquipmentAdds.size() > 0) {
            userEquipmentService.saveBatch(userEquipmentAdds);
        }
    }

    /**
     * 系统发放1数量的装备卷轴到玩家背包
     *
     * @param prLv         概率等级
     * @param giftBoxNum   盒子数量
     * @param summonReq    请求参数
     * @param combatRecord 战斗记录
     */
    private void sendEquipmentFlagForPlayer(Integer prLv, Integer giftBoxNum, SummonReq summonReq, GmCombatRecordEntity combatRecord, UserEntity user, SummonedEventDto summonedEventDto) {
        // 获取指定稀有度的装备卷轴
        Map<String, Object> fragMap = new HashMap<>();
        fragMap.put("status", Constant.enable);
        fragMap.put("rareCode", prLv + "");
        List<EquipmentFragEntity> equipmentFrags = equipmentFragService.getEquipmentFragPro(fragMap);
        // 存储发放的装备卷轴集合
        List<UserEquipmentFragEntity> userEquipmentFragAdds = new ArrayList<>();
        for (int i = 0; i < giftBoxNum; i++) {
            // 随机生成卷轴下标
            Random randomHeroIndex = new Random();
            int equipmentFragIndex = randomHeroIndex.nextInt(equipmentFrags.size());
            // 开始发放卷轴至玩家背包
            double scale = equipmentFrags.get(equipmentFragIndex).getScale();
            UserEquipmentFragEntity userEquipmentFrag = new UserEquipmentFragEntity();
            userEquipmentFrag.setEquipmentFragId(equipmentFrags.get(equipmentFragIndex).getEquipmentFragId());
            userEquipmentFrag.setUserId(user.getUserId());
            userEquipmentFrag.setStatus(Constant.enable);

            // 如果战斗记录为空本次为召唤反之为副本产出
            if (combatRecord == null) {
                LOGGER.info("equipFlagTokenIds: " + tokenIds);
                if (summonReq.getCurType().equals(Constant.CurrencyType._CRYPTO.getValue())) {
                    userEquipmentFrag.setNftId(Long.parseLong(tokenIds.get(0)));// NFT_tokenID
                    tokenIds.remove(0);// 删除当前位置的tokenID
                }
                userEquipmentFrag.setMintHash(summonReq.getTransactionHash());
                userEquipmentFrag.setFromType(Constant.FromType.SUMMON.getValue());
                userEquipmentFrag.setMintStatus(Constant.enable);
                // 验证活动有效性 验证当前玩家召唤（购买）数量是否已达到限制数量
                if (summonedEventDto.getId() != null && quantity > 0) {
                    scale = scale * summonedEventDto.getDiscountRate();
                    quantity--;
                }
            } else {
                userEquipmentFrag.setFromType(Constant.FromType.DUNGEON.getValue());
                userEquipmentFrag.setSourceId(combatRecord.getId());
            }
            userEquipmentFrag.setScale(scale);
            userEquipmentFrag.setUserEquipFragNum(Constant.Quantity.Q1.getValue());
            userEquipmentFrag.setCreateTime(now);
            userEquipmentFrag.setCreateTimeTs(now.getTime());
            userEquipmentFragAdds.add(userEquipmentFrag);
            // 每次抽奖副本资金池累加
            BigDecimal dungeonFee = Arith.divide(Arith.multiply(summonReq.getRealFee(), Constant.CashPoolScale._DUNGEON.getValue()), BigDecimal.valueOf(summonReq.getSummonNum()));// 获取该订单金额的75%
            CalculateTradeUtil.FundPool = Arith.add(CalculateTradeUtil.FundPool, dungeonFee);

            // 存储装备卷轴召唤返回集合
            GiftBoxEquipmentRsp giftBoxEquipmentRsp = new GiftBoxEquipmentRsp();
            giftBoxEquipmentRsp.setEquipName(equipmentFrags.get(equipmentFragIndex).getEquipName());// 名称
            giftBoxEquipmentRsp.setEquipIconUrl(equipmentFrags.get(equipmentFragIndex).getEquipFragIconUrl());// 图标
            giftBoxEquipmentRsp.setEquipRareCode(Integer.valueOf(equipmentFrags.get(equipmentFragIndex).getEquipRarecode()));// 稀有度
            giftBoxEquipmentRsp.setEquipType(Constant.FragType.FRAG.getValue());// 装备类型：0 装备,1 卷轴
            giftBoxEquipmentRsp.setEquipFragNum(Constant.Quantity.Q1.getValue());// 数量
            giftBoxEquipmentRsps.add(giftBoxEquipmentRsp);
        }
        // 批量插入
        if (userEquipmentFragAdds.size() > 0) {
            userEquipmentFragService.saveBatch(userEquipmentFragAdds);
        }
    }

    /**
     * 装备\卷轴召唤池
     *
     * @param user
     * @param summonReq
     * @param combatRecord
     * @param orignalRates
     */
    private void equipmentSummonPool(UserEntity user, SummonReq summonReq, GmCombatRecordEntity combatRecord, List<Double> orignalRates, SummonedEventDto summonedEventDto) {
        // 获取副本池金额
        String poolBalance = sysConfigService.getValue(Constant.CashPool._DUNGEON.getValue());
        CalculateTradeUtil.FundPool = new BigDecimal(poolBalance);
        LOGGER.info("获取副本池金额: " + CalculateTradeUtil.FundPool);
        // 抽奖次数
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, summonReq.getSummonNum());
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            // 当前概率等级奖品的数量
            int giftBoxNum = entry.getValue();
            LOGGER.info("equipGiftBoxNum: " + giftBoxNum);
            // 使用万能策略模式
            UniversalStrategyModeTool<Integer> ifFunction = new UniversalStrategyModeTool<>(new HashMap<>());
            ifFunction
                    .add(Constant.PrLv.PrLv1.getValue(), () ->
                            // 1级概率产出白色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv1.getValue(), giftBoxNum, summonReq, combatRecord, user, summonedEventDto)
                    )
                    .add(Constant.PrLv.PrLv2.getValue(), () ->
                            // 2级概率产出绿色卷轴*1
                            sendEquipmentFlagForPlayer(Constant.PrLv.PrLv2.getValue(), giftBoxNum, summonReq, combatRecord, user, summonedEventDto)
                    )
                    .add(Constant.PrLv.PrLv3.getValue(), () ->
                            // 3级概率产出绿色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv2.getValue(), giftBoxNum, summonReq, combatRecord, user, summonedEventDto)
                    )
                    .add(Constant.PrLv.PrLv4.getValue(), () ->
                            // 4级概率产出紫色卷轴*1
                            sendEquipmentFlagForPlayer(Constant.PrLv.PrLv4.getValue(), giftBoxNum, summonReq, combatRecord, user, summonedEventDto)
                    )
                    .add(Constant.PrLv.PrLv5.getValue(), () ->
                            // 5级概率产出蓝色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv3.getValue(), giftBoxNum, summonReq, combatRecord, user, summonedEventDto)
                    )
                    .add(Constant.PrLv.PrLv6.getValue(), () ->
                            // 6级概率产出紫色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv4.getValue(), giftBoxNum, summonReq, combatRecord, user, summonedEventDto)
                    )
                    .add(Constant.PrLv.PrLv7.getValue(), () ->
                            // 7级概率产出橙色卷轴*1
                            sendEquipmentFlagForPlayer(Constant.PrLv.PrLv5.getValue(), giftBoxNum, summonReq, combatRecord, user, summonedEventDto)
                    )
                    .add(Constant.PrLv.PrLv8.getValue(), () ->
                            // 8级概率产出橙色装备
                            sendEquipmentForPlayer(Constant.PrLv.PrLv5.getValue(), giftBoxNum, summonReq, combatRecord, user, summonedEventDto)
                    );
            ifFunction.doIf(entry.getKey() + Constant.Quantity.Q1.getValue());
//            LOGGER.info(gifts.get(entry.getKey()).getEquipId() + ", count=" + entry.getValue() + ", probability="
//                    + entry.getValue());
        }
    }

    /**
     * 副本产出的装备
     *
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
        int rmon = rm.nextInt(Constant.Quantity.Q3.getValue());
        rmon = rmon == 0 ? Constant.Quantity.Q1.getValue() : rmon;
        SummonReq summonReq = new SummonReq();
        summonReq.setSummonNum(rmon);
        // 开始奖品发放
        equipmentSummonPool(user, summonReq, combatRecord, orignalRates, null);
    }

    /**
     * 经验召唤池
     *
     * @param user
     * @param summonReq
     * @param orignalRates
     * @return
     */
    private void expSummonPool(UserEntity user, SummonReq summonReq, List<Double> orignalRates, SummonedEventDto summonedEventDto) {
        Map<Integer, Integer> count = LotteryGiftsUtils.gifPron(orignalRates, summonReq.getSummonNum());
        // 奖品发放
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            // 当前概率等级奖品的数量
            int giftBoxNum = entry.getValue();
            LOGGER.info("expGiftBoxNum: " + giftBoxNum);
            switch (entry.getKey()) {
                case 0:
                    // 1级概率产出随机1-10件
                    sendExpPropForPlayer(Constant.PrLv.PrLv1.getValue(), giftBoxNum, summonReq, user, summonedEventDto);
                    break;
                case 1:
                    // 2级概率产出随机1-8件
                    sendExpPropForPlayer(Constant.PrLv.PrLv2.getValue(), giftBoxNum, summonReq, user, summonedEventDto);
                    break;
                case 2:
                    // 3级概率产出随机1-4件
                    sendExpPropForPlayer(Constant.PrLv.PrLv3.getValue(), giftBoxNum, summonReq, user, summonedEventDto);
                    break;
                case 3:
                    // 4级概率产出1件
                    sendExpPropForPlayer(Constant.PrLv.PrLv4.getValue(), giftBoxNum, summonReq, user, summonedEventDto);
                    break;
            }
//            LOGGER.info(gifts.get(entry.getKey()).getEquipId() + ", count=" + entry.getValue() + ", probability="
//                    + entry.getValue());
        }
    }

    /**
     * 系统发放经验道具至玩家背包
     *
     * @param prLv
     * @param giftBoxNum
     * @param summonReq
     * @param user
     */
    private void sendExpPropForPlayer(Integer prLv, Integer giftBoxNum, SummonReq summonReq, UserEntity user, SummonedEventDto summonedEventDto) {
        // 获取系统全部经验信息
        Map<String, Object> expMap = new HashMap<>();
        expMap.put("status", Constant.enable);
        List<ExperienceEntity> exps = experienceDao.getExpInfos(expMap);
        // 存储要添加的经验的集合
        List<UserExperienceEntity> userExpAdds = new ArrayList<>();
        for (int i = 0; i < giftBoxNum; i++) {
            int expsI = prLv - 1;
            Random random = new Random();
            int expNum;
            switch (prLv) {
                case 1:
                    expNum = random.nextInt(Constant.Quantity.Q11.getValue());
                    break;
                case 2:
                    expNum = random.nextInt(Constant.Quantity.Q9.getValue());
                    break;
                case 3:
                    expNum = random.nextInt(Constant.Quantity.Q5.getValue());
                    break;
                case 4:
                    expNum = random.nextInt(Constant.Quantity.Q2.getValue());
                    break;
                default:
                    expNum = Constant.Quantity.Q1.getValue();
                    break;
            }
            // 当前稀有度的经验道具数量
            expNum = expNum == 0 ? Constant.Quantity.Q1.getValue() : expNum;
            // 获取scale
            double scale = exps.get(expsI).getScale();
            // 验证活动有效性 验证当前玩家召唤（购买）数量是否已达到限制数量
            if (summonedEventDto.getId() != null && quantity > 0) {
                scale = scale * summonedEventDto.getDiscountRate();
                quantity--;
            }

            for (int j = 0; j < expNum; j++) {
                // 开始发放经验至玩家背包
                UserExperienceEntity userExp = new UserExperienceEntity();
                userExp.setExpId(exps.get(expsI).getId());
                userExp.setUserId(user.getUserId());
                userExp.setScale(scale);
                userExp.setExpNum(Constant.Quantity.Q1.getValue());
                userExp.setStatus(Constant.enable);
                userExp.setMintStatus(Constant.enable);
                userExp.setMintHash(summonReq.getTransactionHash());
                userExp.setCreateTime(now);
                userExp.setCreateTimeTs(now.getTime());
                Long nftTokenId = summonReq.getCurType().equals(Constant.CurrencyType._CRYPTO.getValue()) ? Long.parseLong(tokenIds.get(0)) : null;
                userExp.setNftId(nftTokenId);// NFT_tokenID
                userExpAdds.add(userExp);
            }
            LOGGER.info("expTokenIds: " + tokenIds);
            if (summonReq.getCurType().equals(Constant.CurrencyType._CRYPTO.getValue())) {
                tokenIds.remove(0);// 删除当前位置的tokenID
            }

            // 存储经验召唤返回集合
            GiftBoxExpRsp giftBoxExpRsp = new GiftBoxExpRsp();
            giftBoxExpRsp.setExpName(exps.get(expsI).getExpName());// 经验道具名称
            giftBoxExpRsp.setExpNum(expNum);// 经验数量
            giftBoxExpRsp.setExpRare(exps.get(expsI).getRareCode());// 经验道具稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
            giftBoxExpRsp.setExp(exps.get(expsI).getExp());// 经验值
            giftBoxExpRsp.setIconUrl(exps.get(expsI).getIconUrl());// 图标地址
            giftBoxExpRsp.setDescription(exps.get(expsI).getDescription());// 经验描述
            giftBoxExpRsps.add(giftBoxExpRsp);
        }
        // 批量插入
        if (userExpAdds.size() > 0) {
            userExperienceService.saveBatch(userExpAdds);
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
        for (DrawDtoTestEntity gift : gifts) {
            double probability = gift.getGmProbability();
            if (probability < 0) {
                probability = 0;
            }
            orignalRates.add(probability);
        }
        int j = 0;
        while (j < 1) {
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

