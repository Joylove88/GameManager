package com.gm.modules.basicconfig.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.entity.ExperienceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 经验道具表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-20 19:18:58
 */
@Mapper
public interface ExperienceDao extends BaseMapper<ExperienceEntity> {
    /**
     * 获取全部经验道具
     * @return
     */
    List<ExperienceEntity> getExpInfos(Map<String, Object> map);
}
