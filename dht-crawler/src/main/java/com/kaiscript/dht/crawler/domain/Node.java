package com.kaiscript.dht.crawler.domain;

import com.kaiscript.dht.crawler.constants.Constants;
import com.kaiscript.dht.crawler.exception.DhtException;
import com.kaiscript.dht.crawler.util.ByteUtil;
import io.netty.util.CharsetUtil;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * Created by kaiscript on 2019/4/2.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Node {

    private String nodeId;

    private String ip;

    private int port;

    /**
     * 解析node bytes数组
     * @param bytes
     * @return
     */
    public static Node buildNode(byte[] bytes) {
        if (bytes.length < Constants.NODE_LENGTH) {
            throw new DhtException("node bytes length less than " + Constants.NODE_LENGTH);
        }
        byte[] nodeIdBytes = ArrayUtils.subarray(bytes, 0, 20);
        byte[] ipBytes = ArrayUtils.subarray(bytes, 20, 24);
        byte[] portBytes = ArrayUtils.subarray(bytes, 24, 26);
        String ip = ByteUtil.bytes2Ip(ipBytes);
        int port = ByteUtil.bytes2Int(portBytes);
        return new Node(new String(nodeIdBytes, CharsetUtil.ISO_8859_1), ip, port);
    }

    /**
     * node列表转字节
     * @param nodes
     * @return
     */
    public static byte[] toBytes(List<Node> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new byte[0];
        }
        int bytesLength = nodes.size() * Constants.NODE_LENGTH;
        byte[] newBytes = new byte[bytesLength];

        for (int i = 0; i + Constants.NODE_LENGTH < bytesLength; i += Constants.NODE_LENGTH) {
            int index = i / Constants.NODE_LENGTH;
            Node node = nodes.get(index);
            byte[] nodeBytes = toBytes(node);
            System.arraycopy(nodeBytes, 0, newBytes, i, Constants.NODE_LENGTH);
        }
        return newBytes;
    }


    public static byte[] toBytes(Node node) {
        byte[] nodeBytes = new byte[Constants.NODE_LENGTH];
        byte[] nodeIdBytes = node.getNodeId().getBytes(CharsetUtil.ISO_8859_1);
        System.arraycopy(nodeIdBytes, 0, nodeBytes, 0, 20);
        String[] ipArray = node.getIp().split(".");

        //转换ip字节
        byte[] ipBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            ipBytes[i] = (byte) Integer.parseInt(ipArray[i]);
        }
        System.arraycopy(ipBytes, 0, nodeBytes, 20, 4);
        //转换端口字节
        byte[] portBytes = ByteUtil.int2Bytes(node.getPort());
        System.arraycopy(portBytes, 0, nodeBytes, 24, 2);

        return nodeBytes;
    }

}
