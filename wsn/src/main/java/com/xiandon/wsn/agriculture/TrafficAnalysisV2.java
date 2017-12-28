package com.xiandon.wsn.agriculture;


import com.xiandon.wsn.serial.SerialProtocol;
import com.xiandon.wsn.serial.SerialProtocolV2;


/**
 * Created by pen on 2017/11/30.
 * 智能交通
 */

public class TrafficAnalysisV2 {

    public TrafficAnalysisV2() {
    }

    public static String analysis(String node_num, String node_data) {
        String strAnalysis = "";
        switch (node_num) {
            case "0022":
                String strHeiPlant = SerialProtocolV2.toStringHex(node_data.substring(0, 4));//车牌号
                String strHeiCarType = carType(node_data.substring(4, 6));//小车类型
                String strHeiCharges = node_data.substring(6, 8);//需要扣的费用
                String strHeiRemaining = node_data.substring(8, 10);//剩余费用
                String etcHeiStatus = checkBuilding(node_data.substring(10, 12));//ETC状态
                strAnalysis = strHeiPlant + "--" + strHeiCarType + "--" + strHeiCharges + "--" + strHeiRemaining + "--" + etcHeiStatus;
                break;
            case "0033":
                String redLightNum = node_data.substring(0, 4);
                String redLightGroup = node_data.substring(4, 6);
                String redLightStatus = checkRedLight(node_data.substring(6, 8));
                strAnalysis = redLightNum + "--" + redLightGroup + "--" + redLightStatus;

                break;
            case "0044":
                String strCarLicense = SerialProtocol.toStringHex(node_data.substring(0, 4));
                String strCarType = carType(node_data.substring(4, 8));
                String strCarAddress = SerialProtocol.toStringHex(node_data.substring(10, 18));
                strAnalysis = strCarLicense + "--" + strCarType + "--" + strCarAddress.toString();
                break;
            case "0055":
                String strBuildingNum = node_data.substring(2, 6);
                String strBuildingStatus = checkBuilding(node_data.substring(6, 10));
                strAnalysis = strBuildingNum + "--" + strBuildingStatus;
                break;
            case "0077":
                String strPlant = SerialProtocolV2.toStringHex(node_data.substring(0, 4));//车牌号
                String strInCarType = carType(node_data.substring(4, 6));//小车类型
                String strLast = node_data.substring(6, 8);//剩余车位
                String etcInStatus = checkBuilding(node_data.substring(8, 10));//ETC状态
                int i = SerialProtocol.getSubString(SerialProtocol.hexString2binaryString(strLast), "1");
                strAnalysis = strPlant + "--" + strInCarType + "--" + etcInStatus + "--" + i + " 个 ";
                break;
            case "0099":
                String strOutPlant = SerialProtocolV2.toStringHex(node_data.substring(0, 4));//车牌号
                String strOutCarType = carType(node_data.substring(4, 6));//小车类型
                String strOutCharges = node_data.substring(6, 8);//需要扣的费用
                String strOutRemaining = node_data.substring(8, 10);//剩余费用
                String etcOutStatus = checkBuilding(node_data.substring(10, 12));//ETC状态
                strAnalysis = strOutPlant + "--" + strOutCarType + "--" + strOutCharges + "--" + strOutRemaining + "--" + etcOutStatus;
                break;
            case "00dd":
                String strBusStop = SerialProtocolV2.toStringHex(node_data.substring(0, 4));
                strAnalysis = strBusStop;
                break;
            case "00ee":
                String strStreetLights = checkStreetLights(node_data.substring(2, 4));
                strAnalysis = strStreetLights;
                break;
        }
        return strAnalysis;
    }

    /**
     * 路灯模式
     *
     * @param substring
     * @return
     */
    private static String checkStreetLights(String substring) {
        if (substring.equals("01")) {
            return "节能模式";
        } else if (substring.equals("02")) {
            return "标准模式";
        } else {
            return "传感器信号异常";
        }
    }

    /**
     * 公交站台
     *
     * @param substring
     * @return
     */
    private static String checkBusStop(String substring) {
        if (substring.equals("0001")) {
            return "东川路莲花南路站";
        } else if (substring.equals("0002")) {
            return "东川路虹梅南路站";
        } else {
            return "无名路站";
        }
    }

    /**
     * 建筑灯状态
     *
     * @param substring
     * @return
     */
    private static String checkBuilding(String substring) {
        if (substring.equals("01")) {
            return "打开";
        } else if (substring.equals("02")) {
            return "关闭";
        } else {
            return "传感器信号异常";
        }
    }

    /**
     * 小车类型
     *
     * @param substring
     * @return
     */
    private static String carType(String substring) {
        if (substring.equals("01")) {
            return "私家车";
        } else if (substring.equals("02")) {
            return "公交车";
        } else if (substring.equals("03")) {
            return "特种车";
        } else {
            return "其他车辆";
        }
    }

    /**
     * 红绿灯状态
     *
     * @param substring
     * @return
     */
    private static String checkRedLight(String substring) {
        if (substring.equals("01")) {
            return "红";
        } else if (substring.equals("02")) {
            return "黄";
        } else if (substring.equals("04")) {
            return "绿";
        } else if (substring.equals("08")) {
            return "闪烁";
        } else {
            return "传感器信息异常";
        }
    }

    /**
     * ETC状态
     *
     * @param substring
     * @return
     */
    private static String checkSwitch(String substring) {
        if (substring.equals("0001")) {
            return "打开";
        } else if (substring.equals("0002")) {
            return "关闭";
        } else {
            return "传感器信号异常";
        }
    }

}
