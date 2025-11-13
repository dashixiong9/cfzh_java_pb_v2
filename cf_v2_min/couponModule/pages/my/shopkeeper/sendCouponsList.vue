<template>
	<view>
		<view class="listBox u-rela u-flex-c" v-for="(item,index) of list" :index='item.id' v-if="list.length > 0">
			<view class="num u-abso">
			<text v-if="item.couponType == 3">{{item.denomination / 3600000}}</text>
			<text v-if="item.couponType != 3">{{item.denomination}}</text>
			
			<text v-if="item.couponType == 1">次</text>
			<text v-if="item.couponType == 2">元</text>
			<text v-if="item.couponType == 3">小时</text>
			
			</view>
			<view class="number">{{item.toUidName?'受赠人: ' + item.toUidName :'顾客车牌: ' + item.recipientBody}}</view>
			<view class="name1">处理人：{{item.staffName}}</view>
			<view class="time">{{$u.timeFormat(item.createTime, 'yyyy年mm月dd日 hh时MM分ss秒')}}</view>
		</view>
		<view  style="margin-top: 50%;">
		<u-empty text="没有数据" mode="list" v-if="list.length == 0"></u-empty></view>
		<u-picker v-model="show" mode="time" :default-time='defaultTime' @confirm='confirm'></u-picker>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [],
				show: false,
				defaultTime: "",
				choiceTime: new Date().toLocaleDateString().replace(/\//g, '-'),
				page:1
			}
		},
		methods: {
			confirm(e) {
				console.log(e);
				var {
					year,
					month,
					day
				} = e
				this.choiceTime = year + '-' + month + '-' + day
			},
			async getInfo(){
				var res = await this.$u.api.getMyCouponActivityList({
					
				})
				console.log(123,res) 
				if(res.code == 10002){
					this.couponActivityId = res.data[0].id
					this.getList()
				}
			}, 
			async getList(id){
				var res = await this.$u.api.getCouponActivityCouponLogList({
					couponActivityId:this.couponActivityId,
					page:this.page,
					size:20
				})
				console.log(444,res) 
				if(res.code == 10002){
					this.list = this.list.concat(res.data) 
				}
			},
		},
		onLoad() {
			this.getInfo()
		},
		onReady() {
			this.defaultTime = new Date().toLocaleDateString().replace(/\//g, '-')
		},
		onReachBottom() {
			this.page += 1
			this.getList()
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
	}

	.listBox {
		padding-left: 32rpx;
		background-color: #fff;
		width: 750rpx;
		height: 160rpx;
		background: #FFFFFF;
		border-bottom: 2rpx solid #eaeaea;

		.num {
			height: 56rpx;
			font-size: 40rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 56rpx;
			top: 24rpx;
			right: 32rpx;
		}

		.number {
			height: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 44rpx;
			margin-top: 24rpx;
		}

		.name {
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.5);
			line-height: 34rpx;
		}

		.time {
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.5);
			line-height: 34rpx;
		}
	}

	.dateText {
		margin: 32rpx 0 0 32rpx;
		.text {
			height: 40rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 40rpx;
			margin-right: 6rpx;
		}
	}

	.totalBox {
		width: 686rpx;
		height: 128rpx;
		background: #FFFFFF;
		border-radius: 8rpx;
		margin: 16rpx 0 0 32rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #323233;
		line-height: 128rpx;
		padding-left: 30rpx;
		margin-bottom: 64rpx;
	}
</style>
