package com.gm.modules.market.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取我的物品请求参数
 */
@Data
public class GetItemsReq {
    /**
     * 物品类型（0：英雄，1：英雄碎片，2：装备，3：装备卷轴，4：药水）
     */
//    @NotBlank(message = "itemsType can not null")
    private String itemsType;
}
