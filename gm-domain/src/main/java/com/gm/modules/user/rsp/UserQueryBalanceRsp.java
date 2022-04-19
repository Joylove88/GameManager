package com.gm.modules.user.rsp;

import lombok.Data;

@Data
public class UserQueryBalanceRsp {
	private Double totalAmount;
	private Double balance;
	private Double frozen;

}
