<template>
	<view>
		<view class="inpYes" @click="plateShow = true">
			<!-- <view class="li" :style="plateNo.length>7?'width:10vw;':''" v-for="(item,index) in plateNo">{{item}}</view> -->
			<view class="li"
				:style="plateNo.length > 0 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[0] || ''}}
			</view>
			<view class="li"
				:style="plateNo.length > 1 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[1] || ''}}
			</view>
			<view class="li"
				:style="plateNo.length > 2 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[2] || ''}}
			</view>
			<view class="li"
				:style="plateNo.length > 3 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[3] || ''}}
			</view>
			<view class="li"
				:style="plateNo.length > 4 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[4] || ''}}
			</view>
			<view class="li"
				:style="plateNo.length > 5 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[5] || ''}}
			</view>
			<view class="li"
				:style="plateNo.length > 6 ? 'border: 2rpx solid #488cf5;' : 'border: 2rpx solid #e5e5e5;'">
				{{plateNoArr[6] || ''}}
			</view>
			<view class="li"
				:style="plateNo.length < 8 ? 'font-size: 20rpx;font-family: PingFangSC-Regular, PingFang SC;font-weight: 400;color: #A4A4A4;border: 2rpx solid #e5e5e5;' : 'border: 2rpx solid #488cf5;' ">
				{{plateNoArr.length > 7 ? plateNoArr[7] : '新能源'}}
			</view>
		</view>
		<view class="orderButton" @click="intoDetail">生成订单</view>
		<view class="back">重新拍摄</view>
		<u-keyboard mode="car" @change="valChange" @backspace="backspace" v-model="plateShow" :mask='false'
			:safe-area-inset-bottom='true'></u-keyboard>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				plateNo: '',
				plateShow: false
			}
		},
		computed: {
			plateNoArr() {
				return this.plateNo.split('')
			}
		},
		
		methods: {
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
			setPlate(plate) {
				console.log(plate)
				if (plate.length >= 7) this.plateNo = plate
				this.plateShow = false
			},
			intoDetail(){
				uni.navigateTo({
					url: 'emergencyOrderDetail'
				});
			}
		}
	}
</script>

<style lang="scss">
	.inpYes {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		width: 680rpx;
		margin: 82rpx 0 0 40rpx;

		>.li {
			width: 72rpx;
			height: 80rpx;
			text-align: center;
			line-height: 80rpx;
			color: #0A2463;
			font-size: 48upx;
			font-weight: 600;
			border-radius: 10upx;
		}
	}

	.orderButton {
		width: 670rpx;
		height: 100rpx;
		background: #488CF5;
		border-radius: 16rpx;
		font-size: 36rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 100rpx;
		text-align: center;
		margin: 128rpx 0 0 32rpx;
	}

	.back {
		@extend .orderButton;
		margin: 16rpx 0 0 32rpx;
		background: #fff;
		border: 2rpx solid #488CF5;
		color: #488CF5;
	}
</style>
