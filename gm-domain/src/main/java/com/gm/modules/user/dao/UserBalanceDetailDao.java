package com.gm.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.modules.user.entity.UserBalanceDetailEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 用户资金明细：记录余额变动
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-26 19:10:12
 */
@Mapper
public interface UserBalanceDetailDao extends BaseMapper<UserBalanceDetailEntity> {
    /**
     * 获取账变明细
     * @param map
     * @return
     */
	List<UserBalanceDetailEntity> getUserBalanceDetail(Map<String, Object> map);

    String queryAgentRebate(Long userId);
}
