package com.gm.modules.basicconfig.dao;

import com.gm.modules.basicconfig.entity.GmDungeonConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.basicconfig.rsp.DungeonInfoRsp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 副本配置表
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
@Mapper
public interface GmDungeonConfigDao extends BaseMapper<GmDungeonConfigEntity> {
    /**
     * 获取副本信息
     * @param dungeonConfigEntity
     * @return
     */
	List<DungeonInfoRsp> getDungeonInfo(GmDungeonConfigEntity dungeonConfigEntity);
}
