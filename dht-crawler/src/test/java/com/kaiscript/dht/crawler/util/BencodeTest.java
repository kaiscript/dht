package com.kaiscript.dht.crawler.util;

/**
 * Created by chenkai on 2019/4/1.
 */
public class BencodeTest {

    public static void main(String[] args) {
        Bencode bencode = new Bencode();
        String str = "20:abcdefghij0123456789";
        String integer = "i1999e";
        System.out.println(bencode.decodeString(str.getBytes(), 0));
        System.out.println(bencode.decodeInteger(integer.getBytes(), 0));

    }

}
