package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.Constant.ErrorCode;
import com.gm.common.exception.RRException;
import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.user.dao.GmUserWithdrawDao;
import com.gm.modules.user.entity.*;
import com.gm.modules.user.req.UseWithdrawReq;
import com.gm.modules.user.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service("gmUserWithdrawService")
@Transactional
public class GmUserWithdrawServiceImpl extends ServiceImpl<GmUserWithdrawDao, GmUserWithdrawEntity> implements GmUserWithdrawService {
    private Web3j web3j = TransactionVerifyUtils.connect();

    @Value("${withdraw.payerWalletAddress:#{null}}")
    private String payerWalletAddress;
    @Value("${withdraw.payerWalletAddressKey:#{null}}")
    private String payerWalletAddressKey;
    @Value("${withdraw.busdTokenAddress:#{null}}")
    private String busdTokenAddress;
    @Value("${withdraw.fundPoolAddress:#{null}}")
    private String fundPoolAddress;
    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserBalanceDetailService userBalanceDetailService;
    @Autowired
    private GmUserVipLevelService gmUserVipLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmUserWithdrawEntity> page = this.page(
                new Query<GmUserWithdrawEntity>().getPage(params),
                new QueryWrapper<GmUserWithdrawEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public GmUserWithdrawEntity lastWithdraw(Long userId) {
        return baseMapper.selectOne(new QueryWrapper<GmUserWithdrawEntity>()
                .eq("user_id", userId)
                .notIn("status", Constant.WithdrawStatus.VERIFY_FAIL.getValue(), Constant.WithdrawStatus.CHECK_FAIL.getValue())
                .orderByAsc("update_time")
                .last("limit 1")
        );
    }

//    @Override
//    public void withdraw(UserEntity user, GmUserVipLevelEntity gmUserVipLevel, UseWithdrawReq useWithdrawReq) throws ExecutionException, InterruptedException, IOException {
//        // 1.生成提现订单，状态为待出款
//        GmUserWithdrawEntity gmUserWithdraw = new GmUserWithdrawEntity();
//        gmUserWithdraw.setCreateTime(new Date());
//        gmUserWithdraw.setCreateTimeTs(gmUserWithdraw.getCreateTime().getTime());
//        gmUserWithdraw.setStatus(Constant.WithdrawStatus.APPLY.getValue());
//        gmUserWithdraw.setUserId(user.getUserId());
//        gmUserWithdraw.setWithdrawMoney(useWithdrawReq.getWithdrawMoney());
//        gmUserWithdraw.setCurrency(useWithdrawReq.getWithdrawType());
////        // 2.调用智能合约进行转账
////        //发送方地址
////        String from = "0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45";
////        //发送方私钥
////        String privateKey = "0xf94a3b8f8b5b7d6ed5a8a77a60252de6adc867629bfe2316b495c05e15017e54";
////        //转账数量
////        String amount = useWithdrawReq.getWithdrawMoney();
////        //接收者地址
////        String to = user.getAddress();
////        //出款合约地址
////        String coinAddress = "0x4d9e7fb8503b72b9aD8fc94366899aa99Ab89807";
////        //usdt合约地址
////        String usdtAddress = "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd";
////        //查询地址交易编号随机数
////        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
////        //支付的矿工费
////        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
////        //旷工费限制
////        BigInteger gasLimit = new BigInteger("21000");
////        //秘钥凭证
////        Credentials credentials = Credentials.create(privateKey);
////
////        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
////        //封装转账交易
////        Function function = new Function(
////                "transfer",
////                Arrays.<Type>asList(
////                        new Address(usdtAddress),
////                        new Address(to),
////                        new Uint256(amountWei)
////                ),
////                Collections.<TypeReference<?>>emptyList());
////        String data = FunctionEncoder.encode(function);
////        //签名交易
////        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);
////        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
////        //广播交易
////        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
////        gmUserWithdraw.setWithdrawHash(hash);
//        int insert = baseMapper.insert(gmUserWithdraw);
//        if (insert != 1) {
//            throw new RRException(ErrorCode.WITHDRAW_INSERT_FAIL.getDesc());
//        }
//        // 冻结账号余额
//        BigDecimal frozeMoney = Arith.add(new BigDecimal("1"), useWithdrawReq.getWithdrawHandlingFee());
//        boolean b = userAccountService.withdrawFreeze(user.getUserId(), frozeMoney, useWithdrawReq.getWithdrawType());
//        if (!b) {
//            throw new RRException(ErrorCode.WITHDRAW_UPDATE_ACCOUNT_FAIL.getDesc());
//        }
//        // 插入账变明细
//        // 查询账户余额
//        UserAccountEntity userAccountEntity = userAccountService.queryByUserIdAndCur(user.getUserId(), useWithdrawReq.getWithdrawType());
//        UserBalanceDetailEntity userBalanceDetailEntity = new UserBalanceDetailEntity();
//        userBalanceDetailEntity.setUserId(user.getUserId());
//        userBalanceDetailEntity.setCurrency(useWithdrawReq.getWithdrawType());
//        userBalanceDetailEntity.setAmount(frozeMoney);
//        userBalanceDetailEntity.setTradeType("16");//申请提现冻结
//        userBalanceDetailEntity.setUserBalance(userAccountEntity.getBalance());
//        userBalanceDetailEntity.setTradeTime(new Date());
//        userBalanceDetailEntity.setTradeTimeTs(userBalanceDetailEntity.getTradeTime().getTime());
//        userBalanceDetailEntity.setSourceId(gmUserWithdraw.getWithdrawId());
//        userBalanceDetailEntity.setTradeDesc("申请提现冻结");
//        boolean b1 = userBalanceDetailService.save(userBalanceDetailEntity);
//        if (!b1) {
//            throw new RRException(ErrorCode.WITHDRAW_INSERT_BALANCE_DETAIL_FAIL.getDesc());
//        }
//        if (1 == gmUserVipLevel.getNeedCheck()) {
//            // 不需要审核
//            checkPass(gmUserWithdraw, 1L);
//        }
//    }

    @Override
    public boolean haveApplyWithdrawOrder(Long userId) {
        GmUserWithdrawEntity gmUserWithdrawEntity = baseMapper.selectOne(new QueryWrapper<GmUserWithdrawEntity>()
                .eq("user_id", userId)
                .in("status", Constant.WithdrawStatus.APPLY.getValue(),
                        Constant.WithdrawStatus.VERIFY_SUCCESS.getValue(),
                        Constant.WithdrawStatus.CHECK_SUCCESS.getValue(),
                        Constant.WithdrawStatus.ING.getValue()
                )
                .last("limit 1")
        );
        return gmUserWithdrawEntity != null;
    }

    @Override
    public PageUtils queryWithdrawList(Long userId, Map<String, Object> params, String currency) {
        IPage<GmUserWithdrawEntity> page = this.page(
                new Query<GmUserWithdrawEntity>().getPage(params),
                new QueryWrapper<GmUserWithdrawEntity>()
                        .eq("user_id", userId)
                        .eq("currency", currency)
                        .orderByAsc("create_time")

        );

        return new PageUtils(page);
    }

    @Override
    public void checkPass(GmUserWithdrawEntity gmUserWithdraw, Long userId) {
        // 1.审核通过
        // 1.1更新订单状态
        GmUserWithdrawEntity newUserWithdrawEntity = new GmUserWithdrawEntity();
        newUserWithdrawEntity.setWithdrawId(gmUserWithdraw.getWithdrawId());
        newUserWithdrawEntity.setStatus(Constant.WithdrawStatus.CHECK_SUCCESS.getValue());
        newUserWithdrawEntity.setCheckTime(new Date());
        newUserWithdrawEntity.setCheckTimeTs(newUserWithdrawEntity.getCheckTime().getTime());
        newUserWithdrawEntity.setCheckUser(userId);
        int update = baseMapper.update(newUserWithdrawEntity, new UpdateWrapper<GmUserWithdrawEntity>()
                .eq("withdraw_id", newUserWithdrawEntity.getWithdrawId())
                .eq("status", Constant.WithdrawStatus.VERIFY_SUCCESS.getValue())
        );
        if (update != 1) {
            // 更新异常
            throw new RRException("order check pass update fail");
        }
    }

    @Override
    public void checkFail(GmUserWithdrawEntity gmUserWithdraw, Long userId) {
        // 1.审核失败
        // 1.1更新订单状态
        GmUserWithdrawEntity newUserWithdrawEntity = new GmUserWithdrawEntity();
        newUserWithdrawEntity.setWithdrawId(gmUserWithdraw.getWithdrawId());
        newUserWithdrawEntity.setStatus(Constant.WithdrawStatus.CHECK_FAIL.getValue());
        newUserWithdrawEntity.setCheckTime(new Date());
        newUserWithdrawEntity.setCheckTimeTs(newUserWithdrawEntity.getCheckTime().getTime());
        newUserWithdrawEntity.setCheckUser(userId);
        int update = baseMapper.update(newUserWithdrawEntity, new UpdateWrapper<GmUserWithdrawEntity>()
                .eq("withdrawId", newUserWithdrawEntity.getWithdrawId())
                .eq("status", Constant.WithdrawStatus.VERIFY_SUCCESS.getValue())
        );
        if (update != 1) {
            // 更新异常
            throw new RRException("order check pass update fail");
        }
        // 1.2更新账户余额,解冻余额
        boolean b = userAccountService.withdrawThaw(gmUserWithdraw.getUserId(), gmUserWithdraw.getWithdrawMoney(), gmUserWithdraw.getCurrency());
        if (!b) {
            throw new RRException("order check fail update account fail");
        }
        // 1.3新增账变明细
        // 查询账户余额
        UserAccountEntity userAccountEntity = userAccountService.queryByUserIdAndCur(gmUserWithdraw.getUserId(), gmUserWithdraw.getCurrency());
        UserBalanceDetailEntity userBalanceDetailEntity = new UserBalanceDetailEntity();
        userBalanceDetailEntity.setUserId(gmUserWithdraw.getUserId());
        userBalanceDetailEntity.setCurrency(gmUserWithdraw.getCurrency());
        userBalanceDetailEntity.setAmount(Arith.add(gmUserWithdraw.getWithdrawMoney(), gmUserWithdraw.getServiceFee()));
        userBalanceDetailEntity.setTradeType("17");//提现审核失败，解冻
        userBalanceDetailEntity.setUserBalance(userAccountEntity.getBalance());
        userBalanceDetailEntity.setTradeTime(new Date());
        userBalanceDetailEntity.setTradeTimeTs(userBalanceDetailEntity.getTradeTime().getTime());
        userBalanceDetailEntity.setSourceId(gmUserWithdraw.getWithdrawId());
        userBalanceDetailEntity.setTradeDesc("提现审核失败，解冻");
        boolean b1 = userBalanceDetailService.save(userBalanceDetailEntity);
        if (!b1) {
            throw new RRException(ErrorCode.WITHDRAW_INSERT_BALANCE_DETAIL_FAIL.getDesc());
        }
    }

    @Override
    public List<GmUserWithdrawEntity> queryOrderByStatus(Integer status) {
        return baseMapper.selectList(new QueryWrapper<GmUserWithdrawEntity>()
                .eq("status", status)
        );
    }

    @Override
    public void transfer(GmUserWithdrawEntity gmUserWithdrawEntity) throws IOException, ExecutionException, InterruptedException {
        // 1.查询用户
        UserEntity userEntity = userService.queryByUserId(gmUserWithdrawEntity.getUserId());
        // 2.调用智能合约进行转账
        //发送方地址
        String from = payerWalletAddress;
        //发送方私钥
        String privateKey = payerWalletAddressKey;
        //接收者地址
        String to = userEntity.getAddress();
        //出款合约地址
        String coinAddress = fundPoolAddress;
        //busd合约地址
        String busdAddress = busdTokenAddress;
        //查询地址交易编号随机数
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        //支付的矿工费
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        //旷工费限制
        BigInteger gasLimit = new BigInteger("210000");
        //秘钥凭证
        Credentials credentials = Credentials.create(privateKey);

        BigInteger amountWei = Convert.toWei(Arith.subtract(gmUserWithdrawEntity.getWithdrawMoney(),gmUserWithdrawEntity.getServiceFee()), Convert.Unit.ETHER).toBigInteger();
        //封装转账交易
        Function function = new Function(
                "transfer",
                Arrays.<Type>asList(
                        new Address(busdAddress),
                        new Address(to),
                        new Uint256(amountWei)
                ),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        //签名交易
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        GmUserWithdrawEntity newWithdraw = new GmUserWithdrawEntity();
        newWithdraw.setWithdrawId(gmUserWithdrawEntity.getWithdrawId());
        newWithdraw.setStatus(Constant.WithdrawStatus.ING.getValue());
        newWithdraw.setUpdateTime(new Date());
        newWithdraw.setUpdateTimeTs(newWithdraw.getUpdateTime().getTime());
        int update = baseMapper.update(newWithdraw, new UpdateWrapper<GmUserWithdrawEntity>()
                .eq("withdraw_id", newWithdraw.getWithdrawId())
                .eq("status", Constant.WithdrawStatus.CHECK_SUCCESS.getValue())
        );
        if (update != 1) {
            throw new RRException("更新订单状态失败" + newWithdraw.getWithdrawId());
        }
        //广播交易
        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getResult();
        GmUserWithdrawEntity newWithdraw2 = new GmUserWithdrawEntity();
        newWithdraw2.setWithdrawId(gmUserWithdrawEntity.getWithdrawId());
        newWithdraw2.setWithdrawHash(hash);
        int update2 = baseMapper.update(newWithdraw2, new UpdateWrapper<GmUserWithdrawEntity>()
                .eq("withdraw_id", newWithdraw2.getWithdrawId())
        );
    }

    @Override
    public void confirmTransfer(GmUserWithdrawEntity gmUserWithdrawEntity) throws IOException {
        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(gmUserWithdrawEntity.getWithdrawHash()).send();
        Optional<TransactionReceipt> transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt();
        if (transactionReceipt.isPresent()) {
            TransactionReceipt receipt = transactionReceipt.get();
            String status = receipt.getStatus();
            if ("0x1".equals(status)) {
                // 成功
                // 1.更新订单状态
                GmUserWithdrawEntity newGmUserWithdrawEntity = new GmUserWithdrawEntity();
                newGmUserWithdrawEntity.setWithdrawId(gmUserWithdrawEntity.getWithdrawId());
                newGmUserWithdrawEntity.setConfirmTime(new Date());
                newGmUserWithdrawEntity.setConfirmTimeTs(newGmUserWithdrawEntity.getConfirmTime().getTime());
                newGmUserWithdrawEntity.setStatus(Constant.WithdrawStatus.SUCCESS.getValue());
                int update = baseMapper.update(newGmUserWithdrawEntity, new UpdateWrapper<GmUserWithdrawEntity>()
                        .eq("withdraw_id", newGmUserWithdrawEntity.getWithdrawId())
                        .eq("status", Constant.WithdrawStatus.ING.getValue())
                );
                if (update != 1) {
                    throw new RRException("confirmTransfer 更新订单状态失败" + gmUserWithdrawEntity.getWithdrawId());
                }
                // 2.更新账户余额
                boolean b = userAccountService.withdrawSuccess(gmUserWithdrawEntity.getUserId(), gmUserWithdrawEntity.getWithdrawMoney(), gmUserWithdrawEntity.getCurrency());
                if (!b) {
                    throw new RRException("confirmTransfer 更新账户余额失败" + gmUserWithdrawEntity.getWithdrawId());
                }
                // 3.插入账变明细
                // 查询账户余额
                UserAccountEntity userAccountEntity = userAccountService.queryByUserIdAndCur(gmUserWithdrawEntity.getUserId(), gmUserWithdrawEntity.getCurrency());
                UserBalanceDetailEntity userBalanceDetailEntity = new UserBalanceDetailEntity();
                userBalanceDetailEntity.setUserId(gmUserWithdrawEntity.getUserId());
                userBalanceDetailEntity.setCurrency(gmUserWithdrawEntity.getCurrency());
                userBalanceDetailEntity.setAmount(Arith.add(gmUserWithdrawEntity.getWithdrawMoney(), gmUserWithdrawEntity.getServiceFee()));
                userBalanceDetailEntity.setTradeType("00");//提现
                userBalanceDetailEntity.setUserBalance(userAccountEntity.getBalance());
                userBalanceDetailEntity.setTradeTime(new Date());
                userBalanceDetailEntity.setTradeTimeTs(userBalanceDetailEntity.getTradeTime().getTime());
                userBalanceDetailEntity.setSourceId(gmUserWithdrawEntity.getWithdrawId());
                userBalanceDetailEntity.setTradeDesc("提现成功");
                boolean b1 = userBalanceDetailService.save(userBalanceDetailEntity);
                if (!b1) {
                    throw new RRException(ErrorCode.WITHDRAW_INSERT_BALANCE_DETAIL_FAIL.getDesc());
                }
            } else if ("0x0".equals(status)) {
                // 失败
                // 1.更新订单状态
                GmUserWithdrawEntity newGmUserWithdrawEntity = new GmUserWithdrawEntity();
                newGmUserWithdrawEntity.setWithdrawId(gmUserWithdrawEntity.getWithdrawId());
                newGmUserWithdrawEntity.setConfirmTime(new Date());
                newGmUserWithdrawEntity.setConfirmTimeTs(newGmUserWithdrawEntity.getConfirmTime().getTime());
                newGmUserWithdrawEntity.setStatus(Constant.WithdrawStatus.FAIL.getValue());
                int update = baseMapper.update(newGmUserWithdrawEntity, new UpdateWrapper<GmUserWithdrawEntity>()
                        .eq("withdraw_id", newGmUserWithdrawEntity.getWithdrawId())
                        .eq("status", Constant.WithdrawStatus.ING.getValue())
                );
                if (update != 1) {
                    throw new RRException("confirmTransfer 更新订单状态失败" + gmUserWithdrawEntity.getWithdrawId());
                }
                // 2.更新账户余额
                boolean b = userAccountService.withdrawFail(gmUserWithdrawEntity.getUserId(), gmUserWithdrawEntity.getWithdrawMoney(), gmUserWithdrawEntity.getCurrency());
                if (!b) {
                    throw new RRException("confirmTransfer 更新账户余额失败" + gmUserWithdrawEntity.getWithdrawId());
                }
                // 3.插入账变明细
                // 查询账户余额
                UserAccountEntity userAccountEntity = userAccountService.queryByUserIdAndCur(gmUserWithdrawEntity.getUserId(), gmUserWithdrawEntity.getCurrency());
                UserBalanceDetailEntity userBalanceDetailEntity = new UserBalanceDetailEntity();
                userBalanceDetailEntity.setUserId(gmUserWithdrawEntity.getUserId());
                userBalanceDetailEntity.setCurrency(gmUserWithdrawEntity.getCurrency());
                userBalanceDetailEntity.setAmount(Arith.add(gmUserWithdrawEntity.getWithdrawMoney(), gmUserWithdrawEntity.getServiceFee()));
                userBalanceDetailEntity.setTradeType("17");//提现失败，解冻
                userBalanceDetailEntity.setUserBalance(userAccountEntity.getBalance());
                userBalanceDetailEntity.setTradeTime(new Date());
                userBalanceDetailEntity.setTradeTimeTs(userBalanceDetailEntity.getTradeTime().getTime());
                userBalanceDetailEntity.setSourceId(gmUserWithdrawEntity.getWithdrawId());
                userBalanceDetailEntity.setTradeDesc("提现失败，解冻");
                boolean b1 = userBalanceDetailService.save(userBalanceDetailEntity);
                if (!b1) {
                    throw new RRException(ErrorCode.WITHDRAW_INSERT_BALANCE_DETAIL_FAIL.getDesc());
                }
            }
        }
    }

    @Override
    public void applyWithdraw(UserEntity user, UseWithdrawReq useWithdrawReq) {
        // 生成提现订单，状态为待出款
        GmUserWithdrawEntity gmUserWithdraw = new GmUserWithdrawEntity();
        gmUserWithdraw.setCreateTime(new Date());
        gmUserWithdraw.setCreateTimeTs(gmUserWithdraw.getCreateTime().getTime());
        gmUserWithdraw.setStatus(Constant.WithdrawStatus.APPLY.getValue());
        gmUserWithdraw.setUserId(user.getUserId());
        gmUserWithdraw.setWithdrawMoney(useWithdrawReq.getWithdrawMoney());
        gmUserWithdraw.setCurrency(useWithdrawReq.getWithdrawType());
        gmUserWithdraw.setApplyWithdrawGas(useWithdrawReq.getApplyWithdrawGas());
        gmUserWithdraw.setApplyWithdrawHash(useWithdrawReq.getRefundHash());
        int insert = baseMapper.insert(gmUserWithdraw);
    }

    @Override
    public void withdrawApplyVerify(GmUserWithdrawEntity gmUserWithdrawEntity) {
        GmUserWithdrawEntity newGmUserWithdrawEntity = new GmUserWithdrawEntity();
        newGmUserWithdrawEntity.setWithdrawId(gmUserWithdrawEntity.getWithdrawId());
        boolean verify = true;
        // 查询用户
        UserEntity userEntity = userService.queryByUserId(gmUserWithdrawEntity.getUserId());
        // 1.查询校验该会员上次提现时间
//        GmUserWithdrawEntity lastWithdraw = lastWithdraw(gmUserWithdrawEntity.getUserId());
//        if (lastWithdraw != null) {
//            Date date = DateUtils.addDateHours(lastWithdraw.getCreateTime(), 24);// 上次提现时间加24小时，然后和当前时间做比较
//            if (date.after(new Date())) {// 24小时只能发起一次提现
//                newGmUserWithdrawEntity.setStatus(Constant.WithdrawStatus.VERIFY_FAIL.getValue());
//                newGmUserWithdrawEntity.setApplyRemark("24 hours only once!");
//                verify = false;
//            }
//        }
        // 查询该会员消费等级
        GmUserVipLevelEntity gmUserVipLevel = gmUserVipLevelService.getById(userEntity.getVipLevelId());
        gmUserWithdrawEntity.setServiceFee(new BigDecimal(gmUserVipLevel.getWithdrawHandlingFee()));
        // 冻结账号余额
//        BigDecimal frozeMoney = Arith.add(gmUserWithdrawEntity.getWithdrawMoney(), gmUserWithdrawEntity.getServiceFee());
        // 2.查询校验该会员当前余额
        UserAccountEntity userAccountEntity = userAccountService.queryByUserIdAndCur(userEntity.getUserId(), gmUserWithdrawEntity.getCurrency());
        if (userAccountEntity.getBalance() * gmUserVipLevel.getWithdrawLimit() < gmUserWithdrawEntity.getWithdrawMoney().doubleValue()) {
            // 提现超额
            newGmUserWithdrawEntity.setStatus(Constant.WithdrawStatus.VERIFY_FAIL.getValue());
            newGmUserWithdrawEntity.setApplyRemark("withdraw over!");
            verify = false;
        }
        // 校验通过，修改订单状态
        if (verify) {
            newGmUserWithdrawEntity.setStatus(Constant.WithdrawStatus.VERIFY_SUCCESS.getValue());
            newGmUserWithdrawEntity.setServiceFee(new BigDecimal(gmUserVipLevel.getWithdrawHandlingFee()));
        }
        boolean b = update(newGmUserWithdrawEntity, new UpdateWrapper<GmUserWithdrawEntity>()
                .eq("withdraw_id", newGmUserWithdrawEntity.getWithdrawId())
                .eq("status", Constant.WithdrawStatus.APPLY.getValue())
        );
        if (!b) {
            throw new RRException("更新订单失败！");
        }
        if (!verify) {
            return;
        }
        // 冻结
        boolean b2 = userAccountService.withdrawFreeze(userEntity.getUserId(), gmUserWithdrawEntity.getWithdrawMoney(), gmUserWithdrawEntity.getCurrency());
        if (!b2) {
            throw new RRException(ErrorCode.WITHDRAW_UPDATE_ACCOUNT_FAIL.getDesc());
        }
        // 插入账变明细
        // 查询账户余额
        UserAccountEntity userAccount = userAccountService.queryByUserIdAndCur(userEntity.getUserId(), gmUserWithdrawEntity.getCurrency());
        UserBalanceDetailEntity userBalanceDetailEntity = new UserBalanceDetailEntity();
        userBalanceDetailEntity.setUserId(userEntity.getUserId());
        userBalanceDetailEntity.setCurrency(gmUserWithdrawEntity.getCurrency());
        userBalanceDetailEntity.setAmount(gmUserWithdrawEntity.getWithdrawMoney());
        userBalanceDetailEntity.setTradeType("16");//申请提现冻结
        userBalanceDetailEntity.setUserBalance(userAccount.getBalance());
        userBalanceDetailEntity.setTradeTime(new Date());
        userBalanceDetailEntity.setTradeTimeTs(userBalanceDetailEntity.getTradeTime().getTime());
        userBalanceDetailEntity.setSourceId(gmUserWithdrawEntity.getWithdrawId());
        userBalanceDetailEntity.setTradeDesc("申请提现冻结");
        boolean b1 = userBalanceDetailService.save(userBalanceDetailEntity);
        if (!b1) {
            throw new RRException(ErrorCode.WITHDRAW_INSERT_BALANCE_DETAIL_FAIL.getDesc());
        }
        if (1 == gmUserVipLevel.getNeedCheck()) {
            // 不需要审核
            checkPass(gmUserWithdrawEntity, 1L);
        }
    }

    public static void main(String[] args) throws IOException {
//        String hash = "0xc757ef40930cb2c344ad59e46c64d3eeb34904c65f4f2da48ac97a15d21298a4";
        String hash = "0x6eb1475d06b46e67b26e770146a0612720bfec2162f3e0e602b41d5ad3b3e021";
        Web3j web3j = TransactionVerifyUtils.connect();
//        EthTransaction send = web3j.ethGetTransactionByHash(hash).send();
//{"jsonrpc":"2.0","id":0,"result":{"blockHash":"0x774ea2461c184fb9efa985f5cc1149724d74eccfbe65a7ae91109dc9f92ec5a7","blockNumber":"0x17d2b42","from":"0xaa25aa7a19f9c426e07dee59b12f944f4d9f1dd3","gas":"0x5208","gasPrice":"0x430e23400","hash":"0xc757ef40930cb2c344ad59e46c64d3eeb34904c65f4f2da48ac97a15d21298a4","input":"0x","nonce":"0x646314","to":"0x89394dd3903ae07723012292ddb1f5ca1b6bce45","transactionIndex":"0x0","value":"0x6f05b59d3b20000","type":"0x0","v":"0xe6","r":"0x69a9788699c1be77bb88e219742c321d33e57271c8a9c90bc51c5e6e3815f72b","s":"0x31ebcfaf76bb407255e93e9722bbe7236d445482cfda59945793155ca95f5284"}}
//{"jsonrpc":"2.0","id":0,"result":{"blockHash":"0x1dde6b1f5ad062d03d3f4618c1a984eba64f0ac7ceb00b46bc9aeb29bea531f6","blockNumber":"0x1803967","from":"0xaa25aa7a19f9c426e07dee59b12f944f4d9f1dd3","gas":"0x5208","gasPrice":"0x430e23400","hash":"0x6810d6daa686237f67be5d4439e760cf1d11d1b2bab09a39b5e99bdd7357789b","input":"0x","nonce":"0x656ccc","to":"0xb2bf67468170f1b8f32f29011c2e9ab302d80749","transactionIndex":"0x2","value":"0x6f05b59d3b20000","type":"0x0","v":"0xe6","r":"0x81366c481d587eaef0fe00c0f26ca1959a20a53240a308a002324134fa98784d","s":"0x6b2d60013d57de8752f931b8a34f2afa7404d58bded90e85d41c1bfd28a20183"}}
//        EthGetTransactionReceipt send1 = web3j.ethGetTransactionReceipt(hash).send();
//{"jsonrpc":"2.0","id":1,"result":{"blockHash":"0x774ea2461c184fb9efa985f5cc1149724d74eccfbe65a7ae91109dc9f92ec5a7","blockNumber":"0x17d2b42","contractAddress":null,"cumulativeGasUsed":"0x5208","effectiveGasPrice":"0x430e23400","from":"0xaa25aa7a19f9c426e07dee59b12f944f4d9f1dd3","gasUsed":"0x5208","logs":[],"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000","status":"0x1","to":"0x89394dd3903ae07723012292ddb1f5ca1b6bce45","transactionHash":"0xc757ef40930cb2c344ad59e46c64d3eeb34904c65f4f2da48ac97a15d21298a4","transactionIndex":"0x0","type":"0x0"}}
//{"jsonrpc":"2.0","id":1,"result":{"blockHash":"0x1dde6b1f5ad062d03d3f4618c1a984eba64f0ac7ceb00b46bc9aeb29bea531f6","blockNumber":"0x1803967","contractAddress":null,"cumulativeGasUsed":"0x48569","effectiveGasPrice":"0x430e23400","from":"0xaa25aa7a19f9c426e07dee59b12f944f4d9f1dd3","gasUsed":"0x5208","logs":[],"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000","status":"0x0","to":"0xb2bf67468170f1b8f32f29011c2e9ab302d80749","transactionHash":"0x6810d6daa686237f67be5d4439e760cf1d11d1b2bab09a39b5e99bdd7357789b","transactionIndex":"0x2","type":"0x0"}}
//        System.out.println(111);

//        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(hash).send();
//        Optional<TransactionReceipt> transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt();
//        boolean present = transactionReceipt.isPresent();
//        System.out.println(present);
//        String s = transactionReceipt.toString();
//        System.out.println(s);
//        if (transactionReceipt.isPresent()) {
//            String status = transactionReceipt.get().getStatus();
//            System.out.println(status);
//        }
        // 0x000000000000000000000000000000000000000000000000000000006391911d
        //   0000000000000000000000000000000000000000000000000004ccc5d752e400
        //   00000000000000000000000000000000000000000000000000016bcc41e90000

        String data = "0x000000000000000000000000000000000000000000000000000000006391911d0000000000000000000000000000000000000000000000000004ccc5d752e40000000000000000000000000000000000000000000000000000016bcc41e90000";
//        String data = receipt.getLogs().get(0).getData();
        String one = data.substring(0, 66);// 时间戳
        String two = data.substring(66, 130);// gas费
        String there = data.substring(130, 194);// 提款金额
        BigInteger oneBigInteger = Numeric.toBigInt(one);
        BigDecimal twoBigDecimal = Convert.fromWei(Numeric.toBigInt("0x" + two).toString(), Convert.Unit.ETHER);
        BigDecimal thereBigDecimal = Convert.fromWei(Numeric.toBigInt("0x" + there).toString(), Convert.Unit.ETHER);

        System.out.println(oneBigInteger);
        System.out.println(twoBigDecimal);
        System.out.println(thereBigDecimal);

    }
}
