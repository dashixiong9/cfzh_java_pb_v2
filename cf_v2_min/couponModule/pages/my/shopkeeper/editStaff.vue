<template>
	<view>
		<view class="userBox u-flex-r">
			<image :src="userinfo?userinfo.staffAvatar:'../../../../static/image/my/Headimage2x.png'" ></image>
			<view class="u-flex-c">
				<view class="name">{{userinfo.staffName}}</view>
				<view class="id">ID:{{userinfo.id}}</view>
			</view>
		</view>
		<u-line color="#f7f7f7" />
		<view class="RemarksBox u-flex-r">
			<view class="left">备注名称</view>
			<navigator :url="'shopAssistant/setRemarks?id=' + userinfo.id" hover-class="none"> 
				<view class="u-flex-r u-abso" style="right:38rpx;">
					<view class="right">{{userinfo.note?userinfo.note:'请设置备注'}}</view>
					<image class="right_image" src="../../../../static/image/my/left2x.png"></image>
				</view>
			</navigator>
		</view>
		<view class="TotalBox u-flex-r">
			<view class="left">每日发券总额</view>
			<navigator :url="'shopAssistant/setSendCoupons?total=' + JSON.stringify(total)" hover-class="none">
				<view class="u-flex-r u-abso" style="right:38rpx;">
					<!-- <view class="right" style="margin-left: 168rpx;">{{total.length > 0 ?total[0].sameDayQuota:'请设置每日发券总额'}}</view> -->
					<image class="right_image" src="../../../../static/image/my/left2x.png"></image>
				</view>
			</navigator>
		</view>
		<u-line color="#f7f7f7" />
		<view class="TotalBox u-flex-r" style="margin-top: 0;">
			<view class="left">查看发券细名</view>
			<navigator url="shopAssistant/setSendCoupons.vue" hover-class="none">
				<view class="u-flex-r u-abso" style="right:38rpx;">
					<view class="right" style="margin-left: 168rpx;"></view>
					<image class="right_image" src="../../../../static/image/my/left2x.png"></image>
				</view>
			</navigator>
		</view>
		
		<view class="deleteButton" @click="onDelete">移除员工</view>
	
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userinfo: '',
				remarks:'',
				total:[]
			}
		},
		onLoad(e) {
			this.userinfo = JSON.parse(e.datas)
		},
		onShow() {
			this.getInfo()
		},
		methods:{
			async getInfo(){
					var res = await this.$u.api.getStaffCouponSettingByQuery({
						couponActivityId:uni.getStorageSync('parkingTicketActivityId'),
						staffId:this.userinfo.staffId
					})
					console.log(res) 
					if(res.code == 10002){
						this.total = res.data
					}else{
						this.total = []
					}
			},
			onDelete(){
				uni.showModal({
					title:'提示',
					content:'确定删除吗？',
					success: async (r) => {
						console.log(r);
						if(r.confirm){
							var id = this.userinfo.id
							var res = await this.$u.api.employerDeleteStaff_delete(id)
							console.log(res);
							if(res.code == 10002){
								this.$u.toast(res.message)
								setTimeout(()=>{
									uni.navigateBack()
								},1000)
							}
						}
					}
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
	}
	.deleteButton{
		width: 686rpx;
		height: 100rpx;
		border-radius: 16rpx;
		border: 2rpx solid #488cf5;
		position: absolute;
		left: 32rpx;
		bottom: 44rpx;
		font-size: 36rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #488cf5;
		line-height: 100rpx;
		text-align: center;
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
</style>
