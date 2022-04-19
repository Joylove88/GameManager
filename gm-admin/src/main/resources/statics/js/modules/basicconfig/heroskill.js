$(function () {
    vm.getHeroInfoList();
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/heroskill/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmHeroSkillId', name: 'gmHeroSkillId', index: 'GM_HERO_SKILL_ID', width: 50, key: true },
			{ label: '英雄ID', name: 'heroName', width: 80 },
			{ label: '伤害星级', name: 'skillStarLevel', index: 'SKILL_STAR_LEVEL', width: 80 },
			{ label: '技能位置', name: 'skillSolt', index: 'SKILL_SOLT', width: 80 }, 			
			{ label: '固定伤害', name: 'skillFixedDamage', index: 'SKILL_FIXED_DAMAGE', width: 80 }, 			
			{ label: '英雄属性伤害加成', name: 'skillDamageBonusHero', index: 'SKILL_DAMAGE_BONUS_HERO', width: 80 }, 			
			{ label: '装备属性伤害加成', name: 'skillDamageBonusEquip', index: 'SKILL_DAMAGE_BONUS_EQUIP', width: 80 }, 			
			{ label: '技能描述', name: 'skillDescription', index: 'SKILL_DESCRIPTION', width: 80 }, 			
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
        q: {
            heroName: '',
            status: ''
        },
        heroInfoList: {},
		heroSkill: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.heroSkill = {gmHeroId: '', status: '1', skillStarLevel: '1', skillSolt: '1', skillFixedDamage: 0, skillDamageBonusHero: 0, skillDamageBonusEquip:0};
		},
		update: function (event) {
			var gmHeroSkillId = getSelectedRow();
			if(gmHeroSkillId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(gmHeroSkillId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.heroSkill.gmHeroSkillId == null ? "basicconfig/heroskill/save" : "basicconfig/heroskill/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.heroSkill),
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
			var gmHeroSkillIds = getSelectedRows();
			if(gmHeroSkillIds == null){
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
                        url: baseURL + "basicconfig/heroskill/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmHeroSkillIds),
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
		getInfo: function(gmHeroSkillId){
			$.get(baseURL + "basicconfig/heroskill/info/"+gmHeroSkillId, function(r){
                vm.heroSkill = r.heroSkill;
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
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
			    postData:{'status': vm.q.status, 'heroName': vm.q.heroName},
                page:page
            }).trigger("reloadGrid");
		}
	}
});