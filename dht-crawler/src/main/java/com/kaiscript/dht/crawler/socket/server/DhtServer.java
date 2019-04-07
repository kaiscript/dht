package com.kaiscript.dht.crawler.socket.server;

import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.socket.handler.MsgHandlerManager;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by chenkai on 2019/4/2.
 */
@Component
public class DhtServer {

    @Autowired
    private Config config;
    @Autowired
    private Bencode bencode;
    @Autowired
    private DhtClient dhtClient;
    @Autowired
    private MsgHandlerManager msgHandlerManager;
    @Autowired
    private List<DhtServerHandler> dhtServerHandlers;

    private static final Logger logger = LoggerFactory.getLogger(DhtServer.class);

    @SneakyThrows
    public void start(){
        List<Integer> ports = config.getApp().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Integer port = ports.get(i);
            int index = i;
            new Thread(() -> run(index, port)).start();
        }
        Thread.sleep(5000);
    }

    private void run(int index, int port) {
        logger.info("DhtServer run at port:{}", port);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(dhtServerHandlers.get(index));

            bootstrap.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            logger.error("run server error.port:{},e", port, e);
        }
    }

    @ChannelHandler.Sharable
    public static class DhtServerHandler extends SimpleChannelInboundHandler<DatagramPacket>{

        /**
         * channel索引
         */
        private int index;

        private Bencode bencode;

        private DhtClient dhtClient;

        private MsgHandlerManager msgHandlerManager;

        public DhtServerHandler(int index, Bencode bencode, DhtClient dhtClient, MsgHandlerManager msgHandlerManager) {
            this.index = index;
            this.bencode = bencode;
            this.dhtClient = dhtClient;
            this.msgHandlerManager = msgHandlerManager;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
            byte[] bytes = new byte[datagramPacket.content().readableBytes()];
            datagramPacket.content().readBytes(bytes);

            Map<String,Object> decode = (Map<String, Object>) bencode.decode(bytes);
            Optional<Message> messageOptional = DhtUtil.formatData(decode);
            if (!messageOptional.isPresent()) {
                return;
            }
            Message message = messageOptional.get();
            message.setIndex(index);
            message.setSrcAddress(datagramPacket.sender());

            msgHandlerManager.exec(message);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            dhtClient.setChannel(index, channel);
        }

    }

}
