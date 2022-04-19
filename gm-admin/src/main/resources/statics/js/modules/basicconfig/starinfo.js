$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/starinfo/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmStarId', name: 'gmStarId', index: 'GM_STAR_ID', width: 50, key: true },
			{ label: '星级级别', name: 'gmStarCode', index: 'GM_STAR_CODE', width: 80 }, 			
			{ label: '星级属性加成', name: 'gmStarAttributes', index: 'GM_STAR_ATTRIBUTES', width: 80 }, 			
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
		starInfo: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.starInfo = {gmStarCode:0,gmStarAttributes:0};
		},
		update: function (event) {
			var gmStarId = getSelectedRow();
			if(gmStarId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(gmStarId)
		},
		saveOrUpdate: function (event) {
            if(vm.starInfo.gmStarCode == ''){
                layer.msg("请输入星级级别");
                return;
            }
            if(vm.starInfo.gmStarAttributes == ''){
                layer.msg("请输入星级属性加成");
                return;
            }
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.starInfo.gmStarId == null ? "basicconfig/starinfo/save" : "basicconfig/starinfo/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.starInfo),
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
			var gmStarIds = getSelectedRows();
			if(gmStarIds == null){
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
                        url: baseURL + "basicconfig/starinfo/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmStarIds),
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
		getInfo: function(gmStarId){
			$.get(baseURL + "basicconfig/starinfo/info/"+gmStarId, function(r){
                vm.starInfo = r.starInfo;
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