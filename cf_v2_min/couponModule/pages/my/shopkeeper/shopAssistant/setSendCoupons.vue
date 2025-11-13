<template>
	<view>
		<view style="width: 750rpx;height: 96rpx;background: #FFFFFF;" class="u-flex u-row-center">
			<view>
				<u-tabs font-size='34' :list="navList" active-color="#488cf5" :current="current" @change="change">
				</u-tabs>
			</view>
		</view>
		<input type="number" v-model="value" class="input" :placeholder="placeholderValue" placeholder-class="placeholder"></input>
		
		
		<view style="font-size: 12px;opacity: .6;margin: 16px 0 0 16px;">当前限额：</view>
		<view style="font-size: 12px;opacity: .6;margin: 5px 0 0 16px;" v-if="total !== ''">次数券：{{total[0].sameDayQuota}}次</view>
		<view style="font-size: 12px;opacity: .6;margin: 5px 0 0 16px;" v-if="total !== ''">金额券：{{total[1].sameDayQuota}}元</view>
		<view style="font-size: 12px;opacity: .6;margin: 5px 0 0 16px;" v-if="total !== ''">时长券：{{total[2].sameDayQuota}}小时</view>
		
		<view :class="buttomStatus?'pay_active u-abso':'pay u-abso'" @click="next">保存</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				value:'',
				timeValue:'',
				total: '',
				id:'',
				current:0,
				navList: [{
					name: '金额券'
				}, {
					name: '时间送券'
				}, {
					name: '次数送券'
				}],
			}
		},
		computed:{
			buttomStatus : function (){
				if(this.value){
					return true
				}else{
					return false
				}
			},
			placeholderValue : function (){
				if(this.current == 0){
					return '请设置每日发券金额最高额度'
				}
				if(this.current == 1){
					return '请设置每日发券小时数最高额度'
				}
				if(this.current == 2){
					return '请设置每日发券次数最高额度'
				}
			},
		},
		onLoad(e) {
			console.log(e);
			const arr = JSON.parse(e.total)
			arr[2].sameDayQuota = arr[2].sameDayQuota / 3600000
			this.total = arr
		},
		methods:{
			change(index) {
				this.current = index;
			},
			async next(){
				if(!this.value){
					return
				}
				const id = this.current == 0 ? this.total[1].id : this.current == 1 ? this.total[2].id : this.total[0].id
				if(this.current == 1){
					this.timeValue = this.value * 3600000
				}
				var res = await this.$u.api.employerUpdateStaffCouponSetting({
					sameDayQuota:this.current == 1 ? this.timeValue : this.value,
					id,
					couponActivityId:uni.getStorageSync('parkingTicketActivityId')
				})
				console.log(res)
				if(res.code == 10002){
					const arr = this.total
					if(this.current == 0){
						arr[1].sameDayQuota = this.value
					}
					if(this.current == 1){
						arr[2].sameDayQuota = this.value
					}
					if(this.current == 2){
						arr[0].sameDayQuota = this.value
					}
					this.total = arr
					this.$u.toast('修改成功')
					this.value = ''
				}else{
					this.$u.toast(res.message)
				}

			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
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
		left: 32rpx;
		bottom: 44rpx;
	}
	.pay_active{
		@extend .pay;
		background: #488cf5;
		opacity: 1;
	}
	.placeholder{
		height: 44rpx;
		font-size: 32rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: rgba(50, 50, 51, 0.3);
		line-height: 44rpx;
	}
	.input{
		width: 750rpx;
		height: 100rpx;
		background: #FFFFFF;
		margin-top: 24rpx; 
		padding-left: 24rpx;
		box-sizing: border-box;
	}
</style>
