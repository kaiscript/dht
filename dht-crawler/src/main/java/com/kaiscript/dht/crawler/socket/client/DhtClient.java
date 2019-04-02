package com.kaiscript.dht.crawler.socket.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Created by chenkai on 2019/4/2.
 */
@Component
public class DhtClient {

    /**
     * 对方也是往发送方的端口回复消息，故共用服务端channel
     */
    private Channel channel;

    public void writeAndFlush(InetSocketAddress address, byte[] bytes) {
        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), address));
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
