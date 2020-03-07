# DHT BT种子爬虫
基于DHT的嗅探原理，使用Netty 模拟BT协议Node之间通讯，爬取种子 metadata信息。

## 原理
### DHT 分布式哈希表
DHT 全称 Distributed Hash Table，是一个去中心化的分布式系统，特点是自动去中心化、强容错。

#### Kademlia

Kademlia 是一种通过 DHT 的协议算法，在 Kademlia DHT网络中，每个节点都有一个由 filename SHA1计算而成的160位的散列值代表NodeId。
这样哈希表就可以容纳2^160个节点。

Kademlia 会将节点 映射到一个二叉树，每一个节点都是这个二叉树的叶子。

对每一个节点，都可以按照自己的视角对整个二叉树进行拆分,最多拆成最多160个子树。

拆分的规则是：先从根节点开始，把不包含自己的那个子树拆分出来；然后在剩下的子树再拆分不包含自己的第二层子树；以此类推，直到最后只剩下自己。

每个子树都都代表一个路由表，一个路由表就是一个K桶（K-bucket），每个K桶都记录了子树里面的K个节点。

因此，每个节点都维护着一个由 N个 K桶组成的 Routing Table 路由表。

当A节点要寻找B节点时，便会将A节点的散列值与B节点进行异或，算出与B节点的距离，判断是在哪个K桶，然后向K桶里面的K个节点再发出请求。
这K个节点又会重复这个过程，计算与B节点的距离，直到找到B节点。

### 嗅探攻击

嗅探攻击的原理是加入DHT网络，然后利用 分布式哈希表，不断地认识更多的节点（find_node），并监听其他节点的 get_peers 请求，
这样就能爬取到其他节点共享的资源。

1.新节点A请求某个引导节点B，并将其加入到自己的K桶中。

2.生成一个SHA1散列值的NodeId，代表本节点。

3.向引导节点B发出 find_node 请求，B会将A加入到自己的K桶中，并B返回K个离A最近的节点

4.A收到find_node 回包，拿到B返回的K个节点，再向这K个节点发出find_node 请求，重复以上find_node 过程，并可认识越多越多的节点。

5.加入到DHT网络之后，监听其他节点的get_peers 请求，获取资源信息。

## 协议

### BENCODE 编码

KRPC协议是由BENCODE编码组成的一个简单的RPC结构,BENCODE编码 4种数据类型: string, integer, list 和 dictionary。
参见 [BEP-0003](http://bittorrent.org/beps/bep_0003.html) 协议


### DHT Queries 协议  
协议内容：[BEP-0005](http://bittorrent.org/beps/bep_0005.html)

一条KRPC消息可以代表请求request，也可以代表响应response，由字典组成。

请求 request 包括了以下4种协议：
- ping

    检测节点是否可达
- find_node
    
    请求给定ID的DHT网络中的节点信息，被请求节点会回复在被请求节点的路由表中，距离给定ID最近的K个nodeId信息。
并且被请求节点会将请求节点的信息加入到自身的路由表中。
- get_peers

    向其他节点请求获取info_hash信息
- announce_peer

    表明发出 announce_peer 请求的节点，正在下载 torrent 文件
    
    
Queries 协议数据结构可参见 [BEP-0005](http://bittorrent.org/beps/bep_0005.html) 协议，对应代码可参考 
`com.kaiscript.dht.crawler.domain`

### Handshake、BitTorrent协议扩展

当节点通过 DHT Queries协议加入到DHT网络之后，就嗅探网络上的种子信息了。
当节点收到其他节点的get_peers请求之后，就可以通过Peer Write协议 [Peer wire protocol (TCP)](https://wiki.theory.org/index.php/BitTorrentSpecification#Handshake) 
跟其他节点握手，让对端感知节点的存在。 
然后通过BitTorrent拓展协议 [BEP-0009](http://bittorrent.org/beps/bep_0009.html) 获取对方的拓展信息，拓展信息包括了种子的info_hash。

Extension消息都是bencode编码,有3类不同的消息：
- request
- data
- reject

如果收到的拓展协议的类型是 data ，那么就可以从中解析出种子信息了，data的数据结构如下：
```
Example:

{'msg_type': 1, 'piece': 0, 'total_size': 3425}
d8:msg_typei1e5:piecei0e10:total_sizei34256eexxxxxxxx...
The x represents binary data (the metadata).
```
metadata的结构参见 [Metainfo_File_Structure](https://wiki.theory.org/index.php/BitTorrentSpecification#Metainfo_File_Structure)。
那么此时就可以对metadata进行解析，得到种子文件信息了，解析代码参考 `com.kaiscript.dht.crawler.util.DhtUtil.convert`

握手协议数据结构参见 [Handshake](https://wiki.theory.org/index.php/BitTorrentSpecification#Handshake)

拓展协议数据结构参见 [BEP-0009](http://bittorrent.org/beps/bep_0009.html) 

## 项目结构

- 启动多个端口监听DHT网络中Node请求

    `com.kaiscript.dht.crawler.socket.server.DhtServer`
        
- find_node/get_peers 处理 Handler

    `package com.kaiscript.dht.crawler.socket.handler`
- 请求引导节点任务

    `com.kaiscript.dht.crawler.task.InitFindNodeTask`
- 递归发现节点处理

    `com.kaiscript.dht.crawler.socket.handler.FindNodeRespHandler`
- 监听Node的 get_peers 消息，获取种子信息

    `com.kaiscript.dht.crawler.socket.handler.GetPeersRequestHandler`
    
    `com.kaiscript.dht.crawler.task.FetchMetadataTask`
    
- 根据拓展消息获取不到infohash的种子信息，则去爬取种子网站

    `package com.kaiscript.dht.crawler.parser`

## 参考

- [Kademlia、DHT、KRPC、BitTorrent 协议、DHT Sniffer](https://www.cnblogs.com/LittleHann/p/6180296.html)
- [DHT 分布式哈希表](https://colobu.com/2018/03/26/distributed-hash-table/)
- [BEP](http://bittorrent.org/beps/bep_0000.html)
- [BitTorrentSpecification](https://wiki.theory.org/index.php/BitTorrentSpecification)