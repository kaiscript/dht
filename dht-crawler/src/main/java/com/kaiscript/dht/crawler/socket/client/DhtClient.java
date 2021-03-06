package com.kaiscript.dht.crawler.socket.client;

import com.google.common.collect.Lists;
import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.domain.FindNode;
import com.kaiscript.dht.crawler.domain.GetPeers;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by chenkai on 2019/4/2.
 */
@Component
public class DhtClient {

    private static final Logger logger = LoggerFactory.getLogger(DhtClient.class);

    /**
     * 对方也是往发送方的端口回复消息，故共用服务端channel
     */
    private List<Channel> channels = Lists.newArrayList();

    @Autowired
    private Bencode bencode;

    @Autowired
    public void init(Config config) {
        logger.info("init DhtClient config...");
        List<Integer> ports = config.getApp().getPorts();
        ports.forEach(integer -> channels.add(null));
    }

    public void writeAndFlush(InetSocketAddress address, byte[] bytes, int index) {
        channels.get(index).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), address));
    }

    public void setChannel(int index, Channel channel) {
        channels.set(index, channel);
    }

    /**
     *
     * @param address 发送地址
     * @param nodeId 目标node
     * @param index channel索引
     */
    public void findNode(InetSocketAddress address, String nodeId, int index) {
        FindNode.Request request = new FindNode.Request(nodeId, DhtUtil.generateNodeIdStr());
        writeAndFlush(address, bencode.encodeToBytes(DhtUtil.beanToMap(request)), index);
    }

    public void getPeerResponse(InetSocketAddress address, String tId, String nodeId, String token, List<Node> nodes, int index) {
        GetPeers.Response response = new GetPeers.Response(nodeId, token, new String(Node.toBytes(nodes), CharsetUtil.ISO_8859_1), tId);
        writeAndFlush(address, bencode.encodeToBytes(DhtUtil.beanToMap(response)), index);
    }

}
