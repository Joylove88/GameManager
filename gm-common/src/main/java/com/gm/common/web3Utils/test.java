package com.gm.common.web3Utils;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        String nodeUrl = "https://mainnet.infura.io/v3/4f1cca55624d4c2fbfa6a0bbd60605d2"; // Replace with your Infura project ID
        Web3j web3j = Web3j.build(new HttpService(nodeUrl));

        String accountAddress = "0x78589ec5451968F6f06D12FA9356538Ba0b595a6";
        String tokenContractAddress = "0xdAC17F958D2ee523a2206206994597C13D831ec7";
        long startTime = 1690879358L; // Replace with your desired start time in seconds
        long endTime = 1692693758L;   // Replace with your desired end time in seconds


        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(new BigInteger("17968520")),
                DefaultBlockParameterName.LATEST,
                tokenContractAddress
        );

//        System.out.println("在指定时间段内账户 " + accountAddress + " 的代币交易量为: " + totalTokenAmount);


        filter.addSingleTopic(Numeric.toHexStringWithPrefixZeroPadded(Numeric.toBigInt(accountAddress), 64));
//        filter.addSingleTopic(Numeric.toHexStringWithPrefixZeroPadded(BigInteger.ZERO, 64));
//        filter.addOptionalTopics(
//                Numeric.toHexStringWithPrefixZeroPadded(Numeric.toBigInt(accountAddress), 64),
//                Numeric.toHexStringWithPrefixZeroPadded(BigInteger.ZERO, 64),
//                Numeric.toHexStringWithPrefixZeroPadded(BigInteger.ZERO, 64)
//        );

        try {
            EthLog ethLog = web3j.ethGetLogs(filter).send();
            BigDecimal totalTokenAmount = BigDecimal.ZERO;

            for (EthLog.LogResult logResult : ethLog.getLogs()) {
                EthLog.LogObject log = (EthLog.LogObject) logResult.get();
                long blockTimestamp = web3j.ethGetBlockByHash(log.getBlockHash(), false).send().getBlock().getTimestamp().longValue();
                if (blockTimestamp >= startTime && blockTimestamp <= endTime) {
                    String data = log.getData();
                    BigInteger tokenAmount = Numeric.toBigInt(data);
                    BigDecimal tokenAmountDecimal = Convert.fromWei(tokenAmount.toString(), Convert.Unit.ETHER);
                    totalTokenAmount = totalTokenAmount.add(tokenAmountDecimal);
                }
            }

            System.out.println("Total token amount traded in the given time period: " + totalTokenAmount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
