package com.gm.modules.email.service;

import com.gm.modules.drawGift.service.DrawGiftService;
import com.gm.modules.fundsAccounting.service.FundsAccountingService;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.modules.sys.service.SysConfigService;
import com.gm.modules.sys.service.SysDictService;
import com.gm.modules.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 链上业务类
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
@Service("emailService")
public class emailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(emailService.class);
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

}

