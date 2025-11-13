<template>
	<view>
		<input class="phoneInput" placeholder="输入手机号" placeholder-class="inpitPlaceholder" @input="inputChange"
			maxlength="11">

		</input>
		<view class="u-flex-r u-col-center" style="background: #FFFFFF;">
			<input type="number" class="codeInput" placeholder="输入验证码" @input="codeChange" maxlength="6"
				placeholder-class="inpitPlaceholder" />
			<view class="line"></view>
			<view class="codeText" @click="showCaptcha" :style="active?'color:#488cf5;':''">
				{{times < 60 ? times + '重新获取':'获取验证码'}}
			</view>
		</view>
		<view class="send" @click="send" :style="bindActive?'opacity: 1;':''">绑定</view>
		<!-- #ifdef MP-WEIXIN -->
		<t-captcha ref='captcha' id='captcha' :app-id="captcha_Appid" @verify="handlerVerify" @ready="handlerReady"
					@close="handlerClose" @error="handlerError" />
		<!-- #endif -->
		

	</view>
</template>

<script>
	import {captchaAppid} from '../../../../common/config.js'
	export default {
		data() {
			return {
				value: '',
				active: false,
				times: 60,
				code: '',
				bindActive: false,
				captcha_Appid:captchaAppid
			}
		},
		onLoad() {

		},
		methods: {
			handlerVerify: function(ev) {
				console.log(ev);
				// 如果使用了 mpvue，ev.detail 需要换成 ev.mp.detail
				if (ev.detail.ret === 0) {
					// 验证成功
					console.log('ticket:', ev.detail.ticket)
					this.onGetCode(ev.detail.ticket)
				} else {
					// 验证失败
					// 请不要在验证失败中调用refresh，验证码内部会进行相应处理
				}
			},
			// 验证码准备就绪
			handlerReady: function() {
				console.log('验证码准备就绪')
			},
			// 验证码弹框准备关闭
			handlerClose: function() {
				console.log('验证码弹框准备关闭')
			},
			// 验证码出错
			handlerError: function(ev) {
				console.log(ev.detail.errMsg)
			},

			codeChange(e) {
				this.code = e.detail.value
				if (this.code.length == 6 && this.$u.test.mobile(this.value)) {
					this.bindActive = true
				} else {
					this.bindActive = false
				}
			},
			inputChange(e) {
				this.value = e.detail.value
				if (this.$u.test.mobile(this.value)) {
					this.active = true
				} else {
					this.active = false
				}
			},
			async send() {
				if (this.code.length != 6) {
					return
				}
				var res = await this.$u.api.bindPhone({
					phone: this.value,
					smsCode: this.code
				})
				console.log(res);
				if (res.code == 10002) {
					this.$u.toast('绑定成功')
					var user = uni.getStorageSync('user')
					user.phone = res.data.phone
					uni.setStorageSync('user', user)
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} else {
					this.$u.toast(res.message)
				}
			},
			showCaptcha(){
				if (!this.active && this.times == 60) {
					return
				}
				if(this.times < 60) return 
				this.$scope.selectComponent('#captcha').show()
			},
			async onGetCode(ticket) {
				// console.log(this..selectComponent("#captcha"));
				// 进行业务逻辑，若出现错误需重置验证码，执行以下方法
				// if (error) {
				// this.selectComponent('#captcha').refresh()
				// }
				var res = await this.$u.api.sendSms({
					phone: this.value,
					type: 2,
					ticket,
					platform:'wxmin'
				})
				if (res.code == 10002) {
					var time = setInterval(() => {
						this.times--
						if (!this.times) {
							clearInterval(time)
							this.times = 60
						}
					}, 1000)
				}else{
					this.$u.toast(res.message)
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
	}

	.phoneInput {
		width: 750rpx;
		height: 100rpx;
		background: #FFFFFF;
		margin-top: 32rpx;
		margin-bottom: 1rpx;
		padding-left: 24rpx;
		box-sizing: border-box;
	}

	.inpitPlaceholder {
		font-size: 32rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.3);
		line-height: 44rpx;
	}

	.codeInput {
		width: 496rpx;
		height: 100rpx;
		padding-left: 24rpx;
	}

	.line {
		width: 2rpx;
		height: 32rpx;
		background: #EAEAEA;
		border: 2rpx solid #EAEAEA;
	}

	.codeText {
		width: 160rpx;
		height: 44rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.3);
		line-height: 44rpx;
		text-align: center;
		flex-grow: 1;
	}

	.send {
		width: 686rpx;
		height: 100rpx;
		background: #488cf5;
		border-radius: 16rpx;
		opacity: 0.5;
		margin: 72rpx 0 0 32rpx;
		font-size: 36rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #FFFFFF;
		line-height: 100rpx;
		text-align: center;
	}
</style>
