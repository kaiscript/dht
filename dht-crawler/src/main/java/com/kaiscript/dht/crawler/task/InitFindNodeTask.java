package com.kaiscript.dht.crawler.task;

import com.kaiscript.dht.crawler.config.Config;
import com.kaiscript.dht.crawler.domain.FindNode;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.DhtUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
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
    private Bencode bencode;

    @Autowired
    private DhtClient dhtClient;

    public void start() {

        List<String> initAddresses = config.getApp().getInitAddresses();
        String nodeId = config.getApp().getNodeId();

        initAddresses.forEach(s -> {
            FindNode.Request request = new FindNode.Request(DhtUtil.generateNodeIdStr(), nodeId);
            String[] split = s.split(":");
            InetSocketAddress address = new InetSocketAddress(split[0], NumberUtils.toInt(split[1]));
            dhtClient.writeAndFlush(address, bencode.encodeToBytes(DhtUtil.beanToMap(request)));
        });

    }


}
