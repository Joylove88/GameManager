$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/userequipment/list',
        datatype: "json",
        colModel: [
            { label: 'userEquipmentId', name: 'userEquipmentId', index: 'USER_EQUIPMENT_ID', width: 50, key: true },
            { label: '会员名称', name: 'userName', width: 80 },
            { label: '装备名称', name: 'equipName', width: 80 },
            { label: '装备战力', name: 'equipPower', width: 80 },
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
            { label: '装备等级', name: 'equipLevel', width: 80 },
            { label: '来源ID', name: 'sourceId', width: 80 },
			{ label: '来源类型', name: 'fromType', width: 80, formatter: function (value, options, row) {
                    if (value == '0') {
                        return '<span class="label badge-info" style="background-color: #ed5565;">副本</span>';
                    } else if (value == '1') {
                        return '<span class="label label-info">抽奖</span>';
                    }
                }
            },
			{ label: '状态', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
                    if (value == '0') {
                        return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                    } else if (value == '1') {
                        return '<span class="label label-info">正常</span>';//启用
                    } else if (value == '2') {
                        return '<span class="label label-info">已激活</span>';//启用
                    }
                }
            },
            { label: '初始生命值', name: 'equipHealth', index: 'EQUIP_HEALTH', width: 80 },
            { label: '初始法力值', name: 'equipMana', index: 'EQUIP_MANA', width: 80 },
            { label: '初始生命值恢复', name: 'equipHealthRegen', index: 'EQUIP_HEALTH_REGEN', width: 80 },
            { label: '初始法力值恢复', name: 'equipManaRegen', index: 'EQUIP_MANA_REGEN', width: 80 },
            { label: '初始护甲', name: 'equipArmor', index: 'EQUIP_ARMOR', width: 80 },
            { label: '初始魔抗', name: 'equipMagicResist', index: 'EQUIP_MAGIC_RESIST', width: 80 },
            { label: '初始攻击力', name: 'equipAttackDamage', index: 'EQUIP_ATTACK_DAMAGE', width: 80 },
            { label: '初始法功', name: 'equipAttackSpell', index: 'EQUIP_ATTACK_SPELL', width: 80 },
			{ label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80, formatter: function (value, options, row) {
			    return value;
            }
            },
			{ label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80, formatter: function (value, options, row) {
                return value != null ? value : '';
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
            equipRarecode: ''
        },
		userEquipment: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.userEquipment = {};
		},
		update: function (event) {
			var userEquipmentId = getSelectedRow();
			if(userEquipmentId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(userEquipmentId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.userEquipment.userEquipmentId == null ? "user/userequipment/save" : "user/userequipment/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.userEquipment),
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
			var userEquipmentIds = getSelectedRows();
			if(userEquipmentIds == null){
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
                        url: baseURL + "user/userequipment/delete",
                        contentType: "application/json",
                        data: JSON.stringify(userEquipmentIds),
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
		getInfo: function(userEquipmentId){
			$.get(baseURL + "user/userequipment/info/"+userEquipmentId, function(r){
                vm.userEquipment = r.userEquipment;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData: {'status' : vm.q.status, 'equipName' : vm.q.equipName, 'userName' : vm.q.userName, 'equipRarecode' : vm.q.equipRarecode},
                page:page
            }).trigger("reloadGrid");
		}
	}
});