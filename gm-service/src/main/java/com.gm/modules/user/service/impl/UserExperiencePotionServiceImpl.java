package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.ExperiencePotionDao;
import com.gm.modules.basicconfig.dao.HeroLevelDao;
import com.gm.modules.basicconfig.entity.ExperiencePotionEntity;
import com.gm.modules.basicconfig.entity.HeroLevelEntity;
import com.gm.modules.user.dao.UserExperiencePotionDao;
import com.gm.modules.user.dao.UserHeroDao;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserExperiencePotionEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.req.UseExpReq;
import com.gm.modules.user.rsp.UserExpInfoRsp;
import com.gm.modules.user.service.UserExperiencePotionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("userExperiencePotionService")
public class UserExperiencePotionServiceImpl extends ServiceImpl<UserExperiencePotionDao, UserExperiencePotionEntity> implements UserExperiencePotionService {
    @Autowired
    private UserHeroDao userHeroDao;
    @Autowired
    private HeroLevelDao heroLevelDao;
    @Autowired
    private ExperiencePotionDao experiencePotionDao;
    @Autowired
    private UserExperiencePotionDao userExperiencePotionDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String exPotionName = (String) params.get("exPotionName");
        String status = (String) params.get("status");
        String exPotionRareCode = (String) params.get("exPotionRareCode");
        IPage<UserExperiencePotionEntity> page = this.page(
                new Query<UserExperiencePotionEntity>().getPage(params),
                new QueryWrapper<UserExperiencePotionEntity>()
                    .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                    .eq(StringUtils.isNotBlank(exPotionRareCode), "B.EX_POTION_RARE_CODE", exPotionRareCode)
                    .like(StringUtils.isNotBlank(exPotionName), "B.EX_POTION_NAME", exPotionName)
                    .like(StringUtils.isNotBlank(userName), "C.USER_NAME", userName)
        );

        return new PageUtils(page);
    }

    @Override
    public List<UserExpInfoRsp> getUserEx(UserExperiencePotionEntity userExperiencePotionEntity) {
        return userExperiencePotionDao.getUserEx(userExperiencePotionEntity);
    }

    @Override
    public boolean userHeroUseEx(UserEntity user, UseExpReq useExpReq) {
        // 玩家对已拥有的英雄使用经验药水

        // 获取玩家英雄ID
        String userHeroId = useExpReq.getGmUserHeroId();
        // 如果获取不到玩家使用的药水稀有度 默认给个1
        String rareCode = "";
        if ( StringUtils.isNotBlank(useExpReq.getExpRare()) ) {
            rareCode = useExpReq.getExpRare();
        } else {
            System.out.println("该物品稀有度获取失败");
        }
        // 第一步通过玩家使用的经验药水稀有度获取药水的经验值
        ExperiencePotionEntity exValueEnitity = experiencePotionDao.selectOne(new QueryWrapper<ExperiencePotionEntity>()
                .eq("STATUS",Constant.enable)
                .eq("EX_POTION_RARE_CODE",rareCode)
        );

        // 数据校验：如果该稀有度药水存在说明正常 则进行下一步
        if ( exValueEnitity != null ){
            // 获取玩家英雄经验值
            UserHeroEntity userHero = userHeroDao.selectById(Long.valueOf(userHeroId));
            if ( userHero != null ){
                Date now = new Date();
                // 玩家使用的药水数量大于0 则进行下一步
                if ( useExpReq.getExpNum() < 0 ){
                    System.out.println("请选择药水数量");
                }
                // 通过玩家使用的药水数量进行循环累加经验值
                long exValue = 0;
                for ( int i = 0; i < useExpReq.getExpNum(); i++ ){
                    exValue += exValueEnitity.getExValue();
                    // 玩家使用药水后减少已拥有的该稀有度药水数量
                    UserExperiencePotionEntity userExPoE = new UserExperiencePotionEntity();
                    userExPoE.setExPotionRareCode(rareCode);
                    userExPoE.setGmUserId(user.getUserId());
                    List<UserExperiencePotionEntity> userExPs = userExperiencePotionDao.getUserNotUseEx(userExPoE);
                    UserExperiencePotionEntity userExPoUp = new UserExperiencePotionEntity();
                    userExPoUp.setGmUserExPotionId(userExPs.get(0).getGmUserExPotionId());
                    userExPoUp.setStatus(Constant.used);//将玩家经验药水状态修改为已使用
                    userExPoUp.setUpdateTime(now);
                    userExPoUp.setUpdateTimeTs(now.getTime());
                    userExperiencePotionDao.updateById(userExPoUp);
                }
                // 将累加后的经验值更新到玩家英雄表里
                UserHeroEntity userHeroEntity = new UserHeroEntity();
                userHeroEntity.setGmUserHeroId(Long.valueOf(userHeroId));
                userHeroEntity.setExperienceObtain(userHero.getExperienceObtain() + exValue);
                userHeroDao.updateById(userHeroEntity);
                // 通过玩家英雄经验值匹配英雄等级信息
                HeroLevelEntity heroLevelEntity = new HeroLevelEntity();
                heroLevelEntity.setGmExperienceTotal(userHeroEntity.getExperienceObtain());
                List<HeroLevelEntity> levels = heroLevelDao.getHeroLevel(heroLevelEntity);
                // 获取到玩家本次使用的经验药水总值后判断该英雄是否满足升级条件
                if ( levels.size() > 0 ){
                    // 如果玩家经验值满足升级条件 则进行升级操作，否则跳过
                    // 如果英雄里的等级编码和新获取到的等级编码不同则更新
                    if ( !userHero.getGmHeroLevelId().equals(levels.get(0).getGmHeroLeveId()) ){
                        // 将新等级更新到玩家英雄表里
                        UserHeroEntity uhe = new UserHeroEntity();
                        uhe.setGmUserHeroId(Long.valueOf(userHeroId));
                        uhe.setGmHeroLevelId(levels.get(0).getGmHeroLeveId());
                        uhe.setUpdateTime(now);
                        uhe.setUpdateTimeTs(now.getTime());
                        userHeroDao.updateById(uhe);
                    }
                }
            }
        }
        return true;
    }

}
