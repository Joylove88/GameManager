$(function () {
    vm.getHeroInfoList();
    vm.getStarInfoList();
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/herostar/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmHeroStarId', name: 'gmHeroStarId', index: 'GM_HERO_STAR_ID', width: 50, key: true },
			{ label: '英雄ID', name: 'gmHeroId', index: 'GM_HERO_ID', width: 80, hidden:true},
			{ label: '星级ID', name: 'gmStarId', index: 'GM_STAR_ID', width: 80, hidden:true},
			{ label: '英雄名称', name: 'heroName', width: 60 },
			{ label: '星级', name: 'gmStarCode', width: 60, formatter: function (value, options, row) {
                return value + '★';
            }
            },
			{ label: '英雄概率等级', name: 'gmProbabilityId', index: 'GM_PROBABILITY_ID', width: 50 },
            { label: '状态', name: 'status', index: 'STATUS', width: 60, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else {
                    return '<span class="label label-info">启用</span>';//启用
                }
            }
            },
            { label: '矿工兑换比例（增幅,削减）', name: 'scale', index: 'SCALE', width: 80 },
			{ label: '初始生命值', name: 'gmHealth', index: 'GM_HEALTH', width: 80, formatter: function (value, options, row) {
                return value + ' (+ ' + row.gmGrowHealth + ')*1';
            }
            },
			{ label: '初始法力值', name: 'gmMana', index: 'GM_MANA', width: 80, formatter: function (value, options, row) {
                return value + ' (+ ' + row.gmGrowMana + ')*1';
            }
            },
			{ label: '初始生命值恢复', name: 'gmHealthRegen', index: 'GM_HEALTH_REGEN', width: 80, formatter: function (value, options, row) {
                return value + ' (+ ' + row.gmGrowHealthRegen + ')*1';
            }
            },
			{ label: '初始法力值恢复', name: 'gmManaRegen', index: 'GM_MANA_REGEN', width: 80, formatter: function (value, options, row) {
                return value + ' (+ ' + row.gmGrowManaRegen + ')*1';
            }
            },
			{ label: '初始护甲', name: 'gmArmor', index: 'GM_ARMOR', width: 80, formatter: function (value, options, row) {
                return value + ' (+ ' + row.gmGrowArmor + ')*1';
            }
            },
			{ label: '初始魔抗', name: 'gmMagicResist', index: 'GM_MAGIC_RESIST', width: 80, formatter: function (value, options, row) {
                return value + ' (+ ' + row.gmGrowMagicResist + ')*1';
            }
            },
			{ label: '初始攻击力', name: 'gmAttackDamage', index: 'GM_ATTACK_DAMAGE', width: 80, formatter: function (value, options, row) {
                return value + ' (+ ' + row.gmGrowAttackDamage + ')*1';
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
            heroName: '',
            starCode: '',
            status: ''
        },
		heroStar: {},
        heroInfoList: {},
        starInfoList: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.heroStar = {gmHeroId: '', gmStarId: '', status: '1', itemValuation: 0, gmProbabilityId: 1,
                gmHealth: 0, gmMana: 0, gmHealthRegen: 0, gmManaRegen: 0, gmArmor: 0, gmMagicResist: 0, gmAttackDamage: 0,
                gmGrowHealth: 0, gmGrowMana: 0, gmGrowHealthRegen: 0, gmGrowManaRegen: 0, gmGrowArmor: 0, gmGrowMagicResist: 0, gmGrowAttackDamage: 0
			};
		},
		update: function (event) {
			var gmHeroStarId = getSelectedRow();
			if(gmHeroStarId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";

            
            vm.getInfo(gmHeroStarId)
		},
		saveOrUpdate: function (event) {
		    if(vm.heroStar.gmHeroStarId != null){
		        if(vm.heroStar.gmStarId == 1000){
                    layer.msg("修改操作时无法选择0星");
		            return;
                }
            }
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.heroStar.gmHeroStarId == null ? "basicconfig/herostar/save" : "basicconfig/herostar/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.heroStar),
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
			var gmHeroStarIds = getSelectedRows();
			if(gmHeroStarIds == null){
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
                        url: baseURL + "basicconfig/herostar/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmHeroStarIds),
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
		getInfo: function(gmHeroStarId){
			$.get(baseURL + "basicconfig/herostar/info/"+gmHeroStarId, function(r){
                vm.heroStar = r.heroStar;
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
        getStarInfoList: function () {
            $.ajax({
                type: "POST",
                url: baseURL + "basicconfig/starinfo/getStarInfoList",
                contentType: "application/json",
                success: function (r) {
                    if (r.code == 0) {
                        vm.starInfoList = r.list;
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
                postData: {'heroName': vm.q.heroName, 'starCode': vm.q.starCode, 'status': vm.q.status},
                page:page
            }).trigger("reloadGrid");
		}
	}
});