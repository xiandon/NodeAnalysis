package com.xiandon.wsn;

/**
 * Created by pen on 2017/11/14.
 * 节点信息
 */

public class NodeInfo {
    /**
     * 帧起始符
     */
    private String n_start;

    /**
     * 芯片类型
     */
    private String chip_type;

    /**
     * 数据长度
     */
    private String length;

    /**
     * 传感器类型英文编号
     */
    private String node_num;

    /**
     * 来源地址，传感器中文名称
     */
    private String node_name;

    /**
     * 来源地址，传感器节点编号
     */
    private String node_nums;


    /**
     * 初始地址，系统板号
     */
    private String sys_board;

    /**
     * 帧序列号
     */
    private String frame_num;

    /**
     * 跳数
     */
    private String hops;

    /**
     * 传感器数据
     */
    private String node_data;

    /**
     * 停止符
     */
    private String stop_char;

    /**
     * 校验位
     */
    private String fcs;

    public String getN_start() {
        return n_start;
    }

    public void setN_start(String n_start) {
        this.n_start = n_start;
    }

    public String getChip_type() {
        return chip_type;
    }

    public void setChip_type(String chip_type) {
        this.chip_type = chip_type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getNode_num() {
        return node_num;
    }

    public void setNode_num(String node_num) {
        this.node_num = node_num;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public String getNode_nums() {
        return node_nums;
    }

    public void setNode_nums(String node_nums) {
        this.node_nums = node_nums;
    }

    public String getSys_board() {
        return sys_board;
    }

    public void setSys_board(String sys_board) {
        this.sys_board = sys_board;
    }

    public String getFrame_num() {
        return frame_num;
    }

    public void setFrame_num(String frame_num) {
        this.frame_num = frame_num;
    }

    public String getHops() {
        return hops;
    }

    public void setHops(String hops) {
        this.hops = hops;
    }

    public String getNode_data() {
        return node_data;
    }

    public void setNode_data(String node_data) {
        this.node_data = node_data;
    }

    public String getStop_char() {
        return stop_char;
    }

    public void setStop_char(String stop_char) {
        this.stop_char = stop_char;
    }

    public String getFcs() {
        return fcs;
    }

    public void setFcs(String fcs) {
        this.fcs = fcs;
    }
}
