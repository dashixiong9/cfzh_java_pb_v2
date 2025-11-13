<template>
	<div v-if='info'>
		<view class="paymentSuccessBox u-flex-col u-col-center u-row-center">
			<image src="../../static/paymentSuccess.png" ></image>
			<view class="content">订单已完成</view>
		</view>

		<view class="infoBox">
			<view class="title">使用详情</view>
			<view class="u-flex infoitem">
				<view class="left">订单号</view>
				<view class="right">{{info.cfOrder.id}}</view>
			</view>
			<view class="u-flex infoitem">
				<view class="left">充电地点</view>
				<view class="right">{{info.cfChargingStation.stationName}}</view>
			</view>
			<view class="u-flex infoitem">
				<view class="left">开始时间</view>
				<view class="right">{{$u.timeFormat(info.startTime,'yyyy-mm-dd hh:MM:ss')}}</view>
			</view>
			<view class="u-flex infoitem">
				<view class="left">结束时间</view>
				<view class="right">{{$u.timeFormat(info.endTime,'yyyy-mm-dd hh:MM:ss')}}</view>
			</view>
			<view class="u-flex infoitem">
				<view class="left">电桩编号</view>
				<view class="right">{{info.chargingDeviceId}}</view>
			</view>
			<view class="line"></view>
			<view class="title">使用详情</view>
			<view class="u-flex infoitem">
				<view class="left">充电度数</view>
				<view class="right">{{info.chargingedKwh}}度</view>
			</view>
			<view class="u-flex infoitem">
				<view class="left">电价（{{info.chargingPrice}}元/度）</view>
				<view class="right">¥{{info.chargingFee}}</view>
			</view>
			<view class="u-flex infoitem">
				<view class="left">服务费（{{info.servicePrice}}元/度）</view>
				<view class="right">¥{{info.serviceFee}}</view>
			</view>
			<view class="u-flex infoitem">
				<view class="left">退款</view>
				<view class="right">¥{{(info.cfOrder.refundAmount).toFixed(0)}}</view>
			</view>
			<view class="u-flex actualIncome">
				<view class="title1">实收：</view>
				<view class="title2">¥</view>
				<view class="pirce">{{info.serviceFee + info.chargingFee}}</view>
			</view>
		</view>
		<!-- <image src="../../static/receive.png" class="receive"  @click="intoReceiveParkingTicket"></image> -->
	</div>
</template>

<script>
	export default {
		data(){
			return {
				info:{}
			}
		},
		onLoad(e) {
			this.getInfo(e.id)
		},
		methods:{
			intoReceiveParkingTicket(){
				uni.navigateTo({
					url:'../receiveParkingTicket/index'
				})
			},
			async getInfo(id){
				uni.showLoading()
				var { code, message, data } = await this.$u.api.getChargingOrderByUesLogId({
					id
				})
				uni.hideLoading()
				if(code === 10002){
					this.info = data
				}
				// if(code === 10002){
				// 	this.list = data
				// }
			}
		}
	}
</script>

<style lang="scss">
	page {
		background: #F5F5F5;
		padding-bottom: 260rpx;
	}
	.receive{
		position: fixed;
		width: 630rpx;
		height: 200rpx;
		left: 60rpx;
		bottom: 0;
	}
	.infoBox {
		width: 670rpx;
		height: 1000rpx;
		background: #FFFFFF;
		border-radius: 20rpx 20rpx 24rpx 24rpx;
		margin: 38rpx 0 0 40rpx;
		overflow: auto;
		.actualIncome{
			justify-content: flex-end;
			align-items: flex-end;
			margin-top: 40rpx;
			.title1{
				height: 44rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: rgba(50, 50, 51, 0.8);
				line-height: 44rpx;
			}
			.title2{
				height: 34rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: rgba(50, 50, 51, 0.8);
				line-height: 34rpx;
			}
			.pirce{
				height: 60rpx;
				font-size: 60rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: rgba(50, 50, 51, 0.8);
				line-height: 60rpx;
				margin-right: 40rpx;
			}
		}
		.line {
			width: 590rpx;
			height: 2rpx;
			background: #EAEAEA;
			margin: 34rpx 0 0 40rpx;
		}

		.infoitem {
			justify-content: space-between;
			margin-top: 32rpx;

			.left {
				margin-left: 40rpx;
				height: 44rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.4);
				line-height: 44rpx;
			}

			.right {
				margin-right: 40rpx;
				height: 44rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 44rpx;
			}
		}

		.title {
			width: 128rpx;
			height: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: rgba(50, 50, 51, 0.8);
			line-height: 44rpx;
			margin: 34rpx 0 0 40rpx;
		}
	}

	.paymentSuccessBox {
		width: 670rpx;
		height: 229rpx;
		background: #FFFFFF;
		border-radius: 16rpx;
		margin: 40rpx 0 0 40rpx;

		image {
			width: 100rpx;
			height: 100rpx;
		}

		.content {
			width: 200rpx;
			height: 40rpx;
			font-size: 40rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #488CF5;
			line-height: 40rpx;
			margin-top: 16rpx;
		}
	}
</style>
