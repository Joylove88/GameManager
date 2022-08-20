package com.gm.common.Constant;

public enum ErrorCode {

	USER_NOT_TOKEN(50,"Failed to get user token!"),//获取用户Token失败
	USER_GET_FAIL(51,"Failed to get user information!"),//获取用户信息失败
	EXP_GET_FAIL(51,"Failed to get level information!"),//获取等级信息失败
	USER_GET_BAL_FAIL(51,"Failed to get user balance!"),//获取用户余额失败
	USER_NOT_EXIST(100,"The account does not exist, please contact customer service!"),//账号不存在，请联系客服！
	USER_PASSWORD_ERROR(101,"wrong password!"),//密码错误
	USER_ACCOUNT_EXPIRED(102,"Username has expired, please contact customer service!"),//用户名已失效，请联系客服！
	USER_HAS_EXIST(103,"Username already exists!"),//用户名已存在
	SIGN_ADDRESS_EXCEPTION(104,"address exception!"),
	SIGN_MSG_EXCEPTION(105,"msg exception!"),
	SIGN_SIGNEDMSG_EXCEPTION(106,"signedmsg exception!"),
	SIGN_HASH_EXCEPTION(107,"transaction hash exception!"),
	EXP_NUM_NOT_NULL(108,"Please select the amount of experience potion!"),//请选择经验药水的数量
	EXP_RARE_NOT_NULL(108,"Please select the type of experience potion!"),//请选择经验药水的种类
	ADDRESS_HAS_EXIST(109,"address already exists!"),//地址已存在
	INVITE_ADDRESS_NOT_EXIST(110,"invite address not exists!"),//上级地址不存在
	WITHDRAW_OVER_TIMES(111,"24 hours only once!"),//24小时只能提现一次
	WITHDRAW_OVER_MONEY(112,"withdraw over!"),//提现超额



	//支付相关
	//前端充值

	PAY_WAY_TYPE_NOT_ENABLE(1001, "支付渠道已禁用,请选择其他支付渠道!"),
	PAY_WAY_TYPE_EXCESS(1002, "支付渠道已超额,请选择其他支付渠道!"),
	PAY_WAY_TYPE_NOT_FIND(1003, "该支付渠道不存在,请选择其他支付渠道![请检查渠道ID]"),
	RECHARGE_EXCEED_QUOTA(1004, "充值金额超出限额,请充值:"),//[min->max]
	COMPANY_ACCOUNT_NOT_ENABLE(1005, "公司账户已禁用,请选择其他账户!"),
	COMPANY_ACCOUNT_EXCESS(1006, "账户已超额,请选择其他账户!"),
	COMPANY_ACCOUNT_NOT_FIND(1007, "该账户不存在,请选择其他账户![请检查账户ID]"),
	RECHARGE_TOO_OFTEN(1008, "充值过于频繁,"),//m分钟可充值n次
	RECHARGE_BANK_CODE_IS_NULL(1009, "银行编码不能为空!"),
	TEST_USER_LEVEL_DONT_RECHARGE(1010, "测试层级不能前端充值!"),
	HAVE_NO_HANDLE_RECHARGE_ORDER(1011, "您还有未处理的充值订单,请先处理!"),
	//前端提现
	USER_ACCOUNT_PASSWORD_ERROR(1021, "用户资金密码错误!"),
	NOT_FIND_TAKE_FEE_CONFIG(1022, "该会员所属层级未设置出款配置!"),
	TAKE_FEE_EXCEED_QUOTA(1023, "提现金额超出限额,请提现:"),//[min->max]
	TAKE_FEE_TODAY_MAX(1024, "今日提现金额已超出额度,请明天再提现!"),
	TAKE_FEE_TODAY_NUMBER_MAX(1025, "今日提现次数已超出额度,请明天再提现!"),
	TAKE_FEE_TOO_MORE(1027, "今日提现额度不足,请降低提现金额!"),
	TODAY_TAKE_FEE_TIME_EXCEED(1028, "次,今日不能提现,如有问题请联系客服!"),
	TAKE_FEE_TOO_OFTEN(1029, "提现过于频繁,"),//m分钟可提现n次
	BALANCE_NOT_ENOUGH(1030, "Insufficient account available balance!"),
	TEST_USER_LEVEL_DONT_TAKE_FEE(1031, "测试层级不能前端提现!"),
	THIS_TIME_DONT_TAKE_FEE(1032, "该时间段内不能提现!"),

	FILE_SIZE_ERROR(10303, "文件容量不能超过15MB!"),

	UPLOAD_TO_IOEXCEPTION(10300, "上传文件读写异常！"),
	UPLOAD_TO_FAILD(10301, "上传文件失败！"),
	DENGEROUS_UPDATE_FAILD(1089, "审核通过用户不允许操作!"),
	GET_FTPCONFIG_FAILD(10302, "获取FTP服务器配置失败！");


	private int code;
	private String desc;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	ErrorCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}
}
