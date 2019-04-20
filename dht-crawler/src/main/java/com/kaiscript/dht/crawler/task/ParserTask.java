package com.kaiscript.dht.crawler.task;

import com.kaiscript.dht.crawler.domain.FetchMetadata;
import com.kaiscript.dht.crawler.domain.MetadataInfo;
import com.kaiscript.dht.crawler.parser.BtrabbitParser;
import com.kaiscript.dht.crawler.service.PeerInfohashService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chenkai on 2019/4/19.
 */
@Component
@Slf4j
public class ParserTask {

    @Autowired
    private BtrabbitParser btrabbitParser;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FetchMetadataTask fetchMetadataTask;

    @Autowired
    private PeerInfohashService peerInfohashService;

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void start() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true){
                    String infohash = queue.poll();
                    if (StringUtils.isNotBlank(infohash)) {
                        fetchFromWebsite(infohash);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        log.error("findNode e:", e);
                    }
                }

            }).start();

        }
    }

    public void offer(String infohash) {
        queue.offer(infohash);
    }

    private void fetchFromWebsite(String infohash) {
        Optional<MetadataInfo> ret = btrabbitParser.parse(infohash);
        if (!ret.isPresent()) {
            Optional<FetchMetadata> fetchMetadata = peerInfohashService.queryFromInfohash(infohash);
            if (fetchMetadata.isPresent()) {
                log.info("fetchFromWebsite null.offer to fetchMetadataTask queue.infohash:{}", infohash);
                fetchMetadataTask.offer(fetchMetadata.get());
            }
            return;
        }
        MetadataInfo metadataInfo = ret.get();
        log.info("fetchFromWebsite metadataInfo:{}", metadataInfo);
//        爬取成功，保存数据，移除临时表
        mongoTemplate.insert(metadataInfo);
        peerInfohashService.removeFormInfohash(infohash);
    }

}
