package com.kaiscript.dht.crawler.domain;

import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.constants.YEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by chenkai on 2019/4/2.
 */
@Getter
@Setter
@ToString
public class Message {

    private YEnum y;

    private QueryEnum query;

    private Map<String, Object> data;

    /**
     * t字段
     */
    private String tId;

    private InetSocketAddress srcAddress;

}
