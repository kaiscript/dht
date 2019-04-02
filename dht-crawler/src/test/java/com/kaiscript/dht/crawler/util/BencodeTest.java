package com.kaiscript.dht.crawler.util;

import com.kaiscript.dht.crawler.domain.FindNode;
import io.netty.util.CharsetUtil;

import java.util.Map;

/**
 * Created by chenkai on 2019/4/1.
 */
public class BencodeTest {

    public static void main(String[] args) {
        Bencode bencode = new Bencode();
        String str = "20:abcdefghij0123456789";
        String integer = "i1999e";
//        System.out.println(bencode.decodeString(str.getBytes(), 0));
//        System.out.println(bencode.decodeInteger(integer.getBytes(), 0));

        String list = "l5:hello5:worldi101ee";
//        System.out.println(bencode.decodeList(list.getBytes(), 0));
        String dict = "d2:aai100e2:bb2:bb2:cci200ee";
//        System.out.println(bencode.decodeDict(dict.getBytes(), 0));
        FindNode.Request request = new FindNode.Request(DhtUtil.generateNodeIdStr(), "mnopqrstuvwxyz123477");
        Map<String, Object> stringObjectMap = DhtUtil.beanToMap(request);
        System.out.println(stringObjectMap);
        byte[] bytes = bencode.encodeToBytes(stringObjectMap);

        System.out.println("---------------");
        System.out.println(new String(bytes, CharsetUtil.ISO_8859_1));

    }

}
