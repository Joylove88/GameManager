/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.common.web3Utils;

import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.EthTransferListenUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
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
		Optional<TransactionReceipt> receipt = isVerify(connect(),"0x7586154595b996d6fad912e59b2f7ab0720cdd34134f3d1876e06811b66710be");
		System.out.println(receipt);
//		String BUSD_ADDRESS = "0xeD24FC36d5Ee211Ea25A80239Fb8C4Cfd80f12Ee";
//		String CAPITAL_POOL_ADDRESS = "0xb2bF67468170f1b8F32f29011c2E9ab302D80749";
//		String TEAM_ADDRESS = "0x2A3Ee1e41A8A11546C5732CFB447b752c9F09618";
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
//						} else if (BUSD_ADDRESS.toLowerCase().equals(addressTo)) {// 代币数量（费用+团队抽成）
//							Address tokenAddress = new Address(receipt.get().getLogs().get(j).getTopics().get(2));
//							String tokenNumHex = Numeric.toBigInt(receipt.get().getLogs().get(j).getData()).toString();
//							if (CAPITAL_POOL_ADDRESS.toLowerCase().equals(tokenAddress.toString())) {// 资金池
//								amount = Arith.add(amount, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
//							} else if (TEAM_ADDRESS.toLowerCase().equals(tokenAddress.toString())) {// 团队抽成
//								amountTeam = Arith.add(amountTeam, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
//							}
//						}
//					}
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
