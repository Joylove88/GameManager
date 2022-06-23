$(function () {
    vm.getDungeons();
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/gmdungeonevent/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'ID', width: 50, key: true },
			{ label: '副本ID', name: 'dungeonId', index: 'DUNGEON_ID', width: 80 }, 			
			{ label: '事件名称', name: 'eventName', index: 'EVENT_NAME', width: 80 }, 			
			{ label: '事件描述', name: 'eventDescription', index: 'EVENT_DESCRIPTION', width: 80 },
            { label: '状态', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else {
                    return '<span class="label label-info">启用</span>';//启用
                }
            }
            },
			{ label: '事件等级', name: 'eventLevel', index: 'EVENT_LEVEL', width: 80 }, 			
			{ label: '事件触发概率', name: 'eventPron', index: 'EVENT_PRON', width: 80 }, 			
			{ label: '创建人', name: 'createUser', index: 'CREATE_USER', width: 80 }, 			
			{ label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80 }, 			
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
		gmDungeonEvent: {},
        dungeonList: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
        getDungeons: function () {
            $.ajax({
                type: "POST",
                url: baseURL + "basicconfig/gmdungeonconfig/getDungeons",
                contentType: "application/json",
                success: function (r) {
                    if (r.code == 0) {
                        vm.dungeonList = r.list;
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.gmDungeonEvent = {dungeonId : ''};
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
                var url = vm.gmDungeonEvent.id == null ? "basicconfig/gmdungeonevent/save" : "basicconfig/gmdungeonevent/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmDungeonEvent),
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
                        url: baseURL + "basicconfig/gmdungeonevent/delete",
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
			$.get(baseURL + "basicconfig/gmdungeonevent/info/"+id, function(r){
                vm.gmDungeonEvent = r.gmDungeonEvent;
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