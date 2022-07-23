/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.exception.RRException;
import com.gm.common.utils.CalculateTradeUtil;
import com.gm.common.utils.Constant;
import com.gm.common.utils.R;
import com.gm.modules.basicconfig.entity.GmDungeonConfigEntity;
import com.gm.modules.basicconfig.entity.GmMonsterConfigEntity;
import com.gm.modules.basicconfig.entity.GmTeamConfigEntity;
import com.gm.modules.basicconfig.rsp.DungeonInfoRsp;
import com.gm.modules.basicconfig.rsp.MonsterInfoRsp;
import com.gm.modules.basicconfig.rsp.TeamInfoInBattleRsp;
import com.gm.modules.basicconfig.rsp.TeamInfoRsp;
import com.gm.modules.basicconfig.service.GmDungeonConfigService;
import com.gm.modules.basicconfig.service.GmMonsterConfigService;
import com.gm.modules.basicconfig.service.GmTeamConfigService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.entity.GmMiningInfoEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserHeroEntity;
import com.gm.modules.user.req.FightInfoReq;
import com.gm.modules.user.rsp.FightClaimRsp;
import com.gm.modules.user.rsp.FightInfoRsp;
import com.gm.modules.user.rsp.UserHeroInfoRsp;
import com.gm.modules.user.service.GmMiningInfoService;
import com.gm.modules.user.service.UserHeroService;
import com.gm.modules.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 战斗接口
 *
 * @author axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags="战斗接口")
public class ApiFightController {
    @Autowired
    private GmTeamConfigService teamConfigService;
    @Autowired
    private GmDungeonConfigService dungeonConfigService;
    @Autowired
    private GmMonsterConfigService monsterConfigService;
    @Autowired
    private UserHeroService userHeroService;
    @Autowired
    private FightCoreService fightCoreService;
    @Autowired
    private GmMiningInfoService miningInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysConfigService sysConfigService;

    @Login
    @PostMapping("attck")
    @ApiOperation("开始战斗")
    public R attck(@LoginUser UserEntity user, @RequestBody FightInfoReq req) throws Exception {
        FightInfoRsp rsp = fightCoreService.attck(user, req);
        return R.ok().put("rsp", rsp);
    }

    @Login
    @PostMapping("claim")
    @ApiOperation("点击战斗结果领取奖励")
    public R claim(@LoginUser UserEntity user, @RequestBody FightInfoReq req) throws Exception {
        FightClaimRsp fightClaimRsp = fightCoreService.claim(user,req.getCombatId());
        return R.ok().put("claims", fightClaimRsp);
    }

    @Login
    @PostMapping("getUserTeamInfo")
    @ApiOperation("获取玩家队伍信息")
    public R getUserTeamInfo(@LoginUser UserEntity user) throws Exception {
        Map<String, Object> teamParams = new HashMap<>();
        teamParams.put("userId", user.getUserId());
        List<TeamInfoRsp> list = teamConfigService.getTeamInfoList(teamParams);
        // 如果为新用户 则自动创建3条队伍
        if (list.size() == 0){
            int i = 0;
            while (i < 3){
                int num = (i+1);
                GmTeamConfigEntity teamConfig = new GmTeamConfigEntity();
                teamConfig.setTeamPower(Constant.ZERO);
                teamConfig.setTeamName("TEAM" + num);
                teamConfig.setUserId(user.getUserId());
                teamConfig.setStatus(Constant.ZERO_);// 默认未战斗
                teamConfig.setTeamSolt(num);
                teamConfigService.save(teamConfig);
                TeamInfoRsp rsp = new TeamInfoRsp();
                BeanUtils.copyProperties(rsp, teamConfig);
                rsp.setDungeonId(null);
                rsp.setUserHero1Id(null);
                rsp.setUserHero2Id(null);
                rsp.setUserHero3Id(null);
                rsp.setUserHero4Id(null);
                rsp.setUserHero5Id(null);
                list.add(rsp);
                i++;
            }
        }

        if(list.size() > 0) {
            // 每个队伍插入英雄集合
            int i = 0;
            while (i < list.size()){
                // 该队伍是否在战斗中
                list.get(i).setEndSec(0L);
                if (list.get(i).getEndTimeTs() != null) {
                    Date now = new Date();
                    // 获取倒计时
                    Long endSec = (list.get(i).getEndTimeTs() - now.getTime()) / 1000;
                    if (endSec > 0) {
                        list.get(i).setEndSec(endSec);
                    }
                }
                list.get(i).setCountdown("00:00:00");
                list.get(i).setUserHeroInfoRsps(fightCoreService.getTeamHeroInfoList(null, list.get(i)));
                i++;
            }
        }
        return R.ok().put("teams", list);
    }

    @Login
    @PostMapping("getDungeonInfo")
    @ApiOperation("获取副本信息获取当前队伍英雄以及背包中的英雄")
    public R getDungeonInfo(@LoginUser UserEntity user, @RequestBody FightInfoReq req) throws Exception {
        Map<String, Object> map = new HashMap<>();
        // 获取副本
        GmDungeonConfigEntity dc = new GmDungeonConfigEntity();
        dc.setStatus(Constant.enable);
        List<DungeonInfoRsp> dungeonInfoRsps = dungeonConfigService.getDungeonInfo(dc);

        // 获取全部队伍
        Map<String, Object> teamParams = new HashMap<>();
        teamParams.put("id", null);
        teamParams.put("userId", user.getUserId());
//                team.setDungeonId(dungeonInfoRsps.get(i).getId());
//                team.setStatus(Constant.enable);
        List<TeamInfoRsp> teamInfoList = teamConfigService.getTeamInfoList(teamParams);
        if( dungeonInfoRsps.size() > 0 ){
            int i = 0;
            while ( i < dungeonInfoRsps.size()){
                GmMonsterConfigEntity mc = new GmMonsterConfigEntity();
                mc.setStatus(Constant.enable);
                mc.setDungeonId(dungeonInfoRsps.get(i).getId());
                // 获取该地图的怪物信息
                List<MonsterInfoRsp> monsterInfoRsps = monsterConfigService.getMonsterInfo(mc);
                dungeonInfoRsps.get(i).setMonsterInfoRsps(monsterInfoRsps);
                int j = 0;
                while (j < teamInfoList.size()){
                    // 插入战斗中的队伍及英雄集合
                    // 判断该队伍是否在该副本战斗
                    if (dungeonInfoRsps.get(i).getId().equals(teamInfoList.get(j).getDungeonId())) {
                        Date now = new Date();
                        // 获取倒计时
                        Long endSec = (teamInfoList.get(j).getEndTimeTs() - now.getTime()) / 1000;
                        // 如果倒计时大于0 说明队伍战斗中
                        if (endSec > 0) {
                            TeamInfoInBattleRsp teamInBattle = new TeamInfoInBattleRsp();
                            teamInBattle.setEndSec(endSec);
                            teamInBattle.setCountdown("00:00:00");
                            teamInBattle.setStatus(teamInfoList.get(j).getStatus());
                            teamInBattle.setTeamSolt(teamInfoList.get(j).getTeamSolt());
                            teamInBattle.setTeamName(teamInfoList.get(j).getTeamName());
                            teamInBattle.setTeamPower(teamInfoList.get(j).getTeamPower());
                            teamInBattle.setUserHeroInfoRsps(fightCoreService.getTeamHeroInfoList(null, teamInfoList.get(j)));

                            dungeonInfoRsps.get(i).setTeamInfoInBattleRsps(teamInBattle);
                        }
                    }
                    j++;
                }
                i++;
            }

        }

        // 获取背包英雄
        UserHeroEntity userHero = new UserHeroEntity();
        userHero.setGmUserId(user.getUserId());
        userHero.setStatus(Constant.enable);
        userHero.setStatePlay(Constant.disabled);// 默认：未上阵
        List<UserHeroInfoRsp> bagHeros = userHeroService.getUserAllHero(userHero);

        // 获取当前队伍
        teamParams.put("id", req.getTeamId());
        TeamInfoRsp teamInfo = teamConfigService.getTeamInfo(teamParams);
        if (teamInfo == null) {
            System.out.println("在副本中获取当前队伍失败");
        }
        int j = 0;
        while (j < teamInfoList.size()){
            // 插入队伍配置中的英雄
            if (teamInfoList.get(j).getId().equals(req.getTeamId())) {
                // 未战斗
//                if (Constant.disabled.equals(teamInfoList.get(j).getStatus())) {
//                }
                teamInfo.setUserHeroInfoRsps(fightCoreService.getTeamHeroInfoList(null, teamInfoList.get(j)));
            }
            j++;
        }
        map.put("dungeonInfo", dungeonInfoRsps);
        map.put("teamInfo", teamInfo);
        map.put("bagHeros", bagHeros);
        return R.ok().put("data", map);
    }

    @Login
    @PostMapping("heroesChangeTeam")
    @ApiOperation("英雄上阵下阵操作")
    public R getTeamHeroBagHero(@LoginUser UserEntity user, @RequestBody FightInfoReq req) throws Exception {
        String code = setTeamHero(user, req);
        return R.ok().put("code", code);
    }

    // 校验英雄是否已上阵方法
    private void theHeroIsExisted(TeamInfoRsp teamInfo, Long userHeroId) {
        // 创建校验该英雄是否已上阵的集合
        Map<String, Object> isExistedMap = new HashMap<>();
        isExistedMap.put("hero1Id", teamInfo.getUserHero1Id());
        isExistedMap.put("hero2Id", teamInfo.getUserHero2Id());
        isExistedMap.put("hero3Id", teamInfo.getUserHero3Id());
        isExistedMap.put("hero4Id", teamInfo.getUserHero4Id());
        isExistedMap.put("hero5Id", teamInfo.getUserHero5Id());

        // 开始校验
        if (isExistedMap.containsValue(userHeroId)){
            throw new RRException("该英雄已上阵!");
        }
    }

    // 获取英雄战力
    private Long getHeroPower(UserHeroEntity userHero) {
        return userHeroService.getUserHeroById(userHero).getHeroPower();
    }

    // 英雄上下阵操作方法
    private String setTeamHero(UserEntity user, FightInfoReq req) {
        String success = "";
        // 获取当前队伍
        Map<String, Object> params = new HashMap<>();
        params.put("id", req.getTeamId());
        TeamInfoRsp teamInfo = teamConfigService.getTeamInfo(params);
        if (teamInfo == null) {
            throw new RRException("英雄上下阵时获取队伍信息失败!");
        }

        // 旧战力
        long oldPower = 0;
        // 新战力
        long newPower = 0;
        // 变化的战力
        long changePower = 0;

        int num = 0;

        // 实例化队伍信息类
        GmTeamConfigEntity team = new GmTeamConfigEntity();
        // 实例化玩家英雄类
        UserHeroEntity userHero = new UserHeroEntity();
        userHero.setStatus(Constant.enable);
        // 获取该英雄战力
        long heroPower = 0;
        // 不同顺序校验============================================================
        // 玩家英雄1
        if (req.getUserHero1Id() != null) {
            team.setUserHero1Id(req.getUserHero1Id());
            // 判断玩家是否有上下阵操作
            if (!req.getUserHero1Id().equals(teamInfo.getUserHero1Id())) {

                num = 1;

                // 上阵操作
                if (!req.getUserHero1Id().equals(0L)) {
                    // 校验该英雄是否已上阵
                    theHeroIsExisted(teamInfo, req.getUserHero1Id());
                    userHero.setGmUserHeroId(req.getUserHero1Id());
                    heroPower = getHeroPower(userHero);
                    newPower = newPower + heroPower;
                } else { // 下阵操作
                    team.setUserHero1Id(null);
                }
            } else { // 无上下阵操作
                userHero.setGmUserHeroId(req.getUserHero1Id());
                heroPower = getHeroPower(userHero);
                newPower = newPower + heroPower;
            }
        }

        // 玩家英雄2
        if (req.getUserHero2Id() != null) {
            team.setUserHero2Id(req.getUserHero2Id());
            // 判断玩家是否有上下阵操作
            if ( !req.getUserHero2Id().equals(teamInfo.getUserHero2Id()) ) {

                num = 1;

                // 上阵操作
                if (!req.getUserHero2Id().equals(0L)) {
                    // 校验该英雄是否已上阵
                    theHeroIsExisted(teamInfo, req.getUserHero2Id());
                    userHero.setGmUserHeroId(req.getUserHero2Id());
                    heroPower = getHeroPower(userHero);
                    newPower = newPower + heroPower;
                } else { // 下阵操作
                    team.setUserHero2Id(null);
                }
            } else { // 无上下阵操作
                userHero.setGmUserHeroId(req.getUserHero2Id());
                heroPower = getHeroPower(userHero);
                newPower = newPower + heroPower;
            }
        }

        // 玩家英雄3
        if (req.getUserHero3Id() != null) {
            team.setUserHero3Id(req.getUserHero3Id());
            // 判断玩家是否有上下阵操作
            if ( !req.getUserHero3Id().equals(teamInfo.getUserHero3Id()) ) {

                num = 1;


                // 上阵操作
                if (!req.getUserHero3Id().equals(0L)) {
                    // 校验该英雄是否已上阵
                    theHeroIsExisted(teamInfo, req.getUserHero3Id());
                    userHero.setGmUserHeroId(req.getUserHero3Id());
                    heroPower = getHeroPower(userHero);
                    newPower = newPower + heroPower;
                } else { // 下阵操作
                    team.setUserHero3Id(null);
                }
            } else { // 无上下阵操作
                userHero.setGmUserHeroId(req.getUserHero3Id());
                heroPower = getHeroPower(userHero);
                newPower = newPower + heroPower;
            }
        }

        // 玩家英雄4
        if (req.getUserHero4Id() != null) {
            team.setUserHero4Id(req.getUserHero4Id());
            // 判断玩家是否有上下阵操作
            if ( !req.getUserHero4Id().equals(teamInfo.getUserHero4Id()) ) {

                num = 1;
                // 上阵操作
                if (!req.getUserHero4Id().equals(0L)) {
                    // 校验该英雄是否已上阵
                    theHeroIsExisted(teamInfo, req.getUserHero4Id());
                    userHero.setGmUserHeroId(req.getUserHero4Id());
                    heroPower = getHeroPower(userHero);
                    newPower = newPower + heroPower;
                } else { // 下阵操作
                    team.setUserHero4Id(null);
                }
            } else { // 无上下阵操作
                userHero.setGmUserHeroId(req.getUserHero3Id());
                heroPower = getHeroPower(userHero);
                newPower = newPower + heroPower;
            }
        }

        // 玩家英雄5
        if (req.getUserHero5Id() != null) {
            team.setUserHero5Id(req.getUserHero5Id());
            // 判断玩家是否有上下阵操作
            if ( !req.getUserHero5Id().equals(teamInfo.getUserHero4Id()) ) {

                num = 1;
                // 上阵操作
                if (!req.getUserHero5Id().equals(0L)) {
                    // 校验该英雄是否已上阵
                    theHeroIsExisted(teamInfo, req.getUserHero5Id());
                    userHero.setGmUserHeroId(req.getUserHero5Id());
                    heroPower = getHeroPower(userHero);
                    newPower = newPower + heroPower;
                } else { // 下阵操作
                    team.setUserHero5Id(null);
                }
            } else { // 无上下阵操作
                userHero.setGmUserHeroId(req.getUserHero3Id());
                heroPower = getHeroPower(userHero);
                newPower = newPower + heroPower;
            }
        }

        // 说明玩家有上下阵操作
        if (num != 0){
            Date now = new Date();
            // 获取玩家矿工数量
            Long minId = fightCoreService.getMiners(user.getUserId());
            // 获取本次操作改变的战力值
            oldPower = teamInfo.getTeamPower();
            changePower = newPower - oldPower;
            // 初始化经济平衡方法
            fightCoreService.initTradeBalanceParameter();
            // 更新玩家矿工
            CalculateTradeUtil.updateMiner(BigDecimal.valueOf(changePower));
            // 更新系统中保存的市场总鸡蛋
            sysConfigService.updateValueByKey(Constant.MARKET_EGGS, CalculateTradeUtil.marketEggs.toString());
            GmMiningInfoEntity mini = new GmMiningInfoEntity();
            mini.setId(minId);
            mini.setMiners(CalculateTradeUtil.miners.toString());
            mini.setUpdateTime(now);
            mini.setUpdateTimeTs(now.getTime());
            miningInfoService.updateById(mini);

            // 更新队伍战力
            team.setId(req.getTeamId());
            team.setTeamPower(newPower);
            team.setUpdateUser(user.getUserId());
            team.setUpdateTime(now);
            team.setUpdateTimeTs(now.getTime());
            teamConfigService.setTeamHero(team);

            // 更新玩家战力
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(user.getUserId());
            // 获取最新用户战力
            Long totalPower = (user.getTotalPower() - oldPower) + newPower;
            userEntity.setTotalPower(totalPower);
            userEntity.setUpdateTime(now);
            userEntity.setUpdateTimeTs(now.getTime());
            userService.updateById(userEntity);
            success = "success";
        }
        return success;
    }

}
