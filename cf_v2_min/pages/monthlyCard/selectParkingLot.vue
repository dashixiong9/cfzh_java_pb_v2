<template>
	<view>
		<view class="inputBG">
			<view class="inputBox u-flex-r">
				<image src="../../static/image/home/icon-search2x.png" ></image>
				<input type="text" :focus='true' value="" class="input" placeholder="搜索附近停车场" placeholder-class="placeholder" confirm-type="search"
				 @confirm='SeedSearch' />
			</view>
		</view>
		
		<view class="list"  v-if="carsByPhones.length != 0">
			<!-- <view class="li" style="font-weight: 600;color: #000;">已开通长租包月的停车场</view> -->
			<view class="li" v-for="(item,i) in carsByPhones" @click="selectCar(item)">
				<text >{{item.name}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				carsByPhones:[]
			}
		},
		methods: {
			async SeedSearch(e) {
				console.log(e);
				var res = await this.$u.api.getListByQuery({
					name: e.detail.value,
					page: 1,
					size: 10
				})
				if(res.code == 10002){
					this.carsByPhones = res.data
				}else{
					uni.showToast({
						title:res.message,
						icon:'none'
					})
					this.carsByPhones = []
				}
			},
			selectCar(item){
				var openMonthlyPlanData = uni.getStorageSync('openMonthlyPlanData') || {}
				openMonthlyPlanData.park = item
				uni.setStorageSync("openMonthlyPlanData",openMonthlyPlanData)
				uni.navigateTo({
					url: "packageOptions"
				})
				return
				uni.navigateTo({
					url:"selectLicensePlate"
				})
			}
		}
	}
</script>

<style lang="less">
	page{
		background-color: #fafafa;
	}
	.inputBG {
		width: 750rpx;
		height: 154rpx;
		background-color: #F7F7F7;
		overflow: hidden;
		position: fixed;
		top: 0;
		left: 0;
		.inputBox {
			width: 686rpx;
			height: 88rpx;
			background: #FFFFFF;
			border-radius: 60rpx;
			margin: 32rpx 0 0 32rpx;
	
			image {
				width: 48rpx;
				height: 48rpx;
				margin: 24rpx 0 0 30rpx;
			}
	
			.input {
				width: 580rpx;
				height: 48rpx;
				margin-top: 24rpx;
				margin-left: 20rpx;
			}
		}
	}
	
.list{
	margin-top: 154rpx;
	background-color: #FFFFFF;
	
	.li{
		border-bottom: 2rpx solid #F1F1F1;
		padding: 24upx;
		font-size: 28upx;
		color: #666666;
	}
}
.default {
	display: flex;
	flex-direction: column;
	text-align: center;
	align-items: center;
	margin-top: 400upx;
	background-color: #fafafa;
	> image {
		width: 240upx;
		height: 136upx;
	}

	.hint1 {
		font-size: 26upx;
		color: #4f6593;
	}
	.hint2 {
		font-size: 20upx;
		color: #999999;
	}

	.button {
		border: 2rpx solid #4f6593;
		font-size: 28upx;
		color: #4f6593;
		padding: 10upx 26upx;
		border-radius: 30upx;
		margin-top: 30upx;
	}
}
.add{
	
	position: fixed;
	bottom: 5vw;
	width: 90vw;
	height: 90upx;
	background-color: #6977D0;
	margin: 0upx 5vw;
	text-align: center;
	line-height: 90upx;
	border-radius: 10upx;
	font-size: 32upx;
	color: #FFFFFF;
}
</style>
