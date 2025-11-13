<template>
	<view>
		<view style="width: 750rpx;height: 104rpx;background: #FFFFFF;">
			<scroll-view scroll-x style="white-space: nowrap;display:inline-block;width: 710rpx;margin-left: 20rpx;" >
				<view class="nav u-flex">
					<view class="superfast" @click="changeIndex(index)" v-for="(item,index) in list" :key='item.id'>
						<view :class="current === index ? 'title' : 'noChoose'">{{item.name}}</view>
						<view class="line" v-if="index === current"></view>
					</view>
				</view>
			</scroll-view>
		</view>
		<!-- <scroll-view scroll-x style="white-space: nowrap;display:inline-block;background: #FFFFFF;padding-right: 10rpx;" >
			<view class="nav u-flex">
				<view class="superfast" @click="changeIndex(index)" v-for="(item,index) in tabList" :key='item'>
					<view :class="current === index ? 'title' : 'noChoose'">{{item.name}}</view>
					<view class="line" v-if="index === current"></view>
				</view>
			</view>
		</scroll-view> -->

		<view class="u-flex-col u-col-center" v-if="!chargingRules.length">
			<image src="../../static/image/my/Default.png" style="width: 220rpx;height: 108rpx;margin: 326rpx 0 26rpx 0;">
			</image>
			<view style="font-size: 30rpx;font-family: PingFangSC-Regular, PingFang SC;color: #CDCDCD;">该车辆类型没有收费规则哦
			</view>
		</view>
		
		<view class="ChargingInfo u-flex u-row-between" v-for="item in chargingRules" :key='item.id'>
			<view class="time_pirce" style="margin-left: 32rpx;">{{(!item.startTime ? '00:00' : $u.timeFormat(item.startTime, 'hh:MM')) + '-' + (!item.endTime ? '00:00' : $u.timeFormat(item.endTime, 'hh:MM'))}}</view>
			<view class="time_pirce" style="margin-right: 30rpx;">{{item.type === 'unit_of_time' ? `¥${item.fee}/时` : `¥${item.fee}/次`}}</view>
		</view>
		
		

	</view>
</template>

<script>
	export default {
		data() {
			return {
				index: 3,
				list: [],
				power: 0,
				chargingRules: [],
				current:0,
				carParkId:''
			}
		},
		onLoad(e) {
			this.carParkId = e.id
			this.getList()
		},
		onShow() {
			this.show = false
		},
		computed: {
			defaultShow: function() {
				return !this.list.some(i => i.powerType == this.index)
			}
		},
		methods: {
			async getList() {
				var res = await this.$u.api.getCarparkCarType()
				console.log(res);
				if (res.code === 10002) {
					this.list = res.data
					this.getCarParkChargingRules(this.carParkId,res.data[0].flagKey)
				}
			},
			async getCarParkChargingRules(carParkId,carType){
				var res = await this.$u.api.getCarparkChargingRules2({
					carParkId,
					carType,
					page:1,
					size:100
				})
				console.log(res);
				this.chargingRules = res.data
			},
			changeIndex(index) {
				this.current = index
				this.getCarParkChargingRules(this.carParkId,this.list[index].flagKey)
			}
		}
	}
</script>

<style lang="less">
	page {
		background: #F5F5F5;
	}
	.ChargingInfo{
		width: 670rpx;
		height: 136rpx;
		background: #F8F9FB;
		border-radius: 12rpx;
		border: 2rpx solid rgba(151, 151, 151, 0.08);
		margin: 40rpx 0 0 40rpx;
		.time_pirce{
			font-size: 36rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: rgba(50, 50, 51, 0.8);
			line-height: 50rpx;
		}
	} 
	.popupBox {
		.title {
			width: 220rpx;
			height: 60rpx;
			font-size: 44rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 60rpx;
			margin: 66rpx 0 30rpx 40rpx;
		}

		.infoBox {
			width: 670rpx;
			height: 136rpx;
			background: #F8F9FB;
			border-radius: 12rpx;
			border: 2rpx solid rgba(151, 151, 151, 0.08);
			position: relative;
			margin: 0 0 40rpx 40rpx;

			.title1 {
				height: 50rpx;
				font-size: 36rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: rgba(50, 50, 51, 0.8);
				line-height: 50rpx;
				margin: 28rpx 0 0 32rpx;
			}

			.infoitem1 {
				height: 32rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 32rpx;
				margin-top: 6rpx;
				margin-left: 32rpx;
			}

			.startUp1 {
				width: 144rpx;
				height: 56rpx;
				background: #488CF5;
				border-radius: 44rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #FFFFFF;
				line-height: 56rpx;
				text-align: center;
				position: absolute;
				right: 32rpx;
				top: 40rpx;
			}
		}
	}

	.info {
		width: 670rpx;
		height: 156rpx;
		background: #FFFFFF;
		border-radius: 8rpx;
		border: 2rpx solid #EFF0F2;
		margin: 40rpx 0 0 40rpx;
		padding-left: 32rpx;
		position: relative;

		.startUp {
			width: 160rpx;
			height: 72rpx;
			background: #579BF6;
			border-radius: 36rpx;
			position: absolute;
			top: 40rpx;
			right: 32rpx;
			display: flex;
			justify-content: center;
			align-items: center;

			image {
				width: 32rpx;
				height: 32rpx;
			}

			&>view:last-child {
				height: 32rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #FFFFFF;
				line-height: 32rpx;
			}
		}

		.title {
			height: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 44rpx;
			margin-top: 32rpx;
		}

		.infoitem {
			margin-top: 18rpx;
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.6);
			line-height: 24rpx;
		}
	}

	.nav {
		height: 104rpx;
		.superfast {
			width: 164rpx;
			height: 100%;
			display: flex;
			justify-content: center;
			align-items: center;
			// margin-left: 40rpx;
			margin-right: 40rpx;
			position: relative;
			.title {
				height: 88rpx;
				font-size: 34rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #323233;
				line-height: 88rpx;
			}

			.line {
				width: 100%;
				height: 8rpx;
				background: #323233;
				border-radius: 4rpx;
				position: absolute;
				left: 0;
				bottom: 0;
			}
		}

		.fast {
			width: 104rpx;
			height: 100%;
			display: flex;
			justify-content: center;
			align-items: center;
			margin-left: 40rpx;
			position: relative;

			.title {
				width: 70rpx;
				height: 88rpx;
				font-size: 34rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #323233;
				line-height: 88rpx;
			}

			.line {
				width: 104rpx;
				height: 8rpx;
				background: #323233;
				border-radius: 4rpx;
				position: absolute;
				left: 0;
				bottom: 0;
			}
		}

		.slow {
			width: 104rpx;
			height: 100%;
			margin-left: 40rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			position: relative;

			.title {
				width: 70rpx;
				height: 88rpx;
				font-size: 34rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #323233;
				line-height: 88rpx;
			}

			.line {
				width: 104rpx;
				height: 8rpx;
				background: #323233;
				border-radius: 4rpx;
				position: absolute;
				left: 0;
				bottom: 0;
			}
		}
	}

	.noChoose {
		height: 88rpx;
		font-size: 28rpx !important;
		font-family: PingFangSC-Semibold, PingFang SC;
		font-weight: 600;
		color: rgba(50, 50, 51, 0.5) !important;
		line-height: 88rpx;
	}
</style>
