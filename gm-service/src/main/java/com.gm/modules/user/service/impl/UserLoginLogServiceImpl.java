package com.gm.modules.user.service.impl;

import com.gm.modules.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;

import com.gm.modules.user.dao.UserLoginLogDao;
import com.gm.modules.user.entity.UserLoginLogEntity;
import com.gm.modules.user.service.UserLoginLogService;


@Service("userLoginLogService")
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogDao, UserLoginLogEntity> implements UserLoginLogService {
    @Autowired
    private UserLoginLogDao userLoginLogDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserLoginLogEntity> page = this.page(
                new Query<UserLoginLogEntity>().getPage(params),
                new QueryWrapper<UserLoginLogEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveLoginLog(UserEntity user, String userAgent, String clientIp, String code, String msg) {
        UserLoginLogEntity userLoginLog = new UserLoginLogEntity();
        userLoginLog.setUserId(user.getUserId());
        userLoginLog.setUserCode(user.getAddress());
        userLoginLog.setLoginTime(user.getSignDate());
        userLoginLog.setLoginTimeTs(user.getSignDate().getTime());
        userLoginLog.setLoginIp(clientIp);
        userLoginLog.setStatus("00");//登录标识
        userLoginLog.setLoginType(userAgent);
        userLoginLog.setRspCode(code);
        userLoginLog.setRspMsg(msg);
        userLoginLogDao.insert(userLoginLog);
    }

    @Override
    public List<UserLoginLogEntity> queryUserLoginLog(Date signDate) {
        Map map = new HashMap();
        map.put("LOGIN_TIME",signDate);
        List<UserLoginLogEntity> userLoginLogEntities = userLoginLogDao.selectByMap(map);
        return userLoginLogEntities;
    }

    @Override
    public List<UserLoginLogEntity> getLogin12H(UserLoginLogEntity userLoginLogEntity) {
        return userLoginLogDao.getLogin12H(userLoginLogEntity);
    }


}
