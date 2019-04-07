package com.kaiscript.dht.crawler.constants;

/**
 * @link https://wiki.theory.org/index.php/BitTorrentSpecification#Peer_wire_protocol_.28TCP.29
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

    /**
     * Node节点总字节数
     */
    public static int NODE_LENGTH = NODE_ID_LENGTH + IP_LENGTH + PORT_LENGTH;


}
