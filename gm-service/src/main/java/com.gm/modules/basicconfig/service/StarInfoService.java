package com.gm.modules.basicconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.basicconfig.entity.StarInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-25 14:59:05
 */
public interface StarInfoService extends IService<StarInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取星级信息
     * @return
     */
    List<StarInfoEntity> getStarInfoPro();
}

