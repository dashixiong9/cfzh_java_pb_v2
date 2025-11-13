<template>
	<view>
		<view v-if="list.length == 0" class="u-flex-c">
			<image src="../../../static/image/my/list_not2x(1).png"
				style="width: 220rpx;height: 108rpx;margin: 206rpx 0 0 266rpx;"></image>
			<view class="notBill">您还木有停车记录哦</view>
		</view>
		<u-swipe-action bg-color='#F7F7F7' :show="item.show" :index="index" v-for="(item, index) in list" :key="item.id"
			@click="click" :options="options">
			<view class="stopCarList" @click="intoOrderDetail(item)">
				<view class="listTitle u-flex-r">
					<view class="listName">
						{{item.cfCarPark.name}}
					</view>
					<view class="listStatus" v-if="!item.payTime">
						停车中
					</view>
					<view class="listStatus_old" v-else>
						已完成
					</view>
				</view>
				<view class="licensePlateNumber">
					车牌号：{{item.numberPlate}}
				</view>
				<view class="admissionTime u-flex-r u-col-center">
					<view class="admissionTimeIcon"></view>
					<view class="time">
						入场时间：{{item.inTime?$u.timeFormat(item.inTime, 'yyyy-mm-dd hh:MM:ss'):'-'}}
					</view>
				</view>
				<view class="appearanceTime u-flex-r u-col-center">
					<view class="appearanceTimeIcon"></view>
					<view class="time">
						出场时间：{{item.outTime?$u.timeFormat(item.outTime, 'yyyy-mm-dd hh:MM:ss'):'-'}}
					</view>
				</view>
			</view>
		</u-swipe-action>


	</view>
</template>

<script>
	export default {
		onLoad() {
			this.getList()
		},
		data() {
			return {
				list: [],
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#488cf5'
					}
				}],
				page:1
			}
		},
		methods: {
			click(index, index1) {
				console.log(index)
			},
			intoOrderDetail(item) {
				console.log(item);
				if (item.payTime) {
					uni.navigateTo({
						url:'parkingRecordInfo?id=' + item.id
					})
					return
				}
				uni.navigateTo({
					url: '../replacePay/pay?id=' + item.id
				})
			},
			async getList() {
				var res = await this.$u.api.getCarParkUseLogList({
					page: this.page,
					size: 30
				})
				console.log(res)
				if (res.code == 10002) {
					this.list =  this.list.concat(res.data)
				} else {
					this.list = []
				}
			},
			onReachBottom() {
				this.page++
				this.getList()
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #F7F7F7;
	}
	
	.notBill {
		width: 216rpx;
		height: 34rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.7);
		line-height: 34rpx;
		margin-top: 24rpx;
		margin-left: 268rpx;
	}
	.stopCarTitle {
		margin-top: 476rpx;
		display: none;

		.stopCarIcon {
			width: 220rpx;
			height: 108rpx;
		}

		.stopCarText {
			width: 216rpx;
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 34rpx;
			margin-top: 24rpx;
		}
	}

	.stopCarList {
		width: 686rpx;
		height: 248rpx;
		background: #FFFFFF;
		border-radius: 16rpx;
		margin: 24rpx 0 0 32rpx;

		.listTitle {
			.listName {
				width: 484rpx;
				height: 40rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 40rpx;
				margin: 32rpx 0 0 32rpx;
			}

			.listStatus {
				width: 128rpx;
				height: 56rpx;
				border-radius: 28rpx;
				border: 2rpx solid #EF3864;
				font-size: 24rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #EF3864;
				line-height: 56rpx;
				text-align: center;
				margin: 24rpx 0 0 12rpx;
			}

			.listStatus_old {
				@extend .listStatus;
				border: 2rpx solid #B0B9D0;
				color: #B0B9D0;
			}
		}

		.licensePlateNumber {
			width: 484rpx;
			height: 40rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 40rpx;
			margin: 8rpx 0 0 32rpx;
		}

		.admissionTime {
			margin-top: 16rpx;

			.admissionTimeIcon {
				width: 16rpx;
				height: 16rpx;
				background: #0A2463;
				border-radius: 50%;
				margin-left: 32rpx;
			}
		}

		.appearanceTime {
			margin-top: 16rpx;

			.appearanceTimeIcon {
				width: 16rpx;
				height: 16rpx;
				background: #F0AC35;
				border-radius: 50%;
				margin-left: 32rpx;
			}
		}

		.time {
			width: 590rpx;
			height: 32rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.5);
			line-height: 32rpx;
			margin-left: 16rpx;
		}
	}
</style>
