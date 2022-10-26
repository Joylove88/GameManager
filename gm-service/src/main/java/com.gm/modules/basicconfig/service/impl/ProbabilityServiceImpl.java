package com.gm.modules.basicconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.ProbabilityDao;
import com.gm.modules.basicconfig.entity.ProbabilityEntity;
import com.gm.modules.basicconfig.service.ProbabilityService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;


@Service("probabilityService")
public class ProbabilityServiceImpl extends ServiceImpl<ProbabilityDao, ProbabilityEntity> implements ProbabilityService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String prType = (String) params.get("prType");
        IPage<ProbabilityEntity> page = this.page(
                new Query<ProbabilityEntity>().getPage(params),
                new QueryWrapper<ProbabilityEntity>()
                        .eq(StringUtils.isNotBlank(prType), "A.PR_TYPE", prType)
        );
        return new PageUtils(page);
    }

}
