package com.kaiscript.dht.crawler.domain;

import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.constants.YEnum;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chenkai on 2019/4/2.
 */
public interface FindNode {

    /**
     * FindNode 请求体的其他字段
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class RequestContent{

        /**
         * 请求id
         */
        String id;

        /**
         * 被请求nodeId
         */
        String target;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Request extends CommonRequest{

        RequestContent a = new RequestContent();

        public Request(String id,String target) {
            init();
            a.id = id;
            a.target = target;
        }

        private void init() {
            y = YEnum.QUERY.getType();
            q = QueryEnum.FIND_NODE.getType();
            t = new String(DhtUtil.generateTId(), CharsetUtil.ISO_8859_1);
        }

    }

    /**
     * FindNode回包的其他字段
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ResponseContent{

        public String id;

        public String nodes;

    }

    /**
     * FindNode回包
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Response extends CommonParam{

        public ResponseContent r = new ResponseContent();

        public Response(String id, String nodes) {
            init(id, nodes);
            y = YEnum.RESPONSE.getType();
            t = new String(DhtUtil.generateTId(), CharsetUtil.ISO_8859_1);
        }

        private void init(String id,String nodes) {
            r.id = id;
            r.nodes = nodes;
        }

    }

}
