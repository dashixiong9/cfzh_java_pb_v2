<template>
	<view>
		<view class="box u-flex" v-if="list.length" v-for="item in list" :key='item.id' @click="intoDetail(item.id)">
			<view class="u-flex-col">
				<view class="name u-line-1">{{item.stationName}}</view>
				<view class="address u-line-1">{{item.distance.toFixed()}}m | {{item.address}}</view>
				<view class="price">¥{{item.startPrice.toFixed(2)}}/h</view>
				<view class="vacancy">可充电 剩余{{item.slowChargeNumber - item.usedSlowChargeNumber}}个充电口</view>
			</view>
			<view class="u-flex-col" style="margin-left: 18rpx;">
				<image src="../../../static/image/chargingPileNearby/Recharge.png" class="Recharge"
					@click.stop="intoChargingTime"></image>
				<image src="../../../static/image/chargingPileNearby/navigation.png" class="navigation"
					@click.stop="openMap(item.positionY, item.positionX,item.stationName, item.address)"></image>
			</view>
		</view>
		<view v-if="!list.length">
			<image style="width: 220rpx;height: 108rpx;margin: 370rpx 0 24rpx 266rpx;"
				src="../../../static/image/my/nocharge.png"></image>
			<view
				style="height: 32rpx;font-size: 30rpx;font-weight: 400;color: #CDCDCD;line-height: 32rpx;text-align: center;">
				哎呦~您附近暂无充电桩</view>
		</view>
	</view>
</template>

<script>
	export default {
		props: ['list'],
		data() {
			return {

			}
		},
		methods: {
			intoChargingTime() {
				uni.scanCode({
					scanType: 'qrcode',
					success: (res) => {
						console.log(res);
					}
				})
				// uni.navigateTo({
				// 	url: '../chargingTime/index'
				// });
			},
			intoDetail(id){
				uni.navigateTo({
					url:'/pages/charge/electricBicycleDetail?id=' + id
				})
			},
			
		}
	}
</script>

<style lang="scss">
	page {
		background: #F8F9FB;
		padding-top: 120rpx;
		padding-bottom: 30rpx;
	}

	.Recharge {
		width: 104rpx;
		height: 144rpx;
		margin-top: 40rpx;
	}

	.navigation {
		width: 104rpx;
		height: 52rpx;
	}

	.box {
		width: 670rpx;
		height: 276rpx;
		background: #FFFFFF;
		box-shadow: 0rpx 16rpx 22rpx 0rpx rgba(10, 36, 99, 0.07);
		border-radius: 16rpx;
		margin: 40rpx 0 0 40rpx;
		align-items: flex-start;
	}

	.name {
		width: 470rpx;
		height: 40rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Semibold, PingFang SC;
		font-weight: 600;
		color: #1A1A1A;
		line-height: 40rpx;
		margin: 48rpx 0 0 40rpx;
	}

	.address {
		width: 470rpx;
		height: 32rpx;
		font-size: 28rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.5);
		line-height: 32rpx;
		margin: 12rpx 0 0 40rpx;
	}

	.price {
		width: 134rpx;
		height: 36rpx;
		font-size: 36rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FF5151;
		line-height: 36rpx;
		margin: 16rpx 0 0 40rpx;
	}

	.vacancy {
		width: 470rpx;
		height: 32rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #488CF5;
		line-height: 32rpx;
		margin: 12rpx 0 0 40rpx;
	}
</style>
