var web3J;

//生成菜单
var menuItem = Vue.extend({
    name: 'menu-item',
    props:{item:{}},
    template:[
        '<li>',
        '	<a v-if="item.type === 0" href="javascript:;">',
        '		<i v-if="item.icon != null" :class="item.icon"></i>',
        '		<span>{{item.name}}</span>',
        '		<i class="fa fa-angle-left pull-right"></i>',
        '	</a>',
        '	<ul v-if="item.type === 0" class="treeview-menu">',
        '		<menu-item :item="item" v-for="item in item.list"></menu-item>',
        '	</ul>',

        '	<a v-if="item.type === 1 && item.parentId === 0" :href="\'#\'+item.url">',
        '		<i v-if="item.icon != null" :class="item.icon"></i>',
        '		<span>{{item.name}}</span>',
        '	</a>',

        '	<a v-if="item.type === 1 && item.parentId != 0" :href="\'#\'+item.url"><i v-if="item.icon != null" :class="item.icon"></i><i v-else class="fa fa-circle-o"></i> {{item.name}}</a>',
        '</li>'
    ].join('')
});

//iframe自适应
$(window).on('resize', function() {
	var $content = $('.content');
	$content.height($(this).height() - 154);
	$content.find('iframe').each(function() {
		$(this).height($content.height());
	});
}).resize();

//注册菜单组件
Vue.component('menuItem',menuItem);

var vm = new Vue({
	el:'#rrapp',
	data:{
		user:{},
		menuList:{},
		main:"main.html",
		password:'',
		newPassword:'',
        navTitle:"控制台",
        testDrawStarts: {},
        web3J: {}
	},
	methods: {
		getMenuList: function (event) {
			$.getJSON("sys/menu/nav?_"+$.now(), function(r){
				vm.menuList = r.menuList;
			});
		},
		getUser: function(){
			$.getJSON("sys/user/info?_"+$.now(), function(r){
				vm.user = r.user;
			});
		},
		updatePassword: function(){
			layer.open({
				type: 1,
				skin: 'layui-layer-molv',
				title: "修改密码",
				area: ['550px', '270px'],
				shadeClose: false,
				content: jQuery("#passwordLayer"),
				btn: ['修改','取消'],
				btn1: function (index) {
					var data = "password="+vm.password+"&newPassword="+vm.newPassword;
					$.ajax({
						type: "POST",
					    url: "sys/user/password",
					    data: data,
					    dataType: "json",
					    success: function(result){
							if(result.code == 0){
								layer.close(index);
								layer.alert('修改成功', function(index){
									location.reload();
								});
							}else{
								layer.alert(result.msg);
							}
						}
					});
	            }
			});
		},
        testDraw: function(){
            layer.open({
                type: 1,
                skin: 'layui-layer-molv',
                title: "模拟抽奖",
                area: ['750px', '450px'],
                shadeClose: false,
                content: jQuery("#testDrawD"),
                btn: ['取消'],
            });
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
                url: "user/userherofrag/testDrawStart",
                contentType: "application/json",
                data: JSON.stringify(vm.testDrawStarts.drawType),
                success: function(r){
                    if(r.code === 0){
                        layer.msg("抽奖成功", {icon: 1});
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
                url: "user/userherofrag/testEQDrawStart",
                contentType: "application/json",
                data: JSON.stringify(vm.testDrawStarts.drawType),
                success: function(r){
                    if(r.code === 0){
                        layer.msg("抽奖成功", {icon: 1});
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
        testEXDrawStart: function (type) {
            if(type == 1){
                $('#btnSaveOrUpdateEX').button('loading').delay(1000).queue(function() {
                    vm.testEXDrawStartGo(1);
                });
            } else {
                $('#btnSaveOrUpdateEX2').button('loading').delay(1000).queue(function() {
                    vm.testEXDrawStartGo(2);
                });
            }
        },
        testEXDrawStartGo: function (type) {
            vm.testDrawStarts.drawType = type;
            var btnName = type == 1 ? 'btnSaveOrUpdateEX' : 'btnSaveOrUpdateEX2';
            $.ajax({
                type: "POST",
                url: "user/userherofrag/testEXDrawStart",
                contentType: "application/json",
                data: JSON.stringify(vm.testDrawStarts.drawType),
                success: function(r){
                    if(r.code === 0){
                        layer.msg("抽奖成功", {icon: 1});
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
        connectAccount: function () {
            var web3Provider;
            if (window.ethereum) {
                web3Provider = window.ethereum;
                try {
                    // 请求用户授权
                    window.ethereum.enable();
                } catch (error) {
                    // 用户不授权时
                    console.error("User denied account access")
                }
            }
            web3J = new Web3(web3Provider);//web3js就是你需要的web3实例

            web3J.eth.getAccounts(function (error, result) {
                if (!error)
                    console.log(result)//授权成功后result能正常获取到账号了
            });

            // var MyContract = new web3.eth.Contract(abi, "0x25B15dE515eBBD047e026D64463801f044785cc6");
            // MyContract.methods.setCost().call()
            //     .then(console.log);
        },
        sendT: function () {
            console.log(web3J)
            if(web3J !=null && web3J != 'undefined'){
                web3J.eth.sendTransaction({

                    from:'0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45',

                    to:'0x1ff6771D6e7b799b00A33849849c3E36C669Eb48',

                    value:'1000000000000000'

                }).on('transactionHash',function(hash){

                    console.info(hash);

                }).on('receipt',function(receipt){

                    console.info(receipt)

                }).on('confirmation',function(confirmationNumber, receipt){

                    console.info(confirmationNumber)

                    console.info(receipt)

                }).on('error',console.error);
            }
        },
        donate: function () {
            layer.open({
                type: 2,
                title: false,
                area: ['806px', '467px'],
                closeBtn: 1,
                shadeClose: false,
                content: ['http://cdn.gm.io/donate.jpg', 'no']
            });
        }
	},
	created: function(){
		this.getMenuList();
		this.getUser();
	},
	updated: function(){
		//路由
		var router = new Router();
		routerList(router, vm.menuList);
		router.start();
	}
});



function routerList(router, menuList){
	for(var key in menuList){
		var menu = menuList[key];
		if(menu.type == 0){
			routerList(router, menu.list);
		}else if(menu.type == 1){
			router.add('#'+menu.url, function() {
				var url = window.location.hash;
				
				//替换iframe的url
			    vm.main = url.replace('#', '');
			    
			    //导航菜单展开
			    $(".treeview-menu li").removeClass("active");
			    $("a[href='"+url+"']").parents("li").addClass("active");
			    
			    vm.navTitle = $("a[href='"+url+"']").text();
			});
		}
	}
}