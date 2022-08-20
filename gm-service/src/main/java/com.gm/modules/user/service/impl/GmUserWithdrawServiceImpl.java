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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;



@Service("gmUserWithdrawService")
@Transactional
public class GmUserWithdrawServiceImpl extends ServiceImpl<GmUserWithdrawDao, GmUserWithdrawEntity> implements GmUserWithdrawService {
    private Web3j web3j = TransactionVerifyUtils.connect();

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
    public void withdraw(UserEntity user, UseWithdrawReq useWithdrawReq) {
        // 1.生成提现订单，状态为待出款
        GmUserWithdrawEntity gmUserWithdraw = new GmUserWithdrawEntity();
        gmUserWithdraw.setCreateTime(new Date());
        gmUserWithdraw.setCreateTimeTs(System.currentTimeMillis());
        gmUserWithdraw.setStatus(0);
        gmUserWithdraw.setUserId(user.getUserId());
        gmUserWithdraw.setWithdrawMoney(new BigDecimal(useWithdrawReq.getWithdrawMoney()));
        int insert = baseMapper.insert(gmUserWithdraw);
        // 2.调用智能合约进行转账


    }


}
