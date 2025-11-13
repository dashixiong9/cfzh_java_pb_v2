<template>
	<view class="u-flex-c">
		<view v-for="(item,index) in list" class="listItem u-flex-c u-rela" @click.stop="openParkInfo(item)">
			<view class="title u-flex-r u-col-center">
				<view class="name u-line-1">{{item.name}}</view>
				<view class="near" v-if="index==0">距离最近</view>
			</view>
			<!-- +  item.address -->
			<view class="location u-line-1">{{distance(parseInt(item.distance), ' | ' + item.address)}}</view>
			<view class="priceBox u-flex-r u-col-center">
				<view v-if="cover.callout.currentPrice" class="preferential">￥{{cover.callout.currentPrice}}/h</view>
				<view v-if="item.startingPrice > 0" class="price">￥{{item.startingPrice}}/h</view>
				<!-- <view class="num">{{'￥' + item.cfCarParkChargingRules[0].fee + '/h'}}</view> -->
				<!-- <view class="preferential">{{item.currentPrice}}元/时</view> -->
			</view>
			<view class="giveAMark"> 免费停{{item.freeTime / 60000}}分钟 | 每日封顶{{ item.feeUpperLimit }}元</view>
			<view class="quantity">车位数({{item.parkingSpaceNumber}}) |
				可用({{item.parkingSpaceNumber - item.usedParkingSpaceNumber}})</view>
			<!-- <view class="quantity">车位({{item.parkingSpaceNumber}}) | 可用({{item.parkingSpaceNumber - item.usedParkingSpaceNumber}})</view> -->
			<image src="../../static/image/home/Navigationicon2x.png" class="u-abso rightImg"
				@click.stop="openMap( item.positionY, item.positionX,item.name, item.address)" />
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {

			};
		},
		props: {
			list: {
				type: Array,
				default: []
			}
		},
		methods: {
			distance(e, address) {
				if (e < 1000) {
					return e + 'm ' + address
				} else {
					return e / 1000 + 'km ' + address
				}
			},
			openParkInfo(parkInfo) {
				console.log(123);
				uni.setStorageSync("parkInfo", parkInfo)
				uni.navigateTo({
					url: '/pages/index/parkInfo',
					fail(e) {
						console.log(e);
					}
				})
			},
		}
	}
</script>

<style lang="scss" scoped>
	page {
		background: #F8F9FB;
	}
	.listItem {
		// width: 750rpx;
		// height: 200rpx;
		// background-color: #fff;
		// border-bottom:1rpx solid #E0E7F2; 

		width: 670rpx;
		height: 280rpx;
		background: #FFFFFF;
		box-shadow: 0rpx 16rpx 22rpx 0rpx rgba(10, 36, 99, 0.07);
		border-radius: 16rpx;
		margin: 42rpx 0 0 40rpx;

		.title {
			margin: 32rpx 0 0 32rpx;

			.name {
				max-width: 360rpx;
				height: 40rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #1A1A1A;
				line-height: 40rpx;
			}

			.near {
				width: 96rpx;
				// height: 32rpx;
				border-radius: 4rpx;
				border: 1rpx solid #FF575B;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #F05154;
				line-height: 32rpx;
				text-align: center;
				margin-left: 8rpx;
			}
		}

		.location {
			width: 470rpx;
			height: 32rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.5);
			line-height: 32rpx;
			margin: 12rpx 0 0 32rpx;
		}

		.price {
			height: 36rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #F05154;
			line-height: 36rpx;
		}

		.priceBox {
			margin-left: 32rpx;
			margin-top: 8rpx;

			.num {
				height: 36rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #F05154;
				line-height: 36rpx;
				margin-right: 4rpx;
			}

			.preferential {
				padding: 1rpx 8rpx;
				height: 32rpx;
				background: #F05154;
				border-radius: 4rpx;
				// width: 72rpx;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #FFFFFF;
				line-height: 30rpx;
				margin-left: 4rpx;
			}

		}

		.quantity {
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #3B76D0;
			line-height: 34rpx;
			margin: 12rpx 0 0 32rpx;
		}

		.rightImg {
			width: 120rpx;
			height: 120rpx;
			top: 80rpx;
			right: 30rpx;
		}
	}

	.giveAMark {
		width: 310rpx;
		height: 28rpx;
		font-size: 20rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #FF5151;
		line-height: 28rpx;
		margin: 10rpx 0 0 32rpx;
	}
</style>
