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
		Optional<TransactionReceipt> receipt = isVerify(connect(),"0xfeeb16fcedc4740256f294502c94fad3e7840fb470a4b355b9eaeac091bc9e1c");
		System.out.println(receipt);

	}
}
