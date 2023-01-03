package com.gm.modules.market.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取我的物品请求参数
 */
@Data
public class GetItemsReq {
    /**
     * 物品类型（0：英雄，1：英雄碎片，2：装备，3：装备卷轴，4：经验道具）
     */
//    @NotBlank(message = "itemsType can not null")
    private String itemsType;
    /**
     * 英雄星级
     */
    private Integer starCode;
    /**
     * 装备稀有度(1:白色,2:绿色,3:蓝色,4:紫色,5:橙色)
     */
    private String equipRarecode;
    /**
     * 经验道具稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
     */
    private String exEquipRarecode;
}
