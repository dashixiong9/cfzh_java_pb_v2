<template>
	<view>
		<view class="titleBox">
			<image src="../../static/image/monthlyCard/parkingBG@2x.png" style="width: 100%;height: 100%;" >
			</image>
			<view class="Parkinglot u-line-2">{{type ? info.carParkName : info.park.name}}</view>
		</view>
		<parkingCardList :list='list' @onList='onList'></parkingCardList>
		<u-mask :show="maskShow" @click="maskShow = false">
			<view style="width: 100%;height: 100%;" class="u-flex">
				<view class="choose" @tap.stop>
					<view class="title">选择车牌</view>
					<view class="subtitle">购买月卡需先选择车牌哦</view>
					<scroll-view scroll-y="true" style="widows: 670rpx;height: 300rpx;" v-if="licensePlateList.length > 0">
						<view class="licensePlate" v-for="item of licensePlateList" @click="intoPay(item.numberPlate)">
							{{item.numberPlate}}</view>
					</scroll-view>
					<view v-else style="width: 100%;" class="u-flex-col u-col-center">
						<image src="../../static/image/home/nocar.png" style="width: 220rpx;height: 108rpx;margin-top:20rpx;" ></image>
						<view style="margin:16rpx 0 0 0;width: 362rpx;height: 32rpx;font-family: PingFangSC-Regular, PingFang SC;font-weight: 400;color: #CDCDCD;line-height: 32rpx;">...(- -)..您还未绑定车牌哦~</view>
					</view>
					<navigator url="../my/myCar/addCar" hover-class="none">
						<view class="addCarBox u-flex u-row-center">
							<image src="../../static/image/my/Add@2x.png" ></image>
							<view class="text">添加车辆</view>
						</view>
					</navigator>
				</view>
			</view>
		</u-mask>
	</view>
</template>

<script>
	import parkingCardList from '../../components/parkingCardList/index.vue'
	export default {
		data() {
			return {
				list: [],
				info: '',
				type: '',
				maskShow: false,
				licensePlateList: [],
				activeIndex: 0
			}
		},
		components: {
			parkingCardList
		},
		onLoad(e) {
			console.log(e)
			if (e.type == 'renew') {
				this.info = JSON.parse(e.info)
				this.type = 'renew'
			} else {
				this.info = uni.getStorageSync('openMonthlyPlanData')
			}
			this.getList()
		},
		onShow() {
			this.getMyCarsByCondition()
		},
		methods: {
			async getMyCarsByCondition() {
				var res = await this.$u.api.getMyCarsByCondition({
					page: 1,
					size: 100
				})
				console.log(res)
				if (res.code == 10002) {
					this.licensePlateList = res.data
				} else {
					this.licensePlateList = []
				}
			},
			onList(index) {
				console.log(index);
				this.activeIndex = index
				this.maskShow = true
			},
			intoPay(item) {
				var openMonthlyPlanData = uni.getStorageSync('openMonthlyPlanData') || {}
				openMonthlyPlanData.licensePlate = item
				uni.setStorageSync("openMonthlyPlanData", openMonthlyPlanData)
				if (!this.type) {
					var parkingLotInfo = {
						carParkName: this.info.park.name,
						numberPlate: item
					}
				} else {
					var parkingLotInfo = {
						carParkName: this.info.carParkName,
						numberPlate: item
					}
				}
				uni.navigateTo({
					url: 'pay?setMealInfo=' + JSON.stringify(this.list[this.activeIndex]) + '&parkingLotInfo=' +
						JSON.stringify(
							parkingLotInfo)
				})
			},
			async getList() {
				var res = await this.$u.api.getCarParkPackagePriceByCondition({
					carParkId: this.type == 'renew' ? this.info.carParkId : this.info.park.id
				})
				console.log(res);
				if (res.code != 10002) {
					uni.showModal({
						title: '提示',
						content: '很抱歉，您选择的停车场暂时还没推出套餐，敬请期待',
						showCancel: false,
						confirmText: '确认',
						success: res => {
							console.log(res);
							uni.navigateBack()
						}
					});
				} else {
					this.list = res.data
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background: #F8F9FB;
	}

	.titleBox {
		width: 750rpx;
		height: 294rpx;

		.Parkinglot {
			width: 478rpx;
			height: 112rpx;
			font-size: 40rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #FFFFFF;
			line-height: 56rpx;
			position: absolute;
			top: 94rpx;
			left: 40rpx;
		}
	}

	.choose {
		width: 750rpx;
		// height: 530rpx;
		background: #FFFFFF;
		border-radius: 48rpx 48rpx 0rpx 0rpx;
		align-self: flex-end;

		.title {
			width: 176rpx;
			height: 60rpx;
			font-size: 44rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 60rpx;
			margin: 40rpx 0 0 40rpx;
		}

		.subtitle {
			width: 264rpx;
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 34rpx;
			margin: 4rpx 0 0 40rpx;
		}

		.licensePlate {

			background: #F1F6FF;
			border-radius: 12rpx;
			border: 2rpx solid #488CF5;
			width: 670rpx;
			height: 136rpx;
			border-radius: 12rpx;
			font-size: 44rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #488CF5;
			line-height: 136rpx;
			padding-left: 32rpx;
			margin-top: 20rpx;
			margin-left: 42rpx;
		}

		.addCarBox {
			width: 670rpx;
			height: 136rpx;
			background: rgba(72, 140, 245, 0.08);
			border-radius: 12rpx;
			border: 3rpx dashed #488CF5;
			margin: 46rpx 0 0 40rpx;
			margin-bottom: 32rpx;
			margin-top: 20rpx;

			image {
				width: 32rpx;
				height: 32rpx;
			}

			.text {
				width: 144rpx;
				height: 50rpx;
				font-size: 36rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #488CF5;
				line-height: 50rpx;
			}
		}
	}
</style>
