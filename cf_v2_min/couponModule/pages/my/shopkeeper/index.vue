<template>
	<view>
		<image class="topBg u-abso" src="../../../static/image/BG2@2x.png"></image>
		<view class="u-abso contentBox u-flex-c">
			<view class="name">{{info.title}}</view>
			<view class="contentInfo u-flex-c u-col-center">
				<u-tabs :list="list1" @change="click" :current='current'></u-tabs>
				<view class="myVolume">我的停车券</view>
				<view class="num" v-if="info && current == 0">{{(info.totalIssueQuota - info.issuedQuota) || 0}}元</view>
				<view class="num" v-if="info && current == 1">{{(info.remainingTime / 3600000) || 0}}小时</view>
				<view class="num" v-if="info && current == 2">{{(info.remainingTimes) || 0}}次</view>
				<view class="line"></view>
				<view class="u-flex-r option u-rela">
					<view class="line1 u-abso"></view>
					<!-- <view class="line2 u-abso"></view> -->
					<navigator :url="'./buyParkingTicket?rechargeRatio=' + info.rechargeRatio + '&timeCouponPrice=' + info.timeCouponPrice + '&timesCouponPrice=' + info.timesCouponPrice" hover-class="none">
						<view @click="onSetParkingTicket">购买停车券</view>
					</navigator>
					<!-- 	<navigator url="setParkingTicket/index" hover-class="none">
						<view @click="onSetParkingTicket">设置停车券</view>
					</navigator> -->
					<navigator :url="'./giveAway?id=' + info.id" hover-class="none">
						<view @click="onSetParkingTicket">赠送停车券</view>
					</navigator>
				</view>
			</view>
			<view class="giveAwayNumBox u-flex-r">
				<view class="num1">今日送券总额：{{!todayIssued ? '0' : todayIssued}}</view>
				<view class="intoDetail" @click="intoSendCouponsList">查看详情</view>
			</view>
			<view class="u-flex-r">
				<view class="shopAssistantTilte">我的店员</view>
				<view class="intoDetail" style="margin-top: 36rpx;margin-left: 216rpx;" @click="intoAddShopAssistant">
					添加店员</view>
			</view>
			<view v-if="shopAssistantList.length == 0" class="noShopAssistant u-flex-c u-row-center u-col-center">
				<image src="../../../static/image/noshopAssistant.png" ></image>
				<view class="prompt">您还未添加店员哦</view>
				<view class="addShopAssistant" @click="intoAddShopAssistant">添加店员</view>
			</view>
			<view v-else class="u-flex-c">
				<view class="u-flex-r shopAssistantItem u-col-center" v-for="(item,index) of shopAssistantList"
					@click="onShopAssistantList(item)">
					<image :src="item.staffAvatar"  class="avatar"></image>
					<view class="name_item">{{item.note ? item.note : item.staffName}}</view>
					<view class="todayGiveaway">今日送券:{{item.sameDayCouponCount}}</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				shopAssistantList: [],
				info: '',
				todayIssued: 0,
				current:0,
				list1: [{
					name: '金额券',
				}, {
					name: '时长券',
				}, {
					name: '次数券'
				}],
			}
		},
		onLoad() {

		},
		onShow() {
			this.getMyEmployee()
			this.getInfo()
		},
		methods: {
			click(item) {
				console.log('item', item);
				this.current = item
			},
			onSetParkingTicket() {
				console.log(123);
				uni.setStorageSync('parkingTicketActivityId', this.info.id)
			},
			onShopAssistantList(item) {
				console.log(item);
				uni.setStorageSync('parkingTicketActivityId', this.info.id)
				uni.navigateTo({
					url: 'editStaff?datas=' + JSON.stringify(item)
				})
			},
			async getInfo() {
				var res = await this.$u.api.getMyCouponActivityList({

				})
				console.log(res)
				if (res.code == 10002) {
					this.info = res.data[0]
					this.getDetail(res.data[0].id)
				}
			},
			async getDetail(id) {
				var res = await this.$u.api.activityDetail({
					activityId: id,
				})
				console.log(333, res)
				if (res.code == 10002) {
					if (res.data.todayIssued != null) {
						this.todayIssued = res.data.todayIssued
					}
					// this.info = res.data
				}
			},
			intoAddShopAssistant() {
				uni.navigateTo({
					url: './shopAssistant/add'
				})
			},
			async getMyEmployee() {
				var res = await this.$u.api.getMyStaffList({
					page: 1,
					size: 9999
				})
				console.log(res);
				if (res.code == 10002) {
					this.shopAssistantList = res.data
				} else {
					this.shopAssistantList = []
				}
			},
			intoSendCouponsList() {
				uni.navigateTo({
					url: './sendCouponsList'
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f5f5;
	}

	.topBg {
		width: 750rpx;
		height: 346rpx;
		// background: #488cf5;
		left: 0;
		top: 0;
	}

	.contentBox {
		left: 0;
		top: 0;
		z-index: 1;

		.name {
			height: 56rpx;
			font-size: 40rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #FFFFFF;
			line-height: 56rpx;
			margin: 48rpx 0 0 36rpx;
		}

		.contentInfo {
			width: 686rpx;
			// height: 332rpx;
			background: #FFFFFF;
			border-radius: 18rpx;
			margin: 48rpx 0 0 32rpx;

			.myVolume {
				width: 140rpx;
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #86540B;
				line-height: 40rpx;
				margin-top: 30rpx;
			}

			.num {
				height: 76rpx;
				font-size: 76rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #2C2C2C;
				line-height: 76rpx;
				margin-top: 8rpx;
				text-align: center;
			}

			.line {
				width: 686rpx;
				height: 2rpx;
				background: #EAEAEA;
				margin-top: 54rpx;
			}

			.option {
				width: inherit;
				display: flex;
				justify-content: space-around;

				view {
					width: 228rpx;
					height: 88rpx;
					font-size: 28rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #323233;
					line-height: 88rpx;
					text-align: center;
				}

				.line1 {
					left: 342rpx;
					width: 2rpx;
					height: 38rpx;
					background: #D8D8D8;
					border-radius: 2rpx;
					border: 2rpx solid #EAEAEA;
					top: 26rpx;
				}

				// .line2 {
				// 	@extend .line1;
				// 	left: 342rpx; 
				// }
			}
		}
	}

	.giveAwayNumBox {
		width: 686rpx;
		height: 100rpx;
		background: #FFFFFF;
		border-radius: 16rpx;
		margin: 22rpx 0 0 32rpx;

		.num1 {
			width: 302rpx;
			height: 36rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 36rpx;
			margin: 32rpx 0 0 32rpx;
		}
	}

	.shopAssistantTilte {
		width: 338rpx;
		height: 36rpx;
		font-size: 34rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #323233;
		line-height: 36rpx;
		margin: 48rpx 0 0 36rpx;
	}

	.noShopAssistant {
		width: 682rpx;
		height: 380rpx;
		background: #FFFFFF;
		box-shadow: 0rpx 10rpx 12rpx 0rpx rgba(0, 0, 0, 0.06);
		border-radius: 16rpx;
		margin: 32rpx 0 0 36rpx;

		image {
			width: 220rpx;
			height: 108rpx;
		}

		.prompt {
			width: 192rpx;
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 34rpx;
			margin-top: 16rpx;
		}

		.addShopAssistant {
			width: 184rpx;
			height: 64rpx;
			border-radius: 40rpx;
			border: 2rpx solid #488cf5;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #488cf5;
			line-height: 64rpx;
			text-align: center;
			margin-top: 48rpx;
		}
	}

	.shopAssistantItem {
		margin-left: 32rpx;
		margin-top: 34rpx;

		.avatar {
			width: 80rpx;
			height: 80rpx;
			border-radius: 50%;
		}

		.name_item {
			max-width: 102rpx;
			height: 48rpx;
			font-size: 34rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 48rpx;
			margin-left: 16rpx;
		}

		.todayGiveaway {
			height: 40rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #C24E29;
			line-height: 40rpx;
			margin-left: 348rpx;
		}
	}

	.intoDetail {
		width: 140rpx;
		height: 56rpx;
		border-radius: 40rpx;
		border: 2rpx solid #488cf5;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #488cf5;
		line-height: 56rpx;
		text-align: center;
		margin-left: 182rpx;
		margin-top: 22rpx;
	}
</style>
