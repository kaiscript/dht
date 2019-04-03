package com.kaiscript.dht.crawler.socket.client;

import com.google.common.collect.Lists;
import com.kaiscript.dht.crawler.config.Config;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
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

}
