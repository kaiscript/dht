package com.kaiscript.dht.crawler.domain;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Created by chenkai on 2019/4/17.
 */
@Data
@Accessors(chain = true)
@Document(collection = "metadata_info")
public class MetadataInfo {

    @Field("name")
    private String name;

    @Field("size")
    private String size;

    @Field("magnet")
    private String magnet;

    @Field("files")
    private List<FileInfo> files = Lists.newArrayList();

    @Data
    @Accessors(chain = true)
    public static class FileInfo{

        private String name;

        private String size;

    }

}
