/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gm.annotation.LoginUser;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.*;
import com.gm.common.validator.ValidatorUtils;
import com.gm.annotation.Login;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.req.InviteDataForm;
import com.gm.modules.user.req.InviteForm;
import com.gm.modules.user.req.SignIn;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.entity.UserLoginLogEntity;
import com.gm.modules.user.service.UserTokenService;
import com.gm.modules.user.service.UserLoginLogService;
import com.gm.modules.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 登录接口
 *
 * @author Mark axiang
 */
@RestController
@RequestMapping("/api")
@Api(tags="登录接口")
public class ApiLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService tokenService;
    @Autowired
    private UserLoginLogService userLoginLogService;
    @Autowired
    private SysConfigService sysConfigService;

    @PostMapping("signIn")
    @ApiOperation("签名验证")
    public R signIn(HttpServletRequest request,@RequestBody SignIn signIn) throws ParseException {
        String msgDate = "";
        Date signDate = new Date();
        // 通过MSG获取日期
        if (StringUtils.isEmpty(signIn.getMsg())){
            throw new RRException("Data exception!");
        }
        // MSG安全校验 防止注入攻击;
        if(ValidatorUtils.securityVerify(signIn.getMsg())){
            throw new RRException(ErrorCode.SIGN_MSG_EXCEPTION.getDesc());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        msgDate = signIn.getMsg().substring(signIn.getMsg().indexOf("[") + 1,signIn.getMsg().indexOf("]"));
        signDate = format.parse(msgDate);

        // 登录类型
        String userAgent = UserAgentUtils.getFrontType(request);
        signIn.setUserAgent(userAgent);

        // 登录IP
        String clientIp = IPUtils.getIpAddr(request);
        signIn.setClientIp(clientIp);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(signIn.getAddress())){

            // Address安全校验 防止注入攻击;
            if(ValidatorUtils.securityVerify(signIn.getAddress())){
                throw new RRException(ErrorCode.SIGN_ADDRESS_EXCEPTION.getDesc());
            }
            // signedMsg安全校验 防止注入攻击;
            if(ValidatorUtils.securityVerify(signIn.getSignedMsg())){
                throw new RRException(ErrorCode.SIGN_SIGNEDMSG_EXCEPTION.getDesc());
            }
            // 检测用户是否存在
            UserEntity userEntity = userService.queryByAddress(signIn.getAddress());
            if(userEntity != null){

                // 检测签名日期是否在用户登陆日志内存在 （防止黑客攻击）
                List list = userLoginLogService.queryUserLoginLog(signDate);
                if (list.size() > 0){
                    throw new RRException("The log already exists the date signed!");
                }

                // 获得两个时间的毫秒时间差异
                long isDate = userEntity.getSignDate().getTime() - Constant.HOUR12;


                // 获取用户最后登陆的12小时内的日志数据
                UserLoginLogEntity log = new UserLoginLogEntity();
                log.setUserCode(userEntity.getUserWalletAddress());
                log.setGtTime(userEntity.getSignDate().getTime());
                log.setLtTime(isDate);
                List<UserLoginLogEntity> login12Hs = userLoginLogService.getLogin12H(log);
                // 赋值接收的日期
                userEntity.setSignDate(signDate);
                // 获取到的日志数据必须非空
                if (login12Hs.size() > 0){
                    // 如果接收到的时间大于最新的登陆日志时间说明正常 则进行验签
                    if (signDate.getTime() > login12Hs.get(0).getLoginTimeTs()){
                        map = login(signIn,userEntity);
                    } else {
                        // 进入这里说明存在异常，先将账户状态设置冻结（防止黑客攻击）
                        UserEntity userStatus = new UserEntity();
                        userStatus.setUserId(userEntity.getUserId());
                        userStatus.setAccountStatus(Constant.disabled);
                        userService.updateById(userStatus);

                        // 如果接收到的时间大于登陆日志内最新日期12小时内的最开始的日期 说明玩家存在向前跨时区 属于正常 则进行验签
                        if (signDate.getTime() > login12Hs.get(login12Hs.size()-1).getLoginTimeTs()){
                            map = login(signIn,userEntity);
                        } else {
                            throw new RRException("Signature date is abnormal!");
                        }
                    }
                } else {
                    throw new RRException("Log date is abnormal!");
                }
            } else {

                // 用户不存在则执行自动注册
                UserEntity userRegister = new UserEntity();
                userRegister.setSignDate(signDate);
                userRegister.setUserWalletAddress(signIn.getAddress().toLowerCase());
                userService.userRegister(userRegister);
                UserEntity userRe = userService.queryByAddress(signIn.getAddress());
                map = login(signIn,userRe);
            }
        }
        return R.ok(map);
    }

    private Map login(SignIn signIn,UserEntity userEntity){
        Map<String, Object> map = new HashMap<>();
        // 进行验签 通过后返回TOKEN
        Boolean result = MetaMaskUtil.validate(signIn.getSignedMsg(),signIn.getMsg(),signIn.getAddress());
        if (result){
            // 保存登录记录
            userLoginLogService.saveLoginLog(userEntity,signIn.getUserAgent(),signIn.getClientIp(), "0","用户登陆成功");
            // 开始登陆
            map = userService.login(userEntity);
        } else {
            throw new RRException("The Login Verification failed!");
        }
        return map;
    }

    @Login
    @PostMapping("logout")
    @ApiOperation("退出")
    public R logout(@ApiIgnore @RequestAttribute("userId") long userId){
        tokenService.expireToken(userId);
        return R.ok();
    }


    /**
     * 邀请注册
     */
    @PostMapping("invite")
    @ApiOperation("邀请注册")
    public R invite(@RequestBody InviteForm form) {
        // 1.表单校验
        ValidatorUtils.validateEntity(form);
        // 2.校验两个地址
        UserEntity userEntity = userService.queryByAddress(form.getAddress());
        if (userEntity != null) {
            throw new RRException(ErrorCode.ADDRESS_HAS_EXIST.getDesc());
        }
        UserEntity userInviteEntity = userService.queryByAddress(form.getInviteAddress());
        if (userInviteEntity == null) {
            throw new RRException(ErrorCode.INVITE_ADDRESS_NOT_EXIST.getDesc());
        }
        // 3.保存用户
        UserEntity user = new UserEntity();
        user.setSignDate(new Date());
        user.setUserWalletAddress(form.getAddress().toLowerCase());
        user.setFatherId(userInviteEntity.getUserId());
        user.setGrandfatherId(userInviteEntity.getFatherId());
        userService.userRegister(user);
        return R.ok();
    }

    /**
     * 获取邀请链接数据
     * 代理那边需要增加3个统计数据
     * 邀请链接访问人数
     * 邀请链接注册人数
     * 邀请链接消费人数
     */
    @Login
    @PostMapping("inviteData")
    @ApiOperation("邀请链接数据")
    public R inviteData(@LoginUser UserEntity userEntity) {
//         1.表单校验
//        ValidatorUtils.validateEntity(form);
//        // 2.查询该邀请码数据
        Map<String, Object> map = new HashMap<>();
//        // 查询访问次数
//        UserEntity userEntity = userService.queryByExpandCode(form.getExpandCode());
//        if (userEntity == null) {
//            throw new RRException("邀请码不存在！");
//        }
        map.put("viewTimes", userEntity.getExpandCodeViewTimes());
        // 查询注册人数
        int count = userService.count(new QueryWrapper<UserEntity>().eq("FATHER_ID", userEntity.getUserId()));
        map.put("register", count);
        // 查询消费人数
        int effectiveCount = userService.queryEffectiveUserCount(userEntity);
        map.put("consumer", effectiveCount);
        return R.ok(map);
    }

    public static void main(String[] args) {
//        double s = Math.floor(Math.random() * 1000000);
//        UserEntity userEntity = new UserEntity();
//        userEntity.setNonce((long) s);
//        System.out.println(userEntity.getNonce());


        String r = "0xfdd697c2024e40fcb3707875b3d64f7db68f3729df358f53f24de0dadc4ae0d2";
        String s = "0x435ce19292d9d2972c456136d75d1130e62562f4da63cc26522370733821ef25";
        int v = 27;
        String msg = "HI";
        String MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";
        String message = MESSAGE_PREFIX + msg.length() + msg;
        byte[] hash = Hash.sha3(message.getBytes(StandardCharsets.UTF_8));
        ECDSASignature es = new ECDSASignature( Numeric.toBigInt(r), Numeric.toBigInt(s));
        System.out.println("0x1a43d9d9f10058e03feda50c2aa657b18f1e1181"); //Expected Address
        System.out.println("0x" + Keys.getAddress(Sign.recoverFromSignature(v - 27, es, hash))); //recovered Address

//        String address = "0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45";
//        String address2 = "0x1F8aE97a44039E1994c0d0bAdEfDa77c82E51bAc";
//        System.out.println("Connecting to Ethereum ...");
//        Web3j web3 = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545"));
//        System.out.println("Successfuly connected to Ethereum");
//        EthSendTransaction ethSendTransaction = null;
////        BigInteger nonce = BigInteger.ZERO;
//        try {
////            List outputParametrs = new ArrayList();
////            TypeReference<Bool> typeReference = new TypeReference<Bool>() {
////            };
////            outputParametrs.add(typeReference);
////            Function function = new Function(
////                    "mint",
////                    Arrays.asList(new Utf8String("0xa23517272EeAD508049f11623a75Ece491e10915,300")),
////                    outputParametrs
////            );
////            String encodedFunction = FunctionEncoder.encode(function);
////            Credentials credentials = Credentials.create("75b2d2aefb8e7660aada813fe49536a8e1d1c9b8890b8e1d8eef7960d35a09dd");
////            TransactionManager txManager = new FastRawTransactionManager(web3, credentials);
////            String txHash = txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
////                    "0xA42F52B0F7c443A4d483f06A7423cd8D1c8887fE", encodedFunction, BigInteger.ZERO).getTransactionHash();
////            System.out.println(function.getInputParameters());
////            System.out.println(txHash);
////            TransactionReceiptProcessor receiptProcessor =
////                    new PollingTransactionReceiptProcessor(web3, TransactionManager.DEFAULT_POLLING_FREQUENCY,
////                            TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
////
////            TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash);
////            System.out.println("txReceupt:"+ JSONObject.toJSONString(txReceipt));
////            String hash = ethSendTransaction.getTransactionHash();
////            System.out.println("hash:" + hash);
////            System.out.println(JSONObject.toJSONString(ethSendTransaction));
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
//
//        EthGetBalance balanceWei = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
//        System.out.println("balance in wei: " + balanceWei.getBalance());
//        BigDecimal balanceInEther = Convert.fromWei(balanceWei.getBalance().toString(), Convert.Unit.ETHER);
//        System.out.println("balance in ether: " + balanceInEther);
//        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
//        BigInteger nonce =  ethGetTransactionCount.getTransactionCount();
//        System.out.println("nonce: " + nonce);
//        } catch(Exception ex) {
//            if (null != ethSendTransaction){
//                System.out.println("失败的原因："+ethSendTransaction.getError().getMessage());
//            }
//            throw new RuntimeException("Error whilst sending json-rpc requests", ex);
//        }
    }
}
