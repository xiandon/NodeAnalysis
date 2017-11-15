package com.xiandon.wsn.node;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pen on 2017/11/14.
 * 解析协议，节点实体类
 * 单例模式
 */

public class SmsAnalysis {
    private Context mContext;


    public SmsAnalysis(Context context) {
        mContext = context;
    }

    public NodeInfo analysis(String wsn) throws IOException, XmlPullParserException {

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
        nodeInfo.setNode_num(wsn.substring(10, 14) + wsn.substring(28, 30));

        /**
         * 来源地址，传感器中文名称
         */
        if (wsn.substring(10, 14).equals("0060")) {
            nodeInfo.setNode_name(getName(wsn.substring(10, 14) + wsn.substring(28, 30)));
        }

        /**
         * 来源地址，传感器节点编号
         */
        nodeInfo.setNode_nums(wsn.substring(14, 18) + wsn.substring(28, 30));


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
        nodeInfo.setNode_data(wsn.substring(30, wsn.length() - 4));

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
        return nodeInfo;
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
        InputStream inputStream = mContext.getAssets().open("node_info.xml");
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
}
