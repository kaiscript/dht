package com.kaiscript.dht.crawler.domain;

import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.constants.YEnum;
import com.kaiscript.dht.crawler.util.DhtUtil;
import io.netty.util.CharsetUtil;

/**
 * Created by chenkai on 2019/4/4.
 */
public interface AnnouncePeer {

    class RequestContent{

        String id;

        int implied_port;

        String info_hash;

        int port;

        String token;

    }

    /**
     * AnnouncePeer请求
     */
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
    class Response extends CommonParam{

        ResponseContent r = new ResponseContent();

        public Response() {
            t = new String(DhtUtil.generateTId(), CharsetUtil.ISO_8859_1);
            y = YEnum.RESPONSE.getType();
        }

    }

}
