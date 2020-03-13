package com.qwt.message.consumer.processor;

import com.qwt.message.consumer.service.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBWriter implements Processor<String, String> {

    private DBService dbService;

    @Autowired
    public DBWriter(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public String process(String source) {
        dbService.addValue(source);
        return source;
    }
}
