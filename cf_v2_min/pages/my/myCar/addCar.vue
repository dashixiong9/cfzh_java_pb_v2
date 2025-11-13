<template>
	<view>
		<view style="width: 750rpx;height: 16rpx;background-color: ;"></view>
		<view class="inpYes" @click="onInputList">
			<view class="li" :style="plateNo.length > 0 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">{{plateNoArr[0]}}</view>
			<view class="li" :style="plateNo.length > 1 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">{{plateNoArr[1]}}</view>
			<view class="li" :style="plateNo.length > 2 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">{{plateNoArr[2]}}</view>
			<view class="li" :style="plateNo.length > 3 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">{{plateNoArr[3]}}</view>
			<view class="li" :style="plateNo.length > 4 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">{{plateNoArr[4]}}</view>
			<view class="li" :style="plateNo.length > 5 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">{{plateNoArr[5]}}</view>
			<view class="li" :style="plateNo.length > 6 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">{{plateNoArr[6]}}</view>
			<view class="li" :style="plateNo.length < 8 ? 'font-size: 20rpx;font-family: PingFangSC-Regular, PingFang SC;font-weight: 400;color: #A4A4A4;border: 2rpx solid #e5e5e5;' : 'border: 2rpx solid #488cf5;' ">{{plateNoArr.length > 7 ? plateNoArr[7] : '新能源'}}</view>
		</view>
		<view class="buttonNo" style="opacity: 0.5" v-if="plateNo.length < 7">
			{{type == 'getCoupon' ? '立即领取' : '立即绑定'}}
		</view>
		<view class="buttonNo" v-else style="background-color: #488CF5;" @click="addCar">
			{{type == 'getCoupon' ? '立即领取' : '立即绑定'}}
		</view>
		<InputCar @submit="inputCarSubmit" ref='inputCar' @plateNoChange='InputCarChange' @deleteOne='InputCarDelete'></InputCar>
	</view>
</template>

<script>
	import InputCar from '@/components/woke-inputCar/woke-inputCar.vue'
	export default {
		components: {
			InputCar
		},
		data() {
			return {
				plateNo: '',
				plateShow: true,
				token: '',
				type: '',
				couponDetail: ''
			}
		},
		onLoad(e) {
			console.log(e);
			this.type = e.type
			if (e.type == "getCoupon") {
				uni.setNavigationBarTitle({
					title: '输入车牌领取优惠券'
				})
				this.couponDetail = JSON.parse(e.datas)
			}
			this.token = uni.getStorageSync("token");
			this.$refs.inputCar.open()
		},
		computed:{
			plateNoArr(){
				return this.plateNo.split('')
			}
		},
		methods: {
			// 键盘输入事件
			InputCarChange(e){
				this.plateNo += e
			}, 
			// 键盘删除事件
			InputCarDelete(){
				this.plateNo =  this.plateNo.substr(0, this.plateNo.length - 1);
			},
			inputCarSubmit(e){
				console.log(e);
			},
			onInputList(){
				this.$refs.inputCar.open()
			},
			// 按键被点击(点击退格键不会触发此事件)
			valChange(val) {
				if(this.plateNo.length > 7){
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
			setPlate(plate) {
				console.log(plate)
				if (plate.length >= 7) this.plateNo = plate
				this.plateShow = false
			},
			async addCar() {
				if(this.plateNo.length < 7) return
				if (this.type == 'getCoupon') {
					var getCouponRes = await this.$u.api.getCouponByQrCode({
						qrCodeId:this.couponDetail.qr_code_id,
						numberPlate:this.plateNo,
						couponActivityCouponTypeId: this.couponDetail.coupon_type_id,
					})
					if (getCouponRes.code == 10002) {
						this.$u.toast('领取成功')
						setTimeout(() => {
							uni.reLaunch({
								url:'/pages/my/myCoupon/myCoupon'
							})
						}, 1500)
					} else {
						this.$u.toast(getCouponRes.message)
					}
					return
				}
				uni.showLoading({
					title: '请稍等',
					mask: true
				})
				var res = await this.$u.api.addCar({
					numberPlate: this.plateNo
				})
				if (res.code == 10002) {
					uni.showModal({
						title: '提示',
						content: res.message,
						showCancel: false,
						success: function(res) {
							if (res.confirm) {
								uni.navigateBack()
							}
						}
					});
				} else {
					uni.showToast({
						title: res.message,
						icon: "none"
					})
				}
			},
		}
	}
</script>

<style lang="less">
	page {
		background-color: #FFFFFF;
		// padding-top: 90upx;
	}

	.back {
		>image {
			width: 48upx;
			height: 48upx;
		}
	}

	.headline {
		margin-top: 60upx;

		.title {
			color: #0A2463;
			font-size: 48upx;
			font-weight: 600;
		}

		.hint {
			color: rgba(10, 36, 99, 0.5);
			font-size: 24upx;
			margin-top: 10upx;
		}

	}

	.inp {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		margin-top: 90upx;
		margin-bottom: 160upx;
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

	.inpYes {
		width: 90vw;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		margin-top: 90upx;
		margin-bottom: 160upx;
		margin-left: 40rpx;
		>.li {
			width: 10vw;
			height: 11vw;
			text-align: center;
			line-height: 11vw;
			color: #323233;
			font-size: 48upx;
			font-weight: 600;
			
			border-radius: 10upx;
		}


	}

	.buttonNo {
		width: 90vw;
		margin: 0upx 0vw;
		height: 100upx;
		line-height: 100upx;
		border-radius: 10upx;
		text-align: center;
		color: #FFFFFF;
		background: #488cf5;
		font-size: 36rpx;
		margin-left: 40rpx;
	}
</style>
