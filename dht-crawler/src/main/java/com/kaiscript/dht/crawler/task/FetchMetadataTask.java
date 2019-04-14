package com.kaiscript.dht.crawler.task;

import com.kaiscript.dht.crawler.domain.FetchMetadata;
import com.kaiscript.dht.crawler.domain.Metadata;
import com.kaiscript.dht.crawler.service.MetadataService;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.ByteUtil;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kaiscript on 2019/4/8.
 */
@Component
@Slf4j
public class FetchMetadataTask {

    private Bootstrap bootstrap;

    @Autowired
    private Bencode bencode;

    @PostConstruct
    public void fetchMetadataTask() {
        bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(4))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1, 102400, Integer.MAX_VALUE));

    }

    private Bootstrap bootstap() {
        return bootstrap.clone();
    }

    @Autowired
    private MetadataService metadataService;

    private Queue<FetchMetadata> queue = new LinkedBlockingQueue<>();

    public void offer(FetchMetadata data) {
        queue.offer(data);
    }

    public void startFetch() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                while (true) {
                    fetchMetadata();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void fetchMetadata() {
        FetchMetadata fetchMetadata = queue.poll();
        if (fetchMetadata == null) {
            return;
        }
        log.info("fetchMetadata:{}", fetchMetadata);
        byte[] ret = null;
        String infohashHex = fetchMetadata.getInfohash();
        byte[] infohashBytes = ByteUtil.hexStr2Bytes(infohashHex);
        InetSocketAddress address = new InetSocketAddress(fetchMetadata.getIp(), fetchMetadata.getPort());
        Bootstrap bootstap = bootstap();
        bootstap.handler(new FetchMetadatChannelInitializer(ret))
                .connect(address)
                .addListener(new ConnectListener(infohashBytes, DhtUtil.generateNodeId()));
        if (ret != null) {
            Metadata metadata = bytes2Metadata(ret, fetchMetadata.getInfohash());
            log.info("metadata:{}", metadata);
        }
    }

    @AllArgsConstructor
    private class FetchMetadatChannelInitializer extends ChannelInitializer{

        private byte[] ret;

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline()
                    .addLast(new ReadTimeoutHandler(10))
                    .addLast(new FetchMetadataHandler(ret));
        }

    }

    /**
     * 连接监听器
     */
    @AllArgsConstructor
    private class ConnectListener implements ChannelFutureListener {
        //要握手的info_hash
        private byte[] infohash;
        //本机 info_hash
        private byte[] selfHash;

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                metadataService.handshake(infohash, selfHash, future.channel());
            }
        }
    }

    /**
     * 获取metadata handler
     */
    @AllArgsConstructor
    private class FetchMetadataHandler extends SimpleChannelInboundHandler<ByteBuf>{

        private byte[] ret;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            Channel channel = ctx.channel();
            byte[] msgBytes = new byte[msg.readableBytes()];
            msg.readBytes(msgBytes);

            String messageStr = new String(msgBytes, CharsetUtil.ISO_8859_1);

            /**
             * @see MetadataService.HANDSHAKE_PRE_BYTES
             * 第一个字节是19时为握手消息,需要回复拓展消息
             */
            if (msgBytes[0] == (byte) 19) {
                log.info("sendExtendHandshakeMsg pre");
                metadataService.sendExtendHandshakeMsg(channel);
            }

            //收到握手拓展消息,有ut_metadata字段，则可以向对方请求metadata了
            if (messageStr.contains(MetadataService.UT_METADATA) && messageStr.contains(MetadataService.METADATA_SIZE)) {
                metadataService.sendRequestMetadata(messageStr, channel);
            }
            //收到的 Extension消息包含msg_type,则可能包含数据
            if (messageStr.contains("msg_type")) {
                ret = metadataService.fetchMetadata(messageStr);
            }

        }
    }

    private Metadata bytes2Metadata(byte[] bytes,String infohash) {
        String str = new String(bytes, CharsetUtil.UTF_8);
        //种子文件的sha1 杂凑值前是 6:pieces
        String bencodedMetadata = str.substring(0, str.indexOf("6:pieces")) + "e";
        Map<String,Object> map = (Map<String, Object>) bencode.decode(bencodedMetadata.getBytes(CharsetUtil.UTF_8));
        return DhtUtil.convert(map, infohash);
    }

}
