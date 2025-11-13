<template>
	<view>
		<view class="alike headPortraits" @close="bottomPopup=true">
			<view class="left">
				我的头像
			</view>
			<view class="right" @click="album">
				<view>
					<image :src="userinfo.avatar?userinfo.avatar:'../../../static/image/my/Headimage2x.png'"></image>
				</view>
			</view>
		</view>

		<view class="alike" style="margin-top:0;" @click="openUpNickname">
			<view class="left">
				我的昵称
			</view>
			<view class="right">
				<view class="">{{userinfo.nickName}}</view>
				<image src="../../../static/image/my/left2x.png" style="width: 24rpx;height: 48rpx;margin-left: 10rpx;">
				</image>
			</view>
		</view>

		<view class="alike mobile" @click="openMyMobile">
			<view class="left">
				手机号
			</view>
			<view class="right">
				<view class="">{{userinfo.phone?userinfo.phone:'未绑定'}}</view>
				<image src="../../../static/image/my/left2x.png" style="width: 24rpx;height: 48rpx;margin-left: 10rpx;">
				</image>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		baseUrl
	} from '../../../common/config.js'
	export default {
		data() {
			return {
				userinfo: ''
			}
		},
		onShow() {
			this.userinfo = uni.getStorageSync('user')
		},
		methods: {
			// async getUserinfo(){
			// 	var res = await this.$u.api.findById({
			// 		uid : uni.getStorageSync('user').id,
			// 		simpleQuery:'yes'
			// 	})
			// 	console.log(123,res);
			// },
			album() {
				const that = this
				uni.chooseImage({
					count: 1, //默认9
					success: function({
						tempFiles
					}) {
						const files = tempFiles[0].path
						console.log(files);
						uni.showLoading({
							title: '请稍等',
							icon: 'none'
						});
						uni.uploadFile({
							url: `${baseUrl}/file/upload`,
							filePath: files,
							name: 'file',
							formData: {
								businesskey: 'file'
							},
							header: {
								Authorization: 'Bearer ' + that.vuex_token
							},
							success: async (res) => {
								uni.hideLoading()
								const ress = JSON.parse(res.data)
								if (ress.code === 10002) {
									const {
										data,
										code,
										message
									} = await that.$u.api.customerBaseUpdate({
										avatar: ress.data.filePath
									})
									if (code === 10002) {
										uni.showToast({
											title: message,
											icon: 'none'
										})
										this.userinfo.avatar = data.avatar
										uni.setStorageSync('user', this.userinfo)
									} else {
										uni.showToast({
											title: '更换头像失败',
											icon: 'none'
										})
									}
								} else {
									uni.showToast({
										title: '图片上传失败',
										icon: 'none'
									})
								}
							}
						})
					}
				});
			},
			openUpNickname() {
				uni.navigateTo({
					url: "changeName"
				})
			},
			openMyMobile() {
				uni.navigateTo({
					url: "changePhone/index"
				})
			},

		}
	}
</script>

<style lang="less">
	page {
		background-color: #f7f7f7;
	}

	.alike {
		width: 750rpx;
		padding: 26upx 3vw;
		background-color: #FFFFFF;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		margin-top: 34rpx;

		.left {
			height: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 44rpx;
		}

		.right {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;

			>view {
				color: rgba(50, 50, 51, 0.5);
				font-size: 30upx;

				image {

					width: 104upx;
					height: 104upx;
					border-radius: 50upx;
				}
			}

			.iconfont {
				color: #0A2463;
				font-size: 32upx;
				font-weight: 600;
				margin-left: 20upx;
			}

		}
	}

	.headPortraits {
		border-bottom: 2rpx solid #EFEFEF;
	}

	.mobile {
		margin-top: 20upx;
	}

	.region-box {
		>view {
			width: 90vw;
			margin: 0upx 5vw;
			height: 100upx;
			color: #0A2463;
			font-size: 32upx;
			line-height: 100upx;
			text-align: center;
			border-bottom: 2upx solid #EFEFEF;
			font-weight: 600;
		}

		.cancel {
			color: #0A2463;
			opacity: 0.5;
		}
	}
</style>
