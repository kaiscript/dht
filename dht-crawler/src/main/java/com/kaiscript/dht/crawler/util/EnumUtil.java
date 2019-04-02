package com.kaiscript.dht.crawler.util;

import com.kaiscript.dht.crawler.constants.CommonEnum;

import java.util.Optional;

/**
 * Created by chenkai on 2019/4/2.
 */
public class EnumUtil {

    public static <T extends CommonEnum<X>, X> Optional<T> getEnum(X type, Class<T> clazz) {
        for (T t : clazz.getEnumConstants()) {
            if (t.getType().equals(type)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

}
