package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;
import com.gm.modules.basicconfig.rsp.TeamInfoRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 队伍配置表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-05-31 15:52:53
 */
public interface GmTeamConfigService extends IService<GmTeamConfigEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取队伍信息
     * @param params
     * @return
     */
    TeamInfoRsp getTeamInfo(Map<String, Object> params);

    /**
     * 获取全部队伍信息
     * @param params
     * @return
     */
    List<TeamInfoRsp> getTeamInfoList(Map<String, Object> params);


    /**
     * 英雄上下阵
     * @param teamHero
     */
    void setTeamHero(GmTeamConfigEntity teamHero);
}

