<template>
	<view>
		<view class="box u-flex-c u-rela">
			<view class="amount" v-if="couponDetail.couponType == 1">{{couponDetail.denomination}}次</view>
			<view class="amount" v-if="couponDetail.couponType == 2">{{couponDetail.denomination}}元</view>
			<view class="amount" v-if="couponDetail.couponType == 3">{{(couponDetail.denomination / 3600000)}}小时</view>

			<view class="name">停车场停车优惠券</view>
			<view class="timeLimit">
				有效期限：{{$u.timeFormat(couponDetail.effectiveTime, 'yyyy年mm月dd日')}}-{{$u.timeFormat(couponDetail.expireTime, 'yyyy年mm月dd日')}}
			</view>
			<view class="modeOfUse">使用方式：自动抵扣</view>
			<view class="u-flex-r" v-if="range.length > 0">
				<view class="Rules" style="width:140rpx;">使用范围：</view>
				<view class="Rules" style="width:466rpx;margin-left: 0;">{{rangeText}}</view>
			</view>
			<!-- <view class="Range">使用范围：{{rangeText}}</view> -->
			<view class="u-flex-r">
				<view class="Rules" style="width:140rpx;">使用规则：</view>
				<text class="Rules" style="width:466rpx;margin-left: 0;">1.优惠券仅限在有效期内使用，过期则无法使用；
					2.少量特殊商品不能使用优惠券（有些优惠券标注全场通用则可以使用）；
					3.一张订单只能使用一张优惠券，优惠券不可合并；</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				couponDetail: '',
				range: [],
				rangeTextTwo: '',
				staffId: '',
				qrCodeId: '',
			}
		},
		computed: {
			rangeText: function() {
				if (this.range == null) {
					return []
				}
				return this.range.map(i => i.name).join()
			}
		},
		onLoad(e) {
			this.couponDetail = JSON.parse(e.item)
			this.getDeatil(JSON.parse(e.item).id)
		},
		methods: {
			calculationTime(s) {
				var times = s / (24 * 3600 * 1000)
				return times + '天'
			},
			async getDeatil(couponId) {
				var result = await this.$u.api.selectContainDistanceListByCouponId({
					couponId
				})
				this.range = result.data
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
	}

	.box {
		width: 686rpx;
		// height: 892rpx;
		background: #FFFFFF;
		margin: 32rpx;
		border-radius: 10rpx;

		.amount {
			height: 100rpx;
			font-size: 72rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #DEB19B;
			line-height: 100rpx;
			align-self: center;
			margin: 48rpx 0 20rpx 0;
		}

		.name {
			height: 50rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 50rpx;
			align-self: center;
			margin-bottom: 48rpx;
		}

		.timeLimit {
			width: 606rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 40rpx;
			margin-left: 40rpx;
			margin-bottom: 24rpx;
		}

		.Range {
			@extend .timeLimit;
		}

		.Rules {
			@extend .timeLimit;
		}

		.modeOfUse {
			@extend .timeLimit;
		}
	}

	.button {
		width: 558rpx;
		height: 88rpx;
		background: #DEB19B;
		border-radius: 12rpx;
		opacity: 0.5;
		font-size: 32rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 88rpx;
		bottom: 60rpx;
		left: 66rpx;
		text-align: center;
		margin: 114rpx 0 30rpx 66rpx;
	}

	.button1 {
		@extend .button;
		background: #A39492;
		border-radius: 12rpx;
		opacity: 0.4;
		bottom: 170rpx;
	}
</style>
