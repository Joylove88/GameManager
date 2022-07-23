$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/gmcombatrecord/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'ID', width: 50, key: true },
			{ label: '用户ID', name: 'userId', index: 'USER_ID', width: 80 }, 			
			{ label: '副本ID', name: 'dungeonId', index: 'DUNGEON_ID', width: 80 }, 			
			{ label: '队伍ID', name: 'teamId', index: 'TEAM_ID', width: 80 }, 			
			{ label: '获得的金币', name: 'getGoldCoins', index: 'GET_GOLD_COINS', width: 80 }, 			
			{ label: '获得的装备', name: 'getEquip', index: 'GET_EQUIP', width: 80 }, 			
			{ label: '获得的道具', name: 'getProps', index: 'GET_PROPS', width: 80 }, 			
			{ label: '战斗描述', name: 'combatDescription', index: 'COMBAT_DESCRIPTION', width: 80 }, 			
			{ label: '获得的英雄经验', name: 'getExp', index: 'GET_EXP', width: 80 }, 			
			{ label: '获得的用户经验', name: 'getUserExp', index: 'GET_USER_EXP', width: 80 }, 			
			{ label: '战斗结果状态（0：YOULOSE，1：YOUWIN，2：战斗中）', name: 'status', index: 'STATUS', width: 80 }, 			
			{ label: '战斗开始时间', name: 'startTime', index: 'START_TIME', width: 80 },
			{ label: '战斗结束时间', name: 'endTime', index: 'END_TIME', width: 80 },
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
		gmCombatRecord: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.gmCombatRecord = {};
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
                var url = vm.gmCombatRecord.id == null ? "basicconfig/gmcombatrecord/save" : "basicconfig/gmcombatrecord/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmCombatRecord),
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
                        url: baseURL + "basicconfig/gmcombatrecord/delete",
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
			$.get(baseURL + "basicconfig/gmcombatrecord/info/"+id, function(r){
                vm.gmCombatRecord = r.gmCombatRecord;
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