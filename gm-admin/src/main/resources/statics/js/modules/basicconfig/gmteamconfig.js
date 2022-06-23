$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/gmteamconfig/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'ID', width: 50, key: true },
			{ label: '会员名称', name: 'userAddress', width: 80 },
			{ label: '队伍名称', name: 'teamName', index: 'TEAM_NAME', width: 80 }, 			
			{ label: '队伍顺序', name: 'teamSolt', index: 'TEAM_SOLT', width: 80 }, 			
			{ label: '英雄1ID', name: 'hero1Id', index: 'HERO1_ID', width: 80 }, 			
			{ label: '英雄2ID', name: 'hero2Id', index: 'HERO2_ID', width: 80 }, 			
			{ label: '英雄3ID', name: 'hero3Id', index: 'HERO3_ID', width: 80 }, 			
			{ label: '英雄4ID', name: 'hero4Id', index: 'HERO4_ID', width: 80 }, 			
			{ label: '英雄5ID', name: 'hero5Id', index: 'HERO5_ID', width: 80 }, 			
            { label: '战斗状态', name: 'status', index: 'STATUS', width: 80 , formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ddd;">未战斗</span>';//禁用
                } else  if (value == '1') {
                    return '<span class="label label-info">战斗中</span>';//战斗中
                } else {
                    return '<span class="label label-info">战斗结束</span>';//战斗结束
                }
            }
            },
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
            userAddress: '',
            status: ''
        },
		gmTeamConfig: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.gmTeamConfig = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.gmTeamConfig.id == null ? "basicconfig/gmteamconfig/save" : "basicconfig/gmteamconfig/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmTeamConfig),
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
			var ids = getSelectedRows();
			if(ids == null){
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
                        url: baseURL + "basicconfig/gmteamconfig/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
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
		getInfo: function(id){
			$.get(baseURL + "basicconfig/gmteamconfig/info/"+id, function(r){
                vm.gmTeamConfig = r.gmTeamConfig;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData: {'status': vm.q.status, 'userAddress': vm.q.userAddress},
                page:page
            }).trigger("reloadGrid");
		}
	}
});