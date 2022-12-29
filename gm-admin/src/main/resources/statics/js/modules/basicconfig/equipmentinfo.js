$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/equipmentinfo/list',
        datatype: "json",
        colModel: [			
			{ label: 'equipId', name: 'equipId', index: 'EQUIP_ID', width: 50, key: true },
			{ label: '装备名称', name: 'equipName', index: 'EQUIP_NAME', width: 80 }, 			
			{ label: '装备稀有度', name: 'equipRarecode', index: 'EQUIP_RARECODE', width: 80, formatter: function (value, options, row) {
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
			{ label: '装备等级', name: 'equipLevel', index: 'EQUIP_LEVEL', width: 80 },
			{ label: '状态', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else {
                    return '<span class="label label-info">启用</span>';//启用
                }
            }
            },
            { label: '矿工兑换比例（增幅,削减）', name: 'scale', index: 'SCALE', width: 80 },
			{ label: '初始生命值', name: 'health', index: 'HEALTH', width: 80 },
			{ label: '初始法力值', name: 'mana', index: 'MANA', width: 80 },
			{ label: '初始生命值恢复', name: 'healthRegen', index: 'HEALTH_REGEN', width: 80 },
			{ label: '初始法力值恢复', name: 'manaRegen', index: 'MANA_REGEN', width: 80 },
			{ label: '初始护甲', name: 'armor', index: 'ARMOR', width: 80 },
			{ label: '初始魔抗', name: 'magicResist', index: 'MAGIC_RESIST', width: 80 },
			{ label: '初始攻击力', name: 'attackDamage', index: 'ATTACK_DAMAGE', width: 80 },
			{ label: '初始法功', name: 'attackSpell', index: 'ATTACK_SPELL', width: 80 },
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
		equipmentInfo: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.equipmentInfo = {equipRarecode : '1', status : 1,itemValuation: 0,
                health: 0, mana: 0, healthRegen: 0, manaRegen: 0,
                armor: 0, magicResist: 0, attackDamage: 0, attackSpell: 0,
            };
		},
		update: function (event) {
			var equipId = getSelectedRow();
			if(equipId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(equipId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.equipmentInfo.equipId == null ? "basicconfig/equipmentinfo/save" : "basicconfig/equipmentinfo/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.equipmentInfo),
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
			var equipIds = getSelectedRows();
			if(equipIds == null){
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
                        url: baseURL + "basicconfig/equipmentinfo/delete",
                        contentType: "application/json",
                        data: JSON.stringify(equipIds),
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
        updateS: function (event) {
			var equipIds = getSelectedRows();
			if(equipIds == null){
				return ;
			}
			var lock = false;
            layer.confirm('确定要更新选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
		            $.ajax({
                        type: "POST",
                        url: baseURL + "basicconfig/equipmentinfo/updateEquipJson",
                        contentType: "application/json",
                        data: JSON.stringify(equipIds),
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
		getInfo: function(equipId){
			$.get(baseURL + "basicconfig/equipmentinfo/info/"+equipId, function(r){
                vm.equipmentInfo = r.equipmentInfo;
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