<template>
	<view>
		<view class="inputBG">
			<view class="inputBox u-flex-r">
				<image src="../../../static/image/home/icon-search2x.png" ></image>
				<input type="text" value="" class="input" placeholder="搜索附近停车场" placeholder-class="placeholder" confirm-type="search"
				 @confirm='SeedSearch' />
			</view>
		</view>
		<view style="height: 154rpx;"></view>


		<!-- 搜索历史模块 -->
		<view class="u-flex-c" v-if="SearchHistory.length > 0 && !searchResShow">
			<view class="searchHistory_box u-flex-r u-col-center">
				<view class="title">历史搜索</view>
				<image src="../../../static/image/home/delete_blue2x.png"  @click="clearHistory"></image>
			</view>
			<view style="width: 686rpx;margin-left: 32rpx;" class="u-flex-c">
				<view v-for="(item,index) in SearchHistory" :key="index" @click="onSearchHistory(item)" class="searchHistory_item u-line-1">
					{{item}}
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				active: 0,
				latitude: '',
				longitude: '',
				SearchHistory: [],
				parkingLotList: [],
				chargingPileList: [],
				searchResShow: false
			}
		},
		onLoad() {
			this.getLocation()
			this.getSearchHistory()
		},
		methods: {
			onSearchHistory(e){
				console.log(e);
				this.SeedSearch(e,true)
			},
			// 获取搜索历史
			getSearchHistory() {
				this.SearchHistory = wx.getStorageSync('SearchHistory') || []
			},
			async SeedSearch(e,type=false) {
				console.log(e);
				if(type){
					var value = e
				}else{
					var {
						value
					} = e.detail 
					if(this.SearchHistory.indexOf(value) == -1){
						this.SearchHistory.unshift(value)
						uni.setStorageSync('SearchHistory', this.SearchHistory);
					}
				}
				var res = await this.$u.api.getListByQuery({
					name: value,
					positionX: this.longitude,
					positionY: this.latitude,
					page: 1,
					size: 10
				})
				uni.navigateTo({
					url:`./searchRes?list=${JSON.stringify(res.data)}&active=${this.active}&value=${value}`
				})
			},
			clearHistory() {
				var that = this
				uni.showModal({
					title: '提示',
					content: '操作成功后搜索历史将会被清除，是否删除历史搜索？',
					success: function(res) {
						if (res.confirm) {
							that.SearchHistory = [];
							uni.setStorageSync('SearchHistory', that.SearchHistory);
						} else if (res.cancel) {
							return;
						}
					}
				});
			},
			getLocation() {
				uni.getLocation({
					success: res => {
						var {
							latitude,
							longitude
						} = res
						this.latitude = latitude
						this.longitude = longitude
					},
					fail(err) {
						uni.showToast({
							title: '获取定位失败，请在小程序设置中打开相关权限',
							icon: 'none'
						})
					}
				})
			},
			isActive(e) {
				this.$u.toast('正在开发中')
				return
				this.active = e
			}
		}
	}
</script>

<style lang="scss">
	.searchHistory_box {
		width: 750rpx;
		height: 104rpx;
		.title {
			margin-left: 40rpx;
			margin-top: 8rpx;
			width: 150rpx;
			height: 88rpx;
			font-size: 34rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 88rpx;
		}

		image {
			width: 32rpx;
			height: 32rpx;
			margin-left: 488rpx;
		}
	}

	.line {
		width: 74rpx;
		height: 8rpx;
		background: #488cf5;
		border-radius: 4rpx;
		bottom: 0;
		left: 12rpx;
	}

	.option {
		width: 750rpx;
		height: 88rpx;
		border-bottom: 2rpx solid #eaeaea;

		.optionOne {
			width: 98rpx;
			height: 88rpx;
			font-size: 32rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #488cf5;
			line-height: 88rpx;
			margin-left: 32rpx;
		}

		.optionOne_gray {
			@extend .optionOne;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			opacity: .6;
		}

		.optionTwo {
			@extend .optionOne;
			margin-left: 48rpx;
		}
	}

	.inputBG {
		width: 750rpx;
		height: 154rpx;
		background-color: #F7F7F7;
		overflow: hidden;
		position: fixed;
		top: 0;
		left: 0;
		.inputBox {
			width: 686rpx;
			height: 88rpx;
			background: #FFFFFF;
			border-radius: 60rpx;
			margin: 32rpx 0 0 32rpx;

			image {
				width: 48rpx;
				height: 48rpx;
				margin: 24rpx 0 0 30rpx;
			}

			.input {
				width: 580rpx;
				height: 48rpx;
				margin-top: 24rpx;
				margin-left: 20rpx;
			}
		}
	}

	.placeholder {
		font-size: 32rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(26, 26, 26, 0.5);
		line-height: 48rpx;

	}

	.searchHistory_item {
		width: inherit;
		border-bottom: 2rpx solid #E0E7F2;
		height: 100rpx; 
		font-size: 30rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.7);
		line-height: 100rpx;
	}

	.list_not {
		width: 750rpx;
		height: 500rpx;
		background: #FFFFFF;

		image {
			width: 220rpx;
			height: 108rpx;
		}

		view {
			width: 266rpx;
			height: 32rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #CDCDCD;
			line-height: 32rpx;
			margin-top: 24rpx;
		}
	}
</style>
