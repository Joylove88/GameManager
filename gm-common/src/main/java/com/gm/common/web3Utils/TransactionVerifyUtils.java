/**
 * WEB3J
 */

package com.gm.common.web3Utils;

import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.EthTransferListenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TransactionVerifyUtils {
    @Value("${contractAddress.networks:#{null}}")
    private String networks;

    // 链接链上
    public Web3j connect() {
        System.out.println("Connecting to binanceScan");
        Web3j web3 = Web3j.build(new HttpService(networks));
        System.out.println("Successfuly connected to Ethereum");
        return web3;
    }

    //监听链上交易信息
    public static Optional<TransactionReceipt> isVerify(Web3j web3, String transactionHash) {
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
            } while (!transactionReceipt.isPresent() && i < 3);
            if (transactionReceipt != null) {
                System.out.println("transactionHash:" + transactionReceipt.get().getTransactionHash() + "," +
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

        } catch (Exception ex) {
            throw new RuntimeException("Error whilst sending json-rpc requests", ex);
        }
        return transactionReceipt;
    }

    public static void main(String[] args) {
        Web3j web3 = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"));
		Optional<TransactionReceipt> receipt = isVerify(web3,"0xc8ddb8daaf9735c4d5ea8b3928c23630644466ba6e6285d32e96fabb2ba76105");
		System.out.println(receipt);
		String BUSD_ADDRESS = "0xeD24FC36d5Ee211Ea25A80239Fb8C4Cfd80f12Ee";
		String CAPITAL_POOL_ADDRESS = "0xa2294B906829eD5c2d18618BC724c13889a6173d";
		String TEAM_ADDRESS = "0x2A3Ee1e41A8A11546C5732CFB447b752c9F09618";
		BigDecimal amount = BigDecimal.valueOf(0L);// 资金池
		BigDecimal amountTeam = BigDecimal.valueOf(0L);// 团队抽成
		List<String> tokenIds = new ArrayList<>();
		if (receipt !=null) {
			// token交易数量(抽奖次数)
			int tokenNum = 0;
			// 类型1英雄，2装备，3道具
			int type = 0;
			// 获取生成的token数量
			if (receipt.get().getLogs().size() > 0) {
				for (int j = 0; j < receipt.get().getLogs().size(); j++) {
					String topics0Transfer = EthTransferListenUtils.getTopics0(Constant.disabled); // 获取合约事件Transfer的HASH
					String topics0Mint = EthTransferListenUtils.getTopics0(Constant.enable); // 获取合约事件Mint的HASH
					if (receipt.get().getLogs().get(j).getTopics().get(0).equals(topics0Transfer)) {
						// 获取合约收款地址
						String addressTo = receipt.get().getLogs().get(j).getAddress();
						if (Constant.NFT_HERO_ADDRESS.toLowerCase().equals(addressTo)) {// 英雄NFT
							type = 1;
							tokenNum++;
							tokenIds.add(Numeric.toBigInt(receipt.get().getLogs().get(j).getTopics().get(3)).toString());
						} else if (Constant.NFT_FREE_HERO_ADDRESS.toLowerCase().equals(addressTo)) {// NFT免费英雄地址
                            type = 1;
                            tokenNum++;
                            tokenIds.add(Numeric.toBigInt(receipt.get().getLogs().get(j).getTopics().get(3)).toString());
                        } else if (Constant.NFT_EQUIP_ADDRESS.toLowerCase().equals(addressTo)) {// 装备NFT
							type = 2;
							tokenNum++;
							tokenIds.add(Numeric.toBigInt(receipt.get().getLogs().get(j).getTopics().get(3)).toString());
						} else if (Constant.NFT_EX_ADDRESS.toLowerCase().equals(addressTo)) {// 经验NFT
							type = 3;
							tokenNum++;
							tokenIds.add(Numeric.toBigInt(receipt.get().getLogs().get(j).getTopics().get(3)).toString());
						} else if (BUSD_ADDRESS.toLowerCase().equals(addressTo)) {// 代币数量（费用+团队抽成）
							Address tokenAddress = new Address(receipt.get().getLogs().get(j).getTopics().get(2));
							String tokenNumHex = Numeric.toBigInt(receipt.get().getLogs().get(j).getData()).toString();
							System.out.println(tokenNumHex);
							if (CAPITAL_POOL_ADDRESS.toLowerCase().equals(tokenAddress.toString())) {// 资金池
								amount = Arith.add(amount, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
							} else if (TEAM_ADDRESS.toLowerCase().equals(tokenAddress.toString())) {// 团队抽成
								amountTeam = Arith.add(amountTeam, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
							}
						}
					} else if (receipt.get().getLogs().get(j).getTopics().get(0).equals(topics0Mint)) {
                        String tokenNumHex = Numeric.toBigInt(receipt.get().getLogs().get(j).getData()).toString();
                        System.out.println(tokenNumHex);
                        amount = Arith.add(amount, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
                    }
				}
				System.out.println("本次转入资金池：" + amount);
				System.out.println("本次转入团队池：" + amountTeam);
				System.out.println("tokenNum：" + tokenNum);
				System.out.println("type：" + type);
			}
		}
		System.out.println(tokenIds.get(0));
//		byte[] hash = Hash.sha3(String.valueOf("Transfer(address,address,uint256)").getBytes());
//		System.out.println(Numeric.toHexString(hash));
    }
}
