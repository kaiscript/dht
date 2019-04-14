package com.kaiscript.dht.crawler.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kaiscript.dht.crawler.constants.QueryEnum;
import com.kaiscript.dht.crawler.constants.YEnum;
import com.kaiscript.dht.crawler.domain.Message;
import com.kaiscript.dht.crawler.domain.Metadata;
import com.kaiscript.dht.crawler.domain.Node;
import com.kaiscript.dht.crawler.exception.DhtException;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.kaiscript.dht.crawler.constants.Constants.NODE_LENGTH;

/**
 * Created by chenkai on 2019/4/2.
 */
public class DhtUtil {

    private static final Logger logger = LoggerFactory.getLogger(DhtUtil.class);

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

    /**
     * 转换收到的map数据
     * @param data
     * @return
     */
    public static Optional<Message> formatData(Map<String,Object> data) {
        Message msg = new Message();
        msg.setData(data);
        String y = getString(data, "y");
        Optional<YEnum> yEnumOptional = EnumUtil.getEnum(y, YEnum.class);
        if (!yEnumOptional.isPresent()) {
            logger.info("param y of msg is error.map:" + data);
            return Optional.empty();
        }
        YEnum yEnum = yEnumOptional.get();
        msg.setY(yEnum);
        if (yEnum == YEnum.QUERY) {
            String q = getString(data, "q");
            Optional<QueryEnum> queryEnumOptional = EnumUtil.getEnum(q, QueryEnum.class);
            if (!queryEnumOptional.isPresent()) {
                throw new DhtException("param q of msg is error");
            }
            QueryEnum queryEnum = queryEnumOptional.get();
            msg.setQuery(queryEnum);
        }
        //t字段，消息id
        String tId = getString(data, "t");
        if (StringUtils.isNotBlank(tId)) {
            msg.setTId(tId);
        }

        return Optional.of(msg);
    }

    /**
     * 根据回包r字段获取node列表
     * @param rMap
     * @return
     */
    public static List<Node> getNodeListByMap(Map<String,Object> rMap) {
        List<Node> nodeList = Lists.newLinkedList();
        String nodes = getString(rMap, "nodes");
        if (StringUtils.isBlank(nodes)) {
            return Lists.newArrayList();
        }
        byte[] bytes = nodes.getBytes(CharsetUtil.ISO_8859_1);
        for (int i = 0; i < bytes.length; i+= NODE_LENGTH) {
            Node node = Node.buildNode(ArrayUtils.subarray(bytes, i, i + NODE_LENGTH));
            nodeList.add(node);
        }
        return nodeList;
    }


    public static String getString(Map<String,Object> map, String param) {
        return (String) map.get(param);
    }

    public static Map<String, Object> getMap(Map<String, Object> map, String param) {
        return (Map<String, Object>) map.get(param);
    }

    /**
     * metadata数据转bean
     * @param map
     * @param infohash
     * @return
     */
    public static Metadata convert(Map<String,Object> map,String infohash) {
        Metadata metadata = new Metadata();
        List<Metadata.Info> infos = Lists.newArrayList();
        metadata.setName((String) map.get("name")).setInfohash(infohash);
        if (map.containsKey("files")) {
            List<Map<String,Object>> files = (List<Map<String, Object>>) map.get("files");
            infos = files.parallelStream().map(file -> {
                Metadata.Info info = new Metadata.Info();
                info.setLength((Long) file.get("length"));
                Object pathObj = file.get("path");
                if (pathObj instanceof String) {
                    info.setPath((String) pathObj);
                } else if (pathObj instanceof List) {
                    List<String> pathList = (List<String>) pathObj;
                    StringBuilder sb = new StringBuilder();
                    for (String path : pathList) {
                        sb.append("/").append(path);
                    }
                    info.setPath(sb.toString());
                }
                return info;
            }).collect(Collectors.toList());
            metadata.setLength(infos.stream().mapToLong(Metadata.Info::getLength).sum());
        }
        else {
            long length = (long) map.get("length");
            infos = Collections.singletonList(new Metadata.Info(metadata.getName(), length));
            metadata.setLength(length);
        }
        metadata.setInfos(infos);
        return metadata;
    }

}
