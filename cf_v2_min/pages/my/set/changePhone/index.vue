<template>
	<view>
		<view class="inp u-flex-c">
			<view class="title">我的手机号</view>
			<view class="phone">{{phone?phone:'-'}}</view>
		</view>
		<!-- <view class="trip">注：昵称最多支持8个汉字</view> -->
		<view class="buttonNo" @click="phoneShow = true">
			{{phone?'换绑手机号':'绑定手机号'}}
		</view>


		<!-- <button class="buttonNo" open-type="getPhoneNumber" @getphonenumber='getPhoneNumber'>一键获取手机号</button> -->
		<bindPhonePopup :phoneShow='phoneShow' @closePhoneShow='closePhoneShow' />
	</view>
</template>

<script>
	import { wxAppid, aliAppid } from '@/common/config.js'
	import bindPhonePopup from '../../../../components/bindPhonePopup/index.vue'
	export default {
		data() {
			return {
				phone: '',
				phoneShow:false
			}
		},
		components: {
			bindPhonePopup
		},
		onShow() {
			this.phone = uni.getStorageSync('user').phone
		},
		methods: {
			closePhoneShow() {
				this.phoneShow = false
				setTimeout(() => {
					this.phone = uni.getStorageSync('user').phone
				}, 300);
			},
			clear() {
				this.nickname = ""
			},
			intoBind() {
				uni.navigateTo({
					url: 'bind'
				})
			},
			async getPhoneNumber(e) {
				// #ifdef MP-ALIPAY
				uni.getPhoneNumber({
					success: async (res) => {
						console.log(res);
						var {
							code,
							data
						} = await this.$u.api.getUserPhonenumberByCodeAndLoginType({
							code: res.response,
							loginType: 'ali_mp',
							appid: aliAppid
						})
						if (code === 10002) {
							uni.showToast({
								title: '绑定成功',
								icon: 'none'
							})
							uni.setStorageSync('user', data)
							this.$u.vuex('vuex_user', data)
							this.$emit('closePhoneShow')
						}
					}
				});
				// #endif
				// #ifdef MP-WEIXIN
				if (e.detail.code) {
					var {
						code,
						data,
						token
					} = await this.$u.api.getUserPhonenumberByCodeAndLoginType({
						code: e.detail.code,
						loginType: 'wx_mp',
						appid:wxAppid
					})
					if (code === 10002) {
						uni.showToast({
							title: '绑定成功',
							icon: 'none'
						})
						uni.setStorageSync('user', data)
						uni.setStorageSync('token', token.jwt_token)
						this.$u.vuex('vuex_user', data)
						this.$emit('closePhoneShow')
					}
				}
				// #endif
			}
		}
	}
</script>

<style lang="less">
	page {
		background-color: #f7f7f7;
	}

	.buttonNo {
		width: 90vw;
		height: 100upx;
		line-height: 100upx;
		border-radius: 10upx;
		text-align: center;
		color: #FFFFFF;
		background: #488cf5;
		margin-top: 68rpx;
		margin-left: 32rpx;
	}

	.trip {
		width: 280rpx;
		height: 34rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.5);
		line-height: 34rpx;
		margin: 32rpx 0 0 24rpx;
	}

	.inp {
		width: 750rpx;
		height: 248rpx;
		background: #FFFFFF;
		margin-top: 32rpx;
		padding-left: 24rpx;
		box-sizing: border-box;

		.title {
			width: 100%;
			text-align: center;
			height: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 44rpx;
			margin-top: 64rpx;
			opacity: .6;
		}

		.phone {
			width: 100%;
			text-align: center;
			height: 66rpx;
			font-size: 48rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 66rpx;
			margin-top: 12rpx;
		}
	}
</style>
