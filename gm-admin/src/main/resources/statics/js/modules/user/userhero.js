$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/userhero/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmUserHeroId', name: 'gmUserHeroId', index: 'GM_USER_HERO_ID', width: 50, key: true, hidden:true},
			{ label: '会员名称', name: 'userName', width: 80 },
            { label: '英雄信息', name: 'heroName', width: 80 , formatter: function (value, options, row) {
                var starCode = row.gmStarCode != null && row.gmStarCode != 0 ? row.gmStarCode : 0;
                var starName = "";
                for (var i = 0; i < starCode; i++) {
                    starName += "★";
                }
                return '[' + value + ' ' + starName + ']';
            }
            },
            { label: '英雄战力', name: 'heroPower', width: 80 },
            { label: '英雄等级', name: 'gmLevelName', width: 80 },
            { label: '累计获得的经验', name: 'experienceObtain', width: 80 },
            { label: '状态', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
                    if (value == '0') {
                        return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                    } else if (value == '1') {
                        return '<span class="label label-info">正常</span>';//启用
                    }
            }
            },
            { label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80, formatter: function (value, options, row) {
                return value;
            }
            },
            { label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80},
            {label: '操作', name: '', align: 'center', width: 60, formatter: function (value, options, row) {
                var html = '';
                    html += '<i class="fa fa-solid fa-flask" onclick="vm.useEx(\''+row.gmUserId+'\',\''+row.gmUserHeroId+'\');" style="color: #1e84c5;cursor: pointer;line-height: 18px;font-size:20px;"><span style="display:none">使用经验药水</span></i>';
                return html;
            }}
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
            status: '',
            userName: '',
            gmLevelName: ''
        },
		userHero: {},
        userExList: {},
        userExInfo: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.userHero = {};
		},
		update: function (event) {
			var gmUserHeroId = getSelectedRow();
			if(gmUserHeroId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(gmUserHeroId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.userHero.gmUserHeroId == null ? "user/userhero/save" : "user/userhero/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.userHero),
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
			var gmUserHeroIds = getSelectedRows();
			if(gmUserHeroIds == null){
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
                        url: baseURL + "user/userhero/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmUserHeroIds),
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
		getInfo: function(gmUserHeroId){
			$.get(baseURL + "user/userhero/info/"+gmUserHeroId, function(r){
                vm.userHero = r.userHero;
            });
		},
        getUserExs: function (userId) {
		    vm.userExInfo.gmUserId = userId;
            $.ajax({
                type: "POST",
                url: baseURL + "user/userhero/getUserExs",
                contentType: "application/json",
                data: JSON.stringify(vm.userExInfo),
                success: function (r) {
                    if (r.code == 0) {
                        vm.userExList = r.list;
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        useEx: function (userId,gmUserHeroId) {
		    vm.userExInfo = {userExNum: 0,exPotionRarecode: 1};
		    vm.getUserExs(userId);
            layer.open({
                type: 1,
                skin: 'layui-layer-lan',
                title: "使用经验药水",
                area: ['700px', '250px'],
                shadeClose: false,
                content: jQuery("#useExD"),
                btn: ['使用','取消'],
                btn1: function (index) {
                    if(vm.userExInfo.userExNum == 0){
                        layer.msg("使用数量不能为0!");
                        return;
                    }
                    vm.userExInfo.gmUserId = userId;
                    vm.userExInfo.gmUserHeroId = gmUserHeroId;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "user/userhero/userHeroUseEx",
                        contentType: "application/json",
                        data: JSON.stringify(vm.userExInfo),
                        success: function(result){
                            if(result.code == 0){
                                layer.close(index);
                                layer.msg("使用成功", {icon: 1});
                                $("#jqGrid").trigger("reloadGrid");
                            }else{
                                layer.alert(result.msg);
                            }
                        }
                    });
                }
            });
        },
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData: {'heroName': vm.q.heroName, 'userName': vm.q.userName, 'status': vm.q.status, 'starCode': vm.q.starCode, 'gmLevelName': vm.q.gmLevelName},
                page:page
            }).trigger("reloadGrid");
		}
	}
});