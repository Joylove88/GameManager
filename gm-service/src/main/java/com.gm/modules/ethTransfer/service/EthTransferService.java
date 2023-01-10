package com.gm.modules.ethTransfer.service;

import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.EthTransferListenUtils;
import com.gm.modules.basicconfig.dto.SummonedEventDto;
import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.fundsAccounting.service.FundsAccountingService;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.sys.service.SysDictService;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.req.SummonReq;
import com.gm.modules.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.*;

/**
 * 链上业务类
 *
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
     * BUSD地址
     */
    @Value("${contractAddress.busdTokenAddress:#{null}}")
    private String busdTokenAddress;
    /**
     * 资金池地址
     */
    @Value("${contractAddress.fundPoolAddress:#{null}}")
    private String fundPoolAddress;
    /**
     * 团队地址
     */
    @Value("${contractAddress.teamAddress:#{null}}")
    private String teamAddress;
    /**
     * 英雄合约地址
     */
    @Value("${contractAddress.nftHeroAddress:#{null}}")
    private String nftHeroAddress;
    /**
     * 装备合约地址
     */
    @Value("${contractAddress.nftEquipAddress:#{null}}")
    private String nftEquipAddress;
    /**
     * 经验合约地址
     */
    @Value("${contractAddress.nftExAddress:#{null}}")
    private String nftExpAddress;

    @Transactional(rollbackFor = Exception.class)
    public List eth(String txHash, TransactionOrderEntity order, Optional<TransactionReceipt> receipt, SummonReq form) throws Exception {
        // 召唤集合
        List gifts = new ArrayList();
        if (receipt != null) {
            // 实例化用户类
            UserEntity user = new UserEntity();

            Map<String, Object> map = new HashMap<>();
            BigDecimal amount = BigDecimal.valueOf(0L);// 资金池
            BigDecimal amountTeam = BigDecimal.valueOf(0L);// 团队抽成

            // token交易数量(召唤次数)
            int tokenNum = 0;
            // 类型1英雄，2装备，3道具
            String type = "";
            // TOKENID集合
            List<String> tokenIds = new ArrayList<>();
            // 获取生成的token数量
            if (receipt.get().getLogs().size() > 0) {
                for (int j = 0; j < receipt.get().getLogs().size(); j++) {
                    String topics0 = EthTransferListenUtils.getTopics0(); // 获取合约事件名称的HASH
                    if (receipt.get().getLogs().get(j).getTopics().get(0).equals(topics0)) {
                        // 获取合约收款地址
                        String addressTo = receipt.get().getLogs().get(j).getAddress();
                        if (nftHeroAddress.toLowerCase().equals(addressTo)) {// 英雄NFT
                            type = Constant.SummonType.HERO.getValue();
                            tokenNum++;
                            tokenIds.add(Numeric.toBigInt(receipt.get().getLogs().get(j).getTopics().get(3)).toString());
                        } else if (nftEquipAddress.toLowerCase().equals(addressTo)) {// 装备NFT
                            type = Constant.SummonType.EQUIPMENT.getValue();
                            tokenNum++;
                            tokenIds.add(Numeric.toBigInt(receipt.get().getLogs().get(j).getTopics().get(3)).toString());
                        } else if (nftExpAddress.toLowerCase().equals(addressTo)) {// 经验NFT
                            type = Constant.SummonType.EXPERIENCE.getValue();
                            tokenNum++;
                            tokenIds.add(Numeric.toBigInt(receipt.get().getLogs().get(j).getTopics().get(3)).toString());
                        } else if (busdTokenAddress.toLowerCase().equals(addressTo)) {// 代币数量（费用+团队抽成）
                            Address tokenAddress = new Address(receipt.get().getLogs().get(j).getTopics().get(2));
                            String tokenNumHex = Numeric.toBigInt(receipt.get().getLogs().get(j).getData()).toString();
                            if (fundPoolAddress.toLowerCase().equals(tokenAddress.toString())) {// 资金池
                                amount = Arith.add(amount, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
                            } else if (teamAddress.toLowerCase().equals(tokenAddress.toString())) {// 团队抽成
                                amountTeam = Arith.add(amountTeam, Convert.fromWei(tokenNumHex, Convert.Unit.ETHER));
                            }
                        }
                    }
                }
                LOGGER.info("amount：" + amount);
                LOGGER.info("amountTeam：" + amountTeam);
                LOGGER.info("tokenNum：" + tokenNum);
                LOGGER.info("type：" + type);
            }
            String status = receipt.get().getStatus();
            String address = receipt.get().getFrom();
            // 金额总计
            amount = Arith.add(amount, amountTeam);
            // 查询系统是否存在该用户
            user = userService.queryByAddress(address.toLowerCase());
            // 用户不存在则执行自动注册
            if (user == null) {
                UserEntity userRegister = new UserEntity();
                Date date = new Date();
                userRegister.setSignDate(date);
                userRegister.setAddress(address.toLowerCase());
                user = userService.userRegister(userRegister);
            }
            // 获取召唤价格及活动信息
            SummonedEventDto summonedEventDto = drawGiftService.getSummonedPrice(user.getAddress());
            BigDecimal price = tokenNum == Constant.Quantity.Q1.getValue() ? BigDecimal.valueOf(summonedEventDto.getOnePriceNew()) : BigDecimal.valueOf(summonedEventDto.getTenPriceNew());
            LOGGER.info("price: " + price);
            if (amount.compareTo(price) == -1) {
                return null;
            }
            map.put("userId", user.getUserId());
            map.put("from", address);
            map.put("gasUsed", receipt.get().getGasUsed());
            map.put("blockNumber", receipt.get().getBlockNumber());

            // 设置物品类型
            if (type.equals(Constant.SummonType.HERO.getValue())) {
                // 英雄召唤
                form.setSummonType(Constant.SummonType.HERO.getValue());
            } else if (type.equals(Constant.SummonType.EQUIPMENT.getValue())) {
                // 装备召唤
                form.setSummonType(Constant.SummonType.EQUIPMENT.getValue());
            } else if (type.equals(Constant.SummonType.EXPERIENCE.getValue())) {
                // 经验召唤
                form.setSummonType(Constant.SummonType.EXPERIENCE.getValue());
            }
            // 设置货币类型为加密货币
            form.setCurType(Constant.CurrencyType._CRYPTO.getValue());
            // 设置交易HASH
            form.setTransactionHash(txHash);
            // 设置召唤次数
            form.setSummonNum(tokenNum);
            // 设置NFT_tokenID
            form.setTokenIds(tokenIds);
            // 设置订单金额
            form.setOrderFee(amount);
            // 设置实付金额
            form.setRealFee(price);

            // 如果订单为空则创建新订单
            if (order == null) {
                // 通过交易hash查找订单是否存在
                TransactionOrderEntity orderEntity = transactionOrderService.getOrderHash(txHash);
                if (orderEntity == null) {
                    // 插入一笔订单,订单状态默认待处理
                    transactionOrderService.addOrder(user, null, form);
                }
            }

            // 链上交易状态成功执行召唤，更新订单状态
            if (status.equals(Constant.CHAIN_STATE1)) {
                // 校验成功后开始召唤
                gifts = drawGiftService.startSummon(user, form, summonedEventDto);
                // 更新当前订单状态为成功
                map.put("status", Constant.enable);
                transactionOrderService.updateOrder(form, gifts, map);
            } else if (status.equals(Constant.CHAIN_STATE0)) {// 链上交易状态失败执行，更新订单状态
                // 更新当前订单状态为失败
                map.put("status", Constant.failed);
                transactionOrderService.updateOrder(form, gifts, map);
            }
        }
        return gifts;
    }
}

