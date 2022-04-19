$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/equipmentfrag/list',
        datatype: "json",
        colModel: [			
			{ label: 'equipmentFragId', name: 'equipmentFragId', index: 'EQUIPMENT_FRAG_ID', width: 50, key: true },
			{ label: '装备碎片名称', name: 'equipName', index: 'EQUIPMENT_ID', width: 80 },
			{ label: '装备碎片稀有度', name: 'equipRarecode', width: 80 , formatter: function (value, options, row) {
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
			{ label: '装备碎片数量 默认1', name: 'equipmentFragNum', index: 'EQUIPMENT_FRAG_NUM', width: 80 },
			{ label: '状态', name: 'status', index: 'STATUS', width: 80 , formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else {
                    return '<span class="label label-info">启用</span>';//启用
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
        rowNum: 50,
		rowList : [50,100,500],
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
            equipRarecode: '',
            status: ''
        },
		equipmentFrag: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.equipmentFrag = {itemValuation: 0, equipmentFragNum: 1};
		},
		update: function (event) {
			var equipmentFragId = getSelectedRow();
			if(equipmentFragId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(equipmentFragId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.equipmentFrag.equipmentFragId == null ? "basicconfig/equipmentfrag/save" : "basicconfig/equipmentfrag/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.equipmentFrag),
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
			var equipmentFragIds = getSelectedRows();
			if(equipmentFragIds == null){
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
                        url: baseURL + "basicconfig/equipmentfrag/delete",
                        contentType: "application/json",
                        data: JSON.stringify(equipmentFragIds),
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
		getInfo: function(equipmentFragId){
			$.get(baseURL + "basicconfig/equipmentfrag/info/"+equipmentFragId, function(r){
                vm.equipmentFrag = r.equipmentFrag;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
			    postData: {'status': vm.q.status, 'equipName': vm.q.equipName, 'equipRarecode': vm.q.equipRarecode},
                page:page
            }).trigger("reloadGrid");
		}
	}
});