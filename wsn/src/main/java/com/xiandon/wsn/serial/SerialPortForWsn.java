package com.xiandon.wsn.serial;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Hashtable;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

/**
 * Created by pen on 2017/11/15.
 * 串口流接受类
 */

public class SerialPortForWsn {
    public SerialPortFinder mSerialPortFinder;
    private SerialPort mSerialPort = null;
    private String strError;
    private Hashtable<String, String> htSerialToPath = null;
    private String[] deviceEntries = null;

    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private Handler hdlrRcv;

    /**
     * 发送消息，准备接收数据
     *
     * @param hdlrRcv
     */
    public SerialPortForWsn(Handler hdlrRcv) {
        mSerialPortFinder = new SerialPortFinder();
        deviceEntries = mSerialPortFinder.getSerials(); //mSerialPortFinder.getAllDevices();
        htSerialToPath = mSerialPortFinder.getSerialsToPath();// mSerialPortFinder.getAllDevicesPath();
        this.hdlrRcv = hdlrRcv;
    }

    public String[] getSerials() {
        return deviceEntries;
    }

    public Hashtable<String, String> getSerialsToPath() {
        return htSerialToPath;
    }

    public String getErr() {
        return strError;
    }

    /**
     * 打开串口
     *
     * @param strDevicePath
     * @param strBaudrate
     * @return
     * @throws SecurityException
     * @throws IOException
     * @throws InvalidParameterException
     */
    public SerialPort open(String strDevicePath, String strBaudrate) throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            try {
                int baudrate = Integer.valueOf(strBaudrate);

			/* Check parameters */
                if (strDevicePath == null
                        || strDevicePath.length() == 0
                        || baudrate == -1) {
                    throw new InvalidParameterException();
                }

			/* Open the serial port */
                mSerialPort = new SerialPort(new File(strDevicePath), baudrate, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
            /* Create a receiving thread */
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (Exception ep) {
                strError = ep.getMessage();
            }
        }
        return mSerialPort;
    }

    int iBufLen = 1024;
    byte[] buffer = new byte[iBufLen];
    public boolean bIsIdle = true;

    public void setIsIdle(boolean b) {
        bIsIdle = b;
    }

    /**
     * 流接收线程
     */
    public class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (bIsIdle) {
                    int size;
                    try {
                        if (mInputStream == null) return;
                        size = mInputStream.read(buffer, 0, iBufLen);
                        if (size > 0) {
                            Message msg = Message.obtain(hdlrRcv, 3, size, 0, buffer);
                            if (msg != null) {
                                msg.what = 3;
                                msg.arg1 = size;
                                msg.obj = buffer;
                                hdlrRcv.sendMessage(msg);
                            }
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        strError = e.getMessage();
                        return;
                    }
                }
            }
        }
    }

    public Message myGetMessage() {
        Message message = Message.obtain();
        if (message == null) {
            message = new Message();
        }
        return message;
    }

    /**
     * 串口下发数据
     *
     * @param mSendBuffer
     * @param start
     * @param len
     * @throws IOException
     */
    public void sendData(byte[] mSendBuffer, int start, int len) throws IOException {
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mSendBuffer, 0, mSendBuffer.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            strError = e.getMessage();
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
    }
}
