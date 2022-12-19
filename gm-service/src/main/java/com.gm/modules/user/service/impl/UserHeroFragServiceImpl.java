package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.user.dao.UserHeroFragDao;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroFragEntity;
import com.gm.modules.user.rsp.UserHeroFragInfoRsp;
import com.gm.modules.user.service.UserHeroFragService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("userHeroFragService")
public class UserHeroFragServiceImpl extends ServiceImpl<UserHeroFragDao, UserHeroFragEntity> implements UserHeroFragService {
    @Autowired
    private UserHeroFragDao userHeroFragDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String heroName = (String) params.get("heroName");
        String status = (String) params.get("status");
        String fragNum = (String) params.get("fragNum");
        IPage<UserHeroFragEntity> page = this.page(
                new Query<UserHeroFragEntity>().getPage(params),
                new QueryWrapper<UserHeroFragEntity>()
                .eq(StringUtils.isNotBlank(status), "A.STATUS", status)
                .eq(StringUtils.isNotBlank(fragNum), "A.USER_HERO_FRAG_NUM", fragNum)
                .like(StringUtils.isNotBlank(heroName), "C.HERO_NAME", heroName)
                .like(StringUtils.isNotBlank(userName), "D.USER_NAME", userName)
        );

        return new PageUtils(page);
    }

    @Override
    public List<UserHeroFragInfoRsp> getUserAllHeroFrag(Map<String, Object> map) {
        return userHeroFragDao.getUserAllHeroFrag(map);
    }

    @Override
    public void depleteHeroFrag(Map<String, Object> map) {
        Date now = new Date();
        map.put("updateTime", now.getTime());
        userHeroFragDao.depleteHeroFrag(map);
    }

    @Override
    public List<UserHeroFragEntity> queryUserHeroFrag(UserEntity user) {
        return userHeroFragDao.selectList(new QueryWrapper<UserHeroFragEntity>()
                .eq("USER_ID",user.getUserId())
                .eq("STATUS",1)
                .orderByAsc("CREATE_TIME")
        );
    }

}
