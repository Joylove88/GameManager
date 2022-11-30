package com.gm.modules.user.controller;

import com.gm.common.exception.RRException;
import com.gm.common.utils.Constant;
import com.gm.common.utils.PageUtils;
import com.gm.common.utils.R;
import com.gm.common.validator.ValidatorUtils;
import com.gm.modules.sys.controller.AbstractController;
import com.gm.modules.user.entity.GmUserWithdrawEntity;
import com.gm.modules.user.service.GmUserWithdrawService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 提现表
 *
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-08-09 19:13:43
 */
@RestController
@RequestMapping("user/gmuserwithdraw")
public class GmUserWithdrawController extends AbstractController{
    @Autowired
    private GmUserWithdrawService gmUserWithdrawService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("user:gmuserwithdraw:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = gmUserWithdrawService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{withdrawId}")
    @RequiresPermissions("user:gmuserwithdraw:info")
    public R info(@PathVariable("withdrawId") Long withdrawId) {
        GmUserWithdrawEntity gmUserWithdraw = gmUserWithdrawService.getById(withdrawId);

        return R.ok().put("gmUserWithdraw", gmUserWithdraw);
    }

    /**
     * 审核通过
     */
    @RequestMapping("/pass")
    @RequiresPermissions("user:gmuserwithdraw:pass")
    public R checkPass(@RequestBody GmUserWithdrawEntity gmUserWithdraw) {
        ValidatorUtils.validateEntity(gmUserWithdraw);
        // 查询该笔订单
        gmUserWithdraw = gmUserWithdrawService.getById(gmUserWithdraw.getWithdrawId());
        if (gmUserWithdraw == null) {
            throw new RRException("order not exit");
        }
        if (Constant.WithdrawStatus.APPLY.getValue() != gmUserWithdraw.getStatus()) {
            throw new RRException("order not apply");
        }
        gmUserWithdrawService.checkPass(gmUserWithdraw,getUserId());

        return R.ok();
    }

    /**
     * 审核失败
     */
    @RequestMapping("/fail")
    @RequiresPermissions("user:gmuserwithdraw:fail")
    public R checkFail(@RequestBody GmUserWithdrawEntity gmUserWithdraw) {
        ValidatorUtils.validateEntity(gmUserWithdraw);
        // 查询该笔订单
        gmUserWithdraw = gmUserWithdrawService.getById(gmUserWithdraw.getWithdrawId());
        if (gmUserWithdraw == null) {
            throw new RRException("order not exit");
        }
        if (Constant.WithdrawStatus.APPLY.getValue() != gmUserWithdraw.getStatus()) {
            throw new RRException("order not apply");
        }
        gmUserWithdrawService.checkFail(gmUserWithdraw,getUserId());

        return R.ok();
    }

}
