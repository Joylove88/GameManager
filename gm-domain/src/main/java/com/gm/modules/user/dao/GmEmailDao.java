package com.gm.modules.user.dao;

import com.gm.modules.user.entity.GmEmailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 玩家邮箱
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-10-20 18:37:41
 */
@Mapper
public interface GmEmailDao extends BaseMapper<GmEmailEntity> {
	
}
