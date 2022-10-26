$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/probability/list',
        datatype: "json",
        colModel: [			
			{ label: 'probabilityId', name: 'probabilityId', index: 'PROBABILITY_ID', width: 50, key: true },
			{ label: '概率', name: 'pr', index: 'PR', width: 80 },
			{ label: '概率等级', name: 'prLv', index: 'PR_LV', width: 80 },
			{ label: '说明', name: 'explanation', width: 80 },
			{ label: '类型', name: 'prType', index: 'PR_TYPE', width: 80 , formatter: function (value, options, row) {
                if (value == '1') {
                    return '<span class="label label-info">英雄</span>';//英雄
                } else if (value == '2') {
                    return '<span class="label label-info" style="background-color: #edcb7d;">装备</span>';//装备
                } else if (value == '3') {
                    return '<span class="label label-info">经验丸</span>';//经验丸
                }
            }
            },
			{ label: '状态', name: 'status', index: 'STATUS', width: 80 , formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else if (value == '1') {
                    return '<span class="label label-info">正常</span>';//启用
                }
            }
            },
			{ label: '创建人', name: 'createUser', index: 'CREATE_USER', width: 80 },
			{ label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80 }, 			
			{ label: '修改人', name: 'updateUser', index: 'UPDATE_USER', width: 80 },
			{ label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80 }, 			
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
        q: {
            prType: ''
        },
		probability: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.probability = {prLv : 0, pr : 0, status: '1', prType : 1};
		},
		update: function (event) {
			var probabilityId = getSelectedRow();
			if(probabilityId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(probabilityId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.probability.probabilityId == null ? "basicconfig/probability/save" : "basicconfig/probability/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.probability),
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
			var probabilityIds = getSelectedRows();
			if(probabilityIds == null){
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
                        url: baseURL + "basicconfig/probability/delete",
                        contentType: "application/json",
                        data: JSON.stringify(probabilityIds),
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
		getInfo: function(probabilityId){
			$.get(baseURL + "basicconfig/probability/info/"+probabilityId, function(r){
                vm.probability = r.probability;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
			    postData:{'prType': vm.q.prType},
                page:page
            }).trigger("reloadGrid");
		}
	}
});