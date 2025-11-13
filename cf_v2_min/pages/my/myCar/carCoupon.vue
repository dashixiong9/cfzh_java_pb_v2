<template>
	<view>
		<view class="list" v-if="couponList.length>0">
			<view class="li" v-for="item in couponList" @click="onCoupon(item)">
				<view class="left" v-if="item.couponType == 1">{{item.denomination}}次</view>
				<view class="left" v-if="item.couponType == 2">{{item.denomination}}元</view>
				<view class="left" v-if="item.couponType == 3">{{(item.denomination / 3600000)}}小时</view>
				<view class="middle">
					<view class="name" v-if="item.couponType == 1">次数卷</view>
					<view class="name" v-if="item.couponType == 2">金额券</view>
					<view class="name" v-if="item.couponType == 3">时长券</view>
					<view class="hint u-line-1">使用规则：指定停车场可用</view>
					<view class="hint u-line-1">使用期限：{{getDateymd(item.effectiveTime)}} -
						{{getDateymd(item.expireTime)}}</view>
				</view>
				<view class="right" style="color:rgba(10,36,99,0.3);">查看</view>
			</view>
		</view>
		<view v-else class="u-flex-col">
			<image src="../../../static/image/my/list_not2x(1).png"
				style="width: 220rpx;height: 108rpx;margin: 306rpx 0 0 266rpx;"></image>
			<view class="notBill">该车牌没有优惠券</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				numberPlate: '',
				couponList: []
			}
		},
		onLoad(e) {
			this.numberPlate = e.numberPlate
			uni.setNavigationBarTitle({
				title: e.numberPlate + '车牌优惠券'
			})
			this.getCoupon()
		},
		methods: {
			onCoupon(item){
				uni.navigateTo({
					url:'../myCoupon/couponDetail?item=' + JSON.stringify(item)
				})
			},
			async getCoupon() {
				var res = await this.$u.api.getMyConpon({
					status: 1,
					page: 1,
					size: 1000,
					goodsId: this.numberPlate
				})
				console.log(res);
				if (res.code != 10002) {
					this.$u.toast(res.message)
					this.couponList = []
				} else {
					this.couponList = res.data
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
	}

	.list {
		.li {
			background: url(../../../static/image/my/couponUnused.png);
			width: 90vw;
			margin: 32rpx;
			height: 220upx;
			background-size: 100% 100%;
			background-repeat: no-repeat;
			display: flex;
			flex-direction: row;
			align-items: center;
			>view {
				text-align: center;
			}
			.left {
				width: 220upx;

				color: #FFFFFF;
				font-size: 60upx;
				font-weight: 600;

			}
			.middle {
				text-align: left;
				line-height: 40upx;
				margin-left: 20rpx;

				.name {
					color: #323233;
					font-size: 32upx;
					font-weight: 600;
				}
				.hint {
					color: rgba(10, 36, 99, 0.5);
					font-size: 20upx;

				}

			}
			.right {
				width: 20rpx;
				color: #EF3864;
				font-size: 24upx;
				margin-left: 71rpx; 
			}
		}
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
</style>
