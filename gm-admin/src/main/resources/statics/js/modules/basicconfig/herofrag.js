$(function () {
    vm.getHeroInfoList();
    $("#jqGrid").jqGrid({
        url: baseURL + 'basicconfig/herofrag/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmHeroFragId', name: 'gmHeroFragId', index: 'GM_HERO_FRAG_ID', width: 50, key: true, hidden:true },
			{ label: '英雄ID', name: 'gmHeroInfoId', index: 'GM_HERO_INFO_ID', width: 80, hidden:true },
            { label: '英雄名称', name: 'heroName', width: 80 },
			{ label: '英雄碎片数量', name: 'gmHeroFragNum', index: 'GM_HERO_FRAG_NUM', width: 80 },
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
		heroFrag: {},
        heroInfoList: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.heroFrag = {gmHeroInfoId: '', gmHeroFragNum: 1, itemValuation: 0, status: '1'};
		},
		update: function (event) {
			var gmHeroFragId = getSelectedRow();
			if(gmHeroFragId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(gmHeroFragId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.heroFrag.gmHeroFragId == null ? "basicconfig/herofrag/save" : "basicconfig/herofrag/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.heroFrag),
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
			var gmHeroFragIds = getSelectedRows();
			if(gmHeroFragIds == null){
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
                        url: baseURL + "basicconfig/herofrag/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmHeroFragIds),
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
		getInfo: function(gmHeroFragId){
			$.get(baseURL + "basicconfig/herofrag/info/"+gmHeroFragId, function(r){
                vm.heroFrag = r.heroFrag;
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
                postData: {'heroName': vm.q.heroName, 'status': vm.q.status},
                page:page
            }).trigger("reloadGrid");
		}
	}
});