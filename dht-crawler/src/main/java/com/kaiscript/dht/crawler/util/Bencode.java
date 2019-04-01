package com.kaiscript.dht.crawler.util;

import com.kaiscript.dht.crawler.exception.DhtException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @link http://bittorrent.org/beps/bep_0003.html
 * Created by kaiscript on 2019/3/31.
 */
public class Bencode {

    private static Charset charset = Charset.forName("ISO-8859-1");

    private static final String STRING_SEPARATOR = ":";

    private static final byte STRING_SEPARATOR_BYTE = STRING_SEPARATOR.getBytes(charset)[0];

    private static final String INTEGER_PREFIX = "i";

    private static final String DICT_PREFIX = "d";

    private static final String LIST_PREFIX = "l";

    private static final String TYPE_SUFFIX = "e";

    /**
     * 编码相关
     */


    /**
     * 字符串编码
     * @param str
     * @return
     */
    public String encodeString(String str) {
        return str.length() + STRING_SEPARATOR + str;
    }

    /**
     * integer编码
     * @param l
     * @return
     */
    public String encodeLong(long l) {
        return INTEGER_PREFIX + l + TYPE_SUFFIX;
    }

    /**
     * list 编码
     * @param objects
     * @return
     */
    public String encodeList(List<Object> objects) {
        String[] ret = new String[objects.size() + 2];
        ret[0] = LIST_PREFIX;
        for (int i = 1; i <= objects.size(); i++) {
            ret[i] = encodeAny(objects.get(i - 1));
        }
        ret[ret.length - 1] = TYPE_SUFFIX;
        return String.join("", ret);
    }

    /**
     * Dict 编码
     * @return
     */
    public String encodeDict(Map<String,Object> map) {
        String[] ret = new String[map.size() + 2];
        ret[0] = DICT_PREFIX;
        int index = 1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            ret[index++] = encodeString(entry.getKey()) + encodeAny(entry.getValue());
        }
        ret[ret.length - 1] = TYPE_SUFFIX;
        return String.join("", ret);
    }

    /**
     * 编码任何类型
     * @param object
     * @return
     * @throws Exception
     */
    public String encodeAny(Object object){
        if (object instanceof String) {
            return encodeString((String) object);
        }
        else if(object instanceof Long){
            return encodeLong((Long) object);
        }
        else if(object instanceof Integer){
            return encodeLong(Integer.toUnsignedLong((Integer) object));
        }
        else if(object instanceof List){
            return encodeList((List<Object>) object);
        }
        else if (object instanceof Map) {
            return encodeDict((Map<String, Object>) object);
        }
        else{
            throw new RuntimeException("encodeAny object " + object + " error");
        }
    }

    /**
     * 解码相关
     */

    /**
     * 解码string
     * @param bytes
     * @param start
     * @return
     */
    public DecodeResult decodeString(byte[] bytes,int start) {
        if (start >= bytes.length) {
            throw new DhtException("decodeString error");
        }
        int startIndex = ArrayUtils.indexOf(bytes, STRING_SEPARATOR_BYTE, start);
        if (startIndex < 0) {
            throw new DhtException("decodeString error");
        }
        int strLen = NumberUtils.toInt(new String(ArrayUtils.subarray(bytes, start, startIndex), charset));
        if (strLen < 0) {
            throw new DhtException("decodeString error.length less than 0");
        }
        int endIndex = startIndex + strLen + 1;
        String str = new String(ArrayUtils.subarray(bytes, startIndex + 1, endIndex), charset);
        return new DecodeResult(endIndex, str);
    }

    /**
     * 解码Integer
     * @param bytes
     * @param start
     * @return
     */
    public DecodeResult<Long> decodeInteger(byte[] bytes, int start) {
        if (start >= bytes.length || bytes[start] != INTEGER_PREFIX.charAt(0)) {
            throw new DhtException("decodeInteger error");
        }
        int endIndex = ArrayUtils.indexOf(bytes, TYPE_SUFFIX.getBytes(charset)[0], start);
        if (endIndex < 0) {
            throw new DhtException("decode Integer error.length less than 0");
        }
        long result = 0;
        try {
            result = Long.parseLong(new String(ArrayUtils.subarray(bytes, start + 1, endIndex), charset));
        } catch (NumberFormatException e) {
            throw new DhtException("decode Integer error,value not a Integer");
        }
        return new DecodeResult(++endIndex, result);
    }


    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    class DecodeResult<T>{
        int index;
        T value;
    }

}
