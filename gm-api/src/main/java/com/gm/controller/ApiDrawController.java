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
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.common.utils.R;
import com.gm.annotation.Login;
import com.gm.common.validator.ValidatorUtils;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.entity.SysDictEntity;
import com.gm.modules.sys.service.SysDictService;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.service.UserAccountService;
import com.gm.modules.user.service.UserTokenService;
import com.gm.modules.user.service.UserService;
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
import org.web3j.abi.datatypes.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

/**
 * 抽奖接口
 *
 * @author axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags="抽奖接口")
public class ApiDrawController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService tokenService;
    @Autowired
    private DrawGiftService drawGiftService;
    @Autowired
    private TransactionOrderService transactionOrderService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private SysDictService sysDictService;

    @PostMapping("drawCrypto")
    @ApiOperation("抽奖CRYPTO")
    public R drawCrypto(HttpServletRequest request, @RequestBody DrawForm form)throws Exception{
        // 表单校验
        ValidatorUtils.validateEntity(form);
        // 设置货币类型 为加密货币
        form.setCurType(Constant.enable);
        // HASH校验
        if (StringUtils.isNotBlank(form.getTransactionHash())){
            // HASH安全校验 防止注入攻击;
            if(ValidatorUtils.securityVerify(form.getTransactionHash())){
                throw new RRException(ErrorCode.SIGN_HASH_EXCEPTION.getDesc());
            }
        } else {
            throw new RRException(ErrorCode.SIGN_HASH_EXCEPTION.getDesc());
        }
        Map<String,Object> map = new HashMap<>();
        Optional<TransactionReceipt> receipt = null;
        // 获取用户信息
        UserEntity user = new UserEntity();
        // 通过交易hash查找订单是否存在
        TransactionOrderEntity orderEntity = transactionOrderService.getOrderHash(form.getTransactionHash());
        if (orderEntity == null){
            // 插入一笔订单,订单状态默认待处理
            transactionOrderService.addOrder(user, null ,form);
        }
        List gifts = new ArrayList();

        // 如果订单为空说明是初次请求 则不进行验证
        if (orderEntity != null){
            // 校验用户是否链上交易成功
            receipt = TransactionVerifyUtils.isVerify(TransactionVerifyUtils.connect(),form.getTransactionHash());
        }
        // 验证完成 更新订单
        if(receipt != null){
            String status = receipt.get().getStatus();
            String address = receipt.get().getFrom();

            // 初次查询用户信息，如果为空说明该用户为新用户
            user = userService.queryByAddress(address);
            if (user == null){
                // 用户不存在则执行自动注册
                UserEntity userRegister = new UserEntity();
                Date date = new Date();
                userRegister.setSignDate(date);
                userRegister.setUserWalletAddress(address);
                user = userService.userRegister(userRegister);
            }

            map.put("userId",user.getUserId());
            map.put("from",address);
            map.put("gasUsed",receipt.get().getGasUsed());
            map.put("blockNumber",receipt.get().getBlockNumber());
            if (status.equals(Constant.CHAIN_STATE1)){

                // 校验成功后开始抽奖
                gifts = drawStart(user, form);

                // 更新当前订单状态为成功
                map.put("status",Constant.enable);
                transactionOrderService.updateOrder(form,gifts,map);

            } else if(status.equals(Constant.CHAIN_STATE0)) {
                // 更新当前订单状态为失败
                map.put("status",Constant.failed);
                transactionOrderService.updateOrder(form,gifts,map);
            }
        }
        return R.ok().put("gifts", gifts);
    }

    @Login
    @PostMapping("drawGold")
    @ApiOperation("抽奖Gold")
    public R drawGold(@LoginUser UserEntity user, @RequestBody DrawForm form) throws Exception {
        // 设置货币类型 为金币
        form.setCurType(Constant.disabled);

        List gifts = new ArrayList();

        // 地址校验
        if (StringUtils.isNotBlank(user.getUserWalletAddress())){
            // 获取用户账户余额
            QueryWrapper<UserAccountEntity> wrapper = new QueryWrapper<UserAccountEntity>()
                    .eq("USER_ID",user.getUserId());
            UserAccountEntity userAccount = userAccountService.getOne(wrapper);
            if (userAccount == null){
                throw new RRException(ErrorCode.USER_ACCOUNT_EXPIRED.getDesc());
            }

            // 获取抽奖所需金额
            List<SysDictEntity> drawType = sysDictService.getSysDict("GM_DRAW_CONFIG","GM_DRAW_TYPE");

            // 判断单抽或十连抽进行金额匹配 校验用户余额是否足够支付本次抽奖所需费用
            if (form.getDrawType().equals(drawType.get(0).getCode()) ||
                    form.getDrawType().equals(drawType.get(1).getCode())){

                // 单抽金额
                if (userAccount.getBalance() < Double.valueOf(drawType.get(0).getValue())){
                    throw new RRException(ErrorCode.BALANCE_NOT_ENOUGH.getDesc());
                }

                // 十连抽金额
                if (userAccount.getBalance() < Double.valueOf(drawType.get(1).getValue())){
                    throw new RRException(ErrorCode.BALANCE_NOT_ENOUGH.getDesc());
                }

                // 校验成功后开始抽奖
                gifts = drawStart(user, form);

                // 插入一笔订单,订单状态为成功
                transactionOrderService.addOrder(user,gifts,form);
            }

        } else {

            throw new RRException(ErrorCode.SIGN_ADDRESS_EXCEPTION.getDesc());

        }

        return R.ok().put("gifts", gifts);
    }
    private List<Object> drawStart(UserEntity user, DrawForm form) throws Exception {
        List<Object> gifts = new ArrayList<>();

        if (Constant.ItemType.HERO.getValue().equals(form.getItemType())){
            // 英雄抽奖
            gifts = drawGiftService.heroDrawStart(user,form);
        } else if (Constant.ItemType.EQUIPMENT.getValue().equals(form.getItemType())){
            // 装备抽奖
            gifts = drawGiftService.equipDrawStart(user,form);
        } else if (Constant.ItemType.EXPERIENCE.getValue().equals(form.getItemType())){
            // 经验抽奖
            gifts = drawGiftService.exDrawStart(user,form);
        }
        return gifts;
    }
    @PostMapping("getNFTURL")
    @ApiOperation("英雄抽奖")
    public String getNFTURL(@RequestBody String id){

        return "";
    }


    public static void main(String[] args) throws Exception {
        String address = "0x77C3d93D495e3157Af9345FdA693d84911487877";
        String address2 = "0x1F8aE97a44039E1994c0d0bAdEfDa77c82E51bAc";
        System.out.println("Connecting to Ethereum ...");
        Web3j web3 = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545"));
        System.out.println("Successfuly connected to Ethereum");
        EthSendTransaction ethSendTransaction = null;
        BigInteger nonce = BigInteger.ZERO;
        try {
//            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
//                    "0x1F8aE97a44039E1994c0d0bAdEfDa77c82E51bAc",
//                    DefaultBlockParameterName.PENDING
//            ).send();
//            nonce = ethGetTransactionCount.getTransactionCount();
            List outputParametrs = new ArrayList();
            TypeReference<Bool> typeReference = new TypeReference<Bool>() {
            };
            outputParametrs.add(typeReference);
//            List<Type> inputParametrs = Arrays.asList();
            Function function = new Function(
                    "mint",
                    Arrays.asList(new Utf8String("0xa23517272EeAD508049f11623a75Ece491e10915,300")),
                    outputParametrs
            );
            String encodedFunction = FunctionEncoder.encode(function);
            Credentials credentials = Credentials.create("75b2d2aefb8e7660aada813fe49536a8e1d1c9b8890b8e1d8eef7960d35a09dd");
//            BigInteger gasPrice = BigInteger.valueOf(2000000000);
            TransactionManager txManager = new FastRawTransactionManager(web3, credentials);
            String txHash = txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
                    "0xA42F52B0F7c443A4d483f06A7423cd8D1c8887fE", encodedFunction, BigInteger.ZERO).getTransactionHash();
            System.out.println(function.getInputParameters());
//            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,gasPrice,BigInteger.valueOf(410131),"0xA42F52B0F7c443A4d483f06A7423cd8D1c8887fE",encodedFunction);
//            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//            String hexValue = Numeric.toHexString(signedMessage);
//            System.out.println("hexValue:"+hexValue);
//            ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            System.out.println(txHash);
            TransactionReceiptProcessor receiptProcessor =
                    new PollingTransactionReceiptProcessor(web3, TransactionManager.DEFAULT_POLLING_FREQUENCY,
                            TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);

            TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash);
            System.out.println("txReceupt:"+JSONObject.toJSONString(txReceipt));
//            String hash = ethSendTransaction.getTransactionHash();
//            System.out.println("hash:" + hash);
//            System.out.println(JSONObject.toJSONString(ethSendTransaction));
//        // web3_clientVersion returns the current client version.
//        Web3ClientVersion clientVersion = web3.web3ClientVersion().send();
//
//        //eth_blockNumber returns the number of most recent block.
//        EthBlockNumber blockNumber = web3.ethBlockNumber().send();
//
//        //eth_gasPrice, returns the current price per gas in wei.
//        EthGasPrice gasPrice = web3.ethGasPrice().send();
//
//        // Print result
//        System.out.println("Client version: " + clientVersion.getWeb3ClientVersion());
//        System.out.println("Block number: " + blockNumber.getBlockNumber());
//        System.out.println("Gas price: " + gasPrice.getGasPrice());

//        EthGetBalance balanceWei = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
//        System.out.println("balance in wei: " + balanceWei.getBalance());
//        BigDecimal balanceInEther = Convert.fromWei(balanceWei.getBalance().toString(), Convert.Unit.ETHER);
//        System.out.println("balance in ether: " + balanceInEther);
//        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
//        BigInteger nonce =  ethGetTransactionCount.getTransactionCount();
//        System.out.println("nonce: " + nonce);
        } catch(Exception ex) {
            if (null != ethSendTransaction){
                System.out.println("失败的原因："+ethSendTransaction.getError().getMessage());
            }
            throw new RuntimeException("Error whilst sending json-rpc requests", ex);
        }
    }
}
