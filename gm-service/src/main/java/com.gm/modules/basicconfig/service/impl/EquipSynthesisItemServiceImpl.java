package com.gm.modules.basicconfig.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gm.common.utils.Constant;
import com.gm.modules.basicconfig.dao.EquipmentInfoDao;
import com.gm.modules.basicconfig.entity.EquipmentInfoEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.basicconfig.dao.EquipSynthesisItemDao;
import com.gm.modules.basicconfig.entity.EquipSynthesisItemEntity;
import com.gm.modules.basicconfig.service.EquipSynthesisItemService;


@Service("equipSynthesisItemService")
public class EquipSynthesisItemServiceImpl extends ServiceImpl<EquipSynthesisItemDao, EquipSynthesisItemEntity> implements EquipSynthesisItemService {
    @Autowired
    private EquipmentInfoDao equipmentInfoDao;
    @Autowired
    private EquipSynthesisItemDao equipSynthesisItemDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Map equipMap = new HashMap();
        equipMap.put("STATUS", Constant.enable);
        List<EquipmentInfoEntity> equips = equipmentInfoDao.selectByMap(equipMap);
        String gmEquipFragNum = "1";
        IPage<EquipSynthesisItemEntity> page = this.page(
                new Query<EquipSynthesisItemEntity>().getPage(params),
                new QueryWrapper<EquipSynthesisItemEntity>()
//                .eq(StringUtils.isNotBlank(gmEquipFragNum),"GM_EQUIP_FRAG_NUM",gmEquipFragNum)
        );
        JSONArray jsonArray = new JSONArray();
        //封装装备层级
        for(int i = 0; i < page.getRecords().size(); i++){
            JSONObject jsonObject = new JSONObject();
            long fragNum =page.getRecords().get(i).getGmEquipFragNum();
            for (EquipmentInfoEntity equip1 : equips) {
                long equipId = equip1.getEquipId();
                String equipName = equip1.getEquipName();
                String equipRarecode = equip1.getEquipRarecode();
                // 装备名称封装
                if (page.getRecords().get(i).getGmEquipmentId().equals(equipId)) {
                    page.getRecords().get(i).setEquipName(equipName);
                    page.getRecords().get(i).setEquipRare(equip1.getEquipRarecode());
                    jsonObject.put("eqId",equipId);
                    jsonObject.put("eqName",equipName);
                    jsonObject.put("eqRareCode",equipRarecode);
                }
                // 装备碎片名称封装
                if (page.getRecords().get(i).getGmEquipmentFragId().equals(equipId)) {
                    jsonObject.put("eqFragId",equipId);
                    jsonObject.put("eqFragNum",fragNum);
                    jsonObject.put("eqFragName",equipName + "卷轴碎片");
                    page.getRecords().get(i).setEquipFragName(equipName + "卷轴碎片");
                }
            }
            jsonArray.add(jsonObject);

            // 装备合成项1封装
            String equipItem1 = page.getRecords().get(i).getGmEquipSynthesisItem1();
            if(StringUtils.isNotBlank(equipItem1)){
                boolean b = equipItem1.contains(",");
                JSONArray jsonArray2 = new JSONArray();
                if(b){
                    String[] equipItems1 = equipItem1.split(",");
                    String equipItemName1 = "";
                    for(String e1 : equipItems1){
                        JSONObject jsonObject1 = new JSONObject();
                        for (EquipmentInfoEntity equip : equips) {
                            long equipId = equip.getEquipId();
                            String equipName = equip.getEquipName();
                            String equipRarecode = equip.getEquipRarecode();
                            if (Long.parseLong(e1) == equipId) {
                                equipItemName1 += equipName + ",";
                                page.getRecords().get(i).setEquipItemName1(equipItemName1);
                                jsonObject1.put("eqId",equipId);
                                jsonObject1.put("eqName",equipName);
                                jsonObject1.put("eqRareCode",equipRarecode);
                                if(!equipRarecode.equals("1")) {
                                    jsonObject1.put("eqFragId", equipId);
                                    jsonObject1.put("eqFragNum", fragNum);
                                    jsonObject1.put("eqFragName", equipName + "卷轴碎片");
                                }
                            }
                        }
                        jsonArray2.add(jsonObject1);
                    }
                } else {
                    JSONObject jsonObject1 = new JSONObject();
                    for (EquipmentInfoEntity equip : equips) {
                        long equipId = equip.getEquipId();
                        String equipName = equip.getEquipName();
                        if (Long.parseLong(equipItem1) == equipId) {
                            page.getRecords().get(i).setEquipItemName1(equipName);
                            String equipRarecode = equip.getEquipRarecode();
                            jsonObject1.put("eqId",equipId);
                            jsonObject1.put("eqName",equipName);
                            jsonObject1.put("eqRareCode",equipRarecode);
                            if(!equipRarecode.equals("1")){
                                jsonObject1.put("eqFragId",equipId);
                                jsonObject1.put("eqFragNum",fragNum);
                                jsonObject1.put("eqFragName",equipName + "卷轴碎片");
                            }
                        }
                    }
                    jsonArray2.add(jsonObject1);
                }

                jsonObject.put("equipItem1",jsonArray2);
            }
        }
        System.out.println("=============data:"+jsonArray.toJSONString());
        return new PageUtils(page);
    }

    @Override
    public List<EquipSynthesisItemEntity> getEquipSynthesisItemEntitys(Map<String, Object> map) {
        return equipSynthesisItemDao.selectByMap(map);
    }

    @Override
    public EquipSynthesisItemEntity getEquipSyntheticFormula(Long equipId) {
        // 获取装备合成配方
        EquipSynthesisItemEntity eqSIEs = equipSynthesisItemDao.selectOne(new QueryWrapper<EquipSynthesisItemEntity>()
                .eq("STATUS", Constant.enable)
                .eq("GM_EQUIPMENT_ID", equipId)// 装备ID
        );
        return eqSIEs;
    }

}
