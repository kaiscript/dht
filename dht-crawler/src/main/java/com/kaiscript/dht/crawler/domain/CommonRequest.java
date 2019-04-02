package com.kaiscript.dht.crawler.domain;

/**
 * Created by chenkai on 2019/4/2.
 */
public class CommonRequest extends CommonParam{

    /**
     * 如果 y关键字为 QUERY，则消息体有q字段<br/>
     * get_peers/announce_peer/ping/find_node
     */
    public String q;

}
