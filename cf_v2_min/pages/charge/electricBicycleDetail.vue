<template>
	<view>
		<view class="nameBox">
			<view class="name u-line-1">{{info.stationName}}</view>
			<view class="u-flex addressBox"
				@click="openMap(info.positionY,info.positionX,info.stationName,info.address)">
				<image class='icon' src="../../static/image/home/clock.png" style="width: 32rpx;height: 32rpx;">
				</image>
				<view class="address u-line-1">00:00:00-24:00:00</view>
			</view>
			<view class="u-flex addressBox"
				@click="openMap(info.positionY,info.positionX,info.stationName,info.address)"
				style="margin-top: 18rpx;">
				<image class='icon' src="../../static/image/home/Positioning.png" style="width: 32rpx;height: 32rpx;">
				</image>
				<view class="address u-line-1">{{info.address}}</view>
			</view>
			<image src="http://file.cfeng.wang/Navigationicon2x2.png" class="navIcon"
				@click="openMap(info.positionY,info.positionX,info.stationName,info.address)"></image>
		</view>
		<view class="infoBox">
			<view class="portBox">
				<view class="title">
					充电设备
				</view>
				<view style="width: 670rpx;height: 2rpx;background: #EAEAEA;"></view>

				<view class="portScroll">
					<scroll-view scroll-y="true" style="width: 100%;">
						<view @click='getPortList(item.id,index)'
							style="width: 590rpx;height: 88rpx;display: flex;justify-content: space-between;align-items: center;border-bottom:2rpx solid #EAEAEA;"
							v-for="(item,index) in deviceList" :key='item.id'>
							<view style="display: flex;">
								<view class="aliasName">{{item.aliasName}}</view>
								<view class="portName" v-if="port">{{port.portNumber}}号口</view>
							</view>
							<image src="../../static/image/home/未选中@2x.png" style="width:28rpx;height:28rpx"
								v-if="deviceIndex !== index"></image>
							<image src="../../static/image/home/选中@2x.png" style="width:28rpx;height:28rpx"
								v-if="deviceIndex === index"></image>
						</view>
					</scroll-view>
				</view>
			</view>
			<!-- 		<view class="typeBox">
				
			</view> -->
			<view class="timeBox">
				<view class="title">
					充电时长
				</view>
				<view style="width: 670rpx;height: 2rpx;background: #EAEAEA;"></view>
				<view class="" style="display: flex;flex-wrap: wrap;justify-content: space-evenly;">
					<view :class="timeIndex === index ? 'timeOption redBorder' : 'timeOption'" v-for="(item,index) in timeArr" :key='item.time' @click="setTimeIndex(index)">
						<image src="../../static/image/home/闪电背景@2x.png" class="bg"></image>
						<view class="content">
							<view class="left">{{item.time}}分钟</view>
							<view class="right">¥{{item.price}}</view>
						</view>
					</view>
				</view>
			</view>
			<view class="balanceBox">
				<view class="title">账户余额</view>
				<view class="num">{{balance}}元</view>
				<view class="recharge" @click="intoRecharge">充值</view>
			</view>
			<view class="illustrateBox">
				<view class="title">
					收费说明
				</view>
				<view style="width: 670rpx;height: 2rpx;background: #EAEAEA;"></view>
				<view class="text">充电每小时<span style='color:#488CF5'>{{chargingRules[0].fee}}元</span></view>
			</view>
		</view>
		<image class="selectionperiod" src="../../static/image/home/start.png" @click="startCharging"></image>


		<u-popup v-model="show" mode="bottom" border-radius='30'>
			<view class="popupBox">
				<view class="title">选择充电枪</view>
				<scroll-view scroll-y="true" style="width: 100%;height:400rpx">
					<view>
						<view class="infoBox2 u-flex-col" v-for="(item,index) in chargingPortList" :key='item.id'>
							<view class="title1">{{item.portNumber}}号枪</view>
							<view class="startUp1" @click="intoPay(item,index)" v-if="item.status === 0">
								选择
							</view>
							<view class="startUp1" @click="intoPay(item,index)" v-if="item.status === 1">
								已连接
							</view>
							<view class="startUp1" v-if="item.status === 2" style="color: #FF5151;background: #FFEDED;">
								正在充电
							</view>
						</view>
					</view>
				</scroll-view>

			</view>
		</u-popup>
		
		<!-- #ifdef MP-WEIXIN -->
		<button v-if="!isLogin" @click="wxLogin" class='free-btn-bordernone'
			style='width:100%;height:100%;top:0;left:0;z-index:99999;position: fixed;'>
		</button>
		<!-- #endif -->
		
	</view>
</template>

<script>
	import {wxAppid} from '../../common/config.js'
	export default {
		data() {
			return {
				info: '',
				list: [],
				balance: 0,
				deviceList: [],
				show: false,
				chargingPortList: [],
				deviceIndex: '',
				port: '',
				portIndex: '',
				timeIndex:'',
				chargingRules:{},
				timeArr:[
					{
						time:120,
						price:0.5
					},
					{
						time:360,
						price:1.5
					},
					{
						time:480,
						price:2
					},
					{
						time:720,
						price:3
					}
				],
				isLogin:false,
				code:'',
				id:''
			}
		},
		onLoad(e) {
			this.id = e.id
		},
		onShow() {
			// #ifdef MP-WEIXIN
			this.getCode()
			// #endif
			if (uni.getStorageSync('wxauthed')) {
				this.isLogin = true
				this.getInfo(this.id)
				this.getList()
				this.getChargingStationDeviceList(this.id)
				this.getChargingRules(this.id)
			} else {
				this.isLogin = false
				uni.showToast({
					title:'请点击任意位置登录',
					icon:'none'
				})
			}
		},
		methods: {
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
							appid:wxAppid
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
							this.isLogin = true
							uni.setStorageSync('wxauthed', true)
							uni.setStorageSync('user', loginRes.data)
							uni.setStorageSync('token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_token', loginRes.token.jwt_token)
							this.$u.vuex('vuex_user', loginRes.data)
							this.getInfo(this.id)
							this.getList()
							this.getChargingStationDeviceList(this.id)
							this.getChargingRules(this.id)
						} else {
							this.$u.toast('登陆出错')
						}
					}
				})
			},
			// 获取充电站收费规则
			async getChargingRules(id) {
				var res = await this.$u.api.getChargingRules({
					chargingStationId: id,
				})
				console.log('获取充电站收费规则', res);
				if (res.code === 10002) {
					this.chargingRules = res.data
				}
			},
			async startCharging(){
				if(!this.port){
					uni.showToast({
						title:'请选择充电口',
						icon:'none'
					})
					return
				}
				if(this.timeIndex === ''){
					uni.showToast({
						title:'请选择充电时长',
						icon:'none'
					})
					return
				}
				var res = await this.$u.api.startCharging({
					planChargingTime: this.timeArr[this.timeIndex].time * 60000,
					chargingPort: this.port.portNumber,
					id: this.port.chargingDeviceId
				})
				uni.showToast({
					title:res.message,
					icon:'none'
				})
				if(res.code === 10002){
					setTimeout(()=>{
						uni.navigateBack({
							delta:1
						})
					},1500)
				}
			},
			setTimeIndex(index){
				this.timeIndex = index
			},
			intoPay(item, index) {
				this.port = item
				this.portIndex = index
				this.show = false
			},
			async getInfo(id = '') {
				var res = await this.$u.api.searchChargingStation({
					id
				})
				console.log(res);
				if (res.code === 10002) {
					this.info = res.data[0]
				}
			},
			async getList() {
				var {
					data,
					code
				} = await this.$u.api.getMyAccountList()
				console.log(data);
				if (code == 10002) {
					const balance = data.filter(i => i.scoreType === 'cny')[0].balance
					this.balance = balance
				}
			},
			intoRecharge() {
				uni.navigateTo({
					url: '/pages/my/myWallet/recharge'
				})
			},
			async getChargingStationDeviceList(chargingStationId) {
				var {
					code,
					data
				} = await this.$u.api.searchChargingStationDevice({
					chargingStationId
				})
				console.log(88, data);
				if (code === 10002) {
					this.deviceList = data
				}
			},
			async getPortList(id, index) {
				var {
					code,
					data
				} = await this.$u.api.searchchargingPort({
					page: 1,
					size: 20,
					chargingDeviceId: id
				})
				if (code === 10002) {
					console.log(data);
					this.deviceIndex = index
					this.chargingPortList = data
					this.show = true
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background: #F5F5F5;
	}

	.selectionperiod {
		width: 630rpx;
		height: 200rpx;
		position: fixed;
		left: 64rpx;
		bottom: 0;
	}

	.infoBox {
		position: absolute;
		top: 238rpx;
		left: 40rpx;
		padding-bottom: 300rpx;
		.portBox {
			width: 670rpx;
			height: 292rpx;
			background: #FFFFFF;
			border-radius: 16rpx;

			.title {
				width: 128rpx;
				height: 88rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 88rpx;
				margin-left: 40rpx;
			}

			.portScroll {
				width: 590rpx;
				height: 140rpx;
				margin-left: 40rpx;

				.portItem {
					display: inline-block;
					width: 140rpx;
					height: 140rpx;
					background-image: url('../../static/image/home/idle.png');
					background-size: 100% 100%;
					margin-right: 24rpx;
					font-size: 64rpx;
					font-family: PingFangSC-Medium, PingFang SC;
					font-weight: 500;
					color: rgba(72, 140, 245, 0.5);
					line-height: 140rpx;
					text-align: center;
				}

				.portItem_red {
					@extend .portItem;
					background-image: url('../../static/image/home/notidle.png');
					color: #FF7B7B;
				}

				.aliasName {
					height: 88rpx;
					font-size: 30rpx;
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #323233;
					line-height: 88rpx;
				}

				.portName {
					height: 52rpx;
					background: rgba(72, 140, 245, .2);
					border-radius: 200rpx;
					font-size: 24rpx;
					font-family: PingFangSC-Medium, PingFang SC;
					font-weight: 500;
					color: #488CF5;
					text-align: center;
					margin-left: 16rpx;
					padding: 14rpx 24rpx;
					margin-top: 18rpx;
				}
			}
		}

		.timeBox {
			width: 670rpx;
			height: 474rpx;
			background: #FFFFFF;
			border-radius: 16rpx;
			margin-top: 24rpx;

			.title {
				width: 128rpx;
				height: 88rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 88rpx;
				margin-left: 40rpx;
			}
			.redBorder{
				border: 2rpx solid #4F93F6 !important;
			}
			.timeOption {
				width: 276rpx;
				height: 144rpx;
				background: #FFFFFF;
				border-radius: 16rpx;
				border: 2rpx solid rgba(151, 151, 151, 0.08);
				overflow: hidden;
				position: relative;
				margin-top: 30rpx;
				align-items: center;
				display: flex;
				.bg {
					width: 106rpx;
					height: 170rpx;
					position: absolute;
					left: 28rpx;
					bottom: -38rpx;
				}
				.content{
					justify-content: space-around;
					width: 100%;
					display: flex;
					.left{
						height: 48rpx;
						font-size: 34rpx;
						font-family: PingFangSC-Semibold, PingFang SC;
						font-weight: 600;
						color: #323233;
						line-height: 48rpx;
					}
					.right{
						width: 72rpx;
						height: 48rpx;
						font-size: 34rpx;
						font-family: PingFangSC-Semibold, PingFang SC;
						font-weight: 600;
						color: #FF5151;
						line-height: 48rpx;
						text-align: right;
					}
				}
			}
		}

		.typeBox {
			width: 668rpx;
			height: 100rpx;
			background: #FFFFFF;
			border-radius: 16rpx;
			margin-top: 24rpx;
		}

		.balanceBox {
			width: 668rpx;
			height: 100rpx;
			background: #FFFFFF;
			border-radius: 16rpx;
			margin-top: 24rpx;
			display: flex;
			align-items: center;

			.title {
				width: 128rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				margin-left: 38rpx;
			}

			.num {
				width: 348rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				text-align: right;
			}

			.recharge {
				width: 96rpx;
				height: 52rpx;
				background: #488CF5;
				border-radius: 200rpx;
				background: #488CF5;
				border-radius: 200rpx;
				line-height: 52rpx;
				text-align: center;
				color: #FFFFFF;
				margin-left: 18rpx;
			}
		}

		.illustrateBox {
			width: 668rpx;
			height: 310rpx;
			background: #FFFFFF;
			border-radius: 12rpx;
			margin-top: 24rpx;

			.title {
				width: 128rpx;
				height: 88rpx;
				font-size: 32rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #323233;
				line-height: 88rpx;
				margin-left: 40rpx;
			}

			.text {
				width: 590rpx;
				height: 168rpx;
				font-size: 30rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 42rpx;
				margin: 36rpx 0 0 26rpx;
			}
		}
	}

	.nameBox {
		background-image: url('http://file.cfeng.wang/parkInfoBG2x.png');
		width: 750rpx;
		height: 288rpx;
		background-size: 100% 100%;
		overflow: hidden;
		position: relative;

		.navIcon {
			width: 160rpx;
			height: 160rpx;
			position: absolute;
			right: 48rpx;
			bottom: 0;
			z-index: 1;
		}

		.name {
			width: 670rpx;
			height: 40rpx;
			font-size: 40rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #FFFFFF;
			line-height: 40rpx;
			margin: 58rpx 0 0 40rpx;
		}

		.addressBox {
			margin-top: 20rpx;

			.icon {
				margin-left: 46rpx;
			}

			.address {
				margin-left: 8rpx;
				width: 463rpx;
				height: 40rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #FFFFFF;
				line-height: 40rpx;
			}
		}
	}

	.popupBox {
		height: 500rpx;

		.title {
			width: 220rpx;
			height: 60rpx;
			font-size: 44rpx;
			font-family: PingFangSC-Semibold, PingFang SC;
			font-weight: 600;
			color: #323233;
			line-height: 60rpx;
			margin: 66rpx 0 30rpx 40rpx;
		}

		.infoBox2 {
			width: 670rpx;
			height: 136rpx;
			background: #F8F9FB;
			border-radius: 12rpx;
			border: 2rpx solid rgba(151, 151, 151, 0.08);
			position: relative;
			margin: 0 0 40rpx 40rpx;

			.title1 {
				height: 50rpx;
				font-size: 36rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: rgba(50, 50, 51, 0.8);
				line-height: 50rpx;
				margin: 28rpx 0 0 32rpx;
			}

			.infoitem1 {
				height: 32rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #323233;
				line-height: 32rpx;
				margin-top: 6rpx;
				margin-left: 32rpx;
			}

			.startUp1 {
				width: 144rpx;
				height: 56rpx;
				background: #488CF5;
				border-radius: 44rpx;
				font-size: 24rpx;
				font-family: PingFangSC-Medium, PingFang SC;
				font-weight: 500;
				color: #FFFFFF;
				line-height: 56rpx;
				text-align: center;
				position: absolute;
				right: 32rpx;
				top: 40rpx;
			}
		}
	}
</style>
