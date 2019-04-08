package com.kaiscript.dht.crawler.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by kaiscript on 2019/4/8.
 */
@Data
@Getter
@Setter
@Accessors(chain = true)
public class FetchMetadata {

    private String ip;

    private int port;

    private String infohash;

}
