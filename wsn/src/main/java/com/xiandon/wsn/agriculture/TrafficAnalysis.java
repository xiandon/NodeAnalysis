package com.xiandon.wsn.agriculture;


import com.xiandon.wsn.serial.SerialProtocol;


/**
 * Created by pen on 2017/11/30.
 * 智能交通
 */

public class TrafficAnalysis {

    public TrafficAnalysis() {
    }

    public static String analysis(String node_num, String node_data) {
        String strAnalysis = "";
        switch (node_num) {
            case "006022":
                String etcNum = node_data.substring(2, 6);
                String strSwitch = checkSwitch(node_data.substring(6, 10));
                strAnalysis = etcNum + "and" + strSwitch;
                break;
            case "006033":
                String redLightNum = node_data.substring(2, 6);
                String redLightGroup = node_data.substring(6, 8);
                String redLightStatus = checkRedLight(node_data.substring(8, 10));
                strAnalysis = redLightNum + "and" + redLightGroup + "and" + redLightStatus;
                break;
            case "006044":
                String strCarLicense = SerialProtocol.toStringHex(node_data.substring(2, 6));
                String strCarType = carType(node_data.substring(6, 8));
                String strCarAddress = SerialProtocol.toStringHex(node_data.substring(10, 18));
                strAnalysis = strCarLicense + "and" + strCarType + "and" + strCarAddress;
                break;
            case "006055":
                String strBuildingNum = node_data.substring(2, 6);
                String strBuildingStatus = checkBuilding(node_data.substring(6, 10));
                strAnalysis = strBuildingNum + "and" + strBuildingStatus;
                break;
            case "006077":
                String strLast = node_data.substring(2, 6);
                int i = SerialProtocol.getSubString(SerialProtocol.hexString2binaryString(strLast), "1");
                strAnalysis = i + "个";
                break;
            case "006088":
                String etcEntranceNum = node_data.substring(2, 6);
                String strEntranceSwitch = checkSwitch(node_data.substring(6, 10));
                strAnalysis = etcEntranceNum + "and" + strEntranceSwitch;
                break;
            case "006099":
                String etcExitNum = node_data.substring(2, 6);
                String strExitSwitch = checkSwitch(node_data.substring(6, 10));
                strAnalysis = etcExitNum + "and" + strExitSwitch;
                break;
            case "0060dd":
                String strBusStop = checkBusStop(node_data.substring(2, 6));
                strAnalysis = strBusStop;
                break;
            case "0060ee":
                String strStreetLights = checkStreetLights(node_data.substring(6, 8));
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
        if (substring.equals("0000")) {
            return "打开";
        } else if (substring.equals("0001")) {
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
