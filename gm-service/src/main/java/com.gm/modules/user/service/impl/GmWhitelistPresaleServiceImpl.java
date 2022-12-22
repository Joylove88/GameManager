package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.DateUtils;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.user.dao.GmWhitelistPresaleDao;
import com.gm.modules.user.entity.GmWhitelistPresaleEntity;
import com.gm.modules.user.service.GmWhitelistPresaleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("gmWhitelistPresaleService")
public class GmWhitelistPresaleServiceImpl extends ServiceImpl<GmWhitelistPresaleDao, GmWhitelistPresaleEntity> implements GmWhitelistPresaleService {
    @Autowired
    private GmWhitelistPresaleDao whitelistPresaleDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String address = (String) params.get("address");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        String discountRate = (String) params.get("discountRate");
        String quantityAvailable = (String) params.get("quantityAvailable");
        String status = (String) params.get("status");
        IPage<GmWhitelistPresaleEntity> page = this.page(
                new Query<GmWhitelistPresaleEntity>().getPage(params),
                new QueryWrapper<GmWhitelistPresaleEntity>()
                        .eq(StringUtils.isNotBlank(status), "STATUS", status)
                        .like(StringUtils.isNotBlank(address), "ADDRESS", address)
                        .gt(StringUtils.isNotBlank(startDate), "START_TIME", startDate)
                        .lt(StringUtils.isNotBlank(endDate), "END_TIME", endDate)
                        .eq(StringUtils.isNotBlank(discountRate), "DISCOUNT_RATE", discountRate)
                        .eq(StringUtils.isNotBlank(quantityAvailable), "QUANTITY_AVAILABLE", quantityAvailable)
        );

        return new PageUtils(page);
    }

    @Override
    public List<GmWhitelistPresaleEntity> getWhitelistPresale(Map<String, Object> map) {
        Date now = new Date();
        map.put("status", Constant.enable);
        map.put("startTime", DateUtils.getCurrentData(now, DateUtils.DATE_TIME_PATTERN));
        map.put("endTime", DateUtils.getCurrentData(now, DateUtils.DATE_TIME_PATTERN));
        return whitelistPresaleDao.getWhitelistPresale(map);
    }

}
