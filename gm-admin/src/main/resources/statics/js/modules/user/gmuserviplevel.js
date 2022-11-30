$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/gmuserviplevel/list',
        datatype: "json",
        colModel: [
            {label: 'vipLevelId', name: 'vipLevelId', index: 'VIP_LEVEL_ID', width: 50, key: true, hidden: true},
            {label: '等级code', name: 'vipLevelCode', index: 'VIP_LEVEL_CODE', width: 80},
            {label: '等级名称', name: 'vipLevelName', index: 'VIP_LEVEL_NAME', width: 80},
            {label: '消费金额', name: 'consumeMoney', index: 'CONSUME_MONEY', width: 80},
            {label: '邀请人数', name: 'inviteNum', index: 'INVITE_NUM', width: 80},
            {label: '邀请人数消费金额', name: 'inviteConsumeMoney', index: 'INVITE_CONSUME_MONEY', width: 80},
            {label: '提现上限', name: 'withdrawLimit', index: 'WITHDRAW_LIMIT', width: 80},
            {label: '提现频率', name: 'withdrawFrequency', index: 'WITHDRAW_FREQUENCY', width: 80},
            {label: '提现手续费', name: 'withdrawHandlingFee', index: 'WITHDRAW_HANDLING_FEE', width: 80},
            {label: '首次消费返佣金', name: 'firstBrokerage', index: 'FIRST_BROKERAGE', width: 80},
            {label: '消费返佣金', name: 'brokerage', index: 'BROKERAGE', width: 80},
            {label: '提现手续费', name: 'withdrawHandlingFee', index: 'BROKERAGE', width: 80},
            {label: '提现是否审核', name: 'needCheck', index: 'BROKERAGE', width: 80},
            {label: '创建人', name: 'createUser', index: 'CREATE_USER', width: 80},
            {label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80},
            {label: '创建时间', name: 'createTimeTs', index: 'CREATE_TIME_TS', width: 80, hidden: true},
            {label: '修改人', name: 'updateUser', index: 'UPDATE_USER', width: 80},
            {label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80},
            {label: '修改时间', name: 'updateTimeTs', index: 'UPDATE_TIME_TS', width: 80, hidden: true}
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        gmUserVipLevel: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.gmUserVipLevel = {};
        },
        update: function (event) {
            var vipLevelId = getSelectedRow();
            if (vipLevelId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(vipLevelId)
        },
        saveOrUpdate: function (event) {
            $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function () {
                var url = vm.gmUserVipLevel.vipLevelId == null ? "user/gmuserviplevel/save" : "user/gmuserviplevel/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmUserVipLevel),
                    success: function (r) {
                        if (r.code === 0) {
                            layer.msg("操作成功", {icon: 1});
                            vm.reload();
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        } else {
                            layer.alert(r.msg);
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        }
                    }
                });
            });
        },
        del: function (event) {
            var vipLevelIds = getSelectedRows();
            if (vipLevelIds == null) {
                return;
            }
            var lock = false;
            layer.confirm('确定要删除选中的记录？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                if (!lock) {
                    lock = true;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "user/gmuserviplevel/delete",
                        contentType: "application/json",
                        data: JSON.stringify(vipLevelIds),
                        success: function (r) {
                            if (r.code == 0) {
                                layer.msg("操作成功", {icon: 1});
                                $("#jqGrid").trigger("reloadGrid");
                            } else {
                                layer.alert(r.msg);
                            }
                        }
                    });
                }
            }, function () {
            });
        },
        getInfo: function (vipLevelId) {
            $.get(baseURL + "user/gmuserviplevel/info/" + vipLevelId, function (r) {
                vm.gmUserVipLevel = r.gmUserVipLevel;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        }
    }
});