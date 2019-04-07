package com.kaiscript.dht.crawler.socket.handler;

import com.kaiscript.dht.crawler.constants.YEnum;
import com.kaiscript.dht.crawler.domain.AnnouncePeer;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.task.FindNodeTask;
import com.kaiscript.dht.crawler.util.DhtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
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
    private FindNodeTask findNodeTask;

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
//            log.info("infohash null.map:{}", aMap);
            return;
        }
        AnnouncePeer.RequestContent requestContent = new AnnouncePeer.RequestContent(aMap, srcAddress.getPort());
        String infoHash = requestContent.getInfo_hash();
        log.info("announcePeer infoHash: magnet:?xt=urn:btih:{}", infoHash);

        //加入寻找节点任务中
        Node findNode = new Node();
        findNode.setIp(srcAddress.getHostName());
        findNode.setPort(requestContent.getPort());
        findNodeTask.putNode(findNode);
    }

    @Override
    public boolean isExec(Message message) {
        return message.getY() == YEnum.QUERY;
    }

}
