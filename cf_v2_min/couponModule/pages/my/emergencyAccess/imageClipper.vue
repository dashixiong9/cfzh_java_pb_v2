<template>
	<view>
		<l-clipper v-if="show" :image-url="imageSrc" @success="success" @cancel="cancel" :is-lock-ratio='false' :max-width='750' :max-height='750' />
	</view>
</template>

<script>
	import lClipper from '../../../components/lime-clipper/components/lime-clipper/index.vue'
	export default {
		components:{
			lClipper
		},
		data() {
			return {
				show: true,
				imageSrc:'',
				aisleId:'',
				header:{
					Authorization:'Bearer ' + uni.getStorageSync('token')
				},
				direction:''
			}
		},   
		onLoad(e) { 
			console.log(e);
			this.imageSrc = e.image
			this.aisleId = e.aisleId
			this.direction = e.direction
		},
		methods:{
			cancel(){ 
				uni.navigateBack({
					delta: 1
				});
			},
			async success(e){
				console.log(e);
				uni.uploadFile({
					url: 'https://park.cfeng.wang/carparklog/manuallyUpload',
					filePath: e.url,
					name: 'file',
					formData: {
						carParkCheckPointId:this.aisleId
					},
					header: {
						Authorization:'Bearer ' + this.vuex_token
					},
					success: (res) => {
						console.log(JSON.parse(res.data));
						var ress = JSON.parse(res.data)
						if(ress.code == 10002){
							if(this.direction == 'out'){
								uni.navigateTo({
									url: '../replacePay/pay?id=' + ress.data.id
								});
							}else{
								uni.navigateTo({
									url: 'emergencyOrderDetail?datas=' + JSON.stringify(ress.data)
								});
							}
						}else{
							this.$u.toast(ress.message)
						}
					}
				})
				
			}
		}
	}
</script>

<style>
</style>
