/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.gm.modules.job.task;

import com.alibaba.fastjson.JSONObject;
import com.gm.common.utils.Constant;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.sys.service.SysDictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * 定时器监听链上余额
 * @author axiang
 */
@Component("ethBalanceListenTask")
public class EthBalanceListenTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static List<String> contracts = new ArrayList<>();  //代币合约地址列表,可以存放多个地址
    private static Web3j web3j;

    @Value("${contractAddress.fundPoolAddress:#{null}}")
    private String fundPoolAddress;
    @Value("${contractAddress.busdTokenAddress:#{null}}")
    private String busdTokenAddress;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private TransactionVerifyUtils transactionVerifyUtils;

    @Override
    public void run(String params) {
        logger.info("ethBalanceListenTask定时任务正在执行，参数为：{}", params);
        try {
            // 获取地址
            contracts.add(fundPoolAddress.toLowerCase());
            contracts.add(busdTokenAddress.toLowerCase());
            // 开始链上监听
            startReplayListen_ETH();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void run() throws Exception {
        web3j = transactionVerifyUtils.connect();
    }

    // 启动监听以太坊上的过往交易
    private void startReplayListen_ETH() throws Exception {
        run();
        // 获取资金池余额
        BigDecimal balanceValue = new BigDecimal(getERC20Balance(web3j, contracts.get(0), contracts.get(1)));
        if (balanceValue.compareTo(BigDecimal.ZERO) == 1) {
            // 更新系统中保存的余额
            sysConfigService.updateValueByKey(Constant.CashPool._MAIN.getValue(), balanceValue.toString());
        }
    }

    /**
     * 获取ERC-20 token指定地址余额
     *
     * @param address         查询地址
     * @param contractAddress 合约地址
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static String getERC20Balance(Web3j web3j, String address, String contractAddress) throws ExecutionException, InterruptedException {
        String methodName = "balanceOf";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address fromAddress = new Address(address);
        inputParameters.add(fromAddress);

        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(address, contractAddress, data);

        EthCall ethCall;
        BigDecimal balanceValue = BigDecimal.ZERO;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            BigDecimal value = BigDecimal.valueOf(0);
            if (results != null && results.size() > 0) {
                String s = String.valueOf(results.get(0).getValue());
                value = new BigDecimal(s);
            }
            balanceValue = Convert.fromWei(value, Convert.Unit.ETHER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balanceValue.toString();
    }

    public static void main(String[] args) throws Exception {
//        run();
//        contracts.add("0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45");
//        contracts.add("0xeD24FC36d5Ee211Ea25A80239Fb8C4Cfd80f12Ee");
//        BigDecimal balanceValue = new BigDecimal(getERC20Balance(web3j, contracts.get(0), contracts.get(1)));
//        if (balanceValue.compareTo(BigDecimal.ZERO) == 1) {
//            System.out.println(balanceValue);
//        }
//		String address = "0x25B15dE515eBBD047e026D64463801f044785cc6";
//		String fromAddress = "0x1cabea67c565b5337e688894960839ef1D48b0cD";
//        Address fromAddress = new Address("0x000000000000000000000000000000000000000000000000002386f26fc10000");
//        System.out.println(fromAddress);
//        System.out.println(Convert.fromWei(Numeric.toBigInt("0x000000000000000000000000000000000000000000000000002386f26fc10000").toString(), Convert.Unit.ETHER));
//		contracts.add(Constant.ADDRESS.toLowerCase());
//		contracts.add(Constant.BUSD_ADDRESS.toLowerCase());

//		startReplayListen_ETH();
        //获取起始区块号
//		startReplayListen_ETH(BigInteger.valueOf(17723527));
//        startTransferListen_Token(BigInteger.valueOf(17720623));
//        startTransactionListen_ETH();
    }


}
