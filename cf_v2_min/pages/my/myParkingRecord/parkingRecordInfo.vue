<template>
	<view>
		<view class="basicInfoBox">
			<view class="title">
				基本信息
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">停车场</view>
				<view class="right">{{info.cfCarPark.name}}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">停车时长</view>
				<view class="right">{{getTimeFn(info.cfCarParkUseLog.inTime, info.cfCarParkUseLog.outTime) }}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">应付金额</view>
				<view class="right">¥{{info.cfOrder.amountsPayable || 0}}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">停车券优惠</view>
				<view class="right">¥{{info.cfOrder.couponPaid || 0}}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">其他折扣</view>
				<view class="right">¥0</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">实付金额</view>
				<view class="right">¥{{info.cfOrder.amountActuallyPaid || 0}}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">支付方式</view>
				<view class="right">{{getPayName(info.cfOrder.paymentAgencyShortName)}}</view>
			</view>
		</view>

		<view class="basicInfoBox" style="height:280rpx ;">
			<view class="title">
				入场信息
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">入场时间</view>
				<view class="right">{{$u.timeFormat(info.cfCarParkUseLog.inTime, 'yyyy-mm-dd hh:MM:ss')}}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">入场关口</view>
				<view class="right">{{info.inCheckpoint.positionDescribe}}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">入场放行方式</view>
				<view class="right">{{getCarParkReleaseType(info.cfCarParkUseLog.inReleaseType)}}</view>
			</view>
		</view>

		<view class="basicInfoBox" style="height:280rpx ;">
			<view class="title">
				离场信息
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">离场时间</view>
				<view class="right">{{$u.timeFormat(info.cfCarParkUseLog.outTime, 'yyyy-mm-dd hh:MM:ss')}}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">离场关口</view>
				<view class="right">{{info.outCheckpoint.positionDescribe}}</view>
			</view>
			<view class="u-flex u-row-between item">
				<view class="left">离场放行方式</view>
				<view class="right">{{getCarParkReleaseType(info.cfCarParkUseLog.outReleaseType)}}</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				info: {
					cfCarParkUseLog: {},
					cfOrder: {},
					outCheckpoint: {},
					inCheckpoint: {}
				},
				carParkReleaseType: [{
						key: 0,
						name: "未知"
					},
					{
						key: 1,
						name: "自动抬杆"
					},
					{
						key: 2,
						name: "人工抬杆"
					},
					{
						key: 3,
						name: "手机放行"
					},
				]
			}
		},
		onLoad(e) {
			// this.info = JSON.parse(e.info)
			this.queryOrderByUseId(e.id)
		},
		methods: {
			getCarParkReleaseType(key) {
				if(this.carParkReleaseType.filter(i => i.key == key)[0]){
					return this.carParkReleaseType.filter(i => i.key == key)[0].name
				}
			},
			async queryOrderByUseId(id) {
				var {
					data,
					code,
					message
				} = await this.$u.api.queryOrderByUseId({
					id
				})
				if (code === 10002) {
					this.info = data
				} else {
					uni.showToast({
						title: message
					})
					setTimeout(() => {
						uni.navigateBack({
							delta: 1
						})
					}, 1500)
				}
			},
			getPayName(value) {
				switch (value) {
					case 'system_free_time':
						return '系统免费'
						break;
					case 'wei_xin_pay_cny':
						return '微信小程序'
						break;
					case 'ali_pay_cny':
						return '支付宝'
						break;
					case 'coupon':
						return '优惠券'
						break;
					case 'package':
						return '套餐'
						break;
					case 'wei_xin_h5_pay_cny':
						return '微信h5'
						break;
					case 'ali_h5_pay_cny':
						return '支付宝h5'
						break;
					case 'cash_pay_cny':
						return '现金支付'
						break;
					case 'ccb_aggregate_payment_cny':
						return '建行聚合支付'
						break;
					case 'ccb_Password_free_payment_cny':
						return '建行无感支付'
						break;
					case 'abnormal_termination_of_order':
						return '异常订单'
						break;
					case 'system_fund_account':
						return '系统资金账户'
						break;
					default:
						return ''
				}
			},
			// 计算相差时间
			getTimeFn(inTime, outTime) {
				//di作为一个变量传进来
				//如果时间格式是正确的，那下面这一步转化时间格式就可以不用了
				var dateBegin = new Date(inTime); //将-转化为/，使用new Date
				var dateEnd = new Date(outTime); //获取当前时间
				var dateDiff = dateEnd.getTime() - dateBegin.getTime(); //时间差的毫秒数
				var dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000)); //计算出相差天数
				var leave1 = dateDiff % (24 * 3600 * 1000); //计算天数后剩余的毫秒数
				var hours = Math.floor(leave1 / (3600 * 1000)); //计算出小时数
				//计算相差分钟数
				var leave2 = leave1 % (3600 * 1000); //计算小时数后剩余的毫秒数
				var minutes = Math.floor(leave2 / (60 * 1000)); //计算相差分钟数
				//计算相差秒数
				var leave3 = leave2 % (60 * 1000); //计算分钟数后剩余的毫秒数
				var seconds = Math.round(leave3 / 1000);
				// hours += dayDiff * 24;
				var dealWith = function(nums) {
					if (nums < 10) {
						return "0" + nums;
					} else {
						return nums;
					}
				};
				// console.log([dayDiff, hours, minutes, seconds]);
				if (dayDiff.toString() == "NaN") {
					return "-";
				} else {
					console.log(hours);
					return (
						dealWith(dayDiff) +
						"天" +
						dealWith(hours) +
						"时" +
						dealWith(minutes) +
						"分" +
						dealWith(seconds) +
						"秒"
					);
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background: #F8F9FB;
	}

	.basicInfoBox {
		width: 670rpx;
		height: 538rpx;
		background: #FFFFFF;
		border-radius: 12rpx;
		margin: 40rpx 0 0 40rpx;
		overflow: hidden;

		.item {
			margin-bottom: 32rpx;

			.left {
				height: 32rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 32rpx;
				margin-left: 40rpx;
			}

			.right {
				height: 32rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 32rpx;
				margin-right: 40rpx;
			}
		}

		.title {
			height: 32rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 32rpx;
			margin: 32rpx 0 26rpx 40rpx;
		}
	}
</style>
