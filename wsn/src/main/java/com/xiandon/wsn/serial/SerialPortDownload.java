package com.xiandon.wsn.serial;

import android.util.Log;

import com.xiandon.wsn.utils.DataUtils;

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
        byte[] ff = DataUtils.string2byteArrays(str);
        try {
            mSerialPort.getOutputStream().write(ff, 0, ff.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
