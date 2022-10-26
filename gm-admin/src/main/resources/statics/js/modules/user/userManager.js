$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/user/list',
        datatype: "json",
        colModel: [			
			{ label: 'userId', name: 'userId', index: 'USER_ID', width: 50, key: true },
			{ label: '用户游戏名', name: 'userName', index: 'USER_NAME', width: 80 }, 			
			{ label: '用户钱包地址', name: 'address', index: 'ADDRESS', width: 80 },
			{ label: '用户级别', name: 'userLevelId', index: 'USER_LEVEL_ID', width: 80 },
			{ label: '累计获得的经验', name: 'experienceObtain', index: 'EXPERIENCE_OBTAIN', width: 80 },
			{ label: '用户类型', name: 'userType', index: 'USER_TYPE', width: 80 },
			{ label: '客户端ip', name: 'clientIp', index: 'CLIENT_IP', width: 80 }, 			
			{ label: '状态', name: 'status', index: 'STATUS', width: 80 },
            { label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80, formatter: function (value, options, row) {
                return value;
            }
            },
            { label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80, formatter: function (value, options, row) {
                return value;
            }
            },
			{ label: '推广码', name: 'expandCode', index: 'EXPAND_CODE', width: 80 }, 			
			{ label: '设备类型', name: 'deviceType', index: 'DEVICE_TYPE', width: 80 }, 			
			{ label: '创建人', name: 'createUser', index: 'CREATE_USER', width: 80 }, 			
			{ label: '修改人', name: 'updateUser', index: 'UPDATE_USER', width: 80 }, 			
			{ label: '备注', name: 'remark', index: 'REMARK', width: 80 }, 			
			{ label: '头像url', name: 'imgUrl', index: 'IMG_URL', width: 80 }, 			
			{ label: '用户在线状态：00离线，01：PC在线，02：H5在线，03：android在线，04：iOS在线', name: 'onlineFlag', index: 'ONLINE_FLAG', width: 80 }			
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
		user: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.user = {};
		},
		update: function (event) {
			var userId = getSelectedRow();
			if(userId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(userId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.user.userId == null ? "user/user/save" : "user/user/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.user),
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
			var userIds = getSelectedRows();
			if(userIds == null){
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
                        url: baseURL + "user/user/delete",
                        contentType: "application/json",
                        data: JSON.stringify(userIds),
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
		getInfo: function(userId){
			$.get(baseURL + "user/user/info/"+userId, function(r){
                vm.user = r.user;
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