<template>
	<u-popup v-model="phoneShow" mode="bottom" border-radius="14" width="750rpx" height="400rpx">
		<view class="orderInfo u-flex-col u-col-center" style="height: 100%;">
			<view style="font-size: 40rpx;margin-top: 100rpx;">绑定手机号</view>
			<!-- #ifdef MP -->
			<button class="intoOrder" style="margin-top: 54rpx;" open-type="getPhoneNumber"
				@getphonenumber='getPhoneNumber'>一键绑定手机号</button>
			<!-- #endif -->
		</view>
	</u-popup>
</template>

<script>
	import { wxAppid, aliAppid } from '@/common/config.js'
	export default {
		props: ['phoneShow'],
		methods: {
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
			},
			onGetAuthorize() {
				console.log(123);
			}
		}
	}
</script>

<style lang="less">
	.orderInfo {
		width: 750rpx;
		height: 100%;
		background: #FFFFFF;
		border-radius: 48rpx 48rpx 0rpx 0rpx;
		position: absolute;
		bottom: var(--window-bottom);
		// align-self: flex-end;
		background-image: url('http://file.cfeng.wang/bigLogo.png');
		background-size: 344rpx 432rpx;
		background-repeat: no-repeat;
		background-position-y: 94rpx;

		.parkingTime {
			width: 100%;
			height: 100%;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 34rpx;
			font-size: 40rpx;
			text-align: center;
			margin-top: 110rpx;
		}

		.intoOrder {
			width: 670rpx;
			height: 100rpx;
			background: #488CF5;
			border-radius: 16rpx;
			margin-top: 48rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #FFFFFF;
			line-height: 100rpx;
			text-align: center;
		}
	}
</style>
