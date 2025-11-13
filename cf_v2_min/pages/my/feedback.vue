<template>
	<view>
		<view style="width: 750rpx;height: 24rpx;background: #f7f7f7;"></view>
		<textarea v-model="inputValue" placeholder="尊敬的用户您好，若在使用小程序的过程中遇到任何问题，可以在此给我们留言。我们将及时的回复并解决您的问题。" placeholder-class="placeholder" class="input" />
		<view :class="inputValue.length>0?'submit submit_ac1':'submit submit_ac2'">留言</view>
	</view>
</template>

<script>
	export default {
		data(){
			return {
				inputValue:''
			}
		}
	}
</script>

<style lang="scss">
	.submit{
		width: 686rpx;
		height: 88rpx;
		
		border-radius: 12rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 88rpx;
		text-align: center;
		margin: 32rpx;
	}
	.submit_ac1{
		background: #488cf5;
	}
	.submit_ac2{
		background: rgba(232, 37, 20, 0.4);
	}
	.placeholder{
		font-size: 28rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.7);
		line-height: 40rpx;
	}
	.input{
		width: 686rpx;
		height: 494rpx;
		background: rgba(216, 216, 216, 0.11);
		border-radius: 22rpx;
		margin: 48rpx 0 0 32rpx;
		padding: 42rpx 0 0 24rpx;
		box-sizing: border-box;
	}
</style>
