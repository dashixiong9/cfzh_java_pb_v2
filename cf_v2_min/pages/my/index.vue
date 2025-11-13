<template>
	<view>
		<view class="headerBg"></view>
		<view class="mainBox u-flex-col u-col-center u-rela">
			<image src="../../static/image/my/bg@2x.png"
				style="position: absolute;left: 0;top: 0;width: 750rpx;height: 350rpx;z-index: -1;"></image>
			<!-- #ifdef MP-WEIXIN -->
			<image v-if="!isLogin" src="../../static/image/my/Headimage2x1.png" class="avatar" style="z-index: 9999;">
			</image>
			<image v-else :src="userinfo.avatar" class="avatar" style="z-index: 9999;"></image>
			<!-- <open-data type="userAvatarUrl" class="avatar"></open-data> -->
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<image :src="userinfo.avatar" class="avatar" v-if="userinfo.avatar" style="z-index: 9999;"></image>
			<!-- #endif -->
			<!-- #ifdef MP-ALIPAY -->
			<image src="../../static/image/my/Headimage2x.png" class="avatar" v-if="!userinfo.avatar"
				style="z-index: 9999;"></image>
			<image :src="userinfo.avatar" class="avatar" v-if="userinfo.avatar" style="z-index: 9999;"></image>
			<!-- #endif -->
			<view class="u-flex-c" v-if="!isLogin">
				<!-- #ifdef MP-WEIXIN -->
				<view class="loginPrompt">请先授权</view>
				<!-- #endif -->
				<!-- #ifdef MP-ALIPAY -->
				<view class="loginPrompt">请先登录</view>
				<!-- #endif -->
				<!-- #ifdef H5 -->
				<view class="loginPrompt">请先登录</view>
				<!-- #endif -->
			</view>
			<view class="u-flex-col userinfo" v-if="isLogin">
				<!-- #ifdef H5 -->
				<view class="nickName">{{userinfo.nickName || '暂无昵称'}}</view>
				<!-- #endif -->
				<!-- #ifdef MP-WEIXIN -->
				<view class="nickName">{{userinfo.nickName || '暂无昵称'}}</view>
				<!-- <open-data type="userNickName" class="nickName"></open-data> -->
				<!-- #endif -->
				<view class="u-flex-r u-col-center idBox" style="margin: 20rpx 0 0 20rpx;">
					<view class="user_id">ID:{{userinfo.id || ''}}</view>
					<image class="copyBotton" @click="copyBotton" src="../../static/image/my/copy.png"></image>
				</view>
			</view>
			<view class="myCarAndCoupon u-rela u-flex-r u-row-evenly u-col-center">
				<view class="car u-flex-c u-row-center" @click="intoMyCar">
					<view class="num">{{isLogin?carList:'-'}}</view>
					<view class="text">我的爱车</view>
				</view>
				<view class="car u-flex-c u-row-center" @click="intoParkingCard">
					<view class="num">{{isLogin?parkingCardNum:'-'}}</view>
					<view class="text">我的停车卡</view>
				</view>
				<view class="car u-flex-c u-row-center" @click="intoMyCoupon">
					<view class="num">{{isLogin?coupon:'-'}}</view>
					<view class="text">优惠券</view>
				</view>
			</view>
			<view style="background-color: #fff;margin-top: 48rpx;">
				<view @click='intoParkingRecord' class='u-flex-r u-row-between u-col-center option'>
					<view class="u-flex">
						<image src="../../static/image/my/ParkingRecord.png" style="width: 48rpx;height: 48rpx;">
						</image>
						<view class='normal'>停车记录</view>
					</view>
					<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
				</view>
				<view style="width: 642rpx;height: 1rpx;margin-left: 108rpx;background: #EAEAEA;"></view>
				<view @click='intoChargingRecord' class='u-flex-r u-row-between u-col-center option'>
					<view class="u-flex">
						<image src="../../static/image/my/ParkingRecord.png" style="width: 48rpx;height: 48rpx;">
						</image>
						<view class='normal'>充电记录</view>
					</view>
					<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
				</view>
				<view style="width: 642rpx;height: 1rpx;margin-left: 108rpx;background: #EAEAEA;"></view>
				<view @click='intoMybill' class='u-flex-r u-row-between u-col-center option'>
					<view class="u-flex">
						<image src="../../static/image/my/myBill.png" style="width: 48rpx;height: 48rpx;">
						</image>
						<view class='normal'>我的账单</view>
					</view>
					<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
				</view>
				<view style="width: 642rpx;height: 1rpx;margin-left: 108rpx;background: #EAEAEA;"></view>
				<!-- <view @click='intoMyWallet' class='u-flex-r u-row-between u-col-center option'>
					<view class="u-flex">
						<image src="../../static/image/my/wallet.png" style="width: 48rpx;height: 48rpx;">
						</image>
						<view class='normal'>我的钱包</view>
					</view>
					<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
				</view> -->
			</view>
			<view style="background-color: #fff;margin-top: 24rpx;">
				<view @click='intoReplacePay' class='u-flex-r u-row-between u-col-center option'>
					<view class="u-flex">
						<image src="../../static/image/my/times.png" style="width: 48rpx;height: 48rpx;">
						</image>
						<view class='normal'>临停缴费</view>
					</view>
					<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
				</view>
				<view style="width: 642rpx;height: 1rpx;margin-left: 108rpx;background: #EAEAEA;"></view>
				<view @click='onMaintain' class='u-flex-r u-row-between u-col-center option'
					v-if="userinfo.cfRolesflagKey == 'toll_collector'">
					<view class="u-flex">
						<image src="../../static/image/my/weihu.png" style="width: 48rpx;height: 48rpx;">
						</image>
						<view class='normal'>工作人员维护</view>
					</view>
					<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
				</view>
			</view>


			<!-- v-if="userinfo.cfRolesflagKey == 'merchant'" -->
			<view @click='intoShopkeeper' class='u-flex-r u-row-between u-col-center option' style="margin-top: 24rpx;" v-if="userinfo.cfRolesflagKey == 'merchant'">
				<view class="u-flex">
					<image src="../../static/image/my/wallet@2x.png" style="width: 48rpx;height: 48rpx;">
					</image>
					<view class='normal'>购买&分发停车券</view>
				</view>
				<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
			</view>
			<!-- v-if="userinfo.cfRolesflagKey == 'staff'" -->
			<view @click='intoShopAssistant' class='u-flex-r u-row-between u-col-center option'
				style="margin-top: 24rpx;" v-if="userinfo.cfRolesflagKey == 'staff'">
				<view class="u-flex">
					<image src="../../static/image/my/wallet@2x.png" style="width: 48rpx;height: 48rpx;">
					</image>
					<view class='normal'>赠送停车券</view>
				</view>
				<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
			</view>




			<view @click='intoEmergencyAccess' class='u-flex-r u-row-between u-col-center option'
				style="margin-top: 24rpx;" v-if="userinfo.cfRolesflagKey == 'toll_collector'">
				<view class="u-flex">
					<image src="../../static/image/my/control@2x.png" style="width: 48rpx;height: 48rpx;">
					</image>
					<view class='normal'>应急出入闸</view>
				</view>
				<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
			</view>

			<view style="background-color: #fff;margin-top: 24rpx;margin-bottom: 100rpx; ">
				<!-- #ifdef MP-WEIXIN -->
				<button open-type="contact" class='u-flex-r u-row-between u-col-center option' style="padding: 0;">
					<view class='u-flex-r u-row-between u-col-center option'>
						<view class="u-flex">
							<image src="../../static/image/my/feedback.png" style="width: 48rpx;height: 48rpx;">
							</image>
							<view class='normal'>客服反馈
							</view>
						</view>
						<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
					</view>
				</button>
				<!-- #endif -->
				<view style="width: 642rpx;height: 1rpx;margin-left: 108rpx;background: #EAEAEA;"></view>
				<view @click='intoSet' class='u-flex-r u-row-between u-col-center option' style="border-bottom: 0;">
					<view class="u-flex">
						<image src="../../static/image/my/setup.png" style="width: 48rpx;height: 48rpx;">
						</image>
						<view class='normal'>设置</view>
					</view>
					<image style='width:24rpx;height:48rpx;' src='../../static/image/my/left2x.png'></image>
				</view>

			</view>


		</view>


		<!-- #ifdef MP-WEIXIN -->
		<button v-if="!isLogin" class='free-btn-bordernone' @click="wxLogin"
			style='width:100%;height:100%;top:0;left:0;z-index:99999;position: fixed;'>
		</button>
		<!-- #endif -->

		<!-- #ifdef MP-ALIPAY -->
		<button v-if="!isLogin" class='free-btn-bordernone' @click="aliLogin"
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
	import bindPhonePopup from '../../components/bindPhonePopup/index.vue'
	export default {
		components: {
			bindPhonePopup
		},
		data() {
			return {
				phoneShow: false,
				carList: 0,
				coupon: 0,
				isLogin: false,
				userinfo: {},
				parkingCardNum: 0,
				code: ''
			}
		},
		onShow() {
			var wxauthed = uni.getStorageSync('wxauthed')
			// #ifdef MP-WEIXIN
			this.getCode()
			// #endif
			if (wxauthed) {
				this.getUserData()
				this.getCoupon()
				this.getCar()
				this.parkingCard()
				this.isLogin = true
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
								uni.setStorageSync('user', loginRes.data)
								uni.setStorageSync('token', loginRes.token.jwt_token)
								uni.setStorageSync('wxauthed', true)
								this.$u.vuex('vuex_token', loginRes.token.jwt_token)
								this.$u.vuex('vuex_user', loginRes.data)
								this.token = uni.getStorageSync('token');
								this.getCoupon()
								this.getCar()
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
			async parkingCard() {
				var {
					code,
					data
				} = await this.$u.api.getMyCarParkPackageByCondition({
					page: 1,
					size: 999
				})
				if (code === 10002) {
					this.parkingCardNum = data.length
				} else {
					this.parkingCardNum = 0
				}
			},
			closePhoneShow() {
				this.phoneShow = false
			},
			async getUserData() {
				var res = await this.$u.api.findById({
					uid: uni.getStorageSync('user') ? uni.getStorageSync('user').id : '',
					simpleQuery: 'yes'
				})
				console.log('用户信息', res);
				if (res.code == 10002) {
					console.log(res.data.cfRoles.length);
					this.userinfo = res.data
					if (res.data.cfRoles.length > 0) {
						this.userinfo.cfRolesflagKey = res.data.cfRoles[0].flagKey
					} else {
						this.userinfo.cfRolesflagKey = ''
					}
				}
			},
			intoMyWallet() {
				uni.navigateTo({
					url: 'myWallet/myWallet'
				})
			},
			intoChargingRecord() {
				uni.navigateTo({
					url: '../../chargeModule/pages/chargingRecord/index'
				})
			},
			onMaintain() {
				uni.showActionSheet({
					itemList: ['应急出闸', '车辆信息查询'],
					success: (res) => {
						console.log(res);
						if (res.tapIndex == 0) {
							uni.navigateTo({
								url: '../../couponModule/pages/my/emergencyAccess/index'
							})
						} else {
							uni.navigateTo({
								url: 'checkLicensePlate/enter'
							})
						}
					}
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
							uni.setStorageSync('user', loginRes.data)
							uni.setStorageSync('token', loginRes.token.jwt_token)
							uni.setStorageSync('wxauthed', true)
							this.$u.vuex('vuex_token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_user', loginRes.data)
							this.token = uni.getStorageSync('token');
							this.getCoupon()
							this.getCar()
						} else {
							this.$u.toast('登陆出错')
						}
					},
					fail(error) {
						console.log('获取用户信息失败',error)
					}
				})
			},

			onXiaoxi1() {
				uni.requestSubscribeMessage({
					tmplIds: ['5JtK4ih7dJAYGev_zQTIH9XvTRo-jxYA8ihLI6_kxeA'],
					success(res) {
						console.log(res);
					}
				})
			},
			onXiaoxi2() {
				uni.requestSubscribeMessage({
					tmplIds: ['nTyBgt-bUc-e8mQHcso-UfEyeojiPqYRIsKOM-oyL9E'],
					success(res) {}
				})
			},
			onXiaoxi3() {
				uni.requestSubscribeMessage({
					tmplIds: ['rQqRtJcdU9GtXDJPSMFL7kd98Q0hndewkEH70gk_ptI'],
					success(res) {}
				})
			},
			intoEmergencyAccess() {
				uni.navigateTo({
					url: '../../couponModule/pages/my/emergencyAccess/index'
				})
			},
			intoShopkeeper() {
				uni.navigateTo({
					url: '../../couponModule/pages/my/shopkeeper/index'
				})
			},
			// 复制id
			copyBotton() {
				uni.setClipboardData({
					data: this.userinfo.id
				})
			},
			intoSet() {
				uni.navigateTo({
					url: 'set/index'
				})
			},
			intoShopAssistant() {
				uni.navigateTo({
					url: '../../couponModule/pages/my/shopAssistant/index'
				})
			},
			intoMyCar() {
				uni.navigateTo({
					url: 'myCar/myCar'
				})
			},

			intoMyCoupon() {
				uni.navigateTo({
					url: 'myCoupon/myCoupon'
				})
			},

			intoReplacePay() {
				uni.navigateTo({
					url: 'replacePay/replacePay'
				})
			},

			intoFeedback() {
				uni.navigateTo({
					url: '/pages/my/feedback'
				})
			},

			intoParkingRecord() {
				uni.navigateTo({
					url: '/pages/my/myParkingRecord/myParkingRecord'
				})
			},
			intoMybill() {
				uni.navigateTo({
					url: 'myBill/index'
				})
			},
			intoQrCode() {
				uni.scanCode({
					success: res => {
						console.log(res);
						uni.navigateTo({
							url: 'myCoupon/couponDetail?info=' + res.result + '&type=qr'
						})
					}
				})
			},
			// 获取优惠券
			async getCoupon() {
				var res = await this.$u.api.getMyConpon({
					status: 1,
					page: 1,
					size: 100,
					minExpireTime: new Date().getTime()
				})
				console.log('获取优惠券', res)
				if (res.code == 10002) {
					this.coupon = res.data.length
				}
			},
			intoParkingCard() {
				uni.navigateTo({
					url: 'parkingCard/index',
					success: res => {},
					fail: () => {},
					complete: () => {}
				});
			},
			async getCar() {
				var res = await this.$u.api.getMyCarsByCondition({
					page: 1,
					size: 100
				})
				console.log(res)
				if (res.code == 10002) {
					this.carList = res.data.length
				}
			},

		}
	}
</script>

<style lang="scss">
	button::after {
		border: none;
	}

	page {
		background: #F8F9FB;
	}

	.mainBox {
		width: 750rpx;
		position: absolute;
		top: 150rpx;

		.avatar {
			width: 200rpx;
			height: 200rpx;
			border: 12rpx solid #FFFFFF;
			border-radius: 50%;
			z-index: 2;
			box-sizing: border-box;
			overflow: hidden;
		}

		.userinfo {
			z-index: 1;

			.nickName {
				width: 406rpx;
				height: 40rpx;
				font-size: 40rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #323233;
				line-height: 40rpx;
				text-align: center;
				margin-top: 16rpx;
			}

			.idBox {
				margin-top: 10rpx;
				height: 20rpx;

				.user_id {
					max-width: 306rpx;
					height: 24rpx;
					font-size: 24rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #323233;
					line-height: 24rpx;
				}

				.copyBotton {
					width: 52rpx;
					height: 26rpx;
					margin-left: 8rpx;
				}
			}
		}
	}

	.bg_image {
		width: 750rpx;
		height: 378rpx;
		left: 0;
		top: 0;
	}

	.myInfo {
		z-index: 1;
		height: 208rpx;
		position: relative;

		.avatar {
			width: 136rpx;
			height: 136rpx;
			box-shadow: 0rpx 4rpx 8rpx 0rpx rgba(0, 0, 0, 0.14);
			// border: 4rpx solid #FFFFFF;
			box-sizing: border-box;
			border-radius: 50%;
			margin: 72rpx 0 0 72rpx;
		}

		.flex-c {
			margin: 72rpx 0 0 36rpx;

			.userName {
				width: 244rpx;
				height: 40rpx;
				font-size: 40rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #FFFFFF;
				line-height: 40rpx;

			}

			.userId {
				height: 20rpx;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #FFFFFF;
				line-height: 20rpx;
				margin-top: 20rpx;
			}

		}
	}

	.myCarAndCoupon {
		width: 670rpx;
		height: 154rpx;
		background: #FFFFFF;
		border-radius: 14rpx;
		margin-top: 40rpx;
		box-shadow: 0rpx 16rpx 22rpx 0rpx rgba(10, 36, 99, 0.07);

		.f {
			width: 2rpx;
			height: 38rpx;
			background: #D8D8D8;
			border-radius: 2rpx;
			border: 1rpx solid rgba(20, 111, 209, 0.15);
		}

		.car {
			.num {
				text-align: center;
				width: 222rpx;
				height: 36rpx;
				font-size: 44rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 36rpx;
			}

			.text {
				text-align: center;
				width: 228rpx;
				height: 28rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.5);
				line-height: 28rpx;
				margin-top: 14rpx;
			}
		}
	}

	.mt24 {
		margin-top: 23rpx;
	}

	.option {
		width: 750rpx;
		height: 112rpx;
		// border-bottom: 1rpx solid #eaeaea;
		padding: 0 40rpx;
		background: #fff;
		line-height: 100rpx;
	}

	.loginPrompt {
		width: 164rpx;
		height: 40rpx;
		font-size: 40rpx;
		font-family: PingFangSC-Semibold, PingFang SC;
		font-weight: 600;
		// color: #FFFFFF;
		line-height: 40rpx;
		margin: 20rpx 0 0 20rpx;
	}

	.headerBg {
		position: absolute;
		z-index: 1;
		width: 750rpx;
		height: 248rpx;
		background-color: #488cf5;
		background-image: url(../../static/image/my/LOGO.png);
		background-size: 160rpx 210rpx;
		background-repeat: no-repeat;
		background-attachment: fixed;
		background-position-x: 520rpx;
		background-position-y: 38rpx;
	}

	.normal {
		height: 48rpx;
		font-size: 34rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #323233;
		line-height: 48rpx;
		margin-left: 20rpx;
	}
</style>