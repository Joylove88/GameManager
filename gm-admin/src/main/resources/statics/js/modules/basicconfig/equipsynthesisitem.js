$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/equipsynthesisitem/list',
        datatype: "json",
        colModel: [			
			{ label: 'equipSynthesisItemId', name: 'equipSynthesisItemId', index: 'EQUIP_SYNTHESIS_ITEM_ID', width: 50, key: true },
			{ label: '装备名称', name: 'equipName', width: 80 },
			{ label: '装备稀有度', name: 'equipRare', width: 50 , formatter: function (value, options, row) {
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
			{ label: '装备卷轴名称', name: 'equipFragName', width: 100 , formatter: function (value, options, row) {
                return value + " *" + row.equipFragNum;
            }
            },
			{ label: '需要的卷轴数量', name: 'equipFragNum', index: 'EQUIP_FRAG_NUM', width: 50 },
			{ label: '装备合成项1', name: 'equipItemName1', width: 80 },
			{ label: '装备合成项2', name: 'equipSynthesisItem2', index: 'EQUIP_SYNTHESIS_ITEM2', width: 80 },
			{ label: '装备合成项3', name: 'equipSynthesisItem3', index: 'EQUIP_SYNTHESIS_ITEM3', width: 80 },
			{ label: '白装', name: 'equipWhite', index: 'EQUIP_WHITE', width: 80 },
			{ label: '蓝装', name: 'equipBlue', index: 'EQUIP_BLUE', width: 80 },
			{ label: '状态', name: 'status', index: 'STATUS', width: 50 , formatter: function (value, options, row) {
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
		equipSynthesisItem: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.equipSynthesisItem = {};
		},
		update: function (event) {
			var equipSynthesisItemId = getSelectedRow();
			if(equipSynthesisItemId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(equipSynthesisItemId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.equipSynthesisItem.equipSynthesisItemId == null ? "basicconfig/equipsynthesisitem/save" : "basicconfig/equipsynthesisitem/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.equipSynthesisItem),
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
			var equipSynthesisItemIds = getSelectedRows();
			if(equipSynthesisItemIds == null){
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
                        url: baseURL + "basicconfig/equipsynthesisitem/delete",
                        contentType: "application/json",
                        data: JSON.stringify(equipSynthesisItemIds),
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
		getInfo: function(equipSynthesisItemId){
			$.get(baseURL + "basicconfig/equipsynthesisitem/info/"+equipSynthesisItemId, function(r){
                vm.equipSynthesisItem = r.equipSynthesisItem;
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