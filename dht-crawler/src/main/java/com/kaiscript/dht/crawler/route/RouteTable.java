package com.kaiscript.dht.crawler.route;

import com.google.common.collect.Lists;
import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.domain.Node;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地路由表
 * Created by chenkai on 2019/4/3.
 */
@Component
public class RouteTable {

    @Autowired
    private Config config;

    LRUCache<String, Node> cache = new LRUCache<>(16);

    @PostConstruct
    public void init() {
        cache = new LRUCache<>(16);
        List<String> nodeIds = config.getApp().getNodeIds();
        List<String> initAddresses = config.getApp().getInitAddresses();
        for (int i = 0; i < initAddresses.size(); i++) {
            String address = initAddresses.get(i);
            String[] split = address.split(":");
            Node node = new Node(nodeIds.get(i), split[0], NumberUtils.toInt(split[1]));
            put(node);
        }
    }

    public void put(Node node) {
        synchronized (cache){
            cache.put(node.getNodeId(), node);
        }
    }

    public synchronized List<Node> get8Node() {
        List<Node> ret = Lists.newArrayList();
        synchronized (cache){
            int limit = 0;
            for (Map.Entry<String, Node> entry : cache.entrySet()) {
                ret.add(entry.getValue());
                if (++limit >= 8) {
                    break;
                }
            }
        }
        return ret;
    }

    public class LRUCache<K,V> extends LinkedHashMap<K,V>{

        private int cacheSize;

        public LRUCache(int cacheSize) {
            super(cacheSize, 0.75f, true);
            this.cacheSize = cacheSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > cacheSize;
        }

    }

}
