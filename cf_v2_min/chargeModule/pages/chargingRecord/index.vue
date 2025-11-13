<template>
	<view>
		<u-tabs :list="tabList" :is-scroll="false" :current="current" @change="change"></u-tabs>

		<view v-if="defaultShow" class="u-flex-c">
			<image src="../../static/default.png" style="width: 220rpx;height: 108rpx;margin: 206rpx 0 0 266rpx;">
			</image>
			<view class="notBill">您还木有充电信息哦</view>
		</view>
		<view class="Box" v-for="item in list" :key='item.id' v-if="current === 0" @click="intoOrderDetail(item)">
			<view class="u-flex title">
				<view class="name u-line-1">
					{{item.cfChargingStation !== null ? item.cfChargingStation.stationName : '-'}}</view>
				<view class="time">{{$u.timeFormat(item.startTime,'yyyy-mm-dd')}}</view>
			</view>
			<view class="line"></view>
			<view class="electricity">充电电量：{{item.chargingedKwh}}°</view>
			<view class="amount">消费金额：{{item.chargingFee}}元</view>
			<view class="line"></view>
			<view class="status_blue" v-if="item.chargingStatus === 2" style="margin-left: 510rpx;">充电中</view>
			<view class="status_grey" v-if="item.chargingStatus === 3" style="margin-left: 510rpx;">已完成</view>
		</view>
		
		<view class="Box" v-for="item in bikeList" :key='item.id' v-if="current === 1" @click.stop="intoOrderDetail(item)">
			<view class="u-flex title">
				<view class="name u-line-1">
					{{item.cfChargingStation !== null ? item.cfChargingStation.stationName : '-'}}</view>
				<view class="time">{{$u.timeFormat(item.startTime,'yyyy-mm-dd')}}</view>
			</view>
			<view class="line"></view>
			<view class="electricity">充电电量：{{item.chargingedKwh}}°</view>
			<view class="amount">消费金额：{{item.chargingFee}}元</view>
			<view class="line"></view>
			<view class="u-flex" v-if="item.chargingStatus === 2">
				<view class="endcharging" @click.stop="stopCharging(item)">结束充电</view>
				<view class="status_blue">充电中</view>
			</view>
			<view class="status_grey" v-if="item.chargingStatus === 3">已完成</view>
		</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				page: 1,
				bikePage:1,
				list: [],
				bikeList:[],
				tabList: [{
					name: '电动车'
				}, {
					name: '电动两轮车'
				}],
				current:0
			}
		},
		computed:{
			defaultShow:function(){
				return this.current === 0 ? this.list.length == 0 ? true : false : this.bikeList.length == 0 ? true : false
			}
		},
		onLoad(e) {
			this.getChargingBill()
			this.getChargingBikeBill()
		},
		onReachBottom() {
			if(this.current === 0){
				this.page = this.page + 1
				this.getChargingBill()
			}else{
				this.bikePage = this.bikePage + 1
				this.getChargingBikeBill()
			}
		},
		methods: {
			stopCharging(item){
				uni.showModal({
					title:'提示',
					content:'确定停止充电吗?',
					success: async ({confirm}) => {
						if(confirm){
							uni.showLoading()
							var {
								code,
								message,
								data
							} = await this.$u.api.stopCharging({
								chargingUseLogId: item.id
							})
							uni.hideLoading()
							if (code === 10002) {
								uni.showLoading({
									title:'正在关闭中'
								})
								setTimeout(()=>{
									this.bikeList = []
									this.bikePage = 1
									this.getChargingBikeBill()
									uni.hideLoading()
								},2000)
							}else{
								uni.showToast({
									title: message,
									icon: 'none'
								})
							}
						}
					}
				})
			},
			change(index){
				// this.page = 0 
				// this.bikePage = 0
				this.current = index
			},
			intoOrderDetail({
				id,
				chargingStatus
			}) {
				console.log(id,chargingStatus);
				// 如果是充电中 则不准跳转
				if(chargingStatus === 2){
					if(this.current === 0){
						uni.setStorageSync('charingId', id)
						uni.navigateTo({
							url: '/chargeModule/pages/charging/index',
							fail(err) {
								console.log(err);
							}
						})
					}else{
						return
					}
				}
				uni.navigateTo({
					url: '../orderDetail/index?id=' + id
				})
			},
			async getChargingBill() {
				var {
					code,
					message,
					data
				} = await this.$u.api.getChargingBill({
					page: this.page,
					size: 10,
					chargingType:2
				})
				if (code === 10002) {
					this.list = this.list.concat(data)
				} else {
					this.page = this.page - 1
				}
			},
			async getChargingBikeBill() {
				var {
					code,
					message,
					data
				} = await this.$u.api.getChargingBill({
					page: this.bikePage,
					size: 10,
					chargingType:1
				})
				if (code === 10002) {
					this.bikeList = this.bikeList.concat(data)
				} else {
					this.bikePage = this.bikePage - 1
				}
			},
		}
	}
</script>

<style lang="scss">
	page {
		background: #F5F5F5;
		padding-bottom: 50rpx;
	}
	.endcharging{
		width: 160rpx;
		height: 60rpx;
		border-radius: 200rpx;
		border: 2rpx solid #F58748;
		margin-top: 24rpx;
		font-size: 28rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #F58748;
		line-height: 60rpx;
		text-align: center;
		margin-left: 330rpx;
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

	.Box {
		width: 670rpx;
		height: 362rpx;
		background: #FFFFFF;
		border-radius: 8rpx;
		border: 2rpx solid #EFF0F2;
		margin: 32rpx 0 0 40rpx;

		.status_grey {
			width: 134rpx;
			height: 60rpx;
			background: #AEB6C2;
			border-radius: 200rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #FFFFFF;
			line-height: 60rpx;
			text-align: center;
			margin: 24rpx 0 0 510rpx;
		}

		.status_blue {
			width: 134rpx;
			height: 60rpx;
			background: #488CF5;
			border-radius: 200rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #FFFFFF;
			line-height: 60rpx;
			text-align: center;
			// margin: 24rpx 0 0 510rpx;
			margin: 24rpx 0 0 20rpx;
		}

		.line {
			width: 606rpx;
			height: 2rpx;
			background: #EAEAEA;
			margin: 24rpx 0 0 32rpx;
		}

		.electricity {
			height: 40rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.6);
			line-height: 40rpx;
			margin: 24rpx 0 0 32rpx;
		}

		.amount {
			@extend .electricity;
			margin: 12rpx 0 0 32rpx;
		}

		.title {
			.name {
				width: 432rpx;
				height: 64rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 64rpx;
				margin: 24rpx 0 0 32rpx;
			}

			.time {
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.6);
				line-height: 40rpx;
				margin: 36rpx 0 0 16rpx;
			}
		}
	}
</style>
