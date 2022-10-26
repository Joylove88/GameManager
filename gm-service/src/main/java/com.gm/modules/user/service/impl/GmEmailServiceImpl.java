package com.gm.modules.user.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.GmEmailDao;
import com.gm.modules.user.entity.GmEmailEntity;
import com.gm.modules.user.service.GmEmailService;


@Service("gmEmailService")
public class GmEmailServiceImpl extends ServiceImpl<GmEmailDao, GmEmailEntity> implements GmEmailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmEmailEntity> page = this.page(
                new Query<GmEmailEntity>().getPage(params),
                new QueryWrapper<GmEmailEntity>()
        );

        return new PageUtils(page);
    }

}
