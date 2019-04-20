package com.kaiscript.dht.crawler.socket.handler;

import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.constants.YEnum;
import com.kaiscript.dht.crawler.domain.AnnouncePeer;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.service.PeerInfohashService;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.task.FindNodeTask;
import com.kaiscript.dht.crawler.task.ParserTask;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.DhtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by kaiscript on 2019/4/5.
 */
@Component
@Slf4j
public class AnnouncePeerReqHandler implements MsgHandler {

    @Autowired
    private Bencode bencode;

    @Autowired
    private FindNodeTask findNodeTask;

    @Autowired
    private DhtClient dhtClient;

    @Autowired
    private PeerInfohashService peerInfohashService;
    @Autowired
    private ParserTask parserTask;

    @Override
    public void handle(Message message) {
        Map<String, Object> map = message.getData();
        Map<String, Object> aMap = DhtUtil.getMap(map, "a");
        if (MapUtils.isEmpty(aMap)) {
            return;
        }
        InetSocketAddress srcAddress = message.getSrcAddress();
        Object info_hash = aMap.get("info_hash");
        if (info_hash == null) {
            return;
        }
        //回复
        if (StringUtils.isNotBlank(message.getTId())) {
            AnnouncePeer.Response response = new AnnouncePeer.Response(message.getTId());
            try {
                dhtClient.writeAndFlush(srcAddress,bencode.encodeToBytes(DhtUtil.beanToMap(response)),message.getIndex());
            } catch (Exception e) {
                log.error("AnnouncePeerReqHandler response e,resp:{}", response);
            }
        }


        AnnouncePeer.RequestContent requestContent = new AnnouncePeer.RequestContent(aMap, srcAddress.getPort());
        String infoHash = requestContent.getInfo_hash();
        log.info("announcePeer infoHash: magnet:?xt=urn:btih:{}.address:{}", infoHash, srcAddress);
        //保存临时infohash
        peerInfohashService.savePeerInfohash(srcAddress.getHostName(), requestContent.getPort(), infoHash);
        parserTask.offer(infoHash);
        //加入寻找节点任务中
        Node findNode = new Node();
        findNode.setIp(srcAddress.getHostName());
        findNode.setPort(requestContent.getPort());
        findNodeTask.putNode(findNode);

    }

    @Override
    public boolean isExec(Message message) {
        return message.getY() == YEnum.QUERY && message.getQuery() == QueryEnum.ANNOUNCE_PEER;
    }

}
