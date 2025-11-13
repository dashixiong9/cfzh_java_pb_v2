<template>
  <el-dialog
    v-model="dialogVisible"
    title="停车记录"
    :before-close="handleClose"
    fullscreen
  >
    <el-form ref="form" :inline="true" :model="listQuery" label-width="100px">
      <el-row type="flex" style="margin: 20px 0 0 88px">
        <el-col> 
          <el-row type="flex">
            <el-form-item label="车牌号">
              <el-input
                v-model="listQuery.numberPlateLK.value"
                placeholder="车牌号模糊查找"
              />
            </el-form-item>
            <el-form-item label="车辆类型">
              <el-select
                v-model="listQuery.carTypeEQ.value"
                placeholder="请选择"
                :clearable="true"
              >
                <el-option
                  v-for="item in carTypeOptions"
                  :key="item.flagKey"
                  :label="item.name"
                  :value="item.flagKey"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="支付方式">
              <el-select
                v-model="listQuery.paymentAgencyShortNameEQ.value"
                placeholder="请选择"
                :clearable="true"
              >
                <el-option
                  v-for="item in paymentAgencyShortNameOptions"
                  :key="item.shortName"
                  :label="item.name"
                  :value="item.shortName"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="费用">
              <el-select
                v-model="listQuery.feeEQ.value"
                placeholder="请选择"
                :clearable="true"
              >
                <el-option
                  :key="'fee'"
                  :label="'收费'"
                  :value="'fee'"
                ></el-option>
                <el-option :key="'free'" :label="'免费'" :value="'free'">
                </el-option>
              </el-select>
            </el-form-item>
          </el-row>
          <el-row type="flex">
            <el-form-item label="入场时间">
              <el-date-picker
                v-model="listQuery.inTimeGT.value"
                type="datetime"
                placeholder="开始"
                value-format="timestamp"
                clearable
              />
              -
              <el-date-picker
                v-model="listQuery.inTimeLT.value"
                type="datetime"
                placeholder="结束"
                value-format="timestamp"
                clearable
              />
            </el-form-item>
            <el-form-item label="出场时间">
              <el-date-picker
                v-model="listQuery.outTimeGT.value"
                type="datetime"
                placeholder="开始"
                value-format="timestamp"
                clearable
              />
              -
              <el-date-picker
                v-model="listQuery.outTimeLT.value"
                type="datetime"
                placeholder="结束"
                value-format="timestamp"
                clearable
              />
            </el-form-item>
            <el-form-item label="姓名/楼栋">
              <el-input
                v-model="listQuery.visitUnitLK.value"
                placeholder="车主姓名，或所住的房间号"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                icon="el-icon-search"
                type="primary"
                @click="handleQuery"
              >
                查询
              </el-button>
            </el-form-item>
          </el-row>

          <el-row type="flex" style="width: 700px"> </el-row>
        </el-col>
      </el-row>
    </el-form>

    <el-table :data="list" style="width: 100%" stripe border>
      <el-table-column align="center" label="序号" width="50">
        <template #default="{ $index }">
          {{ $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column
        prop="numberPlate"
        label="车牌号"
        align="center"
        width="100"
      />

      <el-table-column align="center" label="应付金额" width="80">
        <template #default="{ row }">
          {{
            row.cfOrder == null ||
            row.cfOrder.paymentAgencyShortName == "package"
              ? 0
              : row.cfOrder.amountsPayable
          }}
        </template>
      </el-table-column>

      <el-table-column align="center" label="实付金额" width="80">
        <template #default="{ row }">
          {{
            row.cfOrder == null ||
            row.cfOrder.paymentAgencyShortName == "package"
              ? 0
              : row.cfOrder.amountActuallyPaid
          }}
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="姓名/楼栋"
        width="90"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          {{ row.visitUnit || "-" }}
        </template>
      </el-table-column>

      <el-table-column align="center" label="入场时间" width="170">
        <template #default="{ row }">
          {{ row.inTime ? timeFormat(row.inTime, "YYYY-MM-DD hh:MM:ss") : "-" }}
        </template>
      </el-table-column>

      <el-table-column align="center" label="出场时间" width="160">
        <template #default="{ row }">
          {{
            row.outTime ? timeFormat(row.outTime, "YYYY-MM-DD hh:MM:ss") : "-"
          }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="停车时长" width="160">
        <template #default="{ row }">
          {{ getTimeFn(row.inTime, row.outTime) }}
        </template>
      </el-table-column>

      <el-table-column label="车辆类型" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          <el-tag
            effect="dark"
            :type="row.carType == 'monthly_card' ? 'success' : 'danger'"
          >
            {{ getCarTypeName(row.carType) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="车辆状态" width="80">
        <template #default="{ row }">
          <el-tag
            effect="dark"
            :type="row.carStatus == 1 ? 'success' : 'warning'"
          >
            {{ carStatusOption[row.carStatus] }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="入场关卡" width="100">
        <template #default="{ row }">
          {{
            row.cfCarParkCheckpointIn == null
              ? "-"
              : row.cfCarParkCheckpointIn.name
          }}
        </template>
      </el-table-column>

      <el-table-column align="center" label="出场关卡" width="100">
        <template #default="{ row }">
          {{
            row.cfCarParkCheckpointOut == null
              ? "-"
              : row.cfCarParkCheckpointOut.name
          }}
        </template>
      </el-table-column>

      <el-table-column
        label="入场大图"
        width="80px"
        align="center"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <el-image
            :preview-src-list="[row.inBigImage]"
            style="width: 42px; height: 34px"
            :src="row.inSmallImage"
          />
        </template>
      </el-table-column>

      <el-table-column
        label="出场大图"
        width="80px"
        align="center"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <el-image
            :preview-src-list="[row.outBigImage]"
            style="width: 42px; height: 34px"
            :src="row.outSmallImage"
          />
        </template>
      </el-table-column>

      <el-table-column align="center" label="到期时间" width="160">
        <template #default="{ row }">
          {{
            row.cfCarParkPackage
              ? timeFormat(row.cfCarParkPackage.endTime, "yyyy-mm-dd hh:MM:ss")
              : "-"
          }}
        </template>
      </el-table-column>

      <el-table-column align="center" label="支付方式" width="80">
        <template #default="{ row }">
          {{ row.cfPaymentAgency ? row.cfPaymentAgency.name : "-" }}
        </template>
      </el-table-column>

      <el-table-column
        align="center"
        label="车场"
        width="180"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          {{ row.cfCarPark.name }}
        </template>
      </el-table-column>

      <el-table-column
        label="入场方式"
        width="170px"
        align="center"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          {{ getCarParkReleaseType(row.inReleaseType) }}
        </template>
      </el-table-column>
      <el-table-column
        label="出场方式"
        width="170px"
        align="center"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          {{ getCarParkReleaseType(row.outReleaseType) }}
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      background
      :current-page="listQuery.limit.page"
      :page-size="listQuery.limit.limit"
      :page-sizes="[10, 20, 50, 100]"
      :layout="layout"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :total="total"
      style="margin-top: 10px"
    />
  </el-dialog>
</template>
<script>
import { carparkUseLogAdmin, getAllCarTypeList } from "@/network/api";
import { handleFilterParams, $timeFormat, timeFn } from "@/utils/index";
import { listQuery } from "./queruData";
export default {
  data() {
    return {
      list: [],
      dialogVisible: false,
      carTypeOptions: [],
      listLoading: false,
      carStatusOption: ["禁用", "正常", "已过期"],
      listQuery: listQuery,
      layout: "total, sizes, prev, pager, next, jumper",
      carParkReleaseType: [
        { key: 0, name: "未知" },
        { key: 1, name: "自动抬杆" },
        { key: 2, name: "人工抬杆" },
        { key: 3, name: "手机放行" },
      ],
      paymentAgencyShortNameOptions: [
        { shortName: "cash_pay_cny", name: "现金支付" },
        { shortName: "pay_online", name: "电子支付" },
        { shortName: "abnormal_termination_of_order", name: "异常放行" },
      ],
      total:0
    };
  },
  methods: {
    handleClose(done) {
      done();
    },
    getCarParkReleaseType(key){
      return this.carParkReleaseType.filter(i=>i.key == key)[0].name
    },
    timeFormat(time, type) {
      return $timeFormat(time, type);
    },
    showDialog() {
      this.dialogVisible = true;
      this.getAllCarTypeList();
      this.fetchData();
    },
    async getAllCarTypeList() {
      const { data, code } = await getAllCarTypeList();
      console.log(data);
      if (code == 10002) {
        this.carTypeOptions = data;
      } else {
        this.carTypeOptions = [];
      }
    },
        handleQuery() {
      this.listQuery.limit.page = 1;
      this.fetchData();
    },
    handleSizeChange(val) {
      this.listQuery.limit.limit = val;
      this.fetchData();
    },
    handleCurrentChange(val) {
      this.listQuery.limit.page = val;
      this.fetchData();
    },

    getTimeFn(inTime, outTime) {
      return timeFn(inTime, outTime);
    },
    // 解决时间搜索 清除后为null的问题
    timeRepairNull(obj) {
      for (const i in obj) {
        if (i != "limit") {
          if (obj[i].value == null) {
            obj[i].value = "";
          }
        }
      }
      return obj;
    },
    getCarTypeName(carType) {
      var carTypeOptions = this.carTypeOptions;
      if (!carType) return "临时车";
      for (let index = 0; index < carTypeOptions.length; index++) {
        if (carTypeOptions[index].flagKey == carType) {
          return carTypeOptions[index].name;
        }
      }
    },
    async fetchData() {
      this.listLoading = true;
      const { data, total, code, message } = await carparkUseLogAdmin({
        conditions: handleFilterParams(this.timeRepairNull(this.listQuery)),
      });
      console.log(data);
      this.listLoading = false;

      if (code != 10002) {
        this.$message.error(message);
        return;
      }
      this.list = data;
      this.total = total;
    },
  },
};
</script>