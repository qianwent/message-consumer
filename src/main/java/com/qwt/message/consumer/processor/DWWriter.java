package com.qwt.message.consumer.processor;

import com.qwt.message.consumer.service.dw.DWService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DWWriter implements Processor<String, String> {

    private DWService dwService;

    @Autowired
    public DWWriter(DWService dwService) {
        this.dwService = dwService;
    }

    @Override
    public String process(String source) {
        dwService.addMoreValue(source);
        return source;
    }
}
