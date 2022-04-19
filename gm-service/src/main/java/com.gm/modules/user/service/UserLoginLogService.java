package com.gm.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.common.utils.PageUtils;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserLoginLogEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户登录日志
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-08 16:00:57
 */
public interface UserLoginLogService extends IService<UserLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //API
    /**
     * 登录时保存登录记录
     *
     * @param user      登录用户
     * @param userAgent userAgent
     * @param clientIp  客户端IP
     */
    void saveLoginLog(UserEntity user, String userAgent, String clientIp, String code, String msg);

    List<UserLoginLogEntity> queryUserLoginLog(Date signDate);

    // 获取用户后登陆12小时内的数据
    List<UserLoginLogEntity> getLogin12H(UserLoginLogEntity userLoginLogEntity);
}

