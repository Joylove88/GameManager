package com.gm.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.user.dao.GmUserWithdrawDao;
import com.gm.modules.user.entity.GmUserWithdrawEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.UseWithdrawReq;
import com.gm.modules.user.service.GmUserWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Service("gmUserWithdrawService")
@Transactional
public class GmUserWithdrawServiceImpl extends ServiceImpl<GmUserWithdrawDao, GmUserWithdrawEntity> implements GmUserWithdrawService {
    private Web3j web3j = TransactionVerifyUtils.connect();

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

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
    public void withdraw(UserEntity user, UseWithdrawReq useWithdrawReq) throws ExecutionException, InterruptedException, IOException {
        // 1.生成提现订单，状态为待出款
        GmUserWithdrawEntity gmUserWithdraw = new GmUserWithdrawEntity();
        gmUserWithdraw.setCreateTime(new Date());
        gmUserWithdraw.setCreateTimeTs(System.currentTimeMillis());
        gmUserWithdraw.setStatus(0);
        gmUserWithdraw.setUserId(user.getUserId());
        gmUserWithdraw.setWithdrawMoney(new BigDecimal(useWithdrawReq.getWithdrawMoney()));
        // 2.调用智能合约进行转账
        //发送方地址
        String from = "0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45";
        //发送方私钥
        String privateKey = "0xf94a3b8f8b5b7d6ed5a8a77a60252de6adc867629bfe2316b495c05e15017e54";
        //转账数量
        String amount = useWithdrawReq.getWithdrawMoney();
        //接收者地址
        String to = user.getAddress();
        //出款合约地址
        String coinAddress = "0x4d9e7fb8503b72b9aD8fc94366899aa99Ab89807";
        //usdt合约地址
        String usdtAddress = "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd";
        //查询地址交易编号随机数
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        //支付的矿工费
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        //旷工费限制
        BigInteger gasLimit = new BigInteger("21000");
        //秘钥凭证
        Credentials credentials = Credentials.create(privateKey);

        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        //封装转账交易
        Function function = new Function(
                "transfer",
                Arrays.<Type>asList(
                        new Address(usdtAddress),
                        new Address(to),
                        new Uint256(amountWei)
                ),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        //签名交易
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        //广播交易
        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
        gmUserWithdraw.setWithdrawHash(hash);
        int insert = baseMapper.insert(gmUserWithdraw);
        // 定时任务去刷新交易状态


    }

}
