package com.xiandon.wsn.utils;

/**
 * Created by pen on 2018/1/16.
 */

public class DataUtils {
    /**
     * 16进制字符串转16进制字符数组
     *
     * @param s
     * @return
     */
    public static byte[] string2byteArrays(String s) {// 字符变byte[]16
        String ss = s.replace(" ", "");
        int string_len = ss.length();
        int len = string_len / 2;
        if (string_len % 2 == 1) {
            ss = "0" + ss;
            string_len++;
            len++;
        }
        byte[] a = new byte[len];
        for (int i = 0; i < len; i++) {
            a[i] = (byte) Integer.parseInt(ss.substring(2 * i, 2 * i + 2), 16);
        }
        return a;
    }

}
