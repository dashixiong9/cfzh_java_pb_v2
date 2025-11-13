<template>
	<view>
		<input maxlength="8" @input="inputChange" class="input" placeholder="设置备注名称" placeholder-class="placeholder"></input>
		<view class="Prompt">注：备注名称最多支持8个汉字</view>
		<view :class="buttomStatus?'pay_active u-abso':'pay u-abso'" @click="next">保存</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				value: '',
				id:''
			}
		},
		computed:{
			buttomStatus : function (){
				if(this.value){
					return true
				}else{
					return false
				}
			}
		},
		onLoad(e) {
			console.log(e)
			this.id = e.id
		},
		methods:{
			inputChange(e){
				this.value = e.detail.value
			},
			async next(){
				if(!this.value){
					return
				}
				var res = await this.$u.api.employerUpdateStaff({
					note:this.value,
					id:this.id
				})
				console.log(res)
				if(res.code == 10002){
					uni.$emit("remarksChange",res.data.note);
					uni.navigateBack()
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
	.Prompt{
		width: 480rpx;
		height: 34rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.3);
		line-height: 34rpx;
		margin: 32rpx 0 0 24rpx;
	}
	.pay{
		width: 686rpx;
		height: 100rpx;
		background: #488cf5;
		border-radius: 16rpx;
		opacity: 0.4;
		font-size: 36rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #FFFFFF;
		line-height: 100rpx;
		text-align: center;
		left: 32rpx;
		bottom: 44rpx;
	}
	.pay_active{
		@extend .pay;
		background: #488cf5;
		opacity: 1;
	}
	.placeholder{
		height: 44rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.3);
		line-height: 44rpx;
	}
	.input{
		width: 750rpx;
		height: 100rpx;
		background: #FFFFFF;
		margin-top: 24rpx; 
		padding-left: 24rpx;
		box-sizing: border-box;
	}
</style>
