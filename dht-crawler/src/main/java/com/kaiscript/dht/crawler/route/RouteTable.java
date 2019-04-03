package com.kaiscript.dht.crawler.route;

import com.kaiscript.dht.crawler.domain.Node;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 本地路由表
 * Created by chenkai on 2019/4/3.
 */
@Component
public class RouteTable {

    LRUCache<String, Node> cache = new LRUCache<>(16);

    public void put(Node node) {
        cache.put(node.getNodeId(), node);
    }

    public List<Node> get8Node() {
        return cache.values().stream().limit(8).collect(Collectors.toList());
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
