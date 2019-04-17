package com.kaiscript.dht.crawler.domain;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by chenkai on 2019/4/17.
 */
@Data
@Accessors(chain = true)
public class MetadataInfo {

    private String name;

    private String size;

    private String magnet;

    private List<FileInfo> files = Lists.newArrayList();

    @Data
    @Accessors(chain = true)
    public static class FileInfo{

        private String name;

        private String size;

    }

}
