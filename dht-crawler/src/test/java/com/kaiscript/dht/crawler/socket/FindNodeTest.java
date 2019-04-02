package com.kaiscript.dht.crawler.socket;

import com.kaiscript.dht.crawler.domain.FindNode;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * Created by chenkai on 2019/4/2.
 */
public class FindNodeTest {

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    static class Client{

        public void start() {
            try {
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup);
                bootstrap.channel(NioDatagramChannel.class); // dht协议基于 UDP
                bootstrap.option(ChannelOption.SO_BROADCAST, true);
                bootstrap.handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
                Channel channel = bootstrap.bind(12875).sync().channel();
                workerGroup.shutdownGracefully();
                channel.closeFuture().await(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static class ClientHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            FindNode.Request request = new FindNode.Request(DhtUtil.generateNodeIdStr(), "mnopqrstuvwxyz123456");
            Channel channel = ctx.channel();
            Bencode bencode = new Bencode();
            byte[] bytes = bencode.encodeToBytes(DhtUtil.beanToMap(request));
            InetSocketAddress address = new InetSocketAddress("router.utorrent.com", 6881);
            DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(bytes), address);
            channel.writeAndFlush(datagramPacket);
            System.out.println(">>>>>>>>>>>>>");
        }

    }

}
