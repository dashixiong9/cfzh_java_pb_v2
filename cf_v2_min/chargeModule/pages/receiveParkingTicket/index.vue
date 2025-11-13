<template>
	<view>
		<view style="width: 750rpx;height: 18rpx;background-color: #F8F9FB;"></view>
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
		<view :class="plateNo.length > 6 ? 'receive_ac' : 'receive'" @click="onReceive">领取优惠券</view>
		<InputCar @submit="inputCarSubmit" ref='inputCar' @plateNoChange='InputCarChange' @deleteOne='InputCarDelete'></InputCar>
	</view> 
</template>

<script>
	import InputCar from '@/components/woke-inputCar/woke-inputCar.vue'
	export default {
		components: {
			InputCar
		},
		data(){
			return {
				plateNo:''
			}
		},
		computed:{
			plateNoArr(){
				return this.plateNo.split('')
			}
		},
		methods:{
			// 领取
			onReceive(){
				
			},
			onInputList(){
				this.$refs.inputCar.open()
			},
			// 键盘输入事件
			InputCarChange(e){
				console.log(123,e);
				this.plateNo += e
			},
			// 键盘删除事件
			InputCarDelete(){
				this.plateNo =  this.plateNo.substr(0, this.plateNo.length - 1);
			},
			inputCarSubmit(e){
				console.log(e);
			},
		}
	}
</script>

<style lang="scss">
	page{
		width: 375px;
		height: 594px;
		background: #FFFFFF;
	}
	.receive{
		width: 335px;
		height: 50px;
		background: #488CF5;
		border-radius: 8px;
		opacity: 0.5;
		margin: 64px 0 0 16px;
		font-size: 20px;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 50px;
		text-align: center;
	}
	.receive_ac{
		@extend .receive;
		opacity: 1;
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
</style>
