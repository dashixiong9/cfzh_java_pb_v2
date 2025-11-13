<template>
	<view>
		<u-cell-group>
			<u-cell-item :title="item.name" :value='item.positionDescribe' v-for="item of list" :arrow='false'></u-cell-item>
			
		</u-cell-group>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list:[]
			};
		},
		onLoad(option) {
			this.list = JSON.parse(decodeURIComponent(option.list))
		}
	}
</script>

<style lang="scss">

</style>
