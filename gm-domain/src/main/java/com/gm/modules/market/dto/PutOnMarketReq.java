package com.gm.modules.market.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 上架市场接口
 */
@Data
public class PutOnMarketReq {
    /**
     * 物品类型（0：英雄，1：英雄碎片，2：装备，3：装备卷轴，4：经验道具）
     */
    @NotBlank(message = "itemsType can not null")
    private String itemsType;
    /**
     * 物品ID
     */
    @NotBlank(message = "itemsId can not null")
    private Long itemsId;
    /**
     * 物品价格
     */
    @NotBlank(message = "sellPrice can not null")
    private BigDecimal sellPrice;
}
