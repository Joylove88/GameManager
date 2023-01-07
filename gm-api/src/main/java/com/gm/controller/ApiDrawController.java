/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
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
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.basicconfig.dto.SummonedEventDto;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.ethTransfer.service.EthTransferService;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.SummonReq;
import com.gm.modules.user.req.UserInfoReq;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 抽奖接口
 *
 * @author axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags = "抽奖接口")
public class ApiDrawController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService tokenService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private DrawGiftService drawGiftService;
    @Autowired
    private TransactionOrderService transactionOrderService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private EthTransferService ethTransferService;

    @PostMapping("summonedWithCrypto")
    @ApiOperation("CRYPTO召唤")
    public R summonedWithCrypto(HttpServletRequest request, @RequestBody SummonReq form) throws Exception {
        // 表单校验
        ValidatorUtils.validateEntity(form);
        // 设置货币类型 为加密货币
        form.setCurType(Constant.CurrencyType._CRYPTO.getValue());
        // HASH校验
        if (StringUtils.isNotBlank(form.getTransactionHash())) {
            // HASH安全校验 防止注入攻击;
            if (ValidatorUtils.securityVerify(form.getTransactionHash())) {
                throw new RRException(ErrorCode.SIGN_HASH_EXCEPTION.getDesc());
            }
        } else {
            throw new RRException(ErrorCode.SIGN_HASH_EXCEPTION.getDesc());
        }
        List giftBoxs = new ArrayList();
        Optional<TransactionReceipt> receipt = null;
        // 实例化用户类
        UserEntity user = new UserEntity();
        // 通过交易hash查找订单是否存在
        TransactionOrderEntity order = transactionOrderService.getOrderHash(form.getTransactionHash());

        if (order == null) {// 如果订单为空说明是初次请求 先创建订单
            // 插入一笔订单,订单状态默认待处理
            transactionOrderService.addOrder(user, null, form);
        } else {// 订单不为空说明是二次请求 验证成功后执行核心业务
            // 校验用户是否链上交易成功
            receipt = TransactionVerifyUtils.isVerify(TransactionVerifyUtils.connect(), form.getTransactionHash());
            giftBoxs = ethTransferService.eth(form.getTransactionHash(), order, receipt, form);
        }
        return R.ok().put("giftBoxs", giftBoxs);
    }

    @PostMapping("getSummonedPoolPrice")
    @ApiOperation("获取召唤池价格")
    public R getSummonedPoolPrice(@RequestBody UserInfoReq user) {
        // 安全校验 防止注入攻击;
        if (ValidatorUtils.securityVerify(user.getAddress())) {
            throw new RRException(ErrorCode.REQUEST_PARAMETER_DATA_EXCEPTION.getDesc());
        }
        SummonedEventDto rsp = drawGiftService.getSummonedPrice(user.getAddress());
        Map<String, Object> map = new HashMap<>();
        map.put("onePrice", rsp.getOnePriceNew());
        map.put("tenPrice", rsp.getTenPriceNew());
        map.put("oneOrigPrice", rsp.getOnePrice());
        map.put("tenOrigPrice", rsp.getTenPrice());
        map.put("onePriceBUSD", rsp.getSummonRebateSwitch().equals(Constant.enable) ? rsp.getOnePrice() : rsp.getOnePriceNew());
        map.put("tenPriceBUSD", rsp.getSummonRebateSwitch().equals(Constant.enable) ? rsp.getTenPrice() : rsp.getTenPriceNew());
        map.put("quantityUsed", rsp.getQuantityUsed());
        map.put("quantityAvailable", rsp.getQuantityAvailable());
        return R.ok().put("data", map);
    }

    @Login
    @PostMapping("summonedWithGold")
    @ApiOperation("Gold召唤")
    public R summonedWithGold(@LoginUser UserEntity user, @RequestBody SummonReq form) throws Exception {
        // 设置货币类型 为金币
        form.setCurType(Constant.CurrencyType._GOLD_COINS.getValue());
        List giftBoxs = new ArrayList();
        // 地址校验
        if (StringUtils.isNotBlank(user.getAddress())) {
            // 获取用户战斗账户余额
            QueryWrapper<UserAccountEntity> wrapper = new QueryWrapper<UserAccountEntity>()
                    .eq("USER_ID", user.getUserId())
                    .eq("CURRENCY", Constant.ZERO_);
            UserAccountEntity userAccount = userAccountService.getOne(wrapper);
            if (userAccount == null) {
                throw new RRException(ErrorCode.USER_ACCOUNT_EXPIRED.getDesc());
            }
            // 系统识别当前活动有效性，如成功匹配预售白名单内的玩家则获取折扣后的金额否则返回原价
            SummonedEventDto summonedEventDto = drawGiftService.getSummonedPrice(user.getAddress());
            // 默认获取十连抽价格
            BigDecimal subMoney = BigDecimal.valueOf(summonedEventDto.getTenPriceNew());
            // 获取原价
            BigDecimal orderFee = BigDecimal.valueOf(summonedEventDto.getTenPrice());
            // 判断单抽或十连抽进行金额匹配 校验用户余额是否足够支付本次召唤所需费用
            if (form.getSummonNum() == Constant.SummonNum.NUM1.getValue()) {
                // 单抽金额
                subMoney = BigDecimal.valueOf(summonedEventDto.getOnePriceNew());
                orderFee = BigDecimal.valueOf(summonedEventDto.getOnePrice());
                if (userAccount.getBalance() < summonedEventDto.getOnePriceNew()) {
                    throw new RRException(ErrorCode.BALANCE_NOT_ENOUGH.getDesc());
                }
            } else if (form.getSummonNum() == Constant.SummonNum.NUM10.getValue()) {
                // 十连抽金额
                if (userAccount.getBalance() < summonedEventDto.getTenPriceNew()) {
                    throw new RRException(ErrorCode.BALANCE_NOT_ENOUGH.getDesc());
                }
            }
            // 更新玩家账户余额
            boolean effect = userAccountService.updateAccountSub(user.getUserId(), subMoney, Constant.ZERO_);
            if (!effect) {
                throw new RRException("Player account deduction failed!");// 账户金额更新失败
            }
            // 设置form里的金额
            form.setOrderFee(orderFee);
            form.setRealFee(subMoney);
            // 校验成功后开始召唤
            giftBoxs = drawGiftService.startSummon(user, form, summonedEventDto);
            // 插入一笔订单,订单状态为成功
            transactionOrderService.addOrder(user, giftBoxs, form);
        } else {
            throw new RRException(ErrorCode.SIGN_ADDRESS_EXCEPTION.getDesc());
        }

        return R.ok().put("giftBoxs", giftBoxs);
    }

    @Login
    @PostMapping("getSummonedRecord")
    @ApiOperation("获取召唤记录")
    public R getSummonedRecord(@LoginUser UserEntity user) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId().toString());
        PageUtils page = transactionOrderService.queryUserOrder(map);
        return R.ok().put("page", page);
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
//           ).send();
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
            System.out.println("txReceupt:" + JSONObject.toJSONString(txReceipt));
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
        } catch (Exception ex) {
            if (null != ethSendTransaction) {
                System.out.println("失败的原因：" + ethSendTransaction.getError().getMessage());
            }
            throw new RuntimeException("Error whilst sending json-rpc requests", ex);
        }
    }
}
