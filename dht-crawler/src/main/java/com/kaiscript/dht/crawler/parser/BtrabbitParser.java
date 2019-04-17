package com.kaiscript.dht.crawler.parser;

import com.kaiscript.dht.crawler.domain.MetadataInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by chenkai on 2019/4/16.
 */
@Component
public class BtrabbitParser extends AbstractParser {

    public BtrabbitParser() {
        super("http://www.btrabbit.xyz/wiki/");
    }

    @Override
    public String getUrl(String infohash) {
        return websiteUrl + infohash + ".html";
    }
    //364c89d2f9f35f16385894f6650cbadc6a506147

    @Override
    public Optional<MetadataInfo> parseInfo(String html) {
        if (html.contains("404页面")) {
            return Optional.empty();
        }
        MetadataInfo metadataInfo = new MetadataInfo();
        Document document = Jsoup.parse(html);
        String title = document.selectFirst("#wall > h2").text();
        metadataInfo.setName(title);

        Elements allSize = document.select("#wall > div.fileDetail > div > table > tbody > tr > td:nth-child(5)");
        metadataInfo.setSize(allSize.text());

        Elements titleA = document.select("#wall > div.fileDetail > div > div > div.panel-body > a");
        metadataInfo.setMagnet(titleA.attr("href"));

        Elements files = document.select("#wall > div.fileDetail > div > div:nth-child(9) > div.panel-body > ol > li");
        List<MetadataInfo.FileInfo> infos = files.stream().map(file -> {
            MetadataInfo.FileInfo fileInfo = new MetadataInfo.FileInfo();
            Elements span = file.select("span");
            String size = span.html().replace("&nbsp;", " ");
            fileInfo.setSize(size);
            String name = file.text().replace(span.html().replace("&nbsp;"," "), "");
            fileInfo.setName(name);
            return fileInfo;
        }).collect(Collectors.toList());

        metadataInfo.setFiles(infos);

        return Optional.of(metadataInfo);
    }

    public static void main(String[] args) {
        BtrabbitParser btrabbitParser = new BtrabbitParser();
        Optional<MetadataInfo> parse = btrabbitParser.parse("364c89d2f9f35f16385894f6650cbadc6a506147");
        System.out.println(parse);
    }

}
