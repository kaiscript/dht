package com.kaiscript.dht.crawler.domain;

import com.kaiscript.dht.crawler.persist.PeerInfoHash;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class FetchMetadata {

    private String ip;

    private int port;

    private String infohash;

    public static FetchMetadata of(PeerInfoHash peerInfoHash) {
        return new FetchMetadata(peerInfoHash.getIp(), peerInfoHash.getPort(), peerInfoHash.getInfohash());
    }

}
