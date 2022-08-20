/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.common.web3Utils;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.util.Optional;

public class TransactionVerifyUtils {

	// 链接链上
	public static Web3j connect(){
		System.out.println("Connecting to binanceScan");
		Web3j web3 = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"));
		System.out.println("Successfuly connected to Ethereum");
		return web3;
	}

	//监听链上交易信息
	public static Optional<TransactionReceipt> isVerify(Web3j web3,String transactionHash) {
		String status = "0";
		// Wait for transaction to be mined
		Optional<TransactionReceipt> transactionReceipt = null;
		try {
			// 定义次数
			int i = 0;
			do {
				System.out.println("checking if transaction " + transactionHash + " is mined....");
				EthGetTransactionReceipt ethGetTransactionReceiptResp = web3.ethGetTransactionReceipt(transactionHash).send();
				transactionReceipt = ethGetTransactionReceiptResp.getTransactionReceipt();

				i++;
				Thread.sleep(3000); // Retry after 10 sec
			} while(!transactionReceipt.isPresent() && i < 3);
			if (transactionReceipt != null){
				System.out.println("transactionHash:" + transactionReceipt.get().getTransactionHash() + ","+
						"\ntransactionIndex:" + transactionReceipt.get().getTransactionIndex() +
						"\nblockHash:" + transactionReceipt.get().getBlockHash() +
						"\nblockNumber:" + transactionReceipt.get().getBlockNumber() +
						"\ncumulativeGasUsed:" + transactionReceipt.get().getCumulativeGasUsed() +
						"\ngasUsed:" + transactionReceipt.get().getGasUsed() +
						"\ncontractAddress:" + transactionReceipt.get().getContractAddress() +
						"\nroot:" + transactionReceipt.get().getRoot() +
						"\nstatus:" + transactionReceipt.get().getStatus() +
						"\nfrom:" + transactionReceipt.get().getFrom() +
						"\nto:" + transactionReceipt.get().getTo() +
						"\nlogsBloom:" + transactionReceipt.get().getLogsBloom() +
						"\nlogs:" + transactionReceipt.get().getLogs().toString()
				);
			}

		} catch(Exception ex) {
			throw new RuntimeException("Error whilst sending json-rpc requests", ex);
		}
		return transactionReceipt;
	}

	public static void main(String[] args) {
		Optional<TransactionReceipt> receipt = isVerify(connect(),"0xdc4a56499c5656a5377b45985e0546a265d21a6c8c50dd75935f31c1433c9613");
		System.out.println(receipt);
//		String TOKENS_ADDRESS = "0x0abBfa62C78187f201021D19b7D341332c8c6553";
//		String CAPITAL_POOL_ADDRESS = "0xb1EE547128A61E3941aC26374038ecB79a3A21B5";
//		String TEAM_ADDRESS = "0x4f03d425edb670ed056348d17bb57b99a9197250";
//		BigDecimal amount = BigDecimal.valueOf(0L);// 资金池
//		BigDecimal amountTeam = BigDecimal.valueOf(0L);// 团队抽成
//		if (receipt !=null) {
//			// token交易数量(抽奖次数)
//			int tokenNum = 0;
//			// 类型1英雄，2装备，3道具
//			int type = 0;
//			// 获取生成的token数量
//			if (receipt.get().getLogs().size() > 0) {
//				for (int j = 0; j < receipt.get().getLogs().size(); j++) {
//					String topics0 = EthTransferListenUtils.getTopics0(); // 获取合约事件名称的HASH
//					if (receipt.get().getLogs().get(j).getTopics().get(0).equals(topics0)) {
//						// 获取合约收款地址
//						String addressTo = receipt.get().getLogs().get(j).getAddress();
//						if (Constant.NFT_HERO_ADDRESS.toLowerCase().equals(addressTo)) {// 英雄NFT
//							type = 1;
//							tokenNum++;
//						} else if (Constant.NFT_EQUIP_ADDRESS.toLowerCase().equals(addressTo)) {// 装备NFT
//							type = 2;
//							tokenNum++;
//						} else if (Constant.NFT_EX_ADDRESS.toLowerCase().equals(addressTo)) {// 经验NFT
//							type = 3;
//							tokenNum++;
//						} else if (TOKENS_ADDRESS.toLowerCase().equals(addressTo)) {// 代币数量（费用+团队抽成）
//							Address tokenAddress = new Address(receipt.get().getLogs().get(j).getTopics().get(2));
//							String tokenNumHex = Numeric.toBigInt(receipt.get().getLogs().get(j).getData()).toString();
//							if (CAPITAL_POOL_ADDRESS.toLowerCase().equals(tokenAddress.toString())) {// 资金池
//								amount = Arith.add(amount, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
//							} else if (TEAM_ADDRESS.toLowerCase().equals(tokenAddress.toString())) {// 团队抽成
//								amountTeam = Arith.add(amountTeam, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
//							}
//						}
//					}
//					// 获取用户地址
////					if (receipt.get().getLogs().get(j).getTopics().size() > 0){
////						addressFrom = receipt.get().getLogs().get(j).getTopics().get(2);
////						addressFrom = addressFrom.replace(Constant.HEX_ZERO,"");
////					}
//				}
//				System.out.println("本次转入资金池：" + amount);
//				System.out.println("本次转入团队池：" + amountTeam);
//				System.out.println("tokenNum：" + tokenNum);
//				System.out.println("type：" + type);
//			}
//		}

//		byte[] hash = Hash.sha3(String.valueOf("Transfer(address,address,uint256)").getBytes());
//		System.out.println(Numeric.toHexString(hash));
	}
}
