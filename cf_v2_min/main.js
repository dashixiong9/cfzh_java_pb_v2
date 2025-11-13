import Vue from 'vue'
import App from './App'
Vue.config.productionTip = false

//把vuex定义成全局组件
Vue.prototype.$store = store
App.mpType = 'app'

//时间戳转换
Vue.prototype.add0 = function(m) {
	return m < 10 ? '0' + m : m
};


//#ifdef H5
// import VConsole from 'vconsole' 
// const vConsole = new VConsole()
//#endif

// 引入JsonP
import {VueJsonp} from 'vue-jsonp'
Vue.use(VueJsonp)

// Vue.config.ignoredElements = ['wx-open-launch-weapp'];

Vue.prototype.getDateymd = function(str) {
	var time = new Date(str);
	var y = time.getFullYear();
	var m = time.getMonth() + 1;
	var d = time.getDate();
	var h = time.getHours();
	var mm = time.getMinutes();
	var s = time.getSeconds();
	return y + '.' + this.add0(m) + '.' + this.add0(d);
};

Vue.prototype.openMap = function(latitude, longitude, name, address) {
	uni.openLocation({
		latitude: latitude,
		longitude: longitude,
		name: name,
		address: address,
		success: function() {
			console.log('success');
		}
	});
};

// 引入全局uView
import uView from 'uview-ui'
Vue.use(uView);


// 引入vuex
import store from '@/store';

// 引入uView提供的对vuex的简写法文件
let vuexStore = require('@/store/$u.mixin.js');
Vue.mixin(vuexStore);



const app = new Vue({
	...App,
	store
})

// http拦截器，此为需要加入的内容，如果不是写在common目录，请自行修改引入路径
import httpInterceptor from '@/common/http.interceptor.js'
// 这里需要写在最后，是为了等Vue创建对象完成，引入"app"对象(也即页面的"this"实例)
Vue.use(httpInterceptor, app)

// http接口API集中管理引入部分
import httpApi from '@/common/http.api.js'
Vue.use(httpApi, app)

app.$mount()
