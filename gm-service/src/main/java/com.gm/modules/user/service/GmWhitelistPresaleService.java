package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.GmWhitelistPresaleEntity;

import java.util.List;
import java.util.Map;

/**
 * 预售白名单
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-12-15 18:36:47
 */
public interface GmWhitelistPresaleService extends IService<GmWhitelistPresaleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取预售白名单
     * @param map
     * @return
     */
    List<GmWhitelistPresaleEntity> getWhitelistPresale(Map<String, Object> map);
}

