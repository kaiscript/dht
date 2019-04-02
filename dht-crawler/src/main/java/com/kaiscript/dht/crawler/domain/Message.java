package com.kaiscript.dht.crawler.domain;

import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.constants.YEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by chenkai on 2019/4/2.
 */
@Getter
@Setter
public class Message {

    private YEnum y;

    private QueryEnum query;

    private Map<String, Object> data;


}
