<template>
  <div className="group17 flex-col">
    <div className="section5 flex-col">
      <div className="bd11">
        <span className="word15">待缴费车辆</span>
      </div>
    </div>
    <div style="display: flex; height: 450px">
      <div className="section8 flex-col" v-if="showList.length > 0">
        <div
            className="wrap1 flex-row"
            v-for="(item, index) in showList"
            :key="item.id"
            @click="onTab(index)"
            :style="{
            background: index === tabIndex ? '#3A394F' : '',
          }"
        >
          <div class="txt5">{{ item.cfCarParkUseLog.numberPlate }}</div>

          <img src="../../assets/image/close.png" :style="{width:'16px',height:'16px',margin:'0px 0px 1px 5px'}" alt=""
               @click="closeNumberPlate(item)">
        </div>
      </div>
      <div
          v-if="showList.length > 0"
          style="
          width: 74%;
          display: flex;
          flex-direction: column;
          align-items: center;
        "
      >
        <span className="word17">{{
            showList[tabIndex].cfCarParkUseLog.numberPlate
          }}</span>


        <div className="section7 flex-col">
          <span className="word18">{{
              showList[tabIndex].cfCarParkCarType.name
            }}</span>
        </div>
        <div style="display: flex">
            <span className="word19" v-if="showList[tabIndex].cfCouponList.length === 0"
            >总应收款:¥{{ showList[tabIndex].cfOrder.amountsPayable }}</span
            >
          <span className="word19" v-else>总应收款:¥{{
              showList[tabIndex].cfOrder.amountsPayable - showList[tabIndex].cfCouponList[0].denomination
            }}</span>
          <el-popover placement="top" width="160" v-model:visible="visibleShow">
            <p>请输入优惠金额</p>
            <el-input
                placeholder="请输入"
                type="Number"
                style="margin-top: 5px"
                v-model="modifiedAmount"
            ></el-input>
            <div style="text-align: right; margin: 6px 0 0 0">
              <el-button size="mini" type="text" @click="visibleShow = false">取消</el-button>
              <el-button
                  type="primary"
                  size="mini"
                  :disabled="!modifiedAmount"
                  @click="confirmChangeAmount"
              >确定
              </el-button
              >
            </div>
            <template #reference>
              <el-button
                  type="danger"
                  icon="el-icon-edit"
                  size="mini"
                  circle
                  style="margin-left: 6px;width: 40px;height: 40px"
              ></el-button>
            </template>
          </el-popover>
        </div>


        <span className="word20">{{
            getParkingTime(
                showList[tabIndex].cfCarParkUseLog.inTime,
                showList[tabIndex].cfCarParkUseLog.outTime
            )
          }}</span>
        <!-- <span className="word21">应收:0</span> -->
        <div style="display: flex; margin-top: 10px">
          <span className="word22"
          >代收:{{ showList[tabIndex].cfOrder.collectionAmount }}</span
          >

          <span className="word23"
          >优惠券抵扣:{{
              showList[tabIndex].cfCouponList.length > 0 ? showList[tabIndex].cfCouponList[0].denomination : '-'
            }}</span
          >
        </div>
        <span className="txt4"
        >备注信息：{{ showList[tabIndex].cfCarParkUseLog.remarks || "-" }}</span
        >

        <span className="info10"
        >入场信息：{{ showList[tabIndex].inCheckpoint !== null ? showList[tabIndex].inCheckpoint.name : '' }}</span
        >

        <span className="word24"
        >入场时间：{{
            getTimeFormat(showList[tabIndex].cfCarParkUseLog.inTime)
          }}</span
        >
        <span className="word25"
        >出场信息：{{ showList[tabIndex].outCheckpoint !== null ? showList[tabIndex].outCheckpoint.name : '' }}</span
        >
        <span className="txt6"
        >出场时间：{{
            getTimeFormat(showList[tabIndex].cfCarParkUseLog.outTime)
          }}</span
        >
        <div
            style="
            display: flex;
            justify-content: space-evenly;
            width: 100%;
            margin-top: 30px;
          "
        >
          <div
              class="section9 flex-col"
              @click="openDoor('toll')"
          >
            <span class="info11">收费放行</span>
          </div>
          <div
              class="section10 flex-col"
              @click="onAbnormalRelease"
          >
            <span class="info12">异常放行</span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <el-dialog
      title="选择异常原因"
      v-model="openDoorDialogVisible"
      :modal-append-to-body="false"
      width="30%"
      center
  >
    <div class="Box">
      <div
          v-for="(item, index) of reason"
          :key="index"
          :class="index === reasonIndex ? 'reasonBox_ac' : 'reasonBox'"
          @click="onReason(index)"
      >
        {{ item }}
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button
            type="danger"
            @click="openDoor('abnormal')"
            :disabled="reasonIndex === ''"
        >确 定</el-button
        >
      </span>
    </template>
  </el-dialog>
</template>
<script>
import {mapGetters, mapState} from "vuex";
import {toRaw} from "vue";
import {$getParkingTime, $timeFormat} from "../../utils/index";
import {updateLogAndOrder} from "@/network/api";
import {cloneDeep} from "lodash";
import {ElMessage, ElMessageBox} from "element-plus";
import dayjs from "dayjs";
var times = null
export default {
  emits:['countPayAmountByDuty'],
  data() {
    return {
      visibleShow: false,
      modifiedAmount: '',
      list: [],
      tabIndex: 0,
      openDoorDialogVisible: false,
      reason: ["特殊车辆", "系统故障", "业主拒绝缴费", "其他原因", "倒车误拍"],
      reasonIndex: "",
      showList: []
    };
  },
  created() {
    if (localStorage.getItem("ChargeLocalList")) {
      this.list = JSON.parse(localStorage.getItem("ChargeLocalList"))
      this.setTabIndex();
    }
    this.clearChargeLocalList()
  },
  computed: {
    ...mapState(["chargeCar", "paidCar", "globalConfig", "carParkIndex"]),
    ...mapGetters(["carParkId"])
    // eslint-disable-next-line vue/no-dupe-keys
    // list:function (){
    //   return []
    // }
  },
  watch: {
    chargeCar: function (value) {
      this.processingData(toRaw(value));
    },
    // 监听已支付的车辆
    paidCar: function (value) {
      this.setPaidCar(toRaw(value));
    },
    carParkIndex: function () {
      this.showList = this.list.filter(i => i.cfCarPark.id === this.carParkId)
    },
    list: {
      deep: true,
      handler: function (value) {
        // console.log('value发生了变化',value)
        this.showList = value.filter(i => i.cfCarPark.id === this.carParkId) //只显示被选择的停车场的数据
      }
    }
  },
  methods: {
    // 点击修改金额
    async confirmChangeAmount() {
      var {modifiedAmount, tabIndex, showList} = this;
      var info = showList[tabIndex];
      const amountsPayable = info.cfCouponList.length > 0 ? info.cfOrder.amountsPayable - info.cfCouponList[0].denomination : info.cfOrder.amountsPayable
      if (modifiedAmount >= amountsPayable) {
        this.$message({
          message: '不能大于等于总应付金额',
          type: "error",
        });
        return
      }
      var {message, code} = await updateLogAndOrder({
        carParkUseLogId: info.cfOrder.goodsId,
        payTime: new Date().getMilliseconds(),
        orderId: info.cfOrder.id,
        amountOfMoney: modifiedAmount,
        actionType: "change_amount",
      });
      if (code === 10002) {
        console.log(info)
        console.log(this.list)

        // 修改总应收款金额
        var list = cloneDeep(this.list)
        for (const i in list) {
          if (list[i].cfCarParkUseLog.id === info.cfCarParkUseLog.id) {
            list[i].cfOrder.amountsPayable = (list[i].cfOrder.amountsPayable - modifiedAmount).toFixed(2)
          }
        }
        this.list = list
        window.localStorage.setItem('ChargeLocalList', JSON.stringify(this.list))
        this.resetTiming()
        this.modifiedAmount = "";
        this.visibleShow = false;
      }
      this.$message({
        message,
        type: code === 10002 ? "success" : "error",
      });
    },
    // 点击关闭车牌
    closeNumberPlate(item) {
      console.log(item)
      const itemId = item.cfCarParkUseLog.id
      const list = cloneDeep(this.list)
      console.log(list)
      ElMessageBox.confirm(`确定删除${item.cfCarParkUseLog.numberPlate}吗？`, '提示',
          {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
          })
          .then(() => {
            this.list = list.filter(i => i.cfCarParkUseLog.id !== itemId)
            window.localStorage.setItem('ChargeLocalList', JSON.stringify(this.list))
            this.resetTiming()
            this.tabIndex = 0;
          })
    },
    // 修改已支付车辆数据
    setPaidCar(value) {
      // console.log("value", value);
      const numberPlate = value.numberPlate;
      console.log('已支付的车辆', numberPlate)


      const cloneList = cloneDeep(this.list);

      // 将这个车牌在列表中删除
      this.list = cloneList.filter(i => i.cfCarParkUseLog.numberPlate !== numberPlate);
      localStorage.setItem("ChargeLocalList", JSON.stringify(this.list));
      this.resetTiming()
      this.tabIndex = 0;


      if (this.globalConfig.promptPayCar) {
        ElMessage({
          message: `${numberPlate}已支付成功`,
          type: "success",
        });
      }
    },
    onReason(index) {
      this.reasonIndex = index;
    },
    getParkingTime(inTime, outTime) {
      return $getParkingTime(inTime, outTime);
    },
    getTimeFormat(time) {
      return $timeFormat(time, "YYYY-MM-DD HH:mm:ss");
    },
    resetTiming() {
      clearInterval(times)
      this.clearChargeLocalList()
    },
    // 检测来了新的收费车辆
    processingData(data) {
      if (this.list.length > 60) {
        this.list = this.list.slice(0, 60)
      }
      var list = cloneDeep(this.list)
      console.log(data)
      const {id} = data.cfCarParkUseLog
      this.list = list.filter(i => i.cfCarParkUseLog.id !== id)
      localStorage.setItem("ChargeLocalList", JSON.stringify(this.list));
      this.resetTiming()
      this.list.push(data)
      // this.setTabIndex();
      localStorage.setItem("ChargeLocalList", JSON.stringify(this.list));
    },
    setTabIndex() {
      if (this.list.length > 0) {
        this.tabIndex = 0;
      }
    },
    onTab(index) {
      this.tabIndex = index;
    },
    // 点击了异常放行
    onAbnormalRelease() {
      this.openDoorDialogVisible = true;
    },
    cashRelease(item) {
      console.log(item)
    },
    async openDoor(type) {
      var info = this.list[this.tabIndex];
      console.log(info)
      // 收费放行
      if (type === "toll") {
        // ElMessageBox.confirm(`${info.cfCarParkUseLog.numberPlate}出场收费${info.cfOrder.amountsPayable}元`, '提示',
        //     {
        //       confirmButtonText: '确认放行',
        //       cancelButtonText: '取消',
        //       type: 'warning',
        //     })
        const amountsPayable = info.cfCouponList.length > 0 ? info.cfOrder.amountsPayable - info.cfCouponList[0].denomination :info.cfOrder.amountsPayable
        ElMessageBox.alert(
            `<strong style='font-size:25px'>${info.cfCarParkUseLog.numberPlate}出场收费<i style='color:red;'>${amountsPayable}</i>元</strong>`,
            '收费提示',
            {
              dangerouslyUseHTMLString: true,
              confirmButtonText: '确认放行',
              cancelButtonText: '取消',
              showCancelButton: true,
              type: 'warning',
            }
        ).then(async () => {
          const {amountsPayable} = info.cfOrder
          const cfCouponList = info.cfCouponList
          const res = await updateLogAndOrder({
            carParkUseLogId: info.cfOrder.goodsId,
            payTime: new Date().getMilliseconds(),
            orderId: info.cfOrder.id,
            amountOfMoney: amountsPayable,
            actionType: "cash_pay",
            couponId: cfCouponList.length > 0 ? cfCouponList[0].id : ''
          });
          console.log(res)
          if (res.code === 10002) {
            this.$message({
              message: res.message,
              type: "success",
            });
            this.list = this.list.filter((i) => i.cfOrder.id !== info.cfOrder.id);
            localStorage.setItem("ChargeLocalList", JSON.stringify(this.list));
            this.resetTiming()
            this.tabIndex = 0;
            // 更新顶部今日现金收费
            this.$emit('countPayAmountByDuty')
          } else {
            this.$message.error(res.message);
          }
        }).catch((err) => console.log(err))


      } else if (type === "abnormal") {
        // 异常放行
        // 倒车误拍
        if (this.reasonIndex === 4) {
          this.list = this.list.filter((i) => i.cfOrder.id !== info.cfOrder.id);
          localStorage.setItem("ChargeLocalList", JSON.stringify(this.list));
          this.resetTiming()
          this.tabIndex = 0;
          this.openDoorDialogVisible = false;
        } else {
          var {message} = await updateLogAndOrder({
            carParkUseLogId: info.cfOrder.goodsId,
            payTime: new Date().getMilliseconds(),
            orderId: info.cfOrder.id,
            amountOfMoney: 0,
            actionType: "abnormal_termination_of_order",
          });
          this.$message({
            message,
            type: "success",
          });
          this.list = this.list.filter((i) => i.cfOrder.id !== info.cfOrder.id);
          localStorage.setItem("ChargeLocalList", JSON.stringify(this.list));
          this.resetTiming()
          this.tabIndex = 0;
          this.openDoorDialogVisible = false;
        }
      }
    },
    clearChargeLocalList() {
      var list = this.list
      times = setInterval(() => {
        this.list = list.filter(i => (dayjs().valueOf() - i.cfCarParkUseLog.outTime) < 600000)
        localStorage.setItem("ChargeLocalList", JSON.stringify(this.list));
      }, 2000)
    }
  },

};
</script>

<style scoped>
.reasonBox {
  width: 90px;
  height: 36px;
  background: #efefef;
  border-radius: 3px;
  font-size: 12px;
  font-family: PingFangSC-Medium, PingFang SC, serif;
  font-weight: 500;
  color: #323233;
  line-height: 36px;
  margin-bottom: 16px;
  /* margin-right: 20px; */
  text-align: center;
}

.reasonBox_ac {
  width: 90px;
  height: 36px;
  background: #fc4408;
  border-radius: 3px;
  font-size: 12px;
  font-family: PingFangSC-Medium, PingFang SC, serif;
  font-weight: 500;
  color: #ffffff;
  line-height: 36px;
  margin-bottom: 16px;
  /* margin-right: 20px; */
  text-align: center;
}

.Box {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
</style>