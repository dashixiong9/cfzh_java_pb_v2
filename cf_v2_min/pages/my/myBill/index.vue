<template>
	<view>
		<!-- <view class="u-flex-c u-rela Invoice" @click="abc">
			<image src="../../../static/image/my/left2x.png"></image>
			<view class="InvoiceTitle">选择需要开票订单</view>
			<view class="InvoiceSubtitle">若未找到请输入支付平台(微信 支付宝等)订单号进行查找</view>
		</view> -->
		<view v-if="list.length == 0" class="u-flex-c">
			<image src="../../../static/image/my/list_not2x(1).png"
				style="width: 220rpx;height: 108rpx;margin: 206rpx 0 0 266rpx;"></image>
			<view class="notBill">您还木有账单信息哦</view>
		</view>
		<u-swipe-action bg-color='#F7F7F7' :index="index" v-for="(item, index) in list" :key="item.id" @click="click"
			:options="options" v-else disabled>
			<view class="listIem u-flex-c u-rela" @click="openOrderDetails(item)">
				<view class="name">{{item.goodsName}}</view>
				<view class="Price">{{item.amountsPayable}}元</view>
				<view class="timeAndType">{{$u.timeFormat(item.createTime, 'yyyy-mm-dd hh:MM:ss')}}</view>
			</view>
		</u-swipe-action>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [],
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#488cf5'
					}
				}],
				page: 1
			}
		},
		onLoad() {
			this.token = uni.getStorageSync("token");
			this.getOrderList();
		},
		methods: {
			abc() {
				this.$u.toast('正在开发中')
			},
			openOrderDetails(item) {
				uni.setStorageSync('orderDetail', item)
				uni.navigateTo({
					url: 'detail'
				})
			},
			async getOrderList() {
				var res = await this.$u.api.getMyBill({
					page: this.page,
					size: 30
				})
				console.log(res)
				if (res.code == 10002) {
					this.list = this.list.concat(res.data)
				}
			},
			click(index) {
				console.log(index); 
			}
		},
		onReachBottom() {
			this.page = this.page + 1
			this.getOrderList()
		}
	}
</script>

<style lang="scss">
	.Invoice {
		width: 750rpx;
		height: 118rpx;
		background: #FFFFFF;
		box-sizing: content-box;
		border-top: 24rpx solid #f5f5f5;
		border-bottom: 24rpx solid #f5f5f5;

		image {
			width: 32rpx;
			height: 32rpx;
			right: 32rpx;
			top: 44rpx;
			position: absolute;
		}

		.InvoiceTitle {
			width: 260rpx;
			height: 32rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 32rpx;
			margin: 28rpx 0 0 32rpx;
		}

		.InvoiceSubtitle {
			width: 560rpx;
			height: 24rpx;
			font-size: 22rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.24);
			line-height: 24rpx;
			margin: 10rpx 0 0 32rpx;
		}
	}

	.listIem {
		width: 750rpx;
		height: 172rpx;
		background: #FFFFFF;
		border-bottom: 2rpx solid #EAEAEA;
		;

		.name {
			height: 32rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 32rpx;
			margin: 24rpx 0 0 32rpx;
		}

		.Price {
			height: 32rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 32rpx;
			margin: 20rpx 0 0 32rpx;
		}

		.timeAndType {
			height: 20rpx;
			font-size: 20rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.3);
			line-height: 20rpx;
			margin: 20rpx 0 0 32rpx;
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
