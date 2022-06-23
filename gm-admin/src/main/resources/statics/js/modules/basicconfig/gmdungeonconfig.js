$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/gmdungeonconfig/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'ID', width: 50, key: true },
			{ label: '副本名称', name: 'dungeonName', index: 'DUNGEON_NAME', width: 80 }, 			
			{ label: '副本描述', name: 'dungeonDescription', index: 'DUNGEON_DESCRIPTION', width: 80 }, 			
			{ label: '怪物数量随机范围', name: 'monsterNum', width: 80 },
			{ label: '副本最小等级限制', name: 'dungeonLevelMin', index: 'DUNGEON_LEVEL_MIN', width: 80 },
			{ label: '副本最大等级限制', name: 'dungeonLevelMax', index: 'DUNGEON_LEVEL_MAX', width: 80 },
			{ label: '副本奖励等级', name: 'dungeonAward', index: 'DUNGEON_AWARD', width: 80 }, 			
			{ label: '副本推荐战力', name: 'dungeonPower', index: 'DUNGEON_POWER', width: 80 }, 			
			{ label: '所需体力(默认6)', name: 'requiresStamina', index: 'REQUIRES_STAMINA', width: 80 }, 			
			{ label: '产出金币范围', name: 'rangeGoldCoins', index: 'RANGE_GOLD_COINS', width: 80 }, 			
			{ label: '产出装备范围', name: 'rangeEquip', index: 'RANGE_EQUIP', width: 80 }, 			
			{ label: '产出道具范围', name: 'rangeProps', index: 'RANGE_PROPS', width: 80 }, 			
			{ label: '地图图片URL', name: 'dungeonImgUrl', index: 'DUNGEON_IMG_URL', width: 80 }, 			
			{ label: '状态', name: 'status', index: 'STATUS', width: 80 },
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
            status: ''
        },
		gmDungeonConfig: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.gmDungeonConfig = {};
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
                var url = vm.gmDungeonConfig.id == null ? "basicconfig/gmdungeonconfig/save" : "basicconfig/gmdungeonconfig/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmDungeonConfig),
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
                        url: baseURL + "basicconfig/gmdungeonconfig/delete",
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
			$.get(baseURL + "basicconfig/gmdungeonconfig/info/"+id, function(r){
                vm.gmDungeonConfig = r.gmDungeonConfig;
            });
		},
        testFight: function (type) {
            $.ajax({
                type: "POST",
                url: baseURL + "basicconfig/gmdungeonconfig/testFight",
                contentType: "application/json",
                data: JSON.stringify(vm.gmDungeonConfig),
                success: function(r){
                    if(r.code === 0){
                        layer.msg("战斗完成", {icon: 1});
                        // vm.reload();
                    }else{
                        layer.alert(r.msg);
                    }
                }
            });
        },
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData: {'status': vm.q.status},
                page:page
            }).trigger("reloadGrid");
		}
	}
});