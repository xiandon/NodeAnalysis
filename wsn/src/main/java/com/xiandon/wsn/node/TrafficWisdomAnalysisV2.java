package com.xiandon.wsn.node;

import android.content.Context;
import android.util.Xml;

import com.xiandon.wsn.serial.SerialProtocol;
import com.xiandon.wsn.serial.SerialProtocolV2;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pen on 2017/11/14.
 * 解析协议，节点实体类
 * 单例模式
 */

public class TrafficWisdomAnalysisV2 {
    private Context mContext;


    public TrafficWisdomAnalysisV2(Context context) {
        mContext = context;
    }

    public NodeInfo analysis(String wsn) throws IOException, XmlPullParserException {

        String sLength = wsn.substring(6, 10);
        int iLength = Integer.parseInt(sLength, 16);

        if (iLength * 2 + 10 != wsn.length()) {
            return null;
        }

        NodeInfo nodeInfo = new NodeInfo();
        /**
         * 帧起始符
         */
        nodeInfo.setN_start(wsn.substring(0, 2));


        /**
         * 芯片类型
         */
        nodeInfo.setChip_type(wsn.substring(2, 6));

        /**
         * 数据长度
         */
        nodeInfo.setLength(wsn.substring(6, 10));

        /**
         * 传感器类型英文编号
         */
        nodeInfo.setNode_num(wsn.substring(10, 14));

        /**
         * 来源地址，传感器中文名称
         */
        String node_type;
        if (wsn.substring(10, 14).equals("0060")) {
            node_type = wsn.substring(10, 14);
        } else if (wsn.substring(10, 14).equals("0040")) {
            node_type = wsn.substring(10, 14);
        } else {
            node_type = wsn.substring(10, 14);
        }
        nodeInfo.setNode_name(getName(node_type));

        /**
         * 来源地址，传感器节点编号
         */
        nodeInfo.setNode_nums(wsn.substring(14, 18));


        /**
         * 初始地址，系统板号
         */
        nodeInfo.setSys_board(wsn.substring(18, 22));

        /**
         * 帧序列号
         */
        nodeInfo.setFrame_num(wsn.substring(22, 26));

        /**
         * 跳数
         */
        nodeInfo.setHops(wsn.substring(26, 28));

        /**
         * 传感器数据
         */
        nodeInfo.setNode_data(wsn.substring(28, wsn.length() - 4));

        /**
         * 停止符
         */
        nodeInfo.setStop_char(wsn.substring(wsn.length() - 4, wsn.length() - 2));

        /**
         * 校验位
         */
        nodeInfo.setFcs(wsn.substring(wsn.length() - 2, wsn.length()));

        /**
         * 完整协议
         */
        nodeInfo.setWsn(wsn);

        /**
         * 数据更新
         */
        nodeInfo.setData_analysis(analysisData(node_type, wsn.substring(28, wsn.length() - 4)));

        return nodeInfo;


    }


    private static String analysisData(String node_num, String node_data) {
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


    /**
     * 输入传感器编号，返回传感器中文名称
     *
     * @param node_num
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private String getName(String node_num) throws XmlPullParserException, IOException {
        InputStream inputStream = mContext.getAssets().open("node_info_traffic.xml");
        List<NodeForValue> list = null;
        NodeForValue nodeForValue = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, "UTF-8");

        int event = parser.getEventType();//产生第一个事件
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT://判断当前事件是否为文档开始事件
                    list = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:   //判断当前事件是标签元素事件
                    if ("node".equals(parser.getName())) {  //检查开始标签是否是node
                        nodeForValue = new NodeForValue();
                    }
                    if (nodeForValue != null) {
                        if ("node_num".equals(parser.getName())) {
                            nodeForValue.setNode_num(parser.nextText());
                        } else if ("node_name".equals(parser.getName())) {
                            nodeForValue.setNode_name(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("node".equals(parser.getName())) {  //检查结束标签是否是node
                        list.add(nodeForValue);
                    }
                    break;
            }
            event = parser.next();  //进入下一个触发事件
        }

        for (NodeForValue forValue : list) {
            if (node_num.equals(forValue.getNode_num())) {
                return forValue.getNode_name();
            }
        }
        return "待添加类型";
    }

    public String[] extractAmountMsg(String ptCasinoMsg) {
        String returnAmounts[] = new String[4];
        ptCasinoMsg = ptCasinoMsg.replace(" | ", " ");
        String[] amounts = ptCasinoMsg.split(" ");
        for (int i = 0; i < amounts.length; i++) {
            Pattern p = Pattern.compile("(\\d+\\.\\d+)");
            Matcher m = p.matcher(amounts[i]);
            if (m.find()) {
                returnAmounts[i] = m.group(1);
            } else {
                p = Pattern.compile("(\\d+)");
                m = p.matcher(amounts[i]);
                if (m.find()) {
                    returnAmounts[i] = m.group(1);
                }
            }
        }
        return returnAmounts;
    }
}
