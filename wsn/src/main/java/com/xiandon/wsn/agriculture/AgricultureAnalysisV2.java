package com.xiandon.wsn.agriculture;


import java.text.DecimalFormat;

/**
 * Created by pen on 2017/11/16.
 */

public class AgricultureAnalysisV2 {
    private static DecimalFormat df = new DecimalFormat("0.00");

    public AgricultureAnalysisV2() {
    }


    public static String analysis(String node_num, String node_data) {
        String data = null;
        switch (node_num) {
            case "0082":
                String fx = null;
                double windDirection = (Integer.valueOf(node_data.substring(0, 4), 16));
                double fx1 = windDirection / 100;
                if (fx1 >= 5 - (5 * 0.06) && fx1 <= 5 + (5 * 0.06)) {
                    fx = "北风";
                } else if (fx1 >= 1.25 - (1.25 * 0.06) && fx1 <= 1.25 + 1.25 * 0.06) {
                    fx = "东风";
                } else if (fx1 >= 2.5 - (2.5 * 0.06) && fx1 <= 2.5 + 2.5 * 0.06) {
                    fx = "南风";
                } else if (fx1 >= 3.75 - (3.75 * 0.06) && fx1 <= 3.75 + 3.75 * 0.06) {
                    fx = "西风";
                } else if (fx1 >= 0.31 - (0.31 * 0.06) && fx1 <= 0.31 + 0.31 * 0.06) {
                    fx = "东北偏北";
                } else if (fx1 >= 1.56 - (1.56 * 0.06) && fx1 <= 1.56 + 1.56 * 0.06) {
                    fx = "东南偏南";
                } else if (fx1 >= 0.63 - (0.63 * 0.06) && fx1 <= 0.63 + 0.63 * 0.06) {
                    fx = "东北";
                } else if (fx1 >= 1.88 - (1.88 * 0.06) && fx1 <= 1.88 + 1.88 * 0.06) {
                    fx = "东南";
                } else if (fx1 >= 0.94 - (0.94 * 0.06) && fx1 <= 0.94 + 0.94 * 0.06) {
                    fx = "东北偏东";
                } else if (fx1 >= 2.19 - (2.19 * 0.06) && fx1 <= 2.19 + 2.19 * 0.06) {
                    fx = "东南偏南";
                } else if (fx1 >= 2.81 - (2.81 * 0.06) && fx1 <= 2.81 + 2.81 * 0.06) {
                    fx = "西南偏南";
                } else if (fx1 >= 4.06 - (4.06 * 0.06) && fx1 <= 4.06 + 4.06 * 0.06) {
                    fx = "西北偏西";
                } else if (fx1 >= 3.13 - (3.13 * 0.06) && fx1 <= 3.13 + 3.13 * 0.06) {
                    fx = "西南";
                } else if (fx1 >= 4.38 - (4.38 * 0.06) && fx1 <= 4.38 + 4.38 * 0.06) {
                    fx = "西北";
                } else if (fx1 >= 3.44 - (3.44 * 0.06) && fx1 <= 3.44 + 3.44 * 0.06) {
                    fx = "西南偏西";
                } else if (fx1 >= 4.69 - (4.69 * 0.06) && fx1 <= 4.69 + 4.69 * 0.06) {
                    fx = "西北偏北";
                } else {
                    // other
                    fx = "传感器错误";
                }
                data = fx;
                break;

            case "0083":
                double xx = (Integer.valueOf(node_data.substring(0, 4), 16));
                double x22 = xx / 5 - 30;
                double yy = (Integer.valueOf(node_data.substring(4, 8), 16));
                double y22 = 0;
                y22 = yy * 0.2 + 0.5;
                if (y22 > 100) {
                    y22 = 99.9;
                }
                data = df.format(x22) + "℃ | " + df.format(y22) + "%RH";

                break;
            case "0007":
                int dataYuDi = (Integer.valueOf(node_data.substring(0, 4), 16));
                String raindrop = null;
                if (dataYuDi == 0) {
                    raindrop = "有雨滴";
                } else {
                    raindrop = "无雨滴";
                }
                data = raindrop;
                break;
            case "0037":
                String irrigation = null;

                if (node_data.substring(0, 4).equals("0000")) {
                    irrigation = "打开";
                } else {
                    irrigation = "关闭";
                }
                data = irrigation;
                break;
            case "000c":
                String smoke = null;
                int i = (Integer.valueOf(node_data.substring(0, 4), 16));
                if (i == 0) {
                    smoke = "有烟雾";

                } else {
                    smoke = "无烟雾";
                }
                data = smoke;
                break;
            case "0084":
                int co2 = hexToTen(node_data.substring(0, 4));
                data = co2 + "";
                break;
            case "0086":
                int tHumidity = (Integer.valueOf(node_data.substring(6, 10), 16));
                int tTemperature = (Integer.valueOf(node_data.substring(10, 14), 16));
                data = df.format(tHumidity / 10) + "%RH | " + df.format(tTemperature / 10) + "℃";
                break;
            case "0039":
                int alarm = hexToTen(node_data.substring(0, 4));
                String sAlarm = null;
                if (alarm == 0) {
                    sAlarm = "打开";
                } else {
                    sAlarm = "关闭";
                }
                data = sAlarm;
                break;
            case "0031":
                int ventilation = hexToTen(node_data.substring(0, 4));
                String sVentilation = null;
                if (ventilation == 0) {
                    sVentilation = "打开";
                } else {
                    sVentilation = "关闭";
                }
                data = sVentilation;
                break;
            case "0038":
                int spray = hexToTen(node_data.substring(0, 4));
                String sSpray = null;
                if (spray == 0) {
                    sSpray = "打开";
                } else {
                    sSpray = "关闭";
                }
                data = sSpray;
                break;
            case "0032":
                int light = hexToTen(node_data.substring(0, 4));
                String sLight = null;
                if (light == 0) {
                    sLight = "打开";
                } else {
                    sLight = "关闭";
                }
                data = sLight;
                break;
            case "0043":
                int wind = hexToTen(node_data.substring(0, 4));
                String sWind = null;
                if (wind == 0) {
                    sWind = "打开";
                } else {
                    sWind = "关闭";
                }
                data = sWind;
                break;
            case "0001":
                int illumination = hexToTen(node_data.substring(0, 4));
                data = illumination * 400 + "";
                break;
            case "0080":
                    /*水位*/
                double iWater = hexToTen(node_data.substring(0, 4));
                String sHeight = "";
                double dHeight = iWater / 100 / 0.05;
                sHeight = df.format(dHeight) + "厘米";
                data = "当前水位：" + sHeight;
                break;
            case "0081":
                    /*水质PH值*/
                double iPH = hexToTen(node_data.substring(0, 4));
                String sPH = "";
                if (iPH / 100 < 4.85) {
                    double dPH = (4.85 - iPH / 100) / 0.18 + 1;
                    sPH = df.format(dPH) + "";
                }
                data = "当前PH值：" + sPH;
                break;
            case "0087":
                    /*液体浊度*/
                    /*需要校准*/

                double iTurbid = hexToTen(node_data.substring(0, 4));
                double bTurbid = (1 - iTurbid / 100 / 3.2) * 100;
                data = "浑浊度：" + df.format(Math.abs(bTurbid)) + "%";

                break;
            case "0088":
                    /*液体杂质*/
                double iImpurities = hexToTen(node_data.substring(0, 4));
                data = "当前TDS：" + df.format(iImpurities / 100) + "ppm";
                break;
            case "0089":
                    /*模拟CO2*/
                double iCO2 = hexToTen(node_data.substring(0, 4));
                String sCO2 = "";
                double iCO2Concentration = iCO2 / 0.096;
                if (iCO2Concentration - 200 > 0) {
                    sCO2 = df.format(iCO2Concentration - 200) + "";
                } else {
                    sCO2 = "0";
                }
                data = sCO2;
                break;
            case "0040":
                String switch_status = node_data.substring(0, 4);
                String kai = null;
                if (switch_status.equals("0001")) {
                    kai = "打开";
                } else if (switch_status.equals("0002")) {
                    kai = "关闭";
                } else if (switch_status.equals("0000")) {
                    kai = "关闭";
                }
                data = "开关状态：" + kai;
                break;
        }
        return data;
    }

    private static int hexToTen(String string) {
        int i = (Integer.valueOf(string, 16));
        return i;
    }
}
