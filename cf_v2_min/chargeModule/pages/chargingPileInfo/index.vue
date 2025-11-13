<template>
	<view>
		<view class="nav u-flex">
			<view class="superfast" @click="changeIndex(3)">
				<view :class="index === 3 ? 'title' : 'noChoose'">特快充</view>
				<view class="line" v-if="index === 3"></view>
			</view>
			<view class="fast" @click="changeIndex(2)">
				<view :class="index === 2 ? 'title' : 'noChoose'">快充</view>
				<view class="line" v-if="index === 2"></view>
			</view>
			<view class="slow" @click="changeIndex(1)">
				<view :class="index === 1 ? 'title' : 'noChoose'">慢充</view>
				<view class="line" v-if="index === 1"></view>
			</view>
		</view>

		<view class="info u-flex-col" v-for="item in list" :key='item.id' v-if="item.powerType === index">
			<view class="title">{{item.aliasName}}</view>
			<view class="infoitem">功率 | {{item.power}}W</view>
			<view class="startUp" @click="searchPort(item.id,item.power)">
				<image src="../../../static/image/my/lightning.png" ></image>
				<view>启动</view>
			</view>
		</view>

		<view class="u-flex-col u-col-center" v-if="defaultShow">
			<image src="../../static/default.png" style="width: 220rpx;height: 108rpx;margin: 326rpx 0 26rpx 0;">
			</image>
			<view style="font-size: 30rpx;font-family: PingFangSC-Regular, PingFang SC;color: #CDCDCD;">该车场还未铺设该充电设备哦
			</view>
		</view>

		<u-popup v-model="show" mode="bottom" border-radius='30'>
			<view class="popupBox">
				<view class="title">选择充电枪</view>
				<view class="infoBox u-flex-col" v-for="item in chargingPortList" :key='item.id'>
					<view class="title1">{{item.portNumber}}号枪</view>
					<view class="infoitem1">功率 | {{power}}W</view>
					<view class="startUp1" @click="intoPay(item)" v-if="item.status === 0">
						立即启动
					</view>
					<view class="startUp1" @click="intoPay(item)" v-if="item.status === 1">
						已连接
					</view>
					<view class="startUp1" v-if="item.status === 2" style="color: #FF5151;background: #FFEDED;">
						正在充电
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				index: 3,
				list: [],
				show: false,
				chargingPortList: [],
				power: 0,
				chargingRules: []
			}
		},
		onLoad(e) {
			if(e.chargingRules){
				this.chargingRules = JSON.parse(e.chargingRules)
			}
			this.chargingPileId = e.id
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
			intoCharge(item){
				uni.navigateTo({
					url:'../charging/index?datas=' + JSON.stringify(item) 
				})
			},
			intoPay(item) {
				uni.navigateTo({
					url: '../buyingPower/index?chargingPort=' + JSON.stringify(item) + '&chargingRules=' +
						JSON.stringify(this.chargingRules)
				})
			},
			async searchPort(id, power) {
				console.log(power);
				uni.showLoading({
					title: '请稍等',
					icon: 'none'
				})
				var res = await this.$u.api.searchchargingPort({
					page: 1,
					size: 20,
					chargingDeviceId: id
				})
				uni.hideLoading()
				if (res.code === 10002) {
					this.chargingPortList = res.data
					this.show = true
					this.power = power
				}
			},
			async getList() {
				var res = await this.$u.api.searchChargingStationDevice({
					chargingStationId: this.chargingPileId
				})
				console.log(res);
				if (res.code === 10002) {
					this.list = res.data
				}
			},
			changeIndex(index) {
				this.index = index
			}
		}
	}
</script>

<style lang="less">
	page {
		background: #F5F5F5;
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
		width: 750rpx;
		height: 104rpx;
		background: #FFFFFF;

		.superfast {
			width: 164rpx;
			height: 100%;
			display: flex;
			justify-content: center;
			align-items: center;
			margin-left: 40rpx;
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
