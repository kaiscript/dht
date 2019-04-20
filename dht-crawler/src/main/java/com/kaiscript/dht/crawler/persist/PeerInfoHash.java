package com.kaiscript.dht.crawler.persist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by kaiscript on 2019/4/20.
 */
@Data
@Accessors(chain = true)
@Document(collection = "temp_peer_info_hash")
@AllArgsConstructor
public class PeerInfoHash {

    private String ip;

    private int port;

    private String infohash;

    private Date updateTime;

}
