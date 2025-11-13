<template>
	<view>
		<image src="http://file.cfeng.wang/min/BG.png" class="bg_image u-abso" @click="intoList"></image>
		<view class="u-flex-r u-row-evenly description">
			<view class="u-flex-c" v-for="(item,index) in list">
				<image :src="'../../static/image/monthlyCard/' + item.image + '.png'" class="description_image"></image>
				<view class="description_name">{{item.name}}</view>
			</view>
		</view>
		<!-- 我的套餐 -->
		<view class="mySetMeal u-rela u-flex-c" v-if="carList.length == 0">
			<view class="title1">我的停车套餐卡</view>
			<view class="title2">您还未升级停车场套餐卡，快快体验吧</view>
			<image src="http://file.cfeng.wang/Upgrade.png" class="upgrade" @click="intoList"></image>
			<!-- 装饰图片 -->
			<image src="../../static/image/monthlyCard/zs.png" class="zs u-abso"></image>
		</view>
		<!-- 我的套餐 -->
		<view class="mySetMeal u-rela u-flex-c" style="height: 316rpx;" v-for="item of carList"
			v-if="carList.length > 0">
			<view class="title1" style="width: 358rpx;">{{item.name || '-'}}</view>
			<view class="title2 u-flex-r u-col-center">
				<view class="setMealNumber">{{item.numberPlate}}</view>
				<view class="setMealExpire">{{calculation(item.endTime)}}</view>
			</view>
			<view class="setMealAvailable u-line-2">{{item.carParkName}}可用</view>
			<image src="http://file.cfeng.wang/Renew.png" class="Renewal" @click="onRenew(item)"></image>
			<!-- 装饰图片 -->
			<image src="../../static/image/monthlyCard/zs.png" class="zs u-abso"></image>
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
	</view>
</template>

<script>
	import {
		wxAppid,
		aliAppid
	} from '../../common/config.js'
	import dayjs from 'dayjs'
	import bindPhonePopup from '../../components/bindPhonePopup/index.vue'
	export default {
		components: {
			bindPhonePopup
		},
		data() {
			return {
				code: '',
				phoneShow: false,
				isLogin: false,
				carList: [],
				list: [{
						image: 'Discount',
						name: '超低折扣'
					},
					{
						image: 'pay',
						name: '便捷支付'
					},
					{
						image: 'Agreement1',
						name: '优先预约'
					},
					{
						image: 'Agreement2',
						name: '专属客服'
					}
				]
			}
		},
		onShow() {
			var wxauthed = uni.getStorageSync('wxauthed')
			// #ifdef MP-WEIXIN
			this.getCode()
			// #endif
			if (wxauthed) {
				this.isLogin = true
				this.getList()
			} else {
				this.isLogin = false
			}
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
								// if (!loginRes.data.phone) {
								// 	uni.showToast({
								// 		title: '您暂未绑定手机号 可一键绑定手机号',
								// 		icon: 'none'
								// 	})
								// 	this.phoneShow = true
								// }
								this.isLogin = true
								this.userinfo = loginRes.data;
								uni.setStorageSync('wxauthed', true)
								uni.setStorageSync('user', loginRes.data)
								uni.setStorageSync('token', loginRes.token.jwt_token)
								this.$u.vuex('vuex_token', loginRes.token.jwt_token)
								this.$u.vuex('vuex_user', loginRes.data)
								this.getList()
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
			// 点击续费	
			async onRenew(item) {
				var setMealRes = await this.$u.api.getCarParkPackagePriceDetail({
					cfCarParkPackagePriceId: item.packagePriceId
				})
				var parkingLotInfo = {
					carParkName: item.carParkName,
					numberPlate: item.numberPlate
				}
				uni.navigateTo({
					url: 'pay?setMealInfo=' + JSON.stringify(setMealRes.data) + '&parkingLotInfo=' + JSON
						.stringify(parkingLotInfo)
				})

				return

				uni.navigateTo({
					url: 'pay?info=' + JSON.stringify(item)
				})
			},
			calculation(endTime) {
				if (dayjs().valueOf() > endTime) {
					return '已过期'
				}
				var nTime = new Date()
				var time = Math.floor((endTime - nTime) / (1000 * 60 * 60 * 24))
				if (time < 0) return ''
				return time + '天到期'
			},
			async getList() {
				var res = await this.$u.api.getMyCarParkPackageByCondition({
					page: 1,
					size: 999
				})
				console.log(123, res);
				if (res.code == 10002) {
					this.carList = res.data
				} else {
					this.carList = []
				}
			},
			intoList() {
				uni.navigateTo({
					url: './selectParkingLot'
				})
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
							if (!loginRes.data.phone) {
								uni.showToast({
									title: '您暂未绑定手机号 可一键绑定手机号',
									icon: 'none'
								})
								this.phoneShow = true
							}
							this.isLogin = true
							this.userinfo = loginRes.data;
							uni.setStorageSync('wxauthed', true)
							uni.setStorageSync('user', loginRes.data)
							uni.setStorageSync('token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_user', loginRes.data)
							this.getList()
						} else {
							this.$u.toast('登陆出错')
						}
					}
				})



				// uni.getUserProfile({
				// 	desc: '保存用户数据',
				// 	success: (res) => {
				// 		uni.login({
				// 			success: async (loginRes) => {
				// 				let {
				// 					code
				// 				} = loginRes
				// 				let {
				// 					encryptedData,
				// 					iv
				// 				} = res
				// 				var data = {
				// 					iv,
				// 					code,
				// 					loginType: 'mp',
				// 					encryptedData
				// 				}
				// 				var loginRes = await this.$u.api.wxLogin(data)
				// 				console.log('登陆个人信息', loginRes)
				// 				if (loginRes.code == 10002) {
				// 					if(!loginRes.data.phone){
				// 						uni.showToast({
				// 							title:'您暂未绑定手机号 可一键绑定手机号',
				// 							icon:'none'
				// 						})
				// 						this.phoneShow = true
				// 					}
				// 					this.isLogin = true
				// 					this.userinfo = loginRes.data;
				// 					uni.setStorageSync('wxauthed', true)
				// 					uni.setStorageSync('user', loginRes.data)
				// 					uni.setStorageSync('token', loginRes.token.jwt_token)
				// 					this.$u.vuex('vuex_token', loginRes.token.jwt_token)
				// 					this.$u.vuex('vuex_user', loginRes.data)
				// 					this.getList()
				// 				} else {
				// 					this.$u.toast('登陆出错')
				// 				}
				// 			}
				// 		})

				// 	},
				// 	fail(err) {
				// 		console.log('err', err);
				// 	}
				// })
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f8f9fb;
	}

	.bg_image {
		z-index: 2;
		width: 750rpx;
		height: 448rpx;
		left: 0;
		/*  #ifdef  H5  */
		position: fixed !important;
		top: 0rpx !important;
		/*#endif  */
		top: 0;
	}

	.description {
		margin-top: 474rpx;
		margin-bottom: 64rpx;

		.description_image {
			width: 88rpx;
			height: 88rpx;
		}

		.description_name {
			margin-top: 16rpx;
			width: 96rpx;
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 34rpx;
		}
	}

	.mySetMeal {
		width: 686rpx;
		height: 268rpx;
		background: #FFFFFF;
		box-shadow: 0rpx 16rpx 22rpx 0rpx rgba(151, 151, 151, 0.07);
		border-radius: 16rpx;
		margin: 0rpx 0 32rpx 32rpx;

		.zs {
			width: 176rpx;
			height: 176rpx;
			top: 0;
			right: 0;
		}

		.title1 {
			width: 358rpx;
			height: 44rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			color: #323233;
			font-size: 32rpx;
			font-weight: 500;
			line-height: 44rpx;
			margin-left: 32rpx;
			margin-top: 32rpx;
		}

		.title2 {
			width: 442rpx;
			height: 36rpx;
			font-size: 26rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.5);
			line-height: 36rpx;
			margin-left: 32rpx;
			margin-top: 12rpx;
		}

		.upgrade {
			width: 242rpx;
			height: 108rpx;
			margin-left: 8rpx;
			margin-top: 24rpx;
		}
	}

	.setMealNumber {
		// width: 134rpx;
		height: 40rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #323233;
		line-height: 40rpx;
		margin-right: 20rpx;
	}

	.setMealExpire {
		height: 32rpx;
		border-radius: 4rpx;
		border: 2rpx solid #FF575B;
		font-size: 20rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #F05154;
		line-height: 28rpx;
		padding: 0 8rpx;
	}

	.setMealAvailable {
		width: 622rpx;
		height: 72rpx;
		font-size: 26rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.5);
		line-height: 36rpx;
		margin: 6rpx 0 0 32rpx;
	}

	.Renewal {
		width: 242rpx;
		height: 108rpx;
		margin: 4rpx 0 0 444rpx;
	}
</style>