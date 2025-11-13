<template>
  <div className="layer1 flex-col">
    <div className="box1 flex-col">
      <div className="section1 flex-row">
        <div className="flex-row">
          <img
            v-if="company.name != 'dewei' && company.name != 'sixiang'"
            className="icon1"
            src="https://lanhu.oss-cn-beijing.aliyuncs.com/SketchPng6592717fe61b7eb0f0fa07049f60a6210e085b23267655d08c52d501d80c4aed"
          />
          <img v-if="company.name == 'sixiang'" className="icon1" style="margin-top:2px;width: 28px;height: 34px;" src="../../assets/sixianglogo.png" alt="">
          <span className="word1">{{company.title}}</span>
          <span className="info1">{{ nowTime }}</span>
        </div>
        <div className="flex-row">
           <span className="word2" style="margin-right: 18px;">
               今日现金收费：{{totalCashCharge}}
            </span>
          <span className="word2" style="margin-right: 18px">{{
            user.userName
          }}</span>
          <el-button
            type="danger"
            icon="el-icon-edit"
            circle
            @click="drawer = true"
          ></el-button>
          <el-button className="main1 flex-col" @click="onParkingRecord">
            <span className="word3">停车记录</span>
          </el-button>
          <div className="main2 flex-col" @click="onLogin">
            <span className="info2">点我交班</span>
          </div>
        </div>
      </div>
      <div className="section2 flex-col" />
    </div>

    <HandOver ref="handover" />
    <ParkingRecord ref="ParkingRecord" />
  </div>

  <el-drawer
    v-model="drawer"
    title="全局配置文件"
  >
    <div style="display: flex">
      <div>进出场车辆提示：</div>
      <el-switch
        v-model="globalConfig.promptEntryExitCar"
        active-text="开启"
        inactive-text="关闭"
      />
    </div>
    <div style="display: flex;margin-top: 10px">
      <div>已支付车辆提示：</div>
      <el-switch
        v-model="globalConfig.promptPayCar"
        active-text="开启"
        inactive-text="关闭"
      />
    </div>
    <div style="display: flex;margin-top: 10px">
      <div>出场记录只显示收费车辆：</div>
      <el-switch
          v-model="globalConfig.promptChargeOutCar"
          active-text="开启"
          inactive-text="关闭"
      />
    </div>
    <div style="display: flex;margin-top: 10px">
      <div>进出场图片显示：</div>
      <el-switch
          v-model="globalConfig.inOutImageType"
          active-text="小图"
          inactive-text="大图"
      />
    </div>
  </el-drawer>
</template>

<script>
import HandOver from "./HandOver.vue";
import ParkingRecord from "../ParkingRecord";
import { mapState } from "vuex";
import dayjs from "dayjs";
import store from "../../store";
import { countPayAmountByDuty } from '@/network/api'
import CompanyArr from "../../config"
export default {
  components: { HandOver, ParkingRecord },
  computed: {
    ...mapState(["globalConfig"]),
  },
  data() {
    return {
      nowTime: "",
      user: JSON.parse(localStorage.getItem("userInfo")),
      drawer: false,
      totalCashCharge: 0,
      company:CompanyArr
    };
  },
  created() {
    this.setTime();
    this.countPayAmountByDuty();
  },
  watch: {
    globalConfig: {
      deep: true,
      handler(value) {
        store.commit("SETCONFIG", value);
      },
    },
  },
  methods: {
    /**
     * 统计现金收款金额
     */
    async countPayAmountByDuty() {
      const {data, code} = await countPayAmountByDuty({
        paymentAgencyShortName: "cash_pay_cny",
      });
      if (code === 10002) {
        this.totalCashCharge = data == null ? 0 : data;
      }
    },
    onLogin() {
      this.$refs.handover.showDialog();
    },
    setTime() {
      setInterval(() => {
        this.nowTime = dayjs(new Date()).format("YYYY-MM-DD HH:mm:ss");
      }, 1000);
    },
    onParkingRecord() {
      this.$refs.ParkingRecord.showDialog();
    },
  },
};
</script>
