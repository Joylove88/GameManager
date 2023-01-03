package com.gm.modules.user.req;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家使用经验道具
 */
@Data
public class UseExpPropReq {
    /**
     * 玩家英雄ID
     */
    private Long userHeroId;
    /**
     * 经验道具
     */
    List<UseExpReq> expList = new ArrayList<>();
}
