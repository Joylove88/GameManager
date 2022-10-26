$(function () {
    vm.getHeroInfoList();
    vm.getEquipmentInfoList();
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/heroequipment/list',
        datatype: "json",
        colModel: [			
			{ label: 'heroEquipmentId', name: 'heroEquipmentId', index: 'HERO_EQUIPMENT_ID', width: 50, key: true },
			{ label: '英雄', name: 'heroName', width: 80 },
			{ label: '装备', name: 'equipName', width: 80 },
            { label: '装备稀有度', name: 'equipRarecode', width: 80, formatter: function (value, options, row) {
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
			{ label: '装备位置', name: 'equipSolt', width: 80 },
			{ label: '状态', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
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
        heroInfoList: {},
        equipmentInfoList: {},
		heroEquipment: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.heroEquipment = {heroId: '', equipId: '', status: '1', equipSolt: 1};
            $('#searchEngine').val('');
		},
		update: function (event) {
			var heroEquipmentId = getSelectedRow();
			if(heroEquipmentId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(heroEquipmentId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.heroEquipment.heroEquipmentId == null ? "basicconfig/heroequipment/save" : "basicconfig/heroequipment/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.heroEquipment),
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
			var heroEquipmentIds = getSelectedRows();
			if(heroEquipmentIds == null){
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
                        url: baseURL + "basicconfig/heroequipment/delete",
                        contentType: "application/json",
                        data: JSON.stringify(heroEquipmentIds),
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
		getInfo: function(heroEquipmentId){
			$.get(baseURL + "basicconfig/heroequipment/info/"+heroEquipmentId, function(r){
                vm.heroEquipment = r.heroEquipment;
                $('#searchEngine').val(vm.heroEquipment.equipId);
                vm.getEquipmentInfoList();
            });
		},
        getHeroInfoList: function () {
            $.ajax({
                type: "POST",
                url: baseURL + "basicconfig/heroinfo/getHeroInfoList",
                contentType: "application/json",
                success: function (r) {
                    if (r.code == 0) {
                        vm.heroInfoList = r.list;
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        getSEValue: function () {
            var getSEValue = $('#searchEngineD .option-selected').data('value');
            vm.heroEquipment.equipId = getSEValue;
        },
        getEquipmentInfoList: function () {
            $.get(baseURL + "basicconfig/equipmentinfo/getEquipmentInfoList", function (r) {
                if (r.code == 0) {
                    vm.equipmentInfoList = r.list;
                    $('#searchEngine').html('');
                    var searchEngine = $('#searchEngine');
                    searchEngine.append("<option value='' selected='selected'>请选择</option>");
                    for (var i = 0; i < vm.equipmentInfoList.length; i++){
                        var selected =  vm.equipmentInfoList[i].equipId == vm.heroEquipment.equipId ? 'selected="selected"' : '';
                        searchEngine.append("<option "+selected+" value='"+vm.equipmentInfoList[i].equipId+"'>"+vm.equipmentInfoList[i].equipName+"</option>");
                    }
                    $('#searchEngine').comboSelect();


                } else {
                    alert(r.msg);
                }
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