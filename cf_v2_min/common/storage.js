// 设置缓存
export function setLocal(key, val) {
  uni.setStorageSync(key,val);
}
// 获取缓存
export function getLocal(key) {
  let json = uni.getStorageSync(key);
  return json
}
// 移除指定缓存
export function removeLocal(key) {
   uni.removeStorageSync(key);
}
// 移除全部永久缓存
export function clearLocal() {
  uni.clearStorageSync();
}

