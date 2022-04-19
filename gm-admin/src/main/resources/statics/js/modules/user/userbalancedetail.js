$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/userbalancedetail/list',
        datatype: "json",
        colModel: [			
			{ label: 'userBalanceDetailId', name: 'userBalanceDetailId', index: 'USER_BALANCE_DETAIL_ID', width: 50, key: true },
			{ label: '用户id', name: 'userId', index: 'USER_ID', width: 80 }, 			
			{ label: '交易类型：00提现,01副本产出,02英雄抽奖,03装备抽奖,04经验抽奖,11返点，12提现手续费，15后台取款,16提现冻结,17提现解冻,18后台取款冻结,19,后台取款解冻', name: 'tradeType', index: 'TRADE_TYPE', width: 80 }, 			
			{ label: '交易金额', name: 'amount', index: 'AMOUNT', width: 80 }, 			
			{ label: '交易时间', name: 'tradeTime', index: 'TRADE_TIME', width: 80 }, 			
			{ label: '交易时间', name: 'tradeTimeTs', index: 'TRADE_TIME_TS', width: 80 }, 			
			{ label: '交易描述', name: 'tradeDesc', index: 'TRADE_DESC', width: 80 }, 			
			{ label: '交易后的余额', name: 'userBalance', index: 'USER_BALANCE', width: 80 }, 			
			{ label: '交易类型id：提现id,副本产出id,抽奖id,市场交易ID,代理返佣id', name: 'sourceId', index: 'SOURCE_ID', width: 80 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		userBalanceDetail: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.userBalanceDetail = {};
		},
		update: function (event) {
			var userBalanceDetailId = getSelectedRow();
			if(userBalanceDetailId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(userBalanceDetailId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.userBalanceDetail.userBalanceDetailId == null ? "user/userbalancedetail/save" : "user/userbalancedetail/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.userBalanceDetail),
                    success: function(r){
                        if(r.code === 0){
                             layer.msg("操作成功", {icon: 1});
                             vm.reload();
                             $('#btnSaveOrUpdate').button('reset');
                             $('#btnSaveOrUpdate').dequeue();
                        }else{
                            layer.alert(r.msg);
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        }
                    }
                });
			});
		},
		del: function (event) {
			var userBalanceDetailIds = getSelectedRows();
			if(userBalanceDetailIds == null){
				return ;
			}
			var lock = false;
            layer.confirm('确定要删除选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
		            $.ajax({
                        type: "POST",
                        url: baseURL + "user/userbalancedetail/delete",
                        contentType: "application/json",
                        data: JSON.stringify(userBalanceDetailIds),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
                                $("#jqGrid").trigger("reloadGrid");
                            }else{
                                layer.alert(r.msg);
                            }
                        }
				    });
			    }
             }, function(){
             });
		},
		getInfo: function(userBalanceDetailId){
			$.get(baseURL + "user/userbalancedetail/info/"+userBalanceDetailId, function(r){
                vm.userBalanceDetail = r.userBalanceDetail;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});