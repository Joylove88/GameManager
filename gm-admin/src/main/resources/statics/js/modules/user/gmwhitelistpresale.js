$(function () {
    laydate.render({
        elem: '#time',
        range: true,
        timeLimits: false,
        type: 'datetime'
    });
    laydate.render({
        elem: '#eventTime',
        timeLimits: false,
        type: 'datetime',
        range: '-' //或 range: '~' 来自定义分割字符
    });
    // 获取当天时间
    // $('#time').val(getNowFormatDate() +' 00:00:00 - '+ getNowFormatDate() + ' 23:59:59');
    // var inputTime = $('#time').val();
    // var startTime = '';
    // var endTime = '';
    // if (inputTime.length > 10) {
    //     startTime = inputTime.substring(0, 10);
    //     endTime = inputTime.substring(inputTime.length - 10, inputTime.length);
    // }
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/gmwhitelistpresale/list',
        // postData: {'startTime': startTime, 'endTime': endTime},
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'ID', width: 50, key: true},
            {label: '地址', name: 'address', index: 'ADDRESS', width: 80},
            {
                label: '活动类型', name: 'type', index: 'TYPE', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #FF9800;">预售</span>';//预售
                }
            }
            },
            {label: '邮箱', name: 'email', index: 'EMAIL', width: 80},
            {label: '折扣率', name: 'discountRate', index: 'DISCOUNT_RATE', width: 80},
            {label: '可购买数量', name: 'quantityAvailable', index: 'QUANTITY_AVAILABLE', width: 80},
            {
                label: '状态', name: 'status', index: 'STATUS', width: 80, formatter: function (value, options, row) {
                if (value == '0') {
                    return '<span class="label badge-danger" style="background-color: #ed5565;">禁用</span>';//禁用
                } else {
                    return '<span class="label label-info">启用</span>';//启用
                }
            }
            },
            {label: '开始时间', name: 'startTime', width: 80},
            {label: '结束时间', name: 'endTime', width: 80},
            {label: '创建人', name: 'createUser', index: 'CREATE_USER', width: 80},
            {label: '创建时间', name: 'createTime', index: 'CREATE_TIME', width: 80},
            {label: '修改人', name: 'updateUser', index: 'UPDATE_USER', width: 80},
            {label: '修改时间', name: 'updateTime', index: 'UPDATE_TIME', width: 80},
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
        q: {
            address: '',
            discountRate: '',
            quantityAvailable: '',
            status: ''
        },
        gmWhitelistPresale: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.gmWhitelistPresale = {status: '1', type: '0'};
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            if (vm.gmWhitelistPresale.address == '') {
                layer.msg("请输入地址");
                return;
            }
            if (vm.gmWhitelistPresale.type == '') {
                layer.msg("请选择活动类型");
                return;
            }
            // 活动时间
            var eventTime = $('#eventTime').val();
            if (eventTime != null && eventTime.length > 19) {
                var gtTime = eventTime.substring(0, 19);
                var ltTime = eventTime.substring(eventTime.length - 19, eventTime.length);
                vm.gmWhitelistPresale.startTime = gtTime;
                vm.gmWhitelistPresale.endTime = ltTime;
            }

            $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function () {
                var url = vm.gmWhitelistPresale.id == null ? "user/gmwhitelistpresale/save" : "user/gmwhitelistpresale/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.gmWhitelistPresale),
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
            var ids = getSelectedRows();
            if (ids == null) {
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
                        url: baseURL + "user/gmwhitelistpresale/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
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
        getInfo: function (id) {
            $.get(baseURL + "user/gmwhitelistpresale/info/" + id, function (r) {
                vm.gmWhitelistPresale = r.gmWhitelistPresale;
                $('#eventTime').val(vm.gmWhitelistPresale.startTime + " - " + vm.gmWhitelistPresale.endTime);
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            var inputTime = $('#time').val();
            var startTime = '';
            var endTime = '';
            if (inputTime.length > 10) {
                startTime = inputTime.substring(0, 10);
                endTime = inputTime.substring(inputTime.length - 10, inputTime.length);
            }
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {
                    'status': vm.q.status,
                    'address': vm.q.address,
                    'discountRate': vm.q.discountRate,
                    'quantityAvailable': vm.q.quantityAvailable, 'startTime': startTime, 'endTime': endTime
                },
                page: page
            }).trigger("reloadGrid");
        }
    }
});