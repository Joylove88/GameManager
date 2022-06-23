/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gm.annotation.Login;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.fightCore.service.FightCoreService;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.entity.SysDictEntity;
import com.gm.modules.sys.service.SysDictService;
import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.service.UserAccountService;
import com.gm.modules.user.service.UserService;
import com.gm.modules.user.service.UserTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.*;

/**
 * 战斗接口
 *
 * @author axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags="战斗接口")
public class ApiFightController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService tokenService;
    @Autowired
    private FightCoreService fightCoreService;
    @Autowired
    private TransactionOrderService transactionOrderService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private SysDictService sysDictService;

    @Login
    @PostMapping("startFight")
    @ApiOperation("开始战斗")
    public R startFight(@LoginUser UserEntity user, @RequestBody DrawForm form) throws Exception {
        fightCoreService.attck(user);
        return R.ok().put("gifts", "");
    }




}
