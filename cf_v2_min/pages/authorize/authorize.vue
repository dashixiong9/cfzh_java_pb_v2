<template>
	<view>
		<image src="../../static/image/home/LOGO@2x.png" class="logo"></image>
		<div class="text1">您好，</div>
		<div class="text2">欢迎使用富康商业智慧停车场</div>
		<image src="../../static/image/home/BG@2x.png" class="bg"></image>
		<!-- #ifdef MP-WEIXIN -->
		<button @click="wxLogin" class="authorize">
			点击授权
		</button>
		<!-- #endif -->

		<!-- #ifdef MP-ALIPAY -->
		<button @click="aliLogin" class="authorize">
			点击授权
		</button>
		<!-- #endif -->
	</view>
</template>

<script>
	import {
		wxAppid,
		aliAppid
	} from '../../common/config.js'
	export default {
		data() {
			return {
				code: ''
			};
		},
		onShow() {
			// #ifdef MP-WEIXIN
			this.getCode()
			// #endif
		},
		methods: {
			getCode() {
				wx.login({
					success: (res) => {
						console.log(res);
						this.code = res.code
					}
				})
			},
			aliLogin() {
				uni.getAuthCode({
					scopes: ['auth_user'],
					success: async (res) => {
						if (res.authCode) {
							var loginRes = await this.$u.api.getAliMinBaseUserInfoByCode({
								auth_code: res.authCode,
								appid: aliAppid
							})
							if (loginRes.code === 10002) {
								uni.setStorageSync('wxauthed', true)
								uni.setStorageSync('user', loginRes.data)
								uni.setStorageSync('token', loginRes.token.jwt_token)
								uni.navigateBack({
									delta: 1
								})
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
							uni.setStorageSync('wxauthed', true)
							uni.setStorageSync('user', loginRes.data)
							uni.setStorageSync('token', loginRes.token.jwt_token)
							uni.navigateBack({
								delta: 1
							})
						} else {
							this.$u.toast('登陆出错')
						}
					}
				})





				// uni.getUserProfile({
				// 	desc: '保存用户数据',
				// 	success: async ({iv,encryptedData}) => {
				// 		uni.login({
				// 			success: async ({code}) => { 
				// 				var data = { 
				// 					iv,
				// 					code:this.code ? this.code : code,
				// 					loginType: 'mp',
				// 					encryptedData
				// 				}
				// 				this.code = ''
				// 				var loginRes = await this.$u.api.wxLogin(data)
				// 				console.log('登陆个人信息', loginRes)
				// 				if (loginRes.code == 10002) {
				// 					uni.setStorageSync('wxauthed', true)
				// 					uni.setStorageSync('user', loginRes.data)
				// 					uni.setStorageSync('token', loginRes.token.jwt_token)
				// 					uni.navigateBack({
				// 						delta: 1
				// 					})
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
		background: #488CF5;
	}

	.logo {
		width: 72rpx;
		height: 72rpx;
		margin: 208rpx 0 0 70rpx;
	}

	.text1 {
		width: 144rpx;
		height: 66rpx;
		font-size: 48rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 66rpx;
		margin: 6rpx 0 0 72rpx;
	}

	.text2 {
		width: 256rpx;
		height: 44rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 44rpx;
		margin: 8rpx 0 0 72rpx;
	}

	.bg {
		width: 510rpx;
		height: 266rpx;
		margin: 100rpx 0 0 120rpx;
	}

	.authorize {
		width: 590rpx;
		height: 100rpx;
		background: #FFFFFF;
		border-radius: 12rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #488CF5;
		line-height: 100rpx;
		margin: 166rpx 0 0 80rpx;
	}
</style>