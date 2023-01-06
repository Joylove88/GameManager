package com.gm.controller;

import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.modules.market.dto.PutOnMarketReq;
import com.gm.modules.market.service.GmMarketOnlineService;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.service.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 市场接口
 */
@RestController
@RequestMapping("/api")
@Api(tags = "市场接口")
public class ApiMarketController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserHeroService userHeroService;// 用户英雄
    @Autowired
    private UserHeroFragService userHeroFragService;// 用户英雄碎片
    @Autowired
    private UserEquipmentService userEquipmentService;// 用户装备
    @Autowired
    private UserEquipmentFragService userEquipmentFragService;// 用户装备碎片
    @Autowired
    private UserExperienceService userExperienceService;// 用户经验道具
    @Autowired
    private GmMarketOnlineService gmMarketOnlineService;

    /**
     * 获取我的物品
     */
    @Login
    @PostMapping("getItems")
    public R getItems(@LoginUser UserEntity user, @RequestBody Map<String, Object> params) {
        String itemsType = (String) params.getOrDefault("itemsType", "99");
        switch (itemsType) {
            case "0":
                //1.获取我的英雄
                PageUtils page = userHeroService.queryUserHero(user.getUserId(), params);
                return R.ok().put("page", page);
//                List<UserHeroEntity> userHeroEntityList = userHeroService.queryUserHero(user);
//                return R.ok().put("data", userHeroEntityList);
            case "1":
                //2.获取我的英雄碎片
                PageUtils page2 = userHeroFragService.queryUserHeroFrag(user.getUserId(), params);
                return R.ok().put("page", page2);
//                List<UserHeroFragEntity> userHeroFragEntityList = userHeroFragService.queryUserHeroFrag(user);
//                return R.ok().put("data", userHeroFragEntityList);
            case "2":
                //3.获取我的装备
                PageUtils page3 = userEquipmentService.queryUserEquipment(user.getUserId(), params);
                return R.ok().put("page", page3);
//                List<UserEquipmentEntity> userEquipmentEntityList = userEquipmentService.queryUserEquipment(user);
//                return R.ok().put("data", userEquipmentEntityList);
            case "3":
                //4.获取我的装备卷轴
                PageUtils page4 = userEquipmentFragService.queryUserEquipmentFrag(user.getUserId(), params);
                return R.ok().put("page", page4);
//                List<UserEquipmentFragEntity> userEquipmentFragEntityList = userEquipmentFragService.queryUserEquipmentFrag(user);
//                return R.ok().put("data", userEquipmentFragEntityList);
            case "4":
                //5.获取我的经验道具
                PageUtils page5 = userExperienceService.queryUserExperience(user.getUserId(), params);
                return R.ok().put("page", page5);
//                List<UserExperienceEntity> userExperienceEntityList = userExperienceService.queryUserExperience(user);
//                return R.ok().put("data", userExperienceEntityList);
        }
        return R.ok();
    }

    // 1.我的物品接口
    // 2.上架市场接口
    // 3.我的在售物品接口
    // 4.下架市场商品接口
    // 5.市场在售商品接口
    // 6.购买物品接口
    // 7.交易记录接口

    /**
     * 上架市场接口
     */
    @Login
    @PostMapping("putOnMarket")
    public R putOnMarket(@LoginUser UserEntity user, @RequestBody PutOnMarketReq putOnMarketReq) {
        // 1. 查询该物品
        // 2.上架，插入市场一条数据
        switch (putOnMarketReq.getItemsType()) {//物品类型（0：英雄，1：英雄碎片，2：装备，3：装备卷轴，4：经验道具）
            case "0":
                Map<String, Object> userHeroMap = new HashMap<>();
                userHeroMap.put("status", Constant.enable);
                userHeroMap.put("userId", user.getUserId());
                userHeroMap.put("userHeroId", putOnMarketReq.getItemsId());
                UserHeroEntity userHero = userHeroService.getUserHeroById(userHeroMap);
                if (userHero == null) {
                    throw new RRException("user hero not exit");
                }
                // 上架
                gmMarketOnlineService.putOnMarket(user, putOnMarketReq);
                break;
            case "1":
                UserHeroFragEntity userHeroFrag = userHeroFragService.getUserHeroById(user.getUserId(), putOnMarketReq.getItemsId());
                if (userHeroFrag == null) {
                    throw new RRException("user hero frag not exit");
                }
                // 上架
                gmMarketOnlineService.putOnMarket(user, putOnMarketReq);
                break;
            case "2":
                UserEquipmentEntity userEquipment = userEquipmentService.getUserEquipmentById(user.getUserId(), putOnMarketReq.getItemsId());
                if (userEquipment == null) {
                    throw new RRException("user equipment not exit");
                }
                // 上架
                gmMarketOnlineService.putOnMarket(user, putOnMarketReq);
                break;
            case "3":
                UserEquipmentFragEntity userEquipmentFrag = userEquipmentFragService.getUserEquipmentFragById(user.getUserId(), putOnMarketReq.getItemsId());
                if (userEquipmentFrag == null) {
                    throw new RRException("user equipment frag not exit");
                }
                // 上架
                gmMarketOnlineService.putOnMarket(user, putOnMarketReq);
                break;
            case "4":
                UserExperienceEntity userExperience = userExperienceService.getUserExperienceById(user.getUserId(), putOnMarketReq.getItemsId());
                if (userExperience == null) {
                    throw new RRException("user experience not exit");
                }
                // 上架
                gmMarketOnlineService.putOnMarket(user, putOnMarketReq);
                break;
        }
        return R.ok();
    }

    /**
     * 我的在售物品
     */
    @Login
    @PostMapping("getItemsOnMarket")
    public R getItemsOnMarket(@LoginUser UserEntity user) {
        Map<String, Object> map = new HashMap<>();
        //1.获取我的在售英雄
        List<UserHeroEntity> heroList = gmMarketOnlineService.queryUserOnMarketHero(user.getUserId());
        map.put("heroList", heroList);
        //2.获取我的在售英雄碎片
        List<UserHeroFragEntity> heroFragList = gmMarketOnlineService.queryUserOnMarketHeroFrag(user.getUserId());
        map.put("heroFragList", heroFragList);
        //3.获取我的在售装备
        List<UserEquipmentEntity> equipmentList = gmMarketOnlineService.queryUserOnMarketEquipment(user.getUserId());
        map.put("equipmentList", equipmentList);
        //4.获取我的在售装备卷轴
        List<UserEquipmentFragEntity> equipmentFragList = gmMarketOnlineService.queryUserOnMarketEquipmentFrag(user.getUserId());
        map.put("equipmentFragList", equipmentFragList);
        //5.获取我的在售经验道具
        List<UserExperienceEntity> experienceList = gmMarketOnlineService.queryUserOnMarketExperience(user.getUserId());
        map.put("experienceList", experienceList);
        return R.ok().put("data", map);
    }

    /**
     * 市场在售物品接口
     */
//    @Login
    @PostMapping("itemsOnMarket")
    public R itemsOnMarket(@RequestBody Map<String, Object> params) {
        String itemsType = (String) params.getOrDefault("itemsType", "99");
        switch (itemsType) {
            case "0":
                //1.获取市场英雄
                PageUtils page = gmMarketOnlineService.queryUserHero(params);
                return R.ok().put("page", page);
            case "1":
                //2.获取市场英雄碎片
                PageUtils page2 = gmMarketOnlineService.queryUserHeroFrag(params);
                return R.ok().put("page", page2);
            case "2":
                //3.获取市场装备
                PageUtils page3 = gmMarketOnlineService.queryUserEquipment(params);
                return R.ok().put("page", page3);
            case "3":
                //4.获取市场装备卷轴
                PageUtils page4 = gmMarketOnlineService.queryUserEquipmentFrag(params);
                return R.ok().put("page", page4);
            case "4":
                //5.获取市场经验道具
                PageUtils page5 = gmMarketOnlineService.queryUserExperience(params);
                return R.ok().put("page", page5);
        }
        return R.ok();
    }

}
