$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/gmuserwithdraw/list',
        datatype: "json",
        colModel: [
            {label: 'withdrawId', name: 'withdrawId', index: 'WITHDRAW_ID', width: 50, key: true, hidden: true},
            {label: '用户ID', name: 'userId', index: 'USER_ID', width: 80},
            {label: '提现金额', name: 'withdrawMoney', index: 'WITHDRAW_MONEY', width: 80},
            {label: '手续费', name: 'serviceFee', index: 'SERVICE_FEE', width: 80},
            {
                label: '提现状态（0：申请提现，1：提现成功，2：提现失败）', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">申请</span>';
                } else if (value == '1') {
                    return '<span class="label label-info">成功</span>';
                } else if (value == '2') {
                    return '<span class="label label-info">失败</span>';
                }
            }
            },
            {label: '提现交易哈希', name: 'withdrawHash', index: 'WITHDRAW_HASH', width: 80},
            {
                label: '账户类型', name: 'currency', index: 'CURRENCY', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">战斗账户</span>';
                } else if (value == '1') {
                    return '<span class="label label-info">代理账户</span>';
                }
            }
            },
            {label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80},
            {label: '审核时间', name: 'checkTime', index: 'CHECK_TIME', width: 80},
            {label: '审核人', name: 'checkUser', index: 'CHECK_USER', width: 80},
            {label: '确认时间', name: 'confirmTime', index: 'CONFIRM_TIME', width: 80},
            // {label: '创建时间', name: 'createTimeTs', index: 'CREATE_TIME_TS', width: 80},
            // {label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80},
            // {label: '修改时间', name: 'updateTimeTs', index: 'UPDATE_TIME_TS', width: 80},
            {
                label: '操作', name: '', align: 'center', width: 60, formatter: function (value, options, row) {
                var html = '';
                if (row.status === 0) {
                    html += '<i onclick="vm.checkPass(\'' + row.withdrawId + '\');" style="color: #1e84c5;cursor: pointer;line-height: 18px;font-size:20px;">通过&nbsp;/&nbsp;</i>';
                    html += '<i onclick="vm.checkFail(\'' + row.withdrawId + '\');" style="color: #1e84c5;cursor: pointer;line-height: 18px;font-size:20px;">失败</i>';
                }
                return html;
            }
            }
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
        gmUserWithdraw: {},
        q: {
            userName: '',
        },
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.gmUserWithdraw = {};
        },
        checkPass: function (withdrawId) {
            $.ajax({
                type: "POST",
                url: baseURL + "user/gmuserwithdraw/pass",
                contentType: "application/json",
                data: JSON.stringify({"withdrawId": withdrawId}),
                success: function (r) {
                    if (r.code === 0) {
                        layer.msg("操作成功", {icon: 1});
                        vm.reload();
                    } else {
                        layer.alert(r.msg);
                    }
                }
            });
            console.log(withdrawId);
        },
        checkFail: function (withdrawId) {
            $.ajax({
                type: "POST",
                url: baseURL + "user/gmuserwithdraw/fail",
                contentType: "application/json",
                data: JSON.stringify({"withdrawId": withdrawId}),
                success: function (r) {
                    if (r.code === 0) {
                        layer.msg("操作成功", {icon: 1});
                        vm.reload();
                    } else {
                        layer.alert(r.msg);
                    }
                }
            });
        },
        update: function (event) {
            var withdrawId = getSelectedRow();
            if (withdrawId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(withdrawId)
        },
        saveOrUpdate: function (event) {
            $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function () {
                var url = vm.gmUserWithdraw.withdrawId == null ? "user/gmuserwithdraw/save" : "user/gmuserwithdraw/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmUserWithdraw),
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
            var withdrawIds = getSelectedRows();
            if (withdrawIds == null) {
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
                        url: baseURL + "user/gmuserwithdraw/delete",
                        contentType: "application/json",
                        data: JSON.stringify(withdrawIds),
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
        getInfo: function (withdrawId) {
            $.get(baseURL + "user/gmuserwithdraw/info/" + withdrawId, function (r) {
                vm.gmUserWithdraw = r.gmUserWithdraw;
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