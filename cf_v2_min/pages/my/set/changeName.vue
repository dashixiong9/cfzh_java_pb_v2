<template>
	<view>
		<input v-model="nickname" placeholder="请输入新的昵称"  class="inp" />
		<view class="trip">注：昵称最多支持8个汉字</view>
		<view class="buttonNo" @click="send" :style="nickname.length == 0 ? 'opacity: .5;' : ''">
			保存修改
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				nickname:""
			}
		},
		methods: {
			clear(){
				this.nickname = ""
			},
			async send(){
				const {data,code} = await this.$u.api.customerBaseUpdate({
						nickName:this.nickname
				})
				console.log(data,code);
				if(code === 10002){
					const user = this.vuex_user
					user.nickName = this.nickname
					uni.setStorageSync('user',user)
					uni.navigateBack({
						delta:1
					})
				}
			}
		}
	}
</script>

<style lang="less">
	page{
		background-color: #f7f7f7;
	}
	.buttonNo{
		width: 90vw;
		height: 100upx;
		line-height: 100upx;
		border-radius: 10upx;
		text-align: center;
		color: #FFFFFF;
		background: #488cf5;
		margin-top: 74upx;
		margin-left: 32rpx;
	}
	.trip{
		width: 280rpx;
		height: 34rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.5);
		line-height: 34rpx;
		margin: 32rpx 0 0 24rpx;
	}
	.inp{
		width: 750rpx;
		height: 100rpx;
		background: #FFFFFF;
		margin-top: 32rpx;
		padding-left: 24rpx;
		box-sizing: border-box;
	}
</style>
