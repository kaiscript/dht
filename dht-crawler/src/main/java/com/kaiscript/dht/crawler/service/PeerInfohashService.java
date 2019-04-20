package com.kaiscript.dht.crawler.service;

import com.kaiscript.dht.crawler.domain.FetchMetadata;
import com.kaiscript.dht.crawler.persist.PeerInfoHash;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by kaiscript on 2019/4/20.
 */
@Service
public class PeerInfohashService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void savePeerInfohash(String ip, int port, String infohash) {
        mongoTemplate.insert(new PeerInfoHash(ip, port, infohash, new Date()));
    }

    public Optional<FetchMetadata> queryFromInfohash(String infohash) {
        Query query = new Query();
        query.addCriteria(Criteria.where("infohash").is(infohash));
        List<PeerInfoHash> peerInfoHashes = mongoTemplate.find(query, PeerInfoHash.class);
        if (CollectionUtils.isEmpty(peerInfoHashes)) {
            return Optional.empty();
        }
        PeerInfoHash peerInfoHash = peerInfoHashes.get(0);
        return Optional.of(FetchMetadata.of(peerInfoHash));
    }

    public boolean removeFormInfohash(String infohash) {
        Query query = new Query();
        query.addCriteria(Criteria.where("infohash").is(infohash));
        DeleteResult remove = mongoTemplate.remove(query, PeerInfoHash.class);
        return remove.wasAcknowledged();
    }

}
