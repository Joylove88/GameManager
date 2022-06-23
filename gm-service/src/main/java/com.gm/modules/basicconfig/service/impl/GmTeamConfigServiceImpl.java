package com.gm.modules.basicconfig.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
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

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userAddress = (String) params.get("userAddress");
        String status = (String) params.get("status");

        IPage<GmTeamConfigEntity> page = this.page(
                new Query<GmTeamConfigEntity>().getPage(params),
                new QueryWrapper<GmTeamConfigEntity>()
                        .eq(StringUtils.isNotBlank(status), "a.STATUS", status)
                        .like(StringUtils.isNotBlank(userAddress), "b.USER_WALLET_ADDRESS", userAddress)
        );

        return new PageUtils(page);
    }

}
