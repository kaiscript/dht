package com.kaiscript.dht.crawler.domain;

import lombok.*;

/**
 * Created by kaiscript on 2019/4/2.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Node {

    private String nodeId;

    private String ip;

    private int port;

}
