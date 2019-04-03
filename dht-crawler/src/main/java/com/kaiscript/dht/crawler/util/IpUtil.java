package com.kaiscript.dht.crawler.util;

/**
 * Created by chenkai on 2019/4/3.
 */
public class IpUtil {

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
    public static int bytes2Port(byte[] bytes) {
        return (bytes[0] & 0xFF) << 8 |
                (bytes[1] & 0xFF);
    }

    /**
     * 大端序
     * 端口转字节
     * @param port
     * @return
     */
    public static byte[] port2Int(int port) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((port >> 8) & 0xFF);
        bytes[1] = (byte) (port & 0xFF);
        return bytes;
    }

}
