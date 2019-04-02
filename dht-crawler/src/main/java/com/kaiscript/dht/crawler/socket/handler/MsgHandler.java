package com.kaiscript.dht.crawler.socket.handler;

import com.kaiscript.dht.crawler.domain.Message;

/**
 * Created by kaiscript on 2019/4/2.
 */
public interface MsgHandler {

    void handle(Message message);

    boolean isExec(Message message);

}
