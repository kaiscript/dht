package com.kaiscript.dht.crawler.util;

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
        System.out.println(bencode.decodeDict(dict.getBytes(), 0));

    }

}
