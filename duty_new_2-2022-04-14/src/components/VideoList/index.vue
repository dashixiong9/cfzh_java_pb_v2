<template>
  <div
    className="group15 flex-col"
    :style="{
      width: parkSwitch ? '43%' : '60%',
      marginLeft: parkSwitch ? '20px' : '0px',
    }"
  >
    <div className="bd6 flex-row">
      <div className="box3 flex-row">
        <div
          className="group16-0 flex-col"
          v-for="(item, index) in deviceList"
          :key="item.id"
        >
          <div className="layer5-0 flex-col">
            <span className="word11-0">{{ item.title }}</span>
            <el-image
              :id="'show_video' + (index + 1)"
              className="pic1-0"
              :src="require('../../assets/image/BG@2x.png')"
            />
            <div className="section4-0 flex-row">
              <div
                className="block1-0 flex-col"
                @click="onVideoIndex(item, 'release_')"
              >
                <span className="word12-0">开闸放行</span>
              </div>

              <div
                className="block2-01 flex-col"
                style="width: 20%"
                @click="onCapture(item)"
              >
                <span className="word12-0">抓拍</span>
              </div>

              <div
                className="block2-0 flex-col"
                @click="onVideoIndex(item, 'checkouPoint_off_')"
              >
                <span className="txt1-0">关闸</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ReconnectingWebSocket from "reconnecting-websocket";
import { mapState } from "vuex";
import { toRaw } from "vue";
import { getCarparkDeviceAdmin, openOrCloseDoor, capture } from "@/network/api";
export default {
  data() {
    return {
      deviceList: [],
      socket: null,
    };
  },
  computed: {
    ...mapState(["checkPointList", "parkSwitch"]),
  },
  watch: {
    checkPointList: function (value) {
      this.getHardwareList(toRaw(value));
    },
  },
  methods: {
    // 点击了抓拍
    async onCapture(item) {
      console.log(item);
      var { data, code, message } = await capture({
        sn: item.barCode,
      });
      this.$message({
        message,
        type: "success",
      });
      console.log(data, code, message);
    },
    async getHardwareList(value) {
      var idText = value.map((i) => i.id).join(",");
      console.log(idText);
      var { data, code } = await getCarparkDeviceAdmin({
        checkpointIds: idText,
      });

      if (code != 10002) return;
      var checkPointList = this.checkPointList;
      for (const i in data) {
        for (const j in checkPointList) {
          if (data[i].checkpointId == checkPointList[j].id) {
            data[i]["title"] = checkPointList[j].name;
          }
        }
      }
      this.deviceList = data;
      this.getVideo(data);
    },
    getVideo(data) {
      console.log(data)
      for (const i in data) {
        if (data[i].brand === "zhen_shi") {
          this.getLocalVideo_ZhenShi(data[i], Number(i) + 1);
        }
      }
    },

    getLocalVideo_ZhenShi(info, index) {
      // console.log(info.ip, index);
      if (window.WebSocket && info.ip) {
        this.socket = new ReconnectingWebSocket(`ws://${info.ip}:9080/ws:`);
        //收到消息
        this.socket.onmessage = function (data) {
          let videoImage = new FileReader();
          videoImage.onload = function (e) {
            if (e.target.readyState == 2) {
              if (!e.target.result) return;
              document
                .getElementById(`show_video${index}`)
                .setAttribute("src", e.target.result);
            }
          };
          videoImage.readAsDataURL(data.data);
        };
        //连接打开
        this.socket.onopen = function () {
          // console.info("臻识本地视频连接开启", event);
        };
        //连接断开
        this.socket.onclose = function () {
          // console.log("websocket.onclose", event.code);
        };
      }
    },

    // 点击视频开闸
    async onVideoIndex(item, RedisPreFix) {
      var { data, message, code } = await openOrCloseDoor({
        checkPointId: item.checkpointId,
        applicationReason: RedisPreFix == "release_" ? "手动放行" : "手动关闸",
        RedisPreFix,
        type: 1,
      });
      console.log(data);
      if (code == 10002) {
        this.$message({
          message,
          type: "success",
        });
      } else {
        this.$message.error(message);
      }
    },
  },
};
</script>