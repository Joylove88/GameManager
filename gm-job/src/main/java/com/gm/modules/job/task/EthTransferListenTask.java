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
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.entity.UserEntity;
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
import java.lang.reflect.InvocationTargetException;
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
	private static List<String> contracts = new ArrayList<>();  //代币合约地址列表,可以存放多个地址
	private static Disposable ethMissSubscription; //ETH交易空档事件订阅对象
	private static int block_EthMissSub = 0;
	private static Map<String,Object> htAddress = new HashMap<>();
	private static Web3j web3j;
	@Autowired
	private SysConfigService sysConfigService;
	@Autowired
	private UserService userService;
	@Autowired
	private TransactionOrderService transactionOrderService;
	@Autowired
	private DrawGiftService drawGiftService;

	@Override
	public void run(String params){
		logger.info("ethTransferListenTask定时任务正在执行，参数为：{}", params);
		try {
			contracts.add(Constant.NFT_HERO_ADDRESS.toLowerCase());
			contracts.add(Constant.NFT_EQUIP_ADDRESS.toLowerCase());
			contracts.add(Constant.NFT_EX_ADDRESS.toLowerCase());
			htAddress.put(Constant.NFT_HERO_ADDRESS.toLowerCase(),Constant.NFT_HERO_ADDRESS);
			htAddress.put(Constant.NFT_EQUIP_ADDRESS.toLowerCase(),Constant.NFT_EQUIP_ADDRESS);
			htAddress.put(Constant.NFT_EX_ADDRESS.toLowerCase(),Constant.NFT_EX_ADDRESS);
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
		contracts.add(address);
		htAddress.put(address.toLowerCase(),address);
		htAddress.put(fromAddress.toLowerCase(),fromAddress);
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
					if(htAddress.containsKey(fromAddress) || htAddress.containsKey(toAddress)) {  //发现了指定地址上的交易
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
		System.out.println("----------------------------------------------------------------------------------------------------------------------    txHash = "+txHash);
		System.out.println("----------------------------------------------------------------------------------------------------------------------    from = "+from);
		System.out.println("----------------------------------------------------------------------------------------------------------------------    to = "+to);
		System.out.println("----------------------------------------------------------------------------------------------------------------------    value = "+value.doubleValue());
		Optional<TransactionReceipt> receipt = TransactionVerifyUtils.isVerify(web3j,txHash);
		UserEntity user = new UserEntity();
		Map<String,Object> map = new HashMap<>();
		List gifts = new ArrayList();
		if (receipt !=null){
			// token交易数量(抽奖次数)
			int tokenNum = 0;
			// 类型1英雄，2装备，3道具
			int type = 0;
			if (receipt.get().getLogs().size() > 0){
				String addressTo = "";
				String addressFrom = "";
				for(int j = 0; j < receipt.get().getLogs().size(); j++){
					// 获取合约收款地址
					addressTo = receipt.get().getLogs().get(j).getAddress();
					if (Constant.NFT_HERO_ADDRESS.toLowerCase().equals(addressTo)){
						type = 1;
						tokenNum++;
					} else if (Constant.NFT_EQUIP_ADDRESS.toLowerCase().equals(addressTo)){
						type = 2;
						tokenNum++;
					} else if (Constant.NFT_EX_ADDRESS.toLowerCase().equals(addressTo)){
						type = 3;
						tokenNum++;
					}

					// 获取用户地址
//					if (receipt.get().getLogs().get(j).getTopics().size() > 0){
//						addressFrom = receipt.get().getLogs().get(j).getTopics().get(2);
//						addressFrom = addressFrom.replace(Constant.HEX_ZERO,"");
//					}
				}
			}

			if (from.toLowerCase().equals(receipt.get().getFrom().toLowerCase())){
				// 查询系统是否存在该用户
				user = userService.queryByAddress(from.toLowerCase());
				// 用户不存在则自动注册
				if (user == null){
					UserEntity userRegister = new UserEntity();
					Date date = new Date();
					userRegister.setSignDate(date);
					userRegister.setUserWalletAddress(from.toLowerCase());
					user = userService.userRegister(userRegister);
				}

				// 设置订单参数
				DrawForm form = new DrawForm();
				if (tokenNum == 1){
					form.setDrawType("1");
				} else if (tokenNum == 10){
					form.setDrawType("2");
				}
				form.setCurType(Constant.enable);
				form.setTransactionHash(txHash);

				// 通过交易hash查找订单是否存在
				TransactionOrderEntity orderEntity = transactionOrderService.getOrderHash(txHash);
				if (orderEntity == null){
					// 插入一笔订单,订单状态默认待处理
					transactionOrderService.addOrder(user,null,form);
				}

				String status = receipt.get().getStatus();
				String address = receipt.get().getFrom();
				map.put("userId",user.getUserId());
				map.put("from",address);
				map.put("gasUsed",receipt.get().getGasUsed());
				map.put("blockNumber",receipt.get().getBlockNumber());
				if (status.equals(Constant.CHAIN_STATE1)){
					// 设置抽奖类型
					// 校验成功后开始抽奖
					if (type == 1){
						// 英雄抽奖
						form.setItemType(Constant.HERO);
						gifts = drawGiftService.heroDrawStart(user,form);
					} else if (type == 2){
						// 装备抽奖
						form.setItemType(Constant.EQUIPMENT);
						gifts = drawGiftService.equipDrawStart(user,form);
					} else if (type == 3){
						// 经验抽奖
						form.setItemType(Constant.EXPERIENCE);
						gifts = drawGiftService.exDrawStart(user,form);
					}

					// 更新当前订单状态为成功
					map.put("status",Constant.enable);
					transactionOrderService.updateOrder(form,gifts,map);
				} else if(status.equals(Constant.CHAIN_STATE0)) {
					// 更新当前订单状态为失败
					map.put("status",Constant.failed);
					transactionOrderService.updateOrder(form,gifts,map);
				}
			}
		}

	}

	// 更新系统中保存的区块号
	private void setBlock_Num(int blockNum){
		sysConfigService.updateValueByKey(Constant.BLOCK_NUMBER, String.valueOf(blockNum));
	}
}
