<template>
	<view>
		<web-view :src="src"></web-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				src:''
			}
		},
		onLoad(e){
			console.log(e);
			this.src = e.src
		}
	}
</script>

<style>

</style>
