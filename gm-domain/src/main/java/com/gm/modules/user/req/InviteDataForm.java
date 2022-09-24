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
 * 邀请链接数据
 *
 * @author Axiang
 */
@Data
@ApiModel(value = "邀请链接数据表单")
public class InviteDataForm {
    @ApiModelProperty(value = "邀请码")
    @NotBlank(message = "邀请码不能为空")
    private String expandCode;

}
