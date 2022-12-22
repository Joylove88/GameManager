package com.gm.modules.user.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.GmWhitelistAgentDao;
import com.gm.modules.user.entity.GmWhitelistAgentEntity;
import com.gm.modules.user.service.GmWhitelistAgentService;


@Service("gmWhitelistAgentService")
public class GmWhitelistAgentServiceImpl extends ServiceImpl<GmWhitelistAgentDao, GmWhitelistAgentEntity> implements GmWhitelistAgentService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String address = (String) params.get("address");
        String status = (String) params.get("status");
        IPage<GmWhitelistAgentEntity> page = this.page(
                new Query<GmWhitelistAgentEntity>().getPage(params),
                new QueryWrapper<GmWhitelistAgentEntity>()
                        .eq(StringUtils.isNotBlank(status), "STATUS", status)
                        .like(StringUtils.isNotBlank(address), "ADDRESS", address)
        );

        return new PageUtils(page);
    }

}
