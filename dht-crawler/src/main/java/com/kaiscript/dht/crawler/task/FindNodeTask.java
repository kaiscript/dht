package com.kaiscript.dht.crawler.task;

import com.google.common.collect.Sets;
import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

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

    private Set<String> set = Sets.newHashSet();

    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    @PostConstruct
    public void staticsIpSize() {
        service.scheduleAtFixedRate(() -> log.info("findNode setSize:{}.queue size:{}", set.size(), queue.size()), 1, 5, TimeUnit.SECONDS);
    }

    /**
     * 待发送find_node的节点
     */
    private BlockingQueue<Node> queue = new LinkedBlockingQueue<>();

    public void putNode(Node node) {
        if (queue.size() >= 100000) {
            return;
        }
        queue.offer(node);
        set.add(node.getIp());
    }

    public void start() {

        List<String> nodeIds = config.getApp().getNodeIds();
        List<Integer> ports = config.getApp().getPorts();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                while (true){
                    try {
                        for (int j = 0; j < ports.size(); j++) {
                            Node node = queue.take();
                            if (node == null) {
                                continue;
                            }
                            InetSocketAddress address = new InetSocketAddress(node.getIp(), node.getPort());
                            dhtClient.findNode(address, nodeIds.get(j % nodeIds.size()), j);
                        }
                        Thread.sleep(300);
                    } catch (Exception e) {
                        log.error("findNode e:", e);
                    }
                }

            }).start();

        }

    }

}
