package com.kaiscript.dht.crawler.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by kaiscript on 2019/4/8.
 */
@Data
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Metadata {

    private long length;

    private String name;

    private String infohash;

    private List<Info> infos;

    @Data
    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info{

        private String name;

        private String path;

        private long length;

    }

}
