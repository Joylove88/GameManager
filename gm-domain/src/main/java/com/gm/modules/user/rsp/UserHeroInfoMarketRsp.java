package com.gm.modules.user.rsp;

import lombok.Data;

/**
 * 玩家英雄
 */
@Data
public class UserHeroInfoMarketRsp extends UserHeroFragInfoRsp {

    /**
     * 成长率
     */
    private Double growthRate;
}
