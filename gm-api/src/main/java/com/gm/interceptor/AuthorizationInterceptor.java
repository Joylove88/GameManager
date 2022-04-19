/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.interceptor;


import com.gm.common.exception.RRException;
import com.gm.annotation.Login;
import com.gm.modules.user.entity.UserTokenEntity;
import com.gm.modules.user.service.UserTokenService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserTokenService tokenService;

    public static final String USER_KEY = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login annotation;
        if(handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        }else{
            return true;
        }

        if(annotation == null){
            return true;
        }

        //从header中获取token
        String token = request.getHeader("authorization");
        //如果header中不存在token，则从参数中获取token
        if(StringUtils.isBlank(token)){
            token = request.getParameter("authorization");
        }

        //token为空
        if(StringUtils.isBlank(token)){
            throw new RRException("authorization cannot be empty!");
        }

        //查询token信息
        UserTokenEntity userTokenEntity = tokenService.queryByToken(token);
        if(userTokenEntity == null || userTokenEntity.getExpireTime().getTime() < System.currentTimeMillis()){
            throw new RRException("The authorization is invalid, please login again!");
        }

        //设置userId到request里，后续根据userId，获取用户信息
        request.setAttribute(USER_KEY, userTokenEntity.getUserId());

        return true;
    }
}
