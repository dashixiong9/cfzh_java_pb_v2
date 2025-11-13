<template>
	<view>
		<view class="default" v-if="!list.length">
			<image src="../../../../../static/image/my/Default.png"></image>
			<view class="hint1">您还未添加停车券</view>
			<view class="hint2">请您先添加停车场券</view>
			<view class="button" @click="intoAdd">立即添加</view>
		</view>
		<view v-else>
			<view class="u-flex-c">
				<u-swipe-action bg-color='#F7F7F7' @click="click" :options="options" :index="index" v-for="(item, index) in list" :key="item.id">
					<view class="carBox u-flex-c">
							<view class="num">¥{{item.denomination}}</view>
							<view class="setMeal u-flex-c">
								<view class="title">有效期限：领取之日 - {{calculationTime(item.fixedExtensionTime)}}</view>
								<!-- <view class="title">有效期限：领取之日 - {{$u.timeFormat(item.fixedExtensionTime, 'yyyy年mm月dd日 hh时MM分ss秒')}}</view> -->
								<!-- <view class="quickCardOpening">使用范围：宜家装饰材料广场-地上停车场</view> -->
							</view>
					</view>
				</u-swipe-action> 
			</view>
		</view>
		<view class="u-fixed thenAdd" v-if="list.length > 0">
			<view class="buttom" @click="intoAdd">继续添加</view>
		</view>
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
				}]
			}
		},
		onLoad() {

		},
		onShow() {
			this.getList()
		},
		methods: {
			calculationTime(s){
				var times = s / (24 * 3600 * 1000)
				return times + '天'
			},
			
			async click(index, index1) {
				console.log(index);
				var id = this.list[index].id
				var res = await this.$u.api.shopkeeperDeleteCouponActivityCouponType({
					couponActivityCouponTypeId:id 
				})
				console.log(res)
				if(res.code == 10002){
					this.getList()
				}else{
					this.$u.toast(res.message)
				}
			},
			async getList() {
				var res = await this.$u.api.getCouponActivityCouponTypeListByQuery({
					couponActivityId: uni.getStorageSync('parkingTicketActivityId')
				})
				console.log(res);
				if (res.code == 10002) {
					this.list = res.data
				}
			},
			intoAdd() {
				uni.navigateTo({
					url: 'add'
				})
			},
		}
	}
</script>

<style lang="scss">
	page {
		background: #f7f7f7;
	}

	.thenAdd {
		width: 750rpx;
		height: 164rpx;
		background: #FFFFFF;
		box-shadow: 0rpx -4rpx 16rpx 0rpx rgba(0, 0, 0, 0.1);
		left: 0;
		bottom: 0;

		.buttom {
			width: 686rpx;
			height: 100rpx;
			border-radius: 16rpx;
			border: 2rpx solid #488cf5;
			margin: 32rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #488cf5;
			line-height: 100rpx;
			text-align: center;
		}
	}

	.carBox {
		width: 686rpx;
		margin: 24rpx 0 10rpx 32rpx;
		margin-top: 24rpx;
		border-radius: 16rpx;
		overflow: hidden;

		.num {
			width: 686rpx;
			height: 98rpx;
			background: #FE6F63;
			border-radius: 12rpx 12rpx 0rpx 0rpx;
			font-size: 48rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #FFFFFF;
			line-height: 66rpx;
			padding: 16rpx 0 0 32rpx;
		}

		.setMeal {
			background: #FFFFFF;
			height: 81rpx;

			.title {
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.7);
				line-height: 40rpx;
				margin: 18rpx 0 0 32rpx;
			}

			.quickCardOpening {
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.7);
				line-height: 40rpx;
				margin: 8rpx 0 0 32rpx;
			}
		}
	}

	.default {
		display: flex;
		flex-direction: column;
		text-align: center;
		align-items: center;
		margin-top: 348rpx;

		>image {
			width: 240upx;
			height: 136upx;
		}

		.hint1 {
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			margin-top: 16rpx;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 34rpx;
		}

		.hint2 {
			height: 28rpx;
			font-size: 20rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.5);
			line-height: 28rpx;
		}

		.button {
			border: 2rpx solid rgba(232, 37, 20, 0.8);
			padding: 10upx 26upx;
			border-radius: 40rpx;
			margin-top: 30upx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(232, 37, 20, 0.8);
			line-height: 40rpx;
		}
	}
</style>
