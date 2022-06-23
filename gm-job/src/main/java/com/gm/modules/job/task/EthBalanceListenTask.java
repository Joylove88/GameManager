/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.job.task;

import com.gm.common.utils.Constant;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.service.UserService;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
	@Autowired
	private SysConfigService sysConfigService;
	
	@Override
	public void run(String params){
		logger.info("ethBalanceListenTask定时任务正在执行，参数为：{}", params);
		try {
			contracts.add(Constant.ADDRESS.toLowerCase());
			contracts.add(Constant.BUSD_ADDRESS.toLowerCase());
			// 开始链上监听
			startReplayListen_ETH();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void run() throws Exception {
		web3j = TransactionVerifyUtils.connect();
	}
	// 启动监听以太坊上的过往交易
	private void startReplayListen_ETH() throws Exception {
		run();
		// 获取资金池余额
		String code = getERC20Balance(web3j,contracts.get(0),contracts.get(1));
		// 更新系统中保存的余额
		sysConfigService.updateValueByKey(Constant.CASH_POOLING_BALANCE, code);
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
		String address = "0x25B15dE515eBBD047e026D64463801f044785cc6";
		String fromAddress = "0x1cabea67c565b5337e688894960839ef1D48b0cD";
		contracts.add(Constant.ADDRESS.toLowerCase());
		contracts.add(Constant.BUSD_ADDRESS.toLowerCase());

//		startReplayListen_ETH();
		//获取起始区块号
//		startReplayListen_ETH(BigInteger.valueOf(17723527));
//        startTransferListen_Token(BigInteger.valueOf(17720623));
//        startTransactionListen_ETH();
	}


}
