package com.kaiscript.dht.crawler.socket.handler;

import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.domain.FindNode;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.route.RouteTable;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.task.FindNodeTask;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by chenkai on 2019/4/3.
 */
@Component
@Slf4j
public class FindNodeReqHandler implements MsgHandler{

    @Autowired
    private Config config;
    @Autowired
    private RouteTable routeTable;
    @Autowired
    private DhtClient dhtClient;
    @Autowired
    private Bencode bencode;
    @Autowired
    private FindNodeTask findNodeTask;

    @Override
    public void handle(Message message) {
        List<Node> nodeList = routeTable.get8Node();
        List<String> targetNodeIds = config.getApp().getNodeIds();
        byte[] nodeBytes = Node.toBytes(nodeList);
        FindNode.Response response = new FindNode.Response(targetNodeIds.get(message.getIndex() % targetNodeIds.size()), new String(nodeBytes, CharsetUtil.ISO_8859_1));
        //回复其他节点的findNode
        dhtClient.writeAndFlush(message.getSrcAddress(), bencode.encodeToBytes(DhtUtil.beanToMap(response)), message.getIndex());
        log.info("FindNodeReq ip:{}.port:{}", message.getSrcAddress().getHostName(), message.getSrcAddress().getPort());

        Node node = new Node();
        node.setIp(message.getSrcAddress().getHostString());
        node.setPort(message.getSrcAddress().getPort());
        findNodeTask.putNode(node);

    }

    @Override
    public boolean isExec(Message message) {
        return message.getQuery() == QueryEnum.FIND_NODE;
    }


}
