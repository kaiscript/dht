package com.kaiscript.dht.crawler;

import com.kaiscript.dht.crawler.socket.server.DhtServer;
import com.kaiscript.dht.crawler.task.FetchMetadataTask;
import com.kaiscript.dht.crawler.task.FindNodeTask;
import com.kaiscript.dht.crawler.task.InitFindNodeTask;
import com.kaiscript.dht.crawler.task.ParserTask;
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
	@Autowired
	private FindNodeTask findNodeTask;
	@Autowired
	private FetchMetadataTask fetchMetadataTask;
	@Autowired
	private ParserTask parserTask;

	public static void main(String[] args) {
		SpringApplication.run(DhtCrawlerApplication.class, args);

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		dhtServer.start();
		initFindNodeTask.start();
		findNodeTask.start();
		fetchMetadataTask.startFetch();
		parserTask.start();
	}

}
