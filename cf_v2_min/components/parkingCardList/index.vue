<template>
	<view class="u-flex-col">
		<view class="box u-flex u-rela" v-for="(item,index) of list" @click="onList(item,index)">
			<image class="card" src="../../static/image/monthlyCard/card@2x.png" ></image>
			<view class="u-flex-col info">
				<view class="title">{{item.title}}</view>
				<view class="subtitle">{{item.subtitle}}</view>
				<view class="price u-flex u-col-center">
					<image src="../../static/image/monthlyCard/sheng2x.png" ></image> 
					<view class="actualPrice">¥{{item.currentPrice}}</view>
					<view class="originalPrice">¥{{item.originalPrice}}</view>
				</view>
			</view>
			<view class="u-abso seed">立即购买</view>
		</view>
	</view>
</template>

<script>
	export default {
		props:['list'],
		methods:{
			onList(item,index){
				this.$emit('onList',index)
			}
		}
	}
</script>

<style lang="scss">
	.box{
		width: 670rpx;
		height: 280rpx;
		background: #FFFFFF;
		box-shadow: 0rpx 16rpx 22rpx 0rpx rgba(10, 36, 99, 0.07);
		border-radius: 16rpx;
		align-items: flex-start;
		margin: 42rpx 0 0 40rpx;
		.seed{
			width: 132rpx;
			height: 52rpx;
			background: #DEB19B;
			border-radius: 26rpx;
			position: absolute;
			font-size: 24rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #FFFFFF;
			line-height: 52rpx;
			text-align: center;
			bottom: 20rpx;
			right: 32rpx;
		}
		.card{
			width: 120rpx;
			height: 120rpx;
			margin: 54rpx 0 0 32rpx;
		}
		.info{
			.title{
				width: 466rpx;
				height: 40rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Semibold, PingFang SC;
				font-weight: 600;
				color: #1A1A1A;
				line-height: 40rpx;
				margin: 52rpx 0 0 20rpx;
			}
			.subtitle{
				width: 466rpx;
				height: 32rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.5);
				line-height: 32rpx;
				margin: 16rpx 0 0 20rpx;
			}
			.price{
				margin-left: 20rpx;
				margin-top: 16rpx;
				image{
					width: 32rpx;
					height: 32rpx;
				}
				.actualPrice{
					height: 40rpx;
					font-size: 40rpx;
					font-family: PingFangSC-Medium, PingFang SC;
					font-weight: 500;
					color: #FF5151;
					line-height: 40rpx;
					margin-left: 4rpx;
				}
				.originalPrice{
					height: 34rpx;
					font-size: 24rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: rgba(50, 50, 51, 0.5);
					line-height: 34rpx;
					margin-left: 10rpx;
					text-decoration:line-through;
				}
			}
		}
	}
</style>
