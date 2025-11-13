<script>
	import { updataWx } from 'utils/index.js'
	// const plugin = requirePlugin("indoormap")
	export default {
		onLaunch: function(options) {
			// 检测小程序版本更新
			// #ifdef MP-WEIXIN
			updataWx()
			// #endif 
			
			
			// 获取支付宝扫码参数
			 if (options.query && options.query.qrCode) {
			    console.log('获取支付宝扫码参数' + options.query.qrCode);
				const checkpointId = options.query.qrCode.split("/carpark_checkpoint_id/")
				uni.reLaunch({
					url:'/pages/my/replacePay/pay?carpark_checkpoint_id=' + checkpointId
				})
			  }
			 
		},
		onShow: function() {
			console.log('App Show')
			// if(!uni.getStorageSync('wxauthed')){
			// 	uni.reLaunch({
			// 		url:'./pages/my/h5Login/h5Login'
			// 	})
			// }
		},
		onHide: function() {
			// console.log('App Hide')
		}
	}
</script>

<style lang="scss">
	@import "uview-ui/index.scss";
	// @import "static/style/animate.scss";

	/*使用 button::after{ border: none; } 来去除边框*/
	.free-btn-bordernone {
		background: none !important;
		color: #fff !important;
	}

	.free-btn-bordernone::after {
		border: none;
	}
	
	
	
	
	
	@keyframes shake {
	  from,
	  to {
	    transform: translate3d(0, 0, 0);
	  }
	
	  10%,
	  30%,
	  50%,
	  70%,
	  90% {
	    transform: translate3d(-10px, 0, 0);
	  }
	
	  20%,
	  40%,
	  60%,
	  80% {
	    transform: translate3d(10px, 0, 0);
	  }
	}
	
	.shake {
	  animation-name: shake;
	}
	
	
	
	
	
	@keyframes fadeIn {
	  from {
	    opacity: 0;
	  }
	
	  to {
	    opacity: 1;
	  }
	}
	
	.fadeIn {
	  animation-name: fadeIn;
	}
	
	
	
	@keyframes fadeInDown {
	  from {
	    opacity: 0;
	    transform: translate3d(0, -100%, 0);
	  }
	
	  to {
	    opacity: 1;
	    transform: translate3d(0, 0, 0);
	  }
	}
	
	.fadeInDown {
	  animation-name: fadeInDown;
	}
	
	
	
	@keyframes fadeInDownBig {
	  from {
	    opacity: 0;
	    transform: translate3d(0, -2000px, 0);
	  }
	
	  to {
	    opacity: 1;
	    transform: translate3d(0, 0, 0);
	  }
	}
	
	.fadeInDownBig {
	  animation-name: fadeInDownBig;
	}
	
	
	
	@keyframes fadeInLeft {
	  from {
	    opacity: 0;
	    transform: translate3d(-100%, 0, 0);
	  }
	
	  to {
	    opacity: 1;
	    transform: translate3d(0, 0, 0);
	  }
	}
	
	.fadeInLeft {
	  animation-name: fadeInLeft;
	}
	
	
	
	@keyframes fadeInLeftBig {
	  from {
	    opacity: 0;
	    transform: translate3d(-2000px, 0, 0);
	  }
	
	  to {
	    opacity: 1;
	    transform: translate3d(0, 0, 0);
	  }
	}
	
	.fadeInLeftBig {
	  animation-name: fadeInLeftBig;
	}
	
	
	
	@keyframes fadeInRight {
	  from {
	    opacity: 0;
	    transform: translate3d(100%, 0, 0);
	  }
	
	  to {
	    opacity: 1;
	    transform: translate3d(0, 0, 0);
	  }
	}
	
	.fadeInRight {
	  animation-name: fadeInRight;
	}
	
	
	
	@keyframes fadeInRightBig {
	  from {
	    opacity: 0;
	    transform: translate3d(2000px, 0, 0);
	  }
	
	  to {
	    opacity: 1;
	    transform: translate3d(0, 0, 0);
	  }
	}
	
	.fadeInRightBig {
	  animation-name: fadeInRightBig;
	}
	
	
	
	@keyframes fadeInUp {
	  from {
	    opacity: 0;
	    transform: translate3d(0, 100%, 0);
	  }
	
	  to {
	    opacity: 1;
	    transform: translate3d(0, 0, 0);
	  }
	}
	
	.fadeInUp {
	  animation-name: fadeInUp;
	}
	
	
	
	@keyframes fadeInUpBig {
	  from {
	    opacity: 0;
	    transform: translate3d(0, 2000px, 0);
	  }
	
	  to {
	    opacity: 1;
	    transform: translate3d(0, 0, 0);
	  }
	}
	
	.fadeInUpBig {
	  animation-name: fadeInUpBig;
	}
	
	
	
	@keyframes fadeOut {
	  from {
	    opacity: 1;
	  }
	
	  to {
	    opacity: 0;
	  }
	}
	
	.fadeOut {
	  animation-name: fadeOut;
	}
	
	
	
	@keyframes fadeOutDown {
	  from {
	    opacity: 1;
	  }
	
	  to {
	    opacity: 0;
	    transform: translate3d(0, 100%, 0);
	  }
	}
	
	.fadeOutDown {
	  animation-name: fadeOutDown;
	}
	
	
	
	@keyframes fadeOutDownBig {
	  from {
	    opacity: 1;
	  }
	
	  to {
	    opacity: 0;
	    transform: translate3d(0, 2000px, 0);
	  }
	}
	
	.fadeOutDownBig {
	  animation-name: fadeOutDownBig;
	}
	
	
	
	@keyframes fadeOutLeft {
	  from {
	    opacity: 1;
	  }
	
	  to {
	    opacity: 0;
	    transform: translate3d(-100%, 0, 0);
	  }
	}
	
	.fadeOutLeft {
	  animation-name: fadeOutLeft;
	}
	
	
	
	@keyframes fadeOutLeftBig {
	  from {
	    opacity: 1;
	  }
	
	  to {
	    opacity: 0;
	    transform: translate3d(-2000px, 0, 0);
	  }
	}
	
	.fadeOutLeftBig {
	  animation-name: fadeOutLeftBig;
	}
	
	
	
	@keyframes fadeOutRight {
	  from {
	    opacity: 1;
	  }
	
	  to {
	    opacity: 0;
	    transform: translate3d(100%, 0, 0);
	  }
	}
	
	.fadeOutRight {
	  animation-name: fadeOutRight;
	}
	
	
	
	@keyframes fadeOutRightBig {
	  from {
	    opacity: 1;
	  }
	
	  to {
	    opacity: 0;
	    transform: translate3d(2000px, 0, 0);
	  }
	}
	
	.fadeOutRightBig {
	  animation-name: fadeOutRightBig;
	}
	
	
	
	@keyframes fadeOutUp {
	  from {
	    opacity: 1;
	  }
	
	  to {
	    opacity: 0;
	    transform: translate3d(0, -100%, 0);
	  }
	}
	
	.fadeOutUp {
	  animation-name: fadeOutUp;
	}
	
	
	@keyframes bounceIn {
		0% {
			opacity: 0;
			-webkit-transform: scale(.3);
			-ms-transform: scale(.3);
			transform: scale(.3)
		}
	
		50% {
			opacity: 1;
			-webkit-transform: scale(1.05);
			-ms-transform: scale(1.05);
			transform: scale(1.05)
		}
	
		70% {
			-webkit-transform: scale(.9);
			-ms-transform: scale(.9);
			transform: scale(.9)
		}
	
		100% {
			opacity: 1;
			-webkit-transform: scale(1);
			-ms-transform: scale(1);
			transform: scale(1)
		}
	}
	
	.bounceIn {
		-webkit-animation-name: bounceIn;
		animation-name: bounceIn
	}
	
	
	
	
	.animated02s {
	  animation-duration: .2s;
	  animation-fill-mode: both;
	}
	
	.animated03s {
	  animation-duration: .3s;
	  animation-fill-mode: both;
	}
	
	.animated {
	  animation-duration: .4s;
	  animation-fill-mode: both;
	}
	
	.animated07s {
	  animation-duration: .7s;
	  animation-fill-mode: both;
	}
	
	.animated1s {
	  animation-duration: 1s;
	  animation-fill-mode: both;
	}
	
	.animated.infinite {
	  animation-iteration-count: infinite;
	}
	
	.animated.delay-03s {
	  animation-delay: .3s;
	}
	.animated.delay-1s {
	  animation-delay: 1s;
	}
	
	.animated.delay-2s {
	  animation-delay: 2s;
	}
	
	.animated.delay-3s {
	  animation-delay: 3s;
	}
	
	.animated.delay-4s {
	  animation-delay: 4s;
	}
	
	.animated.delay-5s {
	  animation-delay: 5s;
	}
	
	.animated.fast {
	  animation-duration: 800ms;
	}
	
	.animated.faster {
	  animation-duration: 500ms;
	}
	
	.animated.slow {
	  animation-duration: 2s;
	}
	
	.animated.slower {
	  animation-duration: 3s;
	}
	
	@media (prefers-reduced-motion) {
	  .animated {
	    animation: unset !important;
	    transition: none !important;
	  }
	}
</style>
