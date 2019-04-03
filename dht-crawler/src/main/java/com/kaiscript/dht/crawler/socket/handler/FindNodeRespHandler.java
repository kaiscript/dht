package com.kaiscript.dht.crawler.socket.handler;

import com.kaiscript.dht.crawler.constants.YEnum;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.route.RouteTable;
import com.kaiscript.dht.crawler.task.FindNodeTask;
import com.kaiscript.dht.crawler.util.DhtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by kaiscript on 2019/4/2.
 */
@Component
public class FindNodeRespHandler implements MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(FindNodeRespHandler.class);

    @Autowired
    private RouteTable routeTable;
    @Autowired
    private FindNodeTask findNodeTask;

    @Override
    public void handle(Message message) {
        Map<String, Object> data = message.getData();
        Map<String, Object> rMap = DhtUtil.getMap(data, "r");
        List<Node> nodeList = DhtUtil.getNodeListByMap(rMap);
        nodeList.forEach(node -> {
            //加入find_node任务
            findNodeTask.putNode(node);
            //加入本地路由表
            routeTable.put(node);
        });
    }

    @Override
    public boolean isExec(Message message) {
        return message.getY() == YEnum.RESPONSE;
    }

}
