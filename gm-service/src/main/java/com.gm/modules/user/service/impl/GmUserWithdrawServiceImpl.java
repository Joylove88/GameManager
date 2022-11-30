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
import com.gm.modules.user.service.GmUserWithdrawService;
import com.gm.modules.user.service.UserAccountService;
import com.gm.modules.user.service.UserBalanceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Service("gmUserWithdrawService")
@Transactional
public class GmUserWithdrawServiceImpl extends ServiceImpl<GmUserWithdrawDao, GmUserWithdrawEntity> implements GmUserWithdrawService {
    private Web3j web3j = TransactionVerifyUtils.connect();

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserBalanceDetailService userBalanceDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmUserWithdrawEntity> page = this.page(
                new Query<GmUserWithdrawEntity>().getPage(params),
                new QueryWrapper<GmUserWithdrawEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public GmUserWithdrawEntity lastWithdraw(UserEntity user) {
        return baseMapper.selectOne(new QueryWrapper<GmUserWithdrawEntity>()
                .eq("user_id", user.getUserId())
                .ne("status", 2)
                .orderByAsc("update_time")
                .last("limit 1")
        );
    }

    @Override
    public void withdraw(UserEntity user, GmUserVipLevelEntity gmUserVipLevel, UseWithdrawReq useWithdrawReq) throws ExecutionException, InterruptedException, IOException {
        // 1.生成提现订单，状态为待出款
        GmUserWithdrawEntity gmUserWithdraw = new GmUserWithdrawEntity();
        gmUserWithdraw.setCreateTime(new Date());
        gmUserWithdraw.setCreateTimeTs(gmUserWithdraw.getCreateTime().getTime());
        gmUserWithdraw.setStatus(Constant.WithdrawStatus.APPLY.getValue());
        gmUserWithdraw.setUserId(user.getUserId());
        gmUserWithdraw.setWithdrawMoney(new BigDecimal(useWithdrawReq.getWithdrawMoney()));
        gmUserWithdraw.setCurrency(useWithdrawReq.getWithdrawType());
        gmUserWithdraw.setServiceFee(new BigDecimal(useWithdrawReq.getWithdrawHandlingFee()));
//        // 2.调用智能合约进行转账
//        //发送方地址
//        String from = "0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45";
//        //发送方私钥
//        String privateKey = "0xf94a3b8f8b5b7d6ed5a8a77a60252de6adc867629bfe2316b495c05e15017e54";
//        //转账数量
//        String amount = useWithdrawReq.getWithdrawMoney();
//        //接收者地址
//        String to = user.getAddress();
//        //出款合约地址
//        String coinAddress = "0x4d9e7fb8503b72b9aD8fc94366899aa99Ab89807";
//        //usdt合约地址
//        String usdtAddress = "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd";
//        //查询地址交易编号随机数
//        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
//        //支付的矿工费
//        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
//        //旷工费限制
//        BigInteger gasLimit = new BigInteger("21000");
//        //秘钥凭证
//        Credentials credentials = Credentials.create(privateKey);
//
//        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
//        //封装转账交易
//        Function function = new Function(
//                "transfer",
//                Arrays.<Type>asList(
//                        new Address(usdtAddress),
//                        new Address(to),
//                        new Uint256(amountWei)
//                ),
//                Collections.<TypeReference<?>>emptyList());
//        String data = FunctionEncoder.encode(function);
//        //签名交易
//        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);
//        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//        //广播交易
//        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
//        gmUserWithdraw.setWithdrawHash(hash);
        int insert = baseMapper.insert(gmUserWithdraw);
        if (insert != 1) {
            throw new RRException(ErrorCode.WITHDRAW_INSERT_FAIL.getDesc());
        }
        // 冻结账号余额
        BigDecimal frozeMoney = Arith.add(new BigDecimal(useWithdrawReq.getWithdrawMoney()), new BigDecimal(useWithdrawReq.getWithdrawHandlingFee()));
        boolean b = userAccountService.withdrawFreeze(user.getUserId(), frozeMoney, useWithdrawReq.getWithdrawType());
        if (!b) {
            throw new RRException(ErrorCode.WITHDRAW_UPDATE_ACCOUNT_FAIL.getDesc());
        }
        // 插入账变明细
        // 查询账户余额
        UserAccountEntity userAccountEntity = userAccountService.queryByUserIdAndCur(user.getUserId(), useWithdrawReq.getWithdrawType());
        UserBalanceDetailEntity userBalanceDetailEntity = new UserBalanceDetailEntity();
        userBalanceDetailEntity.setUserId(user.getUserId());
        userBalanceDetailEntity.setCurrency(useWithdrawReq.getWithdrawType());
        userBalanceDetailEntity.setAmount(frozeMoney);
        userBalanceDetailEntity.setTradeType("16");//申请提现冻结
        userBalanceDetailEntity.setUserBalance(userAccountEntity.getBalance());
        userBalanceDetailEntity.setTradeTime(new Date());
        userBalanceDetailEntity.setTradeTimeTs(userBalanceDetailEntity.getTradeTime().getTime());
        userBalanceDetailEntity.setSourceId(gmUserWithdraw.getWithdrawId());
        userBalanceDetailEntity.setTradeDesc("申请提现冻结");
        boolean b1 = userBalanceDetailService.save(userBalanceDetailEntity);
        if (!b1) {
            throw new RRException(ErrorCode.WITHDRAW_INSERT_BALANCE_DETAIL_FAIL.getDesc());
        }
        if (1==gmUserVipLevel.getNeedCheck()){
            // 不需要审核
            checkPass(gmUserWithdraw,1L);
        }
    }

    @Override
    public boolean haveApplyWithdrawOrder(Long userId) {
        GmUserWithdrawEntity gmUserWithdrawEntity = baseMapper.selectOne(new QueryWrapper<GmUserWithdrawEntity>()
                .eq("user_id", userId)
                .ne("status", Constant.WithdrawStatus.APPLY)
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
                .eq("withdrawId", newUserWithdrawEntity.getWithdrawId())
                .eq("status", Constant.WithdrawStatus.APPLY.getValue())
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
                .eq("status", Constant.WithdrawStatus.APPLY.getValue())
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

}
