package com.xiandon.wsn.medical;


import android.content.Context;

import com.xiandon.wsn.node.NodeInfo;
import com.xiandon.wsn.utils.XmlAnalysis;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by pen on 2018/1/16.
 * 医疗解析
 */

public class MedicalAnalysis {
    private Context mContext;
    private XmlAnalysis xmlAnalysis;

    public MedicalAnalysis(Context mContext) {
        this.mContext = mContext;
        xmlAnalysis = new XmlAnalysis(mContext, "node_info_medical.xml");

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

        nodeInfo.setNode_name(xmlAnalysis.getName(wsn.substring(10, 14)));

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
        nodeInfo.setData_analysis(analysisData(wsn.substring(10, 14), wsn.substring(28, wsn.length() - 4)));

        return nodeInfo;

    }

    /**
     * 处理数据解析
     *
     * @param strNodeNum  传感器编号
     * @param strNodeData 传感器数据域
     * @return 返回 数据解析后的值
     */
    private String analysisData(String strNodeNum, String strNodeData) {
        String data = "";
        switch (strNodeNum) {
            case "0005":
                /*血压*/
                if (strNodeData.length() == 12) {
                    if (strNodeData.substring(0, 2).equals("01")) {
                        data = strNodeData.substring(2, 6) + "--" + strNodeData.substring(6, 10) + "--" + strNodeData.substring(10, 12);
                    } else if (strNodeData.substring(0, 2).equals("02")) {
                        data = "";
                    }
                } else if (strNodeData.length() == 6) {
                    if (strNodeData.substring(0, 2).equals("00")) {
                        data = strNodeData.substring(2, 6);
                    }
                }
                break;
            case "0012":
                /*血氧传感器*/
                if (strNodeData.length() == 12) {
                    data = strNodeData.substring(0, 4) + "--" + strNodeData.substring(4, 8) + "--" + strNodeData.substring(8, 12);
                }
                break;
            case "0014":
                /*心率传感器*/
                if (strNodeData.length() == 4) {
                    data = strNodeData;
                }
                break;

            case "0015":
                /*心电传感器*/
                break;
            case "001d":
                /*人体温度*/
                if (strNodeData.length() == 8) {
                    data = strNodeData.substring(0, 2) + "." + strNodeData.substring(2, 4) + "℃ --" + strNodeData.substring(4, 6) + "." + strNodeData.substring(6, 8) + "℃";
                }
                break;
            default:
                break;
        }
        return data;
    }
}
