package com.kaiscript.dht.crawler.parser;

import com.kaiscript.dht.crawler.domain.Metadata;

import java.util.List;

/**
 * Created by chenkai on 2019/4/16.
 */
public abstract class AbstractParser {

    public String websiteUrl;

    public AbstractParser(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public abstract String getUrl(String infohash);


    public abstract List<Metadata.Info> queryInfo();

}
