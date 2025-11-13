<template>
	<view>
		<view class="box u-flex-col">
			<view class="u-flex itemBox mt24">
				<view class="left">车牌号</view>
				<view class="right">{{info.cfCarParkUseLog.numberPlate || '-'}}</view>
			</view>
			<view class="u-flex itemBox mt16">
				<view class="left">停车场</view>
				<view class="right">{{info.cfCarPark.name || '-'}}</view>
			</view>
			<view class="u-flex itemBox mt16">
				<view class="left">订单号</view>
				<view class="right">{{info.cfOrder == null ? '-' : info.cfOrder.id}}</view>
			</view>
			<view class="u-flex itemBox mt16">
				<view class="left">停车时长</view>
				<view class="right">{{vuex_direction == 'out' ? newOutTime : '-'}}</view>
			</view>
			<view class="u-flex itemBox mt16">
				<view class="left">进场时间</view>
				<view class="right">
					{{info.cfCarParkUseLog.inTime ? $u.timeFormat(info.cfCarParkUseLog.inTime, 'yyyy-mm-dd hh:MM:ss') : '-'}}
				</view>
			</view>
			<view class="u-flex itemBox mt16">
				<view class="left">出场时间</view>
				<view class="right" v-if="vuex_direction == 'in'">
					-
				</view>
				<view class="right" v-else>
					{{$u.timeFormat(info.cfCarParkUseLog.outTime ? info.cfCarParkUseLog.outTime : new Date(), 'yyyy-mm-dd hh:MM:ss')}}
				</view>
			</view>
			<view class="u-flex itemBox mt16">
				<view class="left">应付金额</view>
				<view class="right" v-if="info.cfOrder.amountsPayable">¥{{info.cfOrder.amountsPayable}}元</view>
				<view class="right" v-else>-</view>
			</view>
			<view class="u-flex itemBox mt16">
				<view class="left">优惠金额</view>
				<view class="right" v-if="info.cfOrder.couponPaid">¥{{info.cfOrder.couponPaid}}元</view>
				<view class="right" v-else>-</view>
			</view>
			<!-- <view class="u-flex itemBox mt16">
				<view class="left">实付金额</view>
				<view class="right">-</view>
			</view> -->
			<view class="u-flex itemBox mt16">
				<view class="left">入场闸口</view>
				<view class="right">{{info.inCheckpoint.name || '-'}}</view>
			</view>
		</view>
		<view class="orderButton" @click="back" v-if="info.qrCode == null">完成</view>
		<view class="orderButton" @click="popupShow = true" v-else>生成二维码</view>
		<u-picker @confirm='handleConfirm' confirm-color="#488cf5" cancel-color="rgba(50, 50, 51, 0.5);" mode="selector"
			v-model="show" :default-selector="[1]" :range="selector"></u-picker>
		<u-popup v-model="popupShow" mode="bottom" border-radius="48" height='716rpx'>
			<view class="u-flex-col popupShowBox">
				<view class="title">扫码付费</view>
				<view class="subTitle">请扫二维码支付停车费</view>
				<image :src="'data:image/png;base64,' + info.qrCode"></image>
			</view>
		</u-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				show: false,
				selector: ['24小时', '30天'],
				info: {
					cfCarParkUseLog: {
						inTime: ''
					}
				},
				newOutTime: '',
				popupShow: false
			}
		},
		methods: {
			handleConfirm(e) {
				console.log(e);
			},
			back() {
				uni.navigateBack();
			}
		},
		onLoad(e) {
			console.log(e);
			var datas = JSON.parse(decodeURIComponent(e.datas))
			this.info = datas
		},
		watch: {
			info: function(e) {
				var inTime = this.info.cfCarParkUseLog.inTime
				var outTime = this.info.cfCarParkUseLog.outTime ? this.info.cfCarParkUseLog.outTime : new Date()
				// if (!outTime) {
				// 	this.newOutTime = '-'
				// 	return
				// }
				console.log(this.$u.timeFormat(inTime, 'yyyy年mm月dd日 hh时MM分ss秒'));
				console.log(this.$u.timeFormat(outTime, 'yyyy年mm月dd日 hh时MM分ss秒'));
				var times = new Date(outTime).getTime() - new Date(inTime).getTime()
				var difftime = times / 1000
				var days = parseInt(difftime / 86400); // 天  24*60*60*1000 
				var hours = parseInt(difftime / 3600) - 24 * days; // 小时 60*60 总小时数-过去的小时数=现在的小时数 
				var minutes = parseInt(difftime % 3600 / 60); // 分钟 -(day*24) 以60秒为一整份 取余 剩下秒数 秒数/60 就是分钟数
				var seconds = parseInt(difftime % 60); // 以60秒为一整份 取余 剩下秒数
				console.log(123, hours);
				this.newOutTime = (days ? (days + '天') : '') + hours + '小时' + minutes + '分钟' + seconds + '秒'
			}
		},
	}
</script>

<style lang="scss">
	page {
		background: #F8F9FB;
	}

	.orderButton {
		width: 670rpx;
		height: 100rpx;
		background: #488CF5;
		border-radius: 16rpx;
		font-size: 36rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 100rpx;
		text-align: center;
		margin: 284rpx 0 0 40rpx;
	}

	.box {
		width: 686rpx;
		height: 640rpx;
		background: #FFFFFF;
		border-radius: 12rpx;
		margin: 32rpx 0 0 32rpx;

		.itemBox {
			justify-content: space-between;

			.left {
				height: 32rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 32rpx;
				margin-left: 20rpx;
			}

			.right {
				height: 32rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 32rpx;
				margin-right: 20rpx;
			}
		}
	}

	.mt24 {
		margin-top: 48rpx;
	}

	.mt16 {
		margin-top: 32rpx;
	}

	.popupShowBox {
		width: 750rpx;
		.title {
			font-family: PingFangSC-Semibold;
			font-size: 44rpx;
			color: #323233;
			letter-spacing: 0;
			margin: 64rpx 0 0 40rpx;
		}

		.subTitle {
			opacity: 0.34;
			font-family: PingFangSC-Regular;
			font-size: 24rpx;
			color: #323233;
			letter-spacing: 0;
			margin: 4rpx 0 0 40rpx;
		}

		image {
			width: 360rpx;
			height: 360rpx;
			margin: 84rpx 0 0 196rpx;
		}
	}
</style>
