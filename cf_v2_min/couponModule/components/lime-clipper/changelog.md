## 0.6.1（2021-03-10）
- 给事件添加阻止冒泡
## 0.6.0（2021-03-06）
增加`source`属性，`Object`类型，key为图片来源类型，value为选项说明。<br>
```js
// source 默认值 
{
	album: '从相册中选择',
	camera: '拍照',
	// #ifdef MP-WEIXIN
	message: '从微信中选择'
	// #endif
}
```
## 0.5.0（2021-02-26）
- 调整为uni_modules目录规范
