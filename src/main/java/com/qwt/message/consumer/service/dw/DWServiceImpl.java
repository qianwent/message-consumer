package com.qwt.message.consumer.service.dw;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.qwt.message.consumer.exception.KnownServiceException;
import org.springframework.stereotype.Component;

@Component
public class DWServiceImpl implements DWService {

    @HystrixCommand(
            groupKey = "DWWrite",
            commandKey = "DWWriteCommand",
            threadPoolKey = "DWWriteCommand",
            ignoreExceptions = {KnownServiceException.class}
    )
    @Override
    public void addMoreValue(String value) {
        // TODO
    }
}
