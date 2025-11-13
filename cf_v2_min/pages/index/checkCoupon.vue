<template>
	<div>
		<div class="box u-flex-c u-rela">
			<div class="amount">
				¥{{
         couponDetail.couponActivityCouponType != null
            ? couponDetail.couponActivityCouponType.denomination
            : "-"
        }}
			</div>
			<div class="name">停车场停车优惠券</div>
			<div class="timeLimit">
				有效期限：
				{{ $u.timeFormat(todayTime, "yyyy年mm月dd日") }}-{{
          couponDetail.couponActivityCouponType != null
            ? $u.timeFormat(
                todayTime.getTime() +
                 couponDetail.couponActivityCouponType.fixedExtensionTime,
                "yyyy年mm月dd日"
              )
            : ""
        }}
			</div>
			<!-- <div class="timeLimit" v-else>
        有效期限：{{
        fixedExtensionTime != null
            ? calculationTime(couponDetail.fixedExtensionTime)
            : ""
        }}
      </div> -->
			<div class="modeOfUse">使用方式：自动抵扣</div>
			<div class="u-flex-r" v-if="range.length > 0">
				<div class="Rules">使用范围：</div>
				<div class="Rules w233" style="margin-left: 0">
					{{ rangeText }}
				</div>
			</div>
			<div class="Range" v-else>使用范围：全部</div>
			<div class="u-flex-r">
				<div class="Rules" style="">使用规则：</div>
				<div class="Rules1 u-flex-c">
					<div>1.优惠券仅限在有效期内使用，过期则无法使用；</div>
					<div>
						2.少量特殊商品不能使用优惠券（有些优惠券标注全场通用则可以使用）；
					</div>
					<div>3.一张订单只能使用一张优惠券，优惠券不可合并；</div>
				</div>
			</div>

			<div class="button111" :style="receivingStatus ? 'opacity:.5;' : 'opacity:1;'" @click="openKeyborad">
				{{ receivingStatus ? "已领取" : "立即领取" }}
			</div>
		</div>
		<plate-input :plate="plateNo" @export="setPlate" @close="plateShow=false" v-if="plateShow" />
	</div>
</template>


<script>
	import plateInput from '@/components/uni-plate-input/uni-plate-input.vue'
	export default {
		data() {
			return {
				couponActivityCouponType: {},
				range: [],
				receivingStatus: false,
				todayTime: new Date(),
				plateNo: '',
				plateShow: false,
				couponDetail:{}
			}
		},
		components: {
			plateInput
		},
		computed: {
			rangeText: function() {
				if (this.range == null) {
					return [];
				}
				return this.range.map((i) => i.name).join();
			},
		},
		onLoad(e) {
			this.couponDetail = JSON.parse(e.couponDetail)
			this.couponDetail.couponActivityCouponType = JSON.parse(this.couponDetail.couponActivityCouponType)
			console.log(this.couponDetail);
		},
		methods: {
			setPlate(numberPlate) {
				if (numberPlate.length >= 7) this.plateNo = numberPlate
				this.plateShow = false
				uni.showModal({
					title: '提示',
					content: '优惠券发送至 ' + numberPlate + ' ,请确认',
					confirmText: '确认无误',
					success: async (res) => {
						if (res.confirm) {
							var {
								data,code,message
							} = await this.$u.api.getCouponByQrCode({
								qrCodeId: this.couponDetail.qrCodeId,
								qrCodeCreateId: this.couponDetail.staffId,
								couponActivityCouponTypeId: this.couponDetail.couponActivityCouponType.id,
								numberPlate
							});
							if(code == 10002){
								this.receivingStatus = true;
							}else{
								uni.showToast({
									title:message,
									icon: 'none'
								})
							}
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});


			},
			async couponScopeUse(id) {
				var {
					data,
					code
				} = await this.$u.api.selectContainDistanceListByCouponId({
					id
				})
				console.log(data);
				if (code == 10002) {
					this.range = res.data;
				}
			},
			openKeyborad() {
				uni.showToast({
					title: '请输入领取优惠券的车牌',
					icon: 'none',
					duration: 1000
				})
				setTimeout(() => {
					this.plateShow = true
				}, 1000)
				// this.$refs.inputCar.open()
			},
			// 键盘输入事件
			InputCarChange(e) {
				this.plateNo += e
			},
			// 键盘删除事件
			InputCarDelete() {
				this.plateNo = this.plateNo.substr(0, this.plateNo.length - 1);
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;
	}

	.w233 {
		width: 466rpx;
	}

	.box {
		width: 686rpx;
		background: #FFFFFF;
		margin: 32rpx;
		border-radius: 10rpx;

		.amount {
			height: 100rpx;
			font-size: 72rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #488CF5;
			line-height: 100rpx;
			align-self: center;
			margin: 48rpx 0 20rpx 0;
		}

		.name {
			height: 50rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 50rpx;
			align-self: center;
			margin-bottom: 48rpx;
		}

		.timeLimit {
			width: 606rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 40rpx;
			margin-left: 40rpx;
			margin-bottom: 24rpx;
			text-align: left;
		}

		.Range {
			width: 606rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 40rpx;
			margin-left: 40rpx;
			margin-bottom: 24rpx;
		}

		.Rules {
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 40rpx;
			margin-left: 40rpx;
			margin-bottom: 24rpx;
			text-align: left;
		}

		.Rules1 {
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 40rpx;
			margin-bottom: 24rpx;
			width: 460rpx;
			text-align: left;
		}

		.modeOfUse {
			width: 606rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.7);
			line-height: 40rpx;
			margin-left: 40rpx;
			margin-bottom: 24rpx;
			text-align: left;
		}
	}

	.button111 {
		width: 558rpx;
		height: 88rpx;
		background: #488CF5;
		border-radius: 12rpx;
		opacity: 0.4;
		font-size: 32rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 88rpx;
		bottom: 60rpx;
		left: 66rpx;
		text-align: center;
		margin: 114rpx 0 30rpx 66rpx;
	}

	.u-flex-c {
		display: flex;
		flex-direction: column;
	}

	.u-flex-r {
		display: flex;
	}
</style>
