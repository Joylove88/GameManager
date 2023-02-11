package com.gm.modules.basicconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.HeroInfoDao;
import com.gm.modules.basicconfig.entity.HeroInfoEntity;
import com.gm.modules.basicconfig.rsp.HeroInfoDetailRsp;
import com.gm.modules.basicconfig.service.HeroInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("heroInfoService")
public class HeroInfoServiceImpl extends ServiceImpl<HeroInfoDao, HeroInfoEntity> implements HeroInfoService {
    @Autowired
    private HeroInfoDao heroInfoDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String heroName = (String) params.get("heroName");
        String status = (String) params.get("status");
        IPage<HeroInfoEntity> page = this.page(new Query<HeroInfoEntity>().getPage(params),new QueryWrapper<HeroInfoEntity>()
        .like(StringUtils.isNotBlank(heroName),"HERO_NAME",heroName)
        .eq(StringUtils.isNotBlank(status),"STATUS",status)
        );

        //将英雄类型转换为中文展示
        for(int i = 0; i < page.getRecords().size(); i++){
            String[] heroType = page.getRecords().get(i).getHeroType().split(",");
            String heroT = "";
            for(String ht : heroType){
                if(ht.equals("00")){
                    heroT += "战士,";
                } else if (ht.equals("01")) {
                    heroT += "法师,";
                } else if (ht.equals("02")) {
                    heroT += "刺客,";
                } else if (ht.equals("03")) {
                    heroT += "坦克,";
                } else {
                    heroT += "辅助,";
                }
            }
            page.getRecords().get(i).setHeroType(heroT);
        }
        return new PageUtils(page);
    }

    @Override
    public List<HeroInfoEntity> queryList() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Constant.enable);
        return heroInfoDao.queryList(map);
    }

    @Override
    public List<HeroInfoDetailRsp> getHeroInfoList() {
        return heroInfoDao.getHeroInfoList();
    }

}
