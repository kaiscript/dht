package com.kaiscript.dht.crawler.parser;

import com.kaiscript.dht.crawler.domain.Metadata;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by chenkai on 2019/4/16.
 */
@Component
public class BtrabbitParser extends AbstractParser {

    public BtrabbitParser() {
        super("http://www.btrabbit.xyz/wiki/");
    }

    @Override
    public String getUrl(String infohash) {
        return null;
    }

    @Override
    public List<Metadata.Info> queryInfo() {
        return null;
    }
}
