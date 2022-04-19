package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.user.dao.UserHeroDao;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.rsp.UserHeroInfoRsp;
import com.gm.modules.user.service.UserHeroService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("userHeroService")
public class UserHeroServiceImpl extends ServiceImpl<UserHeroDao, UserHeroEntity> implements UserHeroService {
    @Autowired
    private UserHeroDao userHeroDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String heroName = (String) params.get("heroName");
        String status = (String) params.get("status");
        String gmStarCode = (String) params.get("gmStarCode");
        String gmLevelName = (String) params.get("gmLevelName");
        IPage<UserHeroEntity> page = this.page(
                new Query<UserHeroEntity>().getPage(params),
                new QueryWrapper<UserHeroEntity>()
                .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                .eq(StringUtils.isNotBlank(gmStarCode), "D.GM_STAR_CODE", gmStarCode)
                .eq(StringUtils.isNotBlank(gmLevelName), "F.GM_LEVEL_NAME", gmLevelName)
                .like(StringUtils.isNotBlank(heroName), "C.HERO_NAME", heroName)
                .like(StringUtils.isNotBlank(userName), "E.USER_NAME", userName)

        );

        return new PageUtils(page);
    }

    @Override
    public List<UserHeroInfoRsp> getUserAllHero(Long userId) {
        return userHeroDao.getUserAllHero(userId);
    }

}
