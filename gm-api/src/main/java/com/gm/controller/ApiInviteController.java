/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录接口
 *
 * @author Mark axiang
 */
@RestController
@RequestMapping("/invite")
@Api(tags = "邀请接口")
public class ApiInviteController {
    @Autowired
    private UserService userService;
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 重定向邀请链接
     */
    @RequestMapping("{expandCode:[a-zA-Z0-9]+}")
    public void inviteRedirect(@PathVariable String expandCode, HttpServletResponse response) throws IOException {
        UserEntity userEntity = userService.queryByExpandCode(expandCode);
        //查询首页地址
        String index_page = sysConfigService.getValue("INDEX_PAGE");
        if (userEntity == null) {
            response.sendRedirect(index_page);
        } else {
            // 更新该邀请码访问次数
            UserEntity newUser = new UserEntity();
            newUser.setUserId(userEntity.getUserId());
            newUser.setExpandCodeViewTimes(userEntity.getExpandCodeViewTimes() + 1);
            userService.updateById(newUser);
            response.sendRedirect(index_page + "?invite=" + userEntity.getAddress());
        }
    }

}
