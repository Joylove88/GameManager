package com.gm.modules.user.req;

import lombok.Data;

/**
 * 玩家使用经验道具
 */
@Data
public class UseExpReq {
    /**
     * 使用数量
     */
    private Integer useNum;
    /**
     * 经验稀有度(1:白色,2:绿色,3:蓝色,4:紫色)
     */
    private String expRare;
}
