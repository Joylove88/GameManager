/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.modules.job.task;

import com.alibaba.fastjson.JSONObject;
import com.gm.common.utils.Constant;
import com.gm.common.web3Utils.TransactionVerifyUtils;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.ethTransfer.service.EthTransferService;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.sys.service.SysDictService;
import com.gm.modules.user.req.SummonReq;
import com.gm.modules.user.service.UserService;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 定时器监听链上
 * @author axiang
 */
@Component("ethTransferListenTask")
public class EthTransferListenTask implements ITask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static Disposable ethMissSubscription; //ETH交易空档事件订阅对象
	private static int block_EthMissSub = 0;
	private static Map<String,Object> contractsAddress = new HashMap<>();//代币合约地址列表,可以存放多个地址
	private static Web3j web3j;
	/**
	 * 英雄合约地址
	 */
	private static String NFT_HERO_ADDRESS;
	/**
	 * 装备合约地址
	 */
	private static String NFT_EQUIP_ADDRESS;
	/**
	 * 经验合约地址
	 */
	private static String NFT_EX_ADDRESS;

	@Autowired
	private SysConfigService sysConfigService;
	@Autowired
	private UserService userService;
	@Autowired
	private TransactionOrderService transactionOrderService;
	@Autowired
	private DrawGiftService drawGiftService;
	@Autowired
	private SysDictService sysDictService;
	@Autowired
	private EthTransferService ethTransferService;

	@Override
	public void run(String params){
		logger.info("ethTransferListenTask定时任务正在执行，参数为：{}", params);
		try {
			// 获取地址
			JSONObject address = sysDictService.getContractsAddress("CONTRACTS", "ADDRESS");
			NFT_HERO_ADDRESS = address.getString("NFT_HERO_ADDRESS");
			NFT_EQUIP_ADDRESS = address.getString("NFT_EQUIP_ADDRESS");
			NFT_EX_ADDRESS = address.getString("NFT_EX_ADDRESS");

			contractsAddress.put(NFT_HERO_ADDRESS.toLowerCase(), NFT_HERO_ADDRESS);
			contractsAddress.put(NFT_EQUIP_ADDRESS.toLowerCase(), NFT_EQUIP_ADDRESS);
			contractsAddress.put(NFT_EX_ADDRESS.toLowerCase(), NFT_EX_ADDRESS);

			// 获取系统中的区块号
			String blockNum = sysConfigService.getValue(Constant.BLOCK_NUMBER);
			// 开始链上监听
			startReplayListen_ETH(BigInteger.valueOf(Long.parseLong(blockNum)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void run() throws Exception {
		web3j = TransactionVerifyUtils.connect();

	}

	public static void main(String[] args) throws Exception {
		String address = "0x25B15dE515eBBD047e026D64463801f044785cc6";
		String fromAddress = "0x1cabea67c565b5337e688894960839ef1D48b0cD";
		contractsAddress.put(address.toLowerCase(),address);
		contractsAddress.put(fromAddress.toLowerCase(),fromAddress);
		//获取起始区块号
//		startReplayListen_ETH(BigInteger.valueOf(17723527));
//        startTransferListen_Token(BigInteger.valueOf(17720623));
//        startTransactionListen_ETH();
	}

	// 启动监听以太坊上的过往交易
	private void startReplayListen_ETH(BigInteger startBlockNum) throws Exception {
		// 启动区块链
		run();
		logger.info("  startReplayListen_ETH:  startBlockNum="+startBlockNum);
		// 回放空档期间的交易
		BigInteger currentBlockNum=null;
		try {
			// 获取当前区块号
			currentBlockNum = web3j.ethBlockNumber().send().getBlockNumber();
			logger.info("  000 currentBlockNum= "+currentBlockNum.intValue());
			if(startBlockNum.compareTo(currentBlockNum) > 0) {
				return;  //测试曾经出现 currentBlockNum得到错误数字，比startBlockNum还小，这时不能启动监听
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("  111 getBlockNumber() Error: ");
			e.printStackTrace();
			return;   //出现异常不能启动监听
		}

		// 创建开始与结束区块， 重放这段时间内的交易，防止遗漏
		DefaultBlockParameter startBlock = new DefaultBlockParameterNumber(startBlockNum);
		DefaultBlockParameter endBlock = new DefaultBlockParameterNumber(currentBlockNum);
		logger.info("[ startTransferListen_ETH:  miss  startBlock="+startBlockNum+", endBlock="+currentBlockNum+"]");
		block_EthMissSub = startBlockNum.intValue();
		BigInteger finalCurrentBlockNum = currentBlockNum;
		ethMissSubscription = web3j.replayPastTransactionsFlowable(startBlock, endBlock)
				.subscribe(tx -> {
					// 更新检查过的区块高度
					block_EthMissSub = tx.getBlockNumber().intValue();
					// 系统设置保存区块号
					setBlock_Num(block_EthMissSub);
					logger.info("  ---replayPastTransactionsFlowable    block_EthMissSub = "+block_EthMissSub);
					String fromAddress = tx.getFrom();
					String toAddress   = tx.getTo();
//					System.out.println("fromAddress="+fromAddress + "toAddress="+toAddress);
					if(contractsAddress.containsKey(fromAddress) || contractsAddress.containsKey(toAddress)) {  //发现了指定地址上的交易
						String txHash = tx.getHash();
						BigDecimal value = Convert.fromWei(tx.getValue().toString(), Convert.Unit.ETHER);
						String timestamp = "";
						try {
							EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(tx.getBlockNumber()), false).send();
							timestamp = String.valueOf(ethBlock.getBlock().getTimestamp());
						} catch (IOException e) {
							logger.error("Block timestamp get failure,block number is {}", tx.getBlockNumber());
							logger.error("Block timestamp get failure,{}", e.getMessage());

						}
						// 监听以太坊上是否有系统生成地址的交易
						callBack_ETH(txHash,fromAddress,toAddress,value,timestamp);
					}
					// 如果当前执行的区块号等于获取的最新区块号 将停止运行。
					if(block_EthMissSub == finalCurrentBlockNum.intValue()){
						web3j.shutdown();
					}
				}, error->{
					logger.error("   ### replayPastTransactionsFlowable  error= ",error);
					error.printStackTrace();
				});


	}

	// txHash转账事件的处理函数
	private void callBack_ETH(String txHash, String from, String to, BigDecimal value, String timestamp) throws Exception {
		logger.info("txHash = "+txHash);
		logger.info("from = "+from);
		logger.info("to = "+to);
		logger.info("value = "+value.doubleValue());
		Optional<TransactionReceipt> receipt = TransactionVerifyUtils.isVerify(web3j,txHash);
		ethTransferService.eth(txHash, null, receipt, new SummonReq());
	}

	// 更新系统中保存的区块号
	private void setBlock_Num(int blockNum){
		sysConfigService.updateValueByKey(Constant.BLOCK_NUMBER, String.valueOf(blockNum));
	}
}
