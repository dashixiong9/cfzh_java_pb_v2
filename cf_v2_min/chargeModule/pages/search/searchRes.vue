<template>
	<view>
		<view class="inputBG">
			<view class="inputBox u-flex-r" @click="Back">
				<image src="../../../static/image/home/icon-search2x.png" ></image>
				<view class="input">
					{{value}}
				</view>
			</view>
		</view>
		<view style="height: 154rpx;"></view>
		<!-- 搜索结果 -->
		<view>
			<view v-if="list.length">
				<view v-for="(item,index) in list" class="listItem u-flex-c u-rela" @click.stop="intoChargingDetail(item)">
					<view class="title u-flex-r u-col-center">
						<view class="name u-line-1">{{item.stationName}}</view>
						<view class="near" v-if="index === 0">距离最近</view>
					</view> 
					 <!-- +  item.address -->
					<view class="location u-line-1">{{item.address}}</view>
					<view class="priceBox u-flex-r u-col-center">
						<!-- <view class="preferential">￥{{cover.callout.currentPrice}}/h</view> -->
						<view class="price">￥{{item.startPrice}}/度</view>
					</view>
					<view class="giveAMark">免费停车{{item.freeParkTime}}分钟</view>
					<view class="u-flex">
						<view class="quantity">
							慢充可用({{item.slowChargeNumber - item.usedSlowChargeNumber}})</view>
						<view class="quantity">
							快充可用({{item.fastChargeNumber - item.usedFastChargeNumber}})</view>
						<view class="quantity">
							超快充可用({{item.superFastChargeNumber - item.usedSuperFastChargeNumber}})
						</view>
					</view>
					<image src="../../../static/image/home/Navigationicon2x.png" class="u-abso rightImg" @click.stop="openMap( item.positionY, item.positionX,item.name, item.address)" />
				</view>
			</view>
			<view class="list_not" v-else>
				<view class="info u-flex-col u-row-center u-col-center">
					<image src="../../../static/image/my/list_not2x.png"></image>
					<view class="list_not_title">
						哎呦~未找到该停车场
					</view>
				</view>
				<div v-if='nearbyParking.length > 0'>
					<view class="recommendTitle">为您推荐</view>
					<parkingList :list='list'></parkingList>
				</div>
			</view>
		</view>

	</view>
</template>

<script>
	import parkingList from '../../../components/parkingList/parkingList.vue'
	export default {
		components: {
			parkingList
		},
		data() {
			return {
				list: [],
				active: false,
				value: '',
				nearbyParking: []
			}
		},
		onLoad(e) {
			console.log(e);
			if (e.list != 'null') {
				this.list = JSON.parse(e.list)
			}

			this.active = e.active
			this.value = e.value
			this.getSelectNearbyDatas()
		},
		methods: {
			intoChargingDetail(parkInfo) {
				uni.navigateTo({
					url: '/pages/index/parkInfo?chargeInfo=' + JSON.stringify(parkInfo)
				})
			},
			isActive(e) {
				this.$u.toast('正在开发中')
				return
				this.active = e
			},
			Back() {
				uni.navigateBack()
			},

			getSelectNearbyDatas() {
				uni.getLocation({
					success: async (location) => {
						const {
							code,
							data
						} = await this.$u.api.selectNearbyDatas({
							//经度
							positionX: location.longitude,
							//纬度
							positionY: location.latitude,
							page: 1,
							limit: 10
						})
						if (code == 10002) {
							this.nearbyParking = data
						}
					}
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #F8F9FB;
	}

	.searchHistory_box {
		width: 750rpx;
		height: 88rpx;
		border-bottom: 2rpx solid #E0E7F2;

		.title {
			width: 130rpx;
			height: 88rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 88rpx;
			margin-left: 32rpx;
		}

		image {
			width: 32rpx;
			height: 32rpx;
			margin-left: 524rpx;
		}
	}

	.line {
		width: 74rpx;
		height: 8rpx;
		background: #488cf5;
		border-radius: 4rpx;
		bottom: 0;
		left: 12rpx;
	}

	.option {
		width: 750rpx;
		height: 88rpx;
		border-bottom: 2rpx solid #eaeaea;

		.optionOne {
			width: 98rpx;
			height: 88rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #488cf5;
			line-height: 88rpx;
			margin-left: 32rpx;
		}

		.optionOne_gray {
			@extend .optionOne;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			opacity: .6;
		}

		.optionTwo {
			@extend .optionOne;
			margin-left: 48rpx;
		}
	}

	.inputBG {
		width: 750rpx;
		height: 152rpx;
		background-color: #fff;
		overflow: hidden;
		position: fixed;
		left: 0;
		top: 0;
		z-index: 1;

		.inputBox {
			width: 686rpx;
			height: 88rpx;
			background: #F8F9FB;
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

	.placeholder {
		font-size: 32rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(26, 26, 26, 0.5);
		line-height: 48rpx;

	}


	.list_not {
		width: 750rpx;
		// height: 500rpx;
		// background: #FFFFFF;

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

		.info {
			width: 670rpx;
			height: 280rpx;
			background: #FFFFFF;
			box-shadow: 0rpx 16rpx 22rpx 0rpx rgba(10, 36, 99, 0.07);
			border-radius: 16rpx;
			margin: 42rpx 0 0 40rpx;
		}

		.recommendTitle {
			width: 144rpx;
			height: 36rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 36rpx;
			margin: 48rpx 0 0 40rpx;
		}
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
