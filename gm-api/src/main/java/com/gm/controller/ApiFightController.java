/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Arith;
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
import com.gm.modules.user.rsp.UserHeroInfoNotAllRsp;
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

import java.math.BigDecimal;
import java.util.*;

/**
 * 战斗接口
 *
 * @author axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags = "战斗接口")
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
        FightClaimRsp fightClaimRsp = fightCoreService.claim(user, req.getCombatId());
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
        if (list.size() == 0) {
            int i = 0;
            while (i < 3) {
                int num = (i + 1);
                GmTeamConfigEntity teamConfig = new GmTeamConfigEntity();
                teamConfig.setTeamPower(Constant.ZERO);
                teamConfig.setTeamName("TEAM" + num);
                teamConfig.setUserId(user.getUserId());
                teamConfig.setStatus(Constant.ZERO_);// 默认未战斗
                teamConfig.setTeamSolt(num);
                teamConfig.setTeamMinter(BigDecimal.ZERO);
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

        if (list.size() > 0) {
            // 每个队伍插入英雄集合
            int i = 0;
            while (i < list.size()) {
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
        userHeroMap.put("userId", user.getUserId());
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
            while (i < jsonArray.size()) {
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
        if (dungeonInfoRsps.size() > 0) {
            int i = 0;
            while (i < dungeonInfoRsps.size()) {
                // 根据每层副本产出的装备稀有度获取对应的装备
                List<EquipmentInfoRsp> equipmentInfoRsps2 = new ArrayList<>();
                if (equipmentInfoRsps.size() > 0) {
                    for (EquipmentInfoRsp equipmentInfoRsp : equipmentInfoRsps) {
                        String[] rareCodes = dungeonInfoRsps.get(i).getRangeEquip().split("-");
                        for (String rareCode : rareCodes) {
                            if (rareCode.equals(equipmentInfoRsp.getEquipRarecode())) {
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
                while (j < teamInfoList.size()) {
                    // 插入战斗中的队伍及英雄集合
                    // 判断该队伍是否在该副本战斗
                    if (dungeonInfoRsps.get(i).getId().equals(teamInfoList.get(j).getDungeonId())) {
                        Date now = new Date();
                        // 获取倒计时
                        Long endSec = (teamInfoList.get(j).getEndTimeTs() - now.getTime()) / 1000;
                        // 如果倒计时大于0 说明队伍战斗中
                        if (endSec > 0) {
                            // 获取战斗信息
                            GmCombatRecordEntity combatRecord = combatRecordService.getOne(new QueryWrapper<GmCombatRecordEntity>()
                                    .eq("TEAM_ID", req.getTeamId())// 玩家队伍ID
                                    .eq("END_TIME_TS", teamInfoList.get(j).getEndTimeTs())// 倒计时
                            );
                            TeamInfoInBattleRsp teamInBattle = new TeamInfoInBattleRsp();
                            teamInBattle.setId(teamInfoList.get(j).getId());
                            if (combatRecord != null) {
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
        userHeroMap.put("userId", user.getUserId());
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("statePlay", Constant.disabled);// 默认：未上阵
        List<UserHeroInfoRsp> bagHeros = userHeroService.getUserAllHero(userHeroMap);

        // 获取当前队伍
        teamParams.put("id", req.getTeamId());
        TeamInfoRsp teamInfo = teamConfigService.getTeamInfo(teamParams);
        if (teamInfo == null) {
            System.out.println("在副本中获取当前队伍失败");
        }
        int j = 0;
        while (j < teamInfoList.size()) {
            // 插入队伍配置中的英雄
            if (teamInfoList.get(j).getId().equals(req.getTeamId())) {
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
     * @param heroIdMap
     * @param teamId
     */
    private void theHeroIsExistedOtherTeam(List<TeamInfoRsp> teamInfoRsps, Map<String, Object> heroIdMap, Long teamId) {
        // 创建校验校验英雄是否已在其他队伍上阵
        Map<String, Object> isExistedMap = new HashMap<>();
        int i = 0;
        while (i < teamInfoRsps.size()) {
            // 跳过当前队伍，校验其他队伍
            if (!teamInfoRsps.get(i).getId().equals(teamId)) {
                // 封装其他队伍中的英雄ID
                Map<String, Object> teamHeroIdMap = new LinkedHashMap<>();
                teamHeroIdMap.put("userHero0", teamInfoRsps.get(i).getUserHero1Id());
                teamHeroIdMap.put("userHero1", teamInfoRsps.get(i).getUserHero2Id());
                teamHeroIdMap.put("userHero2", teamInfoRsps.get(i).getUserHero3Id());
                teamHeroIdMap.put("userHero3", teamInfoRsps.get(i).getUserHero4Id());
                teamHeroIdMap.put("userHero4", teamInfoRsps.get(i).getUserHero5Id());
                int teamHeroI = 0;
                while (teamHeroI < teamHeroIdMap.size()) {
                    // 获取玩家英雄ID
                    Long userHeroId = Long.valueOf(null == teamHeroIdMap.get("userHero" + teamHeroI) ? Constant.ZERO_ : teamHeroIdMap.get("userHero" + teamHeroI).toString());
                    if (userHeroId != null && !userHeroId.equals(Constant.ZERO)) {
                        isExistedMap.put("userHeroId" + teamHeroI, userHeroId);
                    }
                    teamHeroI++;
                }
            }
            i++;
        }

        int newI = 0;
        while (newI < heroIdMap.size()) {
            // 获取玩家英雄ID
            Long userHeroId = Long.valueOf(null == heroIdMap.get("userHero" + newI) ? Constant.ZERO_ : heroIdMap.get("userHero" + newI).toString());
            // 开始校验
            if (isExistedMap.containsValue(userHeroId)) {
                throw new RRException("英雄已在其他队伍上阵!");
            }
            newI++;
        }
    }

    /**
     * 校验当前队伍是否存英雄重复上阵，校验玩家英雄合法性
     * @param heroIdMap
     * @param userId
     * @return
     */
    private List<UserHeroInfoNotAllRsp> theHeroIsRepeat(Map<String, Object> heroIdMap, Long userId) {
        Set set = new HashSet();
        List list = new ArrayList();
        // 获取该玩家全部英雄
        Map<String, Object> userHeroMap = new HashMap<>();
        userHeroMap.put("status", Constant.enable);
        userHeroMap.put("userId", userId);
        List<UserHeroInfoNotAllRsp> userHeros = userHeroService.getUserAllHeroSimple(userHeroMap);

        int i = 0;
        while (i < heroIdMap.size()) {
            // 获取玩家英雄ID
            Long userHeroId = Long.valueOf(null == heroIdMap.get("userHero" + i) ? Constant.ZERO_ : heroIdMap.get("userHero" + i).toString());
            if (userHeroId != null && !userHeroId.equals(Constant.ZERO)) {
                // 校验玩家英雄合法性
                boolean bl = userHeros.stream().anyMatch(a -> a.getUserHeroId().equals(userHeroId));
                if (!bl) {
                    throw new RRException("您不是此英雄的归属者!英雄编码：" + userHeroId);
                }
                set.add(userHeroId);
                list.add(userHeroId);
            }
            i++;
        }
        // 开始校验
        if (set.size() != list.size()) {
            throw new RRException("英雄重复上阵!");
        }
        return userHeros;
    }

    /**
     * 存储队伍中的英雄ID
     * @param team
     * @param userHeroId
     * @param i
     * @return
     */
    private GmTeamConfigEntity setTeamUserHeroId(GmTeamConfigEntity team, Long userHeroId, int i) {
        if (i == 0) {
            team.setUserHero1Id(userHeroId);
        } else if (i == 1) {
            team.setUserHero2Id(userHeroId);
        } else if (i == 2) {
            team.setUserHero3Id(userHeroId);
        } else if (i == 3) {
            team.setUserHero4Id(userHeroId);
        } else if (i == 4) {
            team.setUserHero5Id(userHeroId);
        }
        return team;
    }

    /**
     * 存储玩家英雄上下阵状态
     * @param userHeroId
     * @param state
     * @return
     */
    private UserHeroEntity setUserHeroState(Long userHeroId, String state) {
        // 实例化玩家英雄类 (用于更新玩家英雄上下阵状态)
        UserHeroEntity userHero = new UserHeroEntity();
        userHero.setUserHeroId(userHeroId);
        userHero.setStatePlay(state);// 0下阵，1上阵
        return userHero;
    }

    /**
     * 英雄上下阵操作方法
     * @param user
     * @param req
     * @return
     */
    private void setTeamHero(UserEntity user, FightInfoReq req) {
        // 获取请求的英雄ID
        Map<String, Object> heroIdMap = new LinkedHashMap<>();
        heroIdMap.put("userHero0", req.getUserHero1Id());
        heroIdMap.put("userHero1", req.getUserHero2Id());
        heroIdMap.put("userHero2", req.getUserHero3Id());
        heroIdMap.put("userHero3", req.getUserHero4Id());
        heroIdMap.put("userHero4", req.getUserHero5Id());

        // 校验当前队伍是否存英雄重复上阵，校验玩家英雄合法性
        List<UserHeroInfoNotAllRsp> userHeroInfoRsps = theHeroIsRepeat(heroIdMap, user.getUserId());

        // 获取当前玩家的全部队伍
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getUserId());
        List<TeamInfoRsp> teamInfos = teamConfigService.getTeamInfoList(params);
        if (teamInfos.size() == 0) {
            throw new RRException("英雄上下阵时获取队伍信息失败!");
        }
        // 校验英雄是否已在其他队伍上阵
        theHeroIsExistedOtherTeam(teamInfos, heroIdMap, req.getTeamId());
        // 获取当前队伍
        TeamInfoRsp teamInfo = new TeamInfoRsp();
        int i = 0;
        while (i < teamInfos.size()) {
            if (teamInfos.get(i).getId().equals(req.getTeamId())) {
                teamInfo = teamInfos.get(i);
            }
            i++;
        }

        // 实例化需要重置状态的玩家英雄集合
        List<UserHeroEntity> userHeros = new ArrayList<>();

        // ======初始化时重置上一次队伍中英雄的上下阵状态START======
        // 获取旧队伍英雄ID
        Map<String, Object> teamHeroIdMap = new LinkedHashMap<>();
        teamHeroIdMap.put("userHero0", teamInfo.getUserHero1Id());
        teamHeroIdMap.put("userHero1", teamInfo.getUserHero2Id());
        teamHeroIdMap.put("userHero2", teamInfo.getUserHero3Id());
        teamHeroIdMap.put("userHero3", teamInfo.getUserHero4Id());
        teamHeroIdMap.put("userHero4", teamInfo.getUserHero5Id());
        int oldI = 0;
        while (oldI < teamHeroIdMap.size()) {
            // 获取玩家英雄ID
            Long userHeroId = Long.valueOf(null == teamHeroIdMap.get("userHero" + oldI) ? Constant.ZERO_ : teamHeroIdMap.get("userHero" + oldI).toString());
            if (userHeroId != null && !userHeroId.equals(Constant.ZERO)) {
                userHeros.add(setUserHeroState(userHeroId, Constant.disabled));
            }
            oldI++;
        }
        // 重置玩家英雄上下阵状态（一次性批量操作）
        if (userHeros.size() > 0) {
            userHeroService.updateBatchById(userHeros);
        }
        // ======初始化时重置上一次队伍中英雄的上下阵状态END======


        // 实例化需要重置状态的玩家英雄集合
        userHeros = new ArrayList<>();
        // 实例化队伍信息类
        GmTeamConfigEntity team = new GmTeamConfigEntity();
        // 旧战力
        long oldPower = 0;
        // 新战力
        long newPower = 0;
        // 改变的战力
        long changePower = 0;
        // 旧的矿工数
        BigDecimal oldMinter = BigDecimal.ZERO;
        // 队伍矿工数
        BigDecimal newMinter = BigDecimal.ZERO;
        // 改变的矿工数
        BigDecimal changeMinter = BigDecimal.ZERO;
        // 队伍中英雄上下阵操作识别 大于0为已操作
        int num = 0;
        int newI = 0;
        while (newI < heroIdMap.size()) {
            // 获取队伍中的英雄ID
            Long teamHeroId = Long.valueOf(null == teamHeroIdMap.get("userHero" + newI) ? Constant.ZERO_ : teamHeroIdMap.get("userHero" + newI).toString());
            // 获取玩家英雄ID
            Long userHeroId = Long.valueOf(null == heroIdMap.get("userHero" + newI) ? Constant.ZERO_ : heroIdMap.get("userHero" + newI).toString());
            // 当请求的英雄ID不为空时进入
            if (userHeroId != null) {
                // 存储队伍中的英雄ID
                setTeamUserHeroId(team, userHeroId, newI);
                // 判断玩家是否有上下阵操作
                if (!userHeroId.equals(teamHeroId)) {
                    num++;// 标记为已操作
                    // 上阵操作
                    if (!userHeroId.equals(Constant.ZERO)) {
                        // 获取英雄战力
                        for (UserHeroInfoNotAllRsp hero : userHeroInfoRsps) {
                            if (hero.getUserHeroId().equals(userHeroId)) {
                                newPower = newPower + hero.getHeroPower();
                                newMinter = Arith.add(newMinter, hero.getMinter());
                            }
                        }
                        // 存储玩家英雄上阵状态
                        userHeros.add(setUserHeroState(userHeroId, Constant.enable));
                    } else { // 下阵操作
                        // 因为英雄下阵和无英雄传递过来的值都是0 需要校验数据库中该队伍是否已配置英雄，如已存在配置英雄说明为下阵操作否则为未操作。
                        if (teamHeroId != null) {
                            // 存储队伍中的英雄ID
                            setTeamUserHeroId(team, null, newI);
                        } else {
                            num--;
                        }
                    }
                } else { // 无上下阵操作
                    if (!userHeroId.equals(0L)) {
                        // 获取英雄战力
                        for (UserHeroInfoNotAllRsp hero : userHeroInfoRsps) {
                            if (hero.getUserHeroId().equals(userHeroId)) {
                                newPower = newPower + hero.getHeroPower();
                                newMinter = Arith.add(newMinter, hero.getMinter());
                            }
                        }
                        // 存储玩家英雄上阵状态
                        userHeros.add(setUserHeroState(userHeroId, Constant.enable));
                    }
                }
            }
            newI++;
        }

        // 更新玩家英雄上阵状态
        if (userHeros.size() > 0) {
            userHeroService.updateBatchById(userHeros);
        }
        // 说明玩家有上下阵操作
        if (num != 0) {
            // 获取本次操作之前的战力值
            oldPower = teamInfo.getTeamPower() == null ? 0L : teamInfo.getTeamPower();
            // 获取本次操作改变的战力值
            changePower = newPower - oldPower;
            // 获取本次操作之前的矿工数
            oldMinter = teamInfo.getTeamMinter() == null ? BigDecimal.ZERO : teamInfo.getTeamMinter();
            // 获取本次操作改变的矿工数
            changeMinter = Arith.subtract(newMinter, oldMinter);
            team.setId(req.getTeamId());
            // 更新玩家战力，队伍战力，矿工
            fightCoreService.updateCombat(changePower, oldPower, newPower, user, team, changeMinter, oldMinter, newMinter);
        }
    }

    public static void main(String[] args) {
        String s = "";
        JSONArray jsonArray = JSONArray.parseArray(s);
        List battleDetails = new ArrayList();
//        System.out.println(jsonArray);
        int i = 0;
        while (i < jsonArray.size()) {
            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
            System.out.println(jsonObject);
            battleDetails.add(jsonObject);
            i++;
        }
        System.out.println(battleDetails);
    }
}
