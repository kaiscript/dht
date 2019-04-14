package com.kaiscript.dht.crawler.domain;

import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.constants.YEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kaiscript on 2019/4/14.
 */
public interface GetPeers {

    @Data
    @Accessors(chain = true)
    static class RequestContent{
        private String id;
        private String info_hash;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    static class Request extends CommonRequest{

        private RequestContent a = new RequestContent();

        public Request(String tId,String nodeId,String infoHash){
            t = tId;
            y = YEnum.QUERY.getType();
            q = QueryEnum.GET_PEERS.getType();
            a.id = nodeId;
            a.info_hash = infoHash;
        }

    }

    @Data
    @Accessors(chain = true)
    static class ResponseContent {
        /**
         * 回复方nodeID
         */
        private String id;

        /**
         * 回复方定义的token
         */
        private String token;

        /**
         * 当有该种子时,回复的是values,没有时,回复的是nodes.
         */
        private String nodes;
    }

    @Accessors(chain = true)
    @AllArgsConstructor
    static class Response extends CommonParam{

        private ResponseContent r = new ResponseContent();

        private void init() {
            y = YEnum.RESPONSE.getType();
            r = new GetPeers.ResponseContent();
        }

        public Response(String nodeId,String token,String nodes,String messageId) {
            init();
            r.id = nodeId;
            r.token = token;
            r.nodes = nodes;
            t = messageId;
        }

    }

}
