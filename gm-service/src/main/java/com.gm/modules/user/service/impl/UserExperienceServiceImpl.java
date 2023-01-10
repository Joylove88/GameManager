package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.basicconfig.dao.ExperienceDao;
import com.gm.modules.basicconfig.dao.HeroInfoDao;
import com.gm.modules.basicconfig.dao.HeroLevelDao;
import com.gm.modules.basicconfig.dto.AttributeSimpleEntity;
import com.gm.modules.basicconfig.entity.HeroLevelEntity;
import com.gm.modules.combatStatsUtils.service.CombatStatsUtilsService;
import com.gm.modules.user.dao.UserExperienceDao;
import com.gm.modules.user.dao.UserHeroDao;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserExperienceEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.req.UseExpPropReq;
import com.gm.modules.user.rsp.UserExpInfoDetailRsp;
import com.gm.modules.user.rsp.UserExpInfoRsp;
import com.gm.modules.user.rsp.UserHeroInfoDetailWithGrowRsp;
import com.gm.modules.user.service.UserExperienceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("userExperienceService")
public class UserExperienceServiceImpl extends ServiceImpl<UserExperienceDao, UserExperienceEntity> implements UserExperienceService {
    @Autowired
    private UserHeroDao userHeroDao;
    @Autowired
    private HeroInfoDao heroInfoDao;
    @Autowired
    private HeroLevelDao heroLevelDao;
    @Autowired
    private ExperienceDao experienceDao;
    @Autowired
    private UserExperienceDao userExperienceDao;
    @Autowired
    private CombatStatsUtilsService combatStatsUtilsService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String expName = (String) params.get("expName");
        String status = (String) params.get("status");
        String rareCode = (String) params.get("rareCode");
        IPage<UserExperienceEntity> page = this.page(
                new Query<UserExperienceEntity>().getPage(params),
                new QueryWrapper<UserExperienceEntity>()
                        .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                        .eq(StringUtils.isNotBlank(rareCode), "B.RARE_CODE", rareCode)
                        .like(StringUtils.isNotBlank(expName), "B.EXP_NAME", expName)
                        .like(StringUtils.isNotBlank(userName), "C.USER_NAME", userName)
        );

        return new PageUtils(page);
    }

    @Override
    public List<UserExpInfoRsp> getUserExp(Map<String, Object> map) {
        return userExperienceDao.getUserExp(map);
    }

    @Override
    public List<UserExperienceEntity> getUnusedExpFromPlayers(Map<String, Object> map) {
        return userExperienceDao.getUnusedExpFromPlayers(map);
    }

    /**
     * 经验提升
     *
     * @param user
     * @param useExpPropReq
     * @return
     */
    @Override
    public boolean userHeroUseExp(UserEntity user, UseExpPropReq useExpPropReq) {
        // 获取玩家英雄ID
        Long userHeroId = null != useExpPropReq.getUserHeroId() ? useExpPropReq.getUserHeroId() : Constant.ZERO;
        if (userHeroId.equals(Constant.ZERO)) {
            throw new RRException(ErrorCode.USER_HERO_ID_ERROR.getDesc());
        }

        // 获取玩家英雄信息
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("userHeroId", userHeroId);
        UserHeroInfoDetailWithGrowRsp userHero = userHeroDao.getUserHeroByIdDetailWithGrowRsp(userHeroMap);
        if (userHero == null) {
            throw new RRException(ErrorCode.USER_HERO_GET_FAIL.getDesc() + "=ID: " + userHeroId);
        }

        // 获取玩家背包的全部经验道具
        Map<String, Object> expMap = new HashMap<>();
        expMap.put("userId", user.getUserId());
        List<UserExpInfoDetailRsp> expList = userExperienceDao.getUserExpDetailForUpgrade(expMap);

        // 本次使用的经验道具累计的经验值
        Long expTotal = Constant.ZERO;
        // SCALE
        Double scale = Constant.ZERO_D;
        int scaleI = 0;
        int i = 0;
        while (i < useExpPropReq.getExpList().size()) {
            // 获取使用数量
            Long useNum = null != useExpPropReq.getExpList().get(i).getUseNum() ? useExpPropReq.getExpList().get(i).getUseNum() : Constant.ZERO;
            if (useNum.equals(Constant.ZERO)) {
                throw new RRException(ErrorCode.EXP_NUM_NOT_NULL.getDesc());
            }
            // 获取经验道具稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
            String expRare = useExpPropReq.getExpList().get(i).getExpRare();
            // 经验道具稀有度
            if (StringUtils.isNotBlank(expRare)) {
                // 参数安全校验 防止注入攻击;
                if (ValidatorUtils.securityVerify(expRare)) {
                    throw new RRException(ErrorCode.EXP_RARE_NOT_NULL.getDesc());
                }
            } else {
                throw new RRException(ErrorCode.EXP_RARE_NOT_NULL.getDesc());
            }

            // 通过请求的经验道具稀有度匹配系统中的经验道具
            for (UserExpInfoDetailRsp expInfo : expList) {
                if (expInfo.getExpRare().equals(expRare)) {
                    scale += expInfo.getScale();
                    scaleI ++;
                    // 先校验玩家背包是否有足够的经验道具
                    if (expInfo.getExpNum() < useNum) {
                        throw new RRException(ErrorCode.EXP_NUM_INSUFFICIENT.getDesc());
                    }
                    int j = 0;
                    while (j < useNum) {
                        // 将匹配到的经验道具对应的经验值累加
                        expTotal += expInfo.getExp();
                        j++;
                    }
                }
            }

            // 玩家使用经验道具后减少已拥有的经验道具数量
            expMap.put("rareCode", expRare);
            expMap.put("useNum", useNum);
            userExperienceDao.useExpProp(expMap);
            i++;
        }
        // 设置当前时间
        Date now = new Date();
        // 走到这里说明没有问题
        expTotal = expTotal + userHero.getExperienceObtain();
        // 通过玩家英雄经验值匹配英雄等级信息
        Map<String, Object> heroLvMap = new HashMap<>();
        heroLvMap.put("heroLv", userHero.getLevelCode());
        heroLvMap.put("experienceTotal", expTotal);
        List<HeroLevelEntity> levels = heroLevelDao.getHeroLevel(heroLvMap);
        // 获取最大等级限制(英雄等级不能超过账号10级)
        Integer maxLv = user.getUserLevelId() + 10;
        // 获取最新等级
        Integer newLv = levels.get(0).getLevelCode();
        // 新等级ID
        Long newLvId = levels.get(0).getHeroLeveId();
        for (HeroLevelEntity heroLv : levels) {
            // 如新等级超过最大等级限制则将新等级设置为最大等级并设置经验溢出
            if (newLv > maxLv && heroLv.getLevelCode().equals(maxLv)) {
                // 设置新等级ID
                newLvId = heroLv.getHeroLeveId();
                // 设置经验溢出
                expTotal = heroLv.getExperienceTotal();
            }
        }

        // 初始化当前英雄属性
        AttributeSimpleEntity attribute;
        // 更新英雄经验值及等级
        UserHeroEntity userHeroUp = new UserHeroEntity();
        // 如新等级高于当前等级则进行成长属性值累加
        if (newLv > userHero.getLevelCode()) {
            int lv = newLv - userHero.getLevelCode();
            // 获取本次升级增加的英雄属性
            attribute = combatStatsUtilsService.getHeroAttributeWithLv(userHero, lv);
            // 获取本次升级增加的战力
            double changePower = combatStatsUtilsService.getHeroPower(attribute);
            // 最新战力
            double newPower = userHero.getHeroPower() + changePower;
            // 增加玩家英雄属性
            userHeroUp.setHealth(userHero.getHealth() + attribute.getHp());// 累加生命值
            userHeroUp.setMana(userHero.getMana() + attribute.getMp());// 累加法力值
            userHeroUp.setHealthRegen(userHero.getHealthRegen() + attribute.getHpRegen());// 累加生命值恢复
            userHeroUp.setManaRegen(userHero.getManaRegen() + attribute.getMpRegen());// 累加法力值恢复
            userHeroUp.setArmor(userHero.getArmor() + attribute.getArmor());// 累加护甲
            userHeroUp.setMagicResist(userHero.getMagicResist() + attribute.getMagicResist());// 累加魔抗
            userHeroUp.setAttackDamage(userHero.getAttackDamage() + attribute.getAttackDamage());// 累加攻击力
            userHeroUp.setAttackSpell(userHero.getAttackSpell() + attribute.getAttackSpell());// 累加法功
            // 获取经验道具scale平均值
            scale = scale / scaleI;
            // 计算新的scale
            scale = user.getScale() * (((userHero.getScale() * userHero.getHeroPower()) + (scale * changePower)) / (userHero.getHeroPower() + changePower));
            // 增加玩家英雄战力
            userHeroUp.setHeroPower(newPower);
            // 更新矿工、神谕值以及队伍战力、矿工，玩家战力、矿工
            UserHeroInfoDetailWithGrowRsp rsp = combatStatsUtilsService.updateCombatPower(user, userHero, null, changePower, scale);
            userHeroUp.setMinter(rsp.getMinter());
            userHeroUp.setOracle(rsp.getOracle());
        }
        userHeroUp.setUserHeroId(userHeroId);
        userHeroUp.setHeroLevelId(newLvId);
        userHeroUp.setExperienceObtain(expTotal);
        userHeroUp.setUpdateTime(now);
        userHeroUp.setUpdateTimeTs(now.getTime());
        userHeroDao.updateById(userHeroUp);
        return true;
    }

    @Override
    public PageUtils queryUserExperience(Long userId, Map<String, Object> params) {
        IPage<UserExperienceEntity> page = this.page(
                new Query<UserExperienceEntity>().getPage(params),
                new QueryWrapper<UserExperienceEntity>()
                        .eq("A.USER_ID", userId)
                        .eq("A.STATUS", 1)

        );
        return new PageUtils(page);
    }

    @Override
    public UserExperienceEntity getUserExperienceById(Long userId, Long id) {
        return userExperienceDao.selectOne(new QueryWrapper<UserExperienceEntity>()
                .eq("status", Constant.enable)
                .eq("user_id", userId)
                .eq("id", id)
        );
    }

}
