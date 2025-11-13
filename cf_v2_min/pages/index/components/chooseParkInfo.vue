<template>
	<view>
		<view class="u-abso animated fadeInUp infoBox" v-if="beChosenArr.length && isInternal">
			<view class="listItem u-flex-c u-rela" @click.stop="openParkInfo(beChosenArr[0])">
				<view class="title u-flex-r u-col-center">
					<view class="name u-line-1">{{beChosenArr[0].name}}</view>
				</view>
				<view class="location u-line-1">{{beChosenArr[0].address}}</view>
				<view class="priceBox u-flex-r u-col-center">
					<view class="price">￥{{beChosenArr[0].currentPrice}}/h</view>
					<view class="isCharge" v-if="beChosenArr[0].chargingStationId">可充电</view>
				</view>
				<view class="giveAMark"> 免费停{{beChosenArr[0].freeTime / 60000}}分钟 <text
						v-if="beChosenArr[0].feeUpperLimit" style="margin-left: 10rpx;">
						每日封顶{{ beChosenArr[0].feeUpperLimit }}元</text></view>
				<view class="quantity">车位数({{beChosenArr[0].parkingSpaceNumber}}) |
					可用({{beChosenArr[0].parkingSpaceNumber - beChosenArr[0].usedParkingSpaceNumber}})</view>
				<view class="line" />
				<view style="width: 100%;" class="intoParkingNearby" @click.stop="intoParkingNearby">查看附近 <text
						style="color: #F05154;margin: 0 8rpx;font-weight: 700;">{{parkingData.length}}</text>个停车场</view>
				<image src="../../../static/image/home/Navigationicon2x.png" class="u-abso rightImg"
					@click.stop="openMap(beChosenArr[0].positionY, beChosenArr[0].positionX,beChosenArr[0].name, beChosenArr[0].address)" />
			</view>
		</view>
		<view class="info u-flex-col u-row-center u-col-center animated fadeInUp"
			v-if="!beChosenArr.length && isInternal">
			<image src="../../../static/image/my/list_not2x.png"></image>
			<view class="list_not_title">
				抱歉~附近没有找到停车场
			</view>
		</view>
		
		
		
	</view>
</template>

<script>
	export default {
		props: ['beChosenArr', 'isInternal', 'parkingData'],
		methods: {
			openParkInfo(parkInfo) {
				if (!this.isInternal) {
					this.latitude = parkInfo.location.lat
					this.longitude = parkInfo.location.lng
					return
				}
				console.log(parkInfo);
				uni.setStorageSync("parkInfo", parkInfo)
				uni.navigateTo({
					url: '/pages/index/parkInfo',
					fail(e) {
						console.log(e);
					}
				})
			},
			intoParkingNearby() {
				console.log(this.parkingData);
				uni.setStorageSync('nearbyParkingLotList', this.parkingData)
				uni.navigateTo({
					url: '/pages/index/nearbyParkingLot'
				})
			},
		}
	}
</script>

<style lang="less">
	
	.info {
		position: absolute;
		width: 690rpx;
		height: 280rpx;
		background: #FFFFFF;
		box-shadow: 0rpx 16rpx 22rpx 0rpx rgba(10, 36, 99, 0.07);
		left: 30rpx;
		border-radius: 16rpx;
		z-index: 3;
		bottom: 68rpx;
	
		image {
			width: 220rpx;
			height: 108rpx;
		}
	
		.list_not_title {
			height: 32rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #CDCDCD;
			line-height: 32rpx;
			margin-top: 24rpx;
		}
	}
</style>
