package com.kaiscript.dht.crawler.util;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

/**
 * Created by chenkai on 2019/4/3.
 */
public class ByteUtil {

    /**
     * 字节数组转ip
     *
     * @param bytes
     * @return
     */
    public static String bytes2Ip(byte[] bytes) {
        return String.join(".", String.valueOf(bytes[0] & 0xFF),
                String.valueOf(bytes[1] & 0xFF),
                String.valueOf(bytes[2] & 0xFF),
                String.valueOf(bytes[3] & 0xFF));
    }

    /**
     * 大端序.数值高位在低地址处
     * 端口2个字节。字节转int
     * @param bytes
     * @return
     */
    public static int bytes2Int(byte[] bytes) {
        return (bytes[0] & 0xFF) << 8 |
                (bytes[1] & 0xFF);
    }

    /**
     * 大端序
     * 端口转2字节数组
     * @param val
     * @return
     */
    public static byte[] int2TwoBytes(int val) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((val >> 8) & 0xFF);
        bytes[1] = (byte) (val & 0xFF);
        return bytes;
    }


    /**
     * 端口转4字节数组
     * @param val
     * @return
     */
    public static byte[] int2FourBytes(int val) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((val >> 24) & 0xff);
        bytes[1] = (byte) ((val >> 16) & 0xff);
        bytes[2] = (byte) ((val >> 8) & 0xff);
        bytes[3] = (byte) (val & 0xff);
        return bytes;
    }

    /**
     * 字节转16进制
     * @param bytes
     * @return
     */
    public static String byte2HexString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    /**
     * 16进制转字节
     * @param hexStr
     * @return
     */
    @SneakyThrows
    public static byte[] hexStr2Bytes(String hexStr) {
        return Hex.decodeHex(hexStr.toCharArray());

    }

}
