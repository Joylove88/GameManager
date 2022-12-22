package com.gm.modules.user.req;

import lombok.Data;

/**
 * 玩家英雄
 */
@Data
public class UserHeroInfoReq {
    /**
     * ID
     */
    private Long userHeroId;
    /**
     * 英雄名称
     */
    private String heroName;
    /**
     * 玩家装备ID
     */
    private Long userEquipmentId;
    /**
     * 父子级装备链
     */
    private String parentEquipChain;

}
