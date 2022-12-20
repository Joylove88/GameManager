package com.gm.controller;

import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.modules.market.dto.GetItemsReq;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.service.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 获取我的物品
     */
    @Login
    @PostMapping("getItems")
    public R getItems(@LoginUser UserEntity user, @RequestParam Map<String, Object> params) {
        String itemsType = (String)params.getOrDefault("itemsType","99");
        switch (itemsType) {
            case "0":
                //1.获取我的英雄
                PageUtils page = userHeroService.queryUserHero(user.getUserId(),params);
                return R.ok().put("page", page);
//                List<UserHeroEntity> userHeroEntityList = userHeroService.queryUserHero(user);
//                return R.ok().put("data", userHeroEntityList);
            case "1":
                //2.获取我的英雄碎片
                PageUtils page2 = userHeroFragService.queryUserHeroFrag(user.getUserId(),params);
                return R.ok().put("page", page2);
//                List<UserHeroFragEntity> userHeroFragEntityList = userHeroFragService.queryUserHeroFrag(user);
//                return R.ok().put("data", userHeroFragEntityList);
            case "2":
                //3.获取我的装备
                PageUtils page3 = userEquipmentService.queryUserEquipment(user.getUserId(),params);
                return R.ok().put("page", page3);
//                List<UserEquipmentEntity> userEquipmentEntityList = userEquipmentService.queryUserEquipment(user);
//                return R.ok().put("data", userEquipmentEntityList);
            case "3":
                //4.获取我的装备卷轴
                PageUtils page4 = userEquipmentFragService.queryUserEquipmentFrag(user.getUserId(),params);
                return R.ok().put("page", page4);
//                List<UserEquipmentFragEntity> userEquipmentFragEntityList = userEquipmentFragService.queryUserEquipmentFrag(user);
//                return R.ok().put("data", userEquipmentFragEntityList);
            case "4":
                //5.获取我的药水
                PageUtils page5 = userExperiencePotionService.queryUserExperiencePotion(user.getUserId(),params);
                return R.ok().put("page", page5);
//                List<UserExperiencePotionEntity> userExperiencePotionEntityList = userExperiencePotionService.queryUserExperiencePotion(user);
//                return R.ok().put("data", userExperiencePotionEntityList);
        }
        return R.ok();
    }
}
