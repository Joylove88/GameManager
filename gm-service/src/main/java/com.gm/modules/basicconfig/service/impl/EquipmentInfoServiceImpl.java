package com.gm.modules.basicconfig.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.basicconfig.dao.EquipSynthesisItemDao;
import com.gm.modules.basicconfig.dao.EquipmentInfoDao;
import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;
import com.gm.modules.basicconfig.entity.EquipmentInfoEntity;
import com.gm.modules.basicconfig.service.EquipmentInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("equipmentInfoService")
public class EquipmentInfoServiceImpl extends ServiceImpl<EquipmentInfoDao, EquipmentInfoEntity> implements EquipmentInfoService {
    @Autowired
    private EquipmentInfoDao equipmentInfoDao;
    @Autowired
    private EquipSynthesisItemDao equipSynthesisItemDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EquipmentInfoEntity> page = this.page(
                new Query<EquipmentInfoEntity>().getPage(params),
                new QueryWrapper<EquipmentInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean updateEquipJson(Long[] equipIds) {
        Map equipMap = new HashMap();
        equipMap.put("STATUS", Constant.enable);
        List<EquipmentInfoEntity> equips = equipmentInfoDao.selectByMap(equipMap);
        Map eqSIEMap = new HashMap();
        eqSIEMap.put("STATUS", Constant.enable);
        List<EquipSynthesisItemEntity> eqSIEs = equipSynthesisItemDao.selectByMap(eqSIEMap);
        JSONArray jsonArray = new JSONArray();
        //封装装备层级
        for(int i = 0; i < eqSIEs.size(); i++){
            JSONObject jsonObject = new JSONObject();
            long fragNum =eqSIEs.get(i).getGmEquipFragNum();
            for (EquipmentInfoEntity equip1 : equips) {
                long equipId = equip1.getEquipId();
                String equipName = equip1.getEquipName();
                String equipRarecode = equip1.getEquipRarecode();
                // 装备名称封装
                if (eqSIEs.get(i).getGmEquipmentId().equals(equipId)) {
                    jsonObject.put("eqId",equipId);
                    jsonObject.put("eqName",equipName);
//                    jsonObject.put("eqRareCode",equipRarecode);
                }
                // 装备碎片名称封装
                if (eqSIEs.get(i).getGmEquipmentFragId().equals(equipId)) {
                    jsonObject.put("eqFragId",equipId);
                    jsonObject.put("eqFragNum",fragNum);
                    jsonObject.put("eqFragName",equipName + "卷轴碎片");
                }
            }

            // 装备合成项1封装
            JSONObject jsonObject1 = equipItem(eqSIEs.get(i),equips,1);
            jsonObject.putAll(jsonObject1);
            // 装备合成项2封装
            JSONObject jsonObject2 = equipItem(eqSIEs.get(i),equips,2);
            jsonObject.putAll(jsonObject2);
            // 装备合成项3封装
            JSONObject jsonObject3 = equipItem(eqSIEs.get(i),equips,3);
            jsonObject.putAll(jsonObject3);
            // 白色装备合成封装
            JSONObject jsonObject4 = equipItem(eqSIEs.get(i),equips,4);
            jsonObject.putAll(jsonObject4);
            // 蓝色装备合成封装
            JSONObject jsonObject5 = equipItem(eqSIEs.get(i),equips,5);
            jsonObject.putAll(jsonObject5);

            jsonArray.add(jsonObject);

            EquipmentInfoEntity equipmentInfoEntity = new EquipmentInfoEntity();
            equipmentInfoEntity.setEquipJson(jsonObject.toJSONString());
            equipmentInfoEntity.setEquipId(eqSIEs.get(i).getGmEquipmentId());
            equipmentInfoDao.updateById(equipmentInfoEntity);
        }
        System.out.println("=============data:"+jsonArray.toJSONString());
        return true;
    }

    @Override
    public List<EquipmentInfoEntity> queryList() {
        Map map = new HashMap();
        map.put("status","1");
        return equipmentInfoDao.queryList(map);
    }

    public JSONObject equipItem(EquipSynthesisItemEntity eqSIEs,List<EquipmentInfoEntity> equips,int itemType){
        // 装备合成项封装
        String equipItem = "";
        String jsonName = "equipItem"+itemType;
        if(itemType == 1){
            equipItem = eqSIEs.getGmEquipSynthesisItem1();
        } else if (itemType == 2){
            equipItem = eqSIEs.getGmEquipSynthesisItem2();
        } else if (itemType == 3){
            equipItem = eqSIEs.getGmEquipSynthesisItem3();
        } else if (itemType == 4){
            equipItem = eqSIEs.getGmEquipWhite();
            jsonName = "equipWhite"+itemType;
        } else if (itemType == 5){
            equipItem = eqSIEs.getGmEquipBlue();
            jsonName = "equipBlue"+itemType;
        }
        long fragNum =eqSIEs.getGmEquipFragNum();
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isNotBlank(equipItem)){
            boolean b = equipItem.contains(",");
            JSONArray jsonArray = new JSONArray();
            if(b){
                String[] equipItems2 = equipItem.split(",");
                for(String e1 : equipItems2){
                    JSONObject jsonObject1 = new JSONObject();
                    for (EquipmentInfoEntity equip : equips) {
                        long equipId = equip.getEquipId();
                        String equipName = equip.getEquipName();
                        String equipRarecode = equip.getEquipRarecode();
                        if (Long.parseLong(e1) == equipId) {
                            jsonObject1.put("eqId",equipId);
                            jsonObject1.put("eqName",equipName);
                            if(!equipRarecode.equals("1")) {
                                jsonObject1.put("eqFragId", equipId);
                                jsonObject1.put("eqFragNum", fragNum);
                                jsonObject1.put("eqFragName", equipName + "卷轴碎片");
                            }
                        }
                    }
                    jsonArray.add(jsonObject1);
                }
            } else {
                JSONObject jsonObject1 = new JSONObject();
                for (EquipmentInfoEntity equip : equips) {
                    long equipId = equip.getEquipId();
                    String equipName = equip.getEquipName();
                    if (Long.parseLong(equipItem) == equipId) {
                        String equipRarecode = equip.getEquipRarecode();
                        jsonObject1.put("eqId",equipId);
                        jsonObject1.put("eqName",equipName);
                        if(!equipRarecode.equals("1")){
                            jsonObject1.put("eqFragId",equipId);
                            jsonObject1.put("eqFragNum",fragNum);
                            jsonObject1.put("eqFragName",equipName + "卷轴碎片");
                        }
                    }
                }
                jsonArray.add(jsonObject1);
            }
            jsonObject.put(jsonName,jsonArray);
        }
        return jsonObject;
    }


}
