package com.xiandon.wsn.serial;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

import static android.content.ContentValues.TAG;

/**
 * Created by pen on 2017/11/15.
 * 下发串口下发数据方法
 */

public class SerialPortDownload {
    public SerialPort mSerialPort;
    public OutputStream mOutputStream;

    public SerialPort open(String strDevicePath, String strBaudrate)
            throws SecurityException, IOException, InvalidParameterException {// 获取路径

        System.out.println(strDevicePath + "**" + strBaudrate);
        if (mSerialPort == null) {
            /* Read serial port parameters */
            try {
                int baudrate = Integer.valueOf(strBaudrate);// 得到波率变int

				/* Check parameters */
                if (strDevicePath == null || strDevicePath.length() == 0 || baudrate == -1) {
                    throw new InvalidParameterException();
                }

				/* Open the serial port */
                mSerialPort = new SerialPort(new File(strDevicePath), baudrate, 0);

            } catch (Exception ep) {

            }
        }
        return mSerialPort;
    }

    // 执行下发的动作
    public void DownData(String str) {
        Log.i(TAG, "DownData: " + str);
        if (str == null || str.length() < 30) {
            return;
        }
        byte[] ff = string2byteArrays(str);
        try {
            mSerialPort.getOutputStream().write(ff, 0, ff.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 字符转16进制byte[]
    public byte[] string2byteArrays(String s) {// 字符变byte[]16
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
