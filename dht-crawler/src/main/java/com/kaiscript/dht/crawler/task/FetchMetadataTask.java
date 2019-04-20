package com.kaiscript.dht.crawler.task;

import com.kaiscript.dht.crawler.domain.FetchMetadata;
import com.kaiscript.dht.crawler.domain.Metadata;
import com.kaiscript.dht.crawler.service.MetadataService;
import com.kaiscript.dht.crawler.service.PeerInfohashService;
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
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by kaiscript on 2019/4/8.
 */
@Component
@Slf4j
public class FetchMetadataTask {

    private Bootstrap bootstrap;

    @PostConstruct
    public void fetchMetadataTask() {
        bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(4))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1, 102400, Integer.MAX_VALUE));

    }

    private Bootstrap bootstrap() {
        return bootstrap.clone();
    }

    @Autowired
    private MetadataService metadataService;
    @Autowired
    private PeerInfohashService peerInfohashService;

    private Queue<FetchMetadata> queue = new LinkedBlockingQueue<>();

    public void offer(FetchMetadata data) {
        queue.offer(data);
    }

    public void startFetch() {
        for (int i = 0; i < 10; i++) {
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

    @SneakyThrows
    private void fetchMetadata() {
        FetchMetadata fetchMetadata = queue.poll();
        if (fetchMetadata == null) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        log.info("fetchMetadata:{}", fetchMetadata);
        Ret ret = new Ret(countDownLatch);
        String infohashHex = fetchMetadata.getInfohash();
        byte[] infohashBytes = ByteUtil.hexStr2Bytes(infohashHex);
        InetSocketAddress address = new InetSocketAddress(fetchMetadata.getIp(), fetchMetadata.getPort());
        Bootstrap bootstrap = bootstrap();
        bootstrap.handler(new FetchMetadataChannelInitializer(ret))
                .connect(address)
                .addListener(new ConnectListener(infohashBytes, DhtUtil.generateNodeId()));
        countDownLatch.await(10, TimeUnit.SECONDS);

        if (ret.getRet() != null) {
            Metadata metadata = bytes2Metadata(ret.getRet(), fetchMetadata.getInfohash());
            peerInfohashService.removeFormInfohash(fetchMetadata.getInfohash());
            log.info("infohashHex:{},finalGetMetadata:{}", infohashHex, metadata);
        }
    }

    @AllArgsConstructor
    private class FetchMetadataChannelInitializer extends ChannelInitializer{

        private Ret ret;

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline()
                    .addLast(new ReadTimeoutHandler(60))
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
                return;
            }
            future.channel().close();
        }
    }

    /**
     * 获取metadata handler
     */
    @AllArgsConstructor
    private class FetchMetadataHandler extends SimpleChannelInboundHandler<ByteBuf>{

        private Ret ret;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            log.info("FetchMetadata receive.address:{}", ctx.channel().remoteAddress());
            Channel channel = ctx.channel();
            byte[] msgBytes = new byte[msg.readableBytes()];
            msg.readBytes(msgBytes);

            String messageStr = new String(msgBytes, CharsetUtil.ISO_8859_1);

            /**
             * @see MetadataService.HANDSHAKE_PRE_BYTES
             * 第一个字节是19时为握手消息,需要回复拓展消息
             */
            if (msgBytes[0] == (byte) 19) {
                metadataService.sendExtendHandshakeMsg(channel);
            }

            //收到握手拓展消息,有ut_metadata字段，则可以向对方请求metadata了
            if (messageStr.contains(MetadataService.UT_METADATA) && messageStr.contains(MetadataService.METADATA_SIZE)) {
                metadataService.sendRequestMetadata(messageStr, channel);
            }
            //收到的 Extension消息包含msg_type,则可能包含数据
            log.info("receive:{}", new String(msgBytes, CharsetUtil.UTF_8));
            if (messageStr.contains("msg_type")) {
                fetchMetadata(messageStr);
            }

        }

        /**
         * 获取二进制数据。
         * metadata数据
         * Example:
         {'msg_type': 1, 'piece': 0, 'total_size': 3425}
         d8:msg_typei1e5:piecei0e10:total_sizei34256eexxxxxxxx...
         The x represents binary data (the metadata).
         * @param msgStr
         * @return
         */
        public void fetchMetadata(String msgStr) {
            String resultStr = msgStr.substring(msgStr.indexOf("ee") + 2, msgStr.length());
            byte[] resultStrBytes = resultStr.getBytes(CharsetUtil.ISO_8859_1);
            if (ret.getRet() == null) {
                ret.setRet(resultStrBytes);
            } else {
                ret.setRet(ArrayUtils.addAll(ret.getRet(), resultStrBytes));
            }
            ret.getCountDownLatch().countDown();
            log.info("fetchMetadataFinalData resultStr:{}", new String(resultStrBytes, CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("connection error.e:{}", cause.getMessage());
            ctx.close();
        }
    }

    private Metadata bytes2Metadata(byte[] bytes,String infohash) {
        String str = new String(bytes, CharsetUtil.UTF_8);
        log.info("bytes2Metadata ret:{}", str);
        //种子文件的sha1 杂凑值前是 6:pieces
        Bencode bencode = new Bencode(CharsetUtil.UTF_8);
        String bencodedMetadata = str.substring(0, str.indexOf("6:pieces")) + "e";
        Map<String,Object> map = (Map<String, Object>) bencode.decode(bencodedMetadata.getBytes(CharsetUtil.UTF_8));
        return DhtUtil.convert(map, infohash);
    }

    @Data
    class Ret{

        private byte[] ret;
        private CountDownLatch countDownLatch;

        public Ret(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }
    }


}
