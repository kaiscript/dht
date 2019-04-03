package com.kaiscript.dht.crawler.config;

import com.google.common.collect.Lists;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.socket.handler.MsgHandlerManager;
import com.kaiscript.dht.crawler.socket.server.DhtServer;
import com.kaiscript.dht.crawler.util.Bencode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by chenkai on 2019/4/3.
 */
@Configuration
public class BeanConfig {

    @Bean
    public List<DhtServer.DhtServerHandler> dhtServerHandlers(Config config, Bencode bencode, DhtClient dhtClient, MsgHandlerManager msgHandlerManager) {
        List<DhtServer.DhtServerHandler> ret = Lists.newArrayList();
        List<Integer> ports = config.getApp().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            ret.add(new DhtServer.DhtServerHandler(i, bencode, dhtClient, msgHandlerManager));
        }
        return ret;
    }

}
