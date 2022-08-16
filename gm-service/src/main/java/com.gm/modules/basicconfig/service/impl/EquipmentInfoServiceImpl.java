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
import com.gm.modules.basicconfig.rsp.EquipmentInfoRsp;
import com.gm.modules.basicconfig.service.EquipmentInfoService;
import com.gm.modules.user.dao.UserEquipmentDao;
import com.gm.modules.user.dao.UserHeroEquipmentWearDao;
import com.gm.modules.user.entity.UserEquipmentEntity;
import com.gm.modules.user.rsp.UserHeroEquipmentWearRsp;
import com.gm.modules.user.rsp.UserHeroInfoRsp;
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
    @Autowired
    private UserHeroEquipmentWearDao userHeroEquipmentWearDao;
    @Autowired
    private UserEquipmentDao userEquipmentDao;

    /**
     * 父级装备ID
     */
    private static String parentEquipChain = "";

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
//        Map equipMap = new HashMap();
//        equipMap.put("STATUS", Constant.enable);
//        List<EquipmentInfoEntity> equips = equipmentInfoDao.selectByMap(equipMap);
//        Map eqSIEMap = new HashMap();
//        eqSIEMap.put("STATUS", Constant.enable);
//        List<EquipSynthesisItemEntity> eqSIEs = equipSynthesisItemDao.selectByMap(eqSIEMap);
//        JSONArray jsonArray = new JSONArray();
//        //封装装备层级
//        for(int i = 0; i < eqSIEs.size(); i++){
//            JSONObject jsonObject = new JSONObject();
//            long fragNum =eqSIEs.get(i).getGmEquipFragNum();
//            for (EquipmentInfoEntity equip1 : equips) {
//                long equipId = equip1.getEquipId();
//                String equipName = equip1.getEquipName();
//                String equipRarecode = equip1.getEquipRarecode();
//                // 装备名称封装
//                if (eqSIEs.get(i).getGmEquipmentId().equals(equipId)) {
//                    jsonObject.put("eqId",equipId);
//                    jsonObject.put("eqName",equipName);
////                    jsonObject.put("eqRareCode",equipRarecode);
//                }
//                // 装备碎片名称封装
//                if (eqSIEs.get(i).getGmEquipmentFragId().equals(equipId)) {
//                    jsonObject.put("eqFragId",equipId);
//                    jsonObject.put("eqFragNum",fragNum);
//                    jsonObject.put("eqFragName",equipName + "卷轴碎片");
//                }
//            }
//
//            jsonArray = getEquipJson(jsonObject, eqSIEs.get(i), equips);
//
//            EquipmentInfoEntity equipmentInfoEntity = new EquipmentInfoEntity();
//            equipmentInfoEntity.setEquipJson(jsonObject.toJSONString());
//            equipmentInfoEntity.setEquipId(eqSIEs.get(i).getGmEquipmentId());
//            equipmentInfoDao.updateById(equipmentInfoEntity);
//        }
//        System.out.println("=============data:"+jsonArray.toJSONString());
        return true;
    }

    @Override
    public JSONObject updateEquipJson2(Long heroEquipId, List<EquipmentInfoEntity> equips, UserHeroInfoRsp rsp) {
        parentEquipChain = "";
        // 获取该英雄已穿戴的装备
        List<UserHeroEquipmentWearRsp> wearList = userHeroEquipmentWearDao.getUserWearEQ(rsp.getGmUserHeroId());
        // 获取装备栏中的装备合成公式
        EquipSynthesisItemEntity eqSIEs = getEquipSyntheticFormula(heroEquipId);
        if ( eqSIEs == null ) {
            System.out.println("获取装备合成配方失败");
        }
        //封装装备层级
        JSONObject jsonObject = new JSONObject();
        long fragNum = eqSIEs.getGmEquipFragNum();
        for (EquipmentInfoEntity equip1 : equips) {
            long equipId = equip1.getEquipId();
            String equipName = equip1.getEquipName();
            String equipRarecode = equip1.getEquipRarecode();
            String equipImgUrl = equip1.getEquipImgUrl();
            String equipIconUrl = equip1.getEquipIconUrl();
            // 装备名称封装
            if (eqSIEs.getGmEquipmentId().equals(equipId)) {
                jsonObject.put("equipId", equipId);
                jsonObject.put("equipName", equipName);
                jsonObject.put("equipRareCode",equipRarecode);
                jsonObject.put("equipImgUrl",equipImgUrl);
                jsonObject.put("equipIconUrl",equipIconUrl);
                // 装备碎片名称封装
                jsonObject.put("equipFragId", equipId);
                jsonObject.put("equipFragNum", fragNum);
                jsonObject.put("equipFragName", equipName + "卷轴碎片");
                parentEquipChain = heroEquipId.toString();
                UserEquipmentEntity userEquipment = getActivationState(equipId, wearList, parentEquipChain);
                jsonObject.put("status", userEquipment.getActivationState());
                // 如果该装备状态为已激活则获取该装备属性
                getEquipmentAttributes(jsonObject, userEquipment);
                break;
            }
        }
        // 装备合成项1,2,3,白色,蓝色装备封装
        jsonObject.put("children", equipItem(eqSIEs, equips, wearList));
        System.out.println("=============data:"+jsonObject.toJSONString());
        return jsonObject;

    }

    @Override
    public List<EquipmentInfoEntity> queryList() {
        Map map = new HashMap();
        map.put("status","1");
        return equipmentInfoDao.queryList(map);
    }

    @Override
    public List<EquipmentInfoRsp> getEquipmentInfo(EquipmentInfoEntity equipmentInfoEntity) {
        return equipmentInfoDao.getEquipmentInfo(equipmentInfoEntity);
    }

    @Override
    public List<EquipmentInfoEntity> getEquipmentInfos(Map<String, Object> map) {
        return equipmentInfoDao.selectByMap(map);
    }

    /**
     * 如果该装备状态为已激活则获取该装备属性
     * @return
     */
    private JSONObject getEquipmentAttributes(JSONObject jsonObject, UserEquipmentEntity userEquipment){
        // 如果该装备状态为已激活则获取该装备属性
        if ( userEquipment.getActivationState().equals(Constant.enable) ) {
            jsonObject.put("equipPower", userEquipment.getEquipPower());
            jsonObject.put("equipHealth", userEquipment.getEquipHealth());
            jsonObject.put("equipMana", userEquipment.getEquipMana());
            jsonObject.put("equipHealthRegen", userEquipment.getEquipHealthRegen());
            jsonObject.put("equipManaRegen", userEquipment.getEquipManaRegen());
            jsonObject.put("equipArmor", userEquipment.getEquipArmor());
            jsonObject.put("equipMagicResist", userEquipment.getEquipMagicResist());
            jsonObject.put("equipAttackDamage", userEquipment.getEquipAttackDamage());
            jsonObject.put("equipAttackSpell", userEquipment.getEquipAttackSpell());
        }
        return jsonObject;
    }
    /**
     * 获取装备合成配方
     * @param equipId
     * @return
     */
    private EquipSynthesisItemEntity getEquipSyntheticFormula(Long equipId) {
        // 获取装备合成配方
        EquipSynthesisItemEntity eqSIEs = equipSynthesisItemDao.selectOne(new QueryWrapper<EquipSynthesisItemEntity>()
                .eq("STATUS", Constant.enable)
                .eq("GM_EQUIPMENT_ID", equipId)// 装备ID
        );
        return eqSIEs;
    }

    /**
     * 获取已穿戴/激活的装备状态
     * @param equipId
     * @param wearList
     * @param parentEquipChainID
     * @return
     */
    private UserEquipmentEntity getActivationState(Long equipId, List<UserHeroEquipmentWearRsp> wearList, String parentEquipChainID) {
        String activationState = "0";// 激活状态0：未激活，1：已激活
        UserEquipmentEntity userEquipment = new UserEquipmentEntity();
        userEquipment.setActivationState(activationState);
        // 循环已穿戴的装备
        for ( UserHeroEquipmentWearRsp equipmentWearRsp : wearList ) {
            // 如果已穿戴装备和英雄装备栏的装备ID相等说明该位置装备已激活
            if ( equipmentWearRsp.getGmEquipmentId().equals(equipId) ) {
                // 固定为第1or2or3层位置的装备
                if ( StringUtils.isNotBlank(equipmentWearRsp.getParentEquipChain()) && (
                        equipmentWearRsp.getParentEquipChain().equals(parentEquipChainID)
                )) {
                    // 获取已激活装备的属性
                    userEquipment = userEquipmentDao.selectById(equipmentWearRsp.getGmUserEquipId());
                    activationState = "1";
                    userEquipment.setActivationState(activationState);
                    break;
                }
            }
        }
        return userEquipment;
    }


    /**
     * 装备合成项1,2,3,白色,蓝色装备封装
     * @param eqSIEs
     * @param equips
     * @param wearList
     * @return
     */
    private JSONArray equipItem(EquipSynthesisItemEntity eqSIEs,List<EquipmentInfoEntity> equips, List<UserHeroEquipmentWearRsp> wearList){
        String parentEquipChainID = "";
        // 装备合成项封装
        List list = getEquipItems(eqSIEs);
        JSONArray jsonArray = new JSONArray();
        // 如果合成公式不为空则进入
        int i = 0;
        while ( i < list.size() ) {
            parentEquipChainID = "";
            // 获取合成公式是否包含多件装备
            boolean b = list.get(i).toString().contains(",");
            // 多件装备
            if( b ){
                String[] equipItems2 = list.get(i).toString().split(",");
                int EQI = 0;
                for( String e1 : equipItems2 ){
                    JSONObject jsonObject = getEquipJson(equips, wearList, Long.parseLong(e1), parentEquipChainID, null, EQI);
                    EQI++;
                    jsonArray.add(jsonObject);
                }
            } else {// 单件装备
                int EQI = 0;
                JSONObject jsonObject = getEquipJson(equips, wearList, Long.parseLong(list.get(i).toString()), parentEquipChainID, null, EQI);
                jsonArray.add(jsonObject);
            }
            i++;
        }
        return jsonArray;
    }

    /**
     * 可合成装备的配方
     * @param eqSIEs
     * @param equips
     * @param wearList
     * @param parentEquipChainID
     * @return
     */
    private JSONArray equipItemChild(EquipSynthesisItemEntity eqSIEs,List<EquipmentInfoEntity> equips, List<UserHeroEquipmentWearRsp> wearList, String parentEquipChainID){
        String parentEquipChainIDC = "";
        // 装备合成项封装
        List list = getEquipItems(eqSIEs);
        JSONArray jsonArray = new JSONArray();
        // 如果合成公式不为空则进入
        int i = 0;
        while ( i < list.size() ) {
            parentEquipChainIDC = "";
            // 获取合成公式是否包含多件装备
            boolean b = list.get(i).toString().contains(",");
            // 多件装备
            if( b ){
                String[] equipItems2 = list.get(i).toString().split(",");
                int EQI = 0;
                for( String e1 : equipItems2 ){
                    JSONObject jsonObject = getEquipJson(equips, wearList, Long.parseLong(e1), parentEquipChainID, parentEquipChainIDC, EQI);
                    EQI++;
                    jsonArray.add(jsonObject);
                }
            } else {// 单件装备
                int EQI = 0;
                JSONObject jsonObject = getEquipJson(equips, wearList, Long.parseLong(list.get(i).toString()), parentEquipChainID, parentEquipChainIDC, EQI);
                jsonArray.add(jsonObject);
            }
            i++;
        }
        return jsonArray;
    }

    private JSONObject getEquipJson(List<EquipmentInfoEntity> equips, List<UserHeroEquipmentWearRsp> wearList, Long eqId, String parentEquipChainID, String parentEquipChainIDC, int EQI) {
        // 装备卷轴数量
        long fragNum = 0L;
        JSONObject jsonObject = new JSONObject();
        for ( EquipmentInfoEntity equip : equips ) {
            long equipId = equip.getEquipId();
            String equipName = equip.getEquipName();
            String equipRarecode = equip.getEquipRarecode();
            String equipImgUrl = equip.getEquipImgUrl();
            String equipIconUrl = equip.getEquipIconUrl();
            if ( eqId == equipId ) {
                UserEquipmentEntity userEquipment = new UserEquipmentEntity();
                jsonObject.put("equipId",equipId);
                jsonObject.put("equipName",equipName);
                jsonObject.put("equipImgUrl",equipImgUrl);
                jsonObject.put("equipIconUrl",equipIconUrl);
                jsonObject.put("equipRareCode",equipRarecode);
                if ( parentEquipChainIDC != null ) {
                    // 重构父级装备链(孙)
                    parentEquipChainIDC = parentEquipChainID + ("-" + EQI + ":" + equipId);
                    userEquipment = getActivationState(equipId, wearList, parentEquipChainIDC);
                    jsonObject.put("status", userEquipment.getActivationState());
                } else {
                    // 重构父级装备链（儿）
                    parentEquipChainID = parentEquipChain + ("-" + EQI + ":" +  equipId);
                    userEquipment = getActivationState(equipId, wearList, parentEquipChainID);
                    jsonObject.put("status", userEquipment.getActivationState());
                }

                // 如果该装备状态为已激活则获取该装备属性
                getEquipmentAttributes(jsonObject, userEquipment);

                // 如果为可合成装备继续获取装备合成配方
                if( equipRarecode.equals(Constant.RareCode.rareCode2.getValue()) ||
                        equipRarecode.equals(Constant.RareCode.rareCode4.getValue()) ||
                        equipRarecode.equals(Constant.RareCode.rareCode5.getValue()) ){
                    EquipSynthesisItemEntity eqSIEsChildren = getEquipSyntheticFormula(equipId);
                    if ( eqSIEsChildren == null ) {
                        break;
                    }
                    fragNum = eqSIEsChildren.getGmEquipFragNum();
                    jsonObject.put("children", equipItemChild(eqSIEsChildren, equips, wearList, parentEquipChainID));
                    jsonObject.put("equipFragId",equipId);
                    jsonObject.put("equipFragNum",fragNum);
                    jsonObject.put("equipFragName",equipName + "卷轴碎片");
                    break;
                }
                break;
            }
        }
        return jsonObject;
    }
    /**
     * 获取装备合成项
     * @param eqSIEs
     * @return
     */
    private List getEquipItems(EquipSynthesisItemEntity eqSIEs) {
        List list = new ArrayList();
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipSynthesisItem1())) {
            list.add(eqSIEs.getGmEquipSynthesisItem1());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipSynthesisItem2())) {
            list.add(eqSIEs.getGmEquipSynthesisItem2());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipSynthesisItem3())) {
            list.add(eqSIEs.getGmEquipSynthesisItem3());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipWhite())) {
            list.add(eqSIEs.getGmEquipWhite());
        }
        if ( StringUtils.isNotBlank(eqSIEs.getGmEquipBlue())) {
            list.add(eqSIEs.getGmEquipBlue());
        }
        return list;
    }

}
