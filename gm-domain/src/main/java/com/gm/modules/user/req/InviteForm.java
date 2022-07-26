/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.gm.modules.user.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * 注册表单
 *
 * @author Axiang
 */
@Data
@ApiModel(value = "邀请注册表单")
public class InviteForm {
    @ApiModelProperty(value = "地址")
    @NotBlank(message = "地址不能为空")
    private String address;

    @ApiModelProperty(value = "上级地址")
    @NotBlank(message = "上级地址不能为空")
    private String inviteAddress;

}
