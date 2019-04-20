package com.kaiscript.dht.crawler.socket.handler;

import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.domain.FetchMetadata;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.route.RouteTable;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.task.FetchMetadataTask;
import com.kaiscript.dht.crawler.task.FindNodeTask;
import com.kaiscript.dht.crawler.util.DhtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * Created by kaiscript on 2019/4/14.
 */
@Component
@Slf4j
public class GetPeersRequestHandler implements MsgHandler {

    @Autowired
    private RouteTable routeTable;

    @Autowired
    private Config config;
    @Autowired
    private DhtClient dhtClient;
    @Autowired
    private FetchMetadataTask fetchMetadataTask;
    @Autowired
    private FindNodeTask findNodeTask;

    @Override
    public void handle(Message message) {
        String token = config.getApp().getToken();
        Map<String, Object> map = message.getData();
        InetSocketAddress srcAddress = message.getSrcAddress();
        Map<String, Object> aMap = DhtUtil.getMap(map, "a");
        String infoHash = DhtUtil.getString(aMap, "info_hash");

        List<Node> nodeList = routeTable.get8Node();
        //回复getPeers
        if (StringUtils.isNotBlank(message.getTId())) {
            dhtClient.getPeerResponse(srcAddress, message.getTId(), DhtUtil.generateNodeIdStr(), token, nodeList, message.getIndex());
        }
        log.info("getPeerResponse address:{}", srcAddress);
        if (StringUtils.isNoneBlank(infoHash)) {
            fetchMetadataTask.offer(new FetchMetadata(srcAddress.getHostString(), srcAddress.getPort(), infoHash));
        }

        Node node = new Node();
        node.setIp(srcAddress.getHostString());
        node.setPort(srcAddress.getPort());
        findNodeTask.putNode(node);

    }

    @Override
    public boolean isExec(Message message) {
        return message.getQuery() == QueryEnum.GET_PEERS;
    }

}
