package com.kaiscript.dht.crawler.constants;

/**
 * Created by kaiscript on 2019/4/2.
 */
public class Constants {

    /**
     * nodeId.20个字节
     * BEP-3
     */
    public static int NODE_ID_LENGTH = 20;

    /**
     * ip 长度.4个字节
     */
    public static int IP_LENGTH = 4;

    /**
     * 端口长度.2个字节
     */
    public static int PORT_LENGTH = 2;

    public static int NODE_LENGTH = NODE_ID_LENGTH + IP_LENGTH + PORT_LENGTH;

}
