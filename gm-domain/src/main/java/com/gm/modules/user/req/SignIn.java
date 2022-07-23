/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 验签类
 *
 * @author Axiang Axiang@gmail.com
 */
@Data
@ApiModel(value = "验签类")
public class SignIn {
    @ApiModelProperty(value = "钱包地址")
    @NotBlank(message="User wallet address cannot be empty")
    private String address;
    private String msg;
    private String signedMsg;
    private String clientIp;
    private String userAgent;

}
