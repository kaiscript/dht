package com.kaiscript.dht.crawler.task;

import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kaiscript on 2019/4/3.
 */
@Component
@Slf4j
public class FindNodeTask {

    @Autowired
    private Config config;

    @Autowired
    private DhtClient dhtClient;

    /**
     * 待发送find_node的节点
     */
    private BlockingQueue<Node> queue = new LinkedBlockingQueue<>();

    public void putNode(Node node) {
        queue.offer(node);
    }

    public void start() {

        List<String> nodeIds = config.getApp().getNodeIds();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true){
                    try {
                        for (int j = 0; j < nodeIds.size(); j++) {
                            Node node = queue.take();
                            if (node == null) {
                                continue;
                            }
                            InetSocketAddress address = new InetSocketAddress(node.getIp(), node.getPort());
                            dhtClient.findNode(address, nodeIds.get(j), j);
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        log.error("findNode e:", e);
                    }
                }

            }).start();

        }

    }

}
