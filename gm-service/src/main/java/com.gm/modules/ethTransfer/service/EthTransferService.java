package com.gm.modules.ethTransfer.service;

import com.alibaba.fastjson.JSONObject;
import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.EthTransferListenUtils;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.fundsAccounting.service.FundsAccountingService;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.sys.service.SysDictService;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.DrawForm;
import com.gm.modules.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.*;

/**
 * 链上业务类
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("ethTransferService")
public class EthTransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EthTransferService.class);
    @Autowired
    private UserService userService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private DrawGiftService drawGiftService;
    @Autowired
    private FundsAccountingService fundsAccountingService;
    @Autowired
    private TransactionOrderService transactionOrderService;
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
    /**
     * 团队地址
     */
    private static String TEAM_ADDRESS;
    /**
     * 资金池地址
     */
    private static String CAPITAL_POOL_ADDRESS;
    /**
     * 代币地址
     */
    private static String TOKENS_ADDRESS;

    public void eth(String txHash, TransactionOrderEntity order, Optional<TransactionReceipt> receipt, DrawForm form) throws Exception {
        if (receipt != null) {
            // 获取地址
            JSONObject addressJson = sysDictService.getContractsAddress();
            NFT_HERO_ADDRESS = addressJson.getString("NFT_HERO_ADDRESS");
            NFT_EQUIP_ADDRESS = addressJson.getString("NFT_EQUIP_ADDRESS");
            NFT_EX_ADDRESS = addressJson.getString("NFT_EX_ADDRESS");
            TOKENS_ADDRESS = addressJson.getString("TOKENS_ADDRESS");
            CAPITAL_POOL_ADDRESS = addressJson.getString("CAPITAL_POOL_ADDRESS");
            TEAM_ADDRESS = addressJson.getString("TEAM_ADDRESS");

            // 抽奖集合
            List gifts = new ArrayList();

            // 实例化用户类
            UserEntity user = new UserEntity();

            Map<String,Object> map = new HashMap<>();

            BigDecimal amount = BigDecimal.valueOf(0L);// 资金池
            BigDecimal amountTeam = BigDecimal.valueOf(0L);// 团队抽成

            // token交易数量(抽奖次数)
            int tokenNum = 0;

            // 类型1英雄，2装备，3道具
            int type = 0;

            // 获取生成的token数量
            if (receipt.get().getLogs().size() > 0) {
                for (int j = 0; j < receipt.get().getLogs().size(); j++) {
                    String topics0 = EthTransferListenUtils.getTopics0(); // 获取合约事件名称的HASH
                    if (receipt.get().getLogs().get(j).getTopics().get(0).equals(topics0)) {
                        // 获取合约收款地址
                        String addressTo = receipt.get().getLogs().get(j).getAddress();
                        if (NFT_HERO_ADDRESS.toLowerCase().equals(addressTo)) {// 英雄NFT
                            type = 1;
                            tokenNum++;
                        } else if (NFT_EQUIP_ADDRESS.toLowerCase().equals(addressTo)) {// 装备NFT
                            type = 2;
                            tokenNum++;
                        } else if (NFT_EX_ADDRESS.toLowerCase().equals(addressTo)) {// 经验NFT
                            type = 3;
                            tokenNum++;
                        } else if (TOKENS_ADDRESS.toLowerCase().equals(addressTo)) {// 代币数量（费用+团队抽成）
                            Address tokenAddress = new Address(receipt.get().getLogs().get(j).getTopics().get(2));
                            String tokenNumHex = Numeric.toBigInt(receipt.get().getLogs().get(j).getData()).toString();
                            if (CAPITAL_POOL_ADDRESS.toLowerCase().equals(tokenAddress.toString())) {// 资金池
                                amount = Arith.add(amount, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
                            } else if (TEAM_ADDRESS.toLowerCase().equals(tokenAddress.toString())) {// 团队抽成
                                amountTeam = Arith.add(amountTeam, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
                            }
                        }
                    }
                    // 获取用户地址
//					if (receipt.get().getLogs().get(j).getTopics().size() > 0){
//						addressFrom = receipt.get().getLogs().get(j).getTopics().get(2);
//						addressFrom = addressFrom.replace(Constant.HEX_ZERO,"");
//					}
                }

                // 资金池记账
                // 团队池入账
                fundsAccountingService.setCashPoolAdd(Constant.CashPool._TEAM.getValue(), amountTeam);


                System.out.println("本次转入资金池：" + amount);
                System.out.println("本次转入团队池：" + amountTeam);
                System.out.println("tokenNum：" + tokenNum);
                System.out.println("type：" + type);
            }
            String status = receipt.get().getStatus();
            String address = receipt.get().getFrom();
            map.put("userId",user.getUserId());
            map.put("from",address);
            map.put("gasUsed",receipt.get().getGasUsed());
            map.put("blockNumber",receipt.get().getBlockNumber());

            // 查询系统是否存在该用户
            user = userService.queryByAddress(address.toLowerCase());
            // 用户不存在则执行自动注册
            if (user == null){
                UserEntity userRegister = new UserEntity();
                Date date = new Date();
                userRegister.setSignDate(date);
                userRegister.setUserWalletAddress(address.toLowerCase());
                user = userService.userRegister(userRegister);
            }

            // 设置订单参数
            if (tokenNum == 1){
                form.setDrawType("1");
            } else if (tokenNum == 10){
                form.setDrawType("2");
            }

            if ( order == null ) {
                // 设置抽奖类型
                if (type == 1){
                    // 英雄抽奖
                    form.setItemType(Constant.ItemType.HERO.getValue());
                } else if (type == 2){
                    // 装备抽奖
                    form.setItemType(Constant.ItemType.EQUIPMENT.getValue());
                } else if (type == 3){
                    // 经验抽奖
                    form.setItemType(Constant.ItemType.EXPERIENCE.getValue());
                }
                // 设置货币类型为加密货币
                form.setCurType(Constant.CurrencyType._CRYPTO.getValue());
                // 设置交易HASH
                form.setTransactionHash(txHash);

                // 通过交易hash查找订单是否存在
                TransactionOrderEntity orderEntity = transactionOrderService.getOrderHash(txHash);
                if (orderEntity == null){
                    // 插入一笔订单,订单状态默认待处理
                    transactionOrderService.addOrder(user,null,form);
                }
            }

            // 链上交易状态成功执行抽奖，更新订单状态
            if (status.equals(Constant.CHAIN_STATE1)){

                // 校验成功后开始抽奖
                gifts = drawGiftService.drawStart(user, form);

                // 更新当前订单状态为成功
                map.put("status",Constant.enable);
                transactionOrderService.updateOrder(form,gifts,map);

            } else if(status.equals(Constant.CHAIN_STATE0)) {// 链上交易状态失败执行，更新订单状态
                // 更新当前订单状态为失败
                map.put("status",Constant.failed);
                transactionOrderService.updateOrder(form,gifts,map);
            }
        }
    }
}

