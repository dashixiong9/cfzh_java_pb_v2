<template>
	<view>
		<view style="width: 750rpx;height: 20rpx;background-color: #f7f7f7;"></view>

		<view class="inpYes" @click="onInputList">
			<view class="li"
				:style="plateNo.length > 0 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[0] || ''}}</view>
			<view class="li"
				:style="plateNo.length > 1 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[1] || ''}}</view>
			<view class="li"
				:style="plateNo.length > 2 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[2] || ''}}</view>
			<view class="li"
				:style="plateNo.length > 3 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[3] || ''}}</view>
			<view class="li"
				:style="plateNo.length > 4 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[4] || ''}}</view>
			<view class="li"
				:style="plateNo.length > 5 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[5] || ''}}</view>
			<view class="li"
				:style="plateNo.length > 6 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[6] || ''}}</view>
			<view class="li"
				:style="plateNo.length < 8 ? 'font-size: 20rpx;font-family: PingFangSC-Regular, PingFang SC;font-weight: 400;color: #A4A4A4;border: 2rpx solid #e5e5e5;' : 'border: 2rpx solid #488cf5;' ">
				{{plateNoArr.length > 7 ? plateNoArr[7] : '新能源'}}</view>
		</view>

		<view class="tips">请输入代缴停车费车牌号</view>

		<view style="margin-bottom: 40rpx;" :class="plateNo.length>0 ? 'confirm_active confirm' : 'confirm' "
			@click="intoReplacePayOrder">立即支付</view>


		<!-- #ifdef MP-WEIXIN -->
		<ad unit-id="adunit-1e8b54cf8ddab247"></ad>
		<!-- #endif -->

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



		<plate-input :plate="plateNo" @export="setPlate" @close="plateShow=false" v-if="plateShow" />
		<!-- <InputCar ref='inputCar' @plateNoChange='InputCarChange' @deleteOne='InputCarDelete'></InputCar> -->
	</view>
</template>

<script>
	import {
		wxAppid,
		aliAppid
	} from '../../../common/config.js'
	import InputCar from '@/components/woke-inputCar/woke-inputCar.vue'
	import plateInput from '@/components/uni-plate-input/uni-plate-input.vue'
	export default {
		components: {
			InputCar,
			plateInput
		},
		data() {
			return {
				plateShow: false,
				plateNo: '',
				isLogin: false,
				code: ''
			}
		},
		computed: {
			plateNoArr() {
				return this.plateNo.split('')
			}
		},
		onShow() {
			var wxauthed = uni.getStorageSync('wxauthed')
			// #ifdef MP-WEIXIN
			this.getCode()
			// #endif
			if (wxauthed) {
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
							var loginRes = await this.$u.api.getAliMinBaseUserInfoByCode({
								auth_code: res.authCode,
								appid: aliAppid
							})
							if (loginRes.code === 10002) {
								this.isLogin = true
								uni.setStorageSync('user', loginRes.data)
								uni.setStorageSync('token', loginRes.token.jwt_token)
								uni.setStorageSync('wxauthed', true)
								this.$u.vuex('vuex_token', loginRes.token.jwt_token)
								this.$u.vuex('vuex_user', loginRes.data)
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
							this.isLogin = true
							uni.setStorageSync('user', loginRes.data)
							uni.setStorageSync('token', loginRes.token.jwt_token)
							uni.setStorageSync('wxauthed', true)
							this.$u.vuex('vuex_token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_user', loginRes.data)
						} else {
							this.$u.toast('登陆出错')
						}
					}
				})
			},
			getCode() {
				wx.login({
					success: (res) => {
						console.log(res);
						this.code = res.code
					}
				})
			},
			onInputList() {
				// this.$refs.inputCar.open()
				this.plateShow = true
			},
			// 键盘输入事件
			InputCarChange(e) {
				console.log(123, e);
				this.plateNo += e
			},
			// 键盘删除事件
			InputCarDelete() {
				this.plateNo = this.plateNo.substr(0, this.plateNo.length - 1);
			},
			// 按键被点击(点击退格键不会触发此事件)
			valChange(val) {
				if (this.plateNo.length > 7) {
					this.$u.toast('超出规定字符')
					return
				}
				// 将每次按键的值拼接到value变量中，注意+=写法
				this.plateNo += val;
			},
			// 退格键被点击
			backspace() {
				// 删除value的最后一个字符
				if (this.plateNo.length) this.plateNo = this.plateNo.substr(0, this.plateNo.length - 1);
				console.log(this.value);
			},
			setPlate(e) {
				console.log(e)
				if (e.length >= 7) this.plateNo = e
				this.plateShow = false
			},
			intoReplacePayOrder() {
				uni.navigateTo({
					url: './replacePayOrder?plateNo=' + this.plateNo
				})
			}
		}
	}
</script>

<style lang="less">
	.confirm {
		width: 710rpx;
		height: 88rpx;
		background-color: #488cf5;
		opacity: .4;
		border-radius: 12rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 88rpx;
		text-align: center;
		margin: 64rpx 0 0 20rpx;
	}

	.confirm_active {
		background-color: #488cf5;
		color: #FFFFFF;
		opacity: 1;
	}

	.tips {
		width: 432rpx;
		height: 32rpx;
		font-size: 28rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.7);
		line-height: 32rpx;
		margin: 32rpx 0 0 32rpx;
	}

	.inpYes {
		width: 90vw;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		margin: 84rpx 0 0 32rpx;

		>.li {
			width: 10vw;
			height: 11vw;
			text-align: center;
			line-height: 11vw;
			color: #323233;
			font-size: 48upx;
			font-weight: 600;
			border: 2rpx solid #488cf5;
			border-radius: 10upx;
		}
	}

	.inp {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		margin: 84rpx 0 0 32rpx;
		width: 90vw;

		>.li {

			width: 11vw;
			height: 11vw;
			text-align: center;
			line-height: 11vw;
			color: #0A2463;
			font-size: 48upx;
			font-weight: 600;
			border: 2rpx solid #979797;
			border-radius: 10upx;
		}
	}
</style>