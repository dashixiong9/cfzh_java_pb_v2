<template>
	<view>
		<view class="box u-flex-c u-rela">
			<view class="amount" v-if="couponDetail.source_type == 1">{{couponDetail.denomination}}次</view>
			<view class="amount" v-if="couponDetail.source_type == 2">{{couponDetail.denomination}}元</view>
			<view class="amount" v-if="couponDetail.source_type == 3">{{couponDetail.denomination}}小时</view>

			<view class="name">停车场停车优惠券</view>
			<view class="timeLimit">有效期限：{{(couponDetail.coupon_validity_period / 3600000).toFixed(2)}}小时</view>
			<view class="modeOfUse">使用方式：自动抵扣</view>
			<view class="u-flex-r">
				<view class="Rules" style="width:140rpx;">使用范围：</view>
				<view class="Rules" style="width:466rpx;margin-left: 0;">{{couponDetail.available_stores}}</view>
			</view>
			<!-- <view class="Range">使用范围：{{rangeText}}</view> -->
			<view class="u-flex-r">
				<view class="Rules" style="width:140rpx;">使用规则：</view>
				<text class="Rules" style="width:466rpx;margin-left: 0;">1.优惠券仅限在有效期内使用，过期则无法使用；
					2.少量特殊商品不能使用优惠券（有些优惠券标注全场通用则可以使用）；
					3.一张订单只能使用一张优惠券，优惠券不可合并；</text>
			</view>
			<!-- 	<view class="button1" v-if="couponDetail.status == 0">已使用</view>
			<view class="button" v-if="couponDetail.status == 1">已领取</view>
			<view class="button1" v-if="couponDetail.status == 2">已过期</view>
			 -->
			<view class="button" style="opacity: 1;" @click="plateShow = true">立即领取</view>
		</view>

		<!-- #ifdef MP-WEIXIN -->
		<button v-if="!isLogin" class='free-btn-bordernone' @click="wxLogin"
			style='width:100%;height:100%;top:0;left:0;z-index:99999;position: fixed;'>
		</button>
		<!-- #endif -->
		<plate-input v-if="plateShow" :plate="plateNo" @export="setPlate" @close="plateShow=false"></plate-input>
	</view>
</template>

<script>
	import {
		wxAppid
	} from '../../../common/config.js'
	import plateInput from '@/components/uni-plate-input/uni-plate-input.vue'
	export default {
		data() {
			return {
				code: '',
				isLogin: false,
				plateShow: false,
				plateNo: '',
				couponDetail: {
					effective_duration: '',
					source_type: '',
					qr_code_id: '',
					available_stores: '',
					denomination: '',
					coupon_type_id: '',
					coupon_validity_period: ''
				}
			}
		},
		components: {
			plateInput
		},
		onLoad(e) {
			var wxauthed = uni.getStorageSync('token')
			this.getCode()


			if (wxauthed) {
				this.isLogin = true
			} else {
				this.isLogin = false
			}

			if (e.q) {
				const url = decodeURIComponent(e.q)
				const removedUrl = url.replace('https://parking.fksg.net/getCoupons/', '');
				this.couponDetail = this.convertParamsToObject(removedUrl)
			} else {
				const coupunParams = JSON.parse(decodeURIComponent(e.coupunParams))
				this.couponDetail = coupunParams
			}

		},
		methods: {
			convertParamsToObject(paramsStr) {
				const paramsArr = paramsStr.split('&');
				const paramsObj = paramsArr.reduce((obj, param) => {
					const [key, value] = param.split('=');
					obj[key] = decodeURIComponent(value);
					return obj;
				}, {});
				return paramsObj;
			},

			getCode() {
				wx.login({
					success: (res) => {
						this.code = res.code
					}
				})
			},
			setPlate(plate) {
				this.plateShow = false
				this.plateNo = plate
				uni.showModal({
					title: '提示',
					content: '确定领取到 ' + plate + ' 吗？',
					success: ({
						confirm
					}) => {
						if (confirm) {
							this.getCoupon(plate)
						}
					}
				})
			},
			async getCoupon(plate) {
				var res = await this.$u.api.getCouponByQrCode({
					qrCodeId: this.couponDetail.qr_code_id,
					numberPlate: plate,
					couponActivityCouponTypeId: this.couponDetail.coupon_type_id,
				})
				this.$u.toast(res.message)
				if (res.code == 10002) {
					setTimeout(function() {
						uni.reLaunch({
							url: '/pages/my/myCoupon/myCoupon'
						})
					}, 1500);
				}
			},
			wxLogin(e) {
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
							this.isLogin = true
							uni.setStorageSync('wxauthed', true)
							uni.setStorageSync('user', loginRes.data)
							uni.setStorageSync('token', loginRes.token.jwt_token)
						} else {
							this.$u.toast('登陆出错')
						}
					}
				})
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
	}

	.box {
		width: 686rpx;
		// height: 892rpx;
		background: #FFFFFF;
		margin: 32rpx;
		border-radius: 10rpx;

		.amount {
			height: 100rpx;
			font-size: 72rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #DEB19B;
			line-height: 100rpx;
			align-self: center;
			margin: 48rpx 0 20rpx 0;
		}

		.name {
			height: 50rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 50rpx;
			align-self: center;
			margin-bottom: 48rpx;
		}

		.timeLimit {
			width: 606rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 40rpx;
			margin-left: 40rpx;
			margin-bottom: 24rpx;
		}

		.Range {
			@extend .timeLimit;
		}

		.Rules {
			@extend .timeLimit;
		}

		.modeOfUse {
			@extend .timeLimit;
		}
	}

	.button {
		width: 558rpx;
		height: 88rpx;
		background: #DEB19B;
		border-radius: 12rpx;
		opacity: 0.5;
		font-size: 32rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 88rpx;
		bottom: 60rpx;
		left: 66rpx;
		text-align: center;
		margin: 114rpx 0 30rpx 66rpx;
	}

	.button1 {
		@extend .button;
		background: #A39492;
		border-radius: 12rpx;
		opacity: 0.4;
		bottom: 170rpx;
	}
</style>