import {createStore} from 'vuex'
import createVuexAlong from "vuex-along"; // vuex缓存工具
export default createStore({
    state: {
        carParkList: [],
        carParkIndex:0,
        userinfo: '',
        checkPointList: [],
        inCar: [],
        outCar: [],
        chargeCar: [],
        carImage: {
            inImage: '',
            outImage: '',
            inSmallImage: '',
            outSmallImage: ''
        },
        paidCar: {},
        parkSwitch: false,
        globalConfig: {
            promptEntryExitCar: true,
            promptPayCar: true,
            promptChargeOutCar: false,
            inOutImageType: true, // false为大图 true为小图
        }
    },
    mutations: {
        SETCARPARKLIST(state, payload) {
            state.carParkList = payload
        },
        SETCHECKPOINTLIST(state, payload) {
            state.checkPointList = payload
        },
        SETIN(state, payload) {
            // console.log('入场车辆', payload);
            if (state.inCar.length > 100) {
                state.inCar = state.inCar.slice(0, 100)
            }
            state.inCar.unshift(payload)
        },
        SETOUT(state, payload) {
            if (state.outCar.length > 100) {
                state.outCar = state.outCar.slice(0, 100)
            }
            state.outCar.unshift(payload)
        },
        SETCHARGECAR(state, payload) {
            state.chargeCar = payload
        },
        SETINIMAGE(state, payload) {
            state.carImage.inImage = payload.inImage
            state.carImage.inSmallImage = payload.inSmallImage
        },
        SETOUTIMAGE(state, payload) {
            state.carImage.outImage = payload.outImage
            state.carImage.outSmallImage = payload.outSmallImage
        },
        PAIDCAR(state, payload) {
            state.paidCar = payload
        },
        SETPARKLISTSWITCH(state, payload) {
            state.parkSwitch = payload
        },
        SETCONFIG(state, payload) {
            state.globalConfig = payload
        },
        SetCarParkIndex(state,payload){
            state.carParkIndex = payload
        }
    },
    getters:{
        carParkId:state=>  state.carParkList.length > 0 ? state.carParkList[state.carParkIndex].id : ''
    },
    actions: {},
    modules: {},
    plugins: [createVuexAlong({
        name: 'vuex-along'
    })
    ]
})
