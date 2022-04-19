$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/useraccount/list',
        datatype: "json",
        colModel: [			
			{ label: 'accountId', name: 'accountId', index: 'ACCOUNT_ID', width: 50, key: true },
			{ label: '用户ID', name: 'userId', index: 'USER_ID', width: 80 }, 			
			{ label: '总资产', name: 'totalAmount', index: 'TOTAL_AMOUNT', width: 80 }, 			
			{ label: '可用余额', name: 'balance', index: 'BALANCE', width: 80 }, 			
			{ label: '冻结金额，一般指提现金额', name: 'frozen', index: 'FROZEN', width: 80 }, 			
			{ label: '账户状态', name: 'status', index: 'STATUS', width: 80 },
			{ label: '币种', name: 'currency', index: 'CURRENCY', width: 80 }			
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
		userAccount: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.userAccount = {};
		},
		update: function (event) {
			var accountId = getSelectedRow();
			if(accountId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(accountId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.userAccount.accountId == null ? "user/useraccount/save" : "user/useraccount/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.userAccount),
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
			var accountIds = getSelectedRows();
			if(accountIds == null){
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
                        url: baseURL + "user/useraccount/delete",
                        contentType: "application/json",
                        data: JSON.stringify(accountIds),
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
		getInfo: function(accountId){
			$.get(baseURL + "user/useraccount/info/"+accountId, function(r){
                vm.userAccount = r.userAccount;
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