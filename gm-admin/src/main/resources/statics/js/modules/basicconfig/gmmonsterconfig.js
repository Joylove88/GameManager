$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/gmmonsterconfig/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'ID', width: 50, key: true },
			{ label: '副本名称', name: 'dungeonName', width: 80 },
			{ label: '副本等级', name: 'dungeonLevelMin', width: 80 , formatter: function (value, options, row) {
			    return row.dungeonLevelMin + '-' + row.dungeonLevelMax;
            }
            },
			{ label: '怪物名称', name: 'monsterName', index: 'MONSTER_NAME', width: 80 },
			{ label: '怪物描述', name: 'monsterDescription', index: 'MONSTER_DESCRIPTION', width: 80 }, 			
			{ label: '怪物等级', name: 'monsterLevel', index: 'MONSTER_LEVEL', width: 80 }, 			
			{ label: '怪物战力', name: 'monsterPower', index: 'MONSTER_POWER', width: 80 }, 			
			{ label: '怪物专属技能伤害倍数', name: 'uniqueSkillM', width: 80 },
			{ label: '怪物致命一击伤害倍数', name: 'criticalHitM', width: 80 },
			// { label: '怪物图片URL', name: 'monsterImgUrl', index: 'MONSTER_IMG_URL', width: 80 },
			// { label: '怪物图标URL', name: 'monsterIconUrl', index: 'MONSTER_ICON_URL', width: 80 },
			// { label: '初始生命值', name: 'monsterHealth', index: 'MONSTER_HEALTH', width: 80 },
			// { label: '初始生命值恢复', name: 'monsterHealthRegen', index: 'MONSTER_HEALTH_REGEN', width: 80 },
			// { label: '初始护甲', name: 'monsterArmor', index: 'MONSTER_ARMOR', width: 80 },
			// { label: '初始魔抗', name: 'monsterMagicResist', index: 'MONSTER_MAGIC_RESIST', width: 80 },
			// { label: '初始攻击力', name: 'monsterAttackDamage', index: 'MONSTER_ATTACK_DAMAGE', width: 80 },
			// { label: '法功', name: 'monsterAttackSpell', index: 'MONSTER_ATTACK_SPELL', width: 80 },
			{ label: '击杀经验', name: 'monsterExp', index: 'MONSTER_EXP', width: 80 },
            { label: '状态', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else {
                    return '<span class="label label-info">启用</span>';//启用
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
            monsterName: '',
            dungeonName: '',
            monsterLevel: '',
            status: ''
        },
		gmMonsterConfig: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.gmMonsterConfig = {};
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
                var url = vm.gmMonsterConfig.id == null ? "basicconfig/gmmonsterconfig/save" : "basicconfig/gmmonsterconfig/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmMonsterConfig),
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
                        url: baseURL + "basicconfig/gmmonsterconfig/delete",
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
			$.get(baseURL + "basicconfig/gmmonsterconfig/info/"+id, function(r){
                vm.gmMonsterConfig = r.gmMonsterConfig;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData: {'status': vm.q.status, 'monsterName': vm.q.monsterName, 'dungeonName': vm.q.dungeonName, 'monsterLevel': vm.q.monsterLevel},
                page:page
            }).trigger("reloadGrid");
		}
	}
});