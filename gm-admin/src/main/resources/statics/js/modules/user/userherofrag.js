$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/userherofrag/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmUserHeroFragId', name: 'gmUserHeroFragId', index: 'GM_USER_HERO_FRAG_ID', width: 50, key: true, hidden: true },
            { label: '会员名称', name: 'userName', width: 80 },
            { label: '英雄名称', name: 'heroName', width: 80 },
			{ label: '获得的碎片数量', name: 'gmUserHeroFragNum', index: 'GM_USER_HERO_FRAG_NUM', width: 80 },
			{ label: '状态', name: 'status', index: 'STATUS', width: 80 , formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else if (value == '1') {
                    return '<span class="label label-info">未使用</span>';//启用
                } else if (value == '2')  {
                    return '<span class="label label-info" style="background-color: #ddd;">已消耗</span>';//已消耗
                }
            }
            },
            { label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80, formatter: function (value, options, row) {
                return value;
            }
            }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 100,
		rowList : [100,500,1000],
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
            userName: '',
            status: '',
            fragNum: ''
        },
		userHeroFrag: {},
        testDrawStarts: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.userHeroFrag = {};
		},
        testDraw: function(){
			vm.showList = false;
			vm.title = "模拟抽奖";
		},
		update: function (event) {
			var gmUserHeroFragId = getSelectedRow();
			if(gmUserHeroFragId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(gmUserHeroFragId)
		},
        testDrawStart: function (type) {
            if(type == 1){
                $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                    vm.testDrawStartGo(1);
                });
            } else {
                $('#btnSaveOrUpdate2').button('loading').delay(1000).queue(function() {
                    vm.testDrawStartGo(2);
                });
            }
        },
        testDrawStartGo: function (type) {
            vm.testDrawStarts.drawType = type;
            var btnName = type == 1 ? 'btnSaveOrUpdate' : 'btnSaveOrUpdate2';
                $.ajax({
                type: "POST",
                url: baseURL + "user/userherofrag/testDrawStart",
                contentType: "application/json",
                data: JSON.stringify(vm.testDrawStarts.drawType),
                success: function(r){
                    if(r.code === 0){
                        layer.msg("抽奖成功", {icon: 1});
                        alert("恭喜您获得以下物品：" + r.s);
                        // vm.reload();
                        $('#' + btnName).button('reset');
                        $('#' + btnName).dequeue();
                    }else{
                        layer.alert(r.msg);
                        $('#' + btnName).button('reset');
                        $('#' + btnName).dequeue();
                    }
                }
            });
        },
        testEQDrawStart: function (type) {
            if(type == 1){
                $('#btnSaveOrUpdateEQ').button('loading').delay(1000).queue(function() {
                    vm.testEQDrawStartGo(1);
                });
            } else {
                $('#btnSaveOrUpdateEQ2').button('loading').delay(1000).queue(function() {
                    vm.testEQDrawStartGo(2);
                });
            }
        },
        testEQDrawStartGo: function (type) {
            vm.testDrawStarts.drawType = type;
            var btnName = type == 1 ? 'btnSaveOrUpdateEQ' : 'btnSaveOrUpdateEQ2';
                $.ajax({
                type: "POST",
                url: baseURL + "user/userherofrag/testEQDrawStart",
                contentType: "application/json",
                data: JSON.stringify(vm.testDrawStarts.drawType),
                success: function(r){
                    if(r.code === 0){
                        layer.msg("抽奖成功", {icon: 1});
                        alert("恭喜您获得以下物品：" + r.s);
                        // vm.reload();
                        $('#' + btnName).button('reset');
                        $('#' + btnName).dequeue();
                    }else{
                        layer.alert(r.msg);
                        $('#' + btnName).button('reset');
                        $('#' + btnName).dequeue();
                    }
                }
            });
        },
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.userHeroFrag.gmUserHeroFragId == null ? "user/userherofrag/save" : "user/userherofrag/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.userHeroFrag),
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
			var gmUserHeroFragIds = getSelectedRows();
			if(gmUserHeroFragIds == null){
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
                        url: baseURL + "user/userherofrag/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmUserHeroFragIds),
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
		getInfo: function(gmUserHeroFragId){
			$.get(baseURL + "user/userherofrag/info/"+gmUserHeroFragId, function(r){
                vm.userHeroFrag = r.userHeroFrag;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData: {'heroName': vm.q.heroName, 'userName': vm.q.userName, 'status': vm.q.status, 'fragNum': vm.q.fragNum},
                page:page
            }).trigger("reloadGrid");
		}
	}
});