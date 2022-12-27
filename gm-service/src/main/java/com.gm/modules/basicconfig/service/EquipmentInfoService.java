package com.gm.modules.basicconfig.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;
import com.gm.modules.basicconfig.entity.EquipmentInfoEntity;
import com.gm.modules.basicconfig.rsp.EquipmentInfoRsp;
import com.gm.modules.user.rsp.UserHeroEquipmentWearRsp;
import com.gm.modules.user.rsp.UserHeroInfoRsp;

import java.util.List;
import java.util.Map;

/**
 * 装备基础表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-02-11 17:20:39
 */
public interface EquipmentInfoService extends IService<EquipmentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean updateEquipJson(Long[] equipIds);
    JSONObject updateEquipJson2(Long heroEquipId, EquipSynthesisItemEntity eqSIEs, List<EquipmentInfoEntity> equips, UserHeroInfoRsp rsp, List<UserHeroEquipmentWearRsp> wearList);

    List<EquipmentInfoEntity> queryList();

    /**
     * 获取装备信息
     * @param equipmentInfoEntity
     * @return
     */
    List<EquipmentInfoRsp> getEquipmentInfo(EquipmentInfoEntity equipmentInfoEntity);

    /**
     * 获取装备信息
     * @param map
     * @return
     */
    List<EquipmentInfoEntity> getEquipmentInfos(Map<String, Object> map);
}

