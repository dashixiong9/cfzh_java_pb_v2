// 此处第二个参数vm，就是我们在页面使用的this，你可以通过vm获取vuex等操作，更多内容详见uView对拦截器的介绍部分：
// https://uviewui.com/js/http.html#%E4%BD%95%E8%B0%93%E8%AF%B7%E6%B1%82%E6%8B%A6%E6%88%AA%EF%BC%9F
const install = (Vue, vm) => {
	// 此处没有使用传入的params参数
	let selectNearbyDatas = (params = {}) => vm.$u.get('/carpark/selectNearbyDatas', params);
	let getMyCarsByCondition = (params = {}) => vm.$u.get('/carparkcar/getMyCarsByCondition', params);
	let wxLogin = (params = {}) => vm.$u.get('/auth/getWxBaseUserInfoByCodeAndLoginType', params);
	// 新增车牌 
	let addCar = (params = {}) => vm.$u.post('/carparkcar/add', params);
	// 我的优惠券
	let getMyConpon = (params = {}) => vm.$u.get('/coupon/orderList', params);
	// 停车记录
	let getCarParkUseLogList = (params = {}) => vm.$u.get('/carparklog/getCarParkUseLogList', params);
	// 我的账单
	let getMyBill = (params = {}) => vm.$u.get('/pay/orderList', params);
	// 优惠券详情
	let selectContainDistanceListByCouponId = (params = {}) => vm.$u.get(
		'/carpark/selectContainDistanceListByCouponId', params);
	// 搜索停车场
	let getListByQuery = (params = {}) => vm.$u.get('/carpark/getListByQuery', params);
	// 根据停车记录id查询订单
	let queryOrderByUseId = (params = {}) => vm.$u.get('/carparkorder/queryOrderByUseId', params);
	// 我的车辆套餐
	let getMyCarParkPackageByCondition = (params = {}) => vm.$u.get(
		'/carparkpackage/getMyCarParkPackageByCondition', params);
	// 获取我的员工
	let getMyStaffList = (params = {}) => vm.$u.get('/staff/getMyStaffList', params);
	// 雇主更新员工数据
	let employerUpdateStaff = (params = {}) => vm.$u.put('/staff/employerUpdateStaff', params);
	// 雇主删除员工
	let employerDeleteStaff_delete = (id = {}) => vm.$u.delete('/staff/employerDeleteStaff?id='+id);
	// 雇主添加员工
	let employerAddStaff = (params = {}) => vm.$u.post('/staff/employerAddStaff', params);
	// 根据id查用户数据
	let findById = (params = {}) => vm.$u.get('/ucenter/findById', params);
	// 获取指定停车场的收费规则
	let getCarParkChargingRules = (params = {}) => vm.$u.get('/carpark/getCarParkChargingRules', params);
	
	// // 获取指定停车场的收费规则
	let getCarparkChargingRules2 = (params = {}) => vm.$u.get('/carparkChargingRules/getListByQuery', params);
	
	
	// 分发停车卷信息
	let getMyCouponActivityList = (params = {}) => vm.$u.get('/couponActivity/getMyCouponActivityList', params);
	// 店主添加优惠券活动优惠券类型(设置发券时的 金额,有效期)
	let shopkeeperAddCouponActivityCouponType = (params = {}) => vm.$u.post(
		'/couponActivity/shopkeeperAddCouponActivityCouponType', params);
	// 店主获取优惠券活动优惠券设置列表
	let getMyCouponActivityCouponSettingList = (params = {}) => vm.$u.get(
		'/couponActivity/getMyCouponActivityCouponSettingList', params);
	// 店主删除优惠券活动优惠券类型
	let shopkeeperDeleteCouponActivityCouponType = (params = {}) => vm.$u.delete(
		'/couponActivity/shopkeeperDeleteCouponActivityCouponType', params);
	// 获取员工发放优惠券设置
	let getStaffCouponSettingByQuery = (params = {}) => vm.$u.get(
		'/staffCouponSetting/getStaffCouponSettingByQuery', params);
	// 更新员工发放优惠券设置
	let employerUpdateStaffCouponSetting = (params = {}) => vm.$u.put(
		'/staffCouponSetting/employerUpdateStaffCouponSetting', params);
	// 员工生成领券二维码
	let staffCreateCouponQrCode = (params = {}) => vm.$u.post('/couponActivity/staffCreateCouponQrCode', params);
	// 获取优惠券活动详情
	let activityDetail = (params = {}) => vm.$u.get('/couponActivity/activityDetail', params);
	// 获取优惠券活动优惠券类型
	let getCouponActivityCouponTypeListByQuery = (params = {}) => vm.$u.get(
		'/couponActivity/getCouponActivityCouponTypeListByQuery', params);
	// 通过二维码领取优惠券
	let getCouponByQrCode = (params = {}) => vm.$u.post('/couponActivity/getCouponByQrCode', params);
	// 给优惠活动充值
	let rechargeToCouponActivity = (params = {}) => vm.$u.post('/couponActivity/rechargeToCouponActivity', params);
	// 根据条件获取支付账号基础数据
	let getUserPaymentAgencyListByQuery = (params = {}) => vm.$u.get('/pay/getUserPaymentAgencyListByQuery',
		params);
	// 获取停车场的套餐列表
	let getCarParkPackagePriceByCondition = (params = {}) => vm.$u.get(
		'/carparkpackage/getCarParkPackagePriceByCondition', params);
	// 获取附近或者指定停车场的出入口
	let carparkCheckpointGetListByQuery = (params = {}) => vm.$u.get('/carparkCheckpoint/getListByQuery', params);
	// 删除车辆
	let deleteCarparkcar = (id = {}) => vm.$u.delete('/carparkcar/delete?id='+id);
	// 支付停车费
	let payForCarUseLog = (params = {}) => vm.$u.put('/carparkorder/payForCarUseLog', params);
	// 获取停车场的支付方式(机构)
	let getPaymentAgencyListByCarParkId = (params = {}) => vm.$u.get('/pay/getPaymentAgencyListByCarParkId',
		params);
	// 购买套餐
	let buyCfCarParkPackage = (params = {}) => vm.$u.post('/carparkpackage/buyCfCarParkPackage', params);
	// 获取指定出口关卡最新的车牌识别记录
	let getLastCarParkUseLogList = (params = {}) => vm.$u.get('/carparklog/getLastCarParkUseLogList', params);
	// 发送短信
	let sendSms = (params = {}) => vm.$u.post('/sms/sendSms', params);
	// 绑定手机号
	let bindPhone = (params = {}) => vm.$u.post('/ucenter/bindPhone', params);
	// 员工或者店主直接给车辆赠送优惠券
	let staffOrShopkeeperGiveCouponToCar = (params = {}) => vm.$u.post(
		'/couponActivity/staffOrShopkeeperGiveCouponToCar', params);
	// 获取发券记录
	let getCouponActivityCouponLogList = (params = {}) => vm.$u.get(
		'/couponActivity/getCouponActivityCouponLogList', params);
	// 套餐产品详情
	let getCarParkPackagePriceDetail = (params = {}) => vm.$u.get('/carparkpackage/getCarParkPackagePriceDetail',
		params);
	// 申请通行
	let applyForAccess = (params = {}) => vm.$u.get('/carparklog/applyForAccess',
		params);
	// 获取附近或者指定停车场的出入口
	let selectNearbyCheckPoint = (params = {}) => vm.$u.get('/carparkCheckpoint/selectNearbyCheckPoint',
		params);
	// 人工主动上报停车记录(断电断网 人工手机一键快捷补录停车数据)
	let manuallyUpload = (params = {}) => vm.$u.post('/carparklog/manuallyUpload',
		params);
	// 获取支付机构列表
	let getPaymentAgencyListByQuery = (params = {}) => vm.$u.get('/pay/getPaymentAgencyListByQuery',
		params);
	// 查车辆信息
	let selectPackageListByQuery = (params = {}) => vm.$u.get('/carparkpackageAdmin/selectPackageListByQuery',
		params);
	// 获取我的资金列表
	let getMyAccountList = (params = {}) => vm.$u.get('/account/myAccountList',
		params);
	// 余额充值
	let customerCheckAndAddBalanceByQuery = (params = {}) => vm.$u.put('/account/customerCheckAndAddBalanceByQuery',
		params);
	// 根据条件获取积分类型列表
	let getScoreTypeListByQuery = (params = {}) => vm.$u.get('/account/getScoreTypeListByQuery',
		params);
	// 获取广告
	let selectContainAdByQuery = (params = {}) => vm.$u.get('/adlinks/selectContainAdByQuery',
		params);	
	// 获取附近的充电站
	let getChargingStationList = (params = {}) => vm.$u.get('/chargingStation/selectContalDistanceListByQuery',
		params);
	// 查找充电站		
	let searchChargingStation = (params = {}) => vm.$u.get('/chargingStation/getListByQuery',
			params);
	// 查找口		
	let searchchargingPort = (params = {}) => vm.$u.get('/chargingPort/getListByQuery',
			params);
	// 查找充电设备
	let searchChargingStationDevice = (params = {}) => vm.$u.get('/chargingStationDevice/getListByQuery',
			params);			
	// 查询充电站收费规则
	let getChargingRules = (params = {}) => vm.$u.get('/chargingRules/getListByQuery',
			params);	
					
					
	// 开始充电
	let startCharging = (params = {}) => vm.$u.post('/chargingStationDevice/startCharging', params);		
	// 停止充电
	let stopCharging = (params = {}) => vm.$u.post('/chargingStationDevice/stopCharging', params);				
	// 获取ws地址
	let getMinChatLinkCountsServiceIp = (params = {}) => vm.$u.get('/friends/getMinChatLinkCountsServiceIp',
			params);		
	// 获取公众号信息
	let createJumpToWxMinData = (params = {}) => vm.$u.get('/auth/createJumpToWxMinData',
			params);
	let getUserPhonenumberByCodeAndLoginType = (params = {}) => vm.$u.get('/auth/getUserPhonenumberByCodeAndLoginType', params);
	// 获取已过期车辆的逃费情况
	let queryCfCarParkPackageRecoverFee = (params = {}) => vm.$u.get('/carparkpackage/queryCfCarParkPackageRecoverFee',params);
	// 查询充电账单
	let getChargingBill = (params = {}) => vm.$u.get('/chargingUseLog/getListByQuery',
			params);	
	// 获取所有的车辆类型
	let getCarparkCarType = (params = {}) => vm.$u.get('/carparkcartype/getAllList',
			params);	
	// 根据充电记录id获取充电账单
	let getChargingOrderByUesLogId = (params = {}) => vm.$u.get('/chargingUseLog/getChargingOrderByUesLogId	',
			params);			
	
	// 支付宝小程序登录
	let getAliMinBaseUserInfoByCode = (params = {}) => vm.$u.get('/auth/getAliMinBaseUserInfoByCode',
			params);	
	
	// 用户基本资料更新(昵称/头像/性别等
	let customerBaseUpdate = (params = {}) => vm.$u.put('/ucenter/customerBaseUpdate',
			params);	
	
	
			
	// 将各个定义的接口名称，统一放进对象挂载到vm.$u.api(因为vm就是this，也即this.$u.api)下
	vm.$u.api = {
		selectNearbyDatas,
		getMyCarsByCondition,
		wxLogin,
		addCar,
		getMyConpon,
		getCarParkUseLogList,
		getMyBill,
		selectContainDistanceListByCouponId,
		getListByQuery,
		queryOrderByUseId,
		getMyCarParkPackageByCondition,
		getMyStaffList,
		employerUpdateStaff,
		employerDeleteStaff_delete,
		employerAddStaff,
		findById,
		getCarParkChargingRules,
		getMyCouponActivityList,
		shopkeeperAddCouponActivityCouponType,
		getMyCouponActivityCouponSettingList,
		shopkeeperDeleteCouponActivityCouponType,
		getStaffCouponSettingByQuery,
		employerUpdateStaffCouponSetting,
		staffCreateCouponQrCode,
		getCouponActivityCouponTypeListByQuery,
		getCouponByQrCode,
		rechargeToCouponActivity,
		getUserPaymentAgencyListByQuery,
		getCarParkPackagePriceByCondition,
		carparkCheckpointGetListByQuery,
		deleteCarparkcar,
		payForCarUseLog,
		getPaymentAgencyListByCarParkId,
		buyCfCarParkPackage,
		getLastCarParkUseLogList,
		sendSms,
		bindPhone,
		staffOrShopkeeperGiveCouponToCar,
		activityDetail,
		getCouponActivityCouponLogList,
		getCarParkPackagePriceDetail,
		applyForAccess,
		selectNearbyCheckPoint,
		manuallyUpload,
		getPaymentAgencyListByQuery,
		selectPackageListByQuery,
		getMyAccountList,
		customerCheckAndAddBalanceByQuery,
		getScoreTypeListByQuery,
		selectContainAdByQuery,
		getChargingStationList,
		searchChargingStation,
		searchchargingPort,
		searchChargingStationDevice,
		getChargingRules,
		startCharging,
		stopCharging,
		getMinChatLinkCountsServiceIp,
		createJumpToWxMinData,
		getUserPhonenumberByCodeAndLoginType,
		queryCfCarParkPackageRecoverFee,
		getChargingBill,
		getCarparkCarType,
		getCarparkChargingRules2,
		getChargingOrderByUesLogId,
		getAliMinBaseUserInfoByCode,
		customerBaseUpdate
	};



}

export default {
	install
}
