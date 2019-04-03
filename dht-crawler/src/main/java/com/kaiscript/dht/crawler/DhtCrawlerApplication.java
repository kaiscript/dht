package com.kaiscript.dht.crawler;

import com.kaiscript.dht.crawler.socket.server.DhtServer;
import com.kaiscript.dht.crawler.task.InitFindNodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DhtCrawlerApplication implements ApplicationRunner {

	@Autowired
	private DhtServer dhtServer;

	@Autowired
	private InitFindNodeTask initFindNodeTask;


	public static void main(String[] args) {
		SpringApplication.run(DhtCrawlerApplication.class, args);

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		dhtServer.start();
		initFindNodeTask.start();
	}

}
