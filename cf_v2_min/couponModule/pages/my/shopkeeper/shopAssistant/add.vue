<template>
	<view>
		<input @input="inputChange" class="input" placeholder="请输入员工ID(我的页面用户头像下可复制ID)" placeholder-class="placeholder"></input>
		<view class="Prompt">注：用户ID可通过我的页面用户头像下可复制</view>
		<view :class="buttomStatus?'pay_active':'pay'" @click="next">下一步</view>
	</view>
</template>

<script>
	export default {
		data(){
			return {
				value:''
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
		methods:{
			async next(){
				if(!this.value){
					return
				}

				// return
				// 1210829201413599232
				var res = await this.$u.api.findById({
					uid : this.value,
					simpleQuery:'yes'
				})
				if(res.code != 10002){
					uni.showModal({
						content:'您输入的ID不存在请重新输入',
					    confirmText:'我知道了',
						showCancel:false
					})
					return
				}
				uni.navigateTo({
					url:'detail?userinfo=' + encodeURIComponent(JSON.stringify(res.data))
				})
				console.log(res)
			},
			inputChange(e){
				console.log(e);
				var { value } = e.detail
				this.value = value
			}
		},
		
	}
</script>

<style lang="scss">
	page{
		background-color: #f7f7f7;
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
		margin: 126rpx 0 0 32rpx;
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
	.Prompt{
		width: 480rpx;
		height: 34rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.3);
		line-height: 34rpx;
		margin: 16rpx 0 0 24rpx;
	}
</style>
