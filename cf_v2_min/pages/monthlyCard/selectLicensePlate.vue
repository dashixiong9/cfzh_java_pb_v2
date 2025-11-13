<template>
	<view>
		<view class="list" v-if="list.length != 0">
			<view class="li" style="font-weight: 600;color: #000;">选择车牌</view>
			<!-- <view class="li" v-for="(item,i) in list" @click="selectCar(i,item)">
				<text>{{item.numberPlate}}</text>
			</view> -->
			<view class="u-flex-c" style="background: #f7f7f7;margin-bottom: 150rpx;">
				<view v-for="(item,i) in list" :key="i" class="carBox u-flex-c" @click="selectCar(i,item)">
					<view class="num u-rela"> 
						<image class="u-abso" @click="deleteCar(item)" src="../../static/image/home/delete_white2x.png" style="width: 48rpx;height: 48rpx;top: 28rpx;right: 20rpx;" ></image>
					{{item.numberPlate}}</view>
					<view class="setMeal u-flex-r u-col-center">
						<view class="title">暂无停车卡</view>
						<view class="quickCardOpening">快捷开卡</view>
					</view>
				</view>
			</view>
			
		</view>
		<view class="default" v-else>
			<image src="../../static/image/my/list_not2x.png"></image>
			<view class="hint1">您还未绑定车牌哦</view>
			<view class="hint2">快去绑定享受便捷停车吧</view>
			<view class="button" @click="intoAdd">立即绑定</view>
		</view>

		<view class="add" @click="intoAdd" v-if="list.length">添加车牌</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: []
			}
		},
		onLoad() {

		},
		onShow() {
			this.getList()
		},
		methods: {
			async getList() {
				var res = await this.$u.api.getMyCarsByCondition({
					page: 1,
					size: 100
				})
				console.log(res)
				if (res.code == 10002) {
					this.list = res.data
				}
			},
			selectCar(i, item) {
				this.index = i;
				var openMonthlyPlanData = uni.getStorageSync('openMonthlyPlanData') || {}
				openMonthlyPlanData.licensePlate = item
				uni.setStorageSync("openMonthlyPlanData", openMonthlyPlanData)
				uni.navigateTo({
					url: "packageOptions"
				})
			},
			intoAdd() {
				uni.navigateTo({
					url: '../my/myCar/addCar'
				})
			}
		}
	}
</script>

<style lang="scss">
	page{
		background-color: #f7f7f7;
	}
	.carBox {
		width: 686rpx;
		height: 302rpx;
		// background-color: #fff;
		margin: 24rpx 0 10rpx 32rpx;
		margin-top: 24rpx;
		border-radius: 16rpx;
		overflow: hidden;
	
		
background: #FFFFFF;
		.num {
			width: 686rpx;
			height: 104rpx;
			background: linear-gradient(315deg, #F7A398 0%, #EC6A5F 100%);
			font-size: 36rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #FFFFFF;
			line-height: 104rpx;
			padding-left: 20rpx;
		}
	
		.setMeal {
			height: inherit;
			// background-color: #d8d8d8;
			.title {
				width: 160rpx;
				height: 44rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: rgba(50, 50, 51, 0.5);
				line-height: 44rpx;
				margin-left: 16rpx;
			}
	
			.quickCardOpening {
				width: 184rpx;
				height: 64rpx;
				border-radius: 40rpx;
				border: 2rpx solid rgba(232, 37, 20, 0.8);
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(232, 37, 20, 0.8);
				line-height: 64rpx;
				text-align: center;
				margin-left: 310rpx;
			}
		}
	}
	.default {
		display: flex;
		flex-direction: column;
		text-align: center;
		align-items: center;
		margin-top: 400upx;
		> image {
			width: 220rpx;
			height: 108rpx;
		}
	
		.hint1 {
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 34rpx;
			margin-top: 24rpx;
		}
		.hint2 {
			font-size: 20upx;
			color: #999999;
		}
	
		.button {
			border: 2rpx solid rgba(232, 37, 20, 0.8);
			font-size: 28upx;
			color: rgba(232, 37, 20, 0.8);
			padding: 10upx 26upx;
			border-radius: 30upx;
			margin-top: 30upx;
		}
	}
	.list {
		margin-top: 20upx;
		background-color: #FFFFFF;

		.li {
			border-bottom: 2rpx solid #F1F1F1;
			padding: 24upx;
			font-size: 28upx;
			color: #666666;
		}
	}

	.add {
		width: 686rpx;
		height: 100rpx;
		border-radius: 16rpx;
		border: 2rpx solid #488cf5;
		position: fixed;
		font-size: 36rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #488cf5;
		line-height: 100rpx;
		text-align: center;
		left: 32rpx;
		bottom: 32rpx;
		background: #fff;
	}
</style>
