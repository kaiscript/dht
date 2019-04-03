package com.kaiscript.dht.crawler.socket.handler;

import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.domain.FindNode;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.route.RouteTable;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by chenkai on 2019/4/3.
 */
@Component
public class FindNodeReqHandler implements MsgHandler{

    @Autowired
    private Config config;
    @Autowired
    private RouteTable routeTable;
    @Autowired
    private DhtClient dhtClient;
    @Autowired
    private Bencode bencode;

    @Override
    public void handle(Message message) {
        List<Node> nodeList = routeTable.get8Node();
        List<String> targetNodeIds = config.getApp().getNodeIds();
        byte[] nodeBytes = Node.toBytes(nodeList);
        FindNode.Response response = new FindNode.Response(targetNodeIds.get(message.getIndex()), new String(nodeBytes, CharsetUtil.ISO_8859_1));
        //回复其他节点的findNode
        dhtClient.writeAndFlush(message.getSrcAddress(), bencode.encodeToBytes(DhtUtil.beanToMap(response)), message.getIndex());

    }

    @Override
    public boolean isExec(Message message) {
        return message.getQuery() == QueryEnum.FIND_NODE;
    }


}
