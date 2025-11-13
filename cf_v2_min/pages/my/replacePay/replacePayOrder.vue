<template>
	<view class="u-flex-c">
		<view class="box u-flex-c u-rela" v-for="(item,index) in list" @click="onItem(item)">
			<view class="pay u-abso" v-if="!item.payTime">
				待支付
			</view>
			<view class="listStatus u-abso" v-else>
				已完成
			</view>

			<view class="name u-line-1">{{item.cfCarPark.name}}</view>
			<view class="licensePlate">{{item.numberPlate}}</view>
			<view class="u-flex-r getIntoTime u-col-center">
				<view style="width: 16rpx;height: 16rpx;background: #0A2463;border-radius: 50%;margin-top: 9rpx;">
				</view>
				<view class="licensePlate">{{item.inTime ? $u.timeFormat(item.inTime , 'yyyy-mm-dd hh:MM:ss') : '-'}}</view>
			</view>
			<view class="u-flex-r getOutTime u-col-center">
				<view style="width: 16rpx;height: 16rpx;background: #F0AC35;border-radius: 50%;margin-top: 9rpx;">
				</view>
				<view class="licensePlate">{{item.outTime ? $u.timeFormat(item.outTime , 'yyyy-mm-dd hh:MM:ss') : '-'}}
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [],
				plateNo:''
			}
		},
		onLoad(e) {
			console.log(e);
			var {
				plateNo
			} = e
			this.plateNo = plateNo
			// this.getInfo(plateNo)
		},
		onShow() {
			this.getInfo(this.plateNo)
		},
		methods: {
			onItem(item) {
				console.log(item)
				if (item.payTime) {
					return
				}
				uni.navigateTo({
					url: 'pay?id=' + item.id
				})
			},
			async getInfo(numberPlate) {
				var res = await this.$u.api.getCarParkUseLogList({
					page: 1,
					size: 1000,
					numberPlate
				})
				console.log(res);
				if (res.code == 10002) {
					this.list = res.data
				} else {
					this.$u.toast('没有查询到停车记录')
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background: #F7F7F7;
	}

	.listStatus {
		width: 128rpx;
		height: 56rpx;
		border-radius: 28rpx;
		border: 2rpx solid #B0B9D0;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #B0B9D0;
		line-height: 56rpx;
		text-align: center;
		margin: 24rpx 0 0 12rpx;
		left: 528rpx;
	}

	.box {
		width: 686rpx;
		height: 248rpx;
		background: #FFFFFF;
		border-radius: 16rpx;
		margin: 24rpx 0 0 32rpx;

		.pay {
			width: 128rpx;
			height: 56rpx;
			border-radius: 28rpx;
			border: 2rpx solid #EF3864;
			left: 528rpx;
			top: 24rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #EF3864;
			line-height: 56rpx;
			text-align: center;
		}

		.name {
			width: 484rpx;
			height: 40rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 40rpx;
			margin: 32rpx 0 0 32rpx;
		}

		.licensePlate {
			width: 484rpx;
			height: 40rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 40rpx;
			margin: 8rpx 0 0 32rpx;
		}

		.getIntoTime {
			margin: 16rpx 0 0 32rpx;
			height: 32rpx;
		}

		.getOutTime {
			margin: 16rpx 0 0 32rpx;
			height: 32rpx;
		}
	}

	.licensePlate {
		width: 590rpx;
		height: 32rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(10, 36, 99, 0.5);
		line-height: 32rpx;

	}
</style>
