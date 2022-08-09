$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/gmuserwithdraw/list',
        datatype: "json",
        colModel: [			
			{ label: 'withdrawId', name: 'withdrawId', index: 'WITHDRAW_ID', width: 50, key: true },
			{ label: '用户ID', name: 'userId', index: 'USER_ID', width: 80 }, 			
			{ label: '提现金额', name: 'withdrawMoney', index: 'WITHDRAW_MONEY', width: 80 }, 			
			{ label: '提现状态（0：申请提现，1：提现成功，2：提现失败）', name: 'status', index: 'STATUS', width: 80 }, 			
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
		gmUserWithdraw: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.gmUserWithdraw = {};
		},
		update: function (event) {
			var withdrawId = getSelectedRow();
			if(withdrawId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(withdrawId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.gmUserWithdraw.withdrawId == null ? "user/gmuserwithdraw/save" : "user/gmuserwithdraw/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmUserWithdraw),
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
			var withdrawIds = getSelectedRows();
			if(withdrawIds == null){
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
                        url: baseURL + "user/gmuserwithdraw/delete",
                        contentType: "application/json",
                        data: JSON.stringify(withdrawIds),
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
		getInfo: function(withdrawId){
			$.get(baseURL + "user/gmuserwithdraw/info/"+withdrawId, function(r){
                vm.gmUserWithdraw = r.gmUserWithdraw;
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