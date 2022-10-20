package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.ExperiencePotionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 经验药水表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:18:58
 */
@Mapper
public interface ExperiencePotionDao extends BaseMapper<ExperiencePotionEntity> {
    /**
     * 获取全部经验道具
     * @return
     */
    List<ExperiencePotionEntity> getExpInfos(Map<String, Object> map);
}
