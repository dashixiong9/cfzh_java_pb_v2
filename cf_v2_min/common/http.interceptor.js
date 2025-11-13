import {
	getLocal
} from './storage.js'
import {
	baseUrl
} from './config.js'
// common/http.interceptor.js
// 这里的Vue为Vue对象(非创建出来的实例)，vm为main.js中“Vue.use(httpInterceptor, app)”这一句的第二个参数，
// 为一个Vue的实例，也即每个页面的"this"
// 如果需要了解这个install方法是什么，请移步：https://uviewui.com/components/vueUse.html
const install = (Vue, vm) => {
	// 此为自定义配置参数，具体参数见上方说明
	Vue.prototype.$u.http.setConfig({
		// baseUrl: 'https://park.fksg.net',
		baseUrl,
		loadingText: '努力加载中~',
		loadingTime: 800,
		// 设置自定义头部content-type
		header: {
			'Content-type': 'application/json;charset=UTF-8'
		}
	});
	Vue.prototype.$u.http.interceptor.request = (config) => {
		// 引用token
		const token = uni.getStorageSync('token');
		config.header.Authorization = token ? ('Bearer ' + token) : ''
		return config;
	}
	// 响应拦截，如配置，每次请求结束都会执行本方法
	Vue.prototype.$u.http.interceptor.response = (res) => {
		let routes = getCurrentPages() //获取当前页面栈
		let curRoute = routes[routes.length - 1].route //获取当前页面的路由
		if (res.code == 10002) {
			return res
		} else if (res.code == 24010) {
			uni.clearStorageSync()
			uni.setStorageSync('wxauthed', false)
			// 该页面如果token过期不跳转
			console.log(curRoute);
			if(curRoute !== 'pages/my/replacePay/getClipboard'){
				uni.showToast({
					title: '用户信息已过期,请重新授权',
					icon: 'none'
				})
				setTimeout(() => {
					//#ifdef MP
					uni.navigateTo({
						url: '/pages/authorize/authorize'
					})
					//#endif
					//#ifdef H5
					uni.navigateTo({
						url: '/pages/my/h5Login/h5Login.vue'
					})
					//#endif
				}, 1500)
			}
			return false;
		} else {
			// 如果返回false，则会调用Promise的reject回调，
			// 并将进入this.$u.post(url).then().catch(res=>{})的catch回调中，res为服务端的返回值
			return res
		}
	}
}

export default {
	install
}
