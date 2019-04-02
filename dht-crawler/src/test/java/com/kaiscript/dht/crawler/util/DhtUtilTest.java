package com.kaiscript.dht.crawler.util;

import com.kaiscript.dht.crawler.domain.FindNode;

/**
 * Created by chenkai on 2019/4/2.
 */
public class DhtUtilTest {

    public static void main(String[] args) {
        FindNode.Request request = new FindNode.Request(DhtUtil.generateNodeIdStr(), "1312312");
        System.out.println(DhtUtil.beanToMap(request));

    }

}
