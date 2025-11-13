<template>
	<view>
		<view class="status u-flex-r">
			<view class="left">出场&入场</view>
			<view class="u-flex-r right u-col-center" @click="selectStatu">
				<view v-if="active == 0" class="one">请选择</view>
				<view v-if="active == 1" class="two">入场</view>
				<view v-if="active == 2" class="two">出场</view>
				<image src="../../../../static/image/my/Dropdown.png" class="image" ></image>
			</view>
		</view>
		<picker @change="bindPickerChange" :value="index" :range="list" :disabled='!active'>
			<view class="status u-flex-r" style="margin-top: 0;">
				<view class="left">选择通道</view>
				<view class="u-flex-r right u-col-center" @click="aisle">
					<view v-if="pickerIndex == 0" class="one">请选择</view>
					<view v-if="pickerIndex > 0" class="two">{{list[pickerIndex - 1]}}</view>
					<image src="../../../../static/image/my/Dropdown.png" class="image" ></image>
				</view>
			</view>
		</picker>
		<view class="takeapicture u-flex-col u-col-center u-row-center" @click="takeapicture">
			<image src="../../../../static/image/my/camera@2x.png" ></image>
			<view class="text">点击请拍摄车牌号</view>
		</view>
	</view>
</template>

<script>
	import { baseUrl } from '../../../../common/config.js'
	export default {
		data() {
			return {
				active: 0,
				show: false,
				selector: [],
				pickerIndex: 0
			}
		},
		onLoad() {
			// this.getList()
		},
		computed: {
			list() {
				return this.selector.map(i => i.name)
			}
		},
		methods: {
			aisle() {
				if (!this.active) {
					this.$u.toast('请选择出入场类型')
					return
				} else {
					this.show = true
				}
			},
			bindPickerChange(e) {
				this.pickerIndex = Number(e.detail.value) + 1
			},
			getList(direction) {
				uni.getLocation({
					success: async (location) => {
						console.log(location);
						var {
							latitude,
							longitude
						} = location
						var res = await this.$u.api.selectNearbyCheckPoint({
							page: 1,
							size: 5,
							distance: 2000,
							// positionX: 114.190283,
							// positionY: 30.498288,
							direction,
							positionX:longitude,
							positionY:latitude 
						})
						console.log(res);
						if (res.code == 10002) {
							this.pickerIndex = 1
							this.selector = res.data
						} else {
							this.$u.toast(res.message)
						}
					}
				})

			},
			selectStatu() {
				uni.showActionSheet({
					itemList: ['入场', '出场'],
					success: (res) => {
						console.log(res);
						if (!res.tapIndex) {
							this.active = 1
							this.pickerIndex = 0
							this.getList('in')
							this.$u.vuex('vuex_direction', 'in')
						} else {
							this.active = 2
							this.pickerIndex = 0
							this.getList('out')
							this.$u.vuex('vuex_direction', 'out')
						}
					}
				})
			},
			takeapicture() {
				if (this.active < 1) {
					this.$u.toast('请选择出入场类型')
					return
				}
				if (this.pickerIndex == 0) {
					this.$u.toast('请选择通道')
					return
				}
				uni.chooseImage({
					count: 1,
					sourceType: ['camera'],
					sizeType: ['original'],
					success: (res) => {
						console.log(res);
						const params = {
							image: res.tempFilePaths[0],
							aisleId: this.selector[this.pickerIndex - 1].id,
							direction: this.selector[this.pickerIndex - 1].direction
						}
						uni.showLoading({
							title: '请稍等',
							icon: 'none'
						});
						uni.uploadFile({
							url: `${baseUrl}/carparklog/manuallyUpload`,
							filePath: params.image,
							name: 'file',
							formData: {
								carParkCheckPointId: params.aisleId
							},
							header: {
								Authorization: 'Bearer ' + this.vuex_token
							},
							success: (res) => {
								uni.hideLoading()
								console.log(JSON.parse(res.data));
								var ress = JSON.parse(res.data)
								if (ress.code == 10002) {
									// if (params.direction == 'out') {
									// 	uni.navigateTo({
									// 		url: '../replacePay/pay?id=' + ress.data.id
									// 	});
									// } else {
									uni.navigateTo({
										url: 'emergencyOrderDetail?datas=' + encodeURIComponent(JSON
											.stringify(ress.data))
									});
									// }
								} else {
									this.$u.toast(ress.message)
								}
							}
						})


						// uni.navigateTo({
						// 	url: 'imageClipper' + this.$u.queryParams(params)
						// })
					},
					fail(e) {
						console.log(e);
					}
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background: #F8F9FB;
	}

	.status {
		width: 750rpx;
		height: 100rpx;
		background: #FFFFFF;
		margin-top: 24rpx;
		justify-content: space-between;

		.left {
			height: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 44rpx;
			margin: 28rpx 0 0 32rpx;
		}

		.right {
			margin-right: 32rpx;

			.one {
				width: 96rpx;
				height: 44rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: rgba(50, 50, 51, 0.5);
				line-height: 44rpx;

			}

			.two {
				height: 48rpx;
				font-size: 34rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 48rpx;
			}

			.image {
				width: 32rpx;
				height: 32rpx;
			}
		}
	}

	.takeapicture {
		width: 670rpx;
		height: 360rpx;
		background: #FFFFFF;
		border-radius: 18rpx;
		margin: 24rpx 0 0 40rpx;

		image {
			width: 64rpx;
			height: 64rpx;
		}

		.text {
			width: 256rpx;
			height: 44rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #9B9C9E;
			line-height: 44rpx;
			margin-top: 12rpx;
		}
	}
</style>
