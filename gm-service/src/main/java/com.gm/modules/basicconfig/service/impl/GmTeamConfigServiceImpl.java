package com.gm.modules.basicconfig.service.impl;

import com.gm.modules.basicconfig.rsp.TeamInfoRsp;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.GmTeamConfigDao;
import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;
import com.gm.modules.basicconfig.service.GmTeamConfigService;


@Service("gmTeamConfigService")
public class GmTeamConfigServiceImpl extends ServiceImpl<GmTeamConfigDao, GmTeamConfigEntity> implements GmTeamConfigService {
    @Autowired
    private GmTeamConfigDao teamConfigDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userAddress = (String) params.get("userAddress");
        String status = (String) params.get("status");

        IPage<GmTeamConfigEntity> page = this.page(
                new Query<GmTeamConfigEntity>().getPage(params),
                new QueryWrapper<GmTeamConfigEntity>()
                        .eq(StringUtils.isNotBlank(status), "a.STATUS", status)
                        .like(StringUtils.isNotBlank(userAddress), "b.ADDRESS", userAddress)
        );

        return new PageUtils(page);
    }

    @Override
    public TeamInfoRsp getTeamInfo(Map<String, Object> params) {
        return teamConfigDao.getTeamInfo(params);
    }

    @Override
    public List<TeamInfoRsp> getTeamInfoList(Map<String, Object> params) {
        return teamConfigDao.getTeamInfoList(params);
    }

    @Override
    public void setTeamHero(GmTeamConfigEntity teamHero) {
        teamConfigDao.setTeamHero(teamHero);
    }

}
