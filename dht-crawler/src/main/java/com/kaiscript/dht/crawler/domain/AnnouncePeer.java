package com.kaiscript.dht.crawler.domain;

import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.constants.YEnum;
import com.kaiscript.dht.crawler.util.ByteUtil;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

/**
 * Created by chenkai on 2019/4/4.
 */
public interface AnnouncePeer {

    @Data
    @NoArgsConstructor
    class RequestContent{

        String id;

        int implied_port;

        String info_hash;

        int port;

        String token;

        public RequestContent(Map<String,Object> aMap,int sourcePort) {
            id = ByteUtil.byte2HexString(DhtUtil.getString(aMap, "id").getBytes(CharsetUtil.ISO_8859_1));
            info_hash = ByteUtil.byte2HexString(DhtUtil.getString(aMap, "info_hash").getBytes(CharsetUtil.ISO_8859_1));
            if (aMap.get("implied_port") != null && NumberUtils.toInt((String) aMap.get("implied_port")) != 0) {
                port = sourcePort;
            }
            else{
                port = NumberUtils.toInt(DhtUtil.getString(aMap, "port"));
            }
        }

    }

    /**
     * AnnouncePeer请求
     */
    @Data
    @AllArgsConstructor
    class Request extends CommonRequest{

        RequestContent a = new RequestContent();

        public Request() {
            init();
        }

        private void init() {
            y = YEnum.QUERY.getType();
            q = QueryEnum.ANNOUNCE_PEER.getType();
            t = new String(DhtUtil.generateTId(), CharsetUtil.ISO_8859_1);
        }

    }

    class ResponseContent{

        String id;

    }

    /**
     * AnnouncePeer回包
     */
    @Data
    @AllArgsConstructor
    class Response extends CommonParam{

        ResponseContent r = new ResponseContent();

        public Response() {
            t = new String(DhtUtil.generateTId(), CharsetUtil.ISO_8859_1);
            y = YEnum.RESPONSE.getType();
        }

    }

}
