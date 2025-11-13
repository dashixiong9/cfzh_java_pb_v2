<template>
	<view>
		<view class="nameBox">
			<view class="name u-line-1">{{parkInfo.name}}</view>
			<view class="u-flex addressBox"
				@click="openMap(parkInfo.positionY,parkInfo.positionX,parkInfo.name,parkInfo.address)">
				<image class='icon' src="../../static/image/home/clock.png" style="width: 32rpx;height: 32rpx;" >
				</image>
				<view class="address u-line-1">{{openingHours}}</view>
			</view>
			<view class="u-flex addressBox"
				@click="openMap(parkInfo.positionY,parkInfo.positionX,parkInfo.name,parkInfo.address)"
				style="margin-top: 18rpx;">
				<image class='icon' src="../../static/image/home/Positioning.png" style="width: 32rpx;height: 32rpx;"
					></image>
				<view class="address u-line-1">{{parkInfo.address}}</view>
			</view>
			<image src="http://file.cfeng.wang/Navigationicon2x2.png" class="navIcon"
				@click="openMap(parkInfo.positionY,parkInfo.positionX,parkInfo.name,parkInfo.address)"></image>
		</view>
		<view class="infoBox">
			<view class="parkInfo">
				<view class="title">车场信息</view>
				<view class="parkNumberBox u-flex">
					<view class="left">车位</view>
					<view class="right">车位数({{parkInfo.parkingSpaceNumber}}) |
						可用({{parkInfo.parkingSpaceNumber - parkInfo.usedParkingSpaceNumber}})</view>
				</view>
				<view class="parkNumberBox u-flex">
					<view class="left">起步价</view>
					<view class="right">￥{{parkInfo.startingPrice}}</view>
				</view>
				<view class="charges">
					<view class="u-flex titleBox">
						<view class="leftTitle">临时车收费标准</view>
						<view class="check" @click="intoCharges(parkInfo.id)">查看</view>
					</view>
					<!-- <view class="timePirceBox">
						<view class="u-flex timePirce" v-for="item of parkInfo.cfCarParkChargingRules" :key='item.id'>
							<view class="time">
								{{(!item.startTime ? '00:00' : $u.timeFormat(item.startTime, 'hh:MM')) + '~' + (!item.endTime ? '00:00' : $u.timeFormat(item.endTime, 'hh:MM'))}}
							</view>
							<view class="time">¥{{item.fee}}/时</view>
						</view>
					</view> -->
				</view>
				<view class="line"></view>
				<view class="u-flex buyMonthCard" @click="buyMonthlyCard">
					<view class="leftTitle">购买月卡</view>
					<image src="../../static/image/my/Group@2x(2).png" ></image>
				</view>
			</view>

			<view class="chargeInfo">
				<view class="u-flex titleBox">
					<view class="leftTitle">充电信息</view>
					<view class="check" @click="viewAllChargingPort" v-if="parkInfo.chargingStationId">查看全部</view>
				</view>
				<view class="nocharge" v-if="!parkInfo.chargingStationId">
					<image src="../../static/image/my/nocharge.png" ></image>
					<view>该车场还未铺设充电设备哦</view>
				</view>
				<view v-else>
					<view class="chargingNum u-flex-col">
						<view class="fast u-flex">
							<view class="left">超快充</view>
							<view class="right">全部{{chargingStationInfo.superFastChargeNumber}} |
								可用{{chargingStationInfo.superFastChargeNumber - chargingStationInfo.usedSuperFastChargeNumber}}
							</view>
						</view>
						<view class="fast u-flex">
							<view class="left">快充</view>
							<view class="right">全部{{chargingStationInfo.fastChargeNumber}} |
								可用{{chargingStationInfo.fastChargeNumber - chargingStationInfo.usedFastChargeNumber}}
							</view>
						</view>
						<view class="slow u-flex">
							<view class="left">慢充</view>
							<view class="right">全部{{chargingStationInfo.slowChargeNumber}} |
								可用{{chargingStationInfo.slowChargeNumber - chargingStationInfo.usedSlowChargeNumber}}
							</view>
						</view>
					</view>
					<view class="line"></view>
					<view class="u-flex titleBox">
						<view class="leftTitle">当前费用</view>
						<view class="check" @click="pirceShow = true">价格详情</view>
					</view>
					<view class="" style="margin: 10rpx 0 0 40rpx;">
						¥{{chargingStationInfo.startPrice + chargingStationInfo.serviceFee}}/度</view>
					<view class="line"></view>
					<view class="remind u-flex">
						<view class="left">限时免费停车</view>
						<view class="right">充电免停{{chargingStationInfo.freeParkTime}}分钟，充电完成，请于 订单详情 页领取优惠券</view>
					</view>
				</view>
			</view>
		</view>
		<view class="scanCode u-flex" v-if="parkInfo.chargingStationId">
			<view class="priceTipsBox">
				<view>{{chargingStationInfo.startPrice + chargingStationInfo.serviceFee}} <text
						style="font-size: 20rpx;">/度</text></view>
				<view>电费{{chargingStationInfo.startPrice}}/度，服务费{{chargingStationInfo.serviceFee}}/度 </view>
			</view>
			<view class="scan">扫码充电</view>
		</view>

		<!-- 价格详情popop -->
		<u-popup v-model="pirceShow" mode='bottom' border-radius='28' closeable>
			<view class="popupBox">
				<view class="title">价格详情</view>
				<view class="pirceItem" :style="item.status ? 'background: rgba(72, 140, 245, 0.1)' : ''" v-for="item in chargingRules" :key='item.id'>
					<view class="time">{{getTimeText(item.startTime) + ' - ' + getTimeText(item.endTime)}}</view>
					<view class="pirce u-flex-col">
						<view class="pirce1">¥{{item.fee + item.serviceFee}}/度</view>
						<view class="pirce2">电费 ¥{{item.fee}} + 服务费 ¥{{item.serviceFee}}</view>
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	import dayjs from 'dayjs' 
	export default {
		data() {
			return {
				bottomPopup: false,
				parkInfo: '',
				entranceAndExitList: [],
				chargingStationInfo: {
					startPrice: 0,
					serviceFee: 0,
					superFastChargeNumber: 0,
					fastChargeNumber: 0,
					slowChargeNumber: 0
				},
				pirceShow: false,
				chargingRules: []
			}
		},
		onLoad(e) {
			
			// 去请求停车场的信息
			if (e.chargeInfo) {
				this.chargingStationInfo = JSON.parse(e.chargeInfo)
				this.getChargingRules(this.chargingStationInfo.id)
				this.getCarParkInfo(JSON.parse(e.chargeInfo).carParkId)
			} else {
				this.parkInfo = uni.getStorageSync("parkInfo");
				this.getCfCarParkChargingRules();
				// this.getEntranceAndExit()
				// 去请求充电站的信息
				if (this.parkInfo.chargingStationId) {
					this.getCharingStationInfo()
				}
			}
		},
		computed: {
			openingHours: function() {
				var parkInfo = this.parkInfo
				return this.$u.timeFormat(parkInfo.startBusinessHours, 'hh:MM:ss') + '-' + this.$u.timeFormat(parkInfo
					.endBusinessHours, 'hh:MM:ss')
			}
		},
		methods: {
			intoCharges(id){
				uni.navigateTo({
					url:'Charges?id=' + id
				})
			},
			getTimeText(time) {
				if (time != null) {
					return dayjs(dayjs().startOf('month').valueOf() + time).format(
						'HH:mm'
					)
				} else {
					return '-'
				}
			},
			async getCarParkInfo(id) {
				var res = await this.$u.api.getListByQuery({
					id,
					page: 1,
					size: 10
				})
				console.log(res);
				if (res.code === 10002) {
					this.parkInfo = res.data[0]
					this.getCfCarParkChargingRules();
				}
			},
			async getCharingStationInfo() {
				var res = await this.$u.api.searchChargingStation({
					id: this.parkInfo.chargingStationId,
				})
				console.log('充电站信息', res);
				if (res.code === 10002) {
					this.chargingStationInfo = res.data[0]
					this.getChargingRules(res.data[0].id)
				}
			},
			// 获取充电站收费规则
			async getChargingRules(id) {
				var res = await this.$u.api.getChargingRules({
					chargingStationId: id
				})
				console.log('获取充电站收费规则', res);
				if (res.code === 10002) {
					// this.chargingRules = res.data
					this.getCurrentTimePrice(res.data)
					
				}
			},
			getCurrentTimePrice(datas){
				var arrs = datas
				var newTime = dayjs().valueOf() - dayjs().startOf('day').valueOf()
				for (let i of arrs) {
					if(newTime > i.startTime && newTime < i.endTime){
						console.log(i);
						i.status = 1
					}else{
						i.status = 0
					}
				}
				this.chargingRules = arrs
			},
			viewAllChargingPort() {
				uni.navigateTo({
					url: '../../chargeModule/pages/chargingPileInfo/index?id=' + this.chargingStationInfo.id +
						'&chargingRules=' + JSON.stringify(this.chargingRules)
				})
			},
			async getEntranceAndExit() {
				var res = await this.$u.api.carparkCheckpointGetListByQuery({
					carParkId: this.parkInfo.id
				})
				console.log(res)
				if (res.code == 10002) {
					this.entranceAndExitList = res.data
				}
			},
			buyMonthlyCard() {
				uni.navigateTo({
					url: '../monthlyCard/packageOptions?info=' + JSON.stringify({
						carParkName: this.parkInfo.name,
						carParkId: this.parkInfo.id
					}) + '&type=renew'
				})
			},
			openEditCar() {
				uni.navigateTo({
					url: "editCar/editCar"
				})
			},
			openAddCar() {
				uni.navigateTo({
					url: "addCar/addCar"
				})
			},
			openApplyForOpenGate() {
				uni.navigateTo({
					url: '../monthlyCard/entranceAndExitList?list=' + encodeURIComponent(JSON.stringify(this
						.entranceAndExitList))
				})
				return
				uni.navigateTo({
					url: "../applyForOpenGate/applyForOpenGate?carParkId=" + this.parkInfo.id
				})
			},
			async getCfCarParkChargingRules() {
				var res = await this.$u.api.getCarParkChargingRules({
					carParkId: this.parkInfo.id
				})
				console.log(res)
				if (res.code == 10002) {
					this.parkInfo.cfCarParkChargingRules = res.data;
				}
			},
		}
	}
</script>

<style lang="less">
	.nocharge {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		margin-top: 118rpx;
		margin-bottom: 150rpx;

		image {
			width: 220rpx;
			height: 108rpx;
		}

		view {
			width: 360rpx;
			height: 32rpx;
			font-size: 30rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #CDCDCD;
			line-height: 32rpx;
			margin-top: 24rpx;
		}
	}

	page {
		background-color: #f8f9fb;
	}

	.scanCode {
		width: 750rpx;
		height: 120rpx;
		background: #FFFFFF;
		position: fixed;
		left: 0;
		bottom: 0;
		justify-content: flex-end;

		.priceTipsBox {
			margin-right: 10rpx;

			&>view:first-child {
				height: 60rpx;
				font-size: 36rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: rgba(50, 50, 51, 0.8);
				line-height: 60rpx;
				text-align: right;
			}

			&>view:last-child {
				height: 28rpx;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.5);
				line-height: 28rpx;
			}
		}

		.scan {
			width: 234rpx;
			height: 88rpx;
			background: #488CF5;
			border-radius: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #FFFFFF;
			line-height: 88rpx;
			text-align: center;
			margin-right: 40rpx;
		}
	}

	.chargeInfo {
		width: 670rpx;
		height: 585rpx;
		background: #FFFFFF;
		border-radius: 16rpx;
		margin-bottom: 150rpx;
		margin-top: 24rpx;

		.remind {
			.left {
				width: 136rpx;
				height: 32rpx;
				background: #FF5151;
				border-radius: 4rpx;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #FFFFFF;
				line-height: 32rpx;
				text-align: center;
				margin: 26rpx 0 0 42rpx;
			}

			.right {
				width: 432rpx;
				height: 56rpx;
				font-size: 20rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.6);
				line-height: 28rpx;
				margin-left: 20rpx;
				margin-top: 14rpx;
			}
		}

		.chargingNum {
			.slow {
				justify-content: space-between;
				padding: 0 40rpx;
				margin-top: 26rpx;

				.left {
					width: 56rpx;
					height: 40rpx;
					font-size: 28rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #323233;
					line-height: 40rpx;
				}

				.right {
					font-size: 28rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: rgba(50, 50, 51, 0.6);
					line-height: 40rpx;
					text-align: right;
				}
			}

			.fast {
				margin-top: 32rpx;
				padding: 0 40rpx;
				justify-content: space-between;

				.left {
					height: 40rpx;
					font-size: 28rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #323233;
					line-height: 40rpx;
				}

				.right {
					height: 40rpx;
					font-size: 28rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: rgba(50, 50, 51, 0.6);
					line-height: 40rpx;
					text-align: right;
				}
			}
		}

		.titleBox {
			justify-content: space-between;

			.leftTitle {
				width: 112rpx;
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 40rpx;
				margin-left: 40rpx;
				margin-top: 32rpx;
			}

			.check {
				width: 144rpx;
				height: 52rpx;
				background: rgba(72, 140, 245, 0.1);
				border-radius: 200rpx;
				font-size: 20rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #488CF5;
				line-height: 52rpx;
				margin-right: 40rpx;
				margin-top: 26rpx;
				text-align: center;
			}
		}
	}

	.buyMonthCard {
		image {
			width: 16rpx;
			height: 28rpx;
			margin: 38rpx 0 0 454rpx;
		}

		.leftTitle {
			margin: 32rpx 0 0 40rpx;
			width: 112rpx;
			height: 40rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 40rpx;
		}
	}

	.line {
		width: 590rpx;
		height: 2rpx;
		background: #EAEAEA;
		margin: 40rpx 0 0 40rpx;
	}

	.charges {
		width: 590rpx;
		background: #FAFAFA;
		border-radius: 10rpx;
		margin: 32rpx 0 0 40rpx;
		padding-bottom: 36rpx;

		.timePirceBox {
			.timePirce {
				padding: 0 20rpx;
				margin-top: 20rpx;
				width: 100%;
				justify-content: space-between;

				.time {
					font-size: 26rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: rgba(50, 50, 51, 0.6);
					line-height: 36rpx;
				}
			}
		}

		.titleBox {
			justify-content: space-between;

			.leftTitle {
				width: 196rpx;
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 40rpx;
				margin-left: 18rpx;
				margin-top: 24rpx;
			}

			.check {
				width: 144rpx;
				height: 52rpx;
				background: rgba(72, 140, 245, 0.1);
				border-radius: 200rpx;
				font-size: 20rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #488CF5;
				line-height: 52rpx;
				text-align: center;
				margin-right: 22rpx;
				margin-top: 18rpx;
			}
		}
	}

	.infoBox {
		position: absolute;
		top: 238rpx;
		left: 40rpx;

		.parkInfo {
			width: 670rpx;
			background: #FFFFFF;
			border-radius: 16rpx;
			padding: 32rpx 0;

			.parkNumberBox {
				margin-top: 32rpx;
				justify-content: space-between;
				padding: 0 40rpx 0 40rpx;

				.left {
					height: 40rpx;
					font-size: 28rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #323233;
					line-height: 40rpx;
				}

				.right {
					height: 40rpx;
					font-size: 28rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: rgba(50, 50, 51, 0.6);
					line-height: 40rpx;
				}
			}

			.title {
				width: 112rpx;
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 40rpx;
				margin: 0 0 0 40rpx;
			}
		}

	}

	.nameBox {
		background-image: url('http://file.cfeng.wang/parkInfoBG2x.png');
		width: 750rpx;
		height: 288rpx;
		background-size: 100% 100%;
		overflow: hidden;
		position: relative;

		.navIcon {
			width: 160rpx;
			height: 160rpx;
			position: absolute;
			right: 48rpx;
			bottom: 0;
			z-index: 1;
		}

		.name {
			width: 670rpx;
			height: 40rpx;
			font-size: 40rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #FFFFFF;
			line-height: 40rpx;
			margin: 58rpx 0 0 40rpx;
		}

		.addressBox {
			margin-top: 20rpx;

			.icon {
				margin-left: 46rpx;
			}

			.address {
				margin-left: 8rpx;
				width: 463rpx;
				height: 40rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #FFFFFF;
				line-height: 40rpx;
			}
		}
	}

	.timeBox {
		width: 750rpx;
		height: 112rpx;
		background: #FFFFFF;
		border-bottom: 2rpx solid #e5e5e5;

		.left {
			width: 136rpx;
			height: 48rpx;
			font-size: 34rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 48rpx;
			margin-left: 40rpx;
		}

		.right {
			width: 502rpx;
			height: 48rpx;
			font-size: 34rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.6);
			line-height: 48rpx;
			margin-left: 32rpx;
			text-align: right;
		}
	}

	.popupBox {
		height: 690rpx;

		.pirceItem {
			width: 670rpx;
			height: 136rpx;
			background: #F8F9FB;
			border-radius: 12rpx;
			border: 2rpx solid rgba(151, 151, 151, 0.08);
			margin-left: 42rpx;
			display: flex;
			justify-content: space-between;
			margin-bottom: 40rpx;

			.pirce {
				align-items: flex-end;
				margin: 28rpx 30rpx 0 0;

				.pirce1 {
					width: 130rpx;
					height: 50rpx;
					font-size: 36rpx;
					font-family: PingFangSC-Medium, PingFang SC;
					font-weight: 500;
					color: rgba(50, 50, 51, 0.8);
					line-height: 50rpx;
				}

				.pirce2 {
					width: 266rpx;
					height: 32rpx;
					font-size: 24rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #323233;
					line-height: 32rpx;
					text-align: right;
				}
			}

			.time {
				height: 50rpx;
				font-size: 36rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: rgba(50, 50, 51, 0.8);
				line-height: 50rpx;
				margin: 45rpx 0 0 32rpx;
			}
		}

		.title {
			width: 176rpx;
			height: 60rpx;
			font-size: 44rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 60rpx;
			margin: 40rpx;
		}
	}
</style>
