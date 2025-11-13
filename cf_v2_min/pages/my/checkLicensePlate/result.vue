<template>
	<view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				plateNo: ''
			}
		},
		onLoad(e) {
			this.plateNo = e.plateNo
			this.getInfo()
		},
		methods: {
			async getInfo() {
				var {
					data,
					code
				} = await this.$u.api.selectPackageListByQuery({
					numberPlate: this.plateNo,
					page: 1,
					size: 30,
				})
				console.log(data);
				if(code != 10002){
					uni.showModal({
						title:'提示',
						content:'没有找到相关车辆信息',
						showCancel:false,
						success() {
							uni.navigateBack({
								delta:1
							})
						}
					})
				}
			}
		}
	}
</script>

<style>

</style>
