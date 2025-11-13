<template>
	<view>

		<view class="list">
			<view class="li bottomLine">
				<view class="left">停车场</view>
				<view class="right">{{parkingLotInfo.carParkName}}</view>
			</view>
			<view class="li bottomLine">
				<view class="left">车牌</view>
				<view class="right">{{parkingLotInfo.numberPlate}}</view>
			</view>
			<view style="height: 24rpx;width: 750rpx;background-color: #F8F9FB;"></view>
			<view class="li bottomLine">
				<view class="left">月卡类型</view>
				<view class="right">{{setMealInfo.title}}</view>
			</view>
			<view style="height: 24rpx;width: 750rpx;background-color: #F8F9FB;" v-if="amountsPayable"></view>
			<view class="li bottomLine" style="height: 112rpx;padding: 0;" v-if="amountsPayable">
				<view class="u-flex-col u-row-center" style="margin-left: 30rpx;">
					<view class="left">临停追缴</view>
					<view class="left" style="font-size: 22rpx;margin-top: 12rpx;color: rgba(50, 50, 51, 0.5);">月卡过期后产生的临停费用</view>
				</view>
				<view class="right" style="margin-right: 30rpx;line-height: 112rpx;">￥{{amountsPayable}}</view>
			</view>
		</view>
		<view class="list">
			<view class="li">
				<view class="left">套餐金额</view>
				<view class="right">{{setMealInfo.currentPrice || ''}}元</view>
			</view>
			
			<view class="li">
				<view class="left">其他折扣</view>
				<view class="right">0元</view>
			</view>
		</view>
		
		
		
		<!-- #ifdef MP-WEIXIN -->
		<ad unit-id="adunit-1e8b54cf8ddab247"></ad>
		<!-- #endif -->
		
		
		<view
			style="justify-content: flex-end;width: 750rpx;height: 152rpx;background: #FFFFFF;position: fixed;left:0;bottom:0;"
			class="u-flex-r u-col-center">
			<view class="u-flex-c" style="margin-left: 24rpx;">
				<view class="u-flex-r" style="">
					<view
						style="height: 40rpx;font-size: 24rpx;font-family: PingFangSC-Medium, PingFang SC;font-weight: 500;color: #000000;line-height: 40rpx;">
						合计:¥</view>
					<view
						style="height: 40rpx;font-size: 40rpx;font-family: PingFangSC-Medium, PingFang SC;font-weight: 500;color: #000000;line-height: 28rpx;">
						{{setMealInfo.currentPrice + amountsPayable}}
					</view>
				</view>
			</view>
			<button class="payButtom" @click="add" :loading='buttonStatus' :disabled="buttonStatus">立即支付</button>
		</view>

		<!-- 积分选择模块 -->
		<u-mask :show="scoreOptionShow" @click="offScoreOptionShow">
			<view class="setScore" @tap.stop>
				<view class="title">选择兑换积分</view>
				<view class="optionBox u-flex-r">
					<view class="dec" @click="decScore">
						<image src="../../static/image/my/jian2x.png" ></image>
					</view>
					<input type="number" confirm-type="done" v-model="scoreValue" @input="scoreNum" class="input" />
					<view class="add1" @click="addScore">
						<image src="../../static/image/my/jia2x.png" ></image>
					</view>
				</view>
			</view>
		</u-mask>
	</view>
</template>

<script>
	export default {
		data() {
			const currentDate = this.getDate({
				format: true
			})
			return {
				scoreOptionShow: false,
				scoreValue: 0,
				setMealInfo: '',
				parkingLotInfo: '',
				buttonStatus: false,
				amountsPayable: 0
			}
		},
		onLoad(e) {
			var {
				setMealInfo,
				parkingLotInfo
			} = e
			this.setMealInfo = JSON.parse(setMealInfo)
			this.parkingLotInfo = JSON.parse(parkingLotInfo)
			this.getArrears()
			// this.getDeatil(id)
			return
			this.monthCard = JSON.parse(e.datas)[0]
			this.lpn = e.lpn;
			this.monthCard = uni.getStorageSync("manualPayment_Data");
			this.monthCardBackup = uni.getStorageSync("manualPayment_Data");
			console.log(this.monthCard);
			this.amount = this.monthCard.payCharge
			// 获取可用的优惠券
			this.getCoupon()

		},
		watch: {
			couponList: function(e) {
				console.log(e)
				// if(e.length > 0){
				// 	this.couponId = 1
				// }
			},

			couponId: function(e) {
				console.log(e)
				if (e == 0) {
					this.amount = this.monthCard.payCharge
					this.couponType = ''
					return
				}
				var that = this
				var couponList = this.couponList
				var type = couponList[e - 1].discountType
				this.couponType = type
				console.log('type', type)
				if (type == 2) {
					uni.request({
						url: this.serviceUrl + 'payment/order/anon/getOrderByLpn',
						method: 'POST',
						data: {
							plateId: that.monthCard.plateId,
							hour: couponList[e - 1].hoursDiscount
						},
						header: {
							'Content-type': 'application/x-www-form-urlencoded',
							Accept: 'application/json',
							Authorization: "Bearer  " + this.token
						},
						success: res => {
							if (res.data.code == 1000) {
								that.monthCard = res.data.data
								that.amount = res.data.data.payCharge2
							} else {
								uni.showToast({
									title: '获取优惠券信息失败',
									icon: "none"
								});
							}
							console.log(res)
						},
						fail(res) {
							uni.showToast({
								title: '网络错误！',
								icon: 'none'
							});
						}
					});
				} else if (type == 3) {
					that.amount = 0
				} else if (type == 0) {
					this.monthCard = this.monthCardBackup
					var pay2 = this.monthCard.payCharge - couponList[e - 1].cashDiscount
					if (pay2 < 0) {
						that.amount = 0
					} else {
						that.amount = pay2
					}
				}
			}
		},
		methods: {
			async getArrears() {
				var {
					data,
					code
				} = await this.$u.api.queryCfCarParkPackageRecoverFee({
					cfCarParkPackagePriceId: this.setMealInfo.id,
					numberPlate: this.parkingLotInfo.numberPlate
				})
				if (code === 10002) {
					this.amountsPayable = data.cfOrder.amountsPayable
					if(data.cfOrder.amountsPayable > 0){
						uni.showModal({
							title:'提示',
							content:'该车辆需要缴纳追缴费用,点击确定查看详情',
							success({confirm}) {
								if(confirm){
									uni.navigateTo({
										url:'/pages/my/replacePay/pay?id=' + data.cfCarParkUseLog.id
									})
								}
							}
						})
					}
				}
			},
			scoreNum(e) {
				// console.log(e)
				var regNum = new RegExp('[0-9]', 'g');
				var rsNum = regNum.exec(e.detail.value);
				if (!rsNum) {
					setTimeout(() => {
						wx.showToast({
							title: '只能输入数字',
							icon: 'none'
						})
					}, 1000);
					return
				}
			},
			addScore() {
				this.scoreValue++
			},
			decScore() {
				if (this.scoreValue == 0) {
					return
				}
				this.scoreValue--
			},
			setScoreOptionShow() {
				this.scoreOptionShow = !this.scoreOptionShow
			},
			offScoreOptionShow() {
				this.scoreOptionShow = false
			},

			async getDeatil(id) {
				var res = await this.$u.api.queryOrderByUseId({
					id
				})
				console.log(res)
				console.log(JSON.stringify(res.data))
				if (res.code == 10002) {
					this.monthCard = res.data
				}
			},

			onCoupon(e) {
				var list = this.couponList
				var index = list.map(i => i.id).indexOf(e.id)
				console.log(index)
				this.couponId = index + 1
				this.modalShow = false
			},
			memberInfo() {
				this.modalShow = false
			},
			choiceCoupon() {
				if (this.couponList.length == 0) {
					return
				}
				this.modalShow = true
			},

			async getCoupon() {
				var res = await this.$http(this.serviceUrl + 'discount/getUserUsabledDiscounts', 'POST', {
					channelName: 2,
					category: 1,
					productType: 0,
					parkCode: this.monthCard.parkId,
					phone: uni.getStorageSync('phoneNum'),
					orderId: this.monthCard.orderId
				})
				console.log(res)
				if (res.code == 1000) {
					this.couponList = res.data.datas
				}
			},
			beginDateChange: function(e) {
				this.beginDate = e.target.value
			},
			endDateChange: function(e) {
				this.finishDate = e.target.value
			},
			getDate(type) {
				const date = new Date();
				let year = date.getFullYear();
				let month = date.getMonth() + 1;
				let day = date.getDate();

				if (type === 'start') {
					year = year - 60;
				} else if (type === 'end') {
					year = year + 2;
				}
				month = month > 9 ? month : '0' + month;;
				day = day > 9 ? day : '0' + day;
				return `${year}-${month}-${day}`;
			},
			async add() {
				var _ = this
				this.buttonStatus = true
				// #ifdef  MP-WEIXIN
				var payinfo = await this.$u.api.getPaymentAgencyListByCarParkId({
					carParkId: this.setMealInfo.carParkId,
					useScenes: 'wx_mp'
				})
				// #endif
				// #ifdef  MP-ALIPAY
				var payinfo = await this.$u.api.getPaymentAgencyListByCarParkId({
					carParkId: this.setMealInfo.carParkId,
					useScenes: 'ali_mp'
				})
				// #endif
				// #ifdef  H5
				var payinfo = await this.$u.api.getPaymentAgencyListByCarParkId({
					carParkId: this.setMealInfo.carParkId,
					useScenes: 'wx_web'
				})
				// #endif
				if (payinfo.code != 10002) {
					this.$u.toast(payinfo.message)
					this.buttonStatus = false
					return
				}
				var res = await this.$u.api.buyCfCarParkPackage({
					cfCarParkPackagePriceId: this.setMealInfo.id,
					numberPlate: this.parkingLotInfo.numberPlate,
					payTypeId: payinfo.data[0].id
				})
				if (res.code != 10002) {
					this.$u.toast(res.message)
					this.buttonStatus = false
					return
				} else {
					// #ifdef  H5
					this.wxPay(res.data.data)
					// #endif
					// #ifdef  MP-WEIXIN
					uni.requestPayment({
						provider: 'wxpay',
						timeStamp: res.data.data.timeStamp,
						nonceStr: res.data.data.nonceStr,
						package: res.data.data.package,
						signType: res.data.data.signType,
						paySign: res.data.data.sign,
						success: function(res) {
							_.buttonStatus = false
							console.log(res)
							if (res.errMsg == 'requestPayment:ok') {
								_.$u.toast('支付成功')
								setTimeout(() => {
									uni.navigateBack()
								}, 1500)
							}else{
								_.buttonStatus = false
							}
						},
						fail: function(err) {
							_.buttonStatus = false
							console.log('fail:' + JSON.stringify(err));
						}
					});
					// #endif
					// #ifdef  MP-ALIPAY
					uni.requestPayment({
						provider: 'alipay',
						orderInfo:res.data.data.alipay_trade_create_response.trade_no,
						success: function(res) {
							_.buttonStatus = false
							console.log(123123,res)
							// requestPayment:ok
							if (res.resultCode == '9000') {
								_.$u.toast('订单支付成功')
								setTimeout(() => {
									uni.navigateBack()
								}, 1500)
							}else{
								_.$u.toast(res.memo)
							}
						},
						fail: function(err) {
							_.buttonStatus = false
							console.log('fail:' + JSON.stringify(err));
						}
					});
					// #endif
				}
			},
			wxPay(datas) {
				var _ = this
				window.WeixinJSBridge.invoke(
					"getBrandWCPayRequest", {
						appId: datas.appId, //公众号名称，由商户传入
						timeStamp: String(datas
							.timeStamp
						), //时间戳，自1970年以来的秒数。这里必须要转换为字符串。ios跟android表现不同。后台返回的是数值，但是微信方面必须要json参数都是字符串形式，android会自动转换成字符串（当时我在这里也找了很久的博文才知道的）
						nonceStr: datas.nonceStr, //随机串
						package: datas.package,
						signType: datas.signType, //微信签名方式：
						paySign: datas.sign, //微信签名
					},
					function(res) {
						_.buttonStatus = false
						if (res.err_msg === "get_brand_wcpay_request:ok") {
							uni.showModal({
								title: "提示",
								message: "支付成功",
								confirmColor: "#488cf5",
							});
						} else if (res.err_msg === "get_brand_wcpay_request:cancel") {
							uni.showModal({
								title: "提示",
								content: "支付取消",
								confirmColor: "#488cf5",
							});
						} else {
							uni.showModal({
								title: "提示",
								message: "支付失败",
								confirmColor: "#488cf5",
							});
						}
					}
				);
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f7f7;


	}

	.list {
		background-color: #FFFFFF;
		margin-top: 24upx;

		>.li {
			font-size: 28upx;
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			border-bottom: 2rpx solid #fafafa;
			padding: 30upx;

			.left {
				color: #898989;
			}

			.right {
				color: #000;
			}
		}
	}

	.add {

		position: fixed;
		bottom: 5vw;
		width: 90vw;
		height: 90upx;
		background-color: #6977D0;
		margin: 0upx 5vw;
		text-align: center;
		line-height: 90upx;
		border-radius: 10upx;
		font-size: 32upx;
		color: #FFFFFF;
	}

	.payButtom {
		width: 282rpx;
		height: 96rpx;
		border-radius: 16rpx;
		background: #488cf5;
		font-size: 36rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 96rpx;
		text-align: center;
		margin-right: 34rpx;
	}

	.PreferentialDeduction {
		height: 24rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FF7165;
		line-height: 24rpx;
	}

	.bottomLine {
		border-bottom: 1rpx solid #eaeaea;
	}

	.setScore {
		width: 750rpx;
		height: 346rpx;
		background: #FFFFFF;
		border-radius: 46rpx 46rpx 0rpx 0rpx;
		position: fixed;
		left: 0;
		bottom: 0;

		.title {
			width: 216rpx;
			height: 88rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 88rpx;
			margin: 32rpx 0 0 32rpx;
		}

		.optionBox {
			width: 288rpx;
			height: 88rpx;
			border-radius: 16rpx;
			border: 2rpx solid #DDDDDD;
			margin: 42rpx 0 0 232rpx;

			.dec {
				width: 80rpx;
				height: inherit;

				image {
					width: 32rpx;
					height: 32rpx;
					margin: 30rpx 0 0 24rpx;
				}
			}

			.input {
				width: 128rpx;
				height: 88rpx;
				text-align: center;
			}

			.add1 {
				width: 80rpx;
				height: inherit;

				image {
					width: 32rpx;
					height: 32rpx;
					margin: 30rpx 0 0 24rpx;
				}
			}
		}
	}
</style>
