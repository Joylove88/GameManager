package com.gm.common.utils;

import com.gm.common.web3Utils.TransactionVerifyUtils;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 监听链上交易
 * Created by axiang on 2022/3/20 0020.
 */
public class EthTransferListenUtils {
//    private static List<String> contracts = new ArrayList<>();  //代币合约地址列表,可以存放多个地址
//    private static Disposable tokenSubscription;   //token事件订阅对象
//    private static Disposable ethMissSubscription; //ETH交易空档事件订阅对象
//    private static Disposable ethSubscription;     //ETH交易事件订阅对象
//    private static int block_TokenSub = 0;
//    private static int block_EthSub = 0;
//    private static int block_EthMissSub = 0;
//    private static Map htAddress = new HashMap<>();
//    private static Web3j web3j;
//
//    private static void run() throws Exception {
//        web3j = TransactionVerifyUtils.connect();
//
//    }

    /**
     * 获取链上交易日志事件名称的hash值
     * @return
     */
    public static String getTopics0(){
        byte[] hash = Hash.sha3(String.valueOf(Constant.EVENT_NAME).getBytes());
        return Numeric.toHexString(hash);
    }
//
//    public static void main(String[] args) throws Exception {
//        String address = "0x25B15dE515eBBD047e026D64463801f044785cc6";
//        String fromAddress = "0x1cabea67c565b5337e688894960839ef1D48b0cD";
//        contracts.add(address);
//        htAddress.put(address.toLowerCase(),address);
//        htAddress.put(fromAddress.toLowerCase(),fromAddress);
//        //获取起始区块号
//        startReplayListen_ETH(BigInteger.valueOf(17666907));
////        startTransferListen_Token(BigInteger.valueOf(17720623));
////        startTransactionListen_ETH();
//    }
//
//    /**
//     *  启动监听， 从startBlock区块开始监听token转账事件
//     代币监听会出现的问题： 如果启动区块距离当前区块稍远，非常可能的情况是中间出现的交易太多，监视代码内部出现空指针异常。
//     如果监听启动时接近当前区块问题出现概率小。
//     * @param startBlock
//     */
//    private static void startTransferListen_Token(BigInteger startBlock) throws Exception {
//        // 启动区块链
//        run();
//        // 要监听的合约事件
//        Event event = new Event("Transfer",
//                Arrays.asList(
//                        new TypeReference<Address>() {},
//                        new TypeReference<Address>() {},
//                        new TypeReference<Uint>(){}));
//
//        //过滤器
//        EthFilter filter = new EthFilter(
//                DefaultBlockParameter.valueOf(startBlock),
//                DefaultBlockParameterName.LATEST,
//                contracts);
//
//        filter.addSingleTopic(EventEncoder.encode(event));
//
//        //注册监听,解析日志中的事件
//        block_TokenSub = startBlock.intValue();
//        tokenSubscription = web3j.ethLogFlowable(filter).subscribe(log -> {
//            block_TokenSub = log.getBlockNumber().intValue();
//            String token = log.getAddress();  //这是Token合约地址
//            String txHash = log.getTransactionHash();
//            List<String> topics = log.getTopics();  // 提取转账记录
//            String fromAddress = "0x"+topics.get(1).substring(26);
//            String toAddress = "0x"+topics.get(2).substring(26);
//            System.out.println("  ---token ="+token+",  txHash ="+txHash);
//            System.out.println("  ---fromAddress ="+fromAddress+",  toAddress ="+toAddress);
//            //检查发送地址、接收地址是否属于系统用户， 不是系统用户就不予处理
//            if(htAddress.containsKey(fromAddress) || htAddress.containsKey(toAddress)) {
//                String value1 = log.getData();
//                System.out.println("value======================="+log.toString());
//                BigInteger big = new BigInteger(value1.substring(2), 16);
//                BigDecimal value = Convert.fromWei(big.toString(), Convert.Unit.ETHER);
//                //                    System.out.println("value="+value);
//                String timestamp = "";
//                try {
//                    EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(log.getBlockNumber()), false).send();
//                    timestamp = String.valueOf(ethBlock.getBlock().getTimestamp());
//                } catch (IOException e) {
//                    System.out.println("Block timestamp get failure,block number is {}" + log.getBlockNumber());
//                    System.out.println("Block timestamp get failure,{}"+  e.getMessage());
//                }
//                //执行关键的回调函数
//                callBack_Token(token,txHash,fromAddress,toAddress,value,timestamp);
//
//            }
//        }, error->{
//            System.out.println(" ### tokenSubscription   error= "+ error);
//            error.printStackTrace();
//        });
//        System.out.println("tokenSubscription ="+tokenSubscription);
//        System.out.println(tokenSubscription.isDisposed());
//    }
//
//
//    //启动监听以太坊上的过往交易
//    public static void startReplayListen_ETH(BigInteger startBlockNum) throws Exception {
//        // 启动区块链
//        run();
//        System.out.println("  startReplayListen_ETH:  startBlockNum="+startBlockNum);
//        //回放空档期间的交易
//        BigInteger currentBlockNum=null;
//        try {
//            //获取当前区块号
//            currentBlockNum = web3j.ethBlockNumber().send().getBlockNumber();
//            System.out.println("  000 currentBlockNum= "+currentBlockNum.intValue());
//            if(startBlockNum.compareTo(currentBlockNum) > 0) {
//                return;  //测试曾经出现 currentBlockNum得到错误数字，比startBlockNum还小，这时不能启动监听
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            System.out.println("  111 getBlockNumber() Error: ");
//            e.printStackTrace();
//            return;   //出现异常不能启动监听
//        }
//
//        //创建开始与结束区块， 重放这段时间内的交易，防止遗漏
//        DefaultBlockParameter startBlock = new DefaultBlockParameterNumber(startBlockNum);
//        DefaultBlockParameter endBlock = new DefaultBlockParameterNumber(BigInteger.valueOf(17666908));
//        System.out.println("[ startTransferListen_ETH:  miss  startBlock="+startBlockNum+", endBlock="+currentBlockNum+"]");
//
//        block_EthMissSub = startBlockNum.intValue();
//        ethMissSubscription = web3j.replayPastTransactionsFlowable(startBlock, endBlock)
//            .subscribe(tx -> {
//                //更新检查过的区块高度
//                block_EthMissSub = tx.getBlockNumber().intValue();
//                setBlock_Num(block_EthMissSub);
//                System.out.println("  ---replayPastTransactionsFlowable    block_EthMissSub = "+block_EthMissSub);
//                String fromAddress = tx.getFrom();
//                String toAddress   = tx.getTo();
//                System.out.println("fromAddress="+fromAddress + "toAddress="+toAddress);
//                if(htAddress.containsKey(fromAddress) || htAddress.containsKey(toAddress)) {  //发现了指定地址上的交易
//                    String txHash = tx.getHash();
//                    BigDecimal value = Convert.fromWei(tx.getValue().toString(), Convert.Unit.ETHER);
//                    String timestamp = "";
//                    try {
//                        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(tx.getBlockNumber()), false).send();
//                        timestamp = String.valueOf(ethBlock.getBlock().getTimestamp());
//                    } catch (IOException e) {
//                        System.out.println("Block timestamp get failure,block number is {}" + tx.getBlockNumber());
//                        System.out.println("Block timestamp get failure,{}"+  e.getMessage());
//
//                    }
//                    // 监听以太坊上是否有系统生成地址的交易
//                    callBack_ETH(txHash,fromAddress,toAddress,value,timestamp);
//                }
//            }, error->{
//                System.out.println("   ### replayPastTransactionsFlowable  error= "+ error);
//                error.printStackTrace();
//            });
//    }
//
//
//    //启动监听以太坊上的交易
//    public static void startTransactionListen_ETH() throws Exception {
//        // 启动区块链
//        run();
//        //监听当前区块以后的交易
//        ethSubscription = web3j.transactionFlowable().subscribe(tx -> {
//            //更新检查过的区块高度
//            block_EthSub = tx.getBlockNumber().intValue();
//            System.out.println("  ---transactionFlowable  block_EthSub = "+block_EthSub);
//
//            String txHash = tx.getHash();
//            String fromAddress = tx.getFrom();
//            String toAddress = tx.getTo();
//
//            if(htAddress.containsKey(fromAddress) || htAddress.containsKey(toAddress)) {  //发现了指定地址上的交易
//                BigDecimal value = Convert.fromWei(tx.getValue().toString(), Convert.Unit.ETHER);
//                String timestamp = "";
//                try {
//                    EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(tx.getBlockNumber()), false).send();
//                    timestamp = String.valueOf(ethBlock.getBlock().getTimestamp());
//                } catch (IOException e) {
//                    System.out.println("Block timestamp get failure,block number is {}" + tx.getBlockNumber());
//                    System.out.println("Block timestamp get failure,{}"+  e.getMessage());
//                }
//
//                // 监听以太坊上是否有系统生成地址的交易
//                callBack_ETH(txHash,fromAddress,toAddress,value,timestamp);
//
//            }
//
//        }, error->{
//            System.out.println("   ### transactionFlowable  error= "+ error);
//            error.printStackTrace();
//
//        });
//
//    }

    //token转账事件的处理函数

    public static void callBack_Token(String token, String txHash, String from, String to, BigDecimal value, String timestamp) {

//        System.out.println("----callBack_Token:");
//
//        System.out.println("    token = "+token);
//
//        System.out.println("    txHash = "+token);
//
//        System.out.println("    from = "+from);
//
//        System.out.println("    to = "+to);
//
//        System.out.println("    value = "+value.doubleValue());



    }
//
//    //txHash转账事件的处理函数
//    public static void callBack_ETH(String txHash, String from, String to, BigDecimal value, String timestamp) {
//        System.out.println("----------------------------------------------------------------------------------------------------------------------callBack_Token:");
//
//        System.out.println("----------------------------------------------------------------------------------------------------------------------    txHash = "+txHash);
//
//        System.out.println("----------------------------------------------------------------------------------------------------------------------    from = "+from);
//
//        System.out.println("----------------------------------------------------------------------------------------------------------------------    to = "+to);
//
//        System.out.println("----------------------------------------------------------------------------------------------------------------------    value = "+value.doubleValue());
//        TransactionVerifyUtils.isVerify(web3j,txHash);
//
//    }

    public static void setBlock_Num(int blockNum){

    }
}
