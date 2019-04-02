package com.kaiscript.dht.crawler.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * y关键字值枚举
 * Created by chenkai on 2019/4/2.
 */
@Getter
@AllArgsConstructor
public enum YEnum implements CommonEnum<String>{

    QUERY("q", "请求"),
    RESPONSE("r", "回复"),
    ERROR("e", "错误")
    ;

    private String type;
    private String desc;


    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
