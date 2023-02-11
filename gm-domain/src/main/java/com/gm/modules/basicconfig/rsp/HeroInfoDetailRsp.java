package com.gm.modules.basicconfig.rsp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 英雄
 */
@Data
public class HeroInfoDetailRsp {
    /**
     * 英雄名称
     */
    private String heroName;
    /**
     * 英雄职业
     */
    private String heroType;
    /**
     * 英雄图片地址
     */
    private String heroImgUrl;
    /**
     * 英雄图标地址
     */
    private String heroIconUrl;
    /**
     * 英雄龙骨地址
     */
    private String heroKeelUrl;
    /**
     * 英雄描述
     */
    private String heroDescription;
    /**
     * 英雄角色
     */
    private List<String> roles = new ArrayList<>();
}
