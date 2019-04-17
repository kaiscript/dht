package com.kaiscript.dht.crawler.parser;

import com.kaiscript.dht.crawler.domain.MetadataInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Optional;


/**
 * Created by chenkai on 2019/4/16.
 */
@Slf4j
public abstract class AbstractParser {

    OkHttpClient client = new OkHttpClient();

    public String websiteUrl;

    public AbstractParser(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public abstract String getUrl(String infohash);

    public abstract Optional<MetadataInfo> parseInfo(String html);

    public String getHtml(String infohash) {
        Headers.Builder headers = new Headers.Builder();
        headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        headers.add("Accept-Language", "zh-CN,zh;q=0.9,ja;q=0.8");
        Request request = new Request.Builder()
                .headers(headers.build())
                .get()
                .url(getUrl(infohash))
                .build();

        Call call = client.newCall(request);
        String ret = "";
        try {
            Response execute = call.execute();
            ResponseBody body = execute.body();
            ret = body.string();
        } catch (Exception e) {
            log.error("getHtml e");
        }
        return ret;
    }

    public Optional<MetadataInfo> parse(String infohash) {
        String html = getHtml(infohash);
        return parseInfo(html);
    }



}
