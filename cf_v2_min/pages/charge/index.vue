<template>
	<view>
		<map class="mapBox" :show-location="true" :markers='markers' :latitude='latitude' :longitude='longitude'
			@markertap='markertap' @bindcallouttap='onBindcallouttap' @regionchange='regionchange'
			v-if="homeOption == 1"></map>

		<ElectricBicycle v-if="homeOption == 2" :list='bikeList' />

		<view class="stopCharging u-row-center u-col-center u-flex">
			<view class="option u-flex u-rela">
				<view class="left_bg u-abso" v-if="homeOption==1"></view>
				<view class="right_bg u-abso" v-if="homeOption==2"></view>
				<view :class="homeOption==1?'stc text color_ac':'stc text'" @click="onHomeOption(1)">电动汽车</view>
				<view :class="homeOption==2?'charge text color_ac':'charge text'" @click="onHomeOption(2)">电动自行车</view>
			</view>
		</view>



		<!-- 		<view class="searchBox u-flex-r" @click="onSearchBox">
			<image src="../../static/image/home/icon-search2x.png" mode="aspectFit"></image>
			<view class="title">搜索附近充电站</view>
		</view> -->

		<view class="u-abso animated fadeInUp infoBox" style="padding-bottom: 32rpx;z-index: 3;"
			v-if="externalParking.length && !isInternal && homeOption === 1">
			<scroll-view scroll-y="true" style="height:360rpx;">
				<view class="listItem u-flex-col u-rela" @click.stop="openParkInfo(item)"
					v-for="item in externalParking" :key='item.id'>
					<view class="title">
						<view class="name u-line-1">{{item.title}}</view>
						<view class="location u-line-2" style="height: 64rpx;">{{item.address}}</view>
						<image src="../../static/image/home/Navigationicon2x.png" class="u-abso rightImg"
							style="top: 30%;"
							@click.stop="openMap(item.location.lat, item.location.lng,item.title, item.address)" />
					</view>
				</view>
			</scroll-view>
		</view>



		<view class="u-abso animated fadeInUp infoBox" v-if="beChosenArr.length && isInternal && homeOption == 1">
			<view class="listItem u-flex-c u-rela" @click="intoChargingDetail(beChosenArr[0])">
				<view class="title u-flex-r u-col-center">
					<view class="name u-line-1">{{beChosenArr[0].stationName}}</view>
				</view>
				<view class="location u-line-1">{{beChosenArr[0].address}}</view>
				<view class="priceBox u-flex-r u-col-center">
					<view class="price">￥{{beChosenArr[0].startPrice}}/度</view>
					<view class="isCharge" v-if="beChosenArr[0].chargingStationId">可充电</view>
				</view>
				<view class="giveAMark"> 免费停车{{beChosenArr[0].freeParkTime}}分钟 <text v-if="beChosenArr[0].feeUpperLimit"
						style="margin-left: 10rpx;">
						每日封顶{{ beChosenArr[0].feeUpperLimit }}元</text></view>
				<view class="u-flex">
					<view class="quantity">
						慢充可用({{beChosenArr[0].slowChargeNumber - beChosenArr[0].usedSlowChargeNumber}})</view>
					<view class="quantity">
						快充可用({{beChosenArr[0].fastChargeNumber - beChosenArr[0].usedFastChargeNumber}})</view>
					<view class="quantity">
						超快充可用({{beChosenArr[0].superFastChargeNumber - beChosenArr[0].usedSuperFastChargeNumber}})
					</view>
				</view>

				<view style="height: 30rpx;"></view>
				<!-- 	<view class="line" />
				<view style="width: 100%;" class="intoParkingNearby" @click.stop="intoParkingNearby">查看附近 <text
						style="color: #F05154;margin: 0 8rpx;font-weight: 700;">{{chargeData.length}}</text>个停车场</view>  -->
				<image src="../../static/image/home/Navigationicon2x.png" class="u-abso rightImg"
					@click.stop="openMap(beChosenArr[0].positionY, beChosenArr[0].positionX,beChosenArr[0].name, beChosenArr[0].address)" />
			</view>
		</view>
		<view class="info u-flex-col u-row-center u-col-center animated fadeInUp"
			v-if="!beChosenArr.length && isInternal && homeOption == 1">
			<image src="../../static/image/my/list_not2x.png"></image>
			<view class="list_not_title">
				抱歉~附近没有找到停车场
			</view>
		</view>


		<!-- #ifdef MP-WEIXIN -->
		<button v-if="!isLogin" @click="wxLogin" class='free-btn-bordernone'
			style='width:100%;height:100%;top:0;left:0;z-index:99999;position: fixed;'>
		</button>
		<!-- #endif -->
		<!-- #ifdef MP-ALIPAY -->
		<button v-if="!isLogin" @click="aliLogin" class='free-btn-bordernone'
			style='width:100%;height:100%;top:0;left:0;z-index:99999;position: fixed;'>
		</button>
		<!-- #endif -->


		<bindPhonePopup :phoneShow='phoneShow' @closePhoneShow='closePhoneShow' />
		<ChargingBill :show='chargingBillShow' :chargingList='chargingList' :chargingBikeList='chargingBikeList' />
	</view>
</template>

<script>
	// #ifdef H5
	import wx from 'jweixin-module';
	// #endif
	import {
		wxAppid,
		mpName2,
		aliAppid
	} from '../../common/config.js'
	var QQMapWX = require('../../utils/qqmap-wx-jssdk.js');
	var qqmapsdk;
	import parkingList from '../../components/parkingList/parkingList.vue'
	import bindPhonePopup from '../../components/bindPhonePopup/index.vue'
	import ChargingBill from './components/ChargingBill.vue'
	import ElectricBicycle from './components/ElectricBicycle.vue'
	import {
		setLocal,
		getLocal
	} from '../../common/storage.js'
	export default {
		components: {
			parkingList,
			bindPhonePopup,
			ChargingBill,
			ElectricBicycle
		},
		data() {
			return {
				bikeList: [],
				chargingList: [],
				chargingBikeList: [],
				chargingBillShow: false,
				chargingBikeBillShow: false,
				code: '',
				phoneShow: false,
				carpark_checkpoint_id: '',
				wxMiniPath: '',
				isLogin: false,
				homeOption: 1,
				//纬度
				latitude: '',
				//经度
				longitude: '',
				//停车场数据
				chargeData: [],
				markers: [],
				orderListShow: false,
				orderList: [],
				beChosenArr: [],
				parkingTime: [],
				noLicenseplateBoxShow: false,
				orderTimeInterval: '',
				isInternal: false, // 用来记录当前地图显示的停车是否为中威内部停车场
				externalParking: [], // 外部停车场 
				chooseExternalParking: []
			}
		},
		onReady() {
			uni.setNavigationBarTitle({
				title: mpName2
			})
		},
		onLoad() {
			// this.chargingBillShow = true
			// 初始化腾讯地图
			qqmapsdk = new QQMapWX({
				key: 'XNJBZ-R3RE2-JXXU2-CW5LI-IEZKH-TMBYG'
			});
		},
		watch: {
			orderListShow: {
				deep: true,
				handler: function(val, oldVal) {
					console.log(val);
					if (val) {
						// uni.hideTabBar()
					} else {
						console.log(123);
						clearInterval(this.orderTimeInterval)
						// uni.showTabBar()
					}
				}
			}
		},
		onShow() {
			this.getLocation()
			// #ifdef MP-WEIXIN
			this.getCode()
			// #endif
			if (uni.getStorageSync('wxauthed')) {
				this.getChargingBill()
				this.getChargingBikeBill()
				this.isLogin = true
			} else {
				this.isLogin = false
			}
		},
		onHide() {
			this.chargingBillShow = false
		},
		methods: {
			aliLogin() {
				uni.getAuthCode({
					scopes: ['auth_user'],
					success: async (res) => {
						if (res.authCode) {
							console.log(res.authCode);
							var loginRes = await this.$u.api.getAliMinBaseUserInfoByCode({
								auth_code: res.authCode,
								appid: aliAppid
							})
							console.log(loginRes);
							if (loginRes.code === 10002) {
								// 如果没有绑定手机号 那么就提示用户绑定
								// if (!loginRes.data.phone) {
								// 	uni.showToast({
								// 		title: '您暂未绑定手机号 可一键绑定手机号',
								// 		icon: 'none'
								// 	})
								// 	this.phoneShow = true
								// }
								this.userinfo = loginRes.data;
								this.isLogin = true
								uni.setStorageSync('wxauthed', true)
								uni.setStorageSync('user', loginRes.data)
								uni.setStorageSync('token', loginRes.token.jwt_token)
								this.$u.vuex('vuex_token', loginRes.token.jwt_token)
								this.$u.vuex('vuex_user', loginRes.data)
								this.getChargingBill()
							} else {
								uni.showToast({
									title: loginRes.message,
									icon: 'none'
								})
							}
						}
					},
				});
			},
			async getChargingBill() {
				var {
					code,
					data
				} = await this.$u.api.getChargingBill({
					page: 1,
					size: 200,
					orderBy: 'start_time desc',
					endTime: 0,
					chargingType: 2
				})
				console.log('充电订单', data);
				if (code === 10002) {
					this.chargingBillShow = true
					this.chargingList = data
				}
			},
			async getChargingBikeBill() {
				var {
					code,
					data
				} = await this.$u.api.getChargingBill({
					page: 1,
					size: 200,
					orderBy: 'start_time desc',
					endTime: 0,
					chargingType: 1
				})
				console.log('充电订单', data);
				if (code === 10002) {
					this.chargingBillShow = true
					this.chargingBikeList = data
				}
			},

			getCode() {
				wx.login({
					success: (res) => {
						console.log(res);
						this.code = res.code
					}
				})
			},
			closePhoneShow() {
				this.phoneShow = false
			},
			ParkingTime(e) {
				this.orderTimeInterval = setInterval(() => {
					this.timeFn(this.$u.timeFormat(e, 'yyyy/mm/dd hh:MM:ss'))
				}, 1000)
			},
			timeFn(d1) { //di作为一个变量传进来
				//如果时间格式是正确的，那下面这一步转化时间格式就可以不用了
				var dateBegin = new Date(d1.replace(/-/g, "/")); //将-转化为/，使用new Date
				var dateEnd = new Date(); //获取当前时间
				var dateDiff = dateEnd.getTime() - dateBegin.getTime(); //时间差的毫秒数
				var dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000)); //计算出相差天数
				var leave1 = dateDiff % (24 * 3600 * 1000) //计算天数后剩余的毫秒数
				var hours = Math.floor(leave1 / (3600 * 1000)) //计算出小时数
				//计算相差分钟数
				var leave2 = leave1 % (3600 * 1000) //计算小时数后剩余的毫秒数
				var minutes = Math.floor(leave2 / (60 * 1000)) //计算相差分钟数
				//计算相差秒数
				var leave3 = leave2 % (60 * 1000) //计算分钟数后剩余的毫秒数
				var seconds = Math.round(leave3 / 1000)
				hours += dayDiff * 24
				var dealWith = function(nums) {
					if (nums < 10) {
						return '0' + nums
					} else {
						return nums
					}
				}
				this.parkingTime = [dealWith(dayDiff), dealWith(hours), dealWith(minutes), dealWith(seconds)]
				// return [dayDiff,hours,minutes,seconds]
				// return dayDiff + "天" + hours + "小时" + minutes + "分钟" + seconds + '秒'
				// console.log(" 相差 " + dayDiff + "天 " + hours + "小时 " + minutes + " 分钟" + seconds + " 秒")
				// console.log(dateDiff + "时间差的毫秒数", dayDiff + "计算出相差天数", leave1 + "计算天数后剩余的毫秒数", hours + "计算出小时数", minutes +
				// 	"计算相差分钟数", seconds + "计算相差秒数");
			},
			openParkInfo(parkInfo) {
				if (!this.isInternal) {
					this.latitude = parkInfo.location.lat
					this.longitude = parkInfo.location.lng
					return
				}
				console.log(parkInfo);
				uni.setStorageSync("parkInfo", parkInfo)
				uni.navigateTo({
					url: '/pages/index/parkInfo',
					fail(e) {
						console.log(e);
					}
				})
			},
			intoOrderList() {
				uni.navigateTo({
					url: '../my/myParkingRecord/myParkingRecord'
				});
			},
			regionchange(e) {
				if (e.type == 'end') {
					console.log(e);
					var latitude = e.detail.centerLocation.latitude
					var longitude = e.detail.centerLocation.longitude
					this.init(longitude, latitude)
				}
				this.beChosenArr = []
			},
			markertap(e) {
				console.log(e)
				var index = e.detail.markerId
				// 如果时内部停车场
				if (this.isInternal) {
					this.beChosenArr = [this.chargeData[index]]
				} else {
					console.log(this.externalParking);
					this.chooseExternalParking = []
					this.chooseExternalParking.push(this.externalParking[index])
				}

			},

			wxLogin() {
				var _ = this
				uni.showLoading({
					title: '请稍等'
				})

				uni.getUserProfile({
					desc: '保存用户数据',
					success: async ({
						iv,
						encryptedData
					}) => {
						var data = {
							iv,
							code: this.code,
							loginType: 'mp',
							encryptedData,
							appid: wxAppid
						}
						var loginRes = await this.$u.api.wxLogin(data)
						console.log('登陆个人信息', loginRes)
						uni.hideLoading()
						// code 失效
						if (loginRes.code === 24017) {
							uni.showToast({
								title: 'code失效 请重试一次',
								icon: 'none'
							})
							_.getCode()
						} else if (loginRes.code == 10002) {
							// 如果没有绑定手机号 那么就提示用户绑定
							if (!loginRes.data.phone) {
								uni.showToast({
									title: '您暂未绑定手机号 可一键绑定手机号',
									icon: 'none'
								})
								this.phoneShow = true
							}
							this.userinfo = loginRes.data;
							this.isLogin = true
							uni.setStorageSync('wxauthed', true)
							uni.setStorageSync('user', loginRes.data)
							uni.setStorageSync('token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_user', loginRes.data)
							this.getChargingBill()
						} else {
							this.$u.toast('登陆出错')
						}
					}
				})
			},
			getUserinfo(e) {
				console.log(e);
				if (e.detail.errMsg == 'getUserInfo:ok') {
					this.isLogin = true
					setLocal('wxauthed', true)
					this.wxLogin()
				}
			},
			async init(longitude, latitude) {
				if (!longitude) longitude = this.longitude
				if (!latitude) latitude = this.latitude

				// 获取电动汽车列表
				var res = await this.$u.api.getChargingStationList({
					//经度
					positionX: longitude,
					//纬度
					positionY: latitude,
					page: 1,
					distance: 8000,
					size: 200,
					stationType: 2
				})
				console.log('附近充电站', res);
				if (res.code == 10002) {
					this.beChosenArr = [res.data[0]]
					this.chargeData = res.data
					this.isInternal = true
					this.refreshMarkers(this.chargeData, 'cf')
				} else {
					this.beChosenArr = []
					this.isInternal = false
					this.getOtherParking(latitude, longitude)
				}

				// 获取电动自行车列表
				var bikeRes = await this.$u.api.getChargingStationList({
					//经度
					positionX: longitude,
					//纬度
					positionY: latitude,
					page: 1,
					distance: 8000,
					size: 200,
					stationType: 1
				})
				console.log('bikeRes', bikeRes);
				if (bikeRes.code === 10002) {
					this.bikeList = bikeRes.data
				}
			},
			// 点击了气泡
			onBindcallouttap(e) {
				console.log('气泡', e);
			},
			refreshMarkers(datas, type) {
				if (type === 'cf') {
					var markers = []
					datas.forEach((v, idx) => {
						markers.push({
							'id': idx,
							'latitude': v.positionY,
							'longitude': v.positionX,
							'title': v.stationName,
							'iconPath': 'http://file.cfeng.wang/charge.png',
							'width': '32',
							'height': '32',
							callout: {
								content: v.stationName,
								color: '#FFFFFF',
								fontSize: 12,
								bgColor: '#301819',
								padding: 10,
								borderRadius: 20,
							}
						})
					})
					this.markers = markers
				} else if (type === 'tx') {
					var markers = []
					datas.forEach((v, idx) => {
						markers.push({
							'id': idx,
							'latitude': v.location.lat,
							'longitude': v.location.lng,
							'title': v.title,
							'iconPath': 'http://file.cfeng.wang/charge.png',
							'width': '32',
							'height': '32',
							callout: {
								content: v.title,
								color: '#FFFFFF',
								fontSize: 12,
								bgColor: '#301819',
								padding: 10,
								borderRadius: 20,
							}
						})
					})
					this.markers = markers
				}
			},
			intoParkingNearby() {
				console.log(this.chargeData);
				uni.setStorageSync('nearbyParkingLotList', this.chargeData)
				uni.navigateTo({
					url: '/pages/index/nearbyParkingLot'
				})
			},
			// 调用其他停车场
			getOtherParking(latitude, longitude) {
				var that = this
				// #ifdef H5
				const KEY = "XNJBZ-R3RE2-JXXU2-CW5LI-IEZKH-TMBYG";
				let url = "https://apis.map.qq.com/ws/place/v1/search"
				let keyword = '停车场'
				console.log(this.$jsonp);
				console.log(`nearby(${latitude},${longitude},1000,1)`);
				this.$jsonp(url, {
					key: KEY,
					boundary: `nearby(${latitude},${longitude},1000,1)`,
					keyword: keyword,
					output: "jsonp"
				}).then(res => {
					console.log(res)
					that.externalParking = res.data
					that.refreshMarkers(res.data, 'tx')
				}).catch(err => {
					console.log(err)
				})
				// #endif
				// #ifdef MP
				qqmapsdk.search({
					keyword: '充电站',
					location: `${latitude},${longitude}`,
					success: function(res) {
						console.log(res);
						if (res.status === 0) {
							var markers = []
							that.externalParking = res.data
							that.refreshMarkers(res.data, 'tx')
						}
					},
					fail: function(err) {
						console.log(err);
					}
				})
				// #endif
			},
			intoChargingDetail(item) {
				if (!this.isInternal) return
				uni.navigateTo({
					url: '../index/parkInfo?chargeInfo=' + JSON.stringify(item)
				})
			},
			onSearchBox() {
				uni.navigateTo({
					url: '../../chargeModule/pages/search/index'
				})
			},
			onHomeOption(value) {
				// uni.showToast({
				// 	title: '正在开发中',
				// 	icon: 'none'
				// })
				// return
				this.homeOption = value
			},
			getLocation() {
				uni.getLocation({
					// #ifdef MP-WEIXIN
					type: 'gcj02',
					// #endif
					success: (res) => {
						console.log(555, res)
						this.latitude = res.latitude
						this.longitude = res.longitude
						this.init();
					}
				})
			}
		}
	}
</script>

<style lang="scss">
	.stopCharging {
		width: 750rpx;
		height: 120rpx;
		background: #FFFFFF;
		border-radius: 0rpx 0rpx 32rpx 32rpx;
		z-index: 2;
		position: fixed;
		left: 0;
		top: 0;

		.option {
			width: 444rpx;
			height: 80rpx;
			border-radius: 40rpx;
			border: 2rpx solid #DBDBDB;

			.color_ac {
				color: #fff !important;
			}

			.stc {
				// margin: 18rpx 0 0 64rpx;
				width: 50%;
				height: 100%;
				height: 80rpx;
				text-align: center;
			}

			.charge {
				// margin: 18rpx 0 0 110rpx;
				width: 50%;
				height: 100%;
				height: 80rpx;
				text-align: center;
			}

			.text {
				height: 80rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				// color: #FFFFFF;
				color: rgba(50, 50, 51, 0.5);
				line-height: 80rpx;
				position: relative;
			}

			.left_bg {
				width: 222rpx;
				height: 76rpx;
				background: #488CF5;
				border-radius: 40rpx;
				left: 0;
				top: 0;
				box-sizing: border-box;
			}

			.right_bg {
				width: 222rpx;
				height: 76rpx;
				background: #488CF5;
				border-radius: 40rpx;
				right: 0;
				top: 0;
				box-sizing: border-box;
			}
		}
	}

	.mapBox {
		width: 750rpx;
		height: 100vh;
		z-index: 1;
		position: fixed;
		top: 0;
		left: 0;
	}

	.infoBox {
		width: 690rpx;
		left: 30rpx;
		background: #FFFFFF;
		box-shadow: 0rpx 16rpx 22rpx 0rpx rgba(10, 36, 99, 0.07);
		border-radius: 16rpx;
		z-index: 3;
		bottom: 68rpx;
		overflow: hidden;
	}

	.searchBox {
		width: 690rpx;
		height: 96rpx;
		background: #FFFFFF;
		box-shadow: 0rpx 8rpx 16rpx 0rpx rgba(0, 0, 0, 0.08), 0rpx 0rpx 2rpx 0rpx rgba(0, 0, 0, 0.08);
		border-radius: 60rpx;
		position: fixed;
		z-index: 2;
		left: 30rpx;
		top: 56rpx;

		image {
			width: 48rpx;
			height: 48rpx;
			margin: 24rpx 0 0 30rpx;
		}

		.title {
			height: 48rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(26, 26, 26, 0.5);
			line-height: 48rpx;
			margin: 24rpx 0 0 8rpx;
		}
	}

	.noLicenseplateBox {
		width: 624rpx;
		height: 776rpx;
		background: #FDFEFF;
		border-radius: 16rpx;

		.image {
			width: 200rpx;
			height: 200rpx;
			margin-top: 104rpx;
		}

		.title1 {
			width: 294rpx;
			height: 36rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 36rpx;
			margin-top: 16rpx;
		}

		.title2 {
			width: 342rpx;
			height: 80rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.5);
			line-height: 40rpx;
			margin-top: 32rpx;
			text-align: center;
		}

		.button {
			width: 456rpx;
			height: 100rpx;
			background: #488cf5;
			border-radius: 16rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #FFFFFF;
			line-height: 100rpx;
			text-align: center;
			margin-top: 130rpx;
		}
	}

	.noLicenseplate_close {
		width: 48rpx;
		height: 48rpx;
		margin: 44rpx 0 0 0;
		z-index: 4;
	}

	.title22 {
		// width: 262rpx;
		height: 90rpx;
		font-size: 64rpx;
		font-family: PingFangSC-Semibold, PingFang SC;
		font-weight: 600;
		color: #323233;
		line-height: 90rpx;
	}



	.orderInfo {
		width: 750rpx;
		height: 526rpx;
		background: #FFFFFF;
		border-radius: 48rpx 48rpx 0rpx 0rpx;
		position: absolute;
		bottom: var(--window-bottom);
		// align-self: flex-end;
		background-image: url('http://file.cfeng.wang/bigLogo.png');
		background-size: 344rpx 432rpx;
		background-repeat: no-repeat;
		background-position-y: 94rpx;

		.numberPlate {
			height: 56rpx;
			font-size: 40rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 56rpx;
			margin-top: 100rpx;
		}

		.parkingTime {
			width: 96rpx;
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 34rpx;
			margin-top: 4rpx;
		}

		.timesBox {
			height: 80rpx;
			margin-top: 12rpx;

			.day {
				height: 72rpx;
				font-size: 52rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #488CF5;
				line-height: 72rpx;
				margin-right: 12rpx;
			}

			.time {
				// width: 80rpx;
				min-width: 80rpx;
				padding: 0 8rpx;
				height: 80rpx;
				background: #488CF5;
				border-radius: 10rpx;
				font-size: 52rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #FFFFFF;
				line-height: 80rpx;
				text-align: center;
			}

			.timeDivision {
				width: 8rpx;
				height: 26rpx;
				margin: 0 8rpx;
			}
		}

		.intoOrder {
			width: 670rpx;
			height: 100rpx;
			background: #488CF5;
			border-radius: 16rpx;
			margin-top: 80rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #FFFFFF;
			line-height: 100rpx;
			text-align: center;
		}

		.more {
			margin-top: 76rpx;

			.intoOrder2 {
				width: 320rpx;
				height: 100rpx;
				background: #488CF5;
				border-radius: 16rpx;
				font-size: 36rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #FFFFFF;
				line-height: 100rpx;
				text-align: center;
			}

			.seeMore {
				width: 320rpx;
				height: 100rpx;
				border-radius: 16rpx;
				border: 2rpx solid #488CF5;
				font-size: 36rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #488CF5;
				line-height: 100rpx;
				text-align: center;
				margin-left: 30rpx;
			}
		}
	}

	.listItem {
		background-color: #fff;

		.title {
			.name {
				max-width: 360rpx;
				height: 40rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #1A1A1A;
				line-height: 40rpx;
				margin: 32rpx 0 0 32rpx;
			}

			.near {
				width: 96rpx;
				// height: 32rpx;
				border-radius: 4rpx;
				border: 1rpx solid #FF575B;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #F05154;
				line-height: 32rpx;
				text-align: center;
				margin-left: 8rpx;
			}
		}

		.location {
			width: 470rpx;
			height: 32rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			line-height: 32rpx;
			margin: 4rpx 0 0 34rpx;
			color: rgba(50, 50, 51, 0.5);
		}

		.price {
			height: 36rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #F05154;
			line-height: 36rpx;
		}

		.giveAMark {
			width: 446rpx;
			height: 28rpx;
			font-size: 20rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #FF5151;
			line-height: 28rpx;
			margin: 10rpx 0 0 32rpx;
		}

		.priceBox {
			margin-left: 34rpx;
			margin-top: 12rpx;

			.isCharge {
				width: 78rpx;
				height: 32rpx;
				border-radius: 4rpx;
				border: 2rpx solid #FF5151;
				line-height: 32rpx;
				text-align: center;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #FF5151;
				margin-left: 8rpx;
				box-sizing: content-box;
			}

			.num {
				height: 36rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #F05154;
				line-height: 36rpx;
				margin-right: 4rpx;
			}

			.preferential {
				padding: 1rpx 8rpx;
				height: 32rpx;
				background: #FF5151;
				border-radius: 4rpx;
				// width: 72rpx;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #FFFFFF;
				line-height: 30rpx;
				margin-left: 4rpx;
			}


		}

		.quantity {
			// width: 446rpx;
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #3B76D0;
			line-height: 34rpx;
			margin: 12rpx 0 0 32rpx;
		}

		.line {
			width: 622rpx;
			height: 1rpx;
			background: #EAEAEA;
			margin: 16rpx 0 0 36rpx;
		}

		.intoParkingNearby {
			width: 310rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.5);
			text-align: center;
			height: 88rpx;
			line-height: 88rpx;
		}

		.rightImg {
			width: 120rpx;
			height: 120rpx;
			top: 88rpx;
			right: 30rpx;
		}
	}
</style>