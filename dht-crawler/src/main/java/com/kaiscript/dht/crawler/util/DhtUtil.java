package com.kaiscript.dht.crawler.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.exception.DhtException;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by chenkai on 2019/4/2.
 */
public class DhtUtil {

    /**
     * transaction ID 只要16位
     * @return
     */
    public static byte[] generateTId() {
        String s = UUID.randomUUID().toString();
        byte[] bytes = DigestUtils.sha1(s);
        byte[] newByte = new byte[2];
        newByte[1] = (byte) (bytes[0] & 0xff);
        newByte[0] = (byte) (bytes[1] & 0xff);
        return newByte;
    }

    /**
     * 生成随机nodeId.<br></>
     * info_hash为20位字节. BEP-003协议
     * @return
     */
    public static byte[] generateNodeId() {
        return RandomUtils.nextBytes(20);
    }

    public static String generateNodeIdStr() {
        return new String(generateNodeId(), CharsetUtil.ISO_8859_1);
    }

    /**
     * bean 转 map
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> Map<String,Object> beanToMap(T obj){
        Map<String, Object> ret = Maps.newLinkedHashMap();
        List<Field> fields = Lists.newArrayList();
        ReflectionUtils.doWithFields(obj.getClass(), fields::add, field -> true);
        fields.forEach(field -> {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                //基本数据类型如string，integer，list等 则放入map，其他自定义类递归解析
                ret.put(field.getName(), field.getType().getName().contains("java") ? value : beanToMap(value));
            } catch (Exception e) {
                throw new DhtException("beanToMap error");
            }
        });
        return ret;
    }


    public static Message getMsg(Map<String,Object> data) {
        Message msg = new Message();
        msg.setData(data);
        return msg;
    }



}
