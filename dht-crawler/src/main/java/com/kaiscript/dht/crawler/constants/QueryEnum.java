package com.kaiscript.dht.crawler.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * y字段为q时，q的枚举值
 * Created by chenkai on 2019/4/2.
 */
@AllArgsConstructor
@Getter
public enum QueryEnum implements CommonEnum<String>{

    PING("ping","ping"),
    FIND_NODE("find_node","寻找节点"),
    GET_PEERS("get_peers","寻找peers"),
    ANNOUNCE_PEER("announce_peer","接受peer消息");

    private String type;
    private String desc;


}
