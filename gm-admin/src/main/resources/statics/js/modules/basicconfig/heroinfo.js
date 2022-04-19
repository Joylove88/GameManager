$(function () {
    vm.getHeroTypeList();//加载获取英雄类型

    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/heroinfo/list',
        datatype: "json",
        colModel: [			
			{ label: 'heroId', name: 'heroId', width: 50, key: true},
			{ label: '英雄名称', name: 'heroName', index: 'HERO_NAME', width: 60 },
			{ label: '英雄类型', name: 'heroType', index: 'HERO_TYPE', width: 60 },
			{ label: '英雄图片', name: 'heroImgUrl', index: 'HERO_TYPE', width: 50 , formatter: function (value, options, row) {
                return '<img height="66" src="'+value+'">';
            }
            },
			{ label: '英雄龙骨', name: 'heroKeelUrl', index: 'HERO_TYPE', width: 80 },
			{ label: '英雄描述', name: 'heroDescription', index: 'HERO_TYPE', width: 80 },
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
            heroName: '',
            status: ''
        },
		heroInfo: {},
        heroTypeList: {},
        heroCodeList: []
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.heroInfo = {status:'1'};
			vm.heroCodeList = [];
		},
		update: function (event) {
			var heroId = getSelectedRow();
			if(heroId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            vm.heroCodeList = [];

            vm.getInfo(heroId)
		},
		saveOrUpdate: function (event) {
		    if(vm.heroInfo.heroName == ''){
                layer.msg("请输入英雄名称");
                return;
            }
            if(vm.heroCodeList.length < 1){
                layer.msg("请选择至少一个英雄类型");
                return;
            }
            $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                vm.heroInfo.heroType = vm.heroCodeList.toString();
                var url = vm.heroInfo.heroId == null ? "basicconfig/heroinfo/save" : "basicconfig/heroinfo/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.heroInfo),
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
			var heroIds = getSelectedRows();
			if(heroIds == null){
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
                        url: baseURL + "basicconfig/heroinfo/delete",
                        contentType: "application/json",
                        data: JSON.stringify(heroIds),
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
		getInfo: function(heroId){
			$.get(baseURL + "basicconfig/heroinfo/info/"+heroId, function(r){
                vm.heroInfo = r.heroInfo;
                vm.heroCodeList = vm.heroInfo.heroType.split(",");
            });
		},
        getHeroTypeList: function () {
            $.ajax({
                type: "POST",
                url: baseURL + "basicconfig/heroinfo/getHeroTypeList",
                contentType: "application/json",
                success: function (r) {
                    if (r.code == 0) {
                        vm.heroTypeList = r.list;
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
                postData: {'heroName': vm.q.heroName, 'status': vm.q.status},
                page:page
            }).trigger("reloadGrid");
		}
	}
});