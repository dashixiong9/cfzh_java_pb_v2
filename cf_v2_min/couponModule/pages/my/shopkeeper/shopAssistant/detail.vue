<template>
	<view>
		<view class="userBox u-flex-r">
			<image :src="userinfo?userinfo.avatar:'../../../../../static/image/my/Headimage2x.png'" ></image>
			<view class="u-flex-c">
				<view class="name">{{userinfo.nickName}}</view>
				<view class="id">ID:{{userinfo.id}}</view>
			</view>
		</view>
		<u-line color="#f7f7f7" />
		<!-- <view class="RemarksBox u-flex-r">
			<view class="left">备注名称</view>
			<navigator url="./setRemarks" hover-class="none"> 
				<view class="u-flex-r u-abso" style="right:38rpx;">
					<view class="right">{{remarks?remarks:'请设置备注'}}</view>
					<image class="right_image" src="../../../../static/image/my/left2x.png"></image>
				</view>
			</navigator>
		</view>
		<view class="TotalBox u-flex-r">
			<view class="left">每日发券总额</view>
			<navigator url="./setSendCoupons" hover-class="none">
				<view class="u-flex-r u-abso" style="right:38rpx;">
					<view class="right" style="margin-left: 168rpx;">{{total?total:'请设置每日发券总额'}}</view>
					<image class="right_image" src="../../../../static/image/my/left2x.png"></image>
				</view>
			</navigator>
		</view> -->

		<view class="pay_active" @click="next">添加</view>
		<view class="back" @click="back">重新输入ID</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userinfo: '',
				remarks: '',
				total: ''
			}
		},
		onLoad(e) {
			const userinfo = JSON.parse(decodeURIComponent(e.userinfo));
			this.userinfo = userinfo
		},
		onShow() {
			
		},
		methods:{
			async next(){
				var res = await this.$u.api.employerAddStaff({
					// 员工id
					staffId : this.userinfo.id,
					// 雇主id
					employerId : uni.getStorageSync('user').id
				})
				console.log(res)
				if(res.code == 10002){
					this.$u.toast('添加成功')
					setTimeout(()=>{
						uni.navigateBack({
							delta:2
						})
					},1500)
				}else{
					this.$u.toast(res.message)
				}
			},
			back(){
				uni.navigateBack()
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
	}

	.pay_active {
		@extend .pay;
		background: #488cf5;
		opacity: 1;
	}

	.back {
		@extend .pay;
		background: #f7f7f7 !important;
		border: 2rpx solid #488cf5;
		font-size: 36rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #488cf5 !important;
		margin-top: 22rpx !important;
	}

	.pay {
		width: 686rpx;
		height: 100rpx;
		background: #488cf5;
		border-radius: 16rpx;
		font-size: 36rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #FFFFFF;
		line-height: 100rpx;
		text-align: center;
		margin: 118rpx 0 0 32rpx;
	}

	.TotalBox {
		@extend .RemarksBox;
		margin-top: 24rpx;
	}

	.RemarksBox {
		width: 750rpx;
		height: 100rpx;
		background: #FFFFFF;

		.left {
			height: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 44rpx;
			margin: 28rpx 0 0 24rpx;
		}

		.right {
			@extend .left;
			margin: 28rpx 0 0 360rpx;
			color: rgba(50, 50, 51, 0.5);
		}

		.right_image {
			width: 32rpx;
			height: 32rpx;
			margin: 34rpx 0 0 16rpx;
		}
	}

	.userBox {
		width: 750rpx;
		height: 206rpx;
		background: #FFFFFF;
		margin-top: 32rpx;

		>image {
			width: 136rpx;
			height: 136rpx;
			margin: 40rpx 20rpx 0 42rpx;
			border-radius: 50%;
		}
	}

	.name {
		height: 40rpx;
		font-size: 40rpx;
		font-family: PingFangSC-Semibold, PingFang SC;
		font-weight: 600;
		color: #323233;
		line-height: 40rpx;
		margin-top: 68rpx;
	}

	.id {
		height: 20rpx;
		font-size: 20rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.3);
		line-height: 20rpx;
		margin-top: 20rpx;
	}
</style>
