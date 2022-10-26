package com.gm.modules.order.controller;

import java.util.Arrays;
import java.util.Map;

import com.gm.common.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gm.modules.order.entity.TransactionOrderEntity;
import com.gm.modules.order.service.TransactionOrderService;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;



/**
 * 抽奖订单
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-03-12 19:02:34
 */
@RestController
@RequestMapping("order/transactionorder")
public class TransactionOrderController {
    @Autowired
    private TransactionOrderService transactionOrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("order:transactionorder:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = transactionOrderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{transactionOrderId}")
    @RequiresPermissions("order:transactionorder:info")
    public R info(@PathVariable("transactionOrderId") Long transactionOrderId){
        TransactionOrderEntity transactionOrder = transactionOrderService.getById(transactionOrderId);

        return R.ok().put("transactionOrder", transactionOrder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("order:transactionorder:save")
    public R save(@RequestBody TransactionOrderEntity transactionOrder){
        transactionOrderService.save(transactionOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("order:transactionorder:update")
    public R update(@RequestBody TransactionOrderEntity transactionOrder){
        ValidatorUtils.validateEntity(transactionOrder);
        transactionOrderService.updateById(transactionOrder);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("order:transactionorder:delete")
    public R delete(@RequestBody Long[] transactionOrderIds){
        transactionOrderService.removeByIds(Arrays.asList(transactionOrderIds));

        return R.ok();
    }

}
