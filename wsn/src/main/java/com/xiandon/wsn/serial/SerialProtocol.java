package com.xiandon.wsn.serial;

import java.util.ArrayList;

/**
 * Created by pen on 2017/11/15.
 */

public class SerialProtocol {
    public static int FRAME_START_GATEWAYTOARM = 0x37;
    public static int FRAME_START_ARMTOGATEWAY = 0x36;
    public static int MCU_LENGTH = 2;
    public static int MCUTYPE_HB_CC2430 = 0x24;
    public static int MCUTYPE_LB_CC2430 = 0x30;
    public static int MCUTYPE_HB_CC2530 = 0x25;
    public static int MCUTYPE_LB_CC2530 = 0x30;
    public static int MCUTYPE_NUM = 2;
    public static int MIN_FRAME_LEN = 16;
    public static int MAX_FRAME_LEN = 128;

    // frame field
    byte startByte;
    int mcuType = -1;
    int frameLen;
    int sensorType;
    int addrSrc;
    public int addrOriginal;
    int frameSN;
    public int hopCounter;
    int dataLen;
    byte[] dataBuf;
    byte stopElement;
    byte FCS;
    byte[] frameData;
    public String strFrameData;
    // fields
    public static ArrayList<byte[]> mcuTypes;
    public static ArrayList<byte[]> frameRecvHdrs;
    public static boolean bIsInitFrameHdr = false;
    ;

    public static int iHandValidIdx = 0;
    public static int recvDataLen = 0;

    public SerialProtocol() {
        initFrameHdr();

    }

    static public void initFrameHdr() {
        int i = 0, j = 0, k = 0;
        if (mcuTypes == null) {
            mcuTypes = new ArrayList<byte[]>();
        }
        if (frameRecvHdrs == null) {
            frameRecvHdrs = new ArrayList<byte[]>();
        }
        mcuTypes.clear();
        frameRecvHdrs.clear();
        for (i = 0; i < MCUTYPE_NUM; i++) {
            byte[] baMcuType = new byte[MCU_LENGTH];
            switch (i) {
                case 0:
                    // #define MUC_CC2430 0x2430
                    j = 0;
                    baMcuType[j++] = (byte) MCUTYPE_HB_CC2430;
                    baMcuType[j++] = (byte) MCUTYPE_LB_CC2430;
                    break;
                case 1:
                    // #define MUC_CC2430 0x2530
                    j = 0;
                    baMcuType[j++] = (byte) MCUTYPE_HB_CC2530;
                    baMcuType[j++] = (byte) MCUTYPE_LB_CC2530;
                    break;
            }
            k = 0;
            j = 0;
            mcuTypes.add(baMcuType);
            byte[] baFramHdr = new byte[MCU_LENGTH + 1];
            baFramHdr[k++] = (byte) FRAME_START_GATEWAYTOARM; // #define START
            // 0x37
            baFramHdr[k++] = baMcuType[j++];
            baFramHdr[k++] = baMcuType[j++];
            frameRecvHdrs.add(baFramHdr);
        }
        bIsInitFrameHdr = true;
    }

    static public byte calcCheck(byte[] ba, int start, int len) {
        byte result = 0;
        int end = len + start;
        if (end < ba.length && len > 0) {
            int i = start;
            result = ba[i++];
            while (i < end) {
                result = (byte) (result ^ ba[i]);
                i++;
            }
        }
        return result;
    }

    static public boolean DataCheck(byte[] ba) {
        boolean rel = false;
        if (ba.length > 2) {
            byte result;
            int i = 0;
            result = calcCheck(ba, 1, ba.length - 3);
            i = ba.length - 1;
            if (result == ba[i])
                rel = true;
            else
                rel = false;
        }
        return rel;
    }

    /**
     * 16进制编码转字符串
     *
     * @param s
     * @return
     */
    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    static public ArrayList<String> aReceive(byte[] dataReceived, int start) {
        ArrayList<String> slRel = new ArrayList<String>();
        if (!bIsInitFrameHdr) {
            initFrameHdr();
            bIsInitFrameHdr = true;
        }
        // recvDataLen = dataReceived.length;
        int i = 0;
        int end = start + recvDataLen;
        boolean mbHdr = false;
        while (start < end) {
            if (end - start > 2) {
                for (i = 0; i < MCUTYPE_NUM; i++) {
                    byte[] baFramHdr = (byte[]) frameRecvHdrs.get(i);
                    if (dataReceived[start + 0] == baFramHdr[0] && dataReceived[start + 1] == baFramHdr[1]
                            && dataReceived[start + 2] == baFramHdr[2]) {
                        mbHdr = true;
                        break;
                    }
                }

                if (!mbHdr) {
                    start += 1;
                    continue;
                }

                int leftLen = end - start;
                int dataLen = 0;
                int frameLen = 0;
                byte[] frameData;

                if (leftLen <= MIN_FRAME_LEN) // frame error
                    break;

                dataLen = bytesToInt(dataReceived, start + 3, 2);// dataReceived[index+3]<<8
                // dataReceived[index+4];

                if (dataLen > MAX_FRAME_LEN) {
                    // error frame
                    start += MIN_FRAME_LEN;
                    continue;
                }

                if (leftLen < dataLen + 6) { // frame error
                    // iHandValidIdx = index+MIN_FRAME_LEN;
                    break;
                }
                frameLen = dataLen + 6;
                frameData = new byte[frameLen];
                for (int j = 0; j < frameLen; j++) {
                    frameData[j] = dataReceived[start++];
                    printHexString(frameData);
                }
                if (DataCheck(frameData)) {
                    String strHex = bytesToHexString(frameData);
                    slRel.add(strHex);
                }
            } else {
                break;
            }
        }
        iHandValidIdx = start++;
        return slRel;
    }

    static public ArrayList<byte[]> ReceiveToQBA(byte[] dataReceived, int start) {
        ArrayList<byte[]> qlRel = new ArrayList<byte[]>();
        if (!bIsInitFrameHdr) {
            initFrameHdr();
            bIsInitFrameHdr = true;
        }
        // recvDataLen = dataReceived.length;
        int i = 0;
        int end = start + recvDataLen;
        boolean mbHdr = false;
        while (start < end) {
            if (end - start > 2) {
                for (i = 0; i < MCUTYPE_NUM; i++) {
                    byte[] baFramHdr = (byte[]) frameRecvHdrs.get(i);
                    if (dataReceived[start + 0] == baFramHdr[0] && dataReceived[start + 1] == baFramHdr[1]
                            && dataReceived[start + 2] == baFramHdr[2]) {
                        mbHdr = true;
                        break;
                    }
                }

                if (!mbHdr) {
                    start += 1;
                    continue;
                }

                int leftLen = end - start;
                int dataLen = 0;
                int frameLen = 0;
                byte[] frameData;

                if (leftLen <= MIN_FRAME_LEN)
                    break;

                dataLen = bytesToInt(dataReceived, start + 3, 2);

                if (dataLen > MAX_FRAME_LEN) {
                    // error frame
                    start += MIN_FRAME_LEN;
                    continue;
                }

                if (leftLen < dataLen + 6) { // frame error
                    // iHandValidIdx = index+MIN_FRAME_LEN;
                    break;
                }
                frameLen = dataLen + 6;
                frameData = new byte[frameLen];
                for (int j = 0; j < frameLen; j++) {
                    frameData[j] = dataReceived[start++];//
                }
                if (DataCheck(frameData)) //
                    printHexString(frameData);

                {
                    qlRel.add(frameData);
                }

            } else {
                break;
            }
        }

        iHandValidIdx = start++;
        return qlRel;
    }

    public void FrameDataToStructure(byte[] frameDataInput) {
        int miIdx = 0;

        this.frameData = frameDataInput;

        startByte = frameData[miIdx++];

        String strTemp = bytesToHexString(frameData, miIdx, 2);
        mcuType = Integer.valueOf(strTemp);
        miIdx += 2;

        // frameLen = frameData[miIdx++]&0xFF <<8 + frameData[miIdx++]&0xFF ;
        frameLen = bytesToInt(frameData, miIdx, 2);
        miIdx += 2;

        // sensorType = frameData[miIdx++]&0xFF <<8 + frameData[miIdx++]&0xFF ;
        sensorType = bytesToInt(frameData, miIdx, 2);
        miIdx += 2;

        // addrSrc = frameData[miIdx++]&0xFF <<8 + frameData[miIdx++]&0xFF ;
        addrSrc = bytesToInt(frameData, miIdx, 2);
        miIdx += 2;

        // addrOriginal = frameData[miIdx++]&0xFF <<8 + frameData[miIdx++]&0xFF
        // ;
        addrOriginal = bytesToInt(frameData, miIdx, 2);
        miIdx += 2;

        // frameSN = frameData[miIdx++]&0xFF <<8 + frameData[miIdx++]&0xFF ;
        frameSN = bytesToInt(frameData, miIdx, 2);
        miIdx += 2;

        // hopCounter =frameData[miIdx++]&0xFF;
        hopCounter = bytesToInt(frameData, miIdx, 1);
        miIdx += 2;

        dataLen = frameLen - 10;
        miIdx = 14;
        dataBuf = new byte[dataLen];
        for (int i = 0; i < dataLen; i++) {
            dataBuf[i] = frameData[miIdx++];
        }
        stopElement = frameData[miIdx++];
        FCS = frameData[miIdx++];
        strFrameData = bytesToHexString(frameData).toUpperCase();
    }

    public byte[] StructureToFrameData() {
        byte[] sendData = new byte[frameLen + 7];
        int iIdx = 0;
        sendData[iIdx++] = (byte) FRAME_START_ARMTOGATEWAY;

        sendData[iIdx] = frameData[iIdx];// (byte) (mcuType/100) ;
        iIdx += 1;
        sendData[iIdx] = frameData[iIdx];// (byte) (mcuType%100) ;
        iIdx += 1;

        sendData[iIdx++] = (byte) (frameLen / 256);
        sendData[iIdx++] = (byte) (frameLen % 256);
        sendData[iIdx++] = (byte) (sensorType / 256);
        sendData[iIdx++] = (byte) (sensorType % 256);
        sendData[iIdx++] = (byte) (addrSrc / 256);
        sendData[iIdx++] = (byte) (addrSrc % 256);
        sendData[iIdx++] = (byte) (addrOriginal / 256);
        sendData[iIdx++] = (byte) (addrOriginal % 256);
        sendData[iIdx++] = (byte) (frameSN / 256);
        sendData[iIdx++] = (byte) (frameSN % 256);
        sendData[iIdx++] = (byte) (hopCounter % 256);

        for (int i = 0; i < dataLen; i++) {
            sendData[iIdx++] = dataBuf[i];
        }
        sendData[iIdx++] = stopElement;
        FCS = calcCheck(frameData, 1, frameData.length - 3);
        sendData[iIdx++] = FCS;
        sendData[iIdx++] = (byte) 0xff;
        return sendData;
    }

    static public String bytesToHexString(byte[] src, int start, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0 || (start + len) > src.length) {
            return null;
        }
        for (int i = start; i < start + len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /*
     *
     */
    static public int bytesToInt(byte[] src, int start, int len) {
        int iRel = -1;
        if (src == null || src.length <= 0 || (start + len) > src.length || len > 4) {
            return iRel;
        }
        iRel = 0;
        for (int i = start; i < start + len; i++) {
            int v = src[i] & 0xFF;
            iRel = iRel * 256 + v;
        }
        return iRel;
    }

    static public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    static public int StrHexToInt(String strAscii) {
        int iRel = 0;
        for (int i = 0; i < strAscii.length(); i++) {
            char ch = strAscii.charAt(i);
            if (ch >= '0' && ch <= '9') {
                ch -= '0';
                iRel = iRel * 16 + ch;
            } else if (ch >= 'A' && ch <= 'F') {
                ch = (char) (ch + 10 - 'A');
                iRel = iRel * 16 + ch;
            } else {
                break;
            }
        }
        return iRel;
    }

    static public int StrToInt(String strAscii) {
        int iRel = 0;
        for (int i = 0; i < strAscii.length(); i++) {
            char ch = strAscii.charAt(i);
            if (ch >= '0' && ch <= '9') {
                ch -= '0';
                iRel = iRel * 16 + ch;
            } else if (ch >= 'A' && ch <= 'F') {
                ch = (char) (ch + 10 - 'A');
                iRel = iRel * 16 + ch;
            } else {
                break;
            }
        }
        return iRel;
    }

    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
        }

    }

    // 字符串变byte[]16进制数组
    public static byte[] string2byteArrays(String s) {
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


    public static int find(String[] arr, String str) {
        boolean flag = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(str)) {
                flag = true;
                return i;
            }
        }
        if (flag == false) {
            return -1;
        }
        return -1;
    }

    public static boolean isHave(String[] strs, String s) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].indexOf(s) != -1) {
                return true;
            }
        }
        return false;
    }

    // 十六进制转二进制 生成字符串
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    // 统计字符串
    public static int getSubString(String str, String key) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(key, index)) != -1) {
            index = index + key.length();
            count++;
        }
        return count;
    }
}
