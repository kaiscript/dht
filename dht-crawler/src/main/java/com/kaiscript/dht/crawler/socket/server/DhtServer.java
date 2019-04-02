package com.kaiscript.dht.crawler.socket.server;

import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.util.Bencode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by chenkai on 2019/4/2.
 */
@Component
public class DhtServer {

    @Autowired
    private Bencode bencode;
    @Autowired
    private DhtClient dhtClient;

    private static final Logger logger = LoggerFactory.getLogger(DhtServer.class);

    public void start(){
        run(12875);
    }

    private void run(int port) {
        logger.info("DhtServer run port:{}", port);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new DhtServerHandler());

            bootstrap.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            logger.error("run server error.port:{},ERROR", port, e);
        }
    }

    @ChannelHandler.Sharable
    public class DhtServerHandler extends SimpleChannelInboundHandler<DatagramPacket>{

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
            byte[] bytes = new byte[datagramPacket.content().readableBytes()];
            datagramPacket.content().readBytes(bytes);

            Map<String,Object> decode = (Map<String, Object>) bencode.decode(bytes);
            logger.info("channelRead0 map:{}", decode);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            dhtClient.setChannel(channel);
        }

    }

}
