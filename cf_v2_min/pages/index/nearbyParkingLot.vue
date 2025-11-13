<template>
	<view>
		<!-- <view style="width: 750rpx;height: 20rpx;background-color: #f5f5f5;"></view> -->
		<parkingList :list='list' />
	</view>
</template>

<script>
	import parkingList from '../../components/parkingList/parkingList.vue'
	export default {
		components:{ parkingList },
		data() {
			return {
				list: []
			}
		},
		onLoad() {
			this.list = uni.getStorageSync('nearbyParkingLotList')
		}
	}
</script>

<style>
	page{
		background: #F8F9FB;
	}
</style>
