package com.gm.modules.user.dao;

import com.gm.modules.user.entity.GmWhitelistPresaleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 预售白名单
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-12-15 18:36:47
 */
@Mapper
public interface GmWhitelistPresaleDao extends BaseMapper<GmWhitelistPresaleEntity> {

    /**
     * 获取预售白名单
     * @param map
     * @return
     */
    List<GmWhitelistPresaleEntity> getWhitelistPresale(Map<String, Object> map);

}
