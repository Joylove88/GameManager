$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/userexperiencepotion/list',
        datatype: "json",
        colModel: [			
			{ label: 'gmUserExPotionId', name: 'gmUserExPotionId', index: 'GM_USER_EX_POTION_ID', width: 50, key: true },
			{ label: '会员名称', name: 'userName', width: 80 },
            { label: '经验药水名称', name: 'exPotionName', width: 80 },
            { label: '经验值', name: 'exValue', width: 80 },
            { label: '经验药水稀有度', name: 'exPotionRarecode', width: 80, formatter: function (value, options, row) {
                var erc = '';
                if (value == '1') {
                    erc = '白色';
                } else if (value == '2') {
                    erc = '绿色';
                } else if (value == '3') {
                    erc = '蓝色';
                } else if (value == '4') {
                    erc = '紫色';
                }
                return erc;
            }
            },
            { label: '经验药水数量', name: 'userExNum', width: 80 },
            { label: '状态', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else if (value == '1') {
                    return '<span class="label label-info">未使用</span>';//启用
                } else if (value == '2')  {
                    return '<span class="label label-info" style="background-color: #ddd;">已消耗</span>';//已消耗
                }
            }
            },
			{ label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80 }, 			
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
            exPotionName: '',
            userName: '',
            status: '',
            exPotionRarecode: ''
        },
		userExperiencePotion: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.userExperiencePotion = {};
		},
		update: function (event) {
			var gmUserExPotionId = getSelectedRow();
			if(gmUserExPotionId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(gmUserExPotionId)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.userExperiencePotion.gmUserExPotionId == null ? "user/userexperiencepotion/save" : "user/userexperiencepotion/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.userExperiencePotion),
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
			var gmUserExPotionIds = getSelectedRows();
			if(gmUserExPotionIds == null){
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
                        url: baseURL + "user/userexperiencepotion/delete",
                        contentType: "application/json",
                        data: JSON.stringify(gmUserExPotionIds),
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
		getInfo: function(gmUserExPotionId){
			$.get(baseURL + "user/userexperiencepotion/info/"+gmUserExPotionId, function(r){
                vm.userExperiencePotion = r.userExperiencePotion;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData: {'status' : vm.q.status, 'exPotionName' : vm.q.exPotionName, 'userName' : vm.q.userName, 'exPotionRarecode' : vm.q.exPotionRarecode},
                page:page
            }).trigger("reloadGrid");
		}
	}
});