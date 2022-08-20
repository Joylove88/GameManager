/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.common.utils.R;
import com.gm.modules.basicconfig.entity.*;
import com.gm.modules.basicconfig.rsp.*;
import com.gm.modules.basicconfig.service.*;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.sys.service.SysConfigService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 战斗接口
 *
 * @author axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags="战斗接口")
public class ApiFightController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiFightController.class);
    @Autowired
    private GmTeamConfigService teamConfigService;
    @Autowired
    private GmDungeonConfigService dungeonConfigService;
    @Autowired
    private GmMonsterConfigService monsterConfigService;
    @Autowired
    private UserHeroService userHeroService;
    @Autowired
    private GmCombatRecordService combatRecordService;
    @Autowired
    private FightCoreService fightCoreService;
    @Autowired
    private GmMiningInfoService miningInfoService;
    @Autowired
    private EquipmentInfoService equipmentInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysConfigService sysConfigService;

    @Login
    @PostMapping("attck")
    @ApiOperation("开始战斗")
    public R attck(@LoginUser UserEntity user, @RequestBody FightInfoReq req) throws Exception {

        LOGGER.info("战斗准备阶段：队伍上下阵操作");
        // 队伍上下阵操作
        setTeamHero(user, req);
        LOGGER.info("战斗准备阶段：队伍上下阵操作完成");

        LOGGER.info("战斗开始");
        // 开始战斗
        FightInfoRsp rsp = fightCoreService.initAttack(user, req);
        LOGGER.info("战斗结束");
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

        Map<String, Object> map = new HashMap<>();

        // 获取背包英雄
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("gmUserId", user.getUserId());
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("statePlay", Constant.disabled);// 默认：未上阵
        List<UserHeroInfoRsp> bagHeros = userHeroService.getUserAllHero(userHeroMap);

        map.put("teams", list);
        map.put("bagHeros", bagHeros);

        return R.ok().put("data", map);
    }

    @Login
    @PostMapping("getBattleDetailsCurrent")
    @ApiOperation("获取某个队伍当前时间战斗中的记录")
    public R getBattleDetailsCurrent(@LoginUser UserEntity user, @RequestBody FightInfoReq req) throws Exception {
        // 查询战斗过程详情
        Date now = new Date();
        List battleDetails = new ArrayList();
        FightInfoRsp rsp = new FightInfoRsp();
        Map<String, Object> combatRecordMap = new HashMap<>();
        combatRecordMap.put("userId", user.getUserId());
        combatRecordMap.put("teamId", req.getTeamId());
        List<GmCombatRecordEntity> combatRecords = combatRecordService.getCombatRecordNow(combatRecordMap);
        if (combatRecords.size() > 0) {
            JSONArray jsonArray = JSONArray.parseArray(combatRecords.get(0).getCombatDescription());
            int i = 0;
            while ( i < jsonArray.size() ){
                JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
                battleDetails.add(jsonObject);
                i++;
            }
            rsp.setBattleDetails(battleDetails);
            // 获取倒计时
            Long endSec = (combatRecords.get(0).getEndTimeTs() - now.getTime()) / 1000;
            if (endSec > 0) {
                rsp.setEndSec(endSec);
            }
            rsp.setCountdown("00:00:00");
            rsp.setCombatId(combatRecords.get(0).getId());
            rsp.setTeamId(combatRecords.get(0).getTeamId());
            rsp.setStartTimeTs(combatRecords.get(0).getStartTimeTs());
        }

        return R.ok().put("rsp", rsp);
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
        // 获取全部装备
        EquipmentInfoEntity eq = new EquipmentInfoEntity();
        eq.setStatus(Constant.enable);
        List<EquipmentInfoRsp> equipmentInfoRsps = equipmentInfoService.getEquipmentInfo(eq);
        if( dungeonInfoRsps.size() > 0 ){
            int i = 0;
            while ( i < dungeonInfoRsps.size()){
                // 根据每层副本产出的装备稀有度获取对应的装备
                List<EquipmentInfoRsp> equipmentInfoRsps2 = new ArrayList<>();
                if ( equipmentInfoRsps.size() > 0 ) {
                    for ( EquipmentInfoRsp equipmentInfoRsp : equipmentInfoRsps ) {
                        String[] rareCodes = dungeonInfoRsps.get(i).getRangeEquip().split("-");
                        for ( String rareCode : rareCodes ){
                            if ( rareCode.equals(equipmentInfoRsp.getEquipRarecode()) ) {
                                equipmentInfoRsps2.add(equipmentInfoRsp);
                            }
                        }
                    }
                }
                // 插入获取到的装备
                dungeonInfoRsps.get(i).setEquipmentInfoRsps(equipmentInfoRsps2);

                GmMonsterConfigEntity mc = new GmMonsterConfigEntity();
                mc.setStatus(Constant.enable);
                mc.setDungeonId(dungeonInfoRsps.get(i).getId());
                // 获取该地图的怪物信息
                List<MonsterInfoRsp> monsterInfoRsps = monsterConfigService.getMonsterInfo(mc);
                dungeonInfoRsps.get(i).setMonsterInfoRsps(monsterInfoRsps);

                int j = 0;
                while ( j < teamInfoList.size() ){
                    // 插入战斗中的队伍及英雄集合
                    // 判断该队伍是否在该副本战斗
                    if ( dungeonInfoRsps.get(i).getId().equals(teamInfoList.get(j).getDungeonId()) ) {
                        Date now = new Date();
                        // 获取倒计时
                        Long endSec = (teamInfoList.get(j).getEndTimeTs() - now.getTime()) / 1000;
                        // 如果倒计时大于0 说明队伍战斗中
                        if ( endSec > 0 ) {
                            // 获取战斗信息
                            GmCombatRecordEntity combatRecord = combatRecordService.getOne(new QueryWrapper<GmCombatRecordEntity>()
                                    .eq("TEAM_ID", req.getTeamId())// 玩家队伍ID
                                    .eq("END_TIME_TS", teamInfoList.get(j).getEndTimeTs())// 倒计时
                            );
                            TeamInfoInBattleRsp teamInBattle = new TeamInfoInBattleRsp();
                            teamInBattle.setId(teamInfoList.get(j).getId());
                            if ( combatRecord != null ) {
                                teamInBattle.setCombatId(combatRecord.getId());
                            }
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
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("gmUserId", user.getUserId());
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("statePlay", Constant.disabled);// 默认：未上阵
        List<UserHeroInfoRsp> bagHeros = userHeroService.getUserAllHero(userHeroMap);

        // 获取当前队伍
        teamParams.put("id", req.getTeamId());
        TeamInfoRsp teamInfo = teamConfigService.getTeamInfo(teamParams);
        if ( teamInfo == null ) {
            System.out.println("在副本中获取当前队伍失败");
        }
        int j = 0;
        while ( j < teamInfoList.size() ){
            // 插入队伍配置中的英雄
            if ( teamInfoList.get(j).getId().equals(req.getTeamId()) ) {
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
    public R heroesChangeTeam(@LoginUser UserEntity user, @RequestBody FightInfoReq req) throws Exception {
        setTeamHero(user, req);
        return R.ok();
    }

    /**
     * 校验英雄是否已在其他队伍上阵
     * @param teamInfoRsps
     * @param req
     */
    private void theHeroIsExistedOtherTeam(List<TeamInfoRsp> teamInfoRsps, FightInfoReq req) {
        // 创建校验校验英雄是否已在其他队伍上阵
        Map<String, Object> isExistedMap = new HashMap<>();
        int i = 0;
        while ( i < teamInfoRsps.size() ) {
            // 跳过当前队伍，校验其他队伍
            if ( !teamInfoRsps.get(i).getId().equals(req.getTeamId()) ) {
                if ( teamInfoRsps.get(i).getUserHero1Id() != null && teamInfoRsps.get(i).getUserHero1Id() != 0 ) {
                    isExistedMap.put("hero1Id" + i, teamInfoRsps.get(i).getUserHero1Id());
                }

                if ( teamInfoRsps.get(i).getUserHero2Id() != null && teamInfoRsps.get(i).getUserHero2Id() != 0 ) {
                    isExistedMap.put("hero2Id" + i, teamInfoRsps.get(i).getUserHero2Id());
                }

                if ( teamInfoRsps.get(i).getUserHero3Id() != null && teamInfoRsps.get(i).getUserHero3Id() != 0 ) {
                    isExistedMap.put("hero3Id" + i, teamInfoRsps.get(i).getUserHero3Id());
                }

                if ( teamInfoRsps.get(i).getUserHero4Id() != null && teamInfoRsps.get(i).getUserHero4Id() != 0 ) {
                    isExistedMap.put("hero4Id" + i, teamInfoRsps.get(i).getUserHero4Id());
                }

                if( teamInfoRsps.get(i).getUserHero5Id() != null && teamInfoRsps.get(i).getUserHero5Id() != 0 ) {
                    isExistedMap.put("hero5Id" + i, teamInfoRsps.get(i).getUserHero5Id());
                }
                // 开始校验
                if ( isExistedMap.containsValue(req.getUserHero1Id()) ||
                        isExistedMap.containsValue(req.getUserHero2Id()) ||
                        isExistedMap.containsValue(req.getUserHero3Id()) ||
                        isExistedMap.containsValue(req.getUserHero4Id()) ||
                        isExistedMap.containsValue(req.getUserHero5Id()) ) {
                    throw new RRException("英雄已在其他队伍上阵!");
                }
            }
            i++;
        }
    }

    /**
     * 校验当前队伍是否存英雄重复上阵
     * @param req
     */
    private void theHeroIsRepeat(FightInfoReq req) {
        Set set = new HashSet();
        List list = new ArrayList();
        if ( req.getUserHero1Id() != null && req.getUserHero1Id() != 0 ) {
            set.add(req.getUserHero1Id());
            list.add(req.getUserHero1Id());
        }

        if ( req.getUserHero2Id() != null && req.getUserHero2Id() != 0 ) {
            set.add(req.getUserHero2Id());
            list.add(req.getUserHero2Id());
        }

        if ( req.getUserHero3Id() != null && req.getUserHero3Id() != 0 ) {
            set.add(req.getUserHero3Id());
            list.add(req.getUserHero3Id());
        }

        if ( req.getUserHero4Id() != null && req.getUserHero4Id() != 0 ) {
            set.add(req.getUserHero4Id());
            list.add(req.getUserHero4Id());
        }

        if( req.getUserHero5Id() != null && req.getUserHero5Id() != 0 ) {
            set.add(req.getUserHero5Id());
            list.add(req.getUserHero5Id());
        }


        // 开始校验
        if ( set.size() != list.size() ) {
            throw new RRException("英雄重复上阵!");
        }
    }

    /**
     * 获取英雄战力并校验该英雄是否属于当前玩家
     * @param userHero
     * @return
     */
    private Long getHeroPowerAndVerifyBelongs(UserHeroEntity userHero) {
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("gmUserId", userHero.getGmUserId());
        userHeroMap.put("gmUserHeroId", userHero.getGmUserHeroId());
        UserHeroEntity userHeroEntity = userHeroService.getUserHeroById(userHeroMap);
        // 开始校验
        if ( userHeroEntity == null ) {
            throw new RRException("您不是此英雄的归属者!英雄编码："+userHero.getGmUserHeroId());
        }
        return userHeroEntity.getHeroPower();
    }

    /**
     * 更新玩家英雄上下阵状态
     * @param gmUserHeroId
     * @param type
     */
    private void setUserHeroStatePlay(Long gmUserHeroId, String type) {
        // 实例化玩家英雄类 (用于更新玩家英雄上下阵状态)
        UserHeroEntity userHero = new UserHeroEntity();
        userHero.setGmUserHeroId(gmUserHeroId);
        userHero.setStatePlay(type);// 0下阵，1上阵
        userHeroService.updateById(userHero);
    }

    /**
     * 英雄上下阵操作方法
     * @param user
     * @param req
     * @return
     */
    private void setTeamHero(UserEntity user, FightInfoReq req) {
        // 校验当前队伍是否存英雄重复上阵
        theHeroIsRepeat(req);

        // 获取全部队伍
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getUserId());
        List<TeamInfoRsp> teamInfos = teamConfigService.getTeamInfoList(params);
        if (teamInfos.size() == 0 ) {
            throw new RRException("英雄上下阵时获取队伍信息失败!");
        }

        // 校验英雄是否已在其他队伍上阵
        theHeroIsExistedOtherTeam(teamInfos, req);

        // 获取当前队伍
        TeamInfoRsp teamInfo = new TeamInfoRsp();
        int i =0;
        while (i < teamInfos.size()){
            if (teamInfos.get(i).getId().equals(req.getTeamId())){
                teamInfo = teamInfos.get(i);
            }
            i++;
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
        userHero.setGmUserId(user.getUserId());
        // 获取该英雄战力
        long heroPower = 0;
        // 初始化时重置上一次队伍中英雄的上下阵状态
        if ( teamInfo.getUserHero1Id() != null && !teamInfo.getUserHero1Id().equals(0L) ) {
            setUserHeroStatePlay(teamInfo.getUserHero1Id(), Constant.disabled);
        }
        if ( teamInfo.getUserHero2Id() != null && !teamInfo.getUserHero2Id().equals(0L) ) {
            setUserHeroStatePlay(teamInfo.getUserHero2Id(), Constant.disabled);
        }
        if ( teamInfo.getUserHero3Id() != null && !teamInfo.getUserHero3Id().equals(0L) ) {
            setUserHeroStatePlay(teamInfo.getUserHero3Id(), Constant.disabled);
        }
        if ( teamInfo.getUserHero4Id() != null && !teamInfo.getUserHero4Id().equals(0L)  ) {
            setUserHeroStatePlay(teamInfo.getUserHero4Id(), Constant.disabled);
        }
        if ( teamInfo.getUserHero5Id() != null && !teamInfo.getUserHero5Id().equals(0L)  ) {
            setUserHeroStatePlay(teamInfo.getUserHero5Id(), Constant.disabled);
        }

        // 玩家英雄1
        if (req.getUserHero1Id() != null) {
            team.setUserHero1Id(req.getUserHero1Id());

            // 判断玩家是否有上下阵操作
            if (!req.getUserHero1Id().equals(teamInfo.getUserHero1Id())) {

                num++;

                // 上阵操作
                if (!req.getUserHero1Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero1Id());
                    // 获取玩家英雄战力并校验该英雄归属是否当前玩家
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;

                    // 更新玩家英雄1上阵状态
                    setUserHeroStatePlay(req.getUserHero1Id(), Constant.enable);

                } else { // 下阵操作
                    // 因为英雄下阵和无英雄传递过来的值都是0 需要校验数据库中该队伍是否已配置英雄，如已存在配置英雄说明为下阵操作否则为未操作。
                    if (teamInfo.getUserHero1Id() != null) {
                        team.setUserHero1Id(null);
                    } else {
                        num--;
                    }
                }

            } else { // 无上下阵操作
                if (!req.getUserHero1Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero1Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;
                    // 更新玩家英雄1上阵状态
                    setUserHeroStatePlay(req.getUserHero1Id(), Constant.enable);
                }
            }
        }

        // 玩家英雄2
        if (req.getUserHero2Id() != null) {
            team.setUserHero2Id(req.getUserHero2Id());
            // 判断玩家是否有上下阵操作
            if ( !req.getUserHero2Id().equals(teamInfo.getUserHero2Id()) ) {

                num++;

                // 上阵操作
                if (!req.getUserHero2Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero2Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;

                    // 更新玩家英雄2上阵状态
                    setUserHeroStatePlay(req.getUserHero2Id(), Constant.enable);

                } else { // 下阵操作
                    // 因为英雄下阵和无英雄传递过来的值都是0 需要校验数据库中该队伍是否已配置英雄，如已存在配置英雄说明为下阵操作否则为未操作。
                    if (teamInfo.getUserHero2Id() != null) {
                        team.setUserHero2Id(null);
                    } else {
                        num--;
                    }
                }
            } else { // 无上下阵操作
                if (!req.getUserHero2Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero2Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;
                    // 更新玩家英雄1上阵状态
                    setUserHeroStatePlay(req.getUserHero2Id(), Constant.enable);
                }
            }
        }

        // 玩家英雄3
        if (req.getUserHero3Id() != null) {
            team.setUserHero3Id(req.getUserHero3Id());
            // 判断玩家是否有上下阵操作
            if ( !req.getUserHero3Id().equals(teamInfo.getUserHero3Id()) ) {

                num++;

                // 上阵操作
                if (!req.getUserHero3Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero3Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;

                    // 更新玩家英雄3上阵状态
                    setUserHeroStatePlay(req.getUserHero3Id(), Constant.enable);

                } else { // 下阵操作
                    // 因为英雄下阵和无英雄传递过来的值都是0 需要校验数据库中该队伍是否已配置英雄，如已存在配置英雄说明为下阵操作否则为未操作。
                    if (teamInfo.getUserHero3Id() != null) {
                        team.setUserHero3Id(null);
                    } else {
                        num--;
                    }
                }
            } else { // 无上下阵操作
                if (!req.getUserHero3Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero3Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;
                    // 更新玩家英雄1上阵状态
                    setUserHeroStatePlay(req.getUserHero3Id(), Constant.enable);
                }
            }
        }

        // 玩家英雄4
        if (req.getUserHero4Id() != null) {
            team.setUserHero4Id(req.getUserHero4Id());
            // 判断玩家是否有上下阵操作
            if ( !req.getUserHero4Id().equals(teamInfo.getUserHero4Id()) ) {

                num++;
                // 上阵操作
                if (!req.getUserHero4Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero4Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;

                    // 更新玩家英雄4上阵状态
                    setUserHeroStatePlay(req.getUserHero4Id(), Constant.enable);

                } else { // 下阵操作
                    // 因为英雄下阵和无英雄传递过来的值都是0 需要校验数据库中该队伍是否已配置英雄，如已存在配置英雄说明为下阵操作否则为未操作。
                    if (teamInfo.getUserHero4Id() != null) {
                        team.setUserHero4Id(null);
                    } else {
                        num--;
                    }
                }
            } else { // 无上下阵操作
                if (!req.getUserHero3Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero3Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;
                    // 更新玩家英雄1上阵状态
                    setUserHeroStatePlay(req.getUserHero4Id(), Constant.enable);
                }
            }
        }

        // 玩家英雄5
        if (req.getUserHero5Id() != null) {
            team.setUserHero5Id(req.getUserHero5Id());
            // 判断玩家是否有上下阵操作
            if ( !req.getUserHero5Id().equals(teamInfo.getUserHero4Id()) ) {

                num++;
                // 上阵操作
                if (!req.getUserHero5Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero5Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;

                    // 更新玩家英雄5上阵状态
                    setUserHeroStatePlay(req.getUserHero5Id(), Constant.enable);

                } else { // 下阵操作
                    // 因为英雄下阵和无英雄传递过来的值都是0 需要校验数据库中该队伍是否已配置英雄，如已存在配置英雄说明为下阵操作否则为未操作。
                    if (teamInfo.getUserHero5Id() != null) {
                        team.setUserHero5Id(null);
                    } else {
                        num--;
                    }
                }
            } else { // 无上下阵操作
                if (!req.getUserHero3Id().equals(0L)) {
                    userHero.setGmUserHeroId(req.getUserHero3Id());
                    heroPower = getHeroPowerAndVerifyBelongs(userHero);
                    newPower = newPower + heroPower;
                    // 更新玩家英雄1上阵状态
                    setUserHeroStatePlay(req.getUserHero5Id(), Constant.enable);
                }
            }
        }

        // 说明玩家有上下阵操作
        if (num != 0){
            // 获取本次操作之前的战力值
            oldPower = teamInfo.getTeamPower() == null ? 0L : teamInfo.getTeamPower();
            // 获取本次操作改变的战力值
            changePower = newPower - oldPower;
            team.setId(req.getTeamId());
            // 更新玩家战力，队伍战力，矿工
            fightCoreService.updateCombat(changePower, oldPower, newPower, user, team);

        }
    }

    public static void main(String[] args) {
        String s = "";
        JSONArray jsonArray = JSONArray.parseArray(s);
        List battleDetails = new ArrayList();
//        System.out.println(jsonArray);
        int i = 0;
        while (i<jsonArray.size()){
            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
            System.out.println(jsonObject);
            battleDetails.add(jsonObject);
            i++;
        }
        System.out.println(battleDetails);
    }
}
