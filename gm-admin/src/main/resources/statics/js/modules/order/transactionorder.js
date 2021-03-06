$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'order/transactionorder/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmTransactionOrderId', name: 'gmTransactionOrderId', index: 'GM_TRANSACTION_ORDER_ID', width: 50, key: true },
			{ label: '用户ID', name: 'gmUserId', index: 'GM_USER_ID', width: 80 }, 			
			{ label: '物品类型', name: 'itemType', index: 'ITEM_TYPE', width: 80 },
			{ label: '链上交易HASH', name: 'transactionHash', index: 'TRANSACTION_HASH', width: 80 }, 			
			{ label: '抽到的物品信息', name: 'itemData', index: 'ITEM_DATA', width: 80 }, 			
			{ label: '状态', name: 'status', index: 'STATUS', width: 80 },
			{ label: '消耗金额', name: 'transactionFee', index: 'TRANSACTION_FEE', width: 80 }, 			
			{ label: 'GAS费', name: 'transactionGasFee', index: 'TRANSACTION_GAS_FEE', width: 80 }, 			
			{ label: '抽奖类型', name: 'lottyType', index: 'LOTTY_TYPE', width: 80 },
			{ label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80 },
			{ label: '创建时间', name: 'createTimeTs', index: 'CREATE_TIME_TS', width: 80 }, 			
			{ label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80 }, 			
			{ label: '修改时间', name: 'updateTimeTs', index: 'UPDATE_TIME_TS', width: 80 }			
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
		transactionOrder: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.transactionOrder = {};
		},
		update: function (event) {
			var gmTransactionOrderId = getSelectedRow();
			if(gmTransactionOrderId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(gmTransactionOrderId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.transactionOrder.gmTransactionOrderId == null ? "order/transactionorder/save" : "order/transactionorder/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.transactionOrder),
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
			var gmTransactionOrderIds = getSelectedRows();
			if(gmTransactionOrderIds == null){
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
                        url: baseURL + "order/transactionorder/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmTransactionOrderIds),
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
		getInfo: function(gmTransactionOrderId){
			$.get(baseURL + "order/transactionorder/info/"+gmTransactionOrderId, function(r){
                vm.transactionOrder = r.transactionOrder;
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