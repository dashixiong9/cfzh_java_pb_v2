import dayjs from 'dayjs'

// 时间格式化
export const $timeFormat = (datetime, type = 'yyyy-mm-dd') => {
    if (type == 'yyyy-mm-dd') {
        return dayjs(datetime).format('yyyy-mm-dd')
    } else {
        return dayjs(datetime).format(type)
    }
}


export const $getParkingTime = (inTime, outTime) => {
    //di作为一个变量传进来
    //如果时间格式是正确的，那下面这一步转化时间格式就可以不用了
    var dateBegin = new Date(inTime); //将-转化为/，使用new Date
    var dateEnd = new Date(outTime); //获取当前时间
    var dateDiff = dateEnd.getTime() - dateBegin.getTime(); //时间差的毫秒数
    var dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000)); //计算出相差天数
    var leave1 = dateDiff % (24 * 3600 * 1000); //计算天数后剩余的毫秒数
    var hours = Math.floor(leave1 / (3600 * 1000)); //计算出小时数
    //计算相差分钟数
    var leave2 = leave1 % (3600 * 1000); //计算小时数后剩余的毫秒数
    var minutes = Math.floor(leave2 / (60 * 1000)); //计算相差分钟数
    //计算相差秒数
    var leave3 = leave2 % (60 * 1000); //计算分钟数后剩余的毫秒数
    var seconds = Math.round(leave3 / 1000);
    // hours += dayDiff * 24;
    var dealWith = function (nums) {
        if (nums < 10) {
            return "0" + nums;
        } else {
            return nums;
        }
    };
    // console.log([dayDiff, hours, minutes, seconds]);
    if (dayDiff.toString() == "NaN") {
        return "-";
    } else {
        return (
            dealWith(dayDiff) +
            "天" +
            dealWith(hours) +
            "时" +
            dealWith(minutes) +
            "分" +
            dealWith(seconds) +
            "秒"
        );
    }
}


export const commonFilter = (value, datas, returnKey, key) => {
    let returnVal = '无';
    datas.forEach(item => {
        if (item[key] == value) {
            returnVal = returnKey == null ? item : item[returnKey];
            return;
        }
    });
    return returnVal;
}


export const isDate = (arg) => {
    return arg.constructor.toString().indexOf("Date") > -1;
}

// 解析参数
export const handleFilterParams = (listQuery) => {
    let finallyListQuery = {};
    for (const key in listQuery) {
        if (key == "limit") {
            finallyListQuery[key] = {
                operator: "limit",
                page: listQuery[key]["page"],
                limit: listQuery[key]["limit"],
            };
        } else if (key == "group") {
            finallyListQuery[key] = {
                operator: "group",
                alias: listQuery[key]["alias"],
                field: listQuery[key]["field"],
            };
        } else if (
            listQuery[key]["operator"] == "order" &&
            listQuery[key]["value"] != null &&
            listQuery[key]["value"] !== ""
        ) {
            if (
                finallyListQuery[listQuery[key]["operator"]] == null ||
                typeof finallyListQuery[listQuery[key]["operator"]] == "undefined"
            ) {
                finallyListQuery[listQuery[key]["operator"]] = {
                    list: {},
                    operator: listQuery[key]["operator"],
                };
            }
            finallyListQuery[listQuery[key]["operator"]]["list"][
                listQuery[key]["field"]
            ] = {
                type: listQuery[key]["value"],
                alias: listQuery[key]["alias"],
            };
        } else if (
            listQuery[key]["operator"] == "like" &&
            listQuery[key]["value"] != null &&
            listQuery[key]["value"] !== ""
        ) {
            if (
                finallyListQuery[listQuery[key]["operator"]] == null ||
                typeof finallyListQuery[listQuery[key]["operator"]] == "undefined"
            ) {
                finallyListQuery[listQuery[key]["operator"]] = {
                    list: {},
                    operator: listQuery[key]["operator"],
                };
            }
            let likeCollection = finallyListQuery[listQuery[key]["operator"]];
            if (
                likeCollection["list"][listQuery[key]["field"]] == null ||
                typeof likeCollection["list"][listQuery[key]["field"]] == "undefined"
            ) {
                likeCollection["list"][listQuery[key]["field"]] = {};
            }
            let temp = likeCollection["list"][listQuery[key]["field"]];
            temp[temp.length] = {
                value: listQuery[key]["value"],
                alias: listQuery[key]["alias"],
            };
        } else if (listQuery[key]["value"] !== "") {
            if (
                listQuery[key]["dataType"] == "date" &&
                isDate(listQuery[key]["value"])
            ) {
                listQuery[key]["value"] = listQuery[key]["value"].getTime();
            }
            if (
                listQuery[key]["operator"] == ">=" ||
                listQuery[key]["operator"] == "<="
            ) {
                if ((listQuery[key]["operator"] == ">=" && listQuery[key.substring(0, key.length - 2) + 'LT']['value']) ||
                    (listQuery[key]["operator"] == "<=" && listQuery[key.substring(0, key.length - 2) + 'GT']['value'])
                ) {
                    let delimter = listQuery[key]["operator"] == ">=" ? "min" : "max";
                    if (
                        finallyListQuery[listQuery[key]["field"]] == null ||
                        typeof finallyListQuery[listQuery[key]["field"]] == "undefined"
                    ) {
                        finallyListQuery[listQuery[key]["field"]] = {
                            operator: "between",
                        };
                    }
                    finallyListQuery[listQuery[key]["field"]][delimter] =
                        listQuery[key]["value"];
                } else {
                    finallyListQuery[listQuery[key]["field"]] = {
                        operator: listQuery[key]["operator"],
                        value: listQuery[key]["value"],
                    };
                }

            } else {
                finallyListQuery[listQuery[key]["field"]] = {
                    operator: listQuery[key]["operator"],
                    value: listQuery[key]["value"],
                };
            }
        }
    }
    return finallyListQuery;
};

// 计算相差时间
export const timeFn = (inTime, outTime) => {
    //di作为一个变量传进来
    //如果时间格式是正确的，那下面这一步转化时间格式就可以不用了
    var dateBegin = new Date(inTime); //将-转化为/，使用new Date
    var dateEnd = new Date(outTime); //获取当前时间
    var dateDiff = dateEnd.getTime() - dateBegin.getTime(); //时间差的毫秒数
    var dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000)); //计算出相差天数
    var leave1 = dateDiff % (24 * 3600 * 1000); //计算天数后剩余的毫秒数
    var hours = Math.floor(leave1 / (3600 * 1000)); //计算出小时数
    //计算相差分钟数
    var leave2 = leave1 % (3600 * 1000); //计算小时数后剩余的毫秒数
    var minutes = Math.floor(leave2 / (60 * 1000)); //计算相差分钟数
    //计算相差秒数
    var leave3 = leave2 % (60 * 1000); //计算分钟数后剩余的毫秒数
    var seconds = Math.round(leave3 / 1000);
    // hours += dayDiff * 24;
    var dealWith = function (nums) {
        if (nums < 10) {
            return "0" + nums;
        } else {
            return nums;
        }
    };
    // console.log([dayDiff, hours, minutes, seconds]);
    if (dayDiff.toString() == "NaN") {
        return "-";
    } else {
        console.log(hours);
        return (
            dealWith(dayDiff) +
            "天" +
            dealWith(hours) +
            "时" +
            dealWith(minutes) +
            "分" +
            dealWith(seconds) +
            "秒"
        );
    }
}