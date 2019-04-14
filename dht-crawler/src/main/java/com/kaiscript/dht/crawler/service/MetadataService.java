package com.kaiscript.dht.crawler.service;

import com.google.common.collect.Maps;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.ByteUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by kaiscript on 2019/4/7.
 */
@Service
@Slf4j
public class MetadataService {

    @Autowired
    private Bencode bencode;

    /**
     * Handshake 握手消息
     * handshake:
     * <pstrlen><pstr><reserved><info_hash><peer_id>
     */
    /**
     * In version 1.0 of the BitTorrent protocol, pstrlen = 19, and pstr = "BitTorrent protocol".
     * 第1个字节时19，第2-20个字节代表 BitTorrent protocol,第21-28个字节为保留字节，代表版本号，可以全为0
     * 共28个字节
     */
    public static final byte[] HANDSHAKE_PRE_BYTES = {19, 66, 105, 116, 84, 111, 114, 114, 101, 110, 116, 32, 112, 114,
            111, 116, 111, 99, 111, 108, 0, 0, 0, 0, 0, 16, 0, 1};

    public static byte BT_MSG_ID = 20;

    public static byte EXT_HANDSHAKE_ID = 0;

    //metadata.每一片 16KB
    public static final long METADATA_PIECE_SIZE = 16 << 10;

    public static final String UT_METADATA = "ut_metadata";

    public static final String METADATA_SIZE = "metadata_size";

    /**
     * Peer Wire 握手消息
     * @param infoHash
     * @param selfHash
     */
    public void handshake(byte[] infoHash,byte[] selfHash,Channel channel) {
        byte[] handshakeBytes = new byte[68];
        System.arraycopy(HANDSHAKE_PRE_BYTES, 0, handshakeBytes, 0, 28);
        //info_hash: 20-byte SHA1 hash
        System.arraycopy(infoHash, 0, handshakeBytes, 28, 20);
        //peer_id: 20-byte string used as a unique ID for the client
        System.arraycopy(selfHash, 0, handshakeBytes, 48, 20);
        channel.writeAndFlush(Unpooled.copiedBuffer(handshakeBytes));
        log.info("handshake success.");
    }

    /**
     * 拓展握手消息
     * BEP-010 : http://bittorrent.org/beps/bep_0010.html
     * 拓展消息结构
     * 4字节(uint32_t)长度，1字节(uint8_t)固定标志位，1字节(uint8_t)拓展消息标志位，N字节m字典消息体
     */
    public void sendExtendHandshakeMsg(Channel channel) {
        try {
            Map<String,Object> extendMsgMap = Maps.newLinkedHashMap();
            Map<String, Object> mDictMap = Maps.newLinkedHashMap();
            mDictMap.put("ut_metadata", 1);
            extendMsgMap.put("m", mDictMap);
            byte[] extendBytes = bencode.encodeToBytes(extendMsgMap);
            byte[] allExtendBytes = new byte[extendBytes.length + 6];
            byte[] lengthBytes = ByteUtil.int2FourBytes(extendBytes.length + 2);
            System.arraycopy(lengthBytes, 0, allExtendBytes, 0, 4);
            allExtendBytes[4] = BT_MSG_ID;
            allExtendBytes[5] = EXT_HANDSHAKE_ID;
            System.arraycopy(extendBytes, 0, allExtendBytes, 6, extendBytes.length);
            channel.writeAndFlush(Unpooled.copiedBuffer(allExtendBytes));
        } catch (Exception e) {
            log.error("sendExtendHandshakeMsg e:", e);
        }
        log.info("sendExtendHandshakeMsg");
    }

    /**
     * 拓展消息结构:
     * {
     * e: 0,
     * ipv4: xxx,
     * ipv6: xxx,
     * complete_ago: 1,
     * m:
     * {
     * upload_only: 3,
     * lt_donthave: 7,
     * ut_holepunch: 4,
     * ut_metadata: 2,
     * ut_pex: 1,
     * ut_comment: 6
     * },
     * matadata_size: 45377,
     * p: 33733,
     * reqq: 255,
     * v: BitTorrent 7.9.3
     * yp: 19616,
     * yourip: xxx
     * }
     * <p>
     * 请求metadata 的消息结构：
     * {'msg_type': 0, 'piece': 0}
     */
    public void sendRequestMetadata(String msgStr, Channel channel) {
        int utMetadataValueIndex = msgStr.indexOf(UT_METADATA) + UT_METADATA.length() + 1;
        //ut_metadata 的值为integer.故取 i 后面的值,一个字节
        int utMetadataValue = Integer.parseInt(msgStr.substring(utMetadataValueIndex, utMetadataValueIndex + 1));
        int metadataSizeIndex = msgStr.indexOf(METADATA_SIZE) + METADATA_SIZE.length() + 1;
        String otherStr = msgStr.substring(metadataSizeIndex);
        //matadata_size 为integer,取 i跟e之间的值
        int metadataSize = Integer.parseInt(otherStr.substring(0, otherStr.indexOf("e")));
        //多少块
        int pieceNum = (int) Math.ceil(metadataSize / METADATA_PIECE_SIZE);
        for (int i = 0; i < pieceNum; i++) {
            Map<String, Object> requestMetadataMap = Maps.newLinkedHashMap();
            requestMetadataMap.put("msg_type", 0);
            requestMetadataMap.put("piece", 0);
            byte[] metadataMapBytes = bencode.encodeToBytes(requestMetadataMap);
            byte[] allMetadataMapBytes = new byte[metadataMapBytes.length + 6];
            byte[] lengthBytes = ByteUtil.int2TwoBytes(metadataMapBytes.length + 2);
            System.arraycopy(lengthBytes, 0, allMetadataMapBytes, 0, 4);
            allMetadataMapBytes[4] = BT_MSG_ID;
            allMetadataMapBytes[5] = (byte) utMetadataValue; // >0 = extended message as specified by the handshake.
            System.arraycopy(metadataMapBytes, 0, allMetadataMapBytes, 6, metadataMapBytes.length);
            channel.writeAndFlush(Unpooled.copiedBuffer(allMetadataMapBytes));
        }
        log.info("sendRequestMetadata msgStr:{},pieceNum:{}", msgStr, pieceNum);
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
    public byte[] fetchMetadata(String msgStr) {
        String resultStr = msgStr.substring(msgStr.indexOf("ee") + 2, msgStr.length());
        log.info("fetchMetadataFinalData");
        return resultStr.getBytes(CharsetUtil.ISO_8859_1);
    }

}
