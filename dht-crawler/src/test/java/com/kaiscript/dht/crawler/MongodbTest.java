package com.kaiscript.dht.crawler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chenkai on 2019/4/19.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class MongodbTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    class test{
        String name;
    }

//    @Test
    public void test() {
        Query query = Query.query(Criteria.where("name").is("test"));
        List<test> tests = mongoTemplate.find(query, test.class);

        for (test test : tests) {
            System.out.println(test.name);
        }
    }

}
