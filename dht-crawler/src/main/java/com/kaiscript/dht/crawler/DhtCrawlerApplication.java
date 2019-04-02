package com.kaiscript.dht.crawler;

import com.kaiscript.dht.crawler.domain.FindNode;
import com.kaiscript.dht.crawler.socket.client.DhtClient;
import com.kaiscript.dht.crawler.socket.server.DhtServer;
import com.kaiscript.dht.crawler.util.Bencode;
import com.kaiscript.dht.crawler.util.DhtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

@SpringBootApplication
public class DhtCrawlerApplication implements ApplicationRunner {

	@Autowired
	private DhtServer dhtServer;

	@Autowired
	private DhtClient dhtClient; //todo test

	public static void main(String[] args) {
		SpringApplication.run(DhtCrawlerApplication.class, args);

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			testSend();
		}).start();
		dhtServer.start();
	}

	private void testSend() {
		FindNode.Request request = new FindNode.Request(DhtUtil.generateNodeIdStr(), "mnopqrstuvwxyz123456");
		Bencode bencode = new Bencode();
		InetSocketAddress address = new InetSocketAddress("router.utorrent.com", 6881);
		byte[] bytes = bencode.encodeToBytes(DhtUtil.beanToMap(request));
		dhtClient.writeAndFlush(address, bytes);
	}

}
