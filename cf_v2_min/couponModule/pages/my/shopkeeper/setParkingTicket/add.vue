<template>
	<view>
		<view class="list">
			<view class="li">
				<view class="left">设置面值(元)</view>
				<input type="number" class="right" @input="valueChange" :value="numValue" @click="plateShow = true" placeholder-class="placeholder" style="color: #323233;text-align: right;font-size: 32rpx;" placeholder="请输入面值"></input>
			</view>
			<view class="li">
				<view class="left">设置有效期</view>
				<view class="right u-flex u-col-center" @click="show = true">
					<text :style="type?'color: #323233;':'color: rgba(50, 50, 51, 0.5);'">{{type?type:'请选择停车券有效期'}}</text>
					<image src="../../../../../static/image/my/Dropdown.png" style="width: 32rpx;height: 32rpx;" ></image>
				</view>
			</view>
		</view>	
		<view :class="buttomStatus?'pay_active':'pay'" @click="save">保存</view>
		<u-picker @confirm='handleConfirm'  confirm-color="#488cf5" cancel-color="rgba(50, 50, 51, 0.5);" mode="selector" v-model="show"  :default-selector="[1]" :range="selector"></u-picker>
	</view>
</template>

<script>
	export default {
		data(){
			return {
				numValue:'',
				selector: ['24小时','30天'],
				show:false,
				type:'',
				index:''
			}
		},
		watch:{
			
		},
		computed:{
			buttomStatus:function(){
				if(this.numValue && this.type){
					return true
				}else{
					return false
				}
			}
		},
		methods:{
			async save(){
				if(this.type == '24小时'){
					var times = 24 * 3600 * 1000
				}else{
					var times = 24 * 3600 * 1000 * 30
				}
				var res = await this.$u.api.shopkeeperAddCouponActivityCouponType({
					couponActivityId:uni.getStorageSync('parkingTicketActivityId'),
					denomination:this.numValue,
					fixedExtensionTime:times
				})
				console.log(res)
				if(res.code == 10002){
					this.$u.toast('添加成功');
					setTimeout(()=>{
						uni.navigateBack()
					},1500)
				}
			},
			
			handleConfirm(e){
				this.type = this.selector[e[0]];
			},
			valueChange(e){
				this.numValue = e.detail.value
			}
		}
	}
</script>

<style lang="scss">
	page{
		background-color: #f7f7f7;
	}
	.placeholder{
		font-size: 32rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.5);
		line-height: 44rpx;
	}
	.pay{
		width: 686rpx;
		height: 100rpx;
		background: #488cf5;
		border-radius: 16rpx;
		opacity: 0.4;
		font-size: 36rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #FFFFFF;
		line-height: 100rpx;
		text-align: center;
		margin: 52rpx 0 0 32rpx;
	}
	.pay_active{
		@extend .pay;
		background: #488cf5;
		opacity: 1;
	}
	.list {
		background-color: #FFFFFF;
		margin-top: 32upx;
		>.li {
			font-size: 28upx;
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			border-bottom: 2rpx solid #eaeaea;
			padding: 30upx;
			.left {
				color: #898989;
			}
			.right {
				color: #000;
			}
		}
	}
</style>
