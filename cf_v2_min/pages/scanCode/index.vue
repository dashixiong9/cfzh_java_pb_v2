<template>
	<view>
		<image src="http://file.cfeng.wang/scancodeBg.png"
			style="width: 710rpx;height: 712rpx;margin: 118rpx 0 0 40rpx;" ></image>
		<view class="onButton" @click="openInAndOut">{{direction == 'in' ? '开闸入场' : '开闸离场'}}</view>

		<!-- #ifdef MP-WEIXIN -->
		<button v-if="!isLogin" @click="wxLogin" class='free-btn-bordernone'
			style='width:100%;height:100%;top:0;left:0;z-index:99999;position: fixed;'>
		</button>
		<!-- #endif -->

	</view>
</template>

<script>
	import {wxAppid} from '../../common/config.js'
	export default {
		data() {
			return {
				isLogin: false,
				checkPointId: '',
				direction: '',
				code: ''
			}
		},
		onLoad(e) {
			console.log(e);
			var wxauthed = uni.getStorageSync('wxauthed')
			if (e.scene) {
				var sceneString = decodeURIComponent(e.scene)
				this.checkPointId = this.getUrlParam(sceneString, 'cpid')
				this.direction = this.getUrlParam(sceneString, 'dr')
			}
			if (wxauthed) {
				this.isLogin = true
			} else {
				this.isLogin = false
			}
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
			// 扫码开关闸
			async openInAndOut() {
				var res = await this.$u.api.applyForAccess({
					direction: this.direction,
					checkPointId: this.checkPointId,
					type: 1
				})
				console.log(res);
				if (res.code != 10002) {
					this.$u.toast(res.message)
					return
				} else {
					this.$u.toast(res.message)
					if (!res.data.payTime && this.direction == 'out') {
						uni.navigateTo({
							url: '../my/replacePay/pay?id=' + res.data.id
						});
					}
				}
			},
			getUserinfo(e) {
				if (e.detail.errMsg == 'getUserInfo:ok') {
					this.isLogin = true
					uni.setStorageSync('wxauthed', true)
					this.wxLogin()
				}
			},
			getUrlParam(url, name) {
				var vars = url.split("&");
				for (var i = 0; i < vars.length; i++) {
					var pair = vars[i].split("=");
					if (pair[0] == name) {
						return pair[1];
					}
				}
				return (false);
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
							appid:wxAppid
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
							this.userinfo = loginRes.data;
							uni.setStorageSync('wxauthed', true)
							uni.setStorageSync('user', loginRes.data)
							uni.setStorageSync('token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_user', loginRes.data)
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
		background: #FAFCFF;
	}

	.onButton {
		width: 590rpx;
		height: 100rpx;
		background: #488CF5;
		border-radius: 200rpx;
		margin: 156rpx 0 0 80rpx;
		font-size: 36rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 100rpx;
		text-align: center;
	}
</style>
