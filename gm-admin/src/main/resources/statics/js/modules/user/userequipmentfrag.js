$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/userequipmentfrag/list',
        datatype: "json",
        colModel: [			
			{ label: 'userEquipmentFragId', name: 'userEquipmentFragId', index: 'USER_EQUIPMENT_FRAG_ID', width: 50, key: true,hidden: true },
            { label: '会员名称', name: 'userName', width: 80 },
            { label: '装备卷轴碎片名称', name: 'equipName', width: 80 },
            { label: '装备卷轴碎片稀有度', name: 'equipRarecode', width: 80, formatter: function (value, options, row) {
                var erc = '';
                if (value == '1') {
                    erc = '白色';
                } else if (value == '2') {
                    erc = '绿色';
                } else if (value == '3') {
                    erc = '蓝色';
                } else if (value == '4') {
                    erc = '紫色';
                } else if (value == '5') {
                    erc = '橙色';
                }
                return erc;
            }
            },
			{ label: '玩家获得碎片数量', name: 'userEquipFragNum', index: 'USER_EQUIP_FRAG_NUM', width: 80 },
            { label: '来源ID', name: 'sourceId', width: 80 },
            { label: '来源类型', name: 'fromType', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-info" style="background-color: #ed5565;">副本</span>';
                } else if (value == '1') {
                    return '<span class="label label-info">抽奖</span>';
                }
            }
            },
			{ label: '状态', name: 'status', index: 'STATUS', width: 80 , formatter: function (value, options, row) {
                    if (value == '0') {
                        return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                    } else if (value == '1') {
                        return '<span class="label label-info">未使用</span>';//启用
                    } else if (value == '2')  {
                        return '<span class="label label-info" style="background-color: #ddd;">已消耗</span>';//已消耗
                    }
                }
            },
            { label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80, formatter: function (value, options, row) {
                return value;
            }
            },
            { label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80, formatter: function (value, options, row) {
                return value;
            }
            }
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
            equipName: '',
            userName: '',
            status: '',
            fragNum: ''
        },
		userEquipmentFrag: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.userEquipmentFrag = {};
		},
		update: function (event) {
			var userEquipmentFragId = getSelectedRow();
			if(userEquipmentFragId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(userEquipmentFragId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.userEquipmentFrag.userEquipmentFragId == null ? "user/userequipmentfrag/save" : "user/userequipmentfrag/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.userEquipmentFrag),
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
			var userEquipmentFragIds = getSelectedRows();
			if(userEquipmentFragIds == null){
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
                        url: baseURL + "user/userequipmentfrag/delete",
                        contentType: "application/json",
                        data: JSON.stringify(userEquipmentFragIds),
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
		getInfo: function(userEquipmentFragId){
			$.get(baseURL + "user/userequipmentfrag/info/"+userEquipmentFragId, function(r){
                vm.userEquipmentFrag = r.userEquipmentFrag;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData: {'status' : vm.q.status, 'equipName' : vm.q.equipName, 'userName' : vm.q.userName, 'fragNum' : vm.q.fragNum},
                page:page
            }).trigger("reloadGrid");
		}
	}
});