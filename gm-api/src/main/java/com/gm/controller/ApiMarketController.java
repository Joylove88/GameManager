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
    private UserExperiencePotionService userExperiencePotionService;// 用户药水
    @Autowired
    private GmMarketOnlineService gmMarketOnlineService;

    /**
     * 获取我的物品
     */
    @Login
    @PostMapping("getItems")
    public R getItems(@LoginUser UserEntity user, @RequestParam Map<String, Object> params) {
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
                //5.获取我的药水
                PageUtils page5 = userExperiencePotionService.queryUserExperiencePotion(user.getUserId(), params);
                return R.ok().put("page", page5);
//                List<UserExperiencePotionEntity> userExperiencePotionEntityList = userExperiencePotionService.queryUserExperiencePotion(user);
//                return R.ok().put("data", userExperiencePotionEntityList);
        }
        return R.ok();
    }

    // 1.我的物品接口
    // 2.上架市场接口
    // 3.我的在售物品接口
    // 4.下架市场商品接口
    // 5.购买物品接口
    // 6.交易记录接口

    /**
     * 上架市场接口
     */
    @Login
    @PostMapping("putOnMarket")
    public R putOnMarket(@LoginUser UserEntity user, @RequestBody PutOnMarketReq putOnMarketReq) {
        // 1. 查询该物品
        // 2.上架，插入市场一条数据
        switch (putOnMarketReq.getItemsType()) {//物品类型（0：英雄，1：英雄碎片，2：装备，3：装备卷轴，4：药水）
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
                UserExperiencePotionEntity userExperiencePotion = userExperiencePotionService.getUserExperiencePotionById(user.getUserId(), putOnMarketReq.getItemsId());
                if (userExperiencePotion == null) {
                    throw new RRException("user experience potion not exit");
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
//        PageUtils page3 = userEquipmentService.queryUserOnMarketEquipment(user.getUserId());
        //4.获取我的在售装备卷轴
//        PageUtils page4 = userEquipmentFragService.queryUserOnMarketEquipmentFrag(user.getUserId());
        //5.获取我的在售药水
//        PageUtils page5 = userExperiencePotionService.queryUserOnMarketExperiencePotion(user.getUserId());
        return R.ok().put("data", map);
    }

}
