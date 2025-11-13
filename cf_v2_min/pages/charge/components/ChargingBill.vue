<template>
	<u-popup v-model="show" mode="bottom" style="" border-radius="48">
		<view style="padding: 50rpx 0 20rpx 0;">
			<view class="u-rela" style="width: 100%;height: 100%;">
				<image class="bgImage" src="http://file.cfeng.wang/bigLogo.png" style="position: absolute;z-index: -1;">
				</image>
				<view class="Box" style="margin-bottom: 20rpx;" v-for="item in chargingList" :key='item.id'
					@click="intoDetail(item)">
					<view class="left"
						:style="carType === 'bike' ? 'justify-content: center;padding-bottom: 20rpx;' : ''">
						<view>充电中…</view>
						<view>
							四轮 | {{getPercent(item.chargingedKwh,item.planChargingKwh)}}预计还需要{{getTime(item.planChargingKwh,item.chargingPower,item.chargingedKwh)}}小时完成
						</view>
					</view>
					<view class="right">
						查看订单
					</view>
				</view>
			</view>
			<view class="u-rela" style="width: 100%;height: 100%;">
				<image class="bgImage" src="http://file.cfeng.wang/bigLogo.png" style="position: absolute;z-index: -1;">
				</image>
				<view class="Box" style="margin-bottom: 20rpx;" v-for="item in chargingBikeList" :key='item.id'
					@click="intoBikeDetail(item)">
					<view class="left"
						:style="carType === 'bike' ? 'justify-content: center;padding-bottom: 20rpx;' : ''">
						<view>充电中…</view>
						<view>两轮 | {{getBikePercent(item.planChargingTime,item.startTime)}}预计还需要{{getBikeTime(item.planChargingTime,item.startTime)}}小时完成</view>
					</view>
					<view class="right">
						查看订单
					</view>
				</view>
			</view>
		</view>
	</u-popup>
</template>

<script>
	const dayjs = require('dayjs')
	export default {
		props: {
			show: {
				type: Boolean,
				default: false
			},
			chargingList: {
				type: Array,
				default: []
			},
			chargingBikeList: {
				type: Array,
				default: []
			},
		},
		methods: {
			getBikePercent(planChargingTime,startTime){
				const newTime = dayjs().valueOf()
				return ((newTime - startTime) / planChargingTime).toFixed(2) * 100 + '%'
			},
			getBikeTime(planChargingTime,startTime){
				const newTime = dayjs().valueOf()
				return ((planChargingTime - newTime + startTime) / 3600000).toFixed(1)
			},
			intoBikeDetail() {
				uni.navigateTo({
					url: '/chargeModule/pages/chargingRecord/index'
				})
			},
			getTime(planChargingKwh, chargingPower, chargingedKwh) {
				return ((planChargingKwh - chargingedKwh) / (chargingPower / 1000)).toFixed(1)
				// （（计划 - 已充） / 功率） * 60
			},
			intoDetail(e) {
				uni.setStorageSync('charingId', e.id)
				uni.navigateTo({
					url: '/chargeModule/pages/charging/index'
				})
			},
			getPercent(num, total) {
					num = parseFloat(num);
					total = parseFloat(total);
					if (isNaN(num) || isNaN(total)) {
						return "-";
					}
					return total <= 0 ? "0%" : (Math.round(num / total * 100) + '%');
			},
			// getTime(val1, val2, val3) {
			//     num price = 0;
			//     num time = 0;
			//     price = num.parse(val2) - num.parse(val1);
			//     time=(price/val3)*60;
			//     return Utils.formatNum(time.toString(),pos: 0);
			//   }
		}
	}
</script>

<style lang="scss">
	.bgImage {
		width: 344rpx;
		height: 434rpx;
		bottom: 0;
		left: 0;
	}

	.Box {
		width: 670rpx;
		height: 190rpx;
		background: rgba(72, 140, 245, 0.1);
		border-radius: 12rpx;
		margin-left: 40rpx;
		display: flex;
		justify-content: space-between;

		.left {
			display: flex;
			flex-direction: column;
			margin-left: 20rpx;

			&>view:first-child {
				height: 60rpx;
				font-size: 44rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 60rpx;
				margin-top: 40rpx;
			}

			&>view:last-child {
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(72, 140, 245, 0.8);
				line-height: 40rpx;
				margin-top: 10rpx;
			}
		}

		.right {
			width: 168rpx;
			height: 60rpx;
			background: #488CF5;
			border-radius: 200rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #FFFFFF;
			line-height: 60rpx;
			text-align: center;
			margin: 66rpx 40rpx 0 0;
		}
	}
</style>
