package com.kaiscript.dht.crawler.socket.handler;

import com.kaiscript.dht.crawler.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by kaiscript on 2019/4/2.
 */
@Component
public class MsgHandlerManager {

    private List<MsgHandler> list;

    @Autowired
    public MsgHandlerManager(List<MsgHandler> list) {
        this.list = list;
    }

    public void exec(Message msg) {
        list.forEach(msgHandler -> {
            if (msgHandler.isExec(msg)) {
                msgHandler.handle(msg);
            }
        });
    }

}
