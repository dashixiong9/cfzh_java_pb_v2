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
				<parkingList :list='list'></parkingList>
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
</style>
