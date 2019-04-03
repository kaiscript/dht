package com.kaiscript.dht.crawler.task;

import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

/**
 * Created by chenkai on 2019/4/3.
 */
@Component
public class InitFindNodeTask {

    RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('1', 'z').
            filteredBy(LETTERS, DIGITS).build();

    @Autowired
    private Config config;

    @Autowired
    private DhtClient dhtClient;

    public void start() {

        List<String> initAddresses = config.getApp().getInitAddresses();
        List<Integer> ports = config.getApp().getPorts();
        List<String> nodeIds = config.getApp().getNodeIds();

        for (int i = 0; i < Math.min(ports.size(), nodeIds.size()); i++) {
            String s = initAddresses.get(i / initAddresses.size());
            String[] split = s.split(":");
            InetSocketAddress address = new InetSocketAddress(split[0], NumberUtils.toInt(split[1]));
            dhtClient.findNode(address, nodeIds.get(i), i);
        }

    }


}
