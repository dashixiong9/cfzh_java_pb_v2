<template>
	<view>

		<view class="list">
			<view class="li bottomLine">
				<view class="left">停车场</view>
				<view class="right">{{monthCard.cfCarPark.name}}</view>
			</view>
			<view class="li bottomLine">
				<view class="left">车牌</view>
				<view class="right">{{monthCard.cfCarParkUseLog.numberPlate}}</view>
			</view>
			<view style="width: 750rpx;height: 24rpx;background-color: #F8F9FB;"></view>
			<view class="li bottomLine">
				<view class="left">进场时间</view>
				<view class="right">
					{{monthCard.cfCarParkUseLog.inTime ? $u.timeFormat(monthCard.cfCarParkUseLog.inTime, 'yyyy-mm-dd hh:MM:ss') : '-'}}
				</view>
			</view>
			<view class="li">
				<view class="left">停车时长</view>
				<view class="right">{{newOutTime}}</view>
			</view>
		</view>
		<view class="list">
			<view class="li">
				<view class="left">应付金额</view>
				<view class="right">
					{{!monthCard.cfCarParkUseLog.inTime ? 0 : (monthCard.cfOrder.amountsPayable || 0)}}元
				</view>
			</view>

			<view class="li">
				<view class="left u-flex-r u-col-center">
					<image src="../../../static/image/my/juan2x.png" style="width: 36rpx;height: 36rpx;"></image>
					<view class="left" style="margin-left: 4rpx;">停车券</view>
				</view>
				<!-- <view class="right">{{monthCard.payCharge / 100}}元</view> -->
				<view class="right" v-if="!couponList.length">暂无停车场券</view>
				<view class="right u-flex" v-else @click="maskShow = true">
					<view v-if="couponDetail.couponType == 0">
						选择停车券
					</view>
					<view v-if="couponDetail.couponType == 1">
						{{couponDetail.status ? (couponDetail.denomination + '次 - 次数券') : '选择停车券'}}
					</view>
					<view v-if="couponDetail.couponType == 2">
						{{couponDetail.status ? (couponDetail.denomination + '元 - 金额券') : '选择停车券'}}
					</view>
					<view v-if="couponDetail.couponType == 3">
						{{couponDetail.status ? ((couponDetail.denomination / 3600000) + '小时 - 时长券') : '选择停车券'}}
					</view>
					<image src="../../../static/image/my/Dropdown.png" style="width: 32rpx;height: 32rpx;">
					</image>
				</view>

			</view>
		</view>


		<view
			style="margin:30rpx 0 0 32rpx;font-size: 24rpx;height: 68rpx;font-family: PingFangSC-Regular, PingFang SC;font-weight: 400;color: rgba(50, 50, 51, 0.26);line-height: 34rpx;"
			v-if="monthCard.cfCarPark.freeTime">
			温馨提示：为了避免重复扣费请于缴费后{{$u.timeFormat(monthCard.cfCarPark.freeTime, 'MM')}}分钟内离场</view>




		<view style="justify-content: flex-end;width: 750rpx;height: 152rpx;background: #FFFFFF;"
			class="u-flex-r u-col-center">
			<view class="u-flex-c" style="margin-right: 24rpx;">
				<view class="u-flex-r" style="">
					<view class="totalText">合计:</view>
					<view class="totalNum">
						¥ {{monthCard.cfOrder.amountsPayable || ''}}
					</view>
					<!-- <view class="totalNum" v-if="couponDetail.couponType == 1">
						¥ 全免
					</view>
					<view class="totalNum" v-if="couponDetail.couponType == 2">
						¥ {{Math.max(actualAmountPaid - couponDetail.denomination, 0)}}
					</view>
					<view class="totalNum" v-if="couponDetail.couponType == 3">
						¥
						{{Math.max(actualAmountPaid - (monthCard.cfCarPark.currentPrice * (couponDetail.denomination / 3600000)), 0)}}
					</view> -->
					<!-- <view class="totalNum" v-if="!couponDetail.status">
						¥{{actualAmountPaid}}
					</view>
					<view class="totalNum" v-else>
						{{(actualAmountPaid - couponDetail.denomination) <= 0 ? 0 : (actualAmountPaid - couponDetail.denomination) }}
					</view> -->
				</view>
				<view class="PreferentialDeduction" v-if="couponDetail.status && couponDetail.couponType == 1">
					停车券 - {{couponDetail.denomination}}次</view>
				<view class="PreferentialDeduction" v-if="couponDetail.status && couponDetail.couponType == 2">
					停车券 - {{couponDetail.denomination}}元</view>
				<view class="PreferentialDeduction" v-if="couponDetail.status && couponDetail.couponType == 3">
					停车券 - {{couponDetail.denomination / 3600000}}小时</view>
			</view>
			<u-button @click="add" :custom-style="customStyle" ripple :disabled='payDisabled' :loading='payLoading'
				v-if="needPay">
				立即支付
			</u-button>
			<u-button @click="add" :custom-style="customStyleNoPay" ripple :disabled='true' v-if="!needPay">
				无需支付
			</u-button>
		</view>


		<!-- 广告模块 -->
		<!-- 单图 -->
		<!-- 	<div class="advertisBox_Single" v-if='adList.length == 1'>
			<image @click="onAd(adList[0])" :src="adList[0].cfAd.cover"  style="width: 100%;height: 100%;"
				mode="aspectFill"></image>
		</div> -->
		<!-- 多框 -->
		<!-- 	<div class="advertisBox_Many" v-else>
			<div class='advertis_item' v-for="(item,index) in adList" :key='item.id' @click="onAd(item)">
				<image :src="item.cfAd.cover"  class="advertis_img"></image>
				<div class="advertis_price">
					{{item.cfAd.title}}
				</div>
				<div class="u-line-1 advertis_txt">
					{{item.cfAd.subtitle}}
				</div>
			</div>
		</div> -->

		<!-- #ifdef MP-WEIXIN -->
		<ad unit-id="adunit-1e8b54cf8ddab247"></ad>
		<!-- #endif -->


		<!-- 积分选择模块 -->
		<u-mask :show="scoreOptionShow" @click="offScoreOptionShow">
			<view class="setScore" @tap.stop>
				<view class="title">选择兑换积分</view>
				<view class="optionBox u-flex-r">
					<view class="dec" @click="decScore">
						<image src="../../../static/image/my/jian2x.png"></image>
					</view>
					<input type="number" confirm-type="done" v-model="scoreValue" @input="scoreNum" class="input" />
					<view class="add1" @click="addScore">
						<image src="../../../static/image/my/jia2x.png"></image>
					</view>
				</view>
			</view>
		</u-mask>


		<!-- #ifdef MP-WEIXIN -->
		<u-mask :show="show">
			<view class="authorize">
				<view class="title">
					微信授权
				</view>
				<view class="ts">
					请授权头像等信息，以便为你提供更好的服务
				</view>
				<view class="buttons">
					<button @click='wxLogin'>授权登录</button>
				</view>
			</view>
		</u-mask>
		<!-- #endif -->


		<!-- #ifdef MP-ALIPAY -->
		<u-mask :show="show">
			<view class="authorize">
				<view class="title">
					支付宝授权
				</view>
				<view class="ts">
					请授权头像等信息，以便为你提供更好的服务
				</view>
				<view class="buttons">
					<button @click="aliLogin">
						点击授权
					</button>
				</view>
			</view>
		</u-mask>
		<!-- #endif -->


		<u-mask :show="maskShow" @click="maskShow = false">
			<view style="width: 100%;height: 100%;" class="u-flex">
				<view class="choose" @tap.stop>
					<view class="u-flex u-col-center">
						<view class="title">选择优惠券</view>
						<view @click="doNotUseCoupon" style="margin-left: 274rpx;margin-top: 36rpx;color: #488cf5;">
							不使用优惠券</view>
					</view>
					<view class="subtitle">选择优惠券付款更优惠哦～</view>
					<scroll-view scroll-y="true" style="widows: 670rpx;height: 550rpx;" v-if="couponList.length > 0">
						<view class="licensePlate" v-for="item of couponList" @click="onScrollCoupon(item)">
							<view class="title1" v-if="item.couponType == 1">{{item.denomination}}次 - 次数券</view>
							<view class="title1" v-if="item.couponType == 2">{{item.denomination}}元 - 金额券</view>
							<view class="title1" v-if="item.couponType == 3">{{item.denomination / 3600000}}小时 - 时长券
							</view>
							<view class="subTitle1">{{item.goodsId}}可用</view>
						</view>
					</scroll-view>
					<view v-else style="width: 100%;" class="u-flex-col u-col-center">
						<image src="../../../static/image/home/nocar.png"
							style="width: 220rpx;height: 108rpx;margin-top:20rpx;"></image>
						<view
							style="margin:16rpx 0 0 0;width: 362rpx;height: 32rpx;font-family: PingFangSC-Regular, PingFang SC;font-weight: 400;color: #CDCDCD;line-height: 32rpx;">
							...(- -)..您还未绑定车牌哦~</view>
					</view>
				</view>
			</view>
		</u-mask>


		<u-mask :show="paySuccess">
			<view style="width: 100%;height: 100%;" class="u-flex">
				<view class="paySuccessBox u-flex-col u-col-center">
					<image src="../../../static/image/my/paySucc.png" class="img"></image>
					<view class="paySuccTitle">您已成功支付</view>
					<view class="paySuccTitle1">为避免重复扣费，请您在15分钟类离场15分钟后将重新计费</view>
					<view class="confirm" @click="payConfirm">好的，我知道了</view>
				</view>
			</view>
		</u-mask>




	</view>
</template>

<script>
	import {
		setLocal,
		getLocal
	} from '../../../common/storage.js'
	import {
		wxAppid,
		aliAppid
	} from '../../../common/config.js'
	export default {
		data() {
			return {
				code: '',
				adList: [],
				maskShow: false,
				paySuccess: false,
				customStyle: {
					marginTop: '40rpx', // 注意驼峰命名，并且值必须用引号包括，因为这是对象
					color: 'red',
					width: '282rpx',
					height: '96rpx',
					borderRadius: '16rpx',
					background: '#488cf5',
					fontSize: '36rpx',
					fontFamily: 'PingFangSC-Medium, PingFang SC',
					fontWeight: 500,
					color: '#FFFFFF',
					lineHeight: '96rpx',
					textAlign: 'center',
					marginRight: '34rpx',
					marginBottom: '40rpx'
				},
				customStyleNoPay: {
					marginTop: '40rpx', // 注意驼峰命名，并且值必须用引号包括，因为这是对象
					color: 'red',
					width: '282rpx',
					height: '96rpx',
					borderRadius: '16rpx',
					background: '#488cf5',
					fontSize: '36rpx',
					opacity: '0.5',
					fontFamily: 'PingFangSC-Medium, PingFang SC',
					fontWeight: 500,
					color: '#FFFFFF',
					lineHeight: '96rpx',
					textAlign: 'center',
					marginRight: '34rpx',
					marginBottom: '40rpx'
				},
				payDisabled: false,
				payLoading: false,
				scoreOptionShow: false,
				scoreValue: 0,
				user: uni.getStorageSync('user'),
				actualAmountPaid: '',
				show: false,
				couponDetail: {
					status: false,
					denomination: 0,
					couponId: '',
					couponType: 0
				},
				modalShow: false,
				lpn: "",
				monthCard: {
					cfCarParkUseLog: {
						inTime: ''
					},
					cfCarPark: {
						freeTime: ''
					},
					cfCouponList: []
				},
				monthCardBackup: {},
				beginDate: '',
				finishDate: '',
				couponList: [],
				couponId: '',
				selectEndtime: '',
				amount: '',
				couponType: '',
				newOutTime: '',
				checkPointId: '',
				id: '',
				carpark_checkpoint_id: '',

				couponFirst: false, // 用于记录首次加载数据 优惠券默认index
			}
		},
		computed: {
			// 判断实际支付金额是否小于等于0 
			needPay() {
				if (this.actualAmountPaid <= 0 || this.actualAmountPaid == 894787) {
					return false
				} else {
					return true
				}
			}
		},
		onLoad(e) {
			console.log(e, '获取到的参数');
			var {
				id,
				checkPointId,
				carpark_checkpoint_id,
				scene,
				test
			} = e

			// 微信扫码进的小程序
			if (e.q) {
				const q = decodeURIComponent(e.q) // 获取到二维码原始链接内容
				console.log(q, '获取到二维码原始链接内容,获取到二维码原始链接内容');
				const checkpointId = q.split("/carpark_checkpoint_id/")
				this.checkScan(checkpointId[1])
				this.carpark_checkpoint_id = checkpointId[1]
			} else {
				this.id = id
				if (scene) {
					const checkPointArr = scene.split('%253D')
					if (checkPointArr.length == 2) {
						this.checkPointId = checkPointArr[1]
						if (uni.getStorageSync('wxauthed')) {
							this.getCodeIdInfo(checkPointArr[1])
						}
					}
				} else if (carpark_checkpoint_id) {
					this.carpark_checkpoint_id = carpark_checkpoint_id
					this.checkScan(carpark_checkpoint_id)
				} else {
					this.getDeatil(id)
				}
			}
		},
		onShow() {
			// #ifdef MP-WEIXIN
			this.getCode()
			// #endif
			if (!uni.getStorageSync('token')) {
				this.show = true
				setLocal('wxauthed', false)
			} else {
				this.show = false
				setLocal('wxauthed', true)
			}
		},
		watch: {
			monthCard: function(e) {
				var inTime = this.monthCard.cfCarParkUseLog.inTime
				if (!inTime) {
					this.newOutTime = '-'
					return
				}
				var outTime = this.monthCard.cfCarParkUseLog.outTime ? this.monthCard.cfCarParkUseLog.outTime :
					new Date()
				var times = new Date(outTime).getTime() - new Date(inTime).getTime()
				var difftime = times / 1000
				var days = parseInt(difftime / 86400); // 天  24*60*60*1000 
				var hours = parseInt(difftime / 3600) - 24 * days; // 小时 60*60 总小时数-过去的小时数=现在的小时数 
				var minutes = parseInt(difftime % 3600 / 60); // 分钟 -(day*24) 以60秒为一整份 取余 剩下秒数 秒数/60 就是分钟数
				var seconds = parseInt(difftime % 60); // 以60秒为一整份 取余 剩下秒数
				this.newOutTime = (days ? (days + '天') : '') + hours + '小时' + minutes + '分钟' + seconds + '秒'
			}
		},
		methods: {
			onAd(row) {
				// 跳小程序
				if (row.cfAd.appid) {
					uni.navigateToMiniProgram({
						appId: row.cfAd.appid
					})
				} else {
					// #ifdef H5
					window.location.href = row.cfAd.sourceAddress
					// #endif
					// #ifdef MP-WEIXIN
					uni.navigateTo({
						url: `../../webView/index?url=${row.cfAd.sourceAddress}`
					})
					// #endif
				}
			},
			aliLogin() {
				uni.getAuthCode({
					scopes: ['auth_user'],
					success: async (res) => {
						if (res.authCode) {
							var loginRes = await this.$u.api.getAliMinBaseUserInfoByCode({
								auth_code: res.authCode,
								appid: aliAppid
							})
							if (loginRes.code === 10002) {
								this.userinfo = loginRes.data;
								setLocal('user', loginRes.data)
								setLocal('token', loginRes.token.jwt_token)
								this.$u.vuex('vuex_token', loginRes.token.jwt_token)
								this.$u.vuex('vuex_user', loginRes.data)
								this.show = false
								this.checkScan(this.carpark_checkpoint_id)
							} else {
								uni.showToast({
									title: loginRes.message,
									icon: 'none'
								})
							}
						}
					},
				});
			},
			async checkScan(id) {
				var that = this
				var {
					data,
					code
				} = await this.$u.api.getLastCarParkUseLogList({
					outCheckPointId: id
				})
				console.log(381, data);
				if (code != 10002) {
					console.log('id', id);

					var applyForAccessRes = await this.$u.api.applyForAccess({
						checkPointId: id,
						direction: "out",
						type: 1,
					});

					if (applyForAccessRes.code == 10002) {
						if (applyForAccessRes.data.payTime > 0) {
							uni.showToast({
								title: '无需支付 请通行',
								icon: 'none'
							})
						} else {
							that.getDeatil(applyForAccessRes.data.id);
						}
					} else {
						uni.showToast({
							title: '暂无数据 请手动输入车牌缴费',
							icon: 'none'
						})
						setTimeout(() => {
							uni.navigateTo({
								url: 'replacePay'
							})
						}, 1500)
					}
				} else {
					this.monthCard = data 
					this.actualAmountPaid = data.cfOrder.amountsPayable
					this.couponList = data.cfCouponList
					if (data.cfCouponList.length > 0 && !this.couponFirst) {
						this.couponFirst = true
						this.couponDetail = data.cfCouponList[0]
					}
				}
			},
			// async getQueryOrderByUseId(id) {
			// 	var res = await this.$u.api.queryOrderByUseId({
			// 		id,
			// 	});
			// 	if (res.code == 10002) {
			// 		this.monthCard = res.data
			// 		this.actualAmountPaid = res.data.cfOrder.amountsPayable
			// 		this.couponList = res.data.cfCouponList
			// 		if (res.data.cfCouponList.length > 0 && !this.couponFirst) {
			// 			this.couponFirst = true
			// 			this.couponDetail = res.data.cfCouponList[0]
			// 		}
			// 	}
			// },
			async getAd() {
				var {
					data,
					code
				} = await this.$u.api.selectContainAdByQuery()
				if (code == 10002) {
					this.adList = data
				}
			},
			// 点击了选择优惠券列表
			onScrollCoupon(item) {
				var {
					denomination,
					id,
					goodsId
				} = item
				if (goodsId != this.monthCard.cfCarParkUseLog.numberPlate) {
					this.$u.toast('该优惠券此订单不可用')
					return
				}
				this.couponDetail = item
				this.couponDetail.status = true
				// this.couponDetail.denomination = denomination
				// this.couponDetail.couponId = id
				this.maskShow = false
				this.getDeatil(this.id)
			},
			// 点击不使用优惠券
			doNotUseCoupon() {
				this.couponDetail.status = false
				this.couponDetail.denomination = 0
				this.couponDetail.couponId = ''
				this.couponDetail.couponType = 1
				this.maskShow = false
			},
			payConfirm() {
				uni.switchTab({
					url: '/pages/my/index'
				})
			},
			getCode() {
				wx.login({
					success: (res) => {
						console.log(res);
						this.code = res.code
					}
				})
			},
			wxLogin() {
				var _ = this
				uni.showLoading({
					title: '请稍等'
				})

				uni.getUserProfile({
					desc: '保存用户数据',
					success: async ({
						iv,
						encryptedData
					}) => {
						var data = {
							iv,
							code: this.code,
							loginType: 'mp',
							encryptedData,
							appid: wxAppid
						}
						var loginRes = await this.$u.api.wxLogin(data)
						console.log('登陆个人信息', loginRes)
						uni.hideLoading()
						// code 失效
						if (loginRes.code === 24017) {
							uni.showToast({
								title: 'code失效 请重试一次',
								icon: 'none'
							})
							_.getCode()
						} else if (loginRes.code == 10002) {
							this.userinfo = loginRes.data;
							setLocal('user', loginRes.data)
							setLocal('token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_user', loginRes.data)
							this.getCodeIdInfo(this.carpark_checkpoint_id || this.checkPointId)
							this.show = false
						} else {
							this.$u.toast('登陆出错')
						}
					}
				})
			},
			async getCodeIdInfo(outCheckPointId) {
				var res = await this.$u.api.getLastCarParkUseLogList({
					outCheckPointId
				})
				if (res.data == null || res.data == '') {
					uni.showModal({
						title: '提示',
						content: '没有查询到相关信息，请手动输入车牌号缴费',
						confirmText: '去输入',
						showCancel: false,
						success: rres => {
							uni.reLaunch({
								url: 'replacePay'
							})
						}
					})
					return
				}
				if (res.code == 10002) {
					this.monthCard = res.data
					this.actualAmountPaid = res.data.cfOrder.amountsPayable
					this.couponList = res.data.cfCouponList
					if (res.data.cfCouponList.length > 0 && !this.couponFirst) {
						this.couponFirst = true
						this.couponDetail = res.data.cfCouponList[0]
					}
				} else {
					uni.showModal({
						title: '提示',
						content: '没有查到当前出口车辆订单，请手动输入车牌进行查询和缴费',
						showCancel: false,
						confirmText: '确定',
						success: res => {
							uni.reLaunch({
								url: 'replacePay'
							})
						},
						fail: () => {},
						complete: () => {}
					});
				}
			},
			scoreNum(e) {
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
				if (this.user.score == 0) {
					this.$u.toast('您没有积分')
					return
				}
				this.scoreOptionShow = !this.scoreOptionShow
			},
			offScoreOptionShow() {
				this.scoreOptionShow = false
			},

			async getDeatil(id) {
				const paramsData = this.couponDetail.status ? {
					id,
					couponId: this.couponDetail.id
				} : {
					id
				}
				var res = await this.$u.api.queryOrderByUseId(paramsData)
				if (res.code == 10002) {
					this.monthCard = res.data
					this.actualAmountPaid = res.data.cfOrder.amountsPayable
					this.couponList = res.data.cfCouponList
					if (res.data.cfCouponList.length > 0 && !this.couponFirst) {
						this.couponFirst = true
						this.couponDetail = res.data.cfCouponList[0]
					}
					// 获取可用的优惠券
					// this.getCoupon(res.data.cfCarPark.id,res.data.cfCarParkUseLog.numberPlate)
				}
			},

			onCoupon(e) {
				var list = this.couponList
				var index = list.map(i => i.id).indexOf(e.id)
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
			// async getCoupon(shopId,numberPlate) {
			// 	var res = await this.$u.api.getMyConpon({
			// 		status:1,
			// 		page: 1,
			// 		size: 1000,
			// 		goodsId:numberPlate,
			// 		shopId
			// 	})
			// 	if (res.code == 10002) {
			// 		this.couponList = res.data;
			// 	}
			// },

			async add() {
				var that = this
				this.payDisabled = true
				this.payLoading = true
				// #ifdef MP-WEIXIN
				var payinfo = await this.$u.api.getPaymentAgencyListByCarParkId({
					carParkId: this.monthCard.cfCarPark.id,
					useScenes: 'wx_mp'
				})
				// #endif
				// #ifdef  MP-ALIPAY
				var payinfo = await this.$u.api.getPaymentAgencyListByCarParkId({
					carParkId: this.monthCard.cfCarPark.id,
					useScenes: 'ali_mp'
				})
				// #endif
				// #ifdef H5
				var useScenes;
				if (/MicroMessenger/.test(window.navigator.userAgent)) {
					useScenes = 'wx_web'
				} else if (/AlipayClient/.test(window.navigator.userAgent)) {
					useScenes = 'ali_web'
				}
				var payinfo = await this.$u.api.getPaymentAgencyListByCarParkId({
					carParkId: this.monthCard.cfCarPark.id,
					useScenes
				})
				// #endif
				if (payinfo.code != 10002) {
					console.log('payinfo', payinfo);
					this.$u.toast(payinfo.message)
					this.payDisabled = false
					this.payLoading = false
					return
				}
				var res = await this.$u.api.payForCarUseLog({
					payTypeId: payinfo.data[0].id,
					id: this.monthCard.cfCarParkUseLog.id,
					couponId: this.couponDetail.id
				})
				if (res.code != 10002) {
					this.$u.toast(res.message)
					this.payDisabled = false
					this.payLoading = false
					return
				} else {
					// #ifdef  H5
					if (/MicroMessenger/.test(window.navigator.userAgent)) {
						this.wxPay(res.data.data)
					} else if (/AlipayClient/.test(window.navigator.userAgent)) {
						this.aliPay(res.data.data)
					}
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
							if (res.errMsg == 'requestPayment:ok') {
								that.paySuccess = true
								that.payDisabled = false
								that.payLoading = false
							}
						},
						fail: function(err) {
							console.log(err);
							that.payDisabled = false
							that.payLoading = false
						}
					});
					// #endif
					// #ifdef  MP-ALIPAY
					uni.requestPayment({
						provider: 'alipay',
						orderInfo: res.data.data.alipay_trade_create_response.trade_no,
						success: function(res) {
							that.buttonStatus = false
							if (res.resultCode == '9000') {
								that.$u.toast('订单支付成功')
								setTimeout(() => {
									uni.navigateBack()
								}, 1500)
							} else {
								let title = '用户中途取消';
								if(res.resultCode=='6001'){
									title = res.memo ? res.memo : '用户中途取消'
								}
								that.$u.toast(title)
							}
						},
						fail: function(err) {
							console.log('fail:' + JSON.stringify(err));
						}
					});
					// #endif
				}
			},
			// 阿里h5支付
			aliPay(tradeNO) {
				var that = this
				AlipayJSBridge.call("tradePay", {
					tradeNO
				}, function(data) {
					if (data.resultCode == 9000) {
						that.paySuccess = true
						that.payDisabled = false
						that.payLoading = false
					} else {
						uni.showToast({
							title: data.memo,
							icon: 'none'
						})
						that.payDisabled = false
						that.payLoading = false
					}
				});
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
						_.payDisabled = false
						_.payLoading = false
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
		background-color: #F8F9FB;


	}

	.list {
		background-color: #FFFFFF;
		margin-top: 30upx;

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
		background: $u-theme-color;
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
		text-align: right;
		margin-top: 6rpx;
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

	.authorize {
		height: 440rpx;
		width: 500rpx;
		margin-left: 125rpx;
		margin-top: 300rpx;
		background: #fff;
		border-radius: 40rpx;
		padding-top: 50rpx;

		.title {
			font-size: 32upx;
			font-weight: 600;
			text-align: center;
		}

		.ts {
			font-size: 28upx;
			margin: 60upx 0upx;
			width: 400rpx;
			margin-left: 50rpx;
			text-align: center;
		}

		.buttons {
			display: flex;
			flex-direction: row;
			justify-content: center;

			>button {}
		}
	}

	.totalText {
		height: 40rpx;
		font-size: 36rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #000000;
		line-height: 40rpx;
		// margin-left: 154rpx;
	}

	.totalNum {
		height: 40rpx;
		font-size: 48rpx;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FF5151;
		line-height: 28rpx;
		margin-left: 16rpx;
	}

	.choose {
		width: 750rpx;
		// height: 530rpx;
		background: #FFFFFF;
		border-radius: 48rpx 48rpx 0rpx 0rpx;
		align-self: flex-end;

		.title {
			height: 60rpx;
			font-size: 44rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 60rpx;
			margin: 40rpx 0 0 40rpx;
		}

		.subtitle {
			height: 34rpx;
			font-size: 24rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #323233;
			line-height: 34rpx;
			margin: 4rpx 0 0 40rpx;
		}

		.licensePlate {
			background: #F8F9FB;
			border-radius: 12rpx;
			border: 2rpx solid rgba(151, 151, 151, 0.08);
			width: 670rpx;
			height: 136rpx;
			border-radius: 12rpx;
			font-size: 44rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #323233;
			line-height: 136rpx;
			padding-left: 32rpx;
			margin-left: 42rpx;
			margin-top: 40rpx;

			.title1 {
				// width: 116rpx;
				height: 60rpx;
				font-size: 44rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 60rpx;
				margin-top: 20rpx;
			}

			.subTitle1 {
				width: 284rpx;
				height: 32rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 32rpx;
				margin-top: 4rpx;
			}
		}

		.addCarBox {
			width: 670rpx;
			height: 136rpx;
			background: rgba(72, 140, 245, 0.08);
			border-radius: 12rpx;
			border: 3rpx dashed #488CF5;
			margin: 46rpx 0 0 40rpx;
			margin-bottom: 32rpx;
			margin-top: 20rpx;

			image {
				width: 32rpx;
				height: 32rpx;
			}

			.text {
				width: 144rpx;
				height: 50rpx;
				font-size: 36rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #488CF5;
				line-height: 50rpx;
			}
		}
	}

	.couponSubtitle {
		width: 284rpx;
		height: 32rpx;
		font-size: 24rpx;
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #323233;
		line-height: 32rpx;
	}

	.paySuccessBox {
		width: 624rpx;
		height: 776rpx;
		background: #FFFFFF;
		border-radius: 16rpx;
		margin-left: 64rpx;

		.img {
			width: 200rpx;
			height: 200rpx;
			margin-top: 96rpx;
		}

		.paySuccTitle {
			width: 220rpx;
			height: 88rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 88rpx;
			margin-top: 16rpx;
		}

		.paySuccTitle1 {
			width: 492rpx;
			height: 88rpx;
			font-size: 28rpx;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: rgba(50, 50, 51, 0.4);
			line-height: 44rpx;
			margin-top: 10rpx;
			text-align: center;
		}

		.confirm {
			width: 456rpx;
			height: 100rpx;
			background: #488CF5;
			border-radius: 16rpx;
			font-size: 36rpx;
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #FFFFFF;
			line-height: 100rpx;
			text-align: center;
			margin-top: 118rpx;
		}
	}

	.advertisBox_Single {
		width: 670rpx;
		height: 236rpx;
		margin: 50rpx 0 0 40rpx;
	}

	.advertisBox_Many:after {
		content: '';
		width: 42%;
		border: 1px solid transparent;
	}

	.advertisBox_Many {
		margin-top: 40rpx;
		display: flex;
		justify-content: space-evenly;
		flex-wrap: wrap;



		.advertis_item {
			width: 320rpx;
			height: 434rpx;
			background: #FFFFFF;
			border-radius: 10rpx;
			display: flex;
			flex-direction: column;
			margin-bottom: 20rpx;

			.advertis_img {
				width: 272rpx;
				height: 272rpx;
				background: #D8D8D8;
				border-radius: 6rpx;
				margin: 24rpx 0 16rpx 24rpx;
			}

			.advertis_txt {
				width: 252rpx;
				height: 40rpx;
				font-size: 28rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 40rpx;
				margin: 10rpx 0 0rpx 24rpx;
			}

			.advertis_price {
				height: 50rpx;
				font-size: 36rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #FF5151;
				line-height: 50rpx;
				margin: 0rpx 0 0rpx 24rpx;
			}
		}
	}
</style>