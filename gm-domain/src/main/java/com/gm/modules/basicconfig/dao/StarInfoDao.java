package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.StarInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-25 14:59:05
 */
@Mapper
public interface StarInfoDao extends BaseMapper<StarInfoEntity> {
    /**
     * 获取星级信息
     * @return
     */
	List<StarInfoEntity> getStarInfoPro();
}
