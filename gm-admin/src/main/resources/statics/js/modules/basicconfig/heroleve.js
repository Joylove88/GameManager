$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/heroleve/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmHeroLeveId', name: 'gmHeroLeveId', index: 'GM_HERO_LEVE_ID', width: 50, key: true },
			{ label: '等级名称', name: 'gmLevelName', index: 'GM_LEVEL_NAME', width: 80 }, 			
			{ label: '等级描述', name: 'gmLevelDesc', index: 'GM_LEVEL_DESC', width: 80 }, 			
			{ label: '等级编码', name: 'gmLevelCode', index: 'GM_LEVEL_CODE', width: 80 }, 			
			{ label: '是否默认，1是，0否', name: 'gmFlag', index: 'GM_FLAG', width: 80 }, 			
			{ label: '是否删除，1是，0否', name: 'gmDeleted', index: 'GM_DELETED', width: 80 }, 			
			{ label: '晋级到下一级所需经验值', name: 'gmPromotionExperience', index: 'GM_PROMOTION_EXPERIENCE', width: 80 }, 			
			{ label: '升级所需累计经验', name: 'gmExperienceTotal', index: 'GM_EXPERIENCE_TOTAL', width: 80 },
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
		heroLeve: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.heroLeve = {};
		},
		update: function (event) {
			var gmHeroLeveId = getSelectedRow();
			if(gmHeroLeveId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(gmHeroLeveId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.heroLeve.gmHeroLeveId == null ? "basicconfig/heroleve/save" : "basicconfig/heroleve/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.heroLeve),
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
			var gmHeroLeveIds = getSelectedRows();
			if(gmHeroLeveIds == null){
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
                        url: baseURL + "basicconfig/heroleve/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmHeroLeveIds),
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
		getInfo: function(gmHeroLeveId){
			$.get(baseURL + "basicconfig/heroleve/info/"+gmHeroLeveId, function(r){
                vm.heroLeve = r.heroLeve;
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