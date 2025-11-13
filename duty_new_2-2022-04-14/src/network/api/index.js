import request from "@/network/request";

export function login(params) {
  return request({
    url: "/carparkLoginAdmin/login",
    method: "post",
    params,
  });
}

export function logout(params) {
  return request({
    url: "/carparkLoginAdmin/logout",
    method: "post",
    data: params,
  });
}

export function getCarParkList(params) {
  return request({
    url: "/carparkAdmin/getMyCarParks",
    method: "get",
    params,
  });
}
export function getMyInfo(params) {
  return request({
    url: "/ucenterAdmin/getMyInfo",
    method: "get",
    params,
  });
}
export function getCheckPointByQuery(params) {
  return request({
    url: "/carparkCheckPointDeviceAdmin/getListByQuery",
    method: "get",
    params,
  });
}
export function queryOrderByUseId(_id) {
  return request({
    url: "/carparkOrderAdmin/queryOrderByUseId",
    method: "get",
    params: { id: _id },
  });
}
export function countPayAmountByDuty(params) {
  return request({
    url: "/carparkUseLogAdmin/countPayAmountByDuty",
    method: "get",
    params,
  });
}

// 停车记录
export function carparkUseLogAdmin(params) {
  return request({
    url: "/carparkUseLogAdmin/dutySelectListByCondition",
    method: "get",
    params,
  });
}

// 新增停车记录
export function addCarparkUseLogAdmin(params) {
  return request({
    url: "/carparkUseLogAdmin/add",
    method: "post",
    data: params,
  });
}

// 编辑停车记录
export function updateCarparkUseLogAdmin(params) {
  return request({
    url: "/carparkUseLogAdmin/update",
    method: "update",
    params,
  });
}

// 删除停车记录
export function deleteCarparkUseLogAdmin(params) {
  return request({
    url: "/carparkUseLogAdmin/delete",
    method: "delete",
    params,
  });
}

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: "orderAdmin/selectListByCondition",
    method: "get",
    params,
  });
}

// 获取支付机构列表
export function selecPaymentAgencytListByQuery(params) {
  return request({
    url: "paymentAgencyAdmin/selectListByQuery",
    method: "get",
    params,
  });
}

export function getListByLinkerUser(params) {
  return request({
    url: "/carparkAdmin/getListByLinkerUser",
    method: "get",
    params,
  });
}

export function goOffWorkOrGoToWork(params) {
  return request({
    url: "/carparkCheckPointDeviceAdmin/goOffWorkOrGoToWork",
    method: "put",
    params,
  });
}

export function getCarparkDeviceAdmin(params) {
  return request({
    url: "/carparkDeviceAdmin/getListByQuery",
    method: "get",
    params,
  });
}

// 开闸关闸
export function openOrCloseDoor(params) {
  return request({
    url: "/carParkReleaseLogAdmin/releaseByCheckPointId",
    method: "post",
    data: params,
  });
}

// 更新停车记录和账单信息
export function updateLogAndOrder(data) {
  return request({
    url: `/carparkUseLogAdmin/updateLogAndOrder`,
    method: "put",
    data,
  });
}

/**
 * 统计异常放行次数
 * @returns
 */
export function countTheNumbeOfAbnormalReleases() {
  return request({
    url: "/carParkReleaseLogAdmin/countByQuery",
    method: "get",
  });
}

/**
 * 根据条件查询车辆套餐
 * @returns
 */
export function getPackageListByQuery(params) {
  return request({
    url: "carparkpackageAdmin/getPackageListByQuery",
    method: "get",
    params,
  });
}

/**
 * 根据条件查询车辆套餐(返回的数据含车辆类型)
 * @returns
 */
export function selectPackageListByQuery(params) {
  return request({
    url: "carparkpackageAdmin/selectPackageListByQuery",
    method: "get",
    params,
  });
}

/**
 * 获取所有停车类型数据
 * @returns
 */
export function getAllCarTypeList(params) {
  return request({
    url: "carparkcartypeAdmin/getAllList",
    method: "get",
    params,
  });
}

/**
 * 根据条件获取支付机构
 * @returns
 */
export function getPaymentAgencyListByQuery(params) {
  return request({
    url: "paymentAgencyAdmin/selectListByQuery",
    method: "get",
    params,
  });
}



// 获取心跳ws地址
export function getMinChatLinkCountsServiceIp(params) {
  return request({
    url: "/friends/getMinChatLinkCountsServiceIp",
    method: "get",
    params,
  });
}

// 修改停车记录
export function dutyerUpdateUseLog(params) {
  return request({
    url: "/carparkUseLogAdmin/dutyerUpdateUseLog",
    method: "put",
    params,
  });
}

// 抓拍
export function capture(params) {
  return request({
    url: "/carparkUseLogAdmin/capture",
    method: "get",
    params,
  });
}

