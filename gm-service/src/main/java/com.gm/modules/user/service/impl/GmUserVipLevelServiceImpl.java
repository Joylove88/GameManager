package com.gm.modules.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.common.utils.Arith;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.Query;
import com.gm.modules.fundsAccounting.service.FundsAccountingService;
import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.user.dao.GmUserVipLevelDao;
import com.gm.modules.user.entity.GmUserVipLevelEntity;
import com.gm.modules.user.entity.UserAccountEntity;
import com.gm.modules.user.entity.UserBalanceDetailEntity;
import com.gm.modules.user.entity.UserEntity;
import com.gm.modules.user.service.GmUserVipLevelService;
import com.gm.modules.user.service.UserAccountService;
import com.gm.modules.user.service.UserBalanceDetailService;
import com.gm.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("gmUserVipLevelService")
@Transactional
public class GmUserVipLevelServiceImpl extends ServiceImpl<GmUserVipLevelDao, GmUserVipLevelEntity> implements GmUserVipLevelService {
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionOrderService transactionOrderService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserBalanceDetailService userBalanceDetailService;
    @Autowired
    private FundsAccountingService fundsAccountingService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GmUserVipLevelEntity> page = this.page(
                new Query<GmUserVipLevelEntity>().getPage(params),
                new QueryWrapper<GmUserVipLevelEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void updateUserVipLevel(TransactionOrderEntity order) {// 消费来了
        // 查询用户
        UserEntity user = userService.queryByUserId(order.getUserId());
        // 查询该用户消费等级
        GmUserVipLevelEntity currentUserVipLevel = baseMapper.selectById(user.getVipLevelId());
        // 查询所有消费等级
        List<GmUserVipLevelEntity> userVipLevelEntityList = baseMapper.selectList(new QueryWrapper<GmUserVipLevelEntity>()
                .lt("VIP_LEVEL_CODE", "10")
                .orderByAsc("VIP_LEVEL_CODE")
        );
        // 该用户即将消费等级
        GmUserVipLevelEntity willVipLevel = null;
        // 条件1.查询该用户累计消费金额,决定是否给该用户升级
        Double totalMoney = transactionOrderService.queryTotalMoneyByUserId(order.getUserId());
        for (GmUserVipLevelEntity gmUserVipLevel : userVipLevelEntityList) {
            if (currentUserVipLevel.getVipLevelCode() >= gmUserVipLevel.getVipLevelCode()) {
                continue;
            }
            if (gmUserVipLevel.getConsumeMoney() <= totalMoney + order.getTransactionGasFee().doubleValue()) {//客户消费总金额 >= 消费等级要求金额
                // 给用户升级
                willVipLevel = gmUserVipLevel;
            } else {
                break;
            }
        }
        if (willVipLevel != null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(user.getUserId());
            userEntity.setVipLevelId(willVipLevel.getVipLevelId());
            userService.updateById(userEntity);
        }

        GmUserVipLevelEntity fatherWillVipLevel = null;
        // 条件2.查询该用户父亲的所有儿子的累计消费金额,决定是否给父亲升级
        // 查询该用户父亲
        if (user.getFatherId() != null) {
            UserEntity fatherUser = userService.queryByUserId(user.getFatherId());
            // 查询该用户父亲的消费等级
            GmUserVipLevelEntity fatherUserVipLevel = baseMapper.selectById(user.getVipLevelId());
            Double sonTotalMoney = transactionOrderService.querySonTotalMoneyByFatherId(fatherUser.getUserId());
            for (GmUserVipLevelEntity gmUserVipLevel : userVipLevelEntityList) {
                if (fatherUserVipLevel.getVipLevelCode() >= gmUserVipLevel.getVipLevelCode()) {
                    continue;
                }
                if (gmUserVipLevel.getInviteConsumeMoney() <= sonTotalMoney + order.getTransactionFee().doubleValue()) {//客户父亲的所有儿子的消费总金额 >= 消费等级要求金额
                    // 给用户升级
                    fatherWillVipLevel = gmUserVipLevel;
                } else {
                    break;
                }
            }
            if (fatherWillVipLevel != null) {
                UserEntity userEntity = new UserEntity();
                userEntity.setUserId(fatherUser.getUserId());
                userEntity.setVipLevelId(fatherWillVipLevel.getVipLevelId());
                userService.updateById(userEntity);
            }
        }

        // ==========================计算返佣开始===============================
        String agentRebateRatio = sysConfigService.getValue("AGENT_REBATE_RATIO");
        JSONObject jsonObject = JSONObject.parseObject(agentRebateRatio);
        String f = jsonObject.getString("f");
        String gf = jsonObject.getString("gf");
        BigDecimal fget = BigDecimal.valueOf(0);
        BigDecimal gfget = BigDecimal.valueOf(0);
        if (user.getFatherId() != null && user.getFatherId() != 0L) {// 父亲在
            GmUserVipLevelEntity vipLevel = willVipLevel != null ? willVipLevel : currentUserVipLevel;
            Double brokerRatio = totalMoney == 0 ? vipLevel.getFirstBrokerage() : vipLevel.getBrokerage();
            BigDecimal broker = Arith.multiply(order.getTransactionFee(), BigDecimal.valueOf(brokerRatio));
            fget = Arith.multiply(broker, new BigDecimal(f));//父亲所得
            // 更新代理账户金额
            UserEntity fatherUser = userService.queryByUserId(user.getFatherId());
            userAccountService.updateAccountAdd(fatherUser.getUserId(), fget, Constant.ONE_);
            UserAccountEntity userAccount = userAccountService.queryByUserId(fatherUser.getUserId());
            // 插入父亲代理账户账变
            UserBalanceDetailEntity userBalanceDetail = new UserBalanceDetailEntity();
            userBalanceDetail.setAmount(fget);
            userBalanceDetail.setSourceId(order.getTransactionOrderId());
            userBalanceDetail.setTradeTime(new Date());
            userBalanceDetail.setTradeTimeTs(System.currentTimeMillis());
            userBalanceDetail.setTradeType("11");
            userBalanceDetail.setUserId(fatherUser.getUserId());
            userBalanceDetail.setUserBalance(userAccount.getBalance());
            userBalanceDetailService.save(userBalanceDetail);
            if (user.getGrandfatherId() != null && user.getGrandfatherId() != 0L) {// 爷爷在
                // 查询爷爷
                UserEntity grandFatherUser = userService.queryByUserId(user.getGrandfatherId());
                // 更新爷爷账变金额
                userAccountService.updateAccountAdd(grandFatherUser.getUserId(), gfget, Constant.ONE_);
                UserAccountEntity gfUserAccount = userAccountService.queryByUserId(grandFatherUser.getUserId());

                gfget = Arith.multiply(broker, new BigDecimal(gf));//爷爷所得

                // 插入爷爷账变
                UserBalanceDetailEntity gfUserBalanceDetail = new UserBalanceDetailEntity();
                gfUserBalanceDetail.setAmount(gfget);
                gfUserBalanceDetail.setSourceId(order.getTransactionOrderId());
                gfUserBalanceDetail.setTradeTime(new Date());
                gfUserBalanceDetail.setTradeTimeTs(System.currentTimeMillis());
                gfUserBalanceDetail.setTradeType("11");
                gfUserBalanceDetail.setUserId(grandFatherUser.getUserId());
                gfUserBalanceDetail.setUserBalance(gfUserAccount.getBalance());
                userBalanceDetailService.save(gfUserBalanceDetail);

            }
        }
        // ==========================计算返佣结束===============================
        // 用户池入账
        BigDecimal userFee = Arith.add(fget, gfget);// 获取父亲爷爷相加后的代理返佣金额
        fundsAccountingService.setCashPoolAdd(Constant.CashPool._USER.getValue(), userFee);
        // 副本池入账
        BigDecimal dungeonFee = Arith.multiply(order.getTransactionFee(), Constant.CashPoolScale._DUNGEON.getValue());// 获取该订单金额的75%
        dungeonFee = Arith.subtract(dungeonFee, userFee);// 副本金额 = 75%金额-代理费用
        fundsAccountingService.setCashPoolAdd(Constant.CashPool._DUNGEON.getValue(), dungeonFee);
        // 回购池入账
        BigDecimal repoFee = Arith.multiply(order.getTransactionFee(), Constant.CashPoolScale._REPO.getValue());// 获取该订单金额的20%
        fundsAccountingService.setCashPoolAdd(Constant.CashPool._REPO.getValue(), repoFee);
    }

    @Override
    public GmUserVipLevelEntity queryById(Long vipLevelId) {
        return baseMapper.selectById(vipLevelId);
    }

    @Override
    public List<GmUserVipLevelEntity> vipLevelList(UserEntity user) {
        // 1.查询当前会员等级
        return baseMapper.selectList(new QueryWrapper<GmUserVipLevelEntity>()
                .orderByAsc("VIP_LEVEL_CODE"));
    }
}
