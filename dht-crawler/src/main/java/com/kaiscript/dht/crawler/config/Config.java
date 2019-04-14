package com.kaiscript.dht.crawler.config;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by chenkai on 2019/4/3.
 */
@Component
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "dht")
public class Config {

    public App app = new App();

    @Data
    public static class App {

        private List<String> nodeIds;

        private List<Integer> ports;

        private List<String> initAddresses = Lists.newArrayList();

        private String token;
    }

}
