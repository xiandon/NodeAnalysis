package com.xiandon.wsn.utils;

import android.content.Context;
import android.util.Xml;

import com.xiandon.wsn.node.NodeForValue;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pen on 2018/1/16.
 */

public class XmlAnalysis {
    private Context mContext;
    private String xmlName;

    public XmlAnalysis(Context mContext, String xmlName) {
        this.mContext = mContext;
        this.xmlName = xmlName;
    }

    /**
     * 输入传感器编号，返回传感器中文名称
     *
     * @param node_num
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public String getName(String node_num) throws XmlPullParserException, IOException {
        InputStream inputStream = mContext.getAssets().open(xmlName);
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
